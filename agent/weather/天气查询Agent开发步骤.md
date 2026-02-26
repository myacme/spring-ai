# 天气查询Agent开发步骤

## 项目概述
开发一个基于Spring AI的天气查询Agent，支持自然语言查询天气信息，初期使用模拟数据进行开发和测试。

## 技术栈
- Spring Boot 3.3.4
- Spring AI 1.0.3
- Java 21
- Lombok
- Maven

## 开发步骤

### 第一阶段：环境配置和项目结构搭建

1. **完善pom.xml配置**
   - 添加Spring AI依赖
   - 添加Spring Web依赖
   - 添加Lombok依赖
   - 添加测试依赖

2. **创建项目基础结构**
   ```
   src/main/java/com/ljx/weather/
   ├── WeatherAgentApplication.java
   ├── config/
   ├── controller/
   ├── model/
   ├── service/
   └── util/
   src/main/resources/
   └── application.yml
   ```

### 第二阶段：数据模型设计

1. **创建天气数据模型**
   - WeatherInfo: 天气信息实体
   - WeatherQuery: 查询请求实体
   - WeatherResponse: 查询响应实体
   - Location: 地理位置信息

2. **创建AI相关模型**
   - WeatherIntent: 天气查询意图识别
   - QueryParameters: 查询参数解析

### 第三阶段：核心服务开发

1. **天气服务实现**
   - WeatherService: 天气数据查询服务（模拟数据）
   - 支持按城市、日期查询
   - 支持未来几天天气预报

2. **AI服务实现**
   - WeatherAgentService: 天气Agent核心服务
   - 集成Spring AI进行自然语言处理
   - 意图识别和参数提取

3. **工具服务**
   - LocationExtractorService: 地理位置提取
   - DateParserService: 日期解析服务

### 第四阶段：API接口开发

1. **控制器开发**
   - WeatherController: REST API接口
   - 支持POST /weather/query接口
   - 支持流式响应

2. **配置类**
   - WebClientConfig: HTTP客户端配置
   - AI配置类

### 第五阶段：测试和验证

1. **单元测试**
   - 服务层测试
   - 控制器测试

2. **集成测试**
   - API接口测试
   - 模拟数据验证

3. **功能测试**
   - 自然语言查询测试
   - 各种查询场景测试

## 项目结构说明

```
weather/
├── src/
│   ├── main/
│   │   ├── java/com/ljx/weather/
│   │   │   ├── WeatherAgentApplication.java
│   │   │   ├── config/
│   │   │   │   └── WebClientConfig.java
│   │   │   ├── controller/
│   │   │   │   └── WeatherController.java
│   │   │   ├── model/
│   │   │   │   ├── Location.java
│   │   │   │   ├── WeatherInfo.java
│   │   │   │   ├── WeatherQuery.java
│   │   │   │   ├── WeatherResponse.java
│   │   │   │   ├── WeatherIntent.java
│   │   │   │   └── QueryParameters.java
│   │   │   ├── service/
│   │   │   │   ├── WeatherAgentService.java
│   │   │   │   ├── WeatherService.java
│   │   │   │   ├── LocationExtractorService.java
│   │   │   │   └── DateParserService.java
│   │   │   └── util/
│   │   │       └── WeatherDataGenerator.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/ljx/weather/
└── pom.xml
```

## 配置文件说明

### application.yml
```yaml
server:
  port: 8082

spring:
  application:
    name: weather-agent

logging:
  level:
    com.ljx.weather: DEBUG

weather:
  # 模拟数据配置
  mock-data: true
  default-city: 北京

deepseek:
  api-key: ${DEEPSEEK_API_KEY:your_deepseek_api_key}
  model: deepseek-chat
  base-url: https://api.deepseek.com
```

## 测试用例

### 自然语言查询示例
1. "今天北京天气怎么样？"
2. "明天上海的温度是多少？"
3. "后天广州会下雨吗？"
4. "未来一周北京天气预报"
5. "深圳现在天气如何？"

### API调用示例
```bash
curl -X POST http://localhost:8082/weather/query \
  -H "Content-Type: application/json" \
  -d '{"query": "今天北京天气怎么样？"}'
```

## 部署说明

1. 确保Java 21环境
2. 配置DeepSeek API密钥
3. 运行命令：`mvn spring-boot:run`
4. 访问测试接口验证功能

## 后续扩展
- 集成真实天气API（如和风天气、高德天气等）
- 添加更多查询维度（空气质量、紫外线等）
- 支持多语言查询
- 添加缓存机制提升性能