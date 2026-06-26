# RAG（检索增强生成）实现规划

## 一、RAG 概念与演进

### 什么是 RAG

**RAG（Retrieval-Augmented Generation，检索增强生成）** 是一种在大语言模型（LLM）生成回答之前，先从外部知识库中检索相关信息的范式。它解决了 LLM 的三大核心问题：

| 问题 | 表现 | RAG 解决方案 |
|------|------|-------------|
| **知识过时** | 训练数据截止后的事件无法回答 | 检索最新文档补充上下文 |
| **幻觉** | 模型编造不存在的"事实" | 用检索到的真实文档约束生成 |
| **专有知识** | 模型未见过企业内部/个人文档 | 私有文档作为外部知识源 |

### RAG 的演进路线

```
Naive RAG (基础)  →  Advanced RAG (进阶)  →  Modular RAG (模块化)  →  Agentic RAG (智能体)
```

| 层次 | 核心思想 | 复杂度 | 适用场景 |
|------|---------|--------|---------|
| **Naive RAG** | 检索 → 拼接 → 生成 | ⭐ | 简单文档问答，快速验证 |
| **Advanced RAG** | 检索前/后优化策略 | ⭐⭐ | 需要高召回率的知识库 |
| **Modular RAG** | 可组合的模块化流程 | ⭐⭐⭐ | 复杂业务场景，定制化流程 |
| **Agentic RAG** | 智能体自主决策检索 | ⭐⭐⭐⭐ | 复杂推理、多步检索 |

---

## 二、各层次 RAG 详解

### 1. Naive RAG（基础 RAG）

#### 流程

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  文档索引  │ → │  检索文档  │ → │  拼接提示  │ → │  LLM生成  │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
```

**索引阶段：**
1. 文档解析（PDF/Word/TXT/HTML → Text）
2. 文档切片（Document Splitting）
3. 向量化嵌入（Embedding）
4. 存储到向量数据库

**检索阶段：**
1. 用户查询向量化
2. 向量相似度搜索（余弦相似度/内积）
3. 返回 Top-K 相关文档片段

**生成阶段：**
1. 将检索到的文档片段作为上下文
2. 拼接 System Prompt + 上下文 + 用户问题
3. LLM 基于上下文生成回答

#### 局限

- 检索质量依赖嵌入模型和分块策略
- 对复杂查询（多条件、推理）表现差
- 无法处理需要多步推理的问题
- 缺乏对检索结果的验证和优化

---

### 2. Advanced RAG（进阶 RAG）

在 Naive RAG 基础上引入检索**前**和检索**后**的优化策略：

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  检索前优化    │ → │  检索优化      │ → │  检索后优化    │
└──────────────┘    └──────────────┘    └──────────────┘
```

#### 检索前优化（Pre-Retrieval）

| 技术 | 描述 | 效果 |
|------|------|------|
| **查询重写（Query Rewriting）** | 用 LLM 将用户模糊查询改写为更精确的查询 | ↑ 召回率 |
| **查询分解（Query Decomposition）** | 将复杂问题拆分为多个子问题分别检索 | ↑ 覆盖度 |
| **HyDE（假设文档嵌入）** | 先让 LLM 生成假设答案，用假设答案检索 | ↑ 语义匹配 |
| **查询扩展（Query Expansion）** | 同义词/相关词扩展查询 | ↑ 召回率 |

#### 检索优化（Retrieval）

| 技术 | 描述 |
|------|------|
| **混合检索（Hybrid Search）** | 向量检索 + 关键词检索（如 BM25）结合 |
| **多路召回（Multi-Route）** | 从多个不同的知识源/检索策略同时召回 |
| **多粒度检索** | 同时检索段落、文档、摘要等不同粒度的内容 |

#### 检索后优化（Post-Retrieval）

| 技术 | 描述 | 效果 |
|------|------|------|
| **重排序（Re-Ranking）** | 用交叉编码器对初筛结果重新排序 | ↑ 精确率 |
| **上下文压缩（Context Compression）** | 去除冗余、合并相似内容 | ↑ 相关密度 |
| **MMR（最大边际相关性）** | 平衡相关性和多样性，避免重复 | ↑ 多样性 |
| **结果过滤（Filtering）** | 基于元数据/相关性阈值过滤 | ↑ 精确率 |

---

### 3. Modular RAG（模块化 RAG）

Modular RAG 将检索增强流程拆分为可独立配置和组合的功能模块：

#### 核心模块

```
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│  Query   │  │   Route  │  │ Retrieve │  │  Rerank  │  │ Generate │
│ Transform│  │          │  │          │  │          │  │          │
└──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘
```

| 模块 | LangChain4j 对应接口 | 职责 |
|------|----------------------|------|
| **QueryTransform** | `QueryTransformer` | 重写、扩展、分解查询 |
| **Route** | `ContentRouter` / `QueryRouter` | 路由到不同检索策略 |
| **Retrieve** | `ContentRetriever` | 从向量库/文档库检索 |
| **Aggregate** | `ContentAggregator` | 合并多个检索结果 |
| **Rerank** | (自定义实现) | 重排序检索结果 |
| **Generate** | `ChatLanguageModel` | 基于检索结果生成回答 |

#### LangChain4j 中的 RAG 核心架构

```
                    ┌──────────────────────┐
                    │  RetrievalAugmentor   │  ← 入口，编排整个 RAG 流程
                    └──────────────────────┘
                              │
               ┌──────────────┴──────────────┐
               │              │              │
               ▼              ▼              ▼
     ┌─────────────────┐ ┌──────────┐ ┌──────────────┐
     │ QueryTransformer │ │Content   │ │Content       │
     │ (查询预处理)      │ │Retriever │ │Aggregator    │
     └─────────────────┘ │(检索器)   │ │(结果聚合器)   │
                          └──────────┘ └──────────────┘
                               │
                     ┌─────────┴─────────┐
                     │                   │
                     ▼                   ▼
             ┌─────────────┐   ┌────────────────┐
             │EmbeddingStore│   │DocumentSplitter│
             │(向量存储)    │   │(文档切片器)     │
             └─────────────┘   └────────────────┘
```

---

### 4. Agentic RAG（智能体 RAG）

Agentic RAG 让 AI 智能体（Agent）**自主决策**何时检索、检索什么、以及如何使用检索结果。

#### Agentic RAG 的能力

| 能力 | 描述 | 相比 Modular RAG 的优势 |
|------|------|------------------------|
| **按需检索** | 只有需要外部知识时才触发检索 | 节省成本，减少噪声 |
| **多步检索** | 一次生成中可多次检索 | 处理复杂多跳问题 |
| **工具选择** | 可选择不同的检索工具/知识源 | 灵活适配不同场景 |
| **结果验证** | 对检索结果进行评估和修正 | 提高准确性 |
| **自我纠正** | 发现不足后重新检索 | 自我纠错能力 |

#### LangChain4j 中的 Agent 架构

```
                    ┌──────────────┐
                    │   Supervisor  │  ← 路由/协调 Agent
                    │   Agent       │
                    └──────┬───────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
           ▼               ▼               ▼
     ┌──────────┐   ┌──────────┐   ┌──────────┐
     │  Tool 1  │   │  Tool 2  │   │  Tool 3  │
     │(对话)    │   │(检索知识)│   │(计算/查询)│
     └──────────┘   └──────────┘   └──────────┘
                           │
                     ┌─────┴─────┐
                     │  知识库    │
                     │(向量存储)  │
                     └───────────┘
```

---

## 三、技术选型与依赖

### 当前项目基础

| 项目 | 版本 |
|------|------|
| Spring Boot | 3.5.15 |
| Java | 17 |
| LangChain4j | 1.16.0-beta26 |
| 模型接口 | OpenAI 兼容 API |
| 当前提供商 | DeepSeek / 智谱 GLM / Agnes AI |

### 新增依赖

```xml
<!-- 嵌入模型支持（依赖 langchain4j-open-ai 已包含） -->
<!-- 无需额外添加，OpenAiEmbeddingModel 由 langchain4j-open-ai-spring-boot-starter 提供 -->

<!-- 文档解析 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-document-parser-apache-pdfbox</artifactId>
    <version>${langchain4j.version}</version>
</dependency>

<!-- 文档加载器 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-document-loader</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
```

### 向量存储策略

| 策略 | 适用阶段 | 说明 |
|------|---------|------|
| **InMemoryEmbeddingStore** | 开发/演示 | LangChain4j 内置，无需外部服务 |
| **Redis** | 生产就绪 | 当前项目已有 Redis，可通过 langchain4j-redis 集成 |
| **Elasticsearch** | 生产 | 适合已有 ES 的项目 |

> **本项目开发阶段使用 InMemoryEmbeddingStore**，无需额外基础设施。

### 嵌入模型适配

| 提供商 | 嵌入模型 | OpenAI 兼容 | 备注 |
|--------|---------|------------|------|
| DeepSeek | `deepseek-embedding` | ✅ | 需配置 base-url |
| 智谱 GLM | `embedding-3` | ✅ | 与聊天模型共用 base-url |
| Agnes AI | 待确认 | ❓ | 可能不支持 |

---

## 四、案例规划

### 案例 1：基础 RAG — 蜗牛学苑知识库问答

**场景**：学生询问课程信息、报名流程、校区地址等，AI 从知识库中检索准确信息回答。

**难度**：⭐

**文档内容**：
- `courses.txt` — 课程目录与介绍（Java、Python、前端、大数据等课程）
- `campus.txt` — 校区地址与联系方式
- `faq.txt` — 常见问题（报名流程、学费、就业等）

**技术要点**：

| 步骤 | 技术 | 说明 |
|------|------|------|
| ① 文档加载 | `FileDocumentLoader` | 从 resources 加载文本文件 |
| ② 文档切片 | `DocumentSplitter` / `RecursiveDocumentSplitter` | 按段落/句子分割，设置重叠 |
| ③ 文本嵌入 | `EmbeddingModel` (OpenAI 兼容) | 将文本块转为向量 |
| ④ 存储索引 | `EmbeddingStoreIngestor` | 嵌入并存入 InMemoryEmbeddingStore |
| ⑤ 检索增强 | `ContentRetriever` / `RetrievalAugmentor` | 自动检索 + 增强提示词 |
| ⑥ 生成回答 | `AiService` + `@UserMessage` + `@V` | 基于检索结果生成 |

**涉及新文件**：
- `rag/basic/config/RagBasicConfig.java`
- `rag/basic/controller/RagBasicController.java`
- `rag/basic/assistant/KnowledgeAssistant.java`
- `resources/rag-docs/courses.txt`
- `resources/rag-docs/campus.txt`
- `resources/rag-docs/faq.txt`

---

### 案例 2：进阶 RAG — 查询重写与多路检索

**场景**：复杂/模糊查询的优化检索。用户问"我基础不好，想学编程，有什么推荐？"，需要重写查询并多路检索。

**难度**：⭐⭐

**技术要点**：

| 模块 | 技术 | 说明 |
|------|------|------|
| 查询重写 | `QueryTransformer` (自定义) | 用 LLM 将模糊问题改写为精确搜索语句 |
| 多路检索 | 多个 `ContentRetriever` | 从不同文档集分别检索 |
| 结果聚合 | `ContentAggregator` | 合并多路检索结果，去重排序 |
| 自动配置 | `RetrievalAugmentor` | 串联整个增强流程 |

**涉及新文件**：
- `rag/advanced/query/QueryRewriteTransformer.java`
- `rag/advanced/config/RagAdvancedConfig.java`
- `rag/advanced/controller/RagAdvancedController.java`

---

### 案例 3：模块化 RAG — 自定义 RetrievalAugmentor

**场景**：构建完整的模块化 RAG 流水线，包含查询变换 → 路由 → 检索 → 聚合 → 生成 的完整链路。

**难度**：⭐⭐⭐

**技术要点**：

| 模块 | 实现方式 | 说明 |
|------|---------|------|
| `QueryTransformer` | 自定义 | 查询重写/扩展 |
| `ContentRetriever` | 多个实例 | 针对不同文档集配置不同检索器 |
| `ContentAggregator` | 自定义 | 合并、重排序、过滤 |
| `RetrievalAugmentor` | 链式组合 | 串联所有模块 |

**涉及新文件**：
- `rag/modular/config/RagModularConfig.java`
- `rag/modular/service/ModularRagService.java`
- `rag/modular/controller/RagModularController.java`

---

### 案例 4：Agentic RAG — 智能体自主检索

**场景**：AI 招生助理 Agent 自主决定何时需要检索知识库。它能自然对话，在遇到不确定的问题时主动查询知识库，还能结合检索结果进行推理。

**难度**：⭐⭐⭐⭐

**技术要点**：

| 组件 | 技术 | 说明 |
|------|------|------|
| Agent 框架 | `@Agent` + `AgenticServices` | LangChain4j Agent 支持 |
| 检索工具 | `ToolSpecification` / `@Tool` | 将检索封装为 Agent 可调用的工具 |
| 自主决策 | Agent 内部推理 | Agent 自行判断是否需要检索 |
| 多工具组合 | Supervisor Agent | 检索 + 对话 + 其他工具 |

**涉及新文件**：
- `rag/agentic/tool/KnowledgeSearchTool.java`
- `rag/agentic/agent/AdmissionAssistantAgent.java`
- `rag/agentic/config/RagAgenticConfig.java`
- `rag/agentic/controller/RagAgenticController.java`

---

## 五、文件清单总览

```
src/main/java/me/maiz/langchain4jdemo/rag/
├── basic/                              # 案例 1：基础 RAG
│   ├── config/RagBasicConfig.java       # 基础 RAG 配置（EmbeddingStore, ContentRetriever）
│   ├── assistant/KnowledgeAssistant.java # AI Service 接口
│   └── controller/RagBasicController.java# REST 接口
├── advanced/                            # 案例 2：进阶 RAG
│   ├── query/QueryRewriteTransformer.java# 查询重写转换器
│   ├── config/RagAdvancedConfig.java    # 进阶 RAG 配置
│   └── controller/RagAdvancedController.java
├── modular/                             # 案例 3：模块化 RAG
│   ├── config/RagModularConfig.java     # 模块化 RAG 配置
│   ├── service/ModularRagService.java   # 模块化 RAG 服务
│   └── controller/RagModularController.java
└── agentic/                             # 案例 4：智能体 RAG
    ├── tool/KnowledgeSearchTool.java    # 知识检索工具
    ├── agent/AdmissionAssistantAgent.java # Agent 接口
    ├── config/RagAgenticConfig.java     # Agent RAG 配置
    └── controller/RagAgenticController.java

src/main/resources/rag-docs/
├── courses.txt                          # 课程介绍文档
├── campus.txt                           # 校区信息文档
└── faq.txt                              # 常见问题文档
```

---

## 六、实施计划

| 阶段 | 内容 | 预计文件数 | 依赖 |
|------|------|-----------|------|
| **第 1 步** | 新增 Maven 依赖 + embedding 模型配置 | 2 (pom.xml, application.yaml) | — |
| **第 2 步** | 创建知识库文档（课程/校区/FAQ） | 3 | 第 1 步 |
| **第 3 步** | 案例 1：基础 RAG（配置 + Assistant + Controller） | 3 | 第 1-2 步 |
| **第 4 步** | 案例 2：进阶 RAG（查询重写 + 多路检索） | 3 | 第 3 步 |
| **第 5 步** | 案例 3：模块化 RAG（自定义 RetrievalAugmentor） | 3 | 第 3 步 |
| **第 6 步** | 案例 4：Agentic RAG（Agent + 检索工具） | 4 | 第 3 步 |
| **第 7 步** | 验证测试 | 1 | 第 3-6 步 |

---

## 七、关键 API 参考

### LangChain4j RAG 核心接口

```java
// 检索器：从知识库检索相关内容
public interface ContentRetriever {
    List<Content> retrieve(Query query);
}

// 检索增强器：编排整个 RAG 流程
public interface RetrievalAugmentor {
    Query augment(Query query);
    List<Content> retrieve(Query query);
    List<Content> augment(List<Content> contents);
}

// 查询转换器：对用户查询进行预处理
public interface QueryTransformer {
    List<Query> transform(Query query);
}

// 内容聚合器：合并/处理多个检索结果
public interface ContentAggregator {
    List<Content> aggregate(List<Content> contents);
}
```

### 常用构建器

```java
// 创建嵌入存储
EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

// 创建文档切片器
DocumentSplitter splitter = new RecursiveDocumentSplitter(500, 50);

// 创建嵌入存储注入器
EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
    .embeddingStore(embeddingStore)
    .embeddingModel(embeddingModel)
    .documentSplitter(splitter)
    .build();

// 创建检索增强器
RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
    .contentRetriever(contentRetriever)
    .queryTransformer(queryTransformer)
    .build();
```
