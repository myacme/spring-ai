package com.ljx.alibaba.service;

import com.alibaba.cloud.ai.transformer.splitter.SentenceSplitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 文本分词服务 - 演示 Spring AI Alibaba 的两种分词器
 */
@Service
@Slf4j
public class TextSplitterService {
    
    /**
     * 使用 TokenTextSplitter 进行分词（基于 Token）
     * 适用于所有语言，但对中文可能不够精确
     */
    public List<Document> splitByToken(String text) {
        log.info("使用 TokenTextSplitter 进行分词，原文长度: {}", text.length());
        
        // 参数说明：
        // - 500: 每个块的目标 Token 数
        // - 200: 最小字符数
        // - 5:   最小嵌入长度
        // - 1000: 最大分块数
        // - false: 不保留分隔符（中文场景建议false）
        TokenTextSplitter splitter = new TokenTextSplitter(
                500,
                200,
                5,
                1000,
                false);
        
        Document document = new Document(text);
        List<Document> chunks = splitter.apply(Arrays.asList(document));
        
        log.info("分词完成，共 {} 个块", chunks.size());
        return chunks;
    }
    
    /**
     * 使用 SentenceSplitter 进行分词（基于句子）
     * 更适合中文，能识别句子边界，保持语义完整性
     */
    public List<Document> splitBySentence(String text) {
        log.info("使用 SentenceSplitter 进行分词，原文长度: {}", text.length());
        
        // 参数：最大 Token 数为 300
        SentenceSplitter splitter = new SentenceSplitter(300);
        
        Document document = new Document(text);
        List<Document> chunks = splitter.split(Arrays.asList(document));
        
        log.info("分词完成，共 {} 个块", chunks.size());
        return chunks;
    }
    
    /**
     * 按段落分词（基于换行符分隔）
     * 适合结构化文档，保持段落完整性
     */
    public List<Document> splitByParagraph(String text) {
        log.info("使用 ParagraphSplitter 进行分词，原文长度: {}", text.length());
        
        // 按换行符分割文本为段落
        String[] paragraphs = text.split("\\n\\s*\\n");
        
        List<Document> chunks = Arrays.stream(paragraphs)
                .filter(p -> p != null && !p.trim().isEmpty())
                .map(paragraph -> {
                    Document doc = new Document(paragraph.trim());
                    return doc;
                })
                .collect(java.util.stream.Collectors.toList());
        
        log.info("分词完成，共 {} 个块", chunks.size());
        return chunks;
    }
    
    /**
     * 按固定字数分词
     * 简单粗暴的字符数切分，适合对语义要求不高的场景
     * 
     * @param text 待分词文本
     * @param chunkSize 每块的字符数，默认500
     */
    public List<Document> splitByFixedLength(String text, Integer chunkSize) {
        int size = (chunkSize != null && chunkSize > 0) ? chunkSize : 500;
        log.info("使用 FixedLengthSplitter 进行分词，原文长度: {}, 每块字数: {}", text.length(), size);
        
        List<Document> chunks = new java.util.ArrayList<>();
        int totalLength = text.length();
        
        for (int i = 0; i < totalLength; i += size) {
            int endIndex = Math.min(i + size, totalLength);
            String chunk = text.substring(i, endIndex);
            
            if (!chunk.trim().isEmpty()) {
                Document doc = new Document(chunk);
                chunks.add(doc);
            }
        }
        
        log.info("分词完成，共 {} 个块", chunks.size());
        return chunks;
    }
}
