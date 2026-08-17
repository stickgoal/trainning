# Python AI Agent 开发前沿技术快速扫盲

> 面向 Java 后端工程师的 Python AI 应用开发生态认知教程

---

## 一、课程定位

这不是一门 Python 语法课，也不是机器学习原理课。

这是一门 **Python AI 应用开发生态认知教程**：帮助有 Java 后端经验的工程师，建立一张完整的 "Python AI Engineering 技术地图"，看懂 LLM 应用与 Agent 系统是怎么搭起来的、每个组件解决什么问题、如何组合成可运行的应用。

本教程不设固定时长。内容按学习深度组织，每章都是**学员可直接阅读的完整教程正文**（含概念讲解、原理剖析、代码逐段精讲、运行预期、应用场景、陷阱与最佳实践）。你可以按顺序通读，也可以挑感兴趣的章节跳读。

学完后，你不会立刻成为 AI 专家，但你会知道：

- 为什么公司里的 AI 项目都用 Python 写；
- LangChain、LangGraph、RAG、MCP、A2A、Harness、Skill 这些词分别在工程链路的哪一层；
- 一个企业级 Agent 应用由哪些部分拼装而成；
- 下一步该读什么文档、跑哪个 demo、补哪些知识。

---

## 二、目标听众

| 维度 | 假设 |
|---|---|
| 背景 | 3 年以上 Java 后端开发经验，熟悉 Spring 生态 |
| Python 能力 | 能读懂 Python 代码（变量、函数、类、import），但不熟悉 asyncio / decorator / Pydantic 等 |
| AI 知识 | 知道 LLM、用过 ChatGPT，但没亲手搭过 LLM 应用 |
| 数学 | 不要求，本课不涉及梯度下降、反向传播 |
| 机器学习 | 不要求，本课不训练模型，只调用模型 |

> **对 Java 工程师的关键提示**：把 LLM 当成一个 "通过 HTTP 调用的、会推理的外部服务"，把 Agent 当成 "一个用 LLM 做决策、会调用工具的 Spring Service"。剩下的问题就只是 "用什么框架把这两件事拼起来"。

---

## 三、章节地图（按学习路径排序）

本教程共 13 章，分四个阶段。建议按顺序学习，但每章相对独立，可按需跳读。

### 阶段 A · 建立认知地基（第 1–2 章）
回答 "Python AI 生态是什么"。

| # | 章节 | 难度 | 核心问题 |
|---|---|---|---|
| 1 | [Python AI 生态概览](chapters/01_python_ai_ecosystem.md) | ★☆☆ | 为什么 AI 项目都用 Python？技术栈分几层？ |
| 2 | [LLM Application 开发基础](chapters/02_llm_app_basics.md) | ★★☆ | Prompt/Token/Embedding/Tool Calling 等 8 个术语是什么？ |

### 阶段 B · 掌握核心组件（第 3–6 章）
回答 "每个组件解决什么问题"，是教程主体。

| # | 章节 | 难度 | 核心问题 |
|---|---|---|---|
| 3 | [LangChain](chapters/03_langchain.md) | ★★☆ | 怎么把 Prompt/检索/记忆组件化？LCEL 是什么？ |
| 4 | [LangGraph](chapters/04_langgraph.md) | ★★★ | 怎么表达循环、状态、人工介入的 Agent 流程？ |
| 5 | [RAG 技术体系](chapters/05_rag.md) | ★★★ | 怎么让 LLM 用上私有知识？向量检索怎么搭？ |
| 6 | [LlamaIndex 与检索增强进阶](chapters/06_llamaindex.md) | ★★★ | 另一个 RAG 框架怎么用？多路召回与 Rerank 怎么配？ |

### 阶段 C · 工程化能力（第 7–11 章）
回答 "怎么让 Agent 接系统、能协作、跑得稳、怎么选型"。

| # | 章节 | 难度 | 核心问题 |
|---|---|---|---|
| 7 | [MCP（Model Context Protocol）](chapters/07_mcp.md) | ★★☆ | 怎么标准化地把工具暴露给 Agent？ |
| 8 | [A2A（Agent To Agent）](chapters/08_a2a.md) | ★★★ | 多个 Agent 怎么协作？ |
| 9 | [Harness Engineering](chapters/09_harness.md) | ★★★ | 怎么让 Agent 稳定上生产？可观测与护栏怎么加？ |
| 10 | [Agent Skill](chapters/10_skill.md) | ★★☆ | 怎么把专家经验封装成可复用能力？ |
| 11 | [主流框架选型与生态进阶](chapters/11_framework_selection.md) | ★★☆ | CrewAI/AutoGen/DSPy/Semantic Kernel 等怎么选？ |

### 阶段 D · 架构与实战（第 12–13 章）
回答 "如何组合形成 Agent 应用"。

| # | 章节 | 难度 | 核心问题 |
|---|---|---|---|
| 12 | [企业级 Agent 架构](chapters/12_enterprise_arch.md) | ★★★ | 完整的分层架构长什么样？每层职责？ |
| 13 | [综合案例](chapters/13_cases.md) | ★★★ | 两个端到端案例怎么把前 12 章串起来？ |

---

## 四、核心技术地图

```
┌─────────────────────────────────────────────────────────────┐
│                    用户 / 业务系统                            │
└───────────────────────────────┬─────────────────────────────┘
                                │
                ┌───────────────▼───────────────┐
                │      Agent Application        │  ← LangGraph / AutoGen / CrewAI
                │   (编排、状态、循环、人工介入)    │
                └───┬───────┬───────┬───────┬───┘
                    │       │       │       │
              ┌─────▼──┐ ┌─▼───┐ ┌▼────┐ ┌▼─────────┐
              │  LLM   │ │Skill│ │MCP  │ │  A2A 通信  │
              │(推理)  │ │(能力)│ │(工具)│ │(多Agent)  │
              └────────┘ └─────┘ └─────┘ └──────────┘
                    │       │       │       │
              ┌─────▼───────▼───────▼───────▼─────┐
              │  Harness (Runtime/记忆/权限/评估/监控)  │
              └─────┬───────────────────────┬─────┘
                    │                       │
              ┌─────▼─────┐          ┌──────▼──────┐
              │ Knowledge │          │ Enterprise  │
              │ RAG/Vector │          │  Systems   │
              └───────────┘          └────────────┘
```

### 按层归类（Java 工程师可对照 Spring 生态）

| 层 | Python 组件 | Java 类比 |
|---|---|---|
| Web 框架 | FastAPI | Spring Boot |
| 数据校验 | Pydantic | Bean Validation / DTO |
| 异步运行时 | asyncio | Reactor / WebFlux |
| LLM SDK | DeepSeek SDK（OpenAI 兼容）, 通义千问 SDK | 无直接对应，类比 HttpClient |
| 编排框架 | LangChain, LangGraph, LlamaIndex | Spring Integration / Camunda |
| 多 Agent | AutoGen, CrewAI | 微服务编排 |
| 向量检索 | Milvus, Chroma, FAISS | Elasticsearch（向量版） |
| 工具协议 | MCP | RPC / gRPC |
| Agent 运行时 | Harness（自研或 Claude Code 类） | Spring容器 + AOP |

---

## 五、目录结构

```
AI-Native-Development/
├── README.md                  ← 你在这里：学习指南
├── course-outline.md          ← 章节地图与学习路径（无时间约束）
├── chapters/                  ← ★ 学员教程正文（逐章详尽，可直接阅读）
│   ├── 01_python_ai_ecosystem.md
│   ├── 02_llm_app_basics.md
│   ├── 03_langchain.md
│   ├── 04_langgraph.md
│   ├── 05_rag.md
│   ├── 06_llamaindex.md
│   ├── 07_mcp.md
│   ├── 08_a2a.md
│   ├── 09_harness.md
│   ├── 10_skill.md
│   ├── 11_framework_selection.md
│   ├── 12_enterprise_arch.md
│   └── 13_cases.md
├── teacher-guide.md           ← 讲师授课指引（节奏/互动/FAQ）
├── slides-outline.md          ← PPT 页结构（13 章 × 每页要点）
├── diagrams/                  ← Mermaid 架构图（13 张）
│   ├── 01_python_ai_ecosystem.mmd
│   ├── 02_llm_app_stack.mmd
│   ├── 03_langchain.mmd
│   ├── 04_langgraph.mmd
│   ├── 05_rag.mmd
│   ├── 06_llamaindex.mmd
│   ├── 07_mcp.mmd
│   ├── 08_a2a.mmd
│   ├── 09_harness.mmd
│   ├── 10_skill.mmd
│   ├── 11_framework_selection.mmd
│   ├── 12_enterprise.mmd
│   └── 13_cases.mmd
├── examples/                  ← 可运行的 Python 示例（含中文注释）
│   ├── 00_setup.py
│   ├── 01_llm_basics.py
│   ├── 02_langchain_demo.py
│   ├── 03_langgraph_demo.py
│   ├── 04_rag_demo.py
│   ├── 04b_rag_advanced.py
│   ├── 05_llamaindex_demo.py
│   ├── 06_mcp_server.py
│   ├── 07_a2a_demo.py
│   ├── 08_harness_demo.py
│   ├── 09_skill_demo.py
│   ├── 10_framework_selection.py
│   ├── 11_enterprise_arch.py
│   ├── 12_case_knowledge_agent.py
│   └── 13_case_dev_agent.py
└── requirements.txt           ← 依赖清单
```

---

## 六、如何使用本教程

### 自学者（推荐路径）
1. 通读 `README.md`（本文）建立全局认知；
2. 按顺序读 `chapters/01` ~ `chapters/13`，每章配合对应 `examples/` 代码动手跑；
3. 遇到不懂的 Python 语法，看代码里的中文注释（已标注 decorator / async / 类型提示 等）；
4. 读完按 `course-outline.md` 末尾的 "学习路径" 做实战练习。

### 学员（跟课）
1. 课前：扫一眼 `README.md` 和 `course-outline.md`，知道要讲什么；
2. 课中：看架构图（`diagrams/`）和代码（`examples/`）；
3. 课后：精读 `chapters/` 对应章节巩固，按学习路径做项目。

### 讲师
1. 通读 `course-outline.md` 把握章节依赖；
2. 用 `chapters/` 作为授课正文（已详尽到可直接讲）；
3. 参考 `teacher-guide.md` 的节奏建议、互动设计、FAQ 应对；
4. 用 `slides-outline.md` 制作 PPT；
5. 课前把 `examples/` 跑通一遍，确保 demo 可演示。

---

## 七、环境准备（初始化）

> 本教程代码默认用 **DeepSeek**（OpenAI 兼容接口）作 LLM，用**本地开源中文模型 `BAAI/bge-small-zh-v1.5`** 做 Embedding（DeepSeek 等国产 LLM 不提供 Embedding 接口）。两者解耦：LLM 走云端，Embedding 走本地，整条 RAG 管线在国内可直接跑通。

### 1. Python 版本
- 要求 **Python 3.11+**，**推荐 3.12**
- 检查：`python --version`
- 本机已装系统 Python 3.12.8（`C:\Users\ThinkPad\AppData\Local\Programs\Python\Python312\python.exe`）

### 2. 创建虚拟环境（类比 Java 隔离 classpath）
```bash
cd AI-Native-Development
python -m venv .venv
# Windows
.venv\Scripts\activate
# macOS / Linux
source .venv/bin/activate
```

### 3. 安装依赖
```bash
pip install --upgrade pip
pip install -r requirements.txt
# 国内加速（清华镜像）：
pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
```
> `requirements.txt` 已含 `langchain-deepseek`，无需再单装 `langchain-openai`。

### 4. 配置 DeepSeek API Key
LLM 调用需 DeepSeek Key（于 https://platform.deepseek.com 申请）：
```bash
# Windows
set DEEPSEEK_API_KEY=sk-...
set DEEPSEEK_BASE_URL=https://api.deepseek.com
# macOS / Linux
export DEEPSEEK_API_KEY=sk-...
export DEEPSEEK_BASE_URL=https://api.deepseek.com
```
> 也可把上面两行写入项目根目录 `.env`，示例脚本用 `os.getenv` 读取。切换通义千问 / 智谱等兼容端点，只改 `DEEPSEEK_BASE_URL` 即可。

### 5. 预下载本地 Embedding 模型（国内必看）
Embedding 用 `BAAI/bge-small-zh-v1.5`（约 130MB，首次运行自动下载）。Hugging Face 官网在国内常下载慢 / 不通，请用镜像：
```bash
# Windows
set HF_ENDPOINT=https://hf-mirror.com
# macOS / Linux
export HF_ENDPOINT=https://hf-mirror.com
# 触发一次下载（之后纯本地，无需联网）
python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('BAAI/bge-small-zh-v1.5')"
```
模型缓存到 `~/.cache/huggingface/`，后续所有 RAG 示例复用，不重复下载。

> Rerank 示例（第 5、6 章）还会用到 `BAAI/bge-reranker-base`（交叉编码器），首次运行自动下载，同样走 `HF_ENDPOINT` 镜像。

### 7. 验证环境
```bash
python examples/00_setup.py
```
脚本会检查 `DEEPSEEK_API_KEY`、调用一次 `deepseek-chat`、并确认本地 Embedding 模型可用。看到模型返回内容即成功。

### 常见问题
- `No module named 'sentence_transformers'` → 回到第 3 步装依赖。
- Embedding 下载卡住 / 超时 → 确认已设 `HF_ENDPOINT=https://hf-mirror.com`。
- `DEEPSEEK_API_KEY` 未设置 → LLM 示例会报错，回到第 4 步。
- 无需 API Key 的示例：`07_a2a_demo.py` / `08_harness_demo.py` / `09_skill_demo.py` / `10_framework_selection.py` / `11_enterprise_arch.py` / `13_case_dev_agent.py`（纯逻辑验证），以及 `04b_rag_advanced.py`（检索/融合/精排部分无需 Key）；其余需要 DeepSeek Key。

---

## 八、教程风格声明

- **架构师视角**：关注组件边界、数据流、职责划分，而不是 API 细节；
- **学员可直接阅读**：每章是完整教程正文，不是大纲或口播稿；
- **技术实践导向**：每个概念都有可运行的代码，不讲 "概念空话"；
- **避免营销语言**：不吹 "颠覆"、"革命"、"赋能"，只说 "它解决什么问题、什么时候用、什么时候别用"；
- **Java 友好**：代码注释标注 Python 特殊语法，并与 Java 类比。

---

## 九、三个核心问题（贯穿全教程）

1. **Python AI 生态是什么？** → 第 1、2 章
2. **每个组件解决什么问题？为什么需要它？** → 第 3–10 章
3. **如何组合形成 Agent 应用？** → 第 12、13 章

带着这三个问题往下走，读完全教程后你应该能自己画一张企业级 Agent 架构图，并说出每一层为什么存在。
