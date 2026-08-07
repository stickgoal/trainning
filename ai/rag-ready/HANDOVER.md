# RAG-Ready 项目接力开发文档

> **最后更新**: 2026-08-06 20:35  
> **项目路径**: `D:\workspace\处理\trainning\ai\rag-ready`  
> **状态**: 编译通过 ✅ | 测试运行失败 ❌（Redis FT.CREATE 参数格式问题）

---

## 一、项目概述

基于 **Redis Stack + LangChain4j 1.0.0** 的企业级 RAG（检索增强生成）系统，支持多格式文档解析、智能分块、混合检索（BM25 + 向量）、多知识库管理、自动评估与闭环优化。

### 技术栈

| 组件 | 版本 | 备注 |
|------|------|------|
| JDK | 17 | |
| Spring Boot | 3.5.3 | |
| LangChain4j | **1.0.0** | ⚠️ 不是 1.0.1，1.0.0 是最新稳定版 |
| Jedis | 5.2.0 | |
| Apache Tika | 2.9.2 | 文档解析 |
| jtokkit | 1.1.0 | Token 计数（⚠️ 不是 0.6.2） |
| Redis Stack | 7.4.7 | RediSearch v2.10.20, ReJSON v2.8.9 |
| LLM | qwen-plus（百炼） | 通过 OpenAI 兼容接口 |
| Embedding | text-embedding-v3（百炼） | 维度 1024 |

### 环境信息

- **Redis**: `192.168.1.241:6379`，密码 `abc123`
- **百炼 API Key**: 环境变量 `${DASHSCOPE_API_KEY}`
- **Maven 本地仓库**: `D:\appData\mvnrepo`
- **Maven 镜像**: 阿里云 `https://maven.aliyun.com/repository/public`

---

## 二、文件结构（19 个 Java 文件 + 3 个配置文件）

```
rag-ready/
├── pom.xml                          # Maven 配置
├── docker-compose.yml               # Redis Stack 一键启动
├── src/main/resources/
│   └── application.yml              # 应用配置
└── src/
    ├── main/java/com/yourproject/
    │   ├── RagReadyApplication.java          # Spring Boot 启动类
    │   ├── config/
    │   │   ├── RedisConfig.java              # JedisPooled Bean
    │   │   ├── EmbeddingConfig.java          # OpenAiEmbeddingModel Bean
    │   │   ├── RAGConfig.java                # ChatModel Bean（⚠️ 不是 ChatLanguageModel）
    │   │   ├── RAGProperties.java            # @ConfigurationProperties 配置属性
    │   │   └── KnowledgeBaseConfig.java      # 多知识库配置
    │   ├── document/
    │   │   ├── DocumentParser.java           # Apache Tika 文档解析
    │   │   └── DocumentSplitterConfig.java   # 手写递归分块器
    │   ├── retrieval/
    │   │   ├── RedisHybridRetriever.java     # 核心混合检索（BM25 + 向量 + RRF）
    │   │   ├── RrfFusion.java                # RRF 融合算法
    │   │   └── KnowledgeBaseRouter.java      # 多知识库路由
    │   ├── service/
    │   │   └── RagService.java               # 核心业务逻辑
    │   ├── controller/
    │   │   └── RagController.java            # REST API
    │   └── evaluation/
    │       ├── TestDataGenerator.java        # 测试数据生成（30 个 QA 对）
    │       ├── EvaluationService.java        # 评估指标计算
    │       └── AutoOptimizer.java            # 闭环优化（最多 3 轮）
    └── test/java/com/yourproject/
        ├── RAGEvaluationTest.java           # 评估测试入口（6 个 @Order 测试）
        └── RedisDebugTest.java              # Redis 连接调试（临时文件，可删）
```

---

## 三、已完成的 API 迁移修复（LangChain4j 1.0.0 Breaking Changes）

### ⚠️ 关键：LangChain4j 1.0.0 的包名和类名与 0.x/早期 1.0-beta 完全不同

| 旧版 (0.x / beta) | 1.0.0 正式版 | 影响文件 |
|---------------------|-------------|---------|
| `dev.langchain4j.openai.OpenAiChatModel` | `dev.langchain4j.model.openai.OpenAiChatModel` | RAGConfig.java |
| `dev.langchain4j.openai.OpenAiEmbeddingModel` | `dev.langchain4j.model.openai.OpenAiEmbeddingModel` | EmbeddingConfig.java |
| `dev.langchain4j.model.chat.ChatLanguageModel` | `dev.langchain4j.model.chat.ChatModel` | RAGConfig.java, RagService.java |
| `chatModel.generate(prompt)` 返回 String | `chatModel.chat(prompt)` 返回 String | RagService.java |
| `Metadata.getString(key, defaultValue)` | `Metadata.getString(key)` + `containsKey(key)` | RagService.java, DocumentSplitterConfig.java |

### pom.xml 依赖修复

| 原 artifactId | 原版本 | 修复后 | 原因 |
|---------------|--------|--------|------|
| `langchain4j-spring-boot-starter` | 1.0.1 | **删除** | 1.0.1 不存在（只有 beta 版），且不需要 starter（自己配置 Bean） |
| `langchain4j-redis` | 1.0.1 | **删除** | 1.0.1 不存在（只到 1.0.0-alpha1），自己实现了 Retriever |
| `langchain4j-document-parser-apache-tika` | 1.0.1 | `1.0.0-beta3` | 1.0.1 不存在 |
| `jtokkit` | 0.6.2 | `1.1.0` | 0.6.2 不存在 |
| - | - | 新增 `langchain4j-core` 1.0.0 | 传递依赖不够，需显式声明 |

---

## 四、Jedis 5.x sendCommand 调用方式

### 问题

Jedis 5.x 的 `UnifiedJedis.sendCommand(ProtocolCommand, String...)` 要求 `ProtocolCommand` 接口的 `getRaw()` 返回 `byte[]`（不是 `String`）。Lambda `() -> "FT.SEARCH"` 会编译失败。

### 解决方案

添加 helper 方法，使用 `SafeEncoder.encode()` 转换：

```java
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

private ProtocolCommand cmd(String command) {
    return () -> SafeEncoder.encode(command);
}

// 调用示例
jedis.sendCommand(cmd("FT.CREATE"), args.toArray(new String[0]));
jedis.sendCommand(cmd("FT.SEARCH"), indexName, query, ...);
jedis.sendCommand(cmd("JSON.SET"), redisKey, "$", json);
jedis.sendCommand(cmd("FT.DROPINDEX"), indexName);
```

---

## 五、❗ 当前阻塞问题：Redis FT.CREATE VECTOR 参数格式

### 问题描述

`mvn clean test -Dtest=RAGEvaluationTest` 运行时，`RedisHybridRetriever.createIndex()` 在创建 HNSW 向量索引时失败。

### 根因分析（已通过 RedisDebugTest 确认）

**RediSearch v2.10.20 的 VECTOR 语法要求：**

1. **必须指定参数个数**（`VECTOR HNSW 6 TYPE FLOAT32 DIM 1024 DISTANCE_METRIC COSINE` 中的 `6` = 3 对 name-value）
   - 不带参数个数 → `Bad arguments for vector similarity number of parameters`
   
2. **HNSW 不支持 EFCONSTRUCTION 和 M 参数**
   - 带这些参数 → `Bad arguments for algorithm HNSW: EFCONSTRUCTION`
   - 只接受必须参数：`TYPE`、`DIM`、`DISTANCE_METRIC`（6 个 = 3 对）

### 验证结果

| 测试用例 | 结果 |
|----------|------|
| `FT.CREATE ... SCHEMA $.text AS text TEXT`（纯文本，无向量） | ✅ SUCCESS |
| `VECTOR FLAT 6 TYPE FLOAT32 DIM 4 DISTANCE_METRIC L2`（HASH + FLAT + 参数个数） | ✅ SUCCESS |
| `VECTOR FLAT` 不带参数个数 | ❌ FAILED |
| `VECTOR HNSW 6 TYPE FLOAT32 DIM 4 DISTANCE_METRIC COSINE`（JSON + 只必须参数） | ✅ SUCCESS |
| `VECTOR HNSW 8 ... EFCONSTRUCTION 200` | ❌ FAILED |
| `VECTOR HNSW 10 ... EFCONSTRUCTION 200 M 16` | ❌ FAILED |

### 修复方案（⚠️ 未实施，需接力完成）

**修改文件**: `src/main/java/com/yourproject/retrieval/RedisHybridRetriever.java`  
**修改位置**: `createIndex()` 方法，约第 248-260 行

**当前代码（有 BUG）**:
```java
args.add("VECTOR");
args.add("HNSW");
args.add("TYPE");                    // ← 缺少参数个数
args.add("FLOAT32");
args.add("DIM");
args.add(String.valueOf(dimension));
args.add("DISTANCE_METRIC");
args.add("COSINE");
args.add("EFCONSTRUCTION");         // ← 不支持，需删除
args.add(String.valueOf(ragProperties.getHnsw().getEfConstruction()));
args.add("M");                      // ← 不支持，需删除
args.add(String.valueOf(ragProperties.getHnsw().getM()));
```

**修复后**:
```java
args.add("VECTOR");
args.add("HNSW");
args.add("6");                       // ← 参数个数：3 对 name-value
args.add("TYPE");
args.add("FLOAT32");
args.add("DIM");
args.add(String.valueOf(dimension));
args.add("DISTANCE_METRIC");
args.add("COSINE");
// 删除 EFCONSTRUCTION 和 M（此版本 RediSearch 不支持）
```

同时更新日志输出，去掉 efConstruction 和 M 的引用。

### 后续影响

- `RAGProperties.HnswConfig` 中的 `efConstruction`、`m`、`efSearch` 在当前 Redis 版本中**不可用于 FT.CREATE**
- `efSearch` 可能在 FT.SEARCH 查询时作为参数传递（待验证）
- 如果需要 HNSW 高级参数，需升级 Redis Stack 到更新版本

---

## 六、各模块实现细节

### 6.1 config 模块

**RedisConfig.java**
- 创建 `JedisPooled` Bean（`destroyMethod = "close"`）
- 连接 `192.168.1.241:6379`，密码 `abc123`，超时 5000ms

**EmbeddingConfig.java**
- 创建 `OpenAiEmbeddingModel` Bean
- 模型 `text-embedding-v3`，维度 1024
- BaseURL: `https://dashscope.aliyuncs.com/compatible-mode/v1`

**RAGConfig.java**
- 创建 `ChatModel` Bean（⚠️ 类名是 `ChatModel`，不是 `ChatLanguageModel`）
- 模型 `qwen-plus`，temperature 0.3，timeout 60s
- 包名: `dev.langchain4j.model.openai.OpenAiChatModel`

**RAGProperties.java**
- `@ConfigurationProperties(prefix = "rag")`
- 字段: chunkSize(300), overlapTokens(50), topK(5), minScore(0.6)
- 内部类 HnswConfig: efConstruction(200), m(16), efSearch(10)
- 内部类 RrfConfig: k(60)
- 内部类 HybridConfig: vectorWeight(0.7), bm25Weight(0.3)

**KnowledgeBaseConfig.java**
- `@ConfigurationProperties(prefix = "knowledge-base")`
- `Map<String, KnowledgeBaseProperties> bases`
- 三个知识库: hr-policy(idx:kb_hr), finance-ops(idx:kb_finance), general(idx:kb_general)

### 6.2 document 模块

**DocumentParser.java**
- 类名: `ApacheTikaDocumentParser`（⚠️ 不要用 `DocumentParser`，会与 import 冲突）
- 支持: PDF, docx, Excel, Markdown, TXT, HTML
- Metadata 字段: filename, source, timestamp, file_size, section_title

**DocumentSplitterConfig.java**
- 手写递归分块（段落 → 句子 → token 估算）
- ⚠️ `Metadata.getString(key, defaultValue)` 已改为 `containsKey` + `getString`
- 追加 chunk_index, section_title, total_chunks

### 6.3 retrieval 模块

**RedisHybridRetriever.java**（核心，约 430 行）
- `indexDocument()`: JSON.SET 写入 RedisJSON（key 前缀 `doc:`）
- `vectorSearch()`: FT.SEARCH KNN 查询 `*=>[KNN K @embedding $vec AS vector_score]`
- `bm25Search()`: FT.SEARCH `@text:(query)` 文本检索
- `hybridSearch()`: 并行检索 → RRF 融合 → 截取 topK
- `createIndex()`: FT.CREATE（⚠️ 当前有 BUG，见第五章）
- `parseSearchResponse()`: 解析 FT.SEARCH 响应（DIALECT 2 + WITHSCORES）
- 工具方法: `cmd()` 创建 ProtocolCommand, `floatArrayToBase64()`, `escapeJsonString()`, `escapeQuery()`

**RrfFusion.java**
- RRF 公式: `1 / (k + rank)`
- `FusedResult` record: docId, text, score, metadata
- `RetrievalResult` record: docId, text, score, metadata

**KnowledgeBaseRouter.java**
- `searchSingle()`: 单库检索
- `searchMultiple()`: 多库并行检索（CompletableFuture + 二次 RRF 融合）
- `searchAll()`: 搜索所有知识库
- `initAllIndexes()`: 初始化所有索引
- `getKnowledgeBaseNames()`: 返回知识库名称集合

### 6.4 service / controller

**RagService.java**
- 构造器注入: DocumentParser, DocumentSplitterConfig, RedisHybridRetriever, KnowledgeBaseRouter, EmbeddingModel, ChatModel, RAGProperties
- `ingestDocument()`: 解析 → 分块 → embedding → 写入 Redis
- `ingestDirectory()`: 批量入库
- `ask()`: 混合检索 → 构建上下文 → LLM 生成
- `initAllIndexes()`: 初始化索引
- `getKbRouter()`: package-private getter（供 Controller 使用）
- 内部 record: `RagAnswer(answer, retrievals, maxScore)`, `RetrievalInfo(docId, text, score)`
- SystemPrompt: 仅基于检索段回答、标注来源、相似度 < 0.6 时提示未找到

**RagController.java**
- `GET /api/rag/health` - 健康检查
- `POST /api/rag/ingest?filePath=&kbName=general` - 文档入库
- `POST /api/rag/ingest-directory?dirPath=&kbName=general` - 目录批量入库
- `POST /api/rag/ask?query=&kbName=` - RAG 问答
- `POST /api/rag/init-indexes` - 初始化索引
- `GET /api/rag/knowledge-bases` - 列出知识库

### 6.5 evaluation 模块

**TestDataGenerator.java**（21KB，最大文件）
- `generateTestDataset()`: 生成 hr_policy.md, finance_manual.md, tech_docs.md, faq.txt, company_rules.html, faq.json
- `buildQAPairs()`: 构建 30 个 QA 对
- record: `QAPair(question, expectedAnswer, sourceFile, keywords)`

**EvaluationService.java**
- 指标: Recall@5, MRR, NDCG@5, AnswerMatchRate
- `evaluate()`: 执行评估
- 输出: `evaluation-summary.json`, `failure-details.csv`
- record: `EvaluationReport`, `EvalResult`

**AutoOptimizer.java**
- 最多 3 轮调优:
  - 轮 1: 分块策略（chunkSize 200→400→600, overlap 30→80）
  - 轮 2: 检索参数（HNSW efSearch/efConstruction, RRF k）
  - 轮 3: 混合查询权重（vectorWeight/bm25Weight）
- 输出: `optimization-history.md`
- record: `OptimizationHistory`

### 6.6 测试

**RAGEvaluationTest.java**（6 个 @Order 测试）
1. `setup()` @BeforeAll: 生成测试数据 + initIndexes + ingestDirectory("general")
2. `testRecallAt5()`: ≥ 0.85
3. `testMrr()`: ≥ 0.8
4. `testNdcgAt5()`: ≥ 0.75
5. `testAnswerMatchRate()`: ≥ 0.7
6. `testAutoOptimization()`: 验证轮次 1~4
7. `testReportFilesGenerated()`: 验证报告文件生成

---

## 七、下一步待办清单

### 🔴 P0 - 阻塞项（必须修复才能跑通测试）

- [ ] **修复 RedisHybridRetriever.createIndex() 的 VECTOR HNSW 参数**
  - 在 `VECTOR HNSW` 后添加参数个数 `6`
  - 删除 `EFCONSTRUCTION` 和 `M` 参数
  - 详见第五章修复方案

### 🟡 P1 - 验证项（修复 P0 后可能暴露）

- [ ] 验证 FT.SEARCH KNN 查询是否正常返回结果
- [ ] 验证 BM25 查询中文分词效果（可能需要 jieba/HanLP）
- [ ] 验证 `escapeQuery()` 方法对中文查询的转义是否正确（当前将空格替换为 `|`，可能不适用中文）
- [ ] 验证 `parseSearchResponse()` 对 RedisJSON 返回格式的解析是否正确
- [ ] 验证百炼 embedding API 调用是否正常（需要 `DASHSCOPE_API_KEY` 环境变量）

### 🟢 P2 - 优化项

- [ ] 删除 `RedisDebugTest.java`（临时调试文件）
- [ ] `application.yml` 中的 `hnsw.ef-construction` 和 `hnsw.m` 配置在当前 Redis 版本下无效，考虑移除或注释
- [ ] `AutoOptimizer` 第 2 轮调优的 HNSW 参数调整在当前 Redis 版本下不可用，需调整调优策略
- [ ] 测试需在 15 分钟内完成（含索引构建和 3 轮调优），可能需要优化测试数据量
- [ ] 添加中文分词器（jieba/HanLP）提升 BM25 精度
- [ ] 考虑将 `escapeQuery()` 的空格处理改为不转义（让 RediSearch 默认分词处理）

---

## 八、关键代码片段速查

### Jedis sendCommand 正确调用

```java
// import
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

// helper
private ProtocolCommand cmd(String command) {
    return () -> SafeEncoder.encode(command);
}

// FT.CREATE
jedis.sendCommand(cmd("FT.CREATE"), "idx:name", "ON", "JSON", "PREFIX", "1", "doc:",
    "SCHEMA", "$.text", "AS", "text", "TEXT",
    "$.embedding", "AS", "embedding",
    "VECTOR", "HNSW", "6", "TYPE", "FLOAT32", "DIM", "1024", "DISTANCE_METRIC", "COSINE");

// FT.SEARCH (KNN)
jedis.sendCommand(cmd("FT.SEARCH"), indexName,
    "*=>[KNN 5 @embedding $vec AS vector_score]",
    "PARAMS", "2", "vec", base64Vector,
    "WITHSCORES", "DIALECT", "2", "LIMIT", "0", "5",
    "RETURN", "3", "text", "metadata", "vector_score");

// JSON.SET
jedis.sendCommand(cmd("JSON.SET"), "doc:key", "$", jsonString);

// FT.DROPINDEX
jedis.sendCommand(cmd("FT.DROPINDEX"), "idx:name");
```

### LangChain4j 1.0.0 正确 import

```java
// Chat Model（不是 ChatLanguageModel）
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;  // 注意 model.openai 不是 openai

// Embedding
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

// 调用
String answer = chatModel.chat(prompt);  // 不是 generate()
```

### Metadata 正确取值

```java
// LangChain4j 1.0.0 的 Metadata.getString() 只接受一个参数
String filename = segment.metadata().containsKey("filename")
    ? segment.metadata().getString("filename") : "unknown";
```

---

## 九、验收命令

```bash
# 编译
cd D:\workspace\处理\trainning\ai\rag-ready
mvn compile -q

# 编译测试代码
mvn test-compile -q

# 运行评估测试（需先修复 P0 问题）
mvn clean test -Dtest=RAGEvaluationTest

# 预期结果
# - 6 个测试全部通过
# - target/rag-evaluation/ 下生成 evaluation-summary.json、failure-details.csv、optimization-history.md
# - 整个流程在 15 分钟内完成
```

---

## 十、Redis Stack 模块信息

```
Redis: 7.4.7
模块列表:
  - RedisCompat v1
  - redisgears_2 v20020
  - timeseries v11206
  - ReJSON v20809
  - search (RediSearch) v21020
  - bf (RedisBloom) v20816
```

RediSearch v2.10.20 限制:
- VECTOR HNSW 只支持 TYPE/DIM/DISTANCE_METRIC 三个必须参数
- 不支持 EFCONSTRUCTION、M 等 HNSW 高级参数
- 必须显式指定参数个数（如 `6` = 3 对）
