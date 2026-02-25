package com.ljx.service;


import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author ljx
 * @version 1.0.0
 * @create 2025/12/18 13:55
 */
@Service
public class StreamingService {

    private final StreamingChatClient streamingChatClient;

    public StreamingService(StreamingChatClient streamingChatClient) {
        this.streamingChatClient = streamingChatClient;
    }

    public Flux<String> streamChat(String message) {
        return streamingChatClient.stream(new Prompt(message))
                .map(ChatResponse::getResult)
                .map(result -> result.getOutput().getContent());
    }

    public String contextualChat(String userMessage, List<Message> history) {

        // 添加历史对话
        List<Message> messages = new ArrayList<>(history);

        // 添加新消息
        messages.add(new UserMessage(userMessage));

        Prompt prompt = new Prompt(messages);
        Flux<ChatResponse> response = streamingChatClient.stream(prompt);
        return response.blockFirst().getResult().getOutput().getContent();
    }
}