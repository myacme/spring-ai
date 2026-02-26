# 天气查询Agent

基于Spring AI开发的智能天气查询Agent，支持自然语言天气查询和AI驱动的天气问答。

## 功能特性

- 🌤️ **AI智能问答**：基于智谱AI的自然语言天气对话
- 🏙️ **多城市支持**：支持北京、上海、广州、深圳、成都等主要城市
- 🤖 **工具集成**：通过@Tool注解集成天气查询功能
- 🔧 **简洁API**：提供简单的RESTful接口
- 📊 **实时数据**：获取城市实时天气信息

## 技术栈

- Spring Boot 3.3.4
- Spring AI 1.0.0-M6
- Java 21
- Lombok
- Maven

## 快速开始

### 1. 环境准备

确保已安装：
- Java 21
- Maven 3.6+
- 智谱AI API密钥（已内置默认密钥）

### 2. 启动应用

```bash
# 方法1：使用启动脚本
start.bat  # Windows
# 或
./start.sh   # Linux/Mac

# 方法2：使用Maven命令
mvn clean compile spring-boot:run
```

应用启动后访问：http://localhost:8082

### 3. 验证启动

```bash
curl http://localhost:8082/ask?question=你好
```

## API接口

### 核心接口

- `GET /ask` - 通用AI问答接口
- `GET /weather` - 天气查询专用接口

### 接口示例

#### 1. 通用问答
```bash
curl "http://localhost:8082/ask?question=今天天气怎么样？"
```

#### 2. 天气查询
```bash
curl "http://localhost:8082/weather?city=北京"
```

## 支持的查询示例

### 天气相关查询
- "今天北京天气怎么样？"
- "上海现在的温度是多少？"
- "广州天气如何？适合出门吗？"
- "深圳今天会下雨吗？"
- "成都天气预报"

### 通用AI问答
- "你能帮我查询天气吗？"
- "告诉我北京的天气情况"
- "上海今天的气温"

## 项目结构

```
weather/
├── src/main/java/com/ljx/weather/
│   ├── WeatherAgentApplication.java      # 应用主类
│   ├── config/
│   │   └── ChatClientConfig.java         # AI客户端配置
│   ├── controller/
│   │   └── WeatherAgentController.java   # API控制器
│   ├── model/
│   │   ├── WeatherRequest.java           # 天气请求模型
│   │   └── WeatherResponse.java          # 天气响应模型
│   ├── service/
│   │   └── WeatherToolService.java       # 天气工具服务
│   └── util/
├── src/main/resources/
│   ├── application.yml                   # 配置文件
│   └── logback.xml                       # 日志配置
├── pom.xml                               # Maven配置
├── start.bat                             # Windows启动脚本
├── start.sh                              # Linux/Mac启动脚本
├── README.md                             # 项目说明
├── 开发完成总结.md                        # 开发总结
├── 天气查询Agent开发步骤.md                # 开发步骤
└── 测试用例.md                           # 测试文档
```

## 配置说明

### application.yml配置

```yaml
server:
  port: 8082

spring:
  application:
    name: weather-agent
  ai:
    openai:
      api-key: ${ZHIPUAI_API_KEY:your_api_key}  # 智谱AI密钥
      base-url: https://open.bigmodel.cn/api/paas/v4/
      chat:
        completions-path: /chat/completions
        options:
          model: glm-4.7-flash
          temperature: 0.7
          max-tokens: 4096
          top-p: 0.9

logging:
  level:
    com.ljx.weather: DEBUG
    org.springframework.ai: DEBUG
```

## 测试

### API测试示例

```bash
# 测试通用问答
curl "http://localhost:8082/ask?question=北京天气如何？"

# 测试天气查询
curl "http://localhost:8082/weather?city=上海"
```

### 支持的城市
- 北京
- 上海
- 广州
- 深圳
- 成都
- 其他城市返回默认数据

## 部署

### 生产环境部署

1. 打包应用：
```bash
mvn clean package
```

2. 运行jar包：
```bash
java -jar target/weather-1.0-SNAPSHOT.jar
```

### 环境变量配置

```bash
export ZHIPUAI_API_KEY=your_zhipuai_api_key
```

## 后续扩展

- [ ] 集成真实天气API
- [ ] 添加更多天气维度信息
- [ ] 支持历史天气查询
- [ ] 添加天气预警功能
- [ ] 实现多轮对话能力
- [ ] 添加Web前端界面

## 常见问题

### 1. API密钥问题
项目已内置测试用的API密钥，生产环境建议配置自己的密钥。

### 2. 端口冲突
如果8082端口被占用，可在application.yml中修改端口配置。

### 3. 网络连接
AI服务调用需要网络连接，请确保网络环境正常。

## 开发文档

- [开发完成总结.md](开发完成总结.md) - 项目开发总结
- [天气查询Agent开发步骤.md](天气查询Agent开发步骤.md) - 详细开发步骤
- [测试用例.md](测试用例.md) - 完整测试用例

## 贡献

欢迎提交Issue和Pull Request来改进项目。

## 许可证

MIT License