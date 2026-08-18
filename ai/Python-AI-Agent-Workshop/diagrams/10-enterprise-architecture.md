# 10 - 企业级 Agent 架构图

```mermaid
graph TB
    User[终端用户] --> Gateway[API Gateway<br/>网关层]

    subgraph "Agent Application Layer 应用层"
        Gateway --> AgentApp[Agent Application<br/>Agent 应用核心]
        AgentApp --> SessionMgr[Session Manager<br/>会话管理]
        AgentApp --> SkillRouter[Skill Router<br/>技能路由]
        AgentApp --> WorkflowEngine[Workflow Engine<br/>工作流引擎<br/>LangGraph]
    end

    subgraph "Core Services Layer 核心服务层"
        LLM[LLM Service<br/>大模型服务<br/>GPT / Claude / GLM]
        Memory[Memory Service<br/>记忆服务<br/>短期 + 长期]
        Knowledge[Knowledge Service<br/>知识服务<br/>RAG + Vector DB]
        ToolHub[Tool Hub<br/>工具中心<br/>Tool Calling]
    end

    subgraph "Protocol Layer 协议层"
        MCPClient[MCP Client<br/>MCP 客户端]
        A2AHub[A2A Hub<br/>Agent 通信中心]
    end

    subgraph "Engineering Layer 工程层"
        Harness[Agent Harness<br/>运行时基础设施]
        Eval[Evaluation<br/>质量评估]
        Monitor[Observability<br/>可观测性]
        Guard[Security & Permission<br/>安全与权限]
    end

    subgraph "Enterprise Systems 企业系统"
        DB[(企业数据库)]
        ERP[ERP / CRM]
        Git[Git 仓库]
        CI/CD[CI/CD 流水线]
        Wiki[企业知识库 / Wiki]
        Slack[Slack / 飞书 / 钉钉]
    end

    AgentApp --> LLM
    AgentApp --> Memory
    AgentApp --> Knowledge
    AgentApp --> ToolHub

    ToolHub --> MCPClient
    MCPClient --> DB
    MCPClient --> ERP
    MCPClient --> Git
    MCPClient --> CI/CD

    AgentApp --> A2AHub
    A2AHub --> Agent2[其他 Agent]

    Knowledge --> Wiki
    Knowledge --> VectorDB[(Vector DB)]

    AgentApp --> Harness
    Harness --> Eval
    Harness --> Monitor
    Harness --> Guard

    AgentApp --> Slack

    style AgentApp fill:#e1f5fe,stroke:#0288d1,stroke-width:3px
    style Harness fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    style MCPClient fill:#e8f5e9,stroke:#388e3c,stroke-width:2px
    style A2AHub fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
```

## 各层职责说明

| 层级 | 组件 | 职责 | Java 类比 |
|------|------|------|-----------|
| 应用层 | Agent Application | 接收用户请求、路由分发、工作流编排 | Spring Boot Application |
| 核心服务层 | LLM Service | 大模型调用 | 外部服务客户端 |
| 核心服务层 | Memory Service | 对话记忆管理 | Redis / Session Store |
| 核心服务层 | Knowledge Service | 知识检索（RAG） | Elasticsearch Service |
| 核心服务层 | Tool Hub | 工具注册与调用 | Service Registry |
| 协议层 | MCP Client | 标准化工具调用 | JDBC Client |
| 协议层 | A2A Hub | Agent 间通信 | 消息总线 / RPC |
| 工程层 | Harness | 运行时基础设施 | Spring Runtime + JVM |
| 工程层 | Evaluation | 质量评估 | 测试框架 |
| 工程层 | Observability | 监控追踪 | SkyWalking / Zipkin |
| 工程层 | Security | 权限控制 | Spring Security |

## 关键设计决策

### 1. 模型选型
- 通用对话：GPT-4o / Claude 3.5
- 代码生成：Claude 3.5 Sonnet
- 中文场景：GLM-4 / Qwen
- 成本敏感：开源模型 + 本地部署

### 2. 上下文策略
- 短对话：全量保留
- 长对话：摘要压缩 + 向量检索
- 关键信息：写入长期记忆

### 3. 工具治理
- 注册制：工具必须注册才能调用
- 权限分级：只读 / 读写 / 危险操作
- 审计日志：所有工具调用记录

### 4. 安全合规
- 输入过滤：Prompt Injection 防护
- 输出审查：敏感信息过滤
- 数据隔离：租户间数据隔离
