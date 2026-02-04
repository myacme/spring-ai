# Spring AI 集成 DeepSeek 项目

## 项目概述

这是一个基于 Spring Boot 和 Spring AI 框架开发的项目，集成了 DeepSeek 大语言模型 API，提供聊天机器人功能。项目支持普通聊天和流式聊天两种模式，并具备完整的异常处理机制。

## 技术栈

- **Spring Boot**: 3.3.4
- **Spring AI**: 1.0.3
- **Java**: 21
- **Maven**: 构建工具
- **DeepSeek API**: 作为大语言模型服务提供商

## 功能特性

- **聊天接口**: 提供 RESTful API 接口与 DeepSeek 模型进行交互
- **流式响应**: 支持流式返回聊天结果
- **异常处理**: 完善的全局异常处理机制
- **配置化**: 通过 application.yml 进行 API 配置

## 项目结构

```
src/
├── main/
│   ├── java/com/chinapopin/
│   │   ├── controller/
│   │   │   └── AIController.java        # AI 聊天控制器
│   │   ├── entity/
│   │   │   └── ChatRequest.java         # 聊天请求实体
│   │   ├── handler/
│   │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   ├── service/
│   │   │   ├── DeepSeekService.java     # DeepSeek 服务实现
│   │   │   └── StreamingService.java    # 流式聊天服务
│   │   └── SpringAIApplication.java     # 应用启动类
│   └── resources/
│       ├── application.yml              # 应用配置文件
│       └── logback.xml                  # 日志配置文件
```

## 配置说明

### API 配置

在 `application.yml` 文件中配置 DeepSeek API 相关参数：

```yaml
spring:
  ai:
    openai:
      api-key: ${DEEPSEEK_API_KEY:your-api-key-here}  # DeepSeek API 密钥
      base-url: https://api.deepseek.com               # DeepSeek API 基础地址
      chat:
        options:
          model: deepseek-chat                        # 使用的模型名称
          temperature: 0.7                            # 温度参数，控制输出随机性
          max-tokens: 2000                            # 最大生成 token 数量
          top-p: 0.9                                  # 核采样参数
          frequency-penalty: 0.0                      # 频率惩罚
          presence-penalty: 0.0                       # 存在惩罚
server:
  port: 8080                                        # 服务端口号
```

## API 接口

### 聊天接口

- **路径**: `POST /api/ai/chat`
- **请求体**:
  ```json
  {
    "message": "你的问题"
  }
  ```
- **响应**: 返回 AI 回答内容字符串

## 如何运行

1. **克隆项目**:
   ```bash
   git clone <repository-url>
   ```

2. **配置 API 密钥**:
   - 在系统环境变量中设置 `DEEPSEEK_API_KEY` 变量
   - 或直接在 `application.yml` 中替换默认密钥

3. **编译并运行**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **访问服务**:
   - 服务启动后，默认监听在 `http://localhost:8080`

## 依赖项

项目主要依赖包括：
- `spring-boot-starter-web`: Web 开发基础组件
- `spring-ai-openai-spring-boot-starter`: Spring AI OpenAI 集成包
- `lombok`: 简化 Java 代码的工具库

## 许可证

此项目为示例项目，可根据需要自由使用。