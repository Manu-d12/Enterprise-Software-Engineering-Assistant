package org.aiassistant.ai.services;

import org.aiassistant.ai.dtos.RequirementsAnalysisDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RequirementAnalysisService {

    private final ChatClient chatClient;

    private final Resource systemPrompt;

    private final RetryTemplate retryTemplate;

    public RequirementAnalysisService(
            ChatClient chatClient,
            @Value("classpath:prompts/requirements-system.st") Resource resource,
            RetryTemplate retryTemplate
    ) {
        this.chatClient = chatClient;
        this.systemPrompt = resource;
        this.retryTemplate = retryTemplate;
    }

    public RequirementsAnalysisDTO getRequirements(String query) {
        return retryTemplate.execute((context) -> {
            BeanOutputConverter<RequirementsAnalysisDTO> converter =
                    new BeanOutputConverter<>(RequirementsAnalysisDTO.class);

            PromptTemplate promptTemplate = new PromptTemplate("""
            Analyze the following software system and produce a complete requirements analysis.
    
            System description:
            {projectDescription}
    
            {format}
        """);

            Prompt prompt = promptTemplate.create(Map.of(
                    "projectDescription", query,
                    "format", converter.getFormat()));

            String raw = chatClient.prompt(prompt).system(systemPrompt).call().content();
            return converter.convert(raw);
        });
    }
}
