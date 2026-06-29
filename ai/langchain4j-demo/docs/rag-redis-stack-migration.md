# RAG 存储迁移：InMemoryEmbeddingStore → Redis Stack

## 背景

当前项目四个 RAG 层次的向量存储全部使用 `InMemoryEmbeddingStore`，应用重启后向量数据丢失。`docs/rag-plan.md` 已将 Redis 列为生产就绪方案。本次改动把 Basic RAG 层切换到 `RedisEmbeddingStore`（langchain4j-community-redis），并抽出共享配置，便于后续 advanced/modular/agentic 层复用。

## 前置条件

- 必须运行 **redis-stack**（含 RediSearch 向量检索模块），不是普通 redis。
  ```bash
  docker run -d --name redis-stack -p 6379:6379 redis/redis-stack-server:latest
  ```
- 验证 RediSearch 可用：`redis-cli FT._LIST` 应返回空或已有的索引列表。
- 向量相似度检索依赖 `FT.SEARCH`/`FT.CREATE`，普通 Redis 镜像无此模块。

## ⚠️ 关键 API 差异

`RedisEmbeddingStore.Builder` **与** `RedisChatMemoryStore.Builder`（已在 `MemoryConfig` 中使用）**构建方式完全不同**。务必将两者区分开。

### RedisChatMemoryStore.Builder（已有，不改）

```java
// ⚠️ 仅用于聊天记忆，有 host()/port() 方法
RedisChatMemoryStore.builder()
    .host("127.0.0.1")
    .port(6379)
    .storeType(StoreType.STRING)
    .prefix("chat:memory:")
    .build();
```

### RedisEmbeddingStore.Builder（本次新增）

```java
// ⚠️ 没有 host()/port()！必须传入 UnifiedJedis 实例
import redis.clients.jedis.JedisPooled;

JedisPooled jedis = new JedisPooled("redis://127.0.0.1:6379");
// 或带密码：new JedisPooled("redis://default:mypassword@127.0.0.1:6379")

RedisEmbeddingStore.builder()
    .unifiedJedis(jedis)          // ← 连接在这里传入，不走 host/port
    .indexName("rag-basic-embedding")
    .prefix("rag:embedding:")
    .dimension(1024)              // ← Integer，不是 int
    .metadataKeys(List.of("source"))  // ← 把文档 metadata 中的 source 字段存入 Redis
    .build();
```

方法签名对照：

| RedisChatMemoryStore.Builder | RedisEmbeddingStore.Builder |
|---|---|
| `host(String)` | ❌ 无 |
| `port(Integer)` | ❌ 无 |
| `user(String)` | ❌ 无 |
| `password(String)` | ❌ 无 |
| — | `unifiedJedis(UnifiedJedis)` ← **唯一连接入口** |
| `prefix(String)` | `prefix(String)` |
| `storeType(StoreType)` | — |
| `ttl(Long)` | — |
| — | `indexName(String)` |
| — | `dimension(Integer)` |
| — | `metadataKeys(Collection<String>)` |
| — | `metadataConfig(Map<String, SchemaField>)` |

## JedisPooled 构造方式速查

Jedis 6.0.0 提供了多种构造 `JedisPooled`（extends `UnifiedJedis`）的方式：

```java
// 方式一：URI 字符串（推荐，最简单）
new JedisPooled("redis://127.0.0.1:6379");
new JedisPooled("redis://default:mypassword@127.0.0.1:6379");

// 方式二：HostAndPort + JedisClientConfig
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;

new JedisPooled(
    HostAndPort.from("127.0.0.1:6379"),
    JedisClientConfig.builder().build()
);

// 方式三：host + port
new JedisPooled("127.0.0.1", 6379);
```

## 改动清单

### 1. `application.yaml` — 新增 Redis 向量库配置段

在 `spring:` / `langchain4j:` 同级追加：

```yaml
rag:
  redis:
    host: 127.0.0.1
    port: 6379
    # password: yourpassword      # 可选，如需认证
    index-name: rag-basic-embedding
    prefix: rag:embedding:
    dimension: 1024   # DashScope text-embedding-v3=1024; Zhipu embedding-3=2048
```

### 2. 新增 `rag/common/config/RagRedisEmbeddingConfig.java`

包路径：`me.maiz.langchain4jdemo.rag.common.config`

```java
package me.maiz.langchain4jdemo.rag.common.config;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.JedisPooled;

import java.util.List;

@Configuration
public class RagRedisEmbeddingConfig {

    @Bean
    @Primary
    public EmbeddingStore<TextSegment> redisEmbeddingStore(
            @Value("${rag.redis.host}") String host,
            @Value("${rag.redis.port}") int port,
            @Value("${rag.redis.index-name}") String indexName,
            @Value("${rag.redis.prefix}") String prefix,
            @Value("${rag.redis.dimension}") int dimension) {

        // 构建 JedisPooled（UnifiedJedis 子类）
        JedisPooled jedis = new JedisPooled(host, port);

        return RedisEmbeddingStore.builder()
                .unifiedJedis(jedis)
                .indexName(indexName)
                .prefix(prefix)
                .dimension(dimension)
                .metadataKeys(List.of("source"))  // 存储文档 source 元数据
                .build();
    }
}
```

> 如果 redis-stack 有密码，需要改为 `new JedisPooled(host, port, user, password)` 或 URI 方式。

### 3. 修改 `rag/basic/config/RagBasicConfig.java`

三处改动：

**(a) 删除 import：**
```java
// 删除
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Primary;
```

**(b) 删除 `basicEmbeddingStore()` 整个 `@Bean` 方法**（约第 35–39 行）

**(c) 修改 `basicStoreIngestor()` 和 `knowledgeAssistant()` 签名**，新增 `EmbeddingStore<TextSegment> embeddingStore` 参数，替换原来的 `basicEmbeddingStore()` 调用：

```java
@Bean
public EmbeddingStoreIngestor basicStoreIngestor(EmbeddingModel embeddingModel,
                                                 EmbeddingStore<TextSegment> embeddingStore) {
    return EmbeddingStoreIngestor.builder()
            .embeddingStore(embeddingStore)         // ← 从参数注入（@Primary Redis Bean）
            .embeddingModel(embeddingModel)
            .documentSplitter(basicDocumentSplitter())
            .build();
}

@Bean
public KnowledgeAssistant knowledgeAssistant(
        ChatModel chatModel,
        EmbeddingModel embeddingModel,
        EmbeddingStore<TextSegment> embeddingStore) {   // ← 新增参数
    return AiServices.builder(KnowledgeAssistant.class)
            .chatModel(chatModel)
            .contentRetriever(EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)     // ← 从参数注入
                    .embeddingModel(embeddingModel)
                    .maxResults(5)
                    .build())
            .build();
}
```

### 4. pom.xml — 无需改动

`langchain4j-community-redis` 已声明（pom.xml 55–60 行）。`langchain4j-community-redis-spring-boot-starter`（61–65 行）保持注释状态。

### 5. `RagBasicController.java` — 无需改动

按类型注入 `EmbeddingStore<TextSegment>` 的字段会自动拿到 `@Primary` Redis Bean。

## 涉及的完整文件列表

| 操作 | 文件 |
|------|------|
| **新增** | `src/main/java/me/maiz/langchain4jdemo/rag/common/config/RagRedisEmbeddingConfig.java` |
| **修改** | `src/main/resources/application.yaml` — 追加 `rag.redis.*` 段 |
| **修改** | `src/main/java/me/maiz/langchain4jdemo/rag/basic/config/RagBasicConfig.java` |
| **不改** | `pom.xml` — 依赖已就位 |
| **不改** | `rag/basic/controller/RagBasicController.java` — 按类型注入无感切换 |

## 行为变化

1. **持久化**：重启后 Redis 中向量不丢失，无需重新 ingest。
2. **重复 load**：重复调 `POST /rag/basic/load` 会累积重复数据。如需清空：`redis-cli FT.DROPINDEX rag-basic-embedding DD`，再重新 load 让程序自动建索引。
3. **维度绑定**：索引维度创建时固定。切换 embedding 模型（DashScope 1024 ↔ Zhipu 2048）须删除旧索引重建。
4. **索引自动创建**：首次写入时若索引不存在，`RedisEmbeddingStore.addInternal()` 会自动 `FT.CREATE`。

## 验证步骤

```bash
# 1. 启动 redis-stack
docker run -d --name redis-stack -p 6379:6379 redis/redis-stack-server:latest
redis-cli FT._LIST    # 确认 RediSearch 可用

# 2. 启动应用
mvn spring-boot:run

# 3. 入库
curl -X POST http://localhost:8080/rag/basic/load

# 4. 验证索引已创建
redis-cli FT._LIST
# 应输出: rag-basic-embedding

# 5. 验证向量已写入
redis-cli FT.SEARCH rag-basic-embedding "*" LIMIT 0 3
# 应返回带有 rag:embedding: 前缀的 key

# 6. 重启应用（不调 /load）
curl "http://localhost:8080/rag/basic/ask?question=Java课程多少钱"
# 应仍能基于持久化向量检索作答（证明持久化生效）
```
