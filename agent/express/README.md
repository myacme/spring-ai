# 快递查询Agent

基于大模型的智能快递查询系统，使用DeepSeek进行手机号识别和自然语言处理，集成快递100 API进行快递信息查询。

## 功能特性

- ✅ 智能手机号识别（DeepSeek大模型）
- ✅ 快递信息查询（快递100 API）
- ✅ 自然语言意图理解
- ✅ 智能结果摘要生成
- ✅ 批量查询支持
- ✅ 详细的处理日志

## 技术栈

- Spring Boot 3.2.0
- Java 17
- DeepSeek API
- 快递100 API
- Spring WebFlux (响应式编程)
- Lombok

## 项目结构

```
express/
├── src/main/java/com/ljx/express/
│   ├── controller/
│   │   └── ExpressController.java          # REST API控制器
│   ├── service/
│   │   ├── PhoneExtractorService.java      # 手机号提取服务
│   │   ├── Kuaidi100Service.java           # 快递100查询服务
│   │   ├── DeepSeekService.java            # DeepSeek AI服务
│   │   └── ExpressAgentService.java        # Agent协调服务
│   ├── model/
│   │   ├── ExpressRequest.java             # 请求模型
│   │   ├── ExpressResponse.java            # 响应模型
│   │   ├── TrackingInfo.java               # 快递信息模型
│   │   ├── TrackingEvent.java              # 物流轨迹模型
│   │   ├── QueryIntent.java                # 查询意图模型
│   │   └── ExpressQueryResult.java         # 查询结果模型
│   ├── config/
│   │   └── WebClientConfig.java            # WebClient配置
│   └── ExpressAgentApplication.java        # 启动类
└── src/main/resources/
    └── application.yml                     # 配置文件
```

## 配置说明

在 `application.yml` 中配置必要的API密钥：

```yaml
express:
  kuaidi100:
    key: your_kuaidi100_key          # 快递100 API Key
    customer: your_kuaidi100_customer # 快递100 Customer ID
    url: https://poll.kuaidi100.com/poll/query.do

deepseek:
  api-key: your_deepseek_api_key     # DeepSeek API Key
  model: deepseek-chat
  base-url: https://api.deepseek.com
```

## API接口

### 1. 快递查询接口

**POST** `/api/express/query`

请求体：
```json
{
  "message": "帮我查一下13812345678的快递状态"
}
```

响应：
```json
{
  "success": true,
  "message": "快递信息查询成功",
  "extractedPhone": "13812345678",
  "trackingInfos": [...],
  "aiSummary": "您的快递目前正在运输中...",
  "queryIntent": {
    "mainPurpose": "查询快递状态",
    "urgency": "一般",
    "specificRequirements": "无特殊要求"
  },
  "processingTime": 1250
}
```

### 2. 批量查询接口

**POST** `/api/express/batch-query`

### 3. 健康检查接口

**GET** `/api/express/health`

## 运行方式

### 方式1：使用Maven运行
```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

### 方式2：打包后运行
```bash
# 打包
mvn clean package

# 运行
java -jar target/express-agent-1.0.0.jar
```

## 测试示例

启动服务后，可以使用以下命令测试：

```bash
# 健康检查
curl http://localhost:8081/api/express/health

# 快递查询测试
curl -X POST http://localhost:8081/api/express/query \
  -H "Content-Type: application/json" \
  -d '{"message": "帮我查一下13812345678的快递"}'
```

## 开发说明

1. **手机号识别**：使用DeepSeek大模型进行智能识别，支持自然语言输入
2. **意图分析**：分析用户查询的真实意图和紧急程度
3. **快递查询**：调用快递100 API获取物流信息
4. **智能摘要**：使用AI生成友好的自然语言回复
5. **错误处理**：完善的异常处理和备选方案

## 注意事项

- 需要有效的DeepSeek和快递100 API密钥
- 建议在生产环境中配置API密钥的环境变量
- 可以根据实际需求调整AI模型参数和提示词
- 快递100 API的具体参数需要根据官方文档调整