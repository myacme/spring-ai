package com.ljx.alibaba.controller;

import com.ljx.alibaba.service.MilvusVectorService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Milvus 向量数据库控制器
 * 提供文档存储和相似度搜索功能
 */
@RestController
@RequestMapping("/api/milvus")
@RequiredArgsConstructor
public class MilvusController {

    private final MilvusVectorService milvusVectorService;

    /**
     * 添加单个文档
     */
    @PostMapping("/add")
    public Map<String, Object> addDocument(@RequestBody AddDocumentRequest request) {
        milvusVectorService.addDocument(request.getText(), request.getMetadata());
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "文档添加成功");
        result.put("textLength", request.getText().length());
        
        return result;
    }

    /**
     * 批量添加文档（从分词结果）
     */
    @PostMapping("/add-batch")
    public Map<String, Object> addBatchDocuments(@RequestBody BatchAddRequest request) {
        List<Document> documents = request.getTexts().stream()
                .map(text -> new Document(text))
                .collect(Collectors.toList());
        
        milvusVectorService.addDocuments(documents);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "批量添加成功");
        result.put("count", documents.size());
        
        return result;
    }

    /**
     * 导入文本并自动分块
     */
    @PostMapping("/import")
    public Map<String, Object> importText(@RequestBody ImportRequest request) {
        int chunkSize = request.getChunkSize() != null ? request.getChunkSize() : 500;
        milvusVectorService.importTextWithSplitting(request.getText(), chunkSize);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "文本导入成功");
        result.put("textLength", request.getText().length());
        result.put("chunkSize", chunkSize);
        
        return result;
    }

    /**
     * 相似度搜索
     */
    @PostMapping("/search")
    public Map<String, Object> search(@RequestBody SearchRequest request) {
        int topK = request.getTopK() != null ? request.getTopK() : 5;
        double threshold = request.getThreshold() != null ? request.getThreshold() : 0.0;
        
        List<Document> results = milvusVectorService.similaritySearch(
                request.getQuery(), 
                topK, 
                threshold
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("query", request.getQuery());
        result.put("resultCount", results.size());
        result.put("results", results.stream()
                .map(doc -> {
                    Map<String, Object> docMap = new HashMap<>();
                    docMap.put("content", doc.getText());
                    docMap.put("id", doc.getId());
                    docMap.put("metadata", doc.getMetadata());
                    docMap.put("score", doc.getScore());
                    return docMap;
                })
                .collect(Collectors.toList()));
        
        return result;
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteDocuments(@RequestBody DeleteRequest request) {
        milvusVectorService.deleteDocuments(request.getDocumentIds());
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "文档删除成功");
        result.put("deletedCount", request.getDocumentIds().size());
        
        return result;
    }

    // ==================== 请求 DTO ====================

    @Data
    static class AddDocumentRequest {
        private String text;
        private Map<String, Object> metadata;
    }

    @Data
    static class BatchAddRequest {
        private List<String> texts;
    }

    @Data
    static class ImportRequest {
        private String text;
        private Integer chunkSize;
    }

    @Data
    static class SearchRequest {
        private String query;
        private Integer topK;
        private Double threshold;
    }

    @Data
    static class DeleteRequest {
        private List<String> documentIds;
    }
}
