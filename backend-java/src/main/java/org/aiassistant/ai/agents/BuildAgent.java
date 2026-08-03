package org.aiassistant.ai.agents;

import org.aiassistant.ai.dtos.codegen.Blueprint;
import org.aiassistant.ai.dtos.codegen.PlannedFile;
import org.aiassistant.ai.tools.ContractTool;
import org.aiassistant.ai.tools.FileTools;
import org.aiassistant.utils.Constants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Generates the actual content of a single planned file. Owns everything about turning a
 * {@link PlannedFile} into a written file: building the LLM context, calling the model
 * (with the file/contract tools available), and parsing the response into content plus a
 * compact interface summary that later files stay consistent with.
 */
@Service
public class BuildAgent {

    private static final String CONTENT_MARKER = "<<<CONTENT>>>";

    private static final String INTERFACE_MARKER = "<<<INTERFACE>>>";

    private final RetryTemplate retryTemplate;

    private final ChatClient chatClient;

    private final FileTools fileTools;

    public BuildAgent(
            RetryTemplate retryTemplate,
            @Qualifier("OpenAIChatClient") ChatClient chatClient,
            FileTools fileTools) {
        this.retryTemplate = retryTemplate;
        this.chatClient = chatClient;
        this.fileTools = fileTools;
    }

    /**
     * Builds the context for {@code file}, sends it to the LLM, and parses the response.
     *
     * @param blueprint         the overall project blueprint (exposes the API contract tool)
     * @param file              the file to generate
     * @param interfaceIndexMap signatures of already-generated files, keyed by relative path
     * @return the generated file, or {@code null} if the model produced no usable output
     */
    public GeneratedFile build(Blueprint blueprint, PlannedFile file, Map<String, String> interfaceIndexMap, ReviewAgent.ReviewResult reviewResult, String userId) {
        String context = generateFileContext(file, interfaceIndexMap, reviewResult);
        return generateFile(blueprint, context, userId);
    }

    private GeneratedFile generateFile(Blueprint blueprint, String context, String userId) {
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
                    .advisors(a -> a.param(Constants.USER_ID, userId))
                    .call()
                    .content();

            return parseGeneratedFile(raw);
        });
    }

    private String generateFileContext(PlannedFile file, Map<String, String> interfaceIndexMap, ReviewAgent.ReviewResult reviewResult) {
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
            context.append("\n## Already-generated files Interface Indexes you must stay consistent with\n");
            interfaceIndexMap.forEach((path, signatures) -> {
                context.append("\n").append(path).append("\n");
                context.append(signatures).append("\n");
            });
            context.append("\nRules:\n");
            context.append("- Use these exact names, types, and signatures.\n");
            context.append("- Do not invent members that aren't listed above.\n");
        }

        // 3. Review feedback from the previous attempt (present only on a regeneration)
        if (reviewResult != null && !reviewResult.approved()
                && reviewResult.issues() != null && !reviewResult.issues().isEmpty()) {
            context.append("\n## Fix these issues from your previous attempt\n");
            context.append("Your previous version of this file was rejected. ");
            context.append("Regenerate the ENTIRE file correcting ALL of the problems below:\n");
            int i = 1;
            for (String issue : reviewResult.issues()) {
                context.append(i++).append(". ").append(issue).append("\n");
            }
            if (reviewResult.summary() != null && !reviewResult.summary().isBlank()) {
                context.append("\nReviewer summary: ").append(reviewResult.summary()).append("\n");
            }
            context.append("\nDo not repeat these mistakes. Output the corrected file in full.\n");
        }

        return context.toString();
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
    public record GeneratedFile(String content, String interfaceIndex) {}
}
