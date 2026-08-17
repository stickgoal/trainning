# PPT 大纲 · 13 章 × 每页要点

> 每章按 "封面 → 核心概念 → 架构图 → 代码 → 应用场景 → 小结" 6 类页排版。
> 配图全部用 `diagrams/` 下的 Mermaid 文件渲染。
> 代码页只放 8–15 行精华，完整代码指向 `examples/`，详尽讲解见 `chapters/`。
> 本大纲不设固定时长，按实际授课需要调整页数与深度。

---

## 全局页

### P0 · 封面
- 标题：**Python AI Agent 开发前沿技术快速扫盲**
- 副标题：面向 Java 后端工程师的生态认知教程
- 一句话：建立 Python AI Engineering 技术地图
- 讲师 / 日期

### P1 · 你是谁 / 这门课不是什么
- 目标听众：3 年+ Java 后端，能读 Python，没搭过 LLM 应用
- 不是：Python 语法课 / 机器学习课 / 数学课
- 是：Python AI 应用开发生态认知教程

### P2 · 章节地图与学习路径
- 四阶段 13 章（见 README 章节地图）
- 三个核心问题贯穿全教程

---

## 第 1 章 · Python AI 生态概览

### P1.1 章节封面
- 阶段 A · 认知地基 · 难度 ★☆☆
- 对应教程：`chapters/01_python_ai_ecosystem.md`

### P1.2 为什么是 Python
4 个原因（每条一行）：
1. AI SDK 丰富（DeepSeek/通义千问 一行调用）
2. 快速实验（REPL/Notebook）
3. 数据处理生态（NumPy/Pandas/PyTorch）
4. LLM 工具链成熟（LangChain/LlamaIndex）

### P1.3 技术栈地图
- 配图：`diagrams/01_python_ai_ecosystem.mmd`
- 表格：Python 组件 ↔ Java 类比（见 README）

### P1.4 代码页
- `examples/00_setup.py` 精华：`from openai import OpenAI; client.chat.completions.create(...)`
- 一句话：8 行代码调通 LLM

### P1.5 应用场景 + 小结
- 选型汇报：AI 后端 Python、网关 Java 的分工

---

## 第 2 章 · LLM Application 开发基础

### P2.1 章节封面
- 阶段 A · 认知地基 · 难度 ★★☆
- 对应教程：`chapters/02_llm_app_basics.md`

### P2.2 8 个术语一页讲完
- Prompt / Token / Context / Embedding / Retrieval / Tool Calling / Memory / Agent
- 每个一句 + Java 类比

### P2.3 完整架构
- 配图：`diagrams/02_llm_app_stack.mmd`
- 数据流：User → Application → LLM → Tools → Knowledge → Memory

### P2.4 代码页
- `examples/01_llm_basics.py` 三段：纯调用 / Tool Calling 四步闭环 / Memory
- 重点：Tool Calling 回灌步骤

### P2.5 小结
- 聊天机器人 ≠ LLM 应用：后者带工具 + 知识 + 记忆

---

## 第 3 章 · LangChain

### P3.1 章节封面
- 阶段 B · 核心组件 · 难度 ★★☆
- 对应教程：`chapters/03_langchain.md`

### P3.2 LangChain 解决什么问题
- 不用 LangChain：每个项目重写 Prompt/检索/记忆胶水代码
- 用 LangChain：组件化、可复用、可组合

### P3.3 9 大组件
- Chat Model / Prompt Template / Runnable / Chain / Retriever / Vector Store / Memory / Tool / Agent
- 表格：组件 ↔ 解决问题

### P3.4 架构图
- 配图：`diagrams/03_langchain.mmd`

### P3.5 代码页 ×2
- 页 A：调 LLM + Prompt 模板
- 页 B：LCEL Chain + Agent 选 Tool
- 注释高亮：`|` 管道符（Python 用 `|` 重载为 Runnable 串联）

### P3.6 应用场景 + 小结
- 80% LLM 应用默认起点

---

## 第 4 章 · LangGraph

### P4.1 章节封面
- 阶段 B · 核心组件 · 难度 ★★★（重点章）
- 对应教程：`chapters/04_langgraph.md`

### P4.2 为什么 Chain 不够
- Chain 是单向的；Agent 需要 **循环 + 条件 + 状态**
- LangGraph = 显式状态机 + 图

### P4.3 4 个原语
- Graph / Node / Edge / State（配 Java 类比：BPMN / Service / 状态迁移 / 流程变量）

### P4.4 架构图
- 配图：`diagrams/04_langgraph.mmd`

### P4.5 代码页
- `examples/03_langgraph_demo.py`
- 高亮：`StateGraph(State)`、`add_node`、`add_conditional_edges`、`compile()`

### P4.6 人工介入
- `interrupt_before=["human_review"]` 节点
- 一句话：把 Agent 从 "黑盒循环" 变成 "可中断流程"

### P4.7 应用场景 + 小结

---

## 第 5 章 · RAG

### P5.1 章节封面
- 阶段 B · 核心组件 · 难度 ★★★
- 对应教程：`chapters/05_rag.md`

### P5.2 6 步管线
- Document → Chunk → Embedding → Vector DB → Retriever → LLM

### P5.3 关键概念
- Embedding / Vector DB / 多路召回（向量+BM25+RRF）/ Rerank
- 工具对照表：LangChain RAG / LlamaIndex / Milvus / Chroma / FAISS / Qdrant / pgvector

### P5.4 架构图
- 配图：`diagrams/05_rag.mmd`

### P5.5 代码页
- `examples/04_rag_demo.py`
- 高亮：`RecursiveCharacterTextSplitter`、`Chroma.from_documents`、`as_retriever`
- 防幻觉：第四问返回 "未找到"

### P5.6 进阶代码页：多路召回 + Rerank
- `examples/04b_rag_advanced.py`
- 高亮：`BM25Retriever`、`EnsembleRetriever`（RRF）、`ContextualCompressionRetriever`（Rerank）

### P5.7 应用场景 + 小结
- 企业知识库、文档问答、合规检索
- 生产 RAG 必加 Rerank

---

## 第 6 章 · LlamaIndex 与检索增强进阶

### P6.1 章节封面
- 阶段 B · 核心组件 · 难度 ★★★
- 对应教程：`chapters/06_llamaindex.md`

### P6.2 与 LangChain 的分工
- LangChain = 通用编排；LlamaIndex = RAG 专用
- 选型：编排选 LangChain，纯 RAG 精细控制选 LlamaIndex，可混用

### P6.3 核心抽象
- Document → Node → Index → Retriever → NodePostprocessor → QueryEngine
- 两阶段：索引（建库）/ 查询（实时）

### P6.4 架构图
- 配图：`diagrams/06_llamaindex.mmd`

### P6.5 代码页
- `examples/05_llamaindex_demo.py`
- 高亮：`VectorStoreIndex`、`as_query_engine`、自定义 `NodePostprocessor`（Rerank）、`RouterQueryEngine`（路由）

### P6.6 多路召回三种形态
- 路由（RouterQueryEngine）/ 融合（RRF）/ 子问题分解（SubQuestionQueryEngine）

### P6.7 技术栈全景 + 小结
- RAG 框架 / 向量库 / Reranker / 评估 / 可观测 / 记忆 / 本地推理 全景表

---

## 第 7 章 · MCP

### P7.1 章节封面
- 阶段 C · 工程化 · 难度 ★★☆
- 对应教程：`chapters/07_mcp.md`

### P7.2 MCP 出现前
- 每个 Agent 自己接工具 → N × M 适配地狱
- MCP：标准化 "LLM ↔ 工具" 协议（类比 gRPC）

### P7.3 三件套
- MCP Server / Client / Tool
- 架构图：`diagrams/07_mcp.mmd`

### P7.4 代码页
- `examples/06_mcp_server.py`
- 高亮：`@mcp.tool()` decorator 把普通函数变 Agent 可调用工具

### P7.5 应用场景 + 小结
- 一个 Agent 接公司 N 个内部系统

---

## 第 8 章 · A2A

### P8.1 章节封面
- 阶段 C · 工程化 · 难度 ★★★
- 对应教程：`chapters/08_a2a.md`

### P8.2 为什么单 Agent 不够
- 任务太复杂 / 角色太多 / 上下文太长 → 拆成多 Agent

### P8.3 三个问题
- Communication / Discovery / Task Delegation

### P8.4 架构图
- 配图：`diagrams/08_a2a.mmd`
- Research → Coding → Testing 协作

### P8.5 伪代码页
- `examples/07_a2a_demo.py`（Agent 类 + 消息传递，无需 API Key）

### P8.6 小结
- 多 Agent 是 "微服务化" 在 Agent 层的重演

---

## 第 9 章 · Harness Engineering

### P9.1 章节封面
- 阶段 C · 工程化 · 难度 ★★★（上生产关键）
- 对应教程：`chapters/09_harness.md`

### P9.2 什么是 Harness
- 让 Agent 稳定运行的工程基础设施
- "Agent = LLM + Harness"，没有 Harness 的 Agent 只是 demo

### P9.3 7 个子能力
- Context / Memory / Tool Runtime / Permission / Eval / Monitor / Feedback

### P9.4 架构图
- 配图：`diagrams/09_harness.mmd`

### P9.5 真实案例
- Claude Code / Codex 的 Harness 形态（CLI + 工具沙箱 + 上下文管理）

### P9.6 代码页
- `examples/08_harness_demo.py`
- 高亮：上下文窗口检查、工具权限校验、决策日志

### P9.7 应用场景 + 小结

---

## 第 10 章 · Agent Skill

### P10.1 章节封面
- 阶段 C · 工程化 · 难度 ★★☆
- 对应教程：`chapters/10_skill.md`

### P10.2 Skill 三层
- Tool（函数） / Prompt（文本） / Skill（能力 = Prompt+Tool+说明+触发）

### P10.3 与 Tool/Prompt 的区别
- 表格对照
- 类比：Tool=方法，Prompt=注释，Skill=带文档的 Spring Bean

### P10.4 架构图
- 配图：`diagrams/10_skill.mmd`

### P10.5 典型 Skill 清单
- Coding / Database / Testing / Deployment

### P10.6 代码页
- `examples/09_skill_demo.py`
- 高亮：`BaseSkill` 基类 + `DatabaseSkill` 子类

### P10.7 小结
- 把专家经验沉淀成可复用 Agent 能力

---

## 第 11 章 · 主流框架选型与生态进阶

### P11.1 章节封面
- 阶段 C · 工程化 · 难度 ★★☆
- 对应教程：`chapters/11_framework_selection.md`

### P11.2 决策三问
- 单 Agent 还是多 Agent？
- 重 RAG 还是重流程编排？
- 是否跨语言栈（.NET / Java）？
- 一句话：先问需求，再选框架

### P11.3 框架速查表
- 单 Agent / 通用：LangChain / LangGraph
- 多 Agent 角色扮演：CrewAI
- 多 Agent 对话式：AutoGen
- 轻量 OpenAI 风格：OpenAI Agents SDK
- 提示词优化编译：DSPy
- 跨语言企业：Semantic Kernel
- RAG 专用：LlamaIndex / Haystack

### P11.4 小结
- 框架是手段不是目的，别被营销带偏
- 选型表见 `chapters/11_framework_selection.md`

## 第 12 章 · 企业级 Agent 架构

### P12.1 章节封面
- 阶段 D · 架构实战 · 难度 ★★★
- 对应教程：`chapters/12_enterprise_arch.md`

### P12.2 分层架构
- 配图：`diagrams/12_enterprise.mmd`
- User → Agent App → {LLM, Memory, Knowledge, Tools, MCP, A2A} → {Workflow, Eval, Observability} → Enterprise Systems

### P12.3 每层职责
- 表格逐层说明

### P12.4 一句话
- "Agent 应用 ≠ 一个 LLM 调用"

---

## 第 13 章 · 综合案例

### P13.1 章节封面
- 阶段 D · 架构实战 · 难度 ★★★
- 对应教程：`chapters/13_cases.md`

### P13.2 案例 1：企业知识库 Agent
- 技术栈：Python + LangChain + RAG + Chroma + MCP
- 架构图：`diagrams/13_cases.mmd` 左半
- 代码：`examples/12_case_knowledge_agent.py`
- 运行流程：用户问制度调 search_policy，问员工调 query_employee_db

### P13.3 案例 2：软件研发 Agent
- 技术栈：Python + LangGraph + Skill + Tool Calling + Harness
- 架构图：`diagrams/13_cases.mmd` 右半
- 代码：`examples/13_case_dev_agent.py`
- 重点：测试失败回环 coding 节点

### P13.4 两案例对比表
- 一个偏 "知识 + 检索"，一个偏 "流程 + 工具"
- 选型原则：检索为主上 LangChain+RAG，流程复杂上 LangGraph

---

## 收尾页

### P14.1 三个核心问题回顾
1. Python AI 生态是什么？
2. 每个组件解决什么问题？
3. 如何组合形成 Agent 应用？

### P14.2 学习路径
- 四周路线图（见 course-outline.md）

### P14.3 推荐资料 + Q&A
- 4 个官方文档链接
- 二维码 / 联系方式
