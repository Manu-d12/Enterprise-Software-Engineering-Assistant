package org.aiassistant.ai.agents;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aiassistant.ai.dtos.codegen.Blueprint;
import org.aiassistant.ai.tools.planning.FileTools;
import org.aiassistant.ai.utils.Helper;
import org.aiassistant.entities.Project;
import org.aiassistant.services.ProjectService;
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
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Turns a plain-language requirement plus a project's file-summary index into a
 * complete, buildable {@link Blueprint}. The agent is given the project's
 * "interface index" (file name -> short description) and can pull the full text
 * of any listed context file on demand through the per-project
 * {@link FileTools#read_file(String)} tool before committing to a plan.
 */
@Service
public class PlanningAgent {

    private static final Logger log = LoggerFactory.getLogger(PlanningAgent.class);

    private final ChatClient chatClient;

    private final Resource systemPrompt;

    private final RetryTemplate retryTemplate;

    /** Base directory (relative to the working dir) under which project context docs live. */
    private final String docsBasePath;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ProjectService projectService;

    public PlanningAgent(
            @Qualifier("OpenAIChatClient") ChatClient chatClient,
            @Value("classpath:prompts/project-blue-print-code-gen.st") Resource systemPrompt,
            RetryTemplate retryTemplate,
            @Value("${project.document.upload-path}") String docsBasePath,
            ProjectService projectService
    ) {
        this.chatClient = chatClient;
        this.systemPrompt = systemPrompt;
        this.retryTemplate = retryTemplate;
        this.docsBasePath = docsBasePath;
        this.projectService = projectService;
    }

    /**
     * Produces a blueprint for the given requirement, honoring the specifications
     * captured in the project's context documents.
     *
     * @param projectId      the project whose context docs the agent may read
     * @return the parsed blueprint, or {@code null} if the model produced no usable output
     */
    public Blueprint plan(String projectId) {

        Project project = projectService.findById(projectId);

        return retryTemplate.execute(ctx -> {
            BeanOutputConverter<Blueprint> converter = Helper.buildConverter(Blueprint.class);

            PromptTemplate promptTemplate = new PromptTemplate("""
                Design the project blueprint for the following project name and description.

                name:
                {name}
                
                description:
                {description}

                PROJECT FILES INTERFACE INDEX (JSON object: file name -> description):
                {interfaceIndex}

                {format}
                """);

            Prompt prompt = promptTemplate.create(Map.of(
                    "name", project.getName(),
                    "description", project.getDescription(),
                    "interfaceIndex", toJson(project.getFileSummaries()),
                    "format", converter.getFormat()));

            String raw = this.chatClient
                    .prompt(prompt)
                    .system(systemPrompt)
                    .tools(new FileTools(docsBasePath, projectId))
                    .call()
                    .content();

            return converter.convert(raw);
        });
    }

    /** Serializes the interface index to JSON for the prompt; falls back to an empty object. */
    private String toJson(Map<String, String> interfaceIndex) {
        if (interfaceIndex == null || interfaceIndex.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(interfaceIndex);
        } catch (Exception e) {
            log.warn("Failed to serialize interface index; sending empty index", e);
            return "{}";
        }
    }
}
