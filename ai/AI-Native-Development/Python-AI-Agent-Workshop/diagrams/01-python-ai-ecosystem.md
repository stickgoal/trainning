# 01 - Python AI 生态技术栈全景图

```mermaid
graph TB
    subgraph "应用层 Application Layer"
        FastAPI[FastAPI<br/>Web API 框架]
        Pydantic[Pydantic<br/>数据校验/序列化]
        AsyncIO[AsyncIO<br/>异步并发]
    end

    subgraph "LLM 编排层 Orchestration Layer"
        LangChain[LangChain<br/>LLM 应用框架]
        LangGraph[LangGraph<br/>Agent 工作流引擎]
        LlamaIndex[LlamaIndex<br/>RAG 专用框架]
    end

    subgraph "多 Agent 层 Multi-Agent Layer"
        AutoGen[AutoGen<br/>微软多 Agent 框架]
        CrewAI[CrewAI<br/>角色扮演多 Agent]
    end

    subgraph "RAG 基础设施层 RAG Infrastructure"
        Embedding[Embedding Model<br/>文本向量化]
        Milvus[Milvus<br/>分布式向量数据库]
        Chroma[Chroma<br/>轻量向量数据库]
        FAISS[FAISS<br/>本地向量检索]
    end

    subgraph "协议与标准层 Protocol Layer"
        MCP[MCP<br/>模型上下文协议<br/>工具标准化]
        A2A[A2A<br/>Agent 间通信协议]
        ToolCalling[Tool Calling<br/>LLM 工具调用]
    end

    subgraph "工程化层 Engineering Layer"
        Harness[Agent Harness<br/>运行时基础设施]
        Skill[Agent Skill<br/>能力封装]
        Eval[Evaluation<br/>质量评估]
        Observability[Observability<br/>可观测性]
    end

    subgraph "LLM 服务层 LLM Provider"
        OpenAI[OpenAI GPT]
        Claude[Anthropic Claude]
        GLM[智谱 GLM]
        Local[本地开源模型<br/>Llama / Qwen]
    end

    FastAPI --> LangChain
    Pydantic --> LangChain
    AsyncIO --> LangChain
    LangChain --> LangGraph
    LangChain --> LlamaIndex
    LangChain --> ToolCalling
    LangGraph --> AutoGen
    LangGraph --> CrewAI
    LlamaIndex --> Embedding
    Embedding --> Milvus
    Embedding --> Chroma
    Embedding --> FAISS
    ToolCalling --> MCP
    MCP --> A2A
    LangGraph --> Harness
    Harness --> Skill
    Harness --> Eval
    Harness --> Observability
    LangChain --> OpenAI
    LangChain --> Claude
    LangChain --> GLM
    LangChain --> Local
```

## Java 工程师对照

| Python AI 生态 | Java 生态类比 |
|---------------|-------------|
| FastAPI | Spring Boot |
| Pydantic | Bean Validation (JSR-303) |
| AsyncIO | CompletableFuture / Reactor |
| LangChain | Spring AI / 自定义编排层 |
| LangGraph | Spring StateMachine |
| Vector DB | Elasticsearch (向量检索) |
| MCP | JDBC / 标准化驱动接口 |
| Harness | JVM + Spring Runtime |
