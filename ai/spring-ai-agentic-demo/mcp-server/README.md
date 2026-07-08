# agentic-mcp-server

电商售后工具集 MCP Server，基于 Spring AI 1.0.0 + Spring Boot 3.5.3，以 **stdio** 传输模式运行。

作为 `spring-ai-agentic-demo` 主项目的子模块，它提供一个独立的 MCP Server 进程，暴露与主应用 `AfterSalesTools` 功能一致的售后查询工具，供主项目通过 MCP Client 调用。

## 架构定位

```
┌─────────────────────────────┐
│   spring-ai-agentic-demo    │  ← 主应用 (port 8081)
│                             │
│  ChatClient                 │
│       ↓                     │
│  ToolCallback[]             │
│   ├─ function-calling 模式  │  → AfterSalesToolConfig (本地 Bean)
│   └─ mcp 模式               │  → MCP Client ──stdio──→ 本 MCP Server
└─────────────────────────────┘
```

主应用通过 `app.tool-mode` 配置项在两种模式间切换，Service 层代码无需任何修改。

## 暴露的工具

| 工具名 | 描述 | 参数 |
|--------|------|------|
| `queryOrder` | 查询订单信息（订单号、商品、状态、金额） | `orderId` — 订单ID，如 `ORD-001` |
| `queryProduct` | 查询商品信息（名称、分类、价格、库存） | `productId` — 商品ID，如 `PRD-001` |
| `queryUser` | 查询用户信息（VIP等级、历史订单数、退款次数） | `userId` — 用户ID，如 `USR-001` |
| `queryUserByOrder` | 根据订单ID查询下单用户信息 | `orderId` — 订单ID |

### 模拟数据

MCP Server 是独立进程，不依赖主应用的 Bean 或数据库。所有数据内嵌在 `AfterSalesMcpTools` 中：

- **订单**: ORD-001, ORD-002, ORD-003
- **商品**: PRD-001, PRD-002, PRD-003
- **用户**: USR-001, USR-002, USR-003

如需接入真实数据源，替换 `AfterSalesMcpTools` 中的 Map 为 Service/Repository 调用即可。

## 技术栈

| 组件 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.5.3 |
| Spring AI | 1.0.0 |
| Spring AI MCP Server Starter | 1.0.0 (via BOM) |
| Lombok | 1.18.36 |

## 构建

```bash
cd mcp-server
mvn clean package
```

产物位于 `target/agentic-mcp-server-0.0.1-SNAPSHOT.jar`。

> **注意**: 必须先完成 MCP Server 的构建，主应用才能在 MCP 模式下通过 stdio 拉起该 jar。

## 运行方式

### 由主应用自动拉起（推荐）

在主项目 `application.yml` 中设置：

```yaml
app:
  tool-mode: mcp
```

启动主应用时，Spring AI MCP Client 会自动以子进程方式启动本 MCP Server，无需手动操作。

### 独立运行调试

```bash
java \
  -Dspring.ai.mcp.server.stdio=true \
  -Dspring.main.web-application-type=none \
  -Dspring.main.banner-mode=off \
  -jar target/agentic-mcp-server-0.0.1-SNAPSHOT.jar
```

启动后进入 stdio 等待状态，可通过标准输入发送 JSON-RPC 消息进行手动测试：

```json
{"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"test","version":"0.0.1"}},"jsonrpc":"2.0","id":0}
{"method":"notifications/initialized","jsonrpc":"2.0"}
{"method":"tools/list","jsonrpc":"2.0","id":1}
{"method":"tools/call","params":{"name":"queryOrder","arguments":{"orderId":"ORD-001"}},"jsonrpc":"2.0","id":2}
```

### 配合外部 MCP Host 使用

本 MCP Server 也可被 Claude Desktop、Cherry Studio、Cursor 等任意 MCP Host 直接使用。

**Claude Desktop / Cherry Studio 配置示例** (`mcp.json`):

```json
{
  "mcpServers": {
    "aftersales": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dspring.main.banner-mode=off",
        "-jar",
        "/absolute/path/to/agentic-mcp-server-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Windows 注意事项**: 如果通过 `.cmd` 包装器调用 java，需改为：

```json
{
  "command": "cmd.exe",
  "args": ["/c", "java", "-jar", "C:\\path\\to\\agentic-mcp-server-0.0.1-SNAPSHOT.jar"]
}
```

## 关键配置说明

### application.yml

```yaml
spring:
  main:
    banner-mode: off          # 必须关闭，否则 banner 输出会干扰 stdio 协议
    web-application-type: none # 不启动 Web 容器，纯 stdio 进程
  ai:
    mcp:
      server:
        name: agentic-aftersales-mcp-server
        version: 1.0.0
        type: SYNC            # 同步模式
        stdio: true           # 启用 stdio 传输
```

### 为什么必须关闭 banner 和日志？

stdio 模式下，MCP Client 与 Server 通过标准输入/输出交换 JSON-RPC 消息。任何非协议内容（Spring Banner、INFO 日志、异常堆栈）都会导致 JSON 解析失败。因此：

- `banner-mode: off` — 禁止启动横幅
- `web-application-type: none` — 不启动 Tomcat/Jetty
- `logging.pattern.console:` — 清空控制台日志格式
- `logging.level.root: WARN` — 仅保留警告以上级别

## 与 Function Calling 模式的对比

| 维度 | Function Calling | MCP |
|------|-----------------|-----|
| 工具来源 | 主应用内 `FunctionToolCallback` Bean | 独立进程的 MCP Server |
| 部署方式 | 同进程 | 子进程（stdio）/ 独立服务（SSE） |
| 数据共享 | 可直接注入主应用 Service | 需自带数据或通过环境变量/配置传入 |
| 适用场景 | 简单集成、快速开发 | 工具解耦、跨语言复用、多客户端共享 |
| 切换成本 | 改一个配置项 | 改一个配置项 |

## 项目结构

```
mcp-server/
├── pom.xml                          # Maven 构建配置
├── README.md                        # 本文档
└── src/main/
    ├── java/com/example/agentic/mcp/
    │   ├── McpServerApplication.java    # Spring Boot 启动类
    │   └── AfterSalesMcpTools.java      # @Tool 注解暴露的售后工具
    └── resources/
        └── application.yml              # stdio 模式配置
```
