package com.ljx.alibaba.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Milvus 向量数据库服务
 * 提供文档存储、检索和相似度搜索功能
 */
@Service
@Slf4j
public class MilvusVectorService {


    @Resource
    private VectorStore vectorStore;

    /**
     * 添加文档到向量数据库
     *
     * @param documents 文档列表
     */
    public void addDocuments(List<Document> documents) {
        log.info("开始添加 {} 个文档到 Milvus", documents.size());
        vectorStore.add(documents);
        log.info("成功添加 {} 个文档", documents.size());
    }

    /**
     * 添加单个文档
     *
     * @param text     文档内容
     * @param metadata 元数据（可选）
     */
    public void addDocument(String text, Map<String, Object> metadata) {
        log.info("添加单个文档，长度: {}", text.length());
        Document document = new Document(text, metadata != null ? metadata : Map.of());
        vectorStore.add(List.of(document));
        log.info("文档添加成功");
    }

    /**
     * 相似度搜索
     *
     * @param query 查询文本
     * @return 相似的文档列表
     */
    public List<Document> similaritySearch(String query) {
        return similaritySearch(query, 5);
    }

    /**
     * 相似度搜索（可指定返回数量）
     *
     * @param query     查询文本
     * @param topK      返回最相似的 K 个结果
     * @param threshold 相似度阈值（0-1），低于此值的结果将被过滤
     * @return 相似的文档列表
     */
    public List<Document> similaritySearch(String query, int topK, double threshold) {
        log.info("执行相似度搜索，查询: {}, topK: {}, threshold: {}", query, topK, threshold);

        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(threshold)
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);
        log.info("搜索完成，找到 {} 个相似文档", results.size());

        return results;
    }

    /**
     * 相似度搜索（使用默认参数）
     *
     * @param query 查询文本
     * @param topK  返回最相似的 K 个结果
     * @return 相似的文档列表
     */
    public List<Document> similaritySearch(String query, int topK) {
        return similaritySearch(query, topK, 0.0);
    }

    /**
     * 删除文档
     *
     * @param documentIds 文档 ID 列表
     */
    public void deleteDocuments(List<String> documentIds) {
        log.info("删除 {} 个文档", documentIds.size());
        vectorStore.delete(documentIds);
        log.info("文档删除成功");
    }

    /**
     * 批量导入分块后的文档
     *
     * @param text      原始文本
     * @param chunkSize 每块的字符数
     */
    public void importTextWithSplitting(String text, int chunkSize) {
        log.info("开始导入文本并自动分块，总长度: {}, 块大小: {}", text.length(), chunkSize);

        // 简单按固定长度分块
        List<Document> documents = splitTextIntoDocuments(text, chunkSize);

        // 添加到向量数据库
        addDocuments(documents);

        log.info("文本导入完成，共 {} 个文档块", documents.size());
    }

    /**
     * 将文本分割为文档列表
     */
    private List<Document> splitTextIntoDocuments(String text, int chunkSize) {
        List<Document> documents = new java.util.ArrayList<>();
        int totalLength = text.length();
        int index = 0;

        for (int i = 0; i < totalLength; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, totalLength);
            String chunk = text.substring(i, endIndex);

            if (!chunk.trim().isEmpty()) {
                Map<String, Object> metadata = Map.of(
                        "chunkIndex", index++,
                        "totalChunks", (totalLength + chunkSize - 1) / chunkSize,
                        "source", "imported_text"
                );
                documents.add(new Document(chunk, metadata));
            }
        }

        return documents;
    }
}
