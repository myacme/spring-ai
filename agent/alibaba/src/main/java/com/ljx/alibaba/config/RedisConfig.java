package com.ljx.alibaba.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

/**
 * Redis Stack 向量数据库配置
 * 用于 RAG (Retrieval-Augmented Generation) 场景
 */
@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.ai.vectorstore.redis.host:localhost}")
    private String host;

    @Value("${spring.ai.vectorstore.redis.port:6379}")
    private Integer port;

    @Value("${spring.ai.vectorstore.redis.password:}")
    private String password;

    @Value("${spring.ai.vectorstore.redis.index:spring-ai-index}")
    private String indexName;

    @Value("${spring.ai.vectorstore.redis.prefix:doc:}")
    private String prefix;

    /**
     * 创建 JedisPooled 连接池
     * Spring AI Redis Vector Store 需要此连接
     */
    @Bean
    public JedisPooled jedisPooled() {
        log.info("正在连接 Redis Stack 服务器: {}:{}", host, port);
        
        JedisPooled jedisPooled;
        if (password != null && !password.isEmpty()) {
            // 使用 URI 方式连接，支持密码
            String uri = String.format("redis://:%s@%s:%d", password, host, port);
            jedisPooled = new JedisPooled(uri);
        } else {
            jedisPooled = new JedisPooled(host, port);
        }
        
        // 测试连接
        try {
            String pong = jedisPooled.ping();
            if ("PONG".equals(pong)) {
                log.info("成功连接到 Redis Stack 服务器");
            }
        } catch (Exception e) {
            log.error("Redis Stack 连接异常: {}", e.getMessage(), e);
        }
        
        return jedisPooled;
    }

    /**
     * 创建 Redis Vector Store
     * 使用 DashScope 的 Embedding 模型（维度为 1536）
     * 注意：索引名称和前缀通过 application.yml 配置
     */
    @Bean
    public RedisVectorStore redisVectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        log.info("初始化 Redis Vector Store");
        
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .initializeSchema(true)  // 自动初始化索引结构
                .build();
    }
}
