package com.ljx.express.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljx.express.model.QueryIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeepSeekService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${deepseek.api-key}")
    private String apiKey;
    
    @Value("${deepseek.model}")
    private String model;
    
    @Value("${deepseek.base-url}")
    private String baseUrl;
    
    public DeepSeekService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }
    
    public String chatCompletion(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
            );
            
            Map<String, Object> response = webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
            
        } catch (Exception e) {
            log.error("DeepSeek API调用失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI服务暂时不可用: " + e.getMessage());
        }
    }
    
    public String chatCompletionWithSystem(String systemPrompt, String userPrompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.3
            );
            
            Map<String, Object> response = webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
            
        } catch (Exception e) {
            log.error("DeepSeek API调用失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI服务暂时不可用: " + e.getMessage());
        }
    }
    
    public QueryIntent analyzeQueryIntent(String userInput) {
        String systemPrompt = "你是一个快递查询意图分析专家";
        String userPrompt = String.format("""
            请分析用户快递查询的意图：
            
            用户输入：%s
            
            请识别以下信息并以JSON格式返回：
            {
                "mainPurpose": "查询快递状态/查询历史记录/其他",
                "urgency": "紧急/一般/不急",
                "specificRequirements": "具体要求说明"
            }
            """, userInput);
            
        try {
            String jsonResult = chatCompletionWithSystem(systemPrompt, userPrompt);
            return objectMapper.readValue(jsonResult, QueryIntent.class);
            
        } catch (Exception e) {
            log.error("意图分析失败: {}", e.getMessage(), e);
            return QueryIntent.builder()
                .mainPurpose("未知")
                .urgency("一般")
                .build();
        }
    }
}