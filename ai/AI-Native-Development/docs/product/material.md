# Material: Python AI Agent 开发前沿技术快速扫盲

> 素材来源：工作区 `README.md` / `course-outline.md` / `slides-outline.md` / `teacher-guide.md` / `chapters/`（13 章教程正文）/ `examples/`（15 个可运行示例）。

## 1. Overview
- 定位：面向 Java 后端工程师的 Python AI 应用开发生态认知教程（"扫盲"）。
- 目标：建立一张 Python AI Engineering 技术地图，看懂 LLM 应用与 Agent 系统如何搭建、各组件职责、如何组合成可运行应用。

## 2. Background
- 受众：3 年+ Java 后端，能读 Python，未亲手搭过 LLM 应用。
- 关键认知锚点：把 LLM 当作"通过 HTTP 调用的推理服务"，把 Agent 当作"用 LLM 做决策、会调工具的 Spring Service"。

## 3. Key Info
- 结构：4 阶段 13 章。A 认知地基（1-2）→ B 核心组件（3-6）→ C 工程化（7-11）→ D 架构实战（12-13）。
- 三大核心问题贯穿全篇：①生态是什么 ②每个组件解决什么 ③如何组合成应用。
- 五层技术栈：Web 框架 / 编排框架 / LLM SDK / 向量检索 / Agent 运行时（均有 Java 类比）。

## 4. Evidence
- 示例：`examples/00_setup.py` 8 行调通 LLM（DeepSeek OpenAI 兼容接口）。
- 示例：`examples/03_langgraph_demo.py` StateGraph + 条件边 + 循环 + 人工介入。
- 示例：`examples/04_rag_demo.py` 6 步管线 Document→Chunk→Embedding→VectorDB→Retriever→LLM。
- 示例：`examples/06_mcp_server.py` FastMCP 暴露工具 + Client 发现调用。

## 5. Analysis
- 组件边界与 Java 类比：FastAPI≈Spring Boot、LangGraph≈Camunda、MCP≈gRPC、Skill≈带文档的 Spring Bean。
- 选型三问：单/多 Agent、重 RAG/重流程、跨语言栈。

## 6. Outlook
- 学习路径：四周从跑通 examples → 内部 RAG 问答 → LangGraph Agent → MCP+Skill 实战。

## Summary
- 高权威来源：本项目教程正文（自产，可自读自学）；无外部网络素材需求；代码全部取自 `examples/` 真实文件。
