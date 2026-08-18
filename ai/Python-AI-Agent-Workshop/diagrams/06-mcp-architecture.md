# 06 - MCP 协议架构图

```mermaid
graph TB
    subgraph "MCP 协议架构"
        LLM[LLM 应用<br/>Claude / GPT / GLM] --> Client

        subgraph "MCP Client 客户端"
            Client[MCP Client<br/>协议客户端<br/>发起工具请求]
            ClientTransport[Transport<br/>通信层<br/>stdio / SSE / HTTP]
        end

        Client --> ClientTransport
        ClientTransport -->|MCP Protocol| ServerTransport

        subgraph "MCP Server 服务端"
            ServerTransport[Transport<br/>通信层]
            Server[MCP Server<br/>协议服务端<br/>注册并暴露能力]
            ServerTool[Tool<br/>工具能力<br/>可被 LLM 调用]
            ServerResource[Resource<br/>资源<br/>文件 / 数据]
            ServerPrompt[Prompt<br/>提示词模板<br/>预定义模板]
        end

        ServerTransport --> Server
        Server --> ServerTool
        Server --> ServerResource
        Server --> ServerPrompt

        ServerTool -->|连接| DB[(数据库<br/>PostgreSQL / MySQL)]
        ServerTool -->|连接| API[外部 API<br/>GitHub / Slack / Jira]
        ServerTool -->|连接| File[文件系统<br/>本地 / 远程]
        ServerTool -->|连接| Code[代码执行<br/>沙箱环境]
    end

    style Client fill:#e1f5fe
    style Server fill:#fff3e0
    style ServerTool fill:#e8f5e9
```

## MCP 解决的核心问题

### Before MCP — 工具集成碎片化

```
LLM App A → 自定义 Tool 接口 → Tool 1, Tool 2, Tool 3
LLM App B → 另一套 Tool 接口 → Tool 1, Tool 2, Tool 3
LLM App C → 又一套 Tool 接口 → Tool 1, Tool 2, Tool 3
```

每个应用都需要重新集成工具，工具无法跨应用复用。

### After MCP — 标准化协议

```
LLM App A → MCP Client → MCP Server 1 (DB)
                       → MCP Server 2 (API)
                       → MCP Server 3 (File)
LLM App B → MCP Client → 同样的 MCP Servers
LLM App C → MCP Client → 同样的 MCP Servers
```

一次实现，处处可用。类似 JDBC 统一了数据库访问。

## MCP vs 传统 API 集成

| 特性 | 传统 API 集成 | MCP |
|------|-------------|-----|
| 接口定义 | 每个 API 自定义 | 标准化 Schema |
| 发现机制 | 手动文档 | 自动发现（list_tools） |
| 调用方式 | 各自实现 | 统一协议（call_tool） |
| 跨应用复用 | 不可复用 | 完全复用 |
| Java 类比 | 各数据库各自驱动 | JDBC 统一接口 |

## MCP 通信流程

```mermaid
sequenceDiagram
    participant LLM as LLM 应用
    participant Client as MCP Client
    participant Server as MCP Server
    participant Resource as 外部资源

    LLM->>Client: 1. 初始化连接
    Client->>Server: 2. initialize handshake
    Server-->>Client: 3. 返回能力声明
    Client->>Server: 4. list_tools()
    Server-->>Client: 5. 返回工具列表
    LLM->>Client: 6. 用户提问
    LLM->>Client: 7. LLM 决定调用工具
    Client->>Server: 8. call_tool(name, args)
    Server->>Resource: 9. 执行实际操作
    Resource-->>Server: 10. 返回结果
    Server-->>Client: 11. 返回工具结果
    Client-->>LLM: 12. 提供结果给 LLM
    LLM-->>LLM: 13. 生成最终回答
```
