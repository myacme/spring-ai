package com.ljx.alibaba.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对话记忆配置
 */
@Configuration
public class ChatMemoryConfig {

    /**
     * 创建基于消息窗口的对话记忆
     * 默认保留最近 10 条消息（5轮对话）
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                // 最多保留10条消息（用户+AI各算一条）
                .maxMessages(10)
                .build();
    }
}
