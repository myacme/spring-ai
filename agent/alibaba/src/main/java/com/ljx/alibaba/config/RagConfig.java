package com.ljx.alibaba.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    private final String QWEN_MODEL = "qwen-max";

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean(name = "milvusChatClient")
    public ChatClient qwenChatClient(@Qualifier("qwen") ChatModel qwen,
                                     ChatMemory chatMemory,
                                     VectorStore vectorStore) {

        RetrievalAugmentationAdvisor advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore).build())
                .build();
        return
                ChatClient.builder(qwen)
                        .defaultOptions(ChatOptions.builder().model(QWEN_MODEL).build())
                        .defaultAdvisors(
                                MessageChatMemoryAdvisor.builder(chatMemory).build(), // 启用对话记忆
                                advisor
                        )
                        //                        .defaultTools(weatherToolService)  // 注册工具
                        .build();
    }
}
