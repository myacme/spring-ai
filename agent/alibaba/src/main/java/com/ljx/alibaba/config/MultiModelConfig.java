package com.ljx.alibaba.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MultiModelConfig {

    // 模型名称常量定义，一套系统多模型共存
    private final String DEEPSEEK_MODEL = "deepseek-v3.2";
    private final String QWEN_MODEL = "qwen-max";

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean(name = "deepseek")
    public ChatModel deepSeek() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(DEEPSEEK_MODEL).build())
                .build();
    }

    @Bean(name = "qwen")
    @Primary  // 设置为默认的主要 Bean
    public ChatModel qwen() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(QWEN_MODEL).build())
                .build();
    }

    @Bean(name = "deepseekChatClient")
    public ChatClient deepseekChatClient(@Qualifier("deepseek") ChatModel deepseek) {
        return ChatClient.builder(deepseek)
                .defaultOptions(ChatOptions.builder().model(DEEPSEEK_MODEL).build())
                .build();
    }

    @Bean(name = "qwenChatClient")
    @Primary  // 设置为默认的主要 Bean
    public ChatClient qwenChatClient(@Qualifier("qwen") ChatModel qwen) {
        return ChatClient.builder(qwen)
                .defaultOptions(ChatOptions.builder().model(QWEN_MODEL).build())
                .build();
    }

    /**
     * 创建 DashScope Embedding 模型
     * 用于文本向量化，供 Milvus 向量数据库使用
     */
    @Bean
    @ConditionalOnMissingBean
    public EmbeddingModel embeddingModel() {
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();
        return new DashScopeEmbeddingModel(dashScopeApi);
    }
}
