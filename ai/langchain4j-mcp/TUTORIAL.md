# LangChain4j MCP 完整教程

> 本教程基于 LangChain4j 1.16.0-beta26，全面介绍如何使用 MCP (Model Context Protocol) 让 AI Service 调用外部工具。

---

## 目录

1. [MCP 协议简介](#1-mcp-协议简介)
2. [核心概念与架构](#2-核心概念与架构)
3. [环境准备](#3-环境准备)
4. [MCP Server 开发](#4-mcp-server-开发)
5. [stdio 传输方式](#5-stdio-传输方式)
6. [HTTP/SSE 传输方式 (Legacy)](#6-httpsse-传输方式-legacy)
7. [Streamable HTTP 传输方式 (推荐)](#7-streamable-http-传输方式-推荐)
8. [MCP Tool Provider 详解](#8-mcp-tool-provider-详解)
9. [AI Service + MCP 集成](#9-ai-service--mcp-集成)
10. [多 MCP Provider 组合](#10-多-mcp-provider-组合)
11. [工具过滤与名称映射](#11-工具过滤与名称映射)
12. [Spring Boot 集成最佳实践](#12-spring-boot-集成最佳实践)
13. [常见问题与排错](#13-常见问题与排错)

---

## 1. MCP 协议简介

### 什么是 MCP？

Model Context Protocol (MCP) 是 Anthropic 提出的开放协议，旨在标准化 LLM 应用与外部数据源/工具之间的通信。它就像 AI 领域的"USB-C"——统一了 AI 模型与外部世界的连接方式。

### MCP 的核心价值

- **标准化接口**：所有工具服务器遵循同一协议，AI 客户端无需为每个工具编写适配代码
- **动态发现**：客户端运行时自动发现服务器提供的工具，无需硬编码
- **解耦合**：工具实现与 AI 逻辑完全分离，可独立开发和部署
- **生态共享**：一个 MCP Server 可被任何支持 MCP 的 AI 客户端使用

### MCP 架构

```
┌─────────────┐     MCP Protocol     ┌─────────────┐
│  MCP Client  │ ←─────────────────→ │  MCP Server  │
│ (LangChain4j)│                     │  (Node.js)   │
└──────┬───────┘                     └──────┬───────┘
       │                                     │
       │ Tool Calling                        │ Tool Execution
       │                                     │
┌──────▼───────┐                     ┌──────▼───────┐
│   LLM (GPT)  │                     │  Tools/APIs  │
└──────────────┘                     └──────────────┘
```

---

## 2. 核心概念与架构

### MCP 传输层 (Transport)

MCP 支持多种传输方式，LangChain4j 实现了以下四种：

| 传输方式 | 类名 | 适用场景 | 状态 |
|----------|------|----------|------|
| stdio | `StdioMcpTransport` | 本地子进程通信 | 稳定 |
| HTTP/SSE | `HttpMcpTransport` | 远程服务器，跨网络 | 已弃用 |
| Streamable HTTP | `StreamableHttpMcpTransport` | 远程服务器，现代标准 | 推荐 |
| WebSocket | `WebSocketMcpTransport` | 实时双向通信 | 实验性 |
| Docker stdio | `DockerMcpTransport` | 容器化 MCP Server | 稳定 |

### MCP 客户端 (McpClient)

`McpClient` 是与 MCP Server 通信的核心接口：

```java
McpClient client = DefaultMcpClient.builder()
    .key("my-client")           // 可选但推荐，多客户端时用于区分
    .transport(transport)       // 传输层
    .build();
```

### MCP 工具提供者 (McpToolProvider)

`McpToolProvider` 将 MCP 工具转换为 LangChain4j 的 `ToolProvider` 接口：

```java
McpToolProvider toolProvider = McpToolProvider.builder()
    .mcpClients(client)         // 一个或多个 MCP Client
    .build();
```

---

## 3. 环境准备

### 3.1 Maven 依赖

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-mcp</artifactId>
    <version>1.16.0-beta26</version>
</dependency>
```

完整的 pom.xml 还需要：
```xml
<!-- LangChain4j 核心 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-spring-boot-starter</artifactId>
    <version>1.16.0-beta26</version>
</dependency>

<!-- OpenAI 兼容的 LLM -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>
    <version>1.16.0-beta26</version>
</dependency>
```

### 3.2 application.yml 配置

```yaml
langchain4j:
  open-ai:
    chat-model:
      api-key: your-api-key
      base-url: https://apihub.agnes-ai.com/v1
      model-name: agnes-2.0-flash
      temperature: 0.7
      log-requests: true
      log-responses: true
```

### 3.3 Node.js 环境 (用于 stdio 传输)

```bash
# 检查 Node.js 版本 (需要 18+)
node --version

# 安装 MCP Server 依赖
cd src/main/resources/mcp-server
npm install
```

---

## 4. MCP Server 开发

### 4.1 为什么需要 MCP Server？

MCP Server 是工具的提供方。LangChain4j 作为 MCP Client 消费这些工具。本项目内置了一个 Node.js MCP Server。

### 4.2 MCP Server 代码 (weather-mcp-server.js)

```javascript
import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
    CallToolRequestSchema,
    ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";

// 创建 MCP Server
const server = new Server(
    { name: "weather-mcp-server", version: "1.0.0" },
    { capabilities: { tools: {} } }
);

// 注册工具列表
server.setRequestHandler(ListToolsRequestSchema, async () => {
    return {
        tools: [
            {
                name: "get_weather",
                description: "查询指定城市的天气信息",
                inputSchema: {
                    type: "object",
                    properties: {
                        city: { type: "string", description: "城市名称" }
                    },
                    required: ["city"]
                }
            },
            {
                name: "calculate",
                description: "执行数学计算",
                inputSchema: {
                    type: "object",
                    properties: {
                        expression: { type: "string", description: "数学表达式" }
                    },
                    required: ["expression"]
                }
            }
        ]
    };
});

// 处理工具调用
server.setRequestHandler(CallToolRequestSchema, async (request) => {
    const { name, arguments: args } = request.params;
    // ... 工具实现 ...
});

// 启动 stdio 传输
const transport = new StdioServerTransport();
await server.connect(transport);
```

### 4.3 MCP Server 的工作流程

1. **启动**：MCP Client 通过 stdio 启动 MCP Server 子进程
2. **握手**：Client 和 Server 完成 MCP 协议握手
3. **工具发现**：Client 调用 `ListTools` 获取可用工具列表
4. **工具调用**：LLM 决定使用工具时，Client 通过 `CallTool` 请求执行
5. **结果返回**：Server 执行工具逻辑，返回结果给 Client

---

## 5. stdio 传输方式

### 5.1 工作原理

stdio 传输是最简单的本地集成方式：
- MCP Client 启动一个子进程（如 Node.js 脚本）
- 通过标准输入(stdin)和标准输出(stdout)进行 JSON-RPC 通信
- 无需网络端口，适合本地开发和部署

### 5.2 代码实现

**传输层：**
```java
McpTransport transport = StdioMcpTransport.builder()
    .command(List.of("node", "/path/to/weather-mcp-server.js"))
    .logEvents(true)  // 日志记录 MCP 通信事件
    .build();
```

**客户端：**
```java
McpClient client = DefaultMcpClient.builder()
    .key("stdio-weather-client")
    .transport(transport)
    .build();
```

**工具提供者：**
```java
McpToolProvider toolProvider = McpToolProvider.builder()
    .mcpClients(client)
    .build();
```

**AI Service 组装：**
```java
StdioAgent agent = AiServices.builder(StdioAgent.class)
    .chatModel(chatModel)
    .toolProvider(toolProvider)
    .build();
```

### 5.3 路径解析

在 Spring Boot 中，MCP Server 脚本通常打包在 classpath 中。需要从 classpath 解析到文件系统路径：

```java
private String resolveScriptPath(String resourcePath) throws Exception {
    var resource = getClass().getClassLoader().getResource(resourcePath);
    if (resource != null) {
        return new File(resource.toURI()).getAbsolutePath();
    }
    // 开发环境回退
    return new File("src/main/resources/" + resourcePath).getAbsolutePath();
}
```

### 5.4 测试

```bash
curl -X POST "http://localhost:9060/api/stdio/ask?question=北京天气怎么样"
```

---

## 6. HTTP/SSE 传输方式 (Legacy)

### 6.1 工作原理

HTTP/SSE 传输通过两个通道通信：
- **SSE 通道**：客户端连接 SSE URL，接收服务器推送的事件
- **POST 通道**：客户端通过服务器提供的 POST URL 发送请求

### 6.2 代码实现

```java
McpTransport transport = HttpMcpTransport.builder()
    .sseUrl("http://localhost:3001/sse")
    .logRequests(true)
    .logResponses(true)
    .build();
```

### 6.3 注意事项

> ⚠️ HTTP/SSE 传输已被弃用，将在未来版本移除。建议使用 Streamable HTTP 替代。

---

## 7. Streamable HTTP 传输方式 (推荐)

### 7.1 工作原理

Streamable HTTP 是 MCP 2025-06-18 规范定义的标准 HTTP 传输方式：
- 客户端发送 HTTP 请求到服务器的 POST 端点
- 服务器返回常规 HTTP 响应，或打开 SSE 流发送多个响应
- 可选启用 subsidiaryChannel 接收服务器主动推送

### 7.2 代码实现

```java
McpTransport transport = StreamableHttpMcpTransport.builder()
    .url("http://localhost:3002/mcp")
    .logRequests(true)
    .logResponses(true)
    .build();
```

### 7.3 Subsidiary Channel (可选)

```java
McpTransport transport = StreamableHttpMcpTransport.builder()
    .url("http://localhost:3002/mcp")
    .subsidiaryChannel(true)  // 启用辅助 SSE 流接收服务器推送
    .build();
```

---

## 8. MCP Tool Provider 详解

### 8.1 基本 usage

```java
// 单个 MCP Client
McpToolProvider provider = McpToolProvider.builder()
    .mcpClients(client)
    .build();

// 多个 MCP Client
McpToolProvider provider = McpToolProvider.builder()
    .mcpClients(client1, client2)
    .failIfOneServerFails(false)  // 某个 Server 失败不影响其他
    .build();
```

### 8.2 与 AI Service 的绑定

方式一：使用 `toolProvider` (推荐)
```java
Bot bot = AiServices.builder(Bot.class)
    .chatModel(model)
    .toolProvider(toolProvider)
    .build();
```

方式二：使用 `tools` (Map 方式)
```java
Map<ToolSpecification, ToolExecutor> tools = mcpClient.listTools().stream()
    .collect(Collectors.toMap(
        tool -> tool,
        tool -> new McpToolExecutor(mcpClient)
    ));

Bot bot = AiServices.builder(Bot.class)
    .chatModel(model)
    .tools(tools)
    .build();
```

---

## 9. AI Service + MCP 集成

### 9.1 AI Service 接口定义

```java
public interface WeatherAssistant {

    @SystemMessage("""
        你是一个专业的天气助手。
        你可以调用 get_weather 工具查询天气，
        调用 calculate 工具执行计算。
        """)
    @UserMessage("{{question}}")
    String chat(String question);
}
```

### 9.2 Spring Boot 集成

```java
@Service
public class WeatherService {

    private final WeatherAssistant assistant;

    @Autowired
    public WeatherService(ChatModel chatModel,
                          @Qualifier("stdioToolProvider") McpToolProvider toolProvider) {
        this.assistant = AiServices.builder(WeatherAssistant.class)
            .chatModel(chatModel)
            .toolProvider(toolProvider)
            .build();
    }

    public String ask(String question) {
        return assistant.chat(question);
    }
}
```

### 9.3 工作流程

```
用户提问 → AI Service → LLM 分析 → 决定调用 MCP 工具
                                         ↓
                                    McpToolProvider
                                         ↓
                                    McpClient (stdio/http/streamable)
                                         ↓
                                    MCP Server 执行工具
                                         ↓
                                    返回结果 → LLM 生成最终回答 → 返回用户
```

---

## 10. 多 MCP Provider 组合

### 10.1 场景

当你需要同时使用来自不同 MCP Server 的工具时，可以将多个 Client 组合到一个 ToolProvider 中。

### 10.2 代码实现

```java
@Configuration
public class MultiProviderConfig {

    @Bean(name = "multiToolProvider")
    public McpToolProvider multiToolProvider(
            @Qualifier("stdioMcpClient") McpClient stdioClient,
            @Qualifier("httpMcpClient") McpClient httpClient) {

        return McpToolProvider.builder()
                .mcpClients(stdioClient, httpClient)
                .failIfOneServerFails(false)
                .build();
    }
}
```

### 10.3 关键参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `failIfOneServerFails` | false | 某个 Server 失败时是否抛异常 |

---

## 11. 工具过滤与名称映射

### 11.1 按名称过滤

```java
McpToolProvider provider = McpToolProvider.builder()
    .mcpClients(client)
    .filterToolNames("get_weather", "calculate")  // 只暴露指定工具
    .build();
```

### 11.2 自定义过滤

```java
McpToolProvider provider = McpToolProvider.builder()
    .mcpClients(client1, client2)
    .filter((mcpClient, tool) ->
        !tool.name().startsWith("echo") ||
        mcpClient.key().equals("numeric-mcp"))
    .build();
```

### 11.3 工具名称映射

当多个 Server 暴露同名工具时，可以使用 `toolNameMapper` 避免冲突：

```java
McpToolProvider provider = McpToolProvider.builder()
    .mcpClients(client1, client2)
    .toolNameMapper((client, toolSpec) ->
        client.key() + "_" + toolSpec.name())  // 前缀重命名
    .build();
```

---

## 12. Spring Boot 集成最佳实践

### 12.1 配置分离

将 MCP 配置放在 application.yml 中，通过 `@Value` 注入：

```yaml
mcp:
  stdio:
    server-script: "mcp-server/weather-mcp-server.js"
  http:
    sse-url: "http://localhost:3001/sse"
  streamable:
    url: "http://localhost:3002/mcp"
```

### 12.2 Bean 命名

使用 `@Bean(name = "xxx")` 和 `@Qualifier` 明确区分不同传输方式的 Bean：

```java
@Bean(name = "stdioMcpClient")
public McpClient stdioMcpClient() { ... }

@Bean(name = "stdioToolProvider")
public McpToolProvider stdioToolProvider() { ... }
```

### 12.3 生命周期管理

MCP Client 持有子进程或网络连接，建议实现 `DisposableBean` 进行清理：

```java
@Bean(destroyMethod = "close")
public McpClient stdioMcpClient() { ... }
```

### 12.4 日志配置

启用 MCP 通信日志有助于调试：

```java
StdioMcpTransport.builder()
    .logEvents(true)        // 记录 stdio 通信事件
    .build();

StreamableHttpMcpTransport.builder()
    .logRequests(true)      // 记录请求
    .logResponses(true)     // 记录响应
    .build();
```

---

## 13. 常见问题与排错

### Q1: MCP Server 启动失败

**症状**: `StdioMcpTransport` 启动时报 "command not found"

**解决**:
1. 确认 Node.js 已安装且在 PATH 中
2. 确认 `npm install` 已在 mcp-server 目录执行
3. 检查脚本路径是否正确（使用绝对路径）

### Q2: 工具调用超时

**症状**: LLM 决定调用工具但长时间无响应

**解决**:
1. 启用 `logEvents(true)` / `logRequests(true)` 查看通信日志
2. 检查 MCP Server 是否正常处理请求
3. 检查网络连接（HTTP/Streamable 传输时）

### Q3: HTTP/SSE 连接失败

**症状**: `HttpMcpTransport` 连接超时

**解决**:
1. 确认 MCP Server 已启动并监听正确端口
2. 检查 SSE URL 是否正确
3. 检查防火墙设置

### Q4: 多 Provider 工具冲突

**症状**: 多个 MCP Server 暴露同名工具导致冲突

**解决**:
1. 使用 `filterToolNames` 只暴露需要的工具
2. 使用 `toolNameMapper` 重命名工具
3. 使用 `filter` 自定义过滤逻辑

### Q5: Spring Boot 启动时 MCP Client 初始化失败

**症状**: 应用启动失败，MCP Client Bean 创建异常

**解决**:
1. 使用 `@Lazy` 延迟初始化 MCP Client
2. 在测试中使用 `@TestPropertySource` 排除 MCP 配置
3. 确认 MCP Server 依赖已安装

---

## 总结

LangChain4j 的 MCP 模块提供了标准化的方式让 AI Service 调用外部工具。通过三种传输方式（stdio、HTTP/SSE、Streamable HTTP），可以覆盖从本地开发到生产部署的各种场景。核心模式是：

```
MCP Transport → Mcp Client → McpToolProvider → AiServices → AI Service
```

掌握这一链路，就能让任何 MCP 兼容的工具服务器为你的 AI 应用提供能力。
