# 11 - 综合案例架构图

## 案例 1：企业知识库 Agent

```mermaid
graph TB
    User[企业员工<br/>提问："公司的差旅报销流程是什么？"] --> WebUI[Web 界面<br/>FastAPI]

    subgraph "企业知识库 Agent"
        WebUI --> Agent[Knowledge Base Agent<br/>知识库 Agent]

        subgraph "检索增强 Retrieval"
            Agent --> Retriever[RAG Retriever<br/>检索器]
            Retriever --> VectorDB[(Vector DB<br/>Milvus<br/>存储企业文档向量)]
            Retriever --> BM25[BM25 Retriever<br/>关键词检索]
            VectorDB --> Hybrid[Hybrid Search<br/>混合检索]
            BM25 --> Hybrid
            Hybrid --> Rerank[Cohere Rerank<br/>重排序]
        end

        subgraph "知识源 Knowledge Sources"
            CompanyDocs[企业制度文档<br/>PDF / Word]
            Wiki[企业 Wiki<br/>Confluence]
            FAQ[FAQ 知识库]
            CompanyDocs -->|Indexing| VectorDB
            Wiki -->|Indexing| VectorDB
            FAQ -->|Indexing| VectorDB
        end

        subgraph "MCP 工具集成"
            Agent --> MCPClient[MCP Client]
            MCPClient --> HRServer[HR MCP Server<br/>查询人事信息]
            MCPClient --> FinanceServer[Finance MCP Server<br/>查询财务流程]
        end

        Rerank --> Agent
        Agent --> LLM[LLM<br/>GLM-4 / Claude]
        LLM -->|生成回答<br/>附带引用来源| WebUI
        WebUI --> User

        Memory[(Memory Store<br/>对话记忆)] --> Agent
    end

    style Agent fill:#e1f5fe,stroke:#0288d1,stroke-width:3px
    style VectorDB fill:#e8f5e9
    style MCPClient fill:#fff3e0
    style LLM fill:#f3e5f5
```

### 技术选型说明

| 组件 | 技术选择 | 选择原因 |
|------|---------|---------|
| Web 框架 | FastAPI | 异步高性能，自动生成 API 文档 |
| LLM 编排 | LangChain | 成熟的 RAG 管线支持 |
| 向量数据库 | Milvus | 企业级分布式，支持大规模文档 |
| 检索策略 | Hybrid Search | 关键词 + 语义混合，提高召回率 |
| 重排序 | Cohere Rerank | 提升检索精度 |
| 工具协议 | MCP | 标准化接入 HR/Finance 系统 |
| 模型 | GLM-4 / Claude | 中文理解强 / 推理能力强 |

---

## 案例 2：软件研发 Agent

```mermaid
graph TB
    Dev[开发者<br/>"实现用户注册 API"] --> IDE[IDE / CLI<br/>交互入口]

    subgraph "软件研发 Agent 系统"
        IDE --> Coordinator[Coordinator Agent<br/>协调 Agent<br/>LangGraph 工作流]

        subgraph "LangGraph 工作流"
            Coordinator --> Understand[需求理解节点<br/>分析开发需求]
            Understand --> Design[设计节点<br/>API 设计 + 数据模型]
            Design --> Code[编码节点<br/>生成代码]
            Code --> Test[测试节点<br/>生成并执行测试]
            Test --> Review[审查节点<br/>代码审查]
            Review -->|需修改| Code
            Review -->|通过| Deploy[部署节点<br/>生成部署配置]
            Deploy --> Done[交付完成]
        end

        subgraph "Skill 能力层"
            CodingSkill[Coding Skill<br/>编码技能<br/>代码生成 + 规范]
            TestingSkill[Testing Skill<br/>测试技能<br/>测试生成 + 执行]
            ReviewSkill[Review Skill<br/>审查技能<br/>代码审查 + 安全检查]
            DeploySkill[Deployment Skill<br/>部署技能<br/>Docker + CI/CD]
        end

        subgraph "Harness 运行时"
            CtxMgr[Context Manager<br/>管理代码文件上下文]
            ToolSandbox[Tool Sandbox<br/>代码执行沙箱]
            GitTool[Git Tool<br/>版本控制]
            Monitor[Monitor<br/>执行监控与日志]
        end

        Coordinator --> CodingSkill
        Coordinator --> TestingSkill
        Coordinator --> ReviewSkill
        Coordinator --> DeploySkill

        CodingSkill --> ToolSandbox
        TestingSkill --> ToolSandbox
        CodingSkill --> GitTool
        DeploySkill --> GitTool

        CtxMgr --> Coordinator
        Monitor --> Coordinator
    end

    Done -->|代码 + 测试 + 文档| Dev

    style Coordinator fill:#e1f5fe,stroke:#0288d1,stroke-width:3px
    style CodingSkill fill:#fff3e0
    style TestingSkill fill:#e8f5e9
    style ToolSandbox fill:#fce4ec
```

### 技术选型说明

| 组件 | 技术选择 | 选择原因 |
|------|---------|---------|
| 工作流引擎 | LangGraph | 支持循环、条件分支、状态管理 |
| 能力封装 | Skill | 将编码/测试/审查封装为可复用技能 |
| 工具执行 | Sandbox | 安全隔离代码执行环境 |
| 版本控制 | Git Integration | 自动提交代码变更 |
| Harness | 自研 | 上下文管理 + 工具沙箱 + 监控 |
| 模型 | Claude 3.5 Sonnet | 代码生成能力最强 |

### 两个案例对比

| 维度 | 企业知识库 Agent | 软件研发 Agent |
|------|-----------------|---------------|
| 核心能力 | 知识检索 + 问答 | 代码生成 + 测试 + 部署 |
| 编排框架 | LangChain | LangGraph |
| 检索需求 | 重（RAG 核心） | 轻（代码文件上下文） |
| 工具复杂度 | 低（查询为主） | 高（执行代码、Git操作） |
| Harness 需求 | 中等 | 高（沙箱 + 监控 + 回滚） |
| 安全要求 | 数据隔离 | 代码执行安全 |
