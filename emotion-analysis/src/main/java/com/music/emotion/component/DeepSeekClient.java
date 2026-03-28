package com.music.emotion.component;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

// 调用 DeepSeek API
@Component
public class DeepSeekClient {
    private final ChatClient chatClient;
    public DeepSeekClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    public String analyze(String prompt) {
        return analyze(prompt, 0.4); // 默认温度 0.2
    }

    public String analyze(String prompt, double temperature) {
        return chatClient.prompt()
                .user(prompt)
                .options(OpenAiChatOptions.builder()
                        .withTemperature(temperature)
                        .build())
                .call()
                .content();
    }
}