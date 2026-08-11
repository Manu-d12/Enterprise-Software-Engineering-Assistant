package org.aiassistant.ai.agents;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.aiassistant.ai.utils.Helper;
import org.aiassistant.utils.Constants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReviewAgent {

    private final ChatClient chatClient;
    private final Resource systemPrompt;

    public ReviewAgent(
            ChatClient chatClient,
            @Value("classpath:prompts/review-agent-system-prompt.st") Resource systemPrompt) {
        this.chatClient = chatClient;
        this.systemPrompt = systemPrompt;
    }

    public ReviewResult reviewFile(
            String content,
            String fileFullPath,
            Map<String, String> interfaceIndex,
            String userId
    ) {

        BeanOutputConverter<ReviewResult> converter = Helper.buildConverter(ReviewResult.class);

        PromptTemplate promptTemplate = new PromptTemplate("""
            Review the following generated file.
        
            File path: {fileFullPath}
        
            --- FILE CONTENT START ---
            {content}
            --- FILE CONTENT END ---
        
            Interface index (already-generated files and the members they expose — this is
            the source of truth for what this file may reference):
            {interfaceIndex}
        
            Review the file against the criteria in your instructions and return the
            structured result.
        
            {format}
            """
        );

        Prompt prompt = promptTemplate.create(Map.of(
                "fileFullPath", fileFullPath,
                "content", content,
                "interfaceIndex", formatInterfaceIndex(interfaceIndex),
                "format", converter.getFormat()
        ));

        String raw = this.chatClient
                .prompt(prompt)
                .system(systemPrompt)
                .advisors(a -> a.param(Constants.USER_ID, userId))
                .options(OpenAiChatOptions.builder().maxTokens(1000).temperature(1.0).build())
                .call()
                .content();

        return converter.convert(raw);

    }

    private String formatInterfaceIndex(Map<String, String> index) {
        if (index == null || index.isEmpty()) {
            return "(no files generated yet)";
        }
        StringBuilder sb = new StringBuilder();
        index.forEach((path, signatures) ->
                sb.append(path).append("\n").append(signatures).append("\n\n"));
        return sb.toString().strip();
    }


    @JsonClassDescription("The result of reviewing a single generated file: whether it is approved, "
            + "any specific issues that must be fixed, and a short overall assessment.")
    public record ReviewResult(

            @JsonPropertyDescription("True if the file has no blocking issues and can be accepted as-is. "
                    + "False if it has at least one blocking issue and must be regenerated. "
                    + "Minor style issues alone do not require rejection.")
            boolean approved,

            @JsonPropertyDescription("Specific, actionable problems found in the file. Each item must name "
                    + "the exact method, field, type, endpoint, or element that is wrong and state how to "
                    + "fix it (e.g. 'Calls taskRepository.findByStatus(String) which is not declared in "
                    + "TaskRepository; add the method or use findAll() and filter'). Return an empty list "
                    + "when the file is approved with no issues. Never null.")
            List<String> issues,

            @JsonPropertyDescription("A one-line overall assessment of the file, e.g. 'Valid controller, "
                    + "matches the contract' or 'Rejected: references a non-existent repository method'.")
            String summary

    ) {}

}
