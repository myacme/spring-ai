# 快递查询Agent开发步骤

## 项目概述
开发一个基于AI的快递查询Agent，能够：
1. 从用户输入中识别手机号码
2. 调用快递查询API获取该手机号相关的快递信息
3. 返回结构化的快递信息给用户

## 技术栈
- Spring Boot 3.3.4
- Java 21
- Spring AI (集成大模型能力)
- Spring WebFlux (响应式编程)
- Jackson (JSON处理)
- Lombok (代码简化)
- DeepSeek/Qwen等大模型API

## 开发步骤

### 1. 项目结构设计

```
express/
├── src/main/java/com/ljx/express/
│   ├── controller/
│   │   └── ExpressController.java
│   ├── service/
│   │   ├── PhoneExtractorService.java
│   │   ├── ExpressQueryService.java
│   │   └── ExpressAgentService.java
│   ├── model/
│   │   ├── ExpressRequest.java
│   │   ├── ExpressResponse.java
│   │   └── TrackingInfo.java
│   └── config/
│       └── WebClientConfig.java
└── src/main/resources/
    └── application.yml
```

### 2. 数据模型定义

#### 2.1 快递查询请求模型
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressRequest {
    private String phoneNumber;
    private String message;
}
```

#### 2.2 快递查询响应模型
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressResponse {
    private boolean success;
    private String message;
    private String errorMessage;
    private List<TrackingInfo> trackingInfos;
    private String extractedPhone;
    private String aiSummary;  // AI生成的智能摘要
    private QueryIntent queryIntent;  // 用户查询意图
    private String aiSuggestion;  // AI建议
    private long processingTime;  // 处理耗时
}

// 查询意图模型
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryIntent {
    private String mainPurpose;  // 主要目的
    private String urgency;      // 紧急程度
    private String specificRequirements;  // 特殊要求
}

// 智能查询结果模型
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressQueryResult {
    private List<TrackingInfo> trackingInfos;
    private String aiSummary;
    private QueryIntent queryIntent;
}
```

#### 2.3 快递跟踪信息模型
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingInfo {
    private String trackingNumber;
    private String carrier;
    private String status;
    private String updateTime;
    private List<TrackingEvent> events;
}
```

### 3. 核心服务实现

#### 3.1 手机号提取服务 (PhoneExtractorService)
```java
@Service
@Slf4j
public class PhoneExtractorService {
    
    private final OpenAiClient openAiClient;
    
    public PhoneExtractorService(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }
    
    // 使用大模型智能提取手机号
    public String extractPhoneNumberWithAI(String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }
        
        try {
            // 构造AI提示词
            String prompt = String.format("""
                请从以下文本中提取手机号码：
                
                输入文本：%s
                
                要求：
                1. 只返回手机号码，不要其他内容
                2. 如果没有找到手机号码，返回"未找到"
                3. 手机号码应该是11位数字，以1开头
                """, input);
            
            ChatResponse response = openAiClient.chatCompletion(
                ChatCompletionRequest.builder()
                    .model("deepseek-chat") // 或其他模型
                    .messages(List.of(
                        ChatCompletionMessage.ofSystem("你是一个专业的信息提取助手"),
                        ChatCompletionMessage.ofUser(prompt)
                    ))
                    .temperature(0.3)
                    .build()
            );
            
            String result = response.getResult().getOutput().getContent().trim();
            
            // 验证提取结果是否为有效手机号
            if (!"未找到".equals(result) && isValidPhoneNumber(result)) {
                log.info("AI提取到手机号: {}", result);
                return result;
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("AI手机号提取失败: {}", e.getMessage(), e);
            // 失败时回退到正则表达式
            return extractPhoneNumberFallback(input);
        }
    }
    
    // 正则表达式备选方案
    private String extractPhoneNumberFallback(String input) {
        Pattern PHONE_PATTERN = Pattern.compile("(?<!\d)(1[3-9]\d{9})(?!\d)");
        Matcher matcher = PHONE_PATTERN.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("1[3-9]\d{9}");
    }
}
```

#### 3.2 快递查询服务 (ExpressQueryService)
```java
@Service
@Slf4j
public class ExpressQueryService {
    
    private final WebClient webClient;
    private final OpenAiClient openAiClient;
    
    public ExpressQueryService(WebClient.Builder webClientBuilder, 
                              OpenAiClient openAiClient) {
        this.webClient = webClientBuilder.build();
        this.openAiClient = openAiClient;
    }
    
    // 智能快递查询 - 结合大模型理解用户意图
    public ExpressQueryResult queryExpressInfoIntelligent(String userInput, String phoneNumber) {
        try {
            // 步骤1: 使用大模型理解用户查询意图
            QueryIntent intent = analyzeQueryIntent(userInput);
            
            // 步骤2: 调用快递API查询基础信息
            List<TrackingInfo> basicInfo = queryExpressInfo(phoneNumber);
            
            // 步骤3: 使用大模型对查询结果进行智能分析和总结
            String aiSummary = generateAISummary(basicInfo, intent);
            
            return ExpressQueryResult.builder()
                .trackingInfos(basicInfo)
                .aiSummary(aiSummary)
                .queryIntent(intent)
                .build();
                
        } catch (Exception e) {
            log.error("智能快递查询失败: {}", e.getMessage(), e);
            throw new RuntimeException("快递查询服务暂时不可用");
        }
    }
    
    // 基础快递查询API调用
    public List<TrackingInfo> queryExpressInfo(String phoneNumber) {
        try {
            String apiUrl = "https://api.express.com/track";
            
            return webClient.post()
                .uri(apiUrl)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of("phone", phoneNumber))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TrackingInfo>>() {})
                .block();
                
        } catch (Exception e) {
            log.error("快递API调用失败: {}", e.getMessage(), e);
            throw new RuntimeException("快递查询API调用失败");
        }
    }
    
    // 使用大模型分析查询意图
    private QueryIntent analyzeQueryIntent(String userInput) {
        String prompt = String.format("""
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
            ChatResponse response = openAiClient.chatCompletion(
                ChatCompletionRequest.builder()
                    .model("deepseek-chat")
                    .messages(List.of(
                        ChatCompletionMessage.ofSystem("你是一个快递查询意图分析专家"),
                        ChatCompletionMessage.ofUser(prompt)
                    ))
                    .responseFormat(ChatCompletionResponseFormat.JSON)
                    .temperature(0.3)
                    .build()
            );
            
            String jsonResult = response.getResult().getOutput().getContent();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResult, QueryIntent.class);
            
        } catch (Exception e) {
            log.error("意图分析失败: {}", e.getMessage(), e);
            return QueryIntent.builder()
                .mainPurpose("未知")
                .urgency("一般")
                .build();
        }
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
            ChatResponse response = openAiClient.chatCompletion(
                ChatCompletionRequest.builder()
                    .model("deepseek-chat")
                    .messages(List.of(
                        ChatCompletionMessage.ofSystem("你是一个专业的快递客服助手"),
                        ChatCompletionMessage.ofUser(prompt)
                    ))
                    .temperature(0.7)
                    .build()
            );
            
            return response.getResult().getOutput().getContent();
            
        } catch (Exception e) {
            log.error("AI摘要生成失败: {}", e.getMessage(), e);
            return "查询到" + trackingInfos.size() + "条快递记录";
        }
    }
}
```

#### 3.3 Agent协调服务 (ExpressAgentService)
```java
@Service
@Slf4j
public class ExpressAgentService {
    
    private final PhoneExtractorService phoneExtractorService;
    private final ExpressQueryService expressQueryService;
    
    public ExpressAgentService(PhoneExtractorService phoneExtractorService,
                              ExpressQueryService expressQueryService) {
        this.phoneExtractorService = phoneExtractorService;
        this.expressQueryService = expressQueryService;
    }
    
    // 智能快递查询主流程
    public ExpressResponse processExpressQuery(String userInput) {
        try {
            log.info("开始处理快递查询请求: {}", userInput);
            
            // 步骤1: 使用大模型提取手机号
            String phoneNumber = phoneExtractorService.extractPhoneNumberWithAI(userInput);
            
            if (phoneNumber == null) {
                return ExpressResponse.builder()
                    .success(false)
                    .message("抱歉，我没有在您的输入中找到有效的手机号码。请提供11位手机号码以便查询快递信息。")
                    .aiSuggestion("您可以这样询问：'帮我查一下13812345678的快递状态'")
                    .build();
            }
            
            // 步骤2: 使用大模型增强的快递查询
            ExpressQueryResult queryResult = expressQueryService
                .queryExpressInfoIntelligent(userInput, phoneNumber);
            
            // 步骤3: 构造智能响应
            return ExpressResponse.builder()
                .success(true)
                .message("快递信息查询成功")
                .extractedPhone(phoneNumber)
                .trackingInfos(queryResult.getTrackingInfos())
                .aiSummary(queryResult.getAiSummary())
                .queryIntent(queryResult.getQueryIntent())
                .build();
                
        } catch (Exception e) {
            log.error("处理快递查询请求失败: {}", e.getMessage(), e);
            return ExpressResponse.builder()
                .success(false)
                .message("处理请求时发生错误，请稍后重试")
                .errorMessage(e.getMessage())
                .build();
        }
    }
    
    // 批量查询服务
    public List<ExpressResponse> batchProcessQueries(List<String> userInputs) {
        return userInputs.parallelStream()
            .map(this::processExpressQuery)
            .collect(Collectors.toList());
    }
}
```

### 4. 控制器实现

```java
@RestController
@RequestMapping("/api/express")
@Slf4j
public class ExpressController {
    
    private final ExpressAgentService expressAgentService;
    
    public ExpressController(ExpressAgentService expressAgentService) {
        this.expressAgentService = expressAgentService;
    }
    
    @PostMapping("/query")
    public ResponseEntity<ExpressResponse> queryExpress(@RequestBody ExpressRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("收到快递查询请求: {}", request.getMessage());
        
        ExpressResponse response = expressAgentService.processExpressQuery(request.getMessage());
        
        // 记录处理时间
        response.setProcessingTime(System.currentTimeMillis() - startTime);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/batch-query")
    public ResponseEntity<List<ExpressResponse>> batchQueryExpress(
            @RequestBody List<ExpressRequest> requests) {
        List<String> messages = requests.stream()
            .map(ExpressRequest::getMessage)
            .collect(Collectors.toList());
            
        List<ExpressResponse> responses = expressAgentService.batchProcessQueries(messages);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthInfo = Map.of(
            "status", "UP",
            "service", "Express Agent",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(healthInfo);
    }
}
```

### 5. 配置文件

#### application.yml
```yaml
server:
  port: 8081

spring:
  application:
    name: express-agent
    
logging:
  level:
    com.ljx.express: DEBUG
    
express:
  api:
    url: https://api.express.com
    timeout: 5000
```

#### WebClient配置
```java
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(1024 * 1024))
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create().responseTimeout(Duration.ofSeconds(10))));
    }
}
```

### 6. 启动类

```java
@SpringBootApplication
@ComponentScan(basePackages = "com.ljx.express")
public class ExpressAgentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ExpressAgentApplication.class, args);
    }
}
```

### 7. 测试用例

#### 7.1 单元测试
```java
@SpringBootTest
class ExpressAgentServiceTest {
    
    @Autowired
    private ExpressAgentService expressAgentService;
    
    @Test
    void testProcessExpressQuery() {
        // 测试正常情况
        String input = "帮我查一下13812345678这个号码的快递";
        ExpressResponse response = expressAgentService.processExpressQuery(input);
        
        assertTrue(response.isSuccess());
        assertEquals("13812345678", response.getExtractedPhone());
    }
    
    @Test
    void testInvalidPhone() {
        // 测试无效手机号
        String input = "帮我查快递";
        ExpressResponse response = expressAgentService.processExpressQuery(input);
        
        assertFalse(response.isSuccess());
        assertEquals("未在输入中找到有效的手机号码", response.getMessage());
    }
}
```

### 8. API集成注意事项

#### 8.1 快递API选择
- 可用的快递查询API提供商：
  - 快递100
  - 聚合数据
  - 阿里云市场快递API
  - 自建快递查询服务

#### 8.2 API调用优化
```java
// 添加重试机制
public List<TrackingInfo> queryExpressInfoWithRetry(String phoneNumber) {
    return Retry.of("express-query", RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1))
            .retryExceptions(WebClientResponseException.class)
            .build())
        .executeCallable(() -> queryExpressInfo(phoneNumber));
}
```

### 9. 部署和监控

#### 9.1 Docker部署
```dockerfile
FROM openjdk:21-jre-slim
COPY target/express-agent.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### 9.2 健康检查端点
```java
@Component
public class HealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // 检查依赖服务状态
        return Health.up()
            .withDetail("express-api", "available")
            .build();
    }
}
```

### 10. 扩展功能

#### 10.1 AI增强功能
- 集成自然语言理解，支持更复杂的查询语句
- 添加多轮对话支持
- 实现智能纠错和建议功能

#### 10.2 性能优化
- 添加缓存机制（Redis）
- 实现异步处理
- 添加限流和熔断机制

#### 10.3 安全增强
- 添加API密钥验证
- 实现请求签名
- 添加访问日志和审计

## 开发时间估算
- 基础功能开发：2-3天
- 测试和完善：1-2天
- 部署和文档：0.5-1天
- 总计：3.5-6天

## 注意事项
1. 确保快递API的稳定性和准确性
2. 处理好手机号隐私保护
3. 做好异常处理和错误恢复
4. 考虑并发访问和性能优化