package org.aiassistant.ai.advisors;

import lombok.AllArgsConstructor;
import org.aiassistant.ai.entities.Token;
import org.aiassistant.ai.repositories.TokenRepo;
import org.aiassistant.entities.User;
import org.aiassistant.services.UserService;
import org.aiassistant.utils.Constants;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.stereotype.Component;

import java.util.Map;

@AllArgsConstructor
@Component
public class TokenCounterAdvisor implements BaseAdvisor {

    private final TokenRepo tokenRepo;
    private final UserService userService;

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        Map<String, Object> context = chatClientResponse.context();

        var usage = chatClientResponse.chatResponse().getMetadata().getUsage();

        Integer input = usage.getPromptTokens();
        Integer output = usage.getCompletionTokens();
        Integer total = usage.getTotalTokens();

        String userId = (String) context.get(Constants.USER_ID);
        User user = userService.findById(userId);
        Token token = tokenRepo.findByUser(user).orElse(Token.builder().inputTokens(0L).outputTokens(0L).totalTokens(0L).user(user).build());

        token.setInputTokens(token.getInputTokens() + input);
        token.setOutputTokens(token.getOutputTokens() + output);
        token.setTotalTokens(token.getTotalTokens() + total);

        tokenRepo.save(token);

        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
