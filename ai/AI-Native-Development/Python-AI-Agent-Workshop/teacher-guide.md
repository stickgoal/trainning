# 讲师讲解稿 — Python AI Agent 开发前沿技术快速扫盲

> 90 分钟课程完整教案 | 面向 Java 后端工程师  
> 每章节包含：时间安排、学习目标、核心概念、架构图说明、代码讲解、讲师话术

---

## 开场（2 min）

### 讲师话术

各位好，欢迎来到《Python AI Agent 开发前沿技术快速扫盲》。

先做个定位说明：这门课**不是** Python 语法课，**不是** 机器学习理论课。我们假设大家有 Java 后端经验，能读懂 Python 代码，但不了解 Python AI 应用生态。

90 分钟里，我们要完成一件事：**建立 Python AI Engineering 的技术地图**。

你会知道：
- Python AI 生态里有哪些组件
- 每个组件解决什么问题
- 它们如何组合成一个 Agent 应用
- 你接下来该怎么学

不期望你课后就能写代码，但期望你课后能看懂任何 AI Agent 项目的架构。

我们开始。

---

## 第 1 章：Python AI 生态概览（5 min）

### 时间安排
- 2 min：Python 为什么赢了
- 2 min：技术栈全景
- 1 min：Java 对比

### 讲师话术

#### Python 为什么成为 AI 主流语言

先回答一个 Java 工程师最关心的问题：**为什么是 Python，不是 Java？**

不是因为 Python 语法更好，也不是因为性能更强。Java 在运行效率上完胜 Python。Python 赢在**生态**，四个方面：

1. **AI SDK 优先支持**：OpenAI、Anthropic、Google，所有大模型厂商发布的官方 SDK，Python 版本永远是第一个，最完整，文档最全。Java 版本通常是社区驱动的第三方封装，滞后 3-6 个月。

2. **快速实验能力**：AI 开发的核心循环是"改 Prompt → 跑一次 → 看结果 → 再改"。Python 的 REPL（交互式环境）和 Jupyter Notebook 让这个循环以秒为单位。Java 需要"改代码 → 编译 → 运行 → 看日志"，慢一个数量级。

3. **数据处理生态**：AI 应用离不开数据处理。Python 有 NumPy、Pandas、Matplotlib 这套数据处理三件套。Java 的数据处理生态分散且不如 Python 成熟。

4. **LLM 工具链成熟**：LangChain、LlamaIndex、AutoGen 这些核心框架，全部是 Python 优先。Java 生态有 Spring AI，但功能覆盖和社区活跃度差距还很大。

一句话总结：**Python 赢在生态，不在语言本身。**

#### 技术栈全景

请大家看 `diagrams/01-python-ai-ecosystem.md` 这张图。

我把 Python AI 技术栈分成六层：

- **应用层**：FastAPI（Web 框架，类比 Spring Boot）、Pydantic（数据校验，类比 Bean Validation）、AsyncIO（异步，类比 CompletableFuture）
- **LLM 编排层**：LangChain（通用框架）、LangGraph（Agent 工作流）、LlamaIndex（RAG 专用）
- **多 Agent 层**：AutoGen（微软）、CrewAI（角色扮演）
- **RAG 基础设施层**：Embedding Model + 向量数据库（Milvus/Chroma/FAISS）
- **协议与标准层**：MCP（工具标准化）、A2A（Agent 通信）、Tool Calling
- **工程化层**：Harness、Skill、Evaluation、Observability

不需要现在记住每一个名词。接下来 80 分钟我们会逐一讲解。

---

## 第 2 章：LLM 应用开发基础（8 min）

### 时间安排
- 3 min：核心概念
- 2 min：应用架构
- 3 min：代码讲解

### 讲师话术

#### 核心概念

先过一遍 LLM 应用的核心概念，每个用一句话解释：

- **Prompt**：给 LLM 的输入指令。类比 Java 里的方法参数。
- **Token**：LLM 处理的最小单位，大约 1 个英文单词 = 1.3 个 Token，1 个中文字 = 1.5 个 Token。
- **Context Window**：LLM 单次能处理的最大 Token 数。GPT-4o 是 128K，Claude 3.5 是 200K。类比 Java 的缓冲区大小。
- **Embedding**：把文本转成向量（一个浮点数数组），捕捉语义信息。语义相近的文本，向量距离也近。
- **Retrieval**：从知识库中检索相关信息，给 LLM 做参考。
- **Tool Calling**：让 LLM 调用外部工具（函数）。LLM 不执行代码，只决定"调用什么工具、传什么参数"，应用层负责执行。
- **Memory**：对话记忆。LLM 本身无状态（每次调用独立），Memory 由应用层维护。类比 HttpSession。
- **Agent**：LLM + Tool + 推理循环。Agent 能自主决定"下一步做什么"，不只是单次问答。

#### 应用架构

请看 `diagrams/02-llm-app-architecture.md`。

整个 LLM 应用架构是：

```
User → Application → LLM → Tools → Knowledge → Memory
```

类比 Java 后端：

```
Client → Controller → Service → External API → Cache → DB
```

LLM 应用不是什么全新物种，它仍然是一个分层架构。只是 Service 层变成了 LLM，External API 变成了 Tools，Cache 变成了 Memory，DB 变成了 Vector DB。

#### 代码讲解

请看 `examples/01-llm-basics.py`。

这个文件展示了四个核心概念：

**1. 基础 LLM 调用**：就是发一个 HTTP 请求给 OpenAI API，传入 messages 列表和参数。`temperature` 控制随机性，0 是确定性输出，1 是高随机性。

**2. 多轮对话与 Memory**：关键点——"记忆"本质上是把历史消息全部重新发给 LLM。LLM 本身无状态，记忆在应用层。这和 HTTP 一样：协议无状态，Session 在服务端。

**3. Tool Calling**：这是最重要的概念。流程是两步：
- 第一步：把工具列表（Schema）发给 LLM，LLM 决定"我要调用 get_weather 工具，参数是 {"city": "北京"}"
- 第二步：应用层执行工具，把结果返回给 LLM，LLM 再生成自然语言回答

`**function_args` 是 Python 的字典解包语法，等价于 `get_weather(city="北京")`。Java 没有直接等价物，类似反射调用。

**4. Embedding**：把文本变成向量。这个向量就是一串浮点数（如 1536 维），捕捉了文本的语义。这是向量检索的基础。

---

## 第 3 章：LangChain 核心架构（10 min）

### 时间安排
- 2 min：LangChain 解决什么问题
- 3 min：核心组件
- 3 min：LCEL 语法
- 2 min：代码讲解

### 讲师话术

#### LangChain 解决什么问题

如果没有 LangChain，你会面对这些问题：
- 每个 LLM 提供商的 API 不同，换模型要改大量代码
- Prompt 管理散落在各处，没有统一模板
- 工具集成方式不统一
- RAG 管线需要自己拼装

LangChain 提供了**统一的抽象层 + 可组合的组件**。类比一下：**LangChain 之于 AI 应用，就像 Spring 之于 Java 后端**。

#### 核心组件

请看 `diagrams/03-langchain-stack.md`。

LangChain 的核心组件：

| 组件 | 职责 | Java 类比 |
|------|------|-----------|
| Chat Model | LLM 调用封装 | HTTP Client |
| Prompt Template | 提示词模板 | Thymeleaf 模板 |
| Runnable | 统一执行接口 | Functional Interface |
| Chain | 组件组合管道 | Stream Pipeline |
| Retriever | 知识检索 | Repository / DAO |
| Vector Store | 向量存储 | ES Client |
| Memory | 对话状态 | HttpSession |
| Tool | 外部能力 | Service Method |
| Agent | 自主决策 | 状态机 + Service |

#### LCEL 语法

LangChain 的核心创新是 **LCEL（LangChain Expression Language）**。它用 `|` 运算符组合组件：

```python
chain = prompt | model | parser
```

这和 Java Stream API 非常像：

```java
stream.filter(...).map(...).collect(...)
```

`|` 是 Python 的位或运算符，LangChain 重载了它。每个组件都实现了 `Runnable` 接口（有 `invoke`、`stream`、`batch` 方法），所以可以自由组合。

#### 代码讲解

请看 `examples/02-langchain-basics.py`。

重点看几个部分：

**Chain 组合**：`chain = prompt | model | parser`。调用 `chain.invoke({"role": "...", "question": "..."})` 时，数据依次流过 prompt → model → parser。

**Tool 定义**：`@tool` 装饰器把普通函数注册为工具。装饰器是 Python 的语法，类似 Java 注解。LangChain 自动从函数签名和 docstring 提取工具描述。

**Agent**：`create_tool_calling_agent` 创建一个推理循环。`AgentExecutor` 管理这个循环：LLM 思考 → 调用工具 → 观察结果 → 继续思考 → ... → 最终回答。

**Pydantic Model**：`BaseModel` 是 Python 的数据校验类，类似 Java 的 DTO + Bean Validation。`with_structured_output` 让 LLM 返回符合 Pydantic Model 的结构化数据。

---

## 第 4 章：LangGraph Agent 工作流（10 min）

### 时间安排
- 2 min：为什么需要 LangGraph
- 3 min：核心概念
- 3 min：代码讲解
- 2 min：高级特性

### 讲师话术

#### 为什么需要 LangGraph

LangChain 的 Chain 是**线性的**：prompt → model → parser，一条路走到底。

但真实的 Agent 需要什么？需要**循环**（执行完工具再回来思考）、**条件分支**（根据结果决定下一步）、**状态管理**（在多个步骤间共享数据）。

LangGraph 就是为这个设计的。**LangGraph = 状态机 + Agent**。类比 Java：LangGraph 之于 LangChain，就像 Spring StateMachine 之于普通 Spring Service。

#### 核心概念

请看 `diagrams/04-langgraph-workflow.md`。

四个核心概念：

- **Graph**：工作流图，包含所有节点和边
- **Node**（节点）：执行单元，每个 Node 是一个函数，接收 State、返回 State 更新
- **Edge**（边）：控制流，可以是固定的（A → B）或条件的（根据 State 决定走 B 还是 C）
- **State**（状态）：在节点间共享的数据容器，使用 TypedDict 定义

关键机制是**条件边**：`reflect` 节点执行完后，调用 `should_continue` 函数判断"任务是否完成"。如果没完成，回到 `execute` 节点继续（形成循环）。如果完成了，进入 `synthesize` 节点输出结果。

#### 代码讲解

请看 `examples/03-langgraph-agent.py`。

重点理解几个部分：

**State 定义**：`AgentState` 是一个 `TypedDict`，定义了在所有节点间共享的数据。`Annotated[list, add_messages]` 表示 `messages` 字段使用 `add_messages` reducer 来合并更新——新消息是追加而不是替换。

**Node 函数**：每个 Node 接收 State，返回 State 的**部分更新**。不需要返回完整 State，LangGraph 会自动合并。

**条件边**：`workflow.add_conditional_edges("reflect", should_continue, {...})`。`should_continue` 函数返回字符串 `"execute"` 或 `"synthesize"`，LangGraph 根据返回值路由到对应节点。

**循环控制**：`iterations` 字段记录循环次数，`should_continue` 里检查 `iterations >= 3` 防止无限循环。

#### 高级特性

LangGraph 还支持：
- **Human-in-the-loop**：中断执行，等待人工确认后再继续。比如危险操作前让用户确认。
- **Checkpointing**：保存状态快照，支持失败恢复和回退。
- **并行节点**：多个节点同时执行，汇总结果。

---

## 第 5 章：RAG 技术体系（10 min）

### 时间安排
- 2 min：RAG 是什么
- 3 min：完整管线
- 3 min：代码讲解
- 2 min：高级 RAG

### 讲师话术

#### RAG 是什么

RAG = Retrieval-Augmented Generation = 检索增强生成。

解决一个核心问题：**LLM 的知识有截止日期，且不了解企业内部数据。**

RAG 的思路：先从知识库检索相关信息，再把信息塞到 Prompt 里，让 LLM 基于这些信息回答。

类比：开卷考试。LLM 是考生，知识库是课本，RAG 是"先翻书找相关段落，再答题"。

#### 完整管线

请看 `diagrams/05-rag-pipeline.md`。

RAG 分三个阶段：

**1. 索引阶段（离线）**：
```
文档 → 加载 → 切分(Chunk) → 向量化(Embedding) → 存入向量数据库
```

**2. 检索阶段（在线）**：
```
用户问题 → 向量化 → 向量数据库相似度搜索 → Top-K 结果 → 重排序
```

**3. 生成阶段（在线）**：
```
用户问题 + 检索到的文档 → 组装 Prompt → LLM 生成回答
```

向量数据库选型：
- **Milvus**：分布式、高性能，生产环境首选。类比 Elasticsearch Cluster。
- **Chroma**：轻量、易用，适合原型。类比 H2 Database。
- **FAISS**：Facebook 开源，本地极快，适合研究。类比 Lucene 索引。

#### 代码讲解

请看 `examples/04-rag-pipeline.py`。

这个文件展示了完整 RAG 管线：

**文档加载**：`TextLoader` 加载文本文件。LangChain 还有 `PyPDFLoader`、`WebBaseLoader` 等。

**文本切分**：`RecursiveCharacterTextSplitter` 按 `["\n\n", "\n", "。", "，", " ", ""]` 优先级递归切分。`chunk_overlap=30` 表示块之间有 30 字符重叠，避免在切分处丢失上下文。

**向量化 + 存储**：`Chroma.from_documents` 一步完成向量化和存储。

**RAG Chain**：
```python
rag_chain = {"context": retriever | format_docs, "question": passthrough} | prompt | llm | parser
```
这条链的含义：检索文档 → 格式化 → 组装 Prompt → LLM 生成 → 解析输出。

#### 高级 RAG

**Hybrid Search（混合检索）**：关键词检索（BM25）+ 向量检索，分数融合。向量检索擅长语义匹配，关键词检索擅长精确匹配，取长补短。

**Rerank（重排序）**：用 Cross-Encoder 模型对检索结果重新打分排序，提升精度。向量检索是"双塔模型"（查询和文档独立编码），Rerank 是"交叉模型"（查询和文档一起编码），精度更高但速度更慢。

---

## 第 6 章：MCP 协议（8 min）

### 时间安排
- 3 min：MCP 解决什么问题
- 3 min：架构与代码讲解
- 2 min：对比传统集成

### 讲师话术

#### MCP 解决什么问题

在 MCP 出现之前，每个 LLM 应用集成工具的方式都不一样。应用 A 用自定义接口，应用 B 用另一套接口。同样的"搜索数据库"功能，在三个不同应用里要实现三遍。

MCP（Model Context Protocol）解决这个问题：**标准化工具暴露协议**。

类比：**MCP 之于工具调用，就像 JDBC 之于数据库访问**。在 JDBC 之前，每个数据库都有自己的驱动 API；JDBC 统一了接口，一次编写，处处运行。MCP 做的是同样的事。

#### 架构与代码讲解

请看 `diagrams/06-mcp-architecture.md` 和 `examples/05-mcp-server.py`。

MCP 架构：

```
LLM 应用 ↔ MCP Client ↔ MCP Server ↔ 外部资源(DB/API/File)
```

- **MCP Server**：实现工具能力，向 Client 暴露 Tool / Resource / Prompt
- **MCP Client**：内嵌在 LLM 应用中，发起工具调用请求
- **Transport**：通信层，支持 stdio / SSE / HTTP

代码重点：

`@server.list_tools()` 装饰器注册工具列表处理器。当 MCP Client 连接时，调用这个函数获取可用工具列表。

`@server.call_tool()` 装饰器注册工具调用处理器。当 LLM 决定调用工具时，MCP Client 发送请求到这里。

`async/await`：Python 异步语法，类似 Java 的 CompletableFuture。MCP Server 通常用异步实现以支持并发请求。

#### 对比传统集成

```
传统方式：                          MCP 方式：
App A → 自定义集成 → HR API         App A → MCP Client ─┐
App B → 自定义集成 → HR API         App B → MCP Client ─┼→ MCP Server → HR API
App C → 自定义集成 → HR API         App C → MCP Client ─┘
（重复工作）                        （一次实现，处处可用）
```

---

## 第 7 章：A2A 多 Agent 协作（7 min）

### 时间安排
- 2 min：为什么需要多 Agent
- 3 min：协作模式与代码讲解
- 2 min：架构图

### 讲师话术

#### 为什么需要多 Agent

单个 Agent 的问题：
1. **能力有限**：一个 Agent 很难同时擅长编码、测试、审查、部署
2. **上下文膨胀**：所有任务挤在一个 Agent 的上下文里，Token 消耗快速增长
3. **耦合度高**：一个环节出错影响全局

解决方案：**多 Agent 分工协作**。每个 Agent 专注一个领域，通过消息传递协调。

类比：**微服务架构**。单体应用拆分成多个服务，每个服务专注一个业务领域，通过 API/消息队列通信。

#### 协作模式与代码讲解

请看 `diagrams/07-a2a-collaboration.md` 和 `examples/06-a2a-collaboration.py`。

三种协作模式：

**1. 中心化协调（Coordinator 模式）**：一个协调 Agent 负责任务分发和结果汇总。代码里展示的就是这种模式——Coordinator 拆解任务，依次委派给 Research Agent、Coding Agent、Testing Agent。

**2. 去中心化协作（Peer-to-Peer 模式）**：Agent 之间直接通信，通过共享状态协调。

**3. 流水线模式（Pipeline 模式）**：Agent 按顺序处理，前一个的输出是后一个的输入。

代码里的核心概念：

**AgentMessage**：Agent 间通信的消息结构，包含发送方、接收方、消息类型、内容。

**Task**：任务定义，包含描述、分配的 Agent、状态、依赖。

**Coordinator.execute_workflow**：完整的协作流程——任务拆解 → 按依赖顺序执行 → Agent 间传递上下文 → 结果汇总。

#### A2A 核心机制

| 机制 | 说明 | Java 类比 |
|------|------|-----------|
| Agent Communication | Agent 间消息传递 | 微服务间通信 |
| Agent Discovery | 能力注册与发现 | 服务注册发现 |
| Task Delegation | 任务委派与结果回收 | 任务分发 |
| Result Aggregation | 结果汇总整合 | 响应聚合 |

---

## 第 8 章：Harness 工程基础设施（7 min）

### 时间安排
- 2 min：什么是 Harness
- 3 min：核心组成与代码讲解
- 2 min：Coding Agent Harness

### 讲师话术

#### 什么是 Harness

**Agent Harness 是让 Agent 稳定运行的工程基础设施。**

为什么需要？因为 LLM 有不确定性：
- 上下文可能溢出 → 需要上下文管理
- LLM 可能编造信息（幻觉）→ 需要质量评估
- LLM 可能调用危险操作 → 需要权限控制
- Token 消耗可能失控 → 需要成本监控
- 输出质量波动 → 需要评估和回归测试

类比：
- **JVM 之于 Java 应用** → Harness 之于 AI Agent
- **Kubernetes 之于微服务** → Harness 之于 Agent

没有 Harness 的 Agent = 一个 main 方法直接调 API 的程序。
有 Harness 的 Agent = 运行在完整框架和监控体系中的企业应用。

#### 核心组成与代码讲解

请看 `diagrams/08-harness-engineering.md` 和 `examples/07-harness-runtime.py`。

Harness 七大组件：

| 组件 | 职责 | 解决的问题 |
|------|------|-----------|
| Context Manager | 上下文窗口管理 | 上下文溢出 |
| Memory | 短期/长期记忆 | 跨会话记忆 |
| Tool Sandbox | 工具执行沙箱 | 工具安全 |
| Permission | 权限控制 | 危险操作 |
| Evaluation | 质量评估 | 输出质量 |
| Monitoring | 可观测性 | 调试和监控 |
| Feedback | 反馈闭环 | 持续改进 |

代码重点：

**TokenCounter**：跟踪 Token 使用量，超过 80% 时触发压缩。这是成本控制的基础。

**ContextManager._compress**：上下文压缩策略——保留 System 消息 + 最近 4 条消息 + 较早消息的摘要。这是上下文管理的核心。

**ToolSandbox.execute**：安全执行工具——权限检查 → 执行 → 异常捕获 → 记录日志。危险操作需要 `require_approval=True` 人工审批。

**Tracer**：调用链追踪，记录每一步操作的耗时和结果。类比 SkyWalking。

#### Coding Agent Harness

以 Claude Code 和 Codex 为例，Coding Agent 的 Harness 需要：

1. **上下文管理**：管理代码文件上下文（可能几十个文件），决定哪些代码放入 LLM 上下文
2. **工具沙箱**：安全执行 LLM 生成的代码，防止破坏性操作
3. **Git 集成**：自动管理代码变更，支持回退
4. **监控**：追踪每个工具调用、每次 LLM 调用的耗时和结果

---

## 第 9 章：Agent Skill 能力封装（5 min）

### 时间安排
- 2 min：Skill 概念
- 2 min：代码讲解
- 1 min：对比总结

### 讲师话术

#### Skill 概念

**Skill 是 Agent 能力的封装方式。**

一个 Skill 包含：
- **Procedure**：执行流程（步骤定义）
- **Prompt**：技能专属提示词
- **Tool**：技能所需工具
- **Knowledge**：领域知识
- **Validation**：输出校验规则

Skill vs Tool vs Prompt 的区别：

| 维度 | Prompt | Tool | Skill |
|------|--------|------|-------|
| 本质 | 文本指令 | 函数 | 完整能力封装 |
| 粒度 | 单次交互 | 单个操作 | 完整工作流 |
| 复杂度 | 低 | 中 | 高 |
| 可复用 | 低 | 中 | 高 |

类比：Tool 是一个方法，Prompt 是一段配置，Skill 是一个 Spring Bean（包含依赖、方法、配置、校验）。

#### 代码讲解

请看 `examples/08-agent-skill.py`。

四个具体 Skill：

**CodingSkill**：编码技能，包含编码规范 Prompt + 代码执行 Tool + Python 编码 Knowledge。

**DatabaseSkill**：数据库技能，包含 SQL 生成 Prompt + 数据库 Schema Knowledge + 只读安全约束。

**TestingSkill**：测试技能，包含 pytest 模板 + 测试代码 + 覆盖率要求。

**DeploymentSkill**：部署技能，包含 Dockerfile 模板 + K8s 配置 + 安全规范。

**SkillRouter**：技能路由器，根据用户请求匹配技能。实际应用中用 LLM + Embedding 做智能匹配。

**SkillOrchestrator**：技能编排器，将多个 Skill 组合完成复杂任务。例如"实现 API 并写测试"→ CodingSkill → TestingSkill。

---

## 第 10 章：企业级 Agent 架构（5 min）

### 讲师话术

请看 `diagrams/10-enterprise-architecture.md`。

企业级 Agent 架构六层：

```
接入层 → 应用层 → 核心服务层 → 协议层 → 工程层 → 企业系统层
```

每层职责：

- **接入层**：API Gateway，认证、限流、路由
- **应用层**：Session Manager、Skill Router、Workflow Engine
- **核心服务层**：LLM Service、Memory、Knowledge (RAG)、Tool Hub
- **协议层**：MCP Client（工具标准化）、A2A Hub（Agent 通信）
- **工程层**：Harness、Evaluation、Observability、Security
- **企业系统层**：DB、ERP、Git、CI/CD、Wiki、Slack/飞书

关键设计决策：

**模型选型**：通用对话用 GPT-4o，代码生成用 Claude 3.5，中文场景用 GLM-4，成本敏感用开源模型。

**上下文策略**：短对话全量保留，长对话摘要压缩 + 向量检索，关键信息写入长期记忆。

**工具治理**：注册制（工具必须注册才能调用）、权限分级（只读/读写/危险操作）、审计日志。

**安全合规**：输入过滤（防 Prompt Injection）、输出审查（敏感信息过滤）、数据隔离。

代码示例见 `examples/09-enterprise-pattern.py`，展示了完整的企业级执行流程。

---

## 第 11 章：综合案例（5 min）

### 讲师话术

最后用两个案例串联所有知识点。

请看 `diagrams/11-combined-cases.md`。

#### 案例 1：企业知识库 Agent

**架构**：Python + LangChain + RAG + Vector DB (Milvus) + MCP

**数据流**：
1. 企业文档（PDF/Word/Wiki）→ 切分 → 向量化 → 存入 Milvus
2. 用户提问 → 向量检索 + 关键词检索（Hybrid Search）→ Rerank
3. 检索结果 + 用户问题 → LLM 生成回答（附带引用来源）
4. 通过 MCP 接入 HR/Finance 系统，回答需要实时数据的问题

**技术选型理由**：
- LangChain：成熟的 RAG 管线支持
- Milvus：企业级分布式向量数据库
- Hybrid Search + Rerank：提高检索精度
- MCP：标准化接入企业系统

#### 案例 2：软件研发 Agent

**架构**：Python + LangGraph + Skill + Tool Calling + Harness

**工作流**：
1. 用户提需求 → Coordinator Agent 拆解任务
2. CodingSkill 生成代码 → Tool Sandbox 安全执行
3. TestingSkill 生成测试 → 执行测试 → 反馈结果
4. ReviewSkill 代码审查 → 如需修改回到编码步骤
5. DeploymentSkill 生成部署配置

**技术选型理由**：
- LangGraph：支持循环（代码→测试→审查→修改→再测试）
- Skill：编码/测试/审查/部署封装为可复用技能
- Harness：上下文管理（代码文件）+ 工具沙箱（安全执行）+ 监控

#### 两个案例对比

| 维度 | 知识库 Agent | 研发 Agent |
|------|-------------|-----------|
| 核心能力 | 检索 + 问答 | 代码生成 + 执行 |
| 编排框架 | LangChain | LangGraph |
| RAG 需求 | 重 | 轻 |
| 工具复杂度 | 低 | 高 |
| Harness 需求 | 中 | 高 |
| 安全要求 | 数据隔离 | 代码执行安全 |

---

## Q&A 与学习路线（10 min）

### 讲师话术

#### 技术地图回顾

11 个章节，一张图：

```
Python AI 生态
├── 基础：FastAPI + Pydantic + AsyncIO
├── 编排：LangChain（Chain）→ LangGraph（Graph + State）
├── 知识：RAG（Embedding + Vector DB + Retriever）
├── 工具：MCP（标准化协议）→ Tool Calling
├── 协作：A2A（多 Agent 协作）
├── 工程化：Harness（运行时）+ Skill（能力封装）
└── 企业级：+ Evaluation + Observability + Security
```

#### 学习路线建议

```
阶段 1（1-2 周）：Python AI 基础
  └─ FastAPI + Pydantic + AsyncIO + OpenAI SDK
  └─ 目标：能写一个简单的 LLM 调用 API 服务

阶段 2（2-3 周）：LangChain 生态
  └─ LangChain + LangGraph + RAG 基础
  └─ 目标：能构建一个 RAG 问答应用 + 简单 Agent

阶段 3（2-3 周）：Agent 工程化
  └─ MCP + Tool Calling + Skill + Harness
  └─ 目标：能构建一个工具增强的 Agent 应用

阶段 4（持续）：实战项目
  └─ 企业知识库 Agent / 研发辅助 Agent
  └─ 目标：完整的企业级 Agent 应用
```

#### 推荐资源

- LangChain 官方文档：https://python.langchain.com
- LangGraph 文档：https://langchain-ai.github.io/langgraph
- LlamaIndex 文档：https://docs.llamaindex.ai
- MCP 规范：https://modelcontextprotocol.io
- OpenAI API 文档：https://platform.openai.com/docs

#### 核心思维转变

最后一点，从 Java 后端到 AI 应用开发，最大的转变不是语言，而是思维方式：

1. **从确定性到概率性**：Java 方法给定输入一定有确定输出。LLM 给定输入，输出有随机性。工程上需要用 Evaluation 和 Guardrails 来兜底。

2. **从代码逻辑到 Prompt 工程**：以前你用 if-else 控制流程，现在你用 Prompt 引导 LLM 的推理方向。

3. **从单体到 Agent 编排**：以前一个 Service 方法搞定的事，现在可能需要拆成多个 Agent 协作。

4. **从功能测试到质量评估**：以前你测功能对不对，现在你还要评估回答好不好、准不准、安全不安全。

这就是为什么需要今天讲的这些组件——它们都是在解决 LLM 应用工程化的问题。

谢谢大家。有问题欢迎提问。

---

## 附录：术语速查表

| 术语 | 全称 | 一句话解释 |
|------|------|-----------|
| LLM | Large Language Model | 大语言模型，通用语言理解与生成引擎 |
| RAG | Retrieval-Augmented Generation | 检索增强生成，给 LLM 外挂知识库 |
| MCP | Model Context Protocol | 模型上下文协议，工具调用标准化接口 |
| A2A | Agent to Agent | Agent 间通信协议 |
| Harness | — | Agent 运行时基础设施 |
| Skill | — | Agent 能力封装 |
| Embedding | — | 文本的向量语义表示 |
| Vector DB | Vector Database | 向量数据库，语义相似度搜索引擎 |
| Tool Calling | — | LLM 调用外部工具 |
| Context Window | — | LLM 单次处理的最大 Token 数 |
| LCEL | LangChain Expression Language | LangChain 声明式管道语法 |
| ReAct | Reasoning + Acting | 推理+行动的 Agent 模式 |
| Prompt Template | — | 参数化的提示词模板 |
| Chunk | — | 文档切分后的文本块 |
| Rerank | — | 检索结果重排序 |
| Hybrid Search | — | 关键词+向量混合检索 |
| Human-in-the-loop | — | 人工介入的 Agent 工作流 |
