# Python AI Agent 开发前沿技术深度工作坊

> 半天（4 小时）深度技术工作坊 | 面向 Java 后端工程师  
> 从概念认知到架构设计，完整覆盖 Python AI Agent 开发技术栈

## 课程定位

本课程**不是** Python 语法课程，**不是** 机器学习理论课程，而是 **Python AI 应用工程化深度工作坊**。

帮助具有 Java 后端经验的工程师：
1. **深入理解** Python AI 应用开发完整技术栈（不只是"听说过"）
2. **动手阅读** 每个核心组件的可运行代码示例
3. **建立** 从 LLM 调用到企业级 Agent 架构的完整技术地图
4. **掌握** 技术选型决策框架和最佳实践

## 目标听众

- 具有 3+ 年 Java 后端开发经验的软件工程师
- 能够阅读 Python 代码（不需要精通 Python 语法）
- 了解 LLM 基本概念（用过 ChatGPT / Claude 等）
- 希望系统性地建立 AI Engineering 技术认知
- 需要评估或参与 AI Agent 项目的技术决策者

## 课程时长

**半天（4 小时 / 240 分钟）**，含两次休息（各 15 分钟）和终场 Q&A（20 分钟）

### 时间安排

| 模块 | 时长 | 内容 |
|------|------|------|
| 开场 | 5 min | 课程定位、议程、互动调研 |
| **上半场（105 min）** | | |
| 第 1 章 | 15 min | Python AI 生态全景与技术选型逻辑 |
| 第 2 章 | 20 min | LLM 应用开发基础——从 API 到结构化输出 |
| 第 3 章 | 25 min | LangChain 核心架构与 LCEL 管线 |
| 第 4 章 | 25 min | LangGraph 状态图与 Agent 工作流 |
| 第 5 章 | 20 min | RAG 技术体系——从基础到高级 |
| — 休息 — | 15 min | |
| **下半场（100 min）** | | |
| 第 6 章 | 20 min | MCP 协议——工具调用标准化 |
| 第 7 章 | 15 min | A2A 多 Agent 协作模式 |
| 第 8 章 | 20 min | Harness 工程基础设施 |
| 第 9 章 | 15 min | Agent Skill 能力封装 |
| 第 10 章 | 15 min | 企业级 Agent 架构 |
| 第 11 章 | 15 min | 综合案例与架构决策 |
| — 休息 — | 15 min | |
| Q&A | 20 min | 问答、学习路线、资源 |

## 课程目录结构

```
Python-AI-Agent-Workshop/
├── README.md              # 课程说明（本文件）
├── course-outline.md      # 课程大纲（详细时间安排 + 学习目标）
├── teacher-guide.md       # 讲师讲解稿（完整教案，约 40KB）
├── slides-outline.md      # 幻灯片大纲（约 80 张幻灯片）
├── diagrams/              # Mermaid 架构图集合（11 张）
│   ├── 01-python-ai-ecosystem.md       # Python AI 生态全景
│   ├── 02-llm-app-architecture.md      # LLM 应用架构
│   ├── 03-langchain-stack.md           # LangChain 组件栈
│   ├── 04-langgraph-workflow.md        # LangGraph 工作流
│   ├── 05-rag-pipeline.md             # RAG 管线
│   ├── 06-mcp-architecture.md          # MCP 协议架构
│   ├── 07-a2a-collaboration.md         # A2A 多 Agent 协作
│   ├── 08-harness-engineering.md       # Harness 工程架构
│   ├── 09-skill-architecture.md        # Skill 架构
│   ├── 10-enterprise-architecture.md   # 企业级架构
│   └── 11-combined-cases.md           # 综合案例
└── examples/              # Python 示例代码（9 个文件，均有详细中文注释）
    ├── 01-llm-basics.py               # LLM 基础：调用/记忆/Tool/Embedding
    ├── 02-langchain-basics.py          # LangChain：Chain/Tool/Agent/Pydantic
    ├── 03-langgraph-agent.py           # LangGraph：State/Node/Edge/循环
    ├── 04-rag-pipeline.py              # RAG：加载/切分/向量化/检索/问答
    ├── 05-mcp-server.py                # MCP：Server/Tool/Handler
    ├── 06-a2a-collaboration.py         # A2A：多Agent协作/任务编排
    ├── 07-harness-runtime.py           # Harness：上下文/沙箱/监控/权限
    ├── 08-agent-skill.py               # Skill：定义/路由/编排
    └── 09-enterprise-pattern.py        # 企业级：六层架构/完整流程
```

## 核心技术栈覆盖

| 领域 | 技术组件 | 课程深度 |
|------|---------|---------|
| Python 应用基础 | FastAPI, Pydantic, AsyncIO | 概念 + 代码注释 |
| LLM API | OpenAI SDK, Anthropic SDK | 代码示例 + 参数详解 |
| LLM 编排 | LangChain, LCEL | 核心组件 + Chain 组合 + 代码 |
| Agent 工作流 | LangGraph | State/Node/Edge + 完整工作流代码 |
| RAG | Embedding, Vector DB, Retriever | 完整管线 + 高级技术 + 代码 |
| 向量数据库 | Milvus, Chroma, FAISS | 选型对比 + 代码示例 |
| 协议 | MCP (Model Context Protocol) | 架构 + Server 创建代码 |
| 多 Agent | A2A, AutoGen, CrewAI | 协作模式 + 代码示例 |
| 工程化 | Harness, Context, Sandbox | 7 大组件 + 代码示例 |
| 能力封装 | Skill, Procedure, Knowledge | 定义/路由/编排 + 代码 |
| 企业架构 | 6 层架构, 安全, 可观测性 | 全景图 + 设计决策 |
| 评估与质量 | Evaluation, Metrics, Feedback | 概念 + 实践方法 |

## 学习成果

完成本工作坊后，学员将能够：

1. **深入理解** Python 在 AI 应用开发中的生态优势和局限
2. **画出** LLM 应用完整架构图并解释每个组件的角色
3. **阅读** LangChain / LangGraph 代码并理解执行流程
4. **设计** RAG 管线并选择合适的切分/检索/重排策略
5. **解释** MCP 协议的价值并理解 Server / Client 交互
6. **描述** 多 Agent 协作的三种模式及适用场景
7. **设计** 企业级 Agent 架构并做出合理技术选型
8. **制定** 团队 Python AI Agent 学习路线

## 互动设计

- 每章结束有 2-3 个"思考题"（不需要编码，引导思考）
- 上半场结束后有 15 分钟休息 + 自由讨论
- 下半场结束前有综合案例讨论环节
- 终场 20 分钟开放 Q&A

## 前置准备

学员无需安装任何软件，课程以代码阅读和架构理解为主。  
如果希望课后动手实验：
- Python 3.11+
- `pip install langchain langgraph openai chromadb`
- OpenAI API Key（或兼容 API）
