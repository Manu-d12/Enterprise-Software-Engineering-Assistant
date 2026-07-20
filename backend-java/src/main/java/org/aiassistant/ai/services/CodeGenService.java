package org.aiassistant.ai.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aiassistant.ai.dtos.codegen.Blueprint;
import org.aiassistant.ai.dtos.codegen.PlannedFile;
import org.aiassistant.ai.tools.ContractTool;
import org.aiassistant.ai.tools.FileTools;
import org.aiassistant.ai.utils.Helper;
import org.aiassistant.utils.FileUtil;
import org.aiassistant.utils.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.retry.support.RetryTemplate;
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

    private static final String CONTENT_MARKER = "<<<CONTENT>>>";

    private static final String INTERFACE_MARKER = "<<<INTERFACE>>>";

    private final ThreadPoolTaskExecutor taskExecutor;

    private final Resource bluePrintCodeGenSystemPrompt;

    private final ChatClient chatClient;

    private final RetryTemplate retryTemplate;

    private final String blueprintDir;

    private final String codegenWorkspaceDir;

    private final String codeGenZipFileWorkspace;

    private final FileTools fileTools;

    public CodeGenService(
            @Qualifier("codeGenExecutor") ThreadPoolTaskExecutor taskExecutor,
            @Value("classpath:prompts/project-blue-print-code-gen.st") Resource bluePrintCodeGenSystemPrompt,
            ChatClient chatClient,
            RetryTemplate retryTemplate,
            @Value("${code.gen.project-blueprint-path}") String blueprintDir,
            @Value("${codegen.workspace.dir}") String codegenWorkspaceDir,
            @Value("${codegen.zip.workspace}") String codeGenZipFileWorkspace,
            FileTools fileTools
    ) {
        this.taskExecutor = taskExecutor;
        this.bluePrintCodeGenSystemPrompt = bluePrintCodeGenSystemPrompt;
        this.chatClient = chatClient;
        this.retryTemplate = retryTemplate;
        this.blueprintDir = blueprintDir;
        this.codegenWorkspaceDir = codegenWorkspaceDir;
        this.codeGenZipFileWorkspace = codeGenZipFileWorkspace;
        this.fileTools = fileTools;
    }

    /* No timeout — a full code-generation run can legitimately take several minutes. */
    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L;

    /* SSE event names the frontend can subscribe to. */
    private static final String EVENT_PROGRESS = "progress";
    private static final String EVENT_COMPLETE = "complete";
    private static final String EVENT_ERROR = "error";
    private static final String EVENT_ZIP_CONVERSION = "zip";

    public SseEmitter planAndGenCode(String requirement) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        taskExecutor.submit(() -> {
            String codeGenJobId = UUID.randomUUID().toString();
            try {
                sendProgress(emitter, "Code generation started. Job ID: " + codeGenJobId);

                /*
                 * Step 1. Requirement Call to LLM
                 * */
                sendProgress(emitter, "Step 1/5 — Analyzing your requirement and designing the project blueprint...");
                Blueprint projectBluePrint = getProjectBluePrint(requirement);
                if (projectBluePrint == null) {
                    sendEvent(emitter, EVENT_ERROR, "Unable to build a project blueprint from the requirement. Aborting.");
                    emitter.complete();
                    return;
                }
                int backendCount = projectBluePrint.backendFiles() == null ? 0 : projectBluePrint.backendFiles().size();
                int frontendCount = projectBluePrint.frontendFiles() == null ? 0 : projectBluePrint.frontendFiles().size();
                sendProgress(emitter, "Blueprint ready — " + backendCount + " backend file(s) and "
                        + frontendCount + " frontend file(s) planned.");

                /*
                 * Step 2. Save the contract/blue to the disk
                 * Sort the backend/frontend files by Rank ASC Order
                 * (Single source of truth)
                 * */
                sendProgress(emitter, "Step 2/5 — Ordering planned files and saving the project blueprint...");
                sortFilesByRank(projectBluePrint);
                writeBluePrintToDisk(projectBluePrint, codeGenJobId);
                sendProgress(emitter, "Project blueprint saved to disk.");

                /*
                 * Step 3. Iterate over the files
                 * Backend first --> frontend and generate the actual content.
                 * */
                sendProgress(emitter, "Step 3/5 — Generating project files...");
                generateProjectFiles(projectBluePrint, codeGenJobId, emitter);

                sendEvent(emitter, EVENT_COMPLETE, "Code generation completed successfully. Job ID: " + codeGenJobId);

                sendEvent(emitter, EVENT_ZIP_CONVERSION, "Converting the code into the ZIP format.");

                String sourceDir = System.getProperty("user.dir")
                        + File.separator + codegenWorkspaceDir
                        + File.separator + codeGenJobId;

                String destZipDir = System.getProperty("user.dir")
                        + File.separator + codeGenZipFileWorkspace
                        + File.separator + codeGenJobId + ".zip";

                ZipUtil.createZipFile(sourceDir, destZipDir);

                sendEvent(emitter, EVENT_ZIP_CONVERSION, "Zip Conversion Done...");

                emitter.complete();
            } catch (Exception ex) {
                log.error("Code generation failed for job {}", codeGenJobId, ex);
                sendEvent(emitter, EVENT_ERROR, "Code generation failed: " + ex.getMessage());
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    private void generateProjectFiles(Blueprint blueprint, String codeGenJobId, SseEmitter emitter) {

        if(blueprint == null) return;

        if(blueprint.frontendFiles() == null && blueprint.backendFiles() == null) return;

        /* Key will be file-path && value will be exports and all of that...*/
        Map<String, String> interfaceIndexMap = new HashMap<>();

        /* Key will be file-path && value will be the generated file content. */
        Map<String, String> generatedFiles = new LinkedHashMap<>();

        /* Backend first so their interfaces are available to the frontend. */
        generateFiles(blueprint, blueprint.backendFiles(), interfaceIndexMap, generatedFiles, "backend", emitter);
        generateFiles(blueprint, blueprint.frontendFiles(), interfaceIndexMap, generatedFiles, "frontend", emitter);

        /* Step 4. All files generated — persist them to the codegen workspace. */
        sendProgress(emitter, "Step 4/5 — Writing " + generatedFiles.size()
                + " generated file(s) to the workspace...");
        writeGeneratedFilesToWorkspace(generatedFiles, codeGenJobId);
        sendProgress(emitter, "All generated files written to the workspace.");

        /*Step 5. Save the interface index... also to the Disk ...*/
        sendProgress(emitter, "Step 5/5 — Saving the interface index...");
        writeGeneratedInterfaceIndexToWorkspace(interfaceIndexMap, codeGenJobId);
        sendProgress(emitter, "Interface index saved.");
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
           System.out.println(ex.getMessage());
       }

    }

    private void generateFiles(Blueprint blueprint,
                               List<PlannedFile> files,
                               Map<String, String> interfaceIndexMap,
                               Map<String, String> generatedFiles,
                               String basePath,
                               SseEmitter emitter) {
        if (files == null || files.isEmpty()) return;

        int total = files.size();
        sendProgress(emitter, "Generating " + basePath + " files (" + total + " to build)...");

        int index = 0;
        for (PlannedFile file : files) {
            index++;
            String label = "[" + basePath + " " + index + "/" + total + "] ";
            String relativePath = basePath + File.separator + file.path();

            sendProgress(emitter, label + "Building context for " + file.path() + "...");
            String context = generateFileContext(file, interfaceIndexMap);

            sendProgress(emitter, label + "Generating " + file.path() + "...");
            GeneratedFile response = generateFile(blueprint, context);
            if (response != null) {
                interfaceIndexMap.put(relativePath, response.interfaceIndex());
                generatedFiles.put(relativePath, response.content());
                sendProgress(emitter, label + "Completed " + file.path());
            } else {
                sendProgress(emitter, label + "Skipped " + file.path() + " (no content was generated).");
            }
        }

        sendProgress(emitter, "Finished generating " + basePath + " files.");
    }

    /**
     * Emits a human-readable progress line to the client under the {@code progress} event.
     * Delegates to {@link #sendEvent}.
     */
    private void sendProgress(SseEmitter emitter, String message) {
        sendEvent(emitter, EVENT_PROGRESS, message);
    }

    /**
     * Sends a single SSE event to the client. Any failure (typically the client having
     * disconnected) is swallowed so it never interrupts the generation flow.
     */
    private void sendEvent(SseEmitter emitter, String eventName, String message) {
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name(eventName)
                    .data(message));
        } catch (Exception ignored) {
            // Client likely disconnected; keep generating without failing the run.
        }
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

    private GeneratedFile generateFile(Blueprint blueprint, String context) {
        return retryTemplate.execute((ctx) -> {
            PromptTemplate promptTemplate = new PromptTemplate("""
            Generate the file described below.

            {context}

            Respond using EXACTLY this format, with no markdown code fences and no
            commentary of any kind:

            <<<CONTENT>>>
            <the complete, raw content of the file, exactly as it should be written to disk>
            <<<INTERFACE>>>
            <a compact summary of the public interface this file exposes, for other files
            to stay consistent with. Include only signatures, not bodies: class/interface
            name, public method signatures with parameter and return types, field names and
            types, and for web files any endpoint paths or element IDs.
            Example: 'UserService (class): createUser(CreateUserRequest) -> UserDto,
            getUser(Long) -> UserDto'>
        """);

            Prompt prompt = promptTemplate.create(Map.of("context", context));

            String raw = this.chatClient
                    .prompt(prompt)
                    .tools(fileTools, new ContractTool(blueprint))
                    .call()
                    .content();

            return parseGeneratedFile(raw);
        });
    }

    /**
     * Splits the raw LLM response into file content and interface index using the
     * {@code <<<CONTENT>>>} / {@code <<<INTERFACE>>>} markers. If the model didn't emit
     * the markers, the whole response is treated as content with an empty interface index.
     */
    private GeneratedFile parseGeneratedFile(String raw) {
        if (raw == null) {
            return null;
        }

        String content;
        String interfaceIndex;

        int interfaceIdx = raw.indexOf(INTERFACE_MARKER);
        if (interfaceIdx != -1) {
            int contentIdx = raw.indexOf(CONTENT_MARKER);
            int contentStart = contentIdx != -1 ? contentIdx + CONTENT_MARKER.length() : 0;
            content = raw.substring(contentStart, interfaceIdx).strip();
            interfaceIndex = raw.substring(interfaceIdx + INTERFACE_MARKER.length()).strip();
        } else {
            // model didn't emit markers — fall back: treat whole thing as content, empty index
            content = raw;
            interfaceIndex = "";
        }

        return new GeneratedFile(stripCodeFences(content), interfaceIndex);
    }

    /**
     * Removes a surrounding markdown code fence (```lang ... ```) if the model wrapped
     * the content in one despite being told not to.
     */
    private String stripCodeFences(String text) {
        if (text == null) {
            return null;
        }
        String stripped = text.strip();
        if (!stripped.startsWith("```")) {
            return stripped;
        }
        int firstNewline = stripped.indexOf('\n');
        if (firstNewline == -1) {
            return stripped;
        }
        String body = stripped.substring(firstNewline + 1);
        int closingFence = body.lastIndexOf("```");
        if (closingFence != -1) {
            body = body.substring(0, closingFence);
        }
        return body.strip();
    }

    /** The generated file's raw content plus a compact summary of the interface it exposes. */
    private record GeneratedFile(String content, String interfaceIndex) {}

    private String generateFileContext(PlannedFile file, Map<String, String> interfaceIndexMap) {
        StringBuilder context = new StringBuilder();

        // 1. The file to generate — its spec
        context.append("## File to generate\n");
        context.append("Path: ").append(file.path()).append("\n");
        context.append("Type: ").append(file.fileType()).append("\n");
        if (file.packageName() != null && !file.packageName().isBlank()) {
            context.append("Package: ").append(file.packageName()).append("\n");
        }
        context.append("Purpose: ").append(file.purpose()).append("\n");

        // 2. The relevant interface-index slice (already-generated files' signatures)
        if (interfaceIndexMap != null && !interfaceIndexMap.isEmpty()) {
            context.append("\n## Already-generated files you must stay consistent with\n");
            interfaceIndexMap.forEach((path, signatures) -> {
                context.append("\n").append(path).append("\n");
                context.append(signatures).append("\n");
            });
            context.append("\nRules:\n");
            context.append("- Use these exact names, types, and signatures.\n");
            context.append("- Do not invent members that aren't listed above.\n");
        }

        return context.toString();
    }

    private Blueprint getProjectBluePrint(String requirement) {
        return retryTemplate.execute((ctx) -> {
            BeanOutputConverter<Blueprint> converter = Helper.buildConverter(Blueprint.class);

            PromptTemplate promptTemplate = new PromptTemplate("""
            Analyze the following requirements.
    
            Requirement:
            {requirement}
    
            {format}
        """);

            Prompt prompt = promptTemplate.create(Map.of(
                    "requirement", requirement,
                    "format", converter.getFormat()));

            String raw = this.chatClient
                    .prompt(prompt)
                    .system(bluePrintCodeGenSystemPrompt)
                    .tools()
                    .call()
                    .content();

            return converter.convert(raw);
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
