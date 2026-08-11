package org.aiassistant.ai.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aiassistant.ai.agents.BuildAgent;
import org.aiassistant.ai.agents.PlanningAgent;
import org.aiassistant.ai.agents.ReviewAgent;
import org.aiassistant.ai.dtos.codegen.Blueprint;
import org.aiassistant.ai.dtos.codegen.PlannedFile;
import org.aiassistant.services.ProjectService;
import org.aiassistant.utils.FileUtil;
import org.aiassistant.utils.SseUtil;
import org.aiassistant.utils.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class CodeGenService {

    private static final Logger log = LoggerFactory.getLogger(CodeGenService.class);

    private final ThreadPoolTaskExecutor taskExecutor;

    private final String blueprintDir;

    private final String codegenWorkspaceDir;

    private final String codeGenZipFileWorkspace;

    private final PlanningAgent planningAgent;

    private final BuildAgent buildAgent;

    private final ReviewAgent reviewAgent;
    
    private final S3Service s3Service;
    private final ProjectService projectService;

    public CodeGenService(
            @Qualifier("codeGenExecutor") ThreadPoolTaskExecutor taskExecutor,
            @Value("${code.gen.project-blueprint-path}") String blueprintDir,
            @Value("${codegen.workspace.dir}") String codegenWorkspaceDir,
            @Value("${codegen.zip.workspace}") String codeGenZipFileWorkspace,
            PlanningAgent planningAgent,
            BuildAgent buildAgent,
            ReviewAgent reviewAgent,
            S3Service s3Service,
            ProjectService projectService) {
        this.taskExecutor = taskExecutor;
        this.blueprintDir = blueprintDir;
        this.codegenWorkspaceDir = codegenWorkspaceDir;
        this.codeGenZipFileWorkspace = codeGenZipFileWorkspace;
        this.planningAgent = planningAgent;
        this.buildAgent = buildAgent;
        this.reviewAgent = reviewAgent;
        this.s3Service = s3Service;
        this.projectService = projectService;
    }

    /* No timeout — a full code-generation run can legitimately take several minutes. */
    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L;

    public SseEmitter planAndGenCode(String projectId, String userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        taskExecutor.submit(() -> {
            String codeGenJobId = UUID.randomUUID().toString();
            try {
                SseUtil.sendProgress(emitter, "Code generation started. Job ID: " + codeGenJobId);

                /*
                 * Step 1. Requirement Call to LLM
                 * */
                SseUtil.sendProgress(emitter, "Step 1/5 — Analyzing your requirement and designing the project blueprint...");
                Blueprint projectBluePrint = planningAgent.plan(projectId, userId);
                if (projectBluePrint == null) {
                    SseUtil.sendEvent(emitter, SseUtil.EVENT_ERROR, "Unable to build a project blueprint from the requirement. Aborting.");
                    emitter.complete();
                    return;
                }
                int backendCount = projectBluePrint.backendFiles() == null ? 0 : projectBluePrint.backendFiles().size();
                int frontendCount = projectBluePrint.frontendFiles() == null ? 0 : projectBluePrint.frontendFiles().size();
                SseUtil.sendProgress(emitter, "Blueprint ready — " + backendCount + " backend file(s) and "
                        + frontendCount + " frontend file(s) planned.");

                /*
                 * Step 2. Save the contract/blue to the disk
                 * Sort the backend/frontend files by Rank ASC Order
                 * (Single source of truth)
                 * */
                SseUtil.sendProgress(emitter, "Step 2/5 — Ordering planned files and saving the project blueprint...");
                sortFilesByRank(projectBluePrint);
                writeBluePrintToDisk(projectBluePrint, codeGenJobId);
                SseUtil.sendProgress(emitter, "Project blueprint saved to disk.");

                /*
                 * Step 3. Iterate over the files
                 * Backend first --> frontend and generate the actual content.
                 * */
                SseUtil.sendProgress(emitter, "Step 3/5 — Generating project files...");
                generateProjectFiles(projectBluePrint, codeGenJobId, emitter, userId);

                SseUtil.sendEvent(emitter, SseUtil.EVENT_COMPLETE, "Code generation completed successfully. Job ID: " + codeGenJobId);

                SseUtil.sendEvent(emitter, SseUtil.EVENT_ZIP, "Converting the code into the ZIP format.");

                String sourceDir = System.getProperty("user.dir")
                        + File.separator + codegenWorkspaceDir
                        + File.separator + codeGenJobId;

                String destZipDir = System.getProperty("user.dir")
                        + File.separator + codeGenZipFileWorkspace
                        + File.separator + codeGenJobId + ".zip";

                ZipUtil.createZipFile(sourceDir, destZipDir);

                SseUtil.sendEvent(emitter, SseUtil.EVENT_ZIP, "Zip Conversion Done...");
                
                SseUtil.sendEvent(emitter, SseUtil.EVENT_S3, "Uploading file to Storage...");

                s3Service.saveFile(new File(destZipDir), projectId, userId);

                SseUtil.sendEvent(emitter, SseUtil.EVENT_S3, "Uploading Done...");
                emitter.complete();
            } catch (Exception ex) {
                log.error("Code generation failed for job {}", codeGenJobId, ex);
                SseUtil.sendEvent(emitter, SseUtil.EVENT_ERROR, "Code generation failed: " + ex.getMessage());
                emitter.completeWithError(ex);
            } finally {

                String sourceDir = System.getProperty("user.dir")
                        + File.separator + codegenWorkspaceDir
                        + File.separator + codeGenJobId;

                String destZipDir = System.getProperty("user.dir")
                        + File.separator + codeGenZipFileWorkspace
                        + File.separator + codeGenJobId + ".zip";


                SseUtil.sendEvent(emitter, SseUtil.EVENT_DELETE, "Deleting the files from instance level...");

                FileUtil.recursiveDelete(new File(sourceDir));
                FileUtil.recursiveDelete(new File(destZipDir));
                FileUtil.recursiveDelete(new File(getBluePrintFilePath(codeGenJobId)));

                SseUtil.sendEvent(emitter, SseUtil.EVENT_DELETE, "Deleted Successfully");
            }
        });
        return emitter;
    }

    private void generateProjectFiles(Blueprint blueprint, String codeGenJobId, SseEmitter emitter, String userId) {

        if(blueprint == null) return;

        if(blueprint.frontendFiles() == null && blueprint.backendFiles() == null) return;

        /* Key will be file-path && value will be exports and all of that...*/
        Map<String, String> interfaceIndexMap = new HashMap<>();

        /* Key will be file-path && value will be the generated file content. */
        Map<String, String> generatedFiles = new LinkedHashMap<>();

        /* Backend first so their interfaces are available to the frontend. */
        generateFiles(blueprint, blueprint.backendFiles(), interfaceIndexMap, generatedFiles, "backend", emitter, userId);
        generateFiles(blueprint, blueprint.frontendFiles(), interfaceIndexMap, generatedFiles, "frontend", emitter, userId);

        /* Step 4. All files generated — persist them to the codegen workspace. */
        SseUtil.sendProgress(emitter, "Step 4/5 — Writing " + generatedFiles.size()
                + " generated file(s) to the workspace...");
        writeGeneratedFilesToWorkspace(generatedFiles, codeGenJobId);
        SseUtil.sendProgress(emitter, "All generated files written to the workspace.");

        /*Step 5. Save the interface index... also to the Disk ...*/
        SseUtil.sendProgress(emitter, "Step 5/5 — Saving the interface index...");
        writeGeneratedInterfaceIndexToWorkspace(interfaceIndexMap, codeGenJobId);
        SseUtil.sendProgress(emitter, "Interface index saved.");
    }

    private void writeGeneratedInterfaceIndexToWorkspace(Map<String, String> interfaceIndexMap, String codeGenJobId) {
        if(interfaceIndexMap == null || interfaceIndexMap.isEmpty()) return;

       try {
           String projectRoot = System.getProperty("user.dir")
                   + File.separator + codegenWorkspaceDir
                   + File.separator + codeGenJobId + File.separator + "interface-index";

           ObjectMapper objectMapper = new ObjectMapper();
           final String interfaceIndex = objectMapper.writeValueAsString(interfaceIndexMap);
           FileUtil.writeFileToPath(projectRoot + File.separator + "index.json", interfaceIndex);
       } catch (Exception ex) {
           log.warn("Failed to write interface index for job {}", codeGenJobId, ex);
       }

    }

    private void generateFiles(Blueprint blueprint,
                               List<PlannedFile> files,
                               Map<String, String> interfaceIndexMap,
                               Map<String, String> generatedFiles,
                               String basePath,
                               SseEmitter emitter,
                               String userId
    ) {
        if (files == null || files.isEmpty()) return;

        int total = files.size();
        SseUtil.sendProgress(emitter, "Generating " + basePath + " files (" + total + " to build)...");

        int index = 0;
        for (PlannedFile file : files) {
            index++;
            String label = "[" + basePath + " " + index + "/" + total + "] ";
            String relativePath = basePath + File.separator + file.path();

            SseUtil.sendProgress(emitter, label + "Generating " + file.path() + "...");
            BuildAgent.GeneratedFile response = null;
            ReviewAgent.ReviewResult reviewResult = null;

            int attempts = 0;
            int maxAttempts = 2;

            while (attempts < maxAttempts) {
                /* can return a 'null' too */
               response = buildAgent.build(blueprint, file, interfaceIndexMap, reviewResult, userId);
               reviewResult = reviewAgent.reviewFile(response.content(), relativePath, interfaceIndexMap, userId);

               if(reviewResult.approved()) {
                   break;
               }
               ++attempts;
            }

            if (response != null) {
                interfaceIndexMap.put(relativePath, response.interfaceIndex());
                generatedFiles.put(relativePath, response.content());
                SseUtil.sendProgress(emitter, label + "Completed " + file.path());
            } else {
                SseUtil.sendProgress(emitter, label + "Skipped " + file.path() + " (no content was generated).");
            }
        }

        SseUtil.sendProgress(emitter, "Finished generating " + basePath + " files.");
    }

    private void writeGeneratedFilesToWorkspace(Map<String, String> generatedFiles, String codeGenJobId) {
        if (generatedFiles == null || generatedFiles.isEmpty()) return;

        String projectRoot = System.getProperty("user.dir")
                + File.separator + codegenWorkspaceDir
                + File.separator + codeGenJobId;

        generatedFiles.forEach((filePath, content) -> {
            String absolutePath = projectRoot + File.separator + filePath;
            FileUtil.writeFileToPath(absolutePath, content);
        });
    }

    /**
     * Sorts the backend and frontend file lists of the blueprint in-place by their
     * {@code rank} in ascending order (lower rank first). Files with a {@code null}
     * rank are pushed to the end so a missing rank never breaks ordering. Ties keep
     * their original relative order (stable sort).
     */
    private void sortFilesByRank(Blueprint projectBluePrint) {
        if (projectBluePrint == null) {
            return;
        }
        sortByRank(projectBluePrint.backendFiles());
        sortByRank(projectBluePrint.frontendFiles());
    }

    private void sortByRank(List<PlannedFile> files) {
        if (files == null || files.size() < 2) {
            return;
        }
        files.sort((first, second) -> {
            if (first == null && second == null) {
                return 0;
            }
            if (first == null) {
                return 1;
            }
            if (second == null) {
                return -1;
            }

            Integer firstRank = first.rank();
            Integer secondRank = second.rank();

            if (firstRank == null && secondRank == null) {
                return 0;
            }
            if (firstRank == null) {
                return 1;
            }
            if (secondRank == null) {
                return -1;
            }

            return Integer.compare(firstRank, secondRank);
        });
    }

    private void writeBluePrintToDisk(Blueprint blueprint, String codeGenJobId) {
        ObjectMapper objectMapper = new ObjectMapper();
        String prettyJson;
        try {
            prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(blueprint);
            String path = getBluePrintFilePath(codeGenJobId);
            FileUtil.writeFileToPath(path, prettyJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private String getBluePrintFilePath(String codeGenJobId) {
        return System.getProperty("user.dir") + File.separator + blueprintDir + File.separator + (codeGenJobId + ".json");
    }
}
