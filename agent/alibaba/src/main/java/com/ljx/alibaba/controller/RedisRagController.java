package com.ljx.alibaba.controller;

import com.ljx.alibaba.entity.DeleteDocumentsRequest;
import com.ljx.alibaba.entity.DocumentRequest;
import com.ljx.alibaba.entity.RagChatRequest;
import com.ljx.alibaba.service.RedisVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Redis RAG 控制器
 * 提供基于 Redis Stack 向量数据库的检索增强生成功能
 */
@RestController
@RequestMapping("/api/redis-rag")
@RequiredArgsConstructor
@Slf4j
public class RedisRagController {

    private final RedisVectorService redisVectorService;
    private final ChatClient chatClient;

    /**
     * 添加单个文档
     *
     * @param request 包含文档内容和元数据
     * @return 文档ID
     */
    @PostMapping("/document")
    public Map<String, Object> addDocument(@RequestBody DocumentRequest request) {
        if (request.getContent() == null || request.getContent().isEmpty()) {
            throw new IllegalArgumentException("文档内容不能为空");
        }

        String documentId = redisVectorService.addDocument(request.getContent(), request.getMetadata());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("documentId", documentId);
        response.put("message", "文档添加成功");

        return response;
    }

    /**
     * 批量添加文档
     *
     * @param documents 文档列表
     * @return 添加结果
     */
    @PostMapping("/documents/batch")
    public Map<String, Object> addDocuments(@RequestBody List<DocumentRequest> documents) {
        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException("文档列表不能为空");
        }

        // 转换为 Map 格式供 Service 层使用
        List<Map<String, Object>> documentMaps = documents.stream()
                .map(doc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("content", doc.getContent());
                    map.put("metadata", doc.getMetadata() != null ? doc.getMetadata() : Map.of());
                    return map;
                })
                .collect(Collectors.toList());

        int count = redisVectorService.addDocuments(documentMaps);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        response.put("message", "成功添加 " + count + " 个文档");

        return response;
    }

    /**
     * 相似度搜索
     *
     * @param query 查询文本
     * @return 相似文档列表
     */
    @GetMapping("/search")
    public Map<String, Object> search(String query) {
        int topK = 5;
        List<Document> documents = redisVectorService.similaritySearch(query, topK);

        List<Map<String, Object>> results = documents.stream()
                .map(doc -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", doc.getId());
                    result.put("content", doc.getText());
                    result.put("metadata", doc.getMetadata());
                    result.put("score", doc.getScore());
                    return result;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("query", query);
        response.put("results", results);
        response.put("count", results.size());

        return response;
    }

    /**
     * RAG 问答 - 基于检索的内容生成答案
     *
     * @param request 包含问题和可选的 topK 参数
     * @return AI 生成的答案和引用的文档
     */
    @PostMapping("/chat")
    public Map<String, Object> ragChat(@RequestBody RagChatRequest request) {
        if (request.getQuestion() == null || request.getQuestion().isEmpty()) {
            throw new IllegalArgumentException("问题不能为空");
        }

        int topK = request.getTopK() != null ? request.getTopK() : 3;

        log.info("收到 RAG 问答请求: {}", request.getQuestion());

        // 1. 检索相关文档
        List<Document> relevantDocs = redisVectorService.similaritySearch(request.getQuestion(), topK);

        if (relevantDocs.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("answer", "抱歉，我没有找到相关的信息来回答您的问题。");
            response.put("sources", List.of());
            return response;
        }

        // 2. 构建上下文
        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // 3. 构建提示词
        String prompt = String.format(
                "基于以下参考信息回答问题。如果参考信息中没有相关内容，请说明无法回答。\n\n" +
                        "参考信息：\n%s\n\n" +
                        "问题：%s\n\n" +
                        "请给出详细、准确的回答：",
                context, request.getQuestion()
        );

        // 4. 调用 AI 生成答案
        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // 5. 构建响应
        List<Map<String, Object>> sources = relevantDocs.stream()
                .map(doc -> {
                    Map<String, Object> source = new HashMap<>();
                    source.put("id", doc.getId());
                    source.put("content", doc.getText());
                    source.put("score", doc.getScore());
                    source.put("metadata", doc.getMetadata());
                    return source;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("question", request.getQuestion());
        response.put("answer", answer);
        response.put("sources", sources);
        response.put("sourceCount", sources.size());

        return response;
    }

    /**
     * 删除文档
     *
     * @param request 包含文档ID列表
     * @return 删除结果
     */
    @DeleteMapping("/documents")
    public Map<String, Object> deleteDocuments(@RequestBody DeleteDocumentsRequest request) {
        if (request.getDocumentIds() == null || request.getDocumentIds().isEmpty()) {
            throw new IllegalArgumentException("文档ID列表不能为空");
        }

        boolean success = redisVectorService.deleteDocuments(request.getDocumentIds());

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "文档删除成功" : "文档删除失败");

        return response;
    }

    /**
     * 清空所有文档
     *
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public Map<String, Object> clearAll() {
        redisVectorService.clearAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已清空所有文档");

        return response;
    }

    /**
     * 加载示例数据
     *
     * @return 加载结果
     */
    @PostMapping("/load-sample-data")
    public Map<String, Object> loadSampleData() {
        List<Map<String, Object>> sampleDocs = List.of(
                createDoc("Spring AI 是一个用于构建 AI 应用程序的 Java 框架，它提供了与各种 AI 模型和服务集成的能力。",
                        Map.of("category", "技术", "source", "Spring AI 官方文档")),
                createDoc("Redis Stack 是 Redis 的扩展版本，包含了向量搜索、JSON、图数据库等高级功能。",
                        Map.of("category", "数据库", "source", "Redis 官方文档")),
                createDoc("RAG（Retrieval-Augmented Generation）是一种结合信息检索和文本生成的 AI 技术，可以提高大语言模型的准确性和可靠性。",
                        Map.of("category", "AI技术", "source", "AI 研究论文")),
                createDoc("向量数据库专门用于存储和检索高维向量数据，支持相似度搜索，是 RAG 系统的核心组件之一。",
                        Map.of("category", "数据库", "source", "技术博客")),
                createDoc("DashScope 是阿里云提供的大语言模型服务平台，支持通义千问等多种 AI 模型。",
                        Map.of("category", "云服务", "source", "阿里云文档")),
                createDoc("Embedding 是将文本转换为数值向量的过程，使得计算机可以计算文本之间的语义相似度。",
                        Map.of("category", "AI技术", "source", "机器学习教程")),
                createDoc("Milvus 是一个开源的向量数据库，专为 AI 应用设计，支持大规模向量相似度搜索。",
                        Map.of("category", "数据库", "source", "Milvus 官方文档")),
                createDoc("Java 21 引入了虚拟线程、模式匹配等新特性，大幅提升了开发效率和运行时性能。",
                        Map.of("category", "编程语言", "source", "Oracle 官方文档"))
        );

        int count = redisVectorService.addDocuments(sampleDocs);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        response.put("message", "成功加载 " + count + " 条示例数据");

        return response;
    }

    /**
     * 辅助方法：创建文档
     */
    private Map<String, Object> createDoc(String content, Map<String, Object> metadata) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("content", content);
        doc.put("metadata", metadata);
        return doc;
    }
}
