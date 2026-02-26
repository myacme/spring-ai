package com.ljx.express.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljx.express.model.QueryIntent;
import com.ljx.express.model.TrackingEvent;
import com.ljx.express.model.TrackingInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.MessageDigest;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class Kuaidi100Service {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final DeepSeekService deepSeekService;
    
    @Value("${express.kuaidi100.key}")
    private String key;
    
    @Value("${express.kuaidi100.customer}")
    private String customer;
    
    @Value("${express.kuaidi100.url}")
    private String apiUrl;
    
    public Kuaidi100Service(WebClient.Builder webClientBuilder, ObjectMapper objectMapper, DeepSeekService deepSeekService) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.deepSeekService = deepSeekService;
    }
    
    // 智能快递查询 - 结合大模型理解用户意图
    public Map<String, Object> queryExpressInfoIntelligent(String userInput, String phoneNumber) {
        try {
            // 步骤1: 使用大模型理解用户查询意图
            QueryIntent intent = deepSeekService.analyzeQueryIntent(userInput);
            
            // 步骤2: 调用快递100 API查询基础信息
            List<TrackingInfo> basicInfo = queryExpressInfo(phoneNumber);
            
            // 步骤3: 使用大模型对查询结果进行智能分析和总结
            String aiSummary = generateAISummary(basicInfo, intent);
            
            Map<String, Object> result = new HashMap<>();
            result.put("trackingInfos", basicInfo);
            result.put("aiSummary", aiSummary);
            result.put("queryIntent", intent);
            
            return result;
            
        } catch (Exception e) {
            log.error("智能快递查询失败: {}", e.getMessage(), e);
            throw new RuntimeException("快递查询服务暂时不可用");
        }
    }
    
    // 基础快递查询API调用 - 快递100
    public List<TrackingInfo> queryExpressInfo(String phoneNumber) {
        try {
            // 快递100查询需要运单号，这里模拟通过手机号关联查询
            // 实际应用中需要有手机号与运单号的映射关系
            log.info("调用快递100 API查询手机号: {}", phoneNumber);
            
            // 生成签名
            String sign = generateSign(phoneNumber, key, customer);
            
            Map<String, Object> requestBody = Map.of(
                "customer", customer,
                "param", Map.of(
                    "phone", phoneNumber,
                    "resultv2", "1"
                ),
                "sign", sign
            );
            
            // 注意：快递100的实际API参数和返回格式需要根据官方文档调整
            Map<String, Object> response = webClient.post()
                .uri(apiUrl)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            // 解析响应并转换为TrackingInfo对象
            return parseKuaidi100Response(response);
            
        } catch (Exception e) {
            log.error("快递100 API调用失败: {}", e.getMessage(), e);
            // 返回模拟数据用于测试
            return createMockTrackingInfo();
        }
    }
    
    private String generateSign(String phoneNumber, String key, String customer) {
        try {
            String param = "{\"phone\":\"" + phoneNumber + "\",\"resultv2\":\"1\"}";
            String input = param + key + customer;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("签名生成失败", e);
        }
    }
    
    private List<TrackingInfo> parseKuaidi100Response(Map<String, Object> response) {
        // 根据快递100实际API响应格式解析
        // 这里需要根据实际API文档调整
        List<TrackingInfo> trackingInfos = new ArrayList<>();
        
        if (response != null && "200".equals(response.get("status"))) {
            // 解析物流信息
            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
            if (data != null) {
                for (Map<String, Object> item : data) {
                    TrackingInfo info = TrackingInfo.builder()
                        .trackingNumber((String) item.get("nu"))
                        .carrier((String) item.get("com"))
                        .status((String) item.get("state"))
                        .updateTime((String) item.get("time"))
                        .build();
                    
                    // 解析物流轨迹
                    List<Map<String, Object>> traces = (List<Map<String, Object>>) item.get("traces");
                    if (traces != null) {
                        List<TrackingEvent> events = new ArrayList<>();
                        for (Map<String, Object> trace : traces) {
                            TrackingEvent event = TrackingEvent.builder()
                                .time((String) trace.get("time"))
                                .context((String) trace.get("context"))
                                .location((String) trace.get("location"))
                                .build();
                            events.add(event);
                        }
                        info.setEvents(events);
                    }
                    
                    trackingInfos.add(info);
                }
            }
        }
        
        return trackingInfos;
    }
    
    // 使用大模型生成智能摘要
    private String generateAISummary(List<TrackingInfo> trackingInfos, QueryIntent intent) {
        StringBuilder infoSummary = new StringBuilder();
        for (TrackingInfo info : trackingInfos) {
            infoSummary.append(String.format("运单号：%s，快递公司：%s，状态：%s\n", 
                info.getTrackingNumber(), info.getCarrier(), info.getStatus()));
        }
        
        String prompt = String.format("""
            根据以下快递信息和用户查询意图，生成友好的回复：
            
            快递信息：
            %s
            
            用户意图：
            主要目的：%s
            紧急程度：%s
            特殊要求：%s
            
            要求：
            1. 用自然语言总结快递状态
            2. 根据紧急程度调整语调
            3. 如果有异常状态，重点提醒
            4. 保持简洁明了
            """, infoSummary, intent.getMainPurpose(), 
            intent.getUrgency(), intent.getSpecificRequirements());
            
        try {
            return deepSeekService.chatCompletionWithSystem(
                "你是一个专业的快递客服助手", prompt);
            
        } catch (Exception e) {
            log.error("AI摘要生成失败: {}", e.getMessage(), e);
            return "查询到" + trackingInfos.size() + "条快递记录";
        }
    }
    
    // 创建模拟数据用于测试
    private List<TrackingInfo> createMockTrackingInfo() {
        List<TrackingEvent> events = Arrays.asList(
            TrackingEvent.builder()
                .time("2024-01-15 10:30:00")
                .context("快递已揽收")
                .location("北京朝阳区")
                .build(),
            TrackingEvent.builder()
                .time("2024-01-15 15:20:00")
                .context("快递已到达北京分拨中心")
                .location("北京")
                .build(),
            TrackingEvent.builder()
                .time("2024-01-16 08:45:00")
                .context("快递已从北京发出，前往上海")
                .location("北京")
                .build()
        );
        
        TrackingInfo info = TrackingInfo.builder()
            .trackingNumber("SF1234567890")
            .carrier("顺丰速运")
            .status("运输中")
            .updateTime("2024-01-16 08:45:00")
            .events(events)
            .build();
            
        return Arrays.asList(info);
    }
}