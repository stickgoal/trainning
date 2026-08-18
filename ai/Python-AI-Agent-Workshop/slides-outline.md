# 幻灯片大纲 - Python AI Agent 开发前沿技术深度工作坊

> 共约 80 张幻灯片 | 4 小时半天工作坊  
> 每张幻灯片标注核心内容和讲解要点

---

## 开场（3 张）

### Slide 1: 课程封面
- 标题：Python AI Agent 开发前沿技术深度工作坊
- 副标题：面向 Java 后端工程师 · 半天系统性技术扫盲
- 讲师 / 日期 / 场地

### Slide 2: 课程定位与承诺
- 不是什么：不是 Python 语法课、不是 ML 理论课
- 是什么：Python AI 应用工程化深度工作坊
- 核心承诺：课后能看懂任何 AI Agent 项目的架构
- 4 大学习成果

### Slide 3: 课程议程
- 上半场（105 min）：生态 → LLM → LangChain → LangGraph → RAG
- 休息 15 min
- 下半场（100 min）：MCP → A2A → Harness → Skill → 企业架构 → 案例
- 休息 15 min
- Q&A（20 min）
- 时间分配总览表

### Slide 4: 互动调研
- 举手统计：用过 OpenAI API？写过 LangChain？做过 RAG？做过 Agent？
- 根据调研结果调整讲解深度

---

## 第 1 章：Python AI 生态全景（8 张）

### Slide 5: 为什么是 Python？——不是语言层面的胜利
- Python 性能不如 Java、类型系统不如 Java 严谨
- 但 Python 赢了——赢在生态，不在语言
- 四个结构性优势预览

### Slide 6: 优势 1——AI SDK 优先支持
- 时间线对比：OpenAI SDK 发布时间（Python vs Java）
- Anthropic SDK 同样 Python 优先
- Java 版本通常是社区第三方封装，滞后 3-6 个月
- 影响：最新模型能力 Python 先用上

### Slide 7: 优势 2——快速实验能力
- AI 开发核心循环：改 Prompt → 跑一次 → 看结果 → 再改
- Python REPL + Jupyter Notebook：以秒为单位迭代
- Java：改代码 → 编译 → 运行 → 看日志，慢一个数量级
- 为什么这很重要：AI 开发 80% 的时间在调 Prompt

### Slide 8: 优势 3&4——数据处理 + LLM 工具链
- 数据处理：NumPy / Pandas / Matplotlib 三件套 vs Java 数据处理碎片化
- LLM 工具链：LangChain / LlamaIndex / AutoGen 全部 Python 优先
- Java 的 Spring AI：定位正确但功能覆盖和社区活跃度差距大

### Slide 9: Python AI 技术栈六层模型
- 应用层：FastAPI / Pydantic / AsyncIO
- LLM 编排层：LangChain / LangGraph / LlamaIndex
- 多 Agent 层：AutoGen / CrewAI
- RAG 基础设施层：Embedding / Vector DB / Retriever / Reranker
- 协议与标准层：MCP / A2A / Tool Calling
- 工程化层：Harness / Skill / Evaluation / Observability
- 每层标注：今天在哪个章节讲

### Slide 10: Java vs Python 技术对照表
| 维度 | Java 方案 | Python 方案 | 差距 |
|------|----------|------------|------|
| Web 框架 | Spring Boot | FastAPI | 平 |
| 数据校验 | Bean Validation | Pydantic | 平 |
| 异步 | CompletableFuture | AsyncIO | 平 |
| LLM 编排 | Spring AI | LangChain | Python 领先 |
| Agent 工作流 | 无成熟方案 | LangGraph | Python 领先 |
| 向量检索 | Spring Data + 自建 | LangChain + Milvus | Python 领先 |
| 工具协议 | 无标准 | MCP | Python 领先 |

### Slide 11: 技术选型决策框架
- 项目规模 → 框架选择决策树
- 小项目：OpenAI SDK 直接调用
- 中项目：LangChain + RAG
- 大项目：LangGraph + MCP + Harness + Skill
- 团队能力 → 渐进式引入路径

### Slide 12: 本章小结与思考题
- Python 赢在生态，不在语言
- 六层技术栈模型是后续章节的导航图
- 思考题：你们公司选 Python 还是 Java？决定因素是什么？

---

## 第 2 章：LLM 应用开发基础（10 张）

### Slide 13: 八大核心概念预览
- Prompt / Token / Context Window / Embedding
- Retrieval / Tool Calling / Memory / Agent
- 每个概念一句话定义
- "接下来逐个深入讲解"

### Slide 14: 概念详解——Prompt 与 Token
- Prompt：给 LLM 的输入指令，分为 System Prompt 和 User Prompt
- Token：LLM 处理的最小单位
  - 1 英文 word ≈ 1.3 Token
  - 1 中文字 ≈ 1.5 Token
  - Token 直接影响成本：GPT-4o 输入 $2.5/1M Token，输出 $10/1M Token
- 为什么 Token 重要：成本控制 + 上下文窗口限制

### Slide 15: 概念详解——Context Window 与 Embedding
- Context Window：LLM 单次处理最大 Token 数
  - GPT-4o: 128K | Claude 3.5: 200K | GPT-3.5: 16K
  - 超出窗口 → 需要 Memory 管理（第 8 章 Harness 讲）
- Embedding：文本 → 向量（浮点数数组）
  - 维度：text-embedding-3-small = 1536 维
  - 语义相近的文本，向量距离也近
  - 这是向量检索的基础（第 5 章 RAG 讲）

### Slide 16: 概念详解——Tool Calling 两步机制
- 核心理解：LLM 不执行代码，只做"决策"
- 第一步：应用发送工具列表 → LLM 返回"我要调用 get_weather，参数 {city: '北京'}"
- 第二步：应用执行工具 → 把结果返回给 LLM → LLM 生成自然语言回答
- 代码片段展示两步流程
- Java 类比：LLM 是 Manager（决策），应用是 Developer（执行）

### Slide 17: 概念详解——Memory 与 Agent
- Memory：LLM 本身无状态（每次调用独立）
  - "记忆" = 应用层把历史消息重新发给 LLM
  - 类比：HTTP 无状态 → Session 在服务端
- Agent：LLM + Tool + 推理循环
  - 不只是单次问答，能自主决定"下一步做什么"
  - ReAct 模式：Reasoning → Action → Observation → Repeat

### Slide 18: LLM 应用架构全景
- Mermaid 架构图：User → Application → LLM → Tools → Knowledge → Memory
- Java 类比：Client → Controller → Service → External API → Cache → DB
- 关键理解：LLM 应用仍然分层，Service 变成了 LLM

### Slide 19: LLM 应用成熟度模型
- Level 0：直接调 API（一次性问答）
- Level 1：Prompt 模板 + 上下文管理（多轮对话）
- Level 2：RAG + Tool Calling（知识增强 + 工具使用）
- Level 3：Agent 自主决策 + 多步推理（ReAct）
- Level 4：多 Agent 协作系统
- 每个等级配代码片段和适用场景

### Slide 20: 代码讲解——examples/01-llm-basics.py
- 代码结构：4 个部分（基础调用 / 多轮对话 / Tool Calling / Embedding）
- 重点 1：temperature 参数对输出的影响
- 重点 2：`**function_args` 字典解包语法
- 重点 3：异步调用 vs 同步调用

### Slide 21: 本章小结与思考题
- 8 个核心概念是后续所有章节的基础
- Tool Calling 两步机制是最重要的理解
- LLM 应用成熟度模型帮你定位项目阶段
- 思考题：Tool Calling 为什么是两步而不是一步？

---

## 第 3 章：LangChain 核心架构（12 张）

### Slide 22: LangChain 解决什么问题？
- 痛点 1：每个 LLM 提供商 API 不同，换模型要改大量代码
- 痛点 2：Prompt 散落在代码各处，没有统一管理
- 痛点 3：工具集成方式不统一
- 痛点 4：RAG 管线需要自己拼装
- LangChain 方案：统一抽象层 + 可组合组件
- Java 类比：Spring 之于 Java 后端

### Slide 23: LangChain 九大核心组件
- 组件全景图（Mermaid）
- Chat Model / Prompt Template / Runnable / Chain
- Retriever / Vector Store / Memory / Tool / Agent
- "接下来逐个讲解"

### Slide 24: Chat Model 与 Prompt Template
- Chat Model：LLM 调用封装
  - 支持 OpenAI / Anthropic / Google / 本地模型
  - 换模型只需改一行代码
  - 代码片段
- Prompt Template：参数化提示词
  - 变量注入：`{role}`, `{question}`
  - Java 类比：Thymeleaf / MessageFormat
  - 代码片段

### Slide 25: Runnable——LCEL 的基础
- Runnable 是统一执行接口：
  - `invoke()`：单次调用
  - `stream()`：流式输出
  - `batch()`：批量调用
- 所有 LangChain 组件都实现了 Runnable
- Java 类比：Functional Interface（Supplier / Function）

### Slide 26: Chain 与 LCEL 语法
- LCEL = LangChain Expression Language
- `|` 运算符组合组件：
  ```python
  chain = prompt | model | parser
  result = chain.invoke({"question": "..."})
  ```
- `|` 是 Python 位或运算符的重载
- Java 类比：Stream API 的 `.map().filter().collect()`
- 数据流向：prompt 的输出 → model 的输入 → model 的输出 → parser 的输入

### Slide 27: LCEL 高级组件
- RunnablePassthrough：透传输入
- RunnableLambda：包装普通函数为 Runnable
- RunnableParallel：并行执行多个 Runnable
- 代码片段：RAG Chain 中的使用

### Slide 28: Retriever 与 Vector Store
- Vector Store：向量存储，对接多种向量数据库
  - Chroma / Milvus / FAISS / Pinecone
  - `from_documents()` 一步完成向量化 + 存储
- Retriever：检索器接口
  - `as_retriever()` 从 Vector Store 创建
  - `invoke(query)` 返回相关文档
- Java 类比：Repository / DAO 模式

### Slide 29: Memory——对话状态管理
- LLM 无状态 → 记忆在应用层
- LangChain Memory 类型：
  - ConversationBufferMemory：全量保留
  - ConversationBufferWindowMemory：只保留最近 N 轮
  - ConversationSummaryMemory：摘要压缩
- Java 类比：HttpSession + 淘汰策略

### Slide 30: Tool 与 @tool 装饰器
- `@tool` 装饰器：把普通函数注册为工具
- 自动从函数签名和 docstring 提取工具描述
- 工具 Schema = 名称 + 描述 + 参数定义
- Java 类比：注解 + 反射
- 代码片段

### Slide 31: Agent——推理行动循环
- Agent = LLM + Tool + 推理循环
- ReAct 模式：Reasoning → Action → Observation → Repeat
- `create_tool_calling_agent` 创建 Agent
- `AgentExecutor` 管理循环：思考 → 调用工具 → 观察结果 → 继续思考
- `max_iterations` 防止无限循环
- 代码片段

### Slide 32: Pydantic 结构化输出
- `with_structured_output(PydanticModel)` 让 LLM 返回结构化数据
- 流程：Pydantic Model → JSON Schema → 约束 LLM 生成
- Java 类比：DTO + Bean Validation + JSON 序列化
- 代码片段

### Slide 33: 代码讲解与本章小结
- `examples/02-langchain-basics.py` 关键片段
- 本章覆盖：Chain / Tool / Agent / Pydantic
- 思考题：LangChain 的 Chain 和 Java Stream API 有什么本质区别？

---

## 第 4 章：LangGraph Agent 工作流（12 张）

### Slide 34: LangChain Chain 的局限性
- Chain 是线性的：prompt → model → parser，一条路走到底
- Agent 的真实需求：
  - 循环：执行完工具再回来思考
  - 条件分支：根据结果决定下一步
  - 状态管理：在多步骤间共享数据
- LangGraph 解决方案：状态机 + Agent

### Slide 35: LangGraph = 状态机 + Agent
- Java 类比：Spring StateMachine vs 普通 Service
- LangGraph 之于 LangChain = Spring StateMachine 之于 Spring Service
- 核心价值：支持循环、条件分支、状态管理

### Slide 36: 四大核心概念——State
- State：在所有节点间共享的数据容器
- TypedDict 定义字段和类型
- Reducer 机制：`Annotated[list, add_messages]` 的含义
  - 新消息追加而不是替换
  - Java 类比：Redux reducer 模式
- 部分更新：Node 只返回变更字段，LangGraph 自动合并

### Slide 37: 四大核心概念——Node 与 Edge
- Node（节点）：执行单元
  - 每个 Node 是一个函数：输入 State → 输出 State 更新
  - 类型：LLM 调用 / 工具执行 / 逻辑判断
- Edge（边）：控制流
  - 固定边：A → B（无条件跳转）
  - 条件边：根据 State 动态决定下一个 Node
  - `add_conditional_edges` 用法
- Java 类比：Transition + Guard Condition

### Slide 38: 四大核心概念——Graph
- StateGraph 构建器：add_node + add_edge + compile
- compile() 返回可执行图实例
- Java 类比：StateMachineBuilder.build()
- 代码片段展示构建过程

### Slide 39: ReAct Agent 工作流图
- Mermaid 工作流图：START → understand → execute → reflect → (循环 or synthesize) → END
- 每个节点的职责说明
- 条件边 should_continue 的判断逻辑

### Slide 40: 代码讲解——State 定义与 Node 函数
- `examples/03-langgraph-agent.py` 逐段讲解
- AgentState：messages / current_step / plan / results / iterations
- understand_node：理解用户意图
- execute_node：执行工具调用
- reflect_node：反思执行结果

### Slide 41: 代码讲解——Graph 构建与执行
- add_node：注册所有节点
- add_edge：连接节点（固定边 + 条件边）
- compile：编译为可执行图
- invoke：执行工作流
- 循环控制：iterations 防止无限循环

### Slide 42: 高级特性——Human-in-the-loop
- 中断执行 → 人工确认 → 继续执行
- 适用场景：危险操作前审批、关键决策确认
- 代码片段展示中断点设置

### Slide 43: 高级特性——Checkpointing 与并行
- Checkpointing：保存状态快照 → 失败恢复 → 时间旅行调试
- 并行节点：多个节点同时执行 → 汇总结果
- 子图：图的模块化组合

### Slide 44: LangGraph vs LangChain Agent 对比与小结
| 维度 | LangChain Agent | LangGraph |
|------|----------------|-----------|
| 流程 | 线性 | 图（循环+条件） |
| 状态管理 | 无 | State + Reducer |
| 人工介入 | 不支持 | 支持 |
| 适用场景 | 简单工具调用 | 复杂工作流 |
- 思考题：如果 Agent 一直无法完成任务，应该如何处理？

---

## 第 5 章：RAG 技术体系（10 张）

### Slide 45: RAG 核心原理
- 问题：LLM 知识有截止日期 + 不了解企业内部数据
- 方案：先检索相关知识 → 塞入 Prompt → LLM 基于知识回答
- 类比：开卷考试——LLM 是考生，知识库是课本
- 为什么不用 Fine-tuning：成本高、更新慢、不可解释

### Slide 46: RAG 完整管线全景
- 索引阶段（离线）：文档 → 加载 → 切分(Chunk) → 向量化(Embedding) → 存入向量数据库
- 检索阶段（在线）：用户问题 → 向量化 → 相似度搜索 → Top-K 结果
- 生成阶段（在线）：用户问题 + 检索文档 → 组装 Prompt → LLM 生成回答
- Mermaid 管线图

### Slide 47: 文档加载与文本切分
- 文档加载：TextLoader / PyPDFLoader / WebBaseLoader
- 文本切分：RecursiveCharacterTextSplitter
  - 递归切分：按 ["\n\n", "\n", "。", "，", " ", ""] 优先级
  - chunk_size：每块最大字符数
  - chunk_overlap：块间重叠，防止切分处丢失上下文
  - 中文友好分隔符配置
- 代码片段

### Slide 48: 向量化与向量存储
- Embedding：文本 → 浮点数向量
  - text-embedding-3-small：1536 维，便宜
  - text-embedding-3-large：3072 维，更精确
- 向量存储选型：
  - Chroma：轻量、嵌入式，开发原型（类比 H2）
  - Milvus：分布式、高性能，生产环境（类比 ES Cluster）
  - FAISS：本地极快，研究适用（类比 Lucene）
- 代码片段

### Slide 49: 检索与 RAG Chain 组装
- 检索：similarity_search，余弦相似度
- k 值选择：太少遗漏，太多噪声
- RAG Chain：
  ```python
  rag_chain = {"context": retriever | format_docs, "question": passthrough} | prompt | llm | parser
  ```
- 数据流图

### Slide 50: 代码讲解——examples/04-rag-pipeline.py
- 完整管线代码逐段讲解
- 从文档创建 → 切分 → 向量化 → 检索 → 问答
- 重点：format_docs 函数的作用

### Slide 51: 高级 RAG——Hybrid Search
- 向量检索擅长语义匹配（"住宿标准" → "酒店费用上限"）
- 关键词检索（BM25）擅长精确匹配（"500元" → 包含 "500"）
- Hybrid Search = 两者结合 + 分数融合（Reciprocal Rank Fusion）
- 代码概念示意

### Slide 52: 高级 RAG——Rerank 与 Chunk 策略
- Rerank：Cross-Encoder 重新打分排序
  - 双塔模型 vs 交叉模型的区别
  - 精度更高但速度慢
- Chunk 策略：
  - 固定大小切分（简单但可能破坏语义）
  - 语义切分（按段落/句子）
  - Parent-Child 分块（小块检索，大块提供上下文）
- 查询改写：用户问题 → LLM 改写为更适合检索的查询

### Slide 53: RAG 评估与向量数据库选型
- 评估指标：
  - 检索质量：Recall@K / MRR / NDCG
  - 生成质量：Faithfulness（忠实度）/ Answer Relevance（回答相关性）
  - 端到端：Ragas 框架
- 选型决策矩阵
- 思考题：chunk_size 设太大或太小分别有什么问题？

### Slide 54: 本章小结
- RAG 三阶段：索引 / 检索 / 生成
- 核心决策：Chunk 策略 / 向量数据库 / 检索方式 / 是否 Rerank
- 高级技术：Hybrid Search / Rerank / 查询改写

---

## — 休息 15 min —

---

## 第 6 章：MCP 协议（10 张）

### Slide 55: MCP 解决什么问题？
- 传统痛点：同一个 HR 系统，三个应用三种集成方式
  - App A → 自定义 REST API
  - App B → GraphQL
  - App C → gRPC
  - 重复工作，无法复用
- MCP 方案：标准化工具暴露协议
- 类比：MCP = JDBC for Tool Calling = USB for Hardware

### Slide 56: MCP 架构三角色模型
- MCP Server：实现工具能力，暴露 Tool / Resource / Prompt
- MCP Client：内嵌在 LLM 应用中，发起调用请求
- Transport：通信层（stdio / SSE / HTTP）
- Mermaid 架构图

### Slide 57: MCP 通信流程
1. LLM 应用启动 → MCP Client 连接 MCP Server
2. MCP Client 调用 list_tools() 获取工具列表
3. LLM 决定调用工具 → MCP Client 发送 call_tool 请求
4. MCP Server 执行工具 → 返回结果
5. LLM 根据结果生成最终回答
- 流程图

### Slide 58: MCP Tool 定义与 Schema
- Tool Schema = 名称 + 描述 + 参数定义（JSON Schema）
- 与 OpenAI Function Calling 格式一致
- 代码片段展示三个 Tool 定义

### Slide 59: MCP Server 代码讲解
- `examples/05-mcp-server.py` 逐段讲解
- `@server.list_tools()` 装饰器
- `@server.call_tool()` 装饰器
- `async/await` 异步处理
- 安全职责：输入校验、权限检查

### Slide 60: MCP 安全设计
- 输入校验：参数合法性检查
- 权限分级：safe / moderate / dangerous
- 危险操作拦截：require_approval
- 审计日志：所有工具调用记录
- Java 类比：SecurityManager

### Slide 61: MCP vs 传统工具集成对比
- 对比图：传统方式 vs MCP 方式
- MCP 核心价值：解耦、复用、标准化
- 何时用 MCP：多应用共享工具能力
- 何时不用：单一应用内部工具、快速原型

### Slide 62: MCP 生态现状
- Anthropic Claude 原生支持 MCP
- OpenAI 逐步支持
- 社区 MCP Server：数据库 / 文件系统 / Git / Slack / 飞书
- 与 OpenAI Function Calling 的关系：互补不互斥

### Slide 63: 本章小结与思考题
- MCP = 工具调用标准化协议
- 三角色：Server / Client / Transport
- 核心价值：一次实现，处处可用
- 思考题：已有 REST API 改造为 MCP Server 成本大吗？

---

## 第 7 章：A2A 多 Agent 协作（7 张）

### Slide 64: 为什么需要多 Agent？
- 单 Agent 问题：能力有限 / 上下文膨胀 / 耦合度高
- 多 Agent 优势：分工协作 / 上下文隔离 / 独立优化
- 类比：微服务架构——单体拆分成多个服务

### Slide 65: 三种协作模式
- 中心化协调（Coordinator）：一个协调者分发任务和汇总结果
- 去中心化（Peer-to-Peer）：Agent 直接通信，共享状态协调
- 流水线（Pipeline）：按顺序处理，前一个输出是后一个输入
- 每种模式的优缺点和适用场景

### Slide 66: Agent 通信机制
- AgentMessage：发送方 / 接收方 / 消息类型 / 内容
- Task：任务定义 / 依赖管理 / 状态追踪
- Agent Discovery：能力注册与发现
- Task Delegation：任务委派与结果回收

### Slide 67: 代码讲解——examples/06-a2a-collaboration.py
- BaseAgent 基类设计
- ResearchAgent / CodingAgent / TestingAgent 具体实现
- Coordinator 协调者：任务拆解 → 分发 → 汇总
- Agent 间上下文传递机制

### Slide 68: 框架对比与选型
| 框架 | 模式 | 特点 | 适用场景 |
|------|------|------|---------|
| LangGraph Multi-Agent | 图编排 | 灵活但复杂 | 复杂工作流 |
| AutoGen | 对话式 | 微软开源 | 对话驱动协作 |
| CrewAI | 角色扮演 | 简单直观 | 快速原型 |

### Slide 69: 本章小结与思考题
- 多 Agent = 分工协作，类比微服务
- 三种模式：Coordinator / P2P / Pipeline
- 框架选型：LangGraph / AutoGen / CrewAI
- 思考题：多 Agent 系统的成本如何控制？

---

## 第 8 章：Harness 工程基础设施（10 张）

### Slide 70: 什么是 Agent Harness？
- 定义：让 Agent 稳定运行的工程基础设施
- 为什么需要——LLM 的不确定性：
  - 上下文溢出 / 幻觉 / 危险操作 / Token 失控 / 质量波动
- 类比：JVM 之于 Java = Kubernetes 之于微服务 = Harness 之于 Agent
- 没有 Harness = 裸跑的脚本；有 Harness = 生产级系统

### Slide 71: Harness 七大组件总览
- Context Manager / Memory / Tool Sandbox
- Permission / Evaluation / Monitoring / Feedback
- 组件关系图（Mermaid）

### Slide 72: 组件详解——Context Manager 与 Memory
- Context Manager：
  - Token 计数和预算管理
  - 80% 阈值触发自动压缩
  - 压缩策略：保留 System + 最近 N 条 + 历史摘要
- Memory：
  - 短期记忆：会话内消息历史
  - 长期记忆：跨会话持久化
  - 记忆检索：按时间 / 相关性 / 重要性

### Slide 73: 组件详解——Tool Sandbox 与 Permission
- Tool Sandbox：
  - 安全执行：权限检查 → 执行 → 异常捕获 → 日志
  - 权限分级：safe / moderate / dangerous
  - 危险操作需要人工审批
- Permission：
  - 基于角色的工具访问控制
  - 输入过滤（防 Prompt Injection）
  - 输出审查（敏感信息过滤）

### Slide 74: 组件详解——Evaluation 与 Monitoring
- Evaluation：
  - 线上评估：实时质量打分
  - 离线评估：回归测试集
  - 评估维度：相关性 / 准确性 / 完整性 / 安全性
- Monitoring（可观测性）：
  - 调用链追踪：每步操作的耗时和结果
  - 指标收集：Token / 响应时间 / 工具调用次数
  - Java 类比：ELK + Prometheus + SkyWalking

### Slide 75: 组件详解——Feedback 闭环
- 用户显式反馈：评分 / 点赞踩
- 隐式反馈：是否采纳 / 是否重新提问
- 反馈驱动优化：Prompt 调整 / 工具改进 / 模型切换
- 持续改进循环

### Slide 76: 代码讲解——examples/07-harness-runtime.py
- TokenCounter：Token 估算算法
- ContextManager._compress：压缩策略实现
- ToolSandbox.execute：安全执行流程
- Tracer：调用链追踪
- AgentHarness：整合所有组件

### Slide 77: Coding Agent Harness 设计
- Claude Code 的 Harness：
  - 上下文管理：管理数十个代码文件
  - 工具沙箱：安全执行 LLM 生成的代码
  - Git 集成：自动管理代码变更
  - 文件系统操作：读写 / 搜索 / 创建文件
- 生产级 Coding Agent 的关键挑战

### Slide 78: 本章小结与思考题
- Harness = Agent 的工程基础设施
- 七大组件：Context / Memory / Sandbox / Permission / Evaluation / Monitoring / Feedback
- 没有 Harness 的 Agent 不能上生产
- 思考题：上下文压缩可能会丢失什么信息？

---

## 第 9 章：Agent Skill 能力封装（7 张）

### Slide 79: Skill 概念与设计原则
- Skill = Procedure + Prompt + Tool + Knowledge + Validation
- 设计原则：
  1. 单一职责——聚焦一个领域
  2. 自包含——包含所需全部资源
  3. 可组合——Skill 之间可以协作
  4. 可复用——跨 Agent 跨项目复用

### Slide 80: Skill vs Tool vs Prompt
| 维度 | Prompt | Tool | Skill |
|------|--------|------|-------|
| 本质 | 文本指令 | 函数 | 完整能力封装 |
| 粒度 | 单次交互 | 单个操作 | 完整工作流 |
| 复杂度 | 低 | 中 | 高 |
| 可复用 | 低 | 中 | 高 |
| Java 类比 | 配置 | 方法 | Spring Bean |

### Slide 81: 具体 Skill 实现示例
- CodingSkill：编码 Prompt + 代码执行 Tool + Python Knowledge
- DatabaseSkill：SQL Prompt + Schema Knowledge + 只读约束
- TestingSkill：pytest 模板 + 覆盖率要求
- DeploymentSkill：Dockerfile 模板 + K8s 配置
- 每个 Skill 的组件拆解图

### Slide 82: Skill Router 与 Orchestrator
- SkillRouter：根据用户请求匹配技能
  - 关键词匹配 / LLM 判断 / Embedding 匹配
- SkillOrchestrator：多 Skill 组合编排
  - 示例：编码 → 测试 → 部署
  - 上下文传递：前一个 Skill 的结果作为后一个的输入

### Slide 83: 代码讲解——examples/08-agent-skill.py
- SkillDefinition 数据结构
- BaseSkill 基类
- 四个具体 Skill 实现
- SkillRouter 路由逻辑
- SkillOrchestrator 编排逻辑

### Slide 84: Skill 复用与分享 + 本章小结
- Skill 作为可分享的标准化能力包
- Skill MarketPlace 概念
- 企业内部 Skill 库管理
- 思考题：你们公司的哪些业务能力适合封装为 Skill？

---

## 第 10 章：企业级 Agent 架构（7 张）

### Slide 85: 企业级 Agent 六层架构
- 接入层：API Gateway（认证/限流/路由）
- 应用层：Session Manager / Skill Router / Workflow Engine
- 核心服务层：LLM Service / Memory / Knowledge / Tool Hub
- 协议层：MCP Client / A2A Hub
- 工程层：Harness / Evaluation / Observability / Security
- 企业系统层：DB / ERP / Git / CI-CD / Wiki / Slack
- 完整架构图（Mermaid）

### Slide 86: 各层职责与组件交互
- 每层核心组件和职责说明
- 层间接口和交互方式
- 数据流向图
- Java 类比：Spring Boot 分层架构

### Slide 87: 关键架构决策——模型选型与上下文策略
- 模型选型：
  - GPT-4o（通用）/ Claude 3.5（代码）/ GLM-4（中文）/ 开源模型（成本）
  - 多模型路由：按任务类型自动选择
- 上下文策略：
  - 短对话全量 / 长对话压缩 / 关键信息持久化
  - Token 预算管理

### Slide 88: 关键架构决策——工具治理与安全合规
- 工具治理：注册制 / 权限分级 / 审计日志
- 安全合规：输入过滤 / 输出审查 / 数据隔离 / 合规审计
- 成本控制：Token 预算 / 模型路由 / 缓存策略

### Slide 89: 代码讲解——examples/09-enterprise-pattern.py
- 完整执行流程：
  接入层 → 安全检查 → 会话管理 → 知识检索 → LLM 推理 → MCP 工具调用 → 质量评估 → 可观测性 → 输出审查 → 最终响应
- 每一步对应架构图的哪一层

### Slide 90: 企业落地挑战
- 成本控制：Token 预算 / 模型路由 / 缓存策略
- 质量保证：评估体系 / 回归测试 / A/B 测试
- 团队组织：AI 工程师角色 / 与后端团队协作
- 运维：监控告警 / 故障恢复 / 版本管理

### Slide 91: 本章小结与思考题
- 六层架构是企业级 Agent 的参考架构
- 最大挑战是工程问题，不是技术问题
- 思考题：如何评估 Agent 系统的 ROI？

---

## 第 11 章：综合案例（7 张）

### Slide 92: 案例 1——企业知识库 Agent
- 需求：企业内部知识问答
- 架构：Python + LangChain + RAG + Milvus + MCP
- 数据流图
- 技术选型理由
- 挑战与解决方案

### Slide 93: 案例 1——架构详解
- 索引管线：文档 → 切分 → 向量化 → Milvus
- 查询管线：问题 → Hybrid Search → Rerank → LLM → 回答
- MCP 集成：HR / Finance 系统接入
- 权限控制：文档级权限过滤

### Slide 94: 案例 2——软件研发 Agent
- 需求：自动化代码实现 / 测试 / 审查 / 部署
- 架构：Python + LangGraph + Skill + Harness
- 工作流图
- 技术选型理由
- 挑战与解决方案

### Slide 95: 案例 2——工作流详解
- LangGraph 状态图：编码 → 测试 → 审查 → (修改循环) → 部署
- Skill 组合：CodingSkill → TestingSkill → ReviewSkill → DeploymentSkill
- Harness：上下文管理 + 工具沙箱 + 监控

### Slide 96: 两个案例对比
| 维度 | 知识库 Agent | 研发 Agent |
|------|-------------|-----------|
| 核心能力 | 检索 + 问答 | 代码生成 + 执行 |
| 编排框架 | LangChain | LangGraph |
| RAG 需求 | 重 | 轻 |
| 工具复杂度 | 低 | 高 |
| Harness 需求 | 中 | 高 |
| 安全要求 | 数据隔离 | 代码执行安全 |

### Slide 97: 架构决策方法论
- 需求分析 → 能力矩阵 → 技术选型 → 架构设计 → 原型验证 → 迭代优化
- 常见反模式：
  - 过度工程化（简单需求用复杂架构）
  - 忽略 Harness（裸跑 Agent 上生产）
  - 忽略评估（没有质量度量）

### Slide 98: 本章小结
- 两个案例覆盖了 RAG 重场景和 Tool 重场景
- 架构决策需要根据具体需求定制
- 没有"最好的架构"，只有"最合适的架构"

---

## 总结与学习路线（4 张）

### Slide 99: 技术地图回顾
- 11 章核心知识点一图总结
- 组件关系总图
- "这张图就是你的 AI Engineering 技术地图"

### Slide 100: 学习路线建议
- 阶段 1（1-2 周）：Python AI 基础
- 阶段 2（2-3 周）：LangChain 生态
- 阶段 3（2-3 周）：Agent 工程化
- 阶段 4（持续）：实战项目
- 每阶段目标和学习资源

### Slide 101: 核心思维转变
1. 从确定性到概率性
2. 从代码逻辑到 Prompt 工程
3. 从单体到 Agent 编排
4. 从功能测试到质量评估
- "最大的转变不是语言，而是思维方式"

### Slide 102: Q&A 与资源
- 开放提问
- 推荐资源列表
- 联系方式

---

## 附录（3 张）

### Slide 103: 术语速查表
| 术语 | 全称 | 一句话解释 |
|------|------|-----------|
| LLM | Large Language Model | 大语言模型 |
| RAG | Retrieval-Augmented Generation | 检索增强生成 |
| MCP | Model Context Protocol | 工具调用标准化协议 |
| A2A | Agent to Agent | Agent 间通信 |
| Harness | — | Agent 运行时基础设施 |
| Skill | — | Agent 能力封装 |
| Embedding | — | 文本向量语义表示 |
| Vector DB | Vector Database | 向量数据库 |
| Tool Calling | — | LLM 调用外部工具 |
| Context Window | — | LLM 单次最大 Token 数 |
| LCEL | LangChain Expression Language | LangChain 声明式管道 |
| ReAct | Reasoning + Acting | 推理+行动 Agent 模式 |
| Hybrid Search | — | 关键词+向量混合检索 |
| Rerank | — | 检索结果重排序 |
| Human-in-the-loop | — | 人工介入工作流 |

### Slide 104: 推荐资源
- 官方文档：LangChain / LangGraph / LlamaIndex / MCP / OpenAI
- 开源项目：LangChain / LangGraph / AutoGen / CrewAI
- 学习课程：DeepLearning.AI / Anthropic Prompt Engineering / OpenAI Cookbook

### Slide 105: 代码示例索引
| 文件 | 章节 | 内容 | 代码量 |
|------|------|------|--------|
| 01-llm-basics.py | 第 2 章 | LLM 调用/记忆/Tool/Embedding | ~150 行 |
| 02-langchain-basics.py | 第 3 章 | Chain/Tool/Agent/Pydantic | ~180 行 |
| 03-langgraph-agent.py | 第 4 章 | State/Node/Edge/循环 | ~220 行 |
| 04-rag-pipeline.py | 第 5 章 | 完整 RAG 管线 | ~180 行 |
| 05-mcp-server.py | 第 6 章 | MCP Server/Tool/Handler | ~200 行 |
| 06-a2a-collaboration.py | 第 7 章 | 多 Agent 协作 | ~280 行 |
| 07-harness-runtime.py | 第 8 章 | 上下文/沙箱/监控/权限 | ~380 行 |
| 08-agent-skill.py | 第 9 章 | Skill 定义/路由/编排 | ~330 行 |
| 09-enterprise-pattern.py | 第 10 章 | 企业级六层架构 | ~400 行 |
