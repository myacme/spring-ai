package com.ljx.alibaba.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Redis 向量数据库服务
 * 提供文档存储、检索和 RAG 功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisVectorService {

    private final RedisVectorStore redisVectorStore;

    /**
     * 添加单个文档到向量数据库
     *
     * @param content 文档内容
     * @param metadata 元数据（可选）
     * @return 文档ID
     */
    public String addDocument(String content, Map<String, Object> metadata) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("文档内容不能为空");
        }
        
        log.info("添加文档到 Redis Vector Store，内容长度: {}", content.length());
        
        Document document = new Document(content, metadata != null ? metadata : Map.of());
        redisVectorStore.add(List.of(document));
        
        log.info("文档添加成功");
        return document.getId();
    }

    /**
     * 批量添加文档到向量数据库
     *
     * @param documents 文档列表，每个文档包含内容和元数据
     * @return 添加的文档数量
     */
    public int addDocuments(List<Map<String, Object>> documents) {
        log.info("批量添加 {} 个文档到 Redis Vector Store", documents.size());
        
        List<Document> documentList = documents.stream()
                .filter(doc -> {
                    String content = (String) doc.get("content");
                    if (content == null || content.trim().isEmpty()) {
                        log.warn("跳过空内容文档: {}", doc);
                        return false;
                    }
                    return true;
                })
                .map(doc -> {
                    String content = (String) doc.get("content");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> metadata = (Map<String, Object>) doc.getOrDefault("metadata", Map.of());
                    return new Document(content, metadata);
                })
                .collect(Collectors.toList());
        
        if (documentList.isEmpty()) {
            log.warn("没有有效的文档可添加");
            return 0;
        }
        
        redisVectorStore.add(documentList);
        log.info("成功添加 {} 个文档", documentList.size());
        
        return documentList.size();
    }

    /**
     * 相似度搜索
     *
     * @param query 查询文本
     * @param topK 返回最相似的 K 个结果
     * @return 相似文档列表
     */
    public List<Document> similaritySearch(String query, int topK) {
        log.info("执行相似度搜索，查询: {}, topK: {}", query, topK);
        
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();
        
        List<Document> results = redisVectorStore.similaritySearch(searchRequest);
        log.info("搜索完成，找到 {} 个相关文档", results.size());
        
        return results;
    }

    /**
     * 删除文档
     *
     * @param documentIds 文档ID列表
     * @return 删除的文档数量
     */
    public boolean deleteDocuments(List<String> documentIds) {
        log.info("删除 {} 个文档", documentIds.size());
        
        try {
            redisVectorStore.delete(documentIds);
            log.info("成功删除文档");
            return true;
        } catch (Exception e) {
            log.error("删除文档失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 清空所有文档
     */
    public void clearAll() {
        log.info("清空 Redis Vector Store 中的所有文档");
        
        // 获取所有文档ID并删除
        List<Document> allDocs = redisVectorStore.similaritySearch(SearchRequest.builder()
                .query("*")
                .topK(10000)
                .build());
        
        List<String> ids = allDocs.stream()
                .map(Document::getId)
                .collect(Collectors.toList());
        
        if (!ids.isEmpty()) {
            redisVectorStore.delete(ids);
            log.info("已清空 {} 个文档", ids.size());
        }
    }

    /**
     * 获取文档数量（近似值）
     *
     * @return 文档数量
     */
    public long getDocumentCount() {
        try {
            List<Document> sample = redisVectorStore.similaritySearch(SearchRequest.builder()
                    .query("*")
                    .topK(1)
                    .build());
            // Redis Vector Store 不直接提供计数方法，这里仅作示例
            return -1; // 表示未知
        } catch (Exception e) {
            log.error("获取文档数量失败: {}", e.getMessage());
            return -1;
        }
    }
}
