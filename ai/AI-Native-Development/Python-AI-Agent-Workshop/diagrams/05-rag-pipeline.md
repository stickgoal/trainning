# 05 - RAG 管线架构图

```mermaid
graph LR
    subgraph "Indexing 索引阶段"
        Docs[原始文档<br/>PDF / Word / HTML / Markdown] --> Loader[Document Loader<br/>文档加载]
        Loader --> Splitter[Text Splitter<br/>文本切分<br/>Chunk 策略]
        Splitter --> Chunks[文档块<br/>Chunks]
        Chunks --> Embedder[Embedding Model<br/>向量化<br/>text → vector]
        Embedder --> VectorDB[(Vector Database<br/>向量数据库<br/>Milvus / Chroma / FAISS)]
    end

    subgraph "Retrieval 检索阶段"
        Query[用户提问<br/>Query] --> QueryEmbed[Query Embedding<br/>问题向量化]
        QueryEmbed --> Search[Similarity Search<br/>相似度搜索<br/>余弦相似度 / 内积]
        VectorDB --> Search
        Search --> TopK[Top-K 结果<br/>最相关的 K 个文档块]
        TopK --> Rerank[Reranker<br/>重排序<br/>Cross-Encoder 模型]
        Rerank --> Reranked[重排序后的结果]
    end

    subgraph "Generation 生成阶段"
        Reranked --> Context[Context Assembly<br/>上下文组装<br/>Prompt + 检索结果]
        Context --> LLM[LLM<br/>大语言模型]
        LLM --> Answer[生成回答<br/>带引用来源]
    end

    subgraph "Hybrid Search 混合检索"
        Keyword[关键词检索<br/>BM25] 
        Semantic[语义检索<br/>Vector Search]
        Keyword --> Fusion[Score Fusion<br/>分数融合]
        Semantic --> Fusion
        Fusion --> TopK
    end

    style Embedder fill:#e8f5e9
    style VectorDB fill:#e8f5e9
    style LLM fill:#e1f5fe
    style Rerank fill:#fff3e0
```

## RAG 管线步骤说明

| 阶段 | 步骤 | 说明 | Java 类比 |
|------|------|------|-----------|
| Indexing | Document Loader | 加载各种格式文档 | 文件解析器 |
| Indexing | Text Splitter | 切分为合适大小的 Chunk | 分页处理 |
| Indexing | Embedding | 文本 → 向量表示 | 对象序列化 |
| Indexing | Vector DB | 存储向量 + 原文 | 索引写入 |
| Retrieval | Query Embedding | 问题 → 向量 | 查询编码 |
| Retrieval | Similarity Search | 向量相似度搜索 | ES 查询 |
| Retrieval | Rerank | 重排序优化 | 搜索结果排序 |
| Generation | Context Assembly | 组装 Prompt | 请求构建 |
| Generation | LLM | 生成最终回答 | 服务调用 |

## Chunk 策略对比

| 策略 | 说明 | 适用场景 |
|------|------|---------|
| Fixed Size | 固定字符数切分 | 通用场景 |
| Recursive | 递归按分隔符切分 | 代码 / 结构化文档 |
| Semantic | 按语义边界切分 | 长文 / 论文 |
| Parent-Child | 大块检索小块返回 | 高精度检索 |

## 向量数据库选型

| 数据库 | 特点 | 适用场景 | Java 类比 |
|--------|------|---------|-----------|
| Milvus | 分布式、高性能 | 生产环境大规模 | Elasticsearch Cluster |
| Chroma | 轻量、易用 | 原型 / 小规模 | H2 Database |
| FAISS | 本地、极快 | 研究实验 | Lucene 索引 |
| pgvector | PostgreSQL 扩展 | 已有 PG 环境 | PostgreSQL + 扩展 |
