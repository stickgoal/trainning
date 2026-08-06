# 02 - LLM 应用架构全景图

```mermaid
graph LR
    User[用户输入<br/>User Input] --> App[Agent Application<br/>应用层]

    subgraph "Agent Application 核心"
        Prompt[Prompt Manager<br/>提示词管理]
        Context[Context Manager<br/>上下文窗口管理]
        Memory[Memory Store<br/>记忆存储<br/>短期 + 长期]
        Router[Tool Router<br/>工具路由]
    end

    App --> Prompt
    App --> Context
    App --> Memory
    App --> Router

    Prompt --> LLM[LLM<br/>大语言模型]
    Context --> LLM
    Memory --> LLM

    Router -->|Tool Calling| Tools[Tools<br/>工具集]
    Router -->|RAG| Knowledge[Knowledge Base<br/>知识库]
    Router -->|MCP| MCPServers[MCP Servers<br/>外部服务]

    LLM -->|生成响应| App
    App -->|输出| User

    subgraph "知识层 Knowledge Layer"
        VectorDB[(Vector DB<br/>向量数据库)]
        Retriever[Retriever<br/>检索器]
        Docs[Documents<br/>文档]
    end

    Knowledge --> Retriever
    Retriever --> VectorDB
    Docs -->|Embedding| VectorDB

    subgraph "工具层 Tool Layer"
        Search[搜索引擎]
        CodeExec[代码执行器]
        DB[数据库]
        API[外部 API]
    end

    Tools --> Search
    Tools --> CodeExec
    Tools --> DB
    Tools --> API

    subgraph "LLM Provider"
        GPT[OpenAI GPT]
        Claude[Anthropic Claude]
        Local[本地模型]
    end

    LLM --> GPT
    LLM --> Claude
    LLM --> Local
```

## 核心概念说明

| 概念 | 英文 | 一句话解释 | Java 类比 |
|------|------|-----------|-----------|
| Prompt | 提示词 | 给 LLM 的输入指令 | 请求参数 |
| Token | 词元 | LLM 处理的最小单位 | 字符编码 |
| Context Window | 上下文窗口 | LLM 单次可处理的 Token 上限 | 缓冲区大小 |
| Embedding | 嵌入向量 | 文本的语义数学表示 | 对象序列化 |
| Retrieval | 检索 | 从知识库获取相关信息 | 数据库查询 |
| Tool Calling | 工具调用 | LLM 请求执行外部工具 | RPC 调用 |
| Memory | 记忆 | 跨轮对话的状态保持 | Session 管理 |
| Agent | 智能体 | LLM + 工具 + 推理循环 | 状态机 + 服务编排 |
