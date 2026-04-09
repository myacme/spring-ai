package com.ljx.alibaba.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 聊天服务 - 演示 Spring AI Alibaba + DeepSeek 基础功能
 */
@Service
@Slf4j
public class ChatService {

    @Resource(name = "qwenChatClient")
    private ChatClient chatClient;
    
    @Resource
    private ChatMemory chatMemory;  // 注入对话记忆

    /**
     * 普通聊天（带记忆）
     */
    public String chat(String message, String conversationId) {
        log.info("收到用户消息 [会话:{}]: {}", conversationId, message);
        
        // 如果没有提供会话ID，使用默认值
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = "default";
        }

        // 1.工具注册到工具集合里
        ToolCallback[] tools = ToolCallbacks.from(new WeatherToolService());

        // 2.将工具集配置进ChatOptions对象
        ChatOptions options = ToolCallingChatOptions.builder().toolCallbacks(tools).build();

        // 3.构建提示词
        Prompt prompt = new Prompt(message, options);

        String finalConversationId = conversationId;
        String response = chatClient.prompt(prompt)
//                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, finalConversationId))  // 使用对话记忆
                .call()
                .content();

        log.info("AI 回复 [会话:{}]: {}", conversationId, response);
        return response;
    }
    
    /**
     * 兼容旧版本的聊天方法（不带会话ID）
     */
    public String chat(String message) {
        return chat(message, "default");
    }



    
    /**
     * 流式聊天（带记忆）
     */
    public Flux<String> chatStream(String message, String conversationId) {
        log.info("收到流式请求 [会话:{}]: {}", conversationId, message);
        
        // 如果没有提供会话ID，使用默认值
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = "default";
        }

        String finalConversationId = conversationId;
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, finalConversationId))  // 使用对话记忆
                // tools 和 RAG advisor 已经在 ChatClient 创建时通过 defaultAdvisors 和 defaultTools 注册
                .stream()
                .content();
    }
    
    /**
     * 兼容旧版本的流式聊天方法（不带会话ID）
     */
    public Flux<String> chatStream(String message) {
        return chatStream(message, "default");
    }
}
