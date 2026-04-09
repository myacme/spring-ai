package com.ljx.alibaba.config;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther zzyybs@126.com
 * @create 2025-07-28 18:24
 * @Description TODO
 */
@Configuration
public class RedisMemoryConfig {

    @Value("${spring.ai.vectorstore.redis.host:localhost}")
    private String host;

    @Value("${spring.ai.vectorstore.redis.port:6379}")
    private Integer port;

    private final String QWEN_MODEL = "qwen-max";

    @Bean
    public RedisChatMemoryRepository redisChatMemoryRepository() {
        return RedisChatMemoryRepository.builder()
                .host(host)
                .port(port)
                .build();
    }

    @Bean(name = "redisMemoryChatClient")
    public ChatClient qwenChatClient(ChatModel qwen,
                                     RedisChatMemoryRepository redisChatMemoryRepository) {
        MessageWindowChatMemory windowChatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(redisChatMemoryRepository)
                .maxMessages(10)
                .build();

        return ChatClient.builder(qwen)
                .defaultOptions(ChatOptions.builder().model(QWEN_MODEL).build())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(windowChatMemory).build())
                .build();
    }
}
