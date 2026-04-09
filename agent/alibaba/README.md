# Spring AI Alibaba + DeepSeek 示例项目

## 项目简介

这是一个演示如何使用 Spring AI Alibaba 集成 DeepSeek 大语言模型的示例项目，包含以下功能：

1. **基础聊天功能** - 普通聊天和流式聊天
2. **文本分词功能** - TokenTextSplitter 和 SentenceSplitter
3. **Function Calling** - 工具调用示例（天气查询）

## 技术栈

- Spring Boot 3.3.4
- Spring AI Alibaba 1.0.0-M6.1
- Java 21
- DeepSeek API
- Maven

## 快速开始

### 1. 配置 API Key

在 `src/main/resources/application.yml` 中配置 DeepSeek API Key：

```yaml
spring:
  ai:
    dashscope:
      api-key: your_deepseek_api_key_here
```

或者设置环境变量：
```bash
set DEEPSEEK_API_KEY=your_api_key
```

### 2. 启动应用

#### Windows:
```bash
start.bat
```

#### 手动启动:
```bash
mvn clean compile spring-boot:run
```

### 3. 测试 API

#### Windows:
```bash
test-api.bat
```

#### 手动测试:

**健康检查:**
```bash
curl http://localhost:8083/api/ai/health
```

**普通聊天:**
```bash
curl -X POST http://localhost:8083/api/ai/chat \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"你好，请介绍一下你自己\"}"
```

**流式聊天:**
```bash
curl -X POST http://localhost:8083/api/ai/chat/stream \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"写一首关于春天的诗\"}"
```

**文本分词 (TokenTextSplitter):**
```bash
curl -X POST http://localhost:8083/api/splitter/token \
  -H "Content-Type: application/json" \
  -d "{\"text\": \"你的长文本内容...\"}"
```

**文本分词 (SentenceSplitter):**
```bash
curl -X POST http://localhost:8083/api/splitter/sentence \
  -H "Content-Type: application/json" \
  -d "{\"text\": \"你的长文本内容...\"}"
```

**Function Calling 天气查询:**
```bash
curl -X POST http://localhost:8083/api/weather/query \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"北京今天天气怎么样？\"}"
```

## 项目结构

```
alibaba/
├── src/main/java/com/ljx/alibaba/
│   ├── AlibabaAiApplication.java          # 应用主类
│   ├── controller/
│   │   ├── ChatController.java            # 聊天控制器
│   │   ├── ChatRequest.java               # 聊天请求
│   │   ├── TextSplitterController.java    # 分词控制器
│   │   ├── SplitRequest.java              # 分词请求
│   │   └── WeatherController.java         # 天气查询控制器
│   └── service/
│       ├── ChatService.java               # 聊天服务
│       ├── TextSplitterService.java       # 分词服务
│       └── WeatherToolService.java        # 天气工具服务
├── src/main/resources/
│   └── application.yml                    # 配置文件
├── pom.xml                                # Maven 配置
├── start.bat                              # 启动脚本
└── test-api.bat                           # API 测试脚本
```

## 核心功能说明

### 1. 基础聊天

使用 `ChatClient` 进行对话：

```java
String response = chatClient.prompt()
        .user(message)
        .call()
        .content();
```

### 2. 流式聊天

支持流式响应，适合实时显示：

```java
Flux<String> stream = chatClient.prompt()
        .user(message)
        .stream()
        .content();
```

### 3. 文本分词

#### TokenTextSplitter
基于 Token 数量分割文本，适用于所有语言：

```java
TokenTextSplitter splitter = new TokenTextSplitter(500, 200, 5, 1000, false);
List<Document> chunks = splitter.apply(documents);
```

#### SentenceSplitter
基于句子边界分割，更适合中文：

```java
SentenceSplitter splitter = new SentenceSplitter(300);
List<Document> chunks = splitter.split(documents);
```

### 4. Function Calling

定义工具函数并使用 `@Tool` 注解：

```java
@Tool(description = "获取指定城市的天气信息")
public String getWeather(String city) {
    // 实现逻辑
}
```

在聊天时注册工具：

```java
chatClient.prompt()
    .user(message)
    .functions("getWeather")
    .call()
    .content();
```

## 注意事项

1. 确保已配置正确的 DeepSeek API Key
2. 需要 Java 21 或更高版本
3. 首次运行需要下载依赖，请耐心等待
4. 如遇到依赖问题，检查是否配置了 Spring Milestones 和 Snapshots 仓库

## 常见问题

**Q: 找不到 ChatClient 类？**  
A: 确保已正确配置 Spring AI Alibaba 依赖和仓库地址。

**Q: 分词效果不好？**  
A: 中文场景建议使用 SentenceSplitter，并调整参数。

**Q: Function Calling 不生效？**  
A: 确保工具方法使用了 `@Tool` 注解，并在调用时通过 `.functions()` 注册。

## 许可证

MIT License
