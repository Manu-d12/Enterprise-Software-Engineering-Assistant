package org.aiassistant.ai.config;


import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
public class ProjectConfiguration {

    private final OpenAiChatModel openAiChatModel;

    @Bean("OpenAIChatClient")
    public ChatClient openAIChatClient() {
        return ChatClient.builder(openAiChatModel).build();
    }

}
