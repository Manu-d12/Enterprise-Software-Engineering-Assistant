package org.aiassistant.ai.config;


import lombok.AllArgsConstructor;
import org.aiassistant.ai.advisors.TokenCounterAdvisor;
import org.aiassistant.ai.exceptions.InvalidAnalysisException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@AllArgsConstructor
@Configuration
public class ProjectConfiguration {

    private final TokenCounterAdvisor tokenCounterAdvisor;

    @Bean("OpenAIChatClient")
    public ChatClient openAIChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(tokenCounterAdvisor)
                .build();
    }

    @Bean
    public RetryTemplate analysisRetryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(3)
                .noBackoff()
                .retryOn(InvalidAnalysisException.class)
                .build();
    }


    @Bean("codeGenExecutor")
    public ThreadPoolTaskExecutor codeGenExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("codegen-");
        executor.initialize();
        return executor;
    }

}
