package org.aiassistant.ai.config;


import org.aiassistant.ai.exceptions.InvalidAnalysisException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class ProjectConfiguration {

    @Bean("OpenAIChatClient")
    public ChatClient openAIChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel).build();
    }

    @Bean
    public RetryTemplate analysisRetryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(3)
                .noBackoff()
                .retryOn(InvalidAnalysisException.class)
                .build();
    }

}
