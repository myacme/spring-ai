# Milvus 向量数据库集成指南

## 概述

本项目已成功集成 Milvus 向量数据库，用于存储和检索向量数据，支持语义搜索和相似度匹配功能。

## 架构组件

### 1. 核心配置
- **MilvusConfig.java** - Milvus 连接和 VectorStore 配置
- **MultiModelConfig.java** - 添加 Embedding 模型支持（使用 DashScope text-embedding-v2）

### 2. 服务层
- **MilvusVectorService.java** - 提供文档存储、检索和相似度搜索功能

### 3. 控制器层
- **MilvusController.java** - REST API 接口

## 快速开始

### 第一步：启动 Milvus 服务

#### 方式一：使用 Docker Compose（推荐）

```bash
# 启动 Milvus
start-milvus.bat

# 停止 Milvus
docker-compose -f docker-compose-milvus.yml down

# 查看日志
docker-compose -f docker-compose-milvus.yml logs -f
```

#### 方式二：使用已有的 Milvus 服务

修改 `application.yml` 中的 Milvus 连接信息：

```yaml
spring:
  ai:
    vectorstore:
      milvus:
        client:
          host: your-milvus-host
          port: 19530
          username: your-username  # 可选
          password: your-password  # 可选
```

### 第二步：启动应用

```bash
start.bat
```

### 第三步：测试功能

```bash
test-milvus.bat
```

## API 接口说明

### 1. 添加单个文档

**接口**: `POST /api/milvus/add`

**请求体**:
```json
{
  "text": "Spring AI 是一个强大的AI开发框架",
  "metadata": {
    "source": "test",
    "category": "ai"
  }
}
```

**响应**:
```json
{
  "success": true,
  "message": "文档添加成功",
  "textLength": 45
}
```

### 2. 批量添加文档

**接口**: `POST /api/milvus/add-batch`

**请求体**:
```json
{
  "texts": [
    "Milvus 是一个开源的向量数据库",
    "向量数据库可以存储和检索高维向量数据"
  ]
}
```

### 3. 导入文本并自动分块

**接口**: `POST /api/milvus/import`

**请求体**:
```json
{
  "text": "这是一段很长的文本内容...",
  "chunkSize": 500
}
```

### 4. 相似度搜索

**接口**: `POST /api/milvus/search`

**请求体**:
```json
{
  "query": "什么是人工智能？",
  "topK": 5,
  "threshold": 0.1
}
```

**参数说明**:
- `query`: 查询文本
- `topK`: 返回最相似的 K 个结果（默认 5）
- `threshold`: 相似度阈值 0-1，低于此值的结果将被过滤（默认 0.0）

**响应**:
```json
{
  "success": true,
  "query": "什么是人工智能？",
  "resultCount": 3,
  "results": [
    {
      "content": "人工智能是计算机科学的一个分支...",
      "id": "doc-id-123",
      "metadata": {
        "chunkIndex": 0,
        "source": "imported_text"
      },
      "score": 0.85
    }
  ]
}
```

### 5. 删除文档

**接口**: `DELETE /api/milvus/delete`

**请求体**:
```json
{
  "documentIds": ["doc-id-1", "doc-id-2"]
}
```

## 配置说明

### application.yml 配置项

```yaml
spring:
  ai:
    vectorstore:
      milvus:
        client:
          host: localhost              # Milvus 服务器地址
          port: 19530                  # Milvus 端口
          username:                    # 用户名（可选）
          password:                    # 密码（可选）
        databaseName: default          # 数据库名称
        collectionName: spring_ai_collection  # 集合名称
        embeddingDimension: 1536       # 向量维度（DashScope text-embedding-v2）
        indexType: IVF_FLAT            # 索引类型
        metricType: COSINE             # 相似度度量方式
```

### 可用的索引类型 (IndexType)
- `IVF_FLAT` - 倒排索引（推荐用于中小规模数据）
- `IVF_SQ8` - 量化索引（节省内存）
- `HNSW` - 分层导航小世界图（高性能）
- `FLAT` - 暴力搜索（精确但慢）

### 可用的相似度度量 (MetricType)
- `COSINE` - 余弦相似度（推荐）
- `L2` - 欧氏距离
- `IP` - 内积

## 使用示例

### Java 代码示例

```java
@Autowired
private MilvusVectorService milvusVectorService;

// 1. 添加文档
milvusVectorService.addDocument("Spring AI 是一个强大的框架", 
    Map.of("source", "manual"));

// 2. 批量导入文本（自动分块）
String longText = "这是一段很长的文本...";
milvusVectorService.importTextWithSplitting(longText, 500);

// 3. 相似度搜索
List<Document> results = milvusVectorService.similaritySearch(
    "什么是 Spring AI？", 
    5,      // topK
    0.5     // threshold
);

// 4. 处理搜索结果
for (Document doc : results) {
    System.out.println("内容: " + doc.getText());
    System.out.println("相似度: " + doc.getScore());
    System.out.println("元数据: " + doc.getMetadata());
}
```

### cURL 示例

```bash
# 添加文档
curl -X POST http://localhost:8083/api/milvus/add \
  -H "Content-Type: application/json" \
  -d "{\"text\": \"测试文档内容\"}"

# 搜索
curl -X POST http://localhost:8083/api/milvus/search \
  -H "Content-Type: application/json" \
  -d "{\"query\": \"测试\", \"topK\": 3}"
```

## 技术栈

- **Milvus**: v2.3.3 - 向量数据库
- **Spring AI**: 1.0.0 - AI 应用框架
- **Spring AI Alibaba**: 1.0.0.2 - 阿里云 AI 集成
- **DashScope Embedding**: text-embedding-v2 - 文本嵌入模型（1536 维）

## 常见问题

### Q1: 连接 Milvus 失败

**解决方案**:
1. 确认 Milvus 服务已启动：`docker-compose -f docker-compose-milvus.yml ps`
2. 检查端口是否正确：默认 19530
3. 查看日志：`docker-compose -f docker-compose-milvus.yml logs -f standalone`

### Q2: 向量维度不匹配

**解决方案**:
确保 `embeddingDimension` 配置与使用的 Embedding 模型匹配：
- DashScope text-embedding-v2: 1536 维
- OpenAI text-embedding-ada-002: 1536 维
- 其他模型请查阅对应文档

### Q3: 搜索结果为空

**可能原因**:
1. 数据库中没有文档
2. 相似度阈值设置过高
3. 查询文本与存储文档差异太大

**解决方案**:
- 降低 threshold 值（如设为 0.0 查看所有结果）
- 增加 topK 值
- 检查是否已成功添加文档

### Q4: 性能优化建议

1. **索引选择**:
   - 小规模数据（< 10万）: IVF_FLAT
   - 大规模数据（> 10万）: HNSW 或 IVF_SQ8

2. **批量导入**: 使用 `addDocuments()` 批量添加，避免逐条添加

3. **分块大小**: 根据实际场景调整 chunkSize（推荐 200-1000）

## 下一步

- 结合 RAG（检索增强生成）实现智能问答
- 集成更多数据源（PDF、Word 等）
- 实现向量数据的增量更新
- 添加向量数据的可视化管理界面

## 参考资料

- [Milvus 官方文档](https://milvus.io/docs)
- [Spring AI 文档](https://spring.io/projects/spring-ai)
- [DashScope API 文档](https://help.aliyun.com/zh/dashscope/)
