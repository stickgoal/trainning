# 课程大纲 - Python AI Agent 开发前沿技术深度工作坊

> 总时长：4 小时（240 分钟），含两次休息和终场 Q&A  
> 面向：Java 后端工程师（3+ 年经验）  
> 定位：Python AI 应用工程化深度工作坊

---

## 时间分配总览

| 模块 | 章节 | 时长 | 累计 | 内容 |
|------|------|------|------|------|
| 开场 | — | 5 min | 00:05 | 课程定位、议程、互动调研 |
| 上半场 | 第 1 章 | 15 min | 00:20 | Python AI 生态全景与技术选型 |
| 上半场 | 第 2 章 | 20 min | 00:40 | LLM 应用开发基础 |
| 上半场 | 第 3 章 | 25 min | 01:05 | LangChain 核心架构与 LCEL |
| 上半场 | 第 4 章 | 25 min | 01:30 | LangGraph 状态图与 Agent 工作流 |
| 上半场 | 第 5 章 | 20 min | 01:50 | RAG 技术体系 |
| 休息 | — | 15 min | 02:05 | — |
| 下半场 | 第 6 章 | 20 min | 02:25 | MCP 协议与工具标准化 |
| 下半场 | 第 7 章 | 15 min | 02:40 | A2A 多 Agent 协作 |
| 下半场 | 第 8 章 | 20 min | 03:00 | Harness 工程基础设施 |
| 下半场 | 第 9 章 | 15 min | 03:15 | Agent Skill 能力封装 |
| 下半场 | 第 10 章 | 15 min | 03:30 | 企业级 Agent 架构 |
| 下半场 | 第 11 章 | 15 min | 03:45 | 综合案例与架构决策 |
| 休息 | — | 15 min | 04:00 | — |
| Q&A | — | 20 min | 04:20 | 问答、学习路线、资源 |

---

## 开场（5 min）

### 目标
- 建立课程预期
- 了解听众背景
- 调整讲解深度

### 内容
- 课程定位说明：不是 Python 课、不是 ML 课，是 AI Engineering 课
- 互动调研：多少人用过 OpenAI API？多少人写过 LangChain？多少人做过 RAG？
- 议程总览：上半场打基础（生态→LLM→LangChain→LangGraph→RAG），下半场建系统（MCP→A2A→Harness→Skill→企业架构→案例）
- 核心承诺：课后能看懂任何 AI Agent 项目的架构

---

## 第 1 章：Python AI 生态全景与技术选型逻辑（15 min）

### 学习目标
- **深入理解** Python 成为 AI 开发主流语言的根本原因（不只是"生态好"）
- **掌握** Python AI 技术栈六层模型及每层核心组件
- **理解** Java vs Python 在 AI 开发中的具体差异和各自优劣
- **建立** 技术选型决策框架

### 核心内容

#### 1.1 Python 为什么赢了——深层原因分析（5 min）
- **不是语言层面的胜利**：Python 性能不如 Java，类型系统不如 Java 严谨
- **四个结构性优势**（每个展开讲）：
  1. AI SDK 优先支持——OpenAI/Anthropic/Google 官方 SDK 时间线对比
  2. 快速实验能力——REPL + Jupyter 的迭代速度对比 Java 编译循环
  3. 数据处理生态——NumPy/Pandas/Matplotlib 三件套 vs Java 数据处理碎片化
  4. LLM 工具链——LangChain/LlamaIndex/AutoGen 都是 Python 优先
- **Java 的机会**：Spring AI 的定位和差距分析

#### 1.2 Python AI 技术栈六层模型（5 min）
- 应用层：FastAPI / Pydantic / AsyncIO / Uvicorn
- LLM 编排层：LangChain / LangGraph / LlamaIndex
- 多 Agent 层：AutoGen / CrewAI / LangGraph Multi-Agent
- RAG 基础设施层：Embedding Model / Vector DB / Retriever / Reranker
- 协议与标准层：MCP / A2A / Tool Calling / OpenAI Function Calling
- 工程化层：Harness / Skill / Evaluation / Observability / Cost Control

#### 1.3 Java vs Python 技术对照表（3 min）
- 逐项对比：Web 框架、数据校验、异步、LLM 编排、状态机、向量检索、工具集成
- 强调：不是"Python 更好"，而是"AI 场景下 Python 生态更成熟"

#### 1.4 技术选型决策框架（2 min）
- 项目规模 → 框架选择决策树
- 团队能力 → 渐进式引入路径

### 架构图
- `diagrams/01-python-ai-ecosystem.md`

### 思考题
1. 你们公司如果要做 AI 应用，选 Python 还是 Java？决定因素是什么？
2. Spring AI 目前最大的差距在哪里？

---

## 第 2 章：LLM 应用开发基础——从 API 到结构化输出（20 min）

### 学习目标
- **掌握** LLM 应用 8 个核心概念的定义和实际影响
- **理解** LLM 应用完整架构和各组件交互
- **阅读** 基础 LLM 调用代码并理解每个参数的含义
- **理解** Tool Calling 的两步执行机制
- **区分** LLM 应用的四个成熟度等级

### 核心内容

#### 2.1 八大核心概念详解（8 min）
每个概念：定义 + 代码示例 + Java 类比 + 常见误区

- **Prompt**：系统提示 vs 用户提示，Prompt Engineering 基本原则
- **Token**：Token 计算方式，中文/英文 Token 差异，为什么 Token 影响成本
- **Context Window**：各模型窗口大小，上下文管理策略
- **Embedding**：向量维度，语义相似度计算，为什么能"语义搜索"
- **Retrieval**：检索的本质是"找最相关的上下文"
- **Tool Calling**：两步机制详解——LLM 决策 vs 应用层执行
- **Memory**：短期记忆（会话内）vs 长期记忆（跨会话），为什么 LLM 本身无状态
- **Agent**：LLM + Tool + 推理循环，从被动问答到主动行动

#### 2.2 LLM 应用架构全景（3 min）
- 架构图讲解：User → Application → LLM → Tools → Knowledge → Memory
- Java 类比：Client → Controller → Service → External API → Cache → DB
- 关键理解：LLM 应用仍然分层，只是 Service 变成了 LLM

#### 2.3 LLM 应用成熟度模型（4 min）
- Level 0：直接调 API（一次性问答）
- Level 1：Prompt 模板 + 上下文管理（多轮对话）
- Level 2：RAG + Tool Calling（知识增强 + 工具使用）
- Level 3：Agent 自主决策 + 多步推理（ReAct / Plan-Execute）
- Level 4：多 Agent 协作系统
- 每个等级配代码片段和适用场景

#### 2.4 代码讲解（5 min）
- `examples/01-llm-basics.py` 逐段讲解
- 重点：Tool Calling 两步流程、`**kwargs` 字典解包、异步调用模式

### 架构图
- `diagrams/02-llm-app-architecture.md`

### 示例代码
- `examples/01-llm-basics.py`

### 思考题
1. 如果用户问"今天北京天气如何"，LLM 自己能回答吗？为什么？
2. Tool Calling 为什么是两步而不是一步？

---

## 第 3 章：LangChain 核心架构与 LCEL 管线（25 min）

### 学习目标
- **深入理解** LangChain 解决的核心问题和设计哲学
- **掌握** LangChain 9 大核心组件的职责和关系
- **阅读** LCEL 管线代码并理解数据流向
- **理解** Agent 的推理-行动循环
- **掌握** Pydantic 结构化输出的原理

### 核心内容

#### 3.1 LangChain 解决什么问题（3 min）
- 没有 LangChain 的痛点（具体场景）
- LangChain 的设计哲学：统一抽象 + 可组合 + 声明式
- Java 类比：Spring 之于 Java 后端

#### 3.2 九大核心组件详解（10 min）
每个组件：定义 + 代码片段 + Java 类比 + 使用注意事项

- **Chat Model**：LLM 调用封装，支持多提供商切换
- **Prompt Template**：参数化提示词模板，变量注入
- **Runnable**：统一执行接口（invoke/stream/batch），LCEL 的基础
- **Chain**：Runnable 组合管道，`|` 运算符
- **Retriever**：知识检索接口，从 Vector DB 获取相关文档
- **Vector Store**：向量存储与检索，对接多种向量数据库
- **Memory**：对话状态管理，短期/长期记忆
- **Tool**：外部能力封装，`@tool` 装饰器
- **Agent**：LLM + Tool + 推理循环

#### 3.3 LCEL 语法深度讲解（5 min）
- `|` 运算符的重载原理
- 数据在 Chain 中的流转过程
- RunnableParallel / RunnablePassthrough / RunnableLambda
- 代码对比：LCEL vs 等价的命令式代码

#### 3.4 Agent 推理循环（4 min）
- ReAct 模式：Reasoning → Action → Observation → Repeat
- AgentExecutor 的执行流程
- 为什么需要 `max_iterations` 防止无限循环

#### 3.5 Pydantic 结构化输出（3 min）
- `with_structured_output` 的原理
- Pydantic Model → JSON Schema → LLM 约束生成
- Java 类比：DTO + Bean Validation + JSON 序列化

### 架构图
- `diagrams/03-langchain-stack.md`

### 示例代码
- `examples/02-langchain-basics.py`

### 思考题
1. LangChain 的 Chain 和 Java Stream API 有什么本质区别？
2. 如果 LLM 返回的 JSON 格式有误，`with_structured_output` 会怎样？

---

## 第 4 章：LangGraph 状态图与 Agent 工作流（25 min）

### 学习目标
- **理解** LangChain Chain 的局限性和 LangGraph 的解决思路
- **掌握** LangGraph 四大核心概念：Graph、Node、Edge、State
- **阅读** 完整的 LangGraph Agent 工作流代码
- **理解** 条件边和循环的实现机制
- **了解** 高级特性：Human-in-the-loop、Checkpointing、并行节点

### 核心内容

#### 4.1 为什么需要 LangGraph（3 min）
- Chain 是线性的——无法循环、无法条件分支、无法状态管理
- Agent 的真实需求：循环推理、根据结果决定下一步、共享状态
- LangGraph = 状态机 + Agent
- Java 类比：Spring StateMachine vs 普通 Service

#### 4.2 四大核心概念深度讲解（8 min）
- **State**（状态）：
  - TypedDict 定义，在所有节点间共享
  - Reducer 机制：`Annotated[list, add_messages]` 的含义
  - 部分更新：Node 只返回变更的字段，LangGraph 自动合并
  - Java 类比：StateObject + Redux reducer 模式

- **Node**（节点）：
  - 每个节点是一个函数：输入 State → 输出 State 更新
  - 节点类型：LLM 调用节点、工具执行节点、逻辑判断节点
  - Java 类比：StateMachine 的 Action 方法

- **Edge**（边）：
  - 固定边：A → B（无条件跳转）
  - 条件边：根据 State 动态决定下一个节点
  - `add_conditional_edges` 的用法详解
  - Java 类比：Transition + Guard Condition

- **Graph**（图）：
  - StateGraph 构建器：add_node + add_edge + compile
  - 编译后得到可执行图实例
  - Java 类比：StateMachineBuilder.build()

#### 4.3 ReAct Agent 工作流完整讲解（8 min）
- 工作流结构：START → understand → execute → reflect → (循环 or synthesize) → END
- 每个节点的职责和代码讲解
- 条件边 `should_continue` 的判断逻辑
- 循环次数控制：`iterations` 字段防止无限循环
- 工作流可视化图讲解

#### 4.4 高级特性（4 min）
- **Human-in-the-loop**：中断执行 → 人工确认 → 继续。适用场景：危险操作前审批
- **Checkpointing**：保存状态快照 → 失败恢复 → 时间旅行调试
- **并行节点**：多个节点同时执行 → 汇总结果
- **子图**：图的模块化组合

#### 4.5 LangGraph vs LangChain Agent 对比（2 min）
- 何时用 LangChain Agent：简单工具调用，线性流程
- 何时用 LangGraph：复杂工作流，需要循环/条件/状态管理

### 架构图
- `diagrams/04-langgraph-workflow.md`

### 示例代码
- `examples/03-langgraph-agent.py`

### 思考题
1. 如果 Agent 在循环中一直无法完成任务，应该如何处理？
2. LangGraph 的 State 和 Spring StateMachine 的 State 有什么区别？

---

## 第 5 章：RAG 技术体系——从基础到高级（20 min）

### 学习目标
- **深入理解** RAG 解决的核心问题和完整管线
- **掌握** 文档加载、切分、向量化、存储、检索的每个环节
- **阅读** 完整 RAG 管线代码
- **理解** 高级 RAG 技术：Hybrid Search、Rerank、Chunk 策略
- **掌握** 向量数据库选型决策

### 核心内容

#### 5.1 RAG 核心原理（3 min）
- 问题：LLM 知识有截止日期 + 不了解企业内部数据
- 方案：先检索相关知识 → 塞入 Prompt → LLM 基于知识回答
- 类比：开卷考试，LLM 是考生，知识库是课本
- 为什么不用 Fine-tuning：成本高、更新慢、不可解释

#### 5.2 RAG 完整管线详解（8 min）
每个阶段：原理 + 代码 + 注意事项

- **文档加载**：TextLoader / PyPDFLoader / WebBaseLoader
- **文本切分**：
  - RecursiveCharacterTextSplitter 的工作原理
  - chunk_size 和 chunk_overlap 的选择
  - 中文友好的分隔符配置
  - 为什么重叠很重要（防止切分处丢失上下文）
- **向量化（Embedding）**：
  - 文本 → 浮点数向量（如 1536 维）
  - 语义相近的文本，向量距离也近
  - OpenAI text-embedding-3-small vs large
- **向量存储**：
  - Chroma（轻量，开发原型）vs Milvus（分布式，生产环境）
  - `from_documents` 一步完成向量化 + 存储
- **检索**：
  - similarity_search：余弦相似度
  - k 值的选择：太少遗漏信息，太多噪声多
- **RAG Chain 组装**：
  - `retriever | format_docs` + `prompt | llm | parser`
  - 上下文注入 Prompt 的方式

#### 5.3 高级 RAG 技术（5 min）
- **Hybrid Search（混合检索）**：
  - 向量检索擅长语义匹配（"住宿标准" → "酒店费用上限"）
  - 关键词检索（BM25）擅长精确匹配（"500元" → 包含 "500"）
  - 分数融合：Reciprocal Rank Fusion
- **Rerank（重排序）**：
  - 双塔模型 vs 交叉模型的区别
  - Cross-Encoder 重新打分，精度更高但速度慢
- **Chunk 策略**：
  - 固定大小切分（简单但可能破坏语义）
  - 语义切分（按段落/句子，保持语义完整）
  - Parent-Child 分块（小块检索，大块提供上下文）
- **查询改写**：
  - 用户问题 → LLM 改写为更适合检索的查询
  - 多查询并行检索 + 结果合并

#### 5.4 向量数据库选型（2 min）
- Milvus：分布式、高性能、生产环境首选。类比 Elasticsearch Cluster
- Chroma：轻量、嵌入式、开发原型。类比 H2 Database
- FAISS：Facebook 开源、本地极快。类比 Lucene 索引
- Pinecone：云托管、免运维。类比托管型 Elasticsearch
- 选型决策矩阵

#### 5.5 RAG 评估指标（2 min）
- 检索质量：Recall@K、MRR、NDCG
- 生成质量：Faithfulness（忠实度）、Answer Relevance（回答相关性）
- 端到端评估：Ragas 框架

### 架构图
- `diagrams/05-rag-pipeline.md`

### 示例代码
- `examples/04-rag-pipeline.py`

### 思考题
1. 如果用户问的问题不在知识库里，RAG 会怎么回答？如何保证不"幻觉"？
2. chunk_size 设太大或太小分别有什么问题？

---

## — 休息 15 min —

---

## 第 6 章：MCP 协议与工具标准化（20 min）

### 学习目标
- **深入理解** MCP 解决的工具集成碎片化问题
- **掌握** MCP Client / Server / Tool 架构
- **阅读** MCP Server 创建和 Tool 暴露代码
- **理解** MCP vs 传统工具集成的对比
- **了解** MCP 生态现状和发展方向

### 核心内容

#### 6.1 MCP 解决什么问题（4 min）
- 传统工具集成的痛点（具体场景）：
  - 应用 A 用自定义 REST API 调 HR 系统
  - 应用 B 用 GraphQL 调同一个 HR 系统
  - 应用 C 用 gRPC 调同一个 HR 系统
  - 同一个能力，三套实现，无法复用
- MCP 的方案：标准化工具暴露协议，一次实现，处处可用
- 类比：MCP 之于工具调用 = JDBC 之于数据库访问 = USB 之于硬件接口

#### 6.2 MCP 架构详解（5 min）
- 三角色模型：
  - **MCP Server**：实现工具能力，暴露 Tool / Resource / Prompt
  - **MCP Client**：内嵌在 LLM 应用中，发起调用请求
  - **Transport**：通信层，支持 stdio / SSE / HTTP
- 通信流程：
  1. LLM 应用启动 → MCP Client 连接 MCP Server
  2. MCP Client 调用 list_tools() 获取工具列表
  3. LLM 决定调用工具 → MCP Client 发送 call_tool 请求
  4. MCP Server 执行工具 → 返回结果
  5. LLM 根据结果生成最终回答

#### 6.3 MCP Server 代码讲解（6 min）
- `examples/05-mcp-server.py` 逐段讲解
- Tool Schema 定义（与 OpenAI Function Calling 格式一致）
- `@server.list_tools()` 和 `@server.call_tool()` 装饰器
- 异步处理：`async/await` 的含义和必要性
- 安全职责：输入校验、权限检查、危险操作拦截

#### 6.4 MCP vs 传统工具集成对比（3 min）
- 代码对比图：传统方式 vs MCP 方式
- MCP 的核心价值：解耦、复用、标准化
- 何时用 MCP：多应用共享工具能力、跨团队协作
- 何时不用 MCP：单一应用内部工具、快速原型

#### 6.5 MCP 生态现状（2 min）
- Anthropic Claude 原生支持 MCP
- OpenAI 逐步支持 MCP
- 社区 MCP Server 生态：数据库、文件系统、Git、Slack 等
- 与 OpenAI Function Calling 的关系

### 架构图
- `diagrams/06-mcp-architecture.md`

### 示例代码
- `examples/05-mcp-server.py`

### 思考题
1. MCP 和 OpenAI Function Calling 是什么关系？是否互斥？
2. 如果已有 REST API，改造为 MCP Server 的成本有多大？

---

## 第 7 章：A2A 多 Agent 协作模式（15 min）

### 学习目标
- **理解** 单 Agent 的局限性和多 Agent 协作的动机
- **掌握** 三种协作模式及其适用场景
- **阅读** 多 Agent 协作代码
- **理解** Agent 间通信机制和任务编排

### 核心内容

#### 7.1 为什么需要多 Agent（3 min）
- 单 Agent 的问题：
  1. 能力有限——一个 Agent 难以同时擅长编码、测试、审查
  2. 上下文膨胀——所有任务挤在一个上下文里
  3. 耦合度高——一个环节出错影响全局
- 多 Agent 的优势：分工协作、上下文隔离、独立优化
- 类比：微服务架构——单体拆分成多个服务

#### 7.2 三种协作模式（5 min）
- **中心化协调（Coordinator 模式）**：
  - 一个 Coordinator Agent 负责任务分发和结果汇总
  - 优点：简单清晰，易于实现
  - 缺点：单点瓶颈，Coordinator 能力限制
  - 适用：流程明确的任务编排

- **去中心化协作（Peer-to-Peer 模式）**：
  - Agent 之间直接通信，通过共享状态协调
  - 优点：灵活，无单点
  - 缺点：复杂度高，可能死锁
  - 适用：探索性任务，Agent 自主性高

- **流水线模式（Pipeline 模式）**：
  - Agent 按顺序处理，前一个输出是后一个输入
  - 优点：简单高效
  - 缺点：不灵活，无法回退
  - 适用：明确的阶段性流程

#### 7.3 代码讲解（5 min）
- `examples/06-a2a-collaboration.py` 逐段讲解
- AgentMessage：消息结构设计
- Task：任务定义和依赖管理
- Coordinator.execute_workflow：完整协作流程
- Agent 间上下文传递机制

#### 7.4 框架对比（2 min）
- LangGraph Multi-Agent：基于图的编排，灵活但复杂
- AutoGen：微软开源，对话式协作
- CrewAI：角色扮演模式，简单直观
- 选型建议

### 架构图
- `diagrams/07-a2a-collaboration.md`

### 示例代码
- `examples/06-a2a-collaboration.py`

### 思考题
1. Coordinator 模式中，如果 Coordinator 本身出了问题怎么办？
2. 多 Agent 系统的成本如何控制（每个 Agent 都消耗 Token）？

---

## 第 8 章：Harness 工程基础设施（20 min）

### 学习目标
- **深入理解** Harness 的定义、必要性和核心职责
- **掌握** Harness 七大组件的设计原理
- **阅读** 完整的 Harness 实现代码
- **理解** Coding Agent（Claude Code / Codex）的 Harness 设计
- **掌握** 生产环境 Agent 的工程要求

### 核心内容

#### 8.1 什么是 Harness（3 min）
- 定义：让 Agent 稳定运行的工程基础设施
- 为什么需要——LLM 的不确定性：
  - 上下文可能溢出
  - LLM 可能幻觉
  - LLM 可能调用危险操作
  - Token 消耗可能失控
  - 输出质量波动
- 类比：JVM 之于 Java 应用 = Kubernetes 之于微服务 = Harness 之于 AI Agent

#### 8.2 七大组件深度讲解（10 min）
每个组件：原理 + 代码 + 生产实践

- **Context Manager（上下文管理）**：
  - Token 计数和预算管理
  - 80% 阈值触发自动压缩
  - 压缩策略：保留 System + 最近 N 条 + 历史摘要
  - Java 类比：带淘汰策略的缓存管理器

- **Memory（记忆）**：
  - 短期记忆：会话内消息历史
  - 长期记忆：跨会话持久化（用户偏好、历史决策）
  - 记忆检索：按时间/相关性/重要性

- **Tool Sandbox（工具沙箱）**：
  - 安全执行工具：权限检查 → 执行 → 异常捕获 → 日志记录
  - 权限分级：safe（只读）/ moderate（写操作）/ dangerous（删除/系统命令）
  - 危险操作需要人工审批
  - Java 类比：SecurityManager + Try-Catch + Timeout

- **Permission（权限控制）**：
  - 基于角色的工具访问控制
  - 输入过滤（防 Prompt Injection）
  - 输出审查（敏感信息过滤）

- **Evaluation（质量评估）**：
  - 线上评估：实时质量打分
  - 离线评估：回归测试集
  - 评估维度：相关性、准确性、完整性、安全性

- **Monitoring（可观测性）**：
  - 调用链追踪：每步操作的耗时和结果
  - 指标收集：Token 消耗、响应时间、工具调用次数
  - 日志：结构化日志 + 审计日志
  - Java 类比：ELK + Prometheus + SkyWalking

- **Feedback（反馈闭环）**：
  - 用户显式反馈：评分、点赞/踩
  - 隐式反馈：用户是否采纳、是否重新提问
  - 反馈驱动优化：Prompt 调整、工具改进

#### 8.3 代码讲解（4 min）
- `examples/07-harness-runtime.py` 重点讲解
- TokenCounter：Token 估算算法
- ContextManager._compress：压缩策略实现
- ToolSandbox.execute：安全执行流程
- Tracer：调用链追踪

#### 8.4 Coding Agent Harness 设计（3 min）
- Claude Code 的 Harness：
  - 上下文管理：管理数十个代码文件的上下文
  - 工具沙箱：安全执行 LLM 生成的代码
  - Git 集成：自动管理代码变更
  - 文件系统操作：读写/搜索/创建文件
- Codex 的 Harness：类似但实现方式不同
- 生产级 Coding Agent 的关键挑战

### 架构图
- `diagrams/08-harness-engineering.md`

### 示例代码
- `examples/07-harness-runtime.py`

### 思考题
1. 如果 Agent 执行了一个危险操作导致数据丢失，Harness 能做什么？
2. 上下文压缩可能会丢失什么信息？如何权衡？

---

## 第 9 章：Agent Skill 能力封装（15 min）

### 学习目标
- **理解** Skill 的定义、设计原则和核心组成
- **掌握** Skill vs Tool vs Prompt 的区别
- **阅读** Skill 定义、路由和编排代码
- **理解** Skill 的复用和分享机制

### 核心内容

#### 9.1 Skill 概念（3 min）
- Skill = Procedure（执行流程）+ Prompt（提示词）+ Tool（工具）+ Knowledge（领域知识）+ Validation（校验规则）
- 设计原则：
  1. 单一职责——每个 Skill 聚焦一个领域
  2. 自包含——包含所需全部资源
  3. 可组合——Skill 之间可以协作
  4. 可复用——跨 Agent 跨项目复用

#### 9.2 Skill vs Tool vs Prompt（3 min）
- Prompt：纯文本指令，无执行逻辑
- Tool：单个函数，单一操作
- Skill：完整能力封装，包含 Prompt + Tool + Knowledge + Validation
- Java 类比：Tool = 方法，Prompt = 配置，Skill = Spring Bean

#### 9.3 具体 Skill 实现讲解（5 min）
- CodingSkill：编码规范 Prompt + 代码执行 Tool + Python Knowledge
- DatabaseSkill：SQL 生成 Prompt + Schema Knowledge + 只读约束
- TestingSkill：pytest 模板 + 覆盖率要求
- DeploymentSkill：Dockerfile 模板 + K8s 配置 + 安全规范
- SkillRouter：根据用户请求匹配技能（关键词 / LLM / Embedding）
- SkillOrchestrator：多 Skill 组合编排

#### 9.4 Skill 复用与分享（2 min）
- Skill 作为可分享的标准化能力包
- Skill MarketPlace 概念
- 企业内部 Skill 库管理

#### 9.5 代码讲解（2 min）
- `examples/08-agent-skill.py` 关键片段

### 架构图
- `diagrams/09-skill-architecture.md`

### 示例代码
- `examples/08-agent-skill.py`

### 思考题
1. 你们公司的哪些业务能力适合封装为 Skill？
2. Skill 的粒度如何把握？太粗和太细各有什么问题？

---

## 第 10 章：企业级 Agent 架构（15 min）

### 学习目标
- **掌握** 企业级 Agent 六层架构及每层职责
- **理解** 各层组件的协作方式
- **掌握** 关键架构决策：模型选型、上下文策略、工具治理、安全合规
- **阅读** 企业级 Agent 完整执行流程代码

### 核心内容

#### 10.1 六层架构详解（6 min）
- **接入层**：API Gateway（认证/限流/路由）
- **应用层**：Session Manager / Skill Router / Workflow Engine
- **核心服务层**：LLM Service / Memory / Knowledge (RAG) / Tool Hub
- **协议层**：MCP Client（工具标准化）/ A2A Hub（Agent 通信）
- **工程层**：Harness / Evaluation / Observability / Security
- **企业系统层**：DB / ERP / Git / CI-CD / Wiki / Slack-飞书
- 每层职责和组件交互关系

#### 10.2 关键架构决策（5 min）
- **模型选型**：
  - 通用对话：GPT-4o
  - 代码生成：Claude 3.5 Sonnet
  - 中文场景：GLM-4 / Qwen
  - 成本敏感：开源模型（Llama / Qwen）+ 自部署
  - 多模型路由：按任务类型自动选择模型

- **上下文策略**：
  - 短对话：全量保留
  - 长对话：摘要压缩 + 向量检索
  - 关键信息：写入长期记忆
  - 成本控制：Token 预算管理

- **工具治理**：
  - 注册制：工具必须注册才能调用
  - 权限分级：只读 / 读写 / 危险操作
  - 审计日志：所有工具调用记录

- **安全合规**：
  - 输入过滤：防 Prompt Injection
  - 输出审查：敏感信息过滤
  - 数据隔离：多租户数据不串
  - 合规审计：完整的操作审计链

#### 10.3 代码讲解（2 min）
- `examples/09-enterprise-pattern.py` 完整执行流程
- 从用户请求到最终回答，经过所有架构层的处理

#### 10.4 企业落地挑战（2 min）
- 成本控制：Token 预算、模型路由、缓存策略
- 质量保证：评估体系、回归测试、A/B 测试
- 团队组织：AI 工程师角色定义、与后端团队协作
- 运维：监控告警、故障恢复、版本管理

### 架构图
- `diagrams/10-enterprise-architecture.md`

### 示例代码
- `examples/09-enterprise-pattern.py`

### 思考题
1. 企业级 Agent 的最大挑战是技术问题还是工程问题？
2. 如何评估 Agent 系统的 ROI？

---

## 第 11 章：综合案例与架构决策（15 min）

### 学习目标
- 通过两个完整案例串联所有知识点
- 理解真实项目的技术选型和架构决策过程
- 掌握不同场景下的架构差异

### 核心内容

#### 11.1 案例 1：企业知识库 Agent（7 min）
- **需求**：企业内部知识问答，支持差旅政策、HR 制度、技术文档等
- **架构**：Python + LangChain + RAG + Milvus + MCP
- **数据流**：
  1. 企业文档（PDF/Word/Wiki）→ 切分 → 向量化 → 存入 Milvus
  2. 用户提问 → Hybrid Search（向量 + 关键词）→ Rerank
  3. 检索结果 + 用户问题 → LLM 生成回答（附带引用来源）
  4. 通过 MCP 接入 HR/Finance 系统回答实时数据问题
- **技术选型理由**：
  - LangChain：成熟的 RAG 管线支持
  - Milvus：企业级分布式向量数据库
  - Hybrid Search + Rerank：提高检索精度
  - MCP：标准化接入企业系统
- **架构图讲解**
- **挑战与解决方案**：
  - 文档更新频率 → 增量索引
  - 多语言支持 → 多语言 Embedding 模型
  - 权限控制 → 文档级权限过滤

#### 11.2 案例 2：软件研发 Agent（7 min）
- **需求**：自动化代码实现、测试编写、代码审查、部署配置
- **架构**：Python + LangGraph + Skill + Tool Calling + Harness
- **工作流**：
  1. 用户提需求 → Coordinator Agent 拆解任务
  2. CodingSkill 生成代码 → Tool Sandbox 安全执行
  3. TestingSkill 生成测试 → 执行测试 → 反馈结果
  4. ReviewSkill 代码审查 → 如需修改回到编码步骤（LangGraph 循环）
  5. DeploymentSkill 生成部署配置
- **技术选型理由**：
  - LangGraph：支持循环（代码→测试→审查→修改→再测试）
  - Skill：编码/测试/审查/部署封装为可复用技能
  - Harness：上下文管理 + 工具沙箱 + 监控
- **架构图讲解**
- **挑战与解决方案**：
  - 代码质量 → 多轮审查 + 测试覆盖率检查
  - 安全性 → 沙箱执行 + 人工审批
  - 上下文管理 → 文件级上下文选择

#### 11.3 两个案例对比（1 min）
| 维度 | 知识库 Agent | 研发 Agent |
|------|-------------|-----------|
| 核心能力 | 检索 + 问答 | 代码生成 + 执行 |
| 编排框架 | LangChain | LangGraph |
| RAG 需求 | 重 | 轻 |
| 工具复杂度 | 低 | 高 |
| Harness 需求 | 中 | 高 |
| 安全要求 | 数据隔离 | 代码执行安全 |

### 架构图
- `diagrams/11-combined-cases.md`

---

## Q&A 与学习路线（20 min）

### 学习路线建议

```
阶段 1（1-2 周）：Python AI 基础
  └─ 环境搭建：Python 3.11+ + pip + venv
  └─ FastAPI + Pydantic + AsyncIO 基础
  └─ OpenAI SDK 直接调用
  └─ 目标：能写一个简单的 LLM 调用 API 服务

阶段 2（2-3 周）：LangChain 生态
  └─ LangChain 核心组件：Chat Model / Prompt / Chain
  └─ LangGraph 基础：State / Node / Edge
  └─ RAG 基础：文档加载 + 切分 + 向量检索
  └─ 目标：能构建一个 RAG 问答应用 + 简单 Agent

阶段 3（2-3 周）：Agent 工程化
  └─ Tool Calling + MCP Server
  └─ Skill 设计与实现
  └─ Harness 基础：上下文管理 + 工具沙箱
  └─ 目标：能构建一个工具增强的 Agent 应用

阶段 4（持续）：实战项目
  └─ 企业知识库 Agent
  └─ 研发辅助 Agent
  └─ 业务流程自动化 Agent
  └─ 目标：完整的企业级 Agent 应用
```

### 推荐资源

**官方文档**
- LangChain: https://python.langchain.com
- LangGraph: https://langchain-ai.github.io/langgraph
- LlamaIndex: https://docs.llamaindex.ai
- MCP: https://modelcontextprotocol.io
- OpenAI API: https://platform.openai.com/docs

**开源项目**
- LangChain: https://github.com/langchain-ai/langchain
- LangGraph: https://github.com/langchain-ai/langgraph
- AutoGen: https://github.com/microsoft/autogen
- CrewAI: https://github.com/crewAIInc/crewAI

**学习路径**
- DeepLearning.AI 的 LangChain 课程
- Anthropic 的 Prompt Engineering 指南
- OpenAI Cookbook

### 核心思维转变

1. **从确定性到概率性**：Java 方法给定输入有确定输出；LLM 输出有随机性
2. **从代码逻辑到 Prompt 工程**：if-else 控制流程 → Prompt 引导推理方向
3. **从单体到 Agent 编排**：一个 Service 方法 → 多个 Agent 协作
4. **从功能测试到质量评估**：功能对不对 → 回答好不好/准不准/安全不安全
