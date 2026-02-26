# 天气查询Agent

基于Spring AI开发的智能天气查询Agent，支持自然语言天气查询和结构化天气查询。

## 功能特性

- 🌤️ **自然语言查询**：支持中文自然语言天气查询
- 🏙️ **多城市支持**：支持北京、上海、广州等主要城市
- 📅 **日期查询**：支持当前天气、历史天气、天气预报
- 🤖 **AI智能解析**：集成DeepSeek AI进行意图识别和参数提取
- 📊 **丰富数据**：提供温度、湿度、风力、空气质量等详细信息
- 🔧 **结构化接口**：提供RESTful API接口

## 技术栈

- Spring Boot 3.3.4
- Spring AI 1.0.3
- Java 21
- Lombok
- Maven

## 快速开始

### 1. 环境准备

确保已安装：
- Java 21
- Maven 3.6+
- DeepSeek API密钥

### 2. 配置API密钥

设置环境变量：
```bash
export DEEPSEEK_API_KEY=your_deepseek_api_key
```

或在application.yml中直接配置：
```yaml
deepseek:
  api-key: your_deepseek_api_key
```

### 3. 启动应用

```bash
# 方法1：使用启动脚本
./start.bat  # Windows
# 或
./start.sh   # Linux/Mac

# 方法2：使用Maven命令
mvn clean compile spring-boot:run
```

应用启动后访问：http://localhost:8082

### 4. 验证启动

```bash
curl http://localhost:8082/weather/health
```

## API接口

### 基础接口

- `GET /weather/health` - 健康检查
- `GET /weather/api-info` - API信息
- `GET /weather/cities` - 支持的城市列表

### 天气查询接口

#### 1. 自然语言查询
```bash
curl -X POST http://localhost:8082/weather/query \
  -H "Content-Type: application/json" \
  -d '{"query": "今天北京天气怎么样？"}'
```

#### 2. 结构化查询
```bash
curl -X POST http://localhost:8082/weather/structured \
  -H "Content-Type: application/json" \
  -d '{
    "query": "天气查询",
    "city": "北京",
    "date": "今天",
    "queryType": "CURRENT"
  }'
```

## 支持的查询示例

### 当前天气查询
- "今天北京天气怎么样？"
- "上海现在的温度是多少？"
- "广州天气如何？"

### 天气预报查询
- "明天深圳天气预报"
- "未来三天杭州天气"
- "北京一周天气预报"

### 降水查询
- "后天会下雨吗？"
- "明天上海降水概率"
- "广州未来几天有雨吗？"

### 多城市支持
- 北京、上海、广州、深圳、杭州、南京、成都、武汉、西安、重庆

## 项目结构

```
weather/
├── src/
│   ├── main/
│   │   ├── java/com/ljx/weather/
│   │   │   ├── WeatherAgentApplication.java     # 应用主类
│   │   │   ├── config/
│   │   │   │   └── WebClientConfig.java        # WebClient配置
│   │   │   ├── controller/
│   │   │   │   └── WeatherController.java      # 控制器
│   │   │   ├── model/
│   │   │   │   ├── Location.java               # 位置模型
│   │   │   │   ├── WeatherInfo.java            # 天气信息模型
│   │   │   │   ├── WeatherQuery.java           # 查询请求模型
│   │   │   │   ├── WeatherResponse.java        # 响应模型
│   │   │   │   ├── QueryParameters.java        # 查询参数模型
│   │   │   │   └── WeatherIntent.java          # 意图模型
│   │   │   ├── service/
│   │   │   │   ├── WeatherAgentService.java    # Agent核心服务
│   │   │   │   ├── WeatherService.java         # 天气服务
│   │   │   │   ├── LocationExtractorService.java # 位置提取服务
│   │   │   │   └── DateParserService.java      # 日期解析服务
│   │   │   └── util/
│   │   │       └── WeatherDataGenerator.java   # 模拟数据生成器
│   │   └── resources/
│   │       ├── application.yml                 # 配置文件
│   │       └── logback.xml                     # 日志配置
│   └── test/
│       └── java/com/ljx/weather/               # 测试代码
├── pom.xml                                     # Maven配置
├── start.bat                                   # 启动脚本
├── 天气查询Agent开发步骤.md                     # 开发文档
└── 测试用例.md                                 # 测试文档
```

## 配置说明

### application.yml主要配置项

```yaml
server:
  port: 8082  # 服务端口

weather:
  mock-data: true        # 使用模拟数据
  default-city: 北京     # 默认城市

deepseek:
  api-key: your_api_key  # DeepSeek API密钥
  model: deepseek-chat   # 模型名称
  temperature: 0.7       # 温度参数
```

## 测试

### 运行单元测试
```bash
mvn test
```

### API测试
参考[测试用例.md](测试用例.md)文件中的详细测试用例。

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

### Docker部署（可选）
```dockerfile
FROM openjdk:21-jre-slim
COPY target/weather-1.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 后续扩展

- [ ] 集成真实天气API（和风天气、高德天气等）
- [ ] 添加更多查询维度（紫外线、空气质量详细信息等）
- [ ] 支持多语言查询
- [ ] 添加缓存机制提升性能
- [ ] 实现用户个性化设置
- [ ] 添加Web前端界面

## 常见问题

### 1. API密钥配置问题
确保已正确配置DeepSeek API密钥，可以通过环境变量或配置文件设置。

### 2. 端口冲突
如果8082端口被占用，可以在application.yml中修改server.port配置。

### 3. 网络连接问题
AI服务调用需要网络连接，请确保网络环境正常。

## 贡献

欢迎提交Issue和Pull Request来改进项目。

## 许可证

MIT License