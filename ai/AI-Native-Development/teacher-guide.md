# 讲师授课指引

> 本文件是**授课节奏与互动设计**的指引，不是教程正文。
> 教程正文在 `chapters/`（学员可直接阅读）。讲师按本指引组织授课，内容引用对应章节。

---

## 一、定位说明

- `chapters/01~13.md`：**教程正文**，学员可直接阅读，也是讲师的授课内容来源；
- 本文件：**怎么讲**——节奏建议、互动设计、常见提问应对、备课清单；
- `slides-outline.md`：PPT 页结构；
- `course-outline.md`：章节地图与学习路径。

本教程**不设固定时长**。讲师可根据听众基础和可用时间灵活安排：完整讲一遍约 4-5 小时；快速扫盲可只讲第 1-6、12-13 章；深入版可加动手实验。

---

## 二、授课节奏建议

### 方案 A：完整版（约 3.5 小时，含互动）

| 段落 | 章节 | 建议时长 | 重点 |
|---|---|---|---|
| 开场 | README + 大纲 | 10 min | 三个核心问题、技术地图 |
| 认知地基 | 第 1-2 章 | 30 min | 生态五层、8 个术语 |
| 核心组件 | 第 3 章 LangChain | 35 min | LCEL `\|` 是重点 |
| 核心组件 | 第 4 章 LangGraph | 40 min | State/Node/Edge、循环、人工介入 |
| 核心组件 | 第 5 章 RAG | 40 min | 6 步管线、防幻觉、多路召回 + Rerank |
| 核心组件 | 第 6 章 LlamaIndex | 30 min | 与 LangChain 分工、Rerank、路由式多路召回 |
| 课间休息 | — | 10 min | — |
| 工程化 | 第 7 章 MCP | 25 min | N×M→N+M |
| 工程化 | 第 8 章 A2A | 25 min | 三大问题、三种模式 |
| 工程化 | 第 9 章 Harness | 30 min | 7 子能力、上生产关键 |
| 工程化 | 第 10 章 Skill | 20 min | Skill vs Tool vs Prompt |
| 架构实战 | 第 11 章 框架选型 | 15 min | 决策三问、速查表 |
| 架构实战 | 第 12 章 企业架构 | 25 min | 六层架构 |
| 架构实战 | 第 13 章 综合案例 | 35 min | 两个案例串讲 |
| 总结 | 学习路径 | 10 min | 回顾三问题、下一步 |

### 方案 B：快速扫盲版（约 2.5 小时）

跳过第 8、10 章，第 7、9 章精简：
- 第 1-2 章（25 min）
- 第 3 章 LangChain（25 min）
- 第 4 章 LangGraph（30 min）
- 第 5 章 RAG（25 min）
- 第 6 章 LlamaIndex（15 min，精简）
- 第 7 章 MCP（15 min，精简）
- 第 9 章 Harness（15 min，精简）
- 第 12 章 架构（15 min）
- 第 13 章 案例（10 min，只讲案例 1）
- 总结（5 min）

### 方案 C：带动手实验版（约 1 天）

每章讲完后留 20-30 min 让学员跑对应 `examples/`，讲师巡场答疑。适合小班培训。

---

## 三、逐章授课要点与互动

### 第 1 章 · Python AI 生态概览
- **重点**：四个原因 + 五层技术栈地图。Java 类比表停留讲透。
- **互动**：问 "你们公司 AI 项目是 Python 全栈还是 Python+Java 网关？" 引出合理分工。
- **演示**：跑 `examples/00_setup.py`，8 行调通 LLM。

### 第 2 章 · LLM Application 开发基础
- **重点**：8 个术语 + Tool Calling 四步闭环（务必讲透，是 Agent 内核）。
- **互动**：问 "聊天机器人和 LLM 应用有什么区别？" 引出工具/知识/记忆。
- **演示**：跑 `examples/01_llm_basics.py` 三段，重点看 Tool Calling 回灌步骤。

### 第 3 章 · LangChain
- **重点**：LCEL 的 `|` 管道符（运算符重载）、`@tool` 的 docstring 工程。
- **互动**：问 "做'问问题→查文档→答'用哪几个组件？" 答案：Prompt+Retriever+Model+Runnable。
- **演示**：跑 `examples/02_langchain_demo.py` 四段，重点段 3 的 `prompt | model | parser`。

### 第 4 章 · LangGraph（重点章）
- **重点**：为什么 Chain 不够（循环）、State 的 reducer、条件边、人工介入。
- **互动**：问 "什么场景必须用 LangGraph？" 答案：循环、人工审批、多 Agent。
- **演示**：跑 `examples/03_langgraph_demo.py`，看条件边形成循环。

### 第 5 章 · RAG
- **重点**：6 步管线、chunk_size 选择、防幻觉 Prompt、多路召回（BM25+向量+RRF）+ Rerank 重排序。
- **互动**：问 "RAG 和微调区别？" 引出 RAG 适合知识更新；问 "召回 3 条和召回 50 条再精排哪个好？" 引出两阶段检索。
- **演示**：跑 `examples/04_rag_demo.py`（看第四问 "食堂" 返回 "未找到" 防幻觉）；跑 `examples/04b_rag_advanced.py`（看多路融合 + Rerank 效果，无需 API Key）。

### 第 6 章 · LlamaIndex
- **重点**：LlamaIndex 与 LangChain 的分工（编排 vs RAG 专用）、Index/QueryEngine 抽象、Rerank 后处理器、路由式多路召回。
- **互动**：问 "做知识库问答选 LangChain 还是 LlamaIndex？" 引出按场景选型、可混用。
- **演示**：跑 `examples/05_llamaindex_demo.py`，看三个能力：基础 RAG、Rerank、Router 路由。

### 第 7 章 · MCP
- **重点**：N×M→N+M 的适配地狱解决、`@mcp.tool()` 跨进程、docstring 工程。
- **互动**：问 "MCP 和 LangChain Tool 区别？" 答案：跨进程/跨语言/可复用。
- **演示**：跑 `examples/06_mcp_server.py`。

### 第 8 章 · A2A
- **重点**：三大问题（通信/发现/委派）、三种模式、Agent 不宜过多。
- **互动**：问 "多 Agent 和微服务什么关系？" 引出 "微服务化在 Agent 层重演"。
- **演示**：跑 `examples/07_a2a_demo.py`（无需 API Key，伪代码级）。

### 第 9 章 · Harness（上生产关键）
- **重点**：7 子能力、"没有 Harness 的 Agent 只是 demo"、Claude Code 案例重点讲。
- **互动**：问 "你们 Agent 上生产最难的是哪步？" 引出评估和监控。
- **演示**：跑 `examples/08_harness_demo.py`，看权限拦截 + 上下文压缩。

### 第 10 章 · Agent Skill
- **重点**：Skill = Prompt+Tool+说明+触发，和 Tool/Prompt 的粒度区别。
- **互动**：问 "Skill 和 MCP Tool 什么关系？" 答案：Skill 可包含 MCP Tool + 专家 Prompt。
- **演示**：跑 `examples/09_skill_demo.py`，看 Skill 选择命中。

### 第 11 章 · 主流框架选型与生态进阶
- **重点**：决策三问（单/多 Agent、重 RAG/重流程、跨语言栈）、框架速查表、别被营销带偏。
- **互动**：问 "拿到一个需求你怎么选框架？" 引出先问需求再选框架。
- **演示**：跑 `examples/10_framework_selection.py`，看 CrewAI 风格的角色/目标/背景定义（无需 API Key）。

### 第 12 章 · 企业级 Agent 架构
- **重点**：六层架构、每层职责、"Agent ≠ 一个 LLM 调用"。
- **互动**：问 "你们公司上 Agent 最缺哪层？" 引出工程治理层。
- **演示**：跑 `examples/11_enterprise_arch.py` 打印分层。

### 第 13 章 · 综合案例
- **重点**：两案例对比、选型原则（检索为主 vs 流程复杂）。
- **互动**：问 "你们业务更像哪个案例？" 帮学员对号入座。
- **演示**：跑 `examples/12_case_knowledge_agent.py`（需 API Key）和 `examples/13_case_dev_agent.py`（无需）。

---

## 四、常见全局提问应对

| 提问 | 应对要点 |
|---|---|
| 学这些要多久能上手？ | 跑通 examples 一周，做内部 demo 一个月，上生产看团队投入 |
| 和 Java + Spring AI 比？ | Spring AI 在追，但生态落后 Python 半年。短期 Python，长期看 Spring AI 起势 |
| Agent 会取代程序员吗？ | 不会取代，会重塑分工。会用 Agent 的淘汰不会用的 |
| 幻觉问题怎么解决？ | RAG + 工具调用 + 人工审批（Harness 的 Permission），没有银弹 |
| 为什么不用 Java 做 AI？ | 第 1 章四个原因：SDK 优先、快速实验、数据处理、工具链成熟 |
| LangChain 这么多组件怎么学？ | 90% 场景用四件套：Prompt+Model+Retriever+Runnable |
| LangGraph 和 LangChain 冲突吗？ | 不冲突，互补。LangChain 是组件库，LangGraph 是编排层 |
| MCP 现在成熟吗？ | 2024 底刚出，生态建设中。Claude Desktop/Cursor 已支持 |
| 多 Agent 会不会太复杂？ | 3-4 个为宜，别为了拆而拆。能单 Agent 干完别上多 Agent |
| 没有 Harness 能上线吗？ | 不能。demo 和生产的分水岭就是 Harness |

---

## 五、备课清单

### 课前准备
- [ ] 通读 `chapters/01~13.md`，熟悉每章内容；
- [ ] 跑通所有 `examples/`，确保 demo 可演示（无需 API Key 的至少跑 07/08/09/10_framework_selection/11_enterprise_arch/13_case_dev_agent，以及 04b 的检索部分）；
- [ ] 配置 `DEEPSEEK_API_KEY`（DeepSeek 兼容端点）；
- [ ] 渲染 `diagrams/` 下 13 张 Mermaid；
- [ ] 用 `slides-outline.md` 制作 PPT；
- [ ] 准备备用问题清单（见上表）。

### 授课节奏检查点（方案 A 完整版）
| 时间点 | 应到章节 |
|---|---|
| 0:10 | 开场完 |
| 0:40 | 第 1-2 章完 |
| 1:15 | 第 3 章完 |
| 1:55 | 第 4 章完 |
| 2:35 | 第 5 章完 |
| 3:05 | 第 6 章完 |
| 3:15 | 休息完 |
| 3:40 | 第 7 章完 |
| 4:05 | 第 8 章完 |
| 4:35 | 第 9 章完 |
| 4:55 | 第 10 章完 |
| 5:10 | 第 11 章完 |
| 5:35 | 第 12 章完 |
| 6:10 | 第 13 章完 |
| 6:20 | 结束 |

（时间为参考，本教程不强制时长，按实际调整。）

### 课后跟进
- 发 `chapters/` 给学员自学巩固；
- 按 `course-outline.md` 学习路径布置实战作业；
- 建内部群收集 bad case，持续优化。

---

## 六、授课风格提醒

- **架构师视角**：讲组件边界、数据流、职责，不深陷 API 细节；
- **技术实践导向**：每个概念配代码演示，不讲空话；
- **避免营销语言**：不吹 "颠覆/革命/赋能"，只说 "解决什么、何时用、何时别用"；
- **Java 友好**：多用 Java 类比（FastAPI≈Spring Boot、LangGraph≈Camunda、MCP≈gRPC）；
- **鼓励提问**：AI 概念多，学员容易懵，留足提问时间。
