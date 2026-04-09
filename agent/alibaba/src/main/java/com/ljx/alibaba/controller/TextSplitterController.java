package com.ljx.alibaba.controller;

import com.ljx.alibaba.entity.SplitRequest;
import com.ljx.alibaba.service.TextSplitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文本分词控制器 - 演示 Spring AI Alibaba 的文档分块功能
 */
@RestController
@RequestMapping("/api/splitter")
@RequiredArgsConstructor
public class TextSplitterController {
    
    private final TextSplitterService textSplitterService;
    
    /**
     * 使用 TokenTextSplitter 进行分词
     */
    @PostMapping("/token")
    public Map<String, Object> splitByToken(@RequestBody SplitRequest request) {
        List<Document> chunks = textSplitterService.splitByToken(request.getText());
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalLength", request.getText().length());
        result.put("chunkCount", chunks.size());
        result.put("chunks", chunks.stream()
                .map(chunk -> {
                    Map<String, Object> chunkMap = new HashMap<>();
                    chunkMap.put("content", chunk.getText());
                    chunkMap.put("length", chunk.getText().length());
                    chunkMap.put("metadata", chunk.getMetadata());
                    return chunkMap;
                })
                .collect(Collectors.toList()));
        
        return result;
    }
    
    /**
     * 使用 SentenceSplitter 进行分词（更适合中文）
     */
    @PostMapping("/sentence")
    public Map<String, Object> splitBySentence(@RequestBody SplitRequest request) {
        List<Document> chunks = textSplitterService.splitBySentence(request.getText());
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalLength", request.getText().length());
        result.put("chunkCount", chunks.size());
        result.put("chunks", chunks.stream()
                .map(chunk -> {
                    Map<String, Object> chunkMap = new HashMap<>();
                    chunkMap.put("content", chunk.getText());
                    chunkMap.put("length", chunk.getText().length());
                    chunkMap.put("metadata", chunk.getMetadata());
                    return chunkMap;
                })
                .collect(Collectors.toList()));
        
        return result;
    }
    
    /**
     * 按段落分词（基于换行符分隔）
     * 适合结构化文档，保持段落完整性
     */
    @PostMapping("/paragraph")
    public Map<String, Object> splitByParagraph(@RequestBody SplitRequest request) {
        List<Document> chunks = textSplitterService.splitByParagraph(request.getText());
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalLength", request.getText().length());
        result.put("chunkCount", chunks.size());
        result.put("chunks", chunks.stream()
                .map(chunk -> {
                    Map<String, Object> chunkMap = new HashMap<>();
                    chunkMap.put("content", chunk.getText());
                    chunkMap.put("length", chunk.getText().length());
                    chunkMap.put("metadata", chunk.getMetadata());
                    return chunkMap;
                })
                .collect(Collectors.toList()));
        
        return result;
    }
    
    /**
     * 按固定字数分词
     * 简单粗暴的字符数切分，可通过 chunkSize 参数指定每块字数（默认500）
     */
    @PostMapping("/fixed-length")
    public Map<String, Object> splitByFixedLength(@RequestBody SplitRequest request) {
        List<Document> chunks = textSplitterService.splitByFixedLength(
                request.getText(), 
                request.getChunkSize()
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalLength", request.getText().length());
        result.put("chunkSize", request.getChunkSize() != null ? request.getChunkSize() : 500);
        result.put("chunkCount", chunks.size());
        result.put("chunks", chunks.stream()
                .map(chunk -> {
                    Map<String, Object> chunkMap = new HashMap<>();
                    chunkMap.put("content", chunk.getText());
                    chunkMap.put("length", chunk.getText().length());
                    chunkMap.put("metadata", chunk.getMetadata());
                    return chunkMap;
                })
                .collect(Collectors.toList()));
        
        return result;
    }
}
