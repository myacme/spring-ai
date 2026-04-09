package com.ljx.alibaba.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Milvus 向量数据库配置
 * 手动配置 MilvusVectorStore Bean
 */
@Configuration
@Slf4j
public class MilvusConfig {

    @Value("${spring.ai.vectorstore.milvus.client.host:localhost}")
    private String host;

    @Value("${spring.ai.vectorstore.milvus.client.port:19530}")
    private Integer port;

    @Value("${spring.ai.vectorstore.milvus.client.username:}")
    private String username;

    @Value("${spring.ai.vectorstore.milvus.client.password:}")
    private String password;

    @Value("${spring.ai.vectorstore.milvus.databaseName:default}")
    private String databaseName;

    @Value("${spring.ai.vectorstore.milvus.collectionName:spring_ai_collection}")
    private String collectionName;

    @Value("${spring.ai.vectorstore.milvus.embeddingDimension:1536}")
    private Integer embeddingDimension;

    @Value("${spring.ai.vectorstore.milvus.indexType:IVF_FLAT}")
    private String indexType;

    @Value("${spring.ai.vectorstore.milvus.metricType:COSINE}")
    private String metricType;

    @Value("${spring.ai.vectorstore.milvus.initializeSchema:true}")
    private Boolean initializeSchema;

    /**
     * 创建 Milvus 客户端连接
     * Spring AI Milvus Vector Store 需要此连接 会自动创建 VectorStore
     */
    @Bean
    public MilvusServiceClient milvusServiceClient() {
        log.info("正在连接 Milvus 服务器: {}:{}", host, port);
        
        ConnectParam.Builder builder = ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port);

        if (username != null && !username.isEmpty()) {
            builder.withAuthorization(username, password);
        }

        MilvusServiceClient client = new MilvusServiceClient(builder.build());
        
        // 测试连接
        try {
            var response = client.listDatabases();
            if (response.getStatus() == 0) {
                log.info("成功连接到 Milvus 服务器");
            } else {
                log.warn("Milvus 连接测试失败: {}", response.getMessage());
            }
        } catch (Exception e) {
            log.error("Milvus 连接异常: {}", e.getMessage(), e);
        }
        
        return client;
    }

    /**
     * 手动创建 Milvus Vector Store
     * 使用 DashScope 的 Embedding 模型
     */
    @Bean
    public VectorStore milvusVectorStore(MilvusServiceClient milvusServiceClient, EmbeddingModel embeddingModel) {
        log.info("初始化 Milvus Vector Store，集合名称: {}, 维度: {}", collectionName, embeddingDimension);
        
        // 将字符串转换为枚举类型
        IndexType indexTypeEnum = IndexType.valueOf(indexType);
        MetricType metricTypeEnum = MetricType.valueOf(metricType);
        
        return MilvusVectorStore.builder(milvusServiceClient, embeddingModel)
                .databaseName(databaseName)
                .collectionName(collectionName)
                .embeddingDimension(embeddingDimension)
                .indexType(indexTypeEnum)
                .metricType(metricTypeEnum)
                .initializeSchema(initializeSchema)
                .build();
    }
}
