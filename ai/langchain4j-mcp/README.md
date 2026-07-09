# LangChain4j MCP Tutorial Demo

> 基于 LangChain4j 1.16.0-beta26 + Spring Boot 3.5.3 的 Model Context Protocol (MCP) 教程项目

## 简介

本项目演示了如何使用 LangChain4j 的 MCP 模块通过 Model Context Protocol 让 AI Service 调用外部工具。MCP 是 Anthropic 提出的开放协议，允许 LLM 客户端与外部工具服务器标准化通信。

## 技术栈

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.5.3 |
| LangChain4j | 1.16.0-beta26 |
| JDK | 17 |
| Lombok | 1.18.36 |
| LLM | Agnes AI (agnes-2.0-flash) |

## 核心概念

### MCP 传输方式

本项目演示了三种 MCP 传输方式：

| 传输方式 | 类名 | 说明 |
|----------|------|------|
| stdio | `StdioMcpTransport` | 通过标准输入/输出与本地子进程通信 |
| HTTP/SSE | `HttpMcpTransport` | 通过 HTTP 请求和 SSE 通道通信 (legacy) |
| Streamable HTTP | `StreamableHttpMcpTransport` | MCP 2025-06-18 规范标准 (推荐) |

### MCP 核心组件

| 组件 | 说明 |
|------|------|
| `McpTransport` | 传输层，负责与 MCP Server 的底层通信 |
| `McpClient` | 客户端，管理与服务器的连接和工具发现 |
| `McpToolProvider` | 工具提供者，将 MCP 工具暴露给 AI Service |
| `AiServices` | LangChain4j 的 AI Service 组装器 |

## 项目结构

```
langchain4j-mcp/
├── src/main/java/com/example/mcp/
│   ├── stdio/          # stdio 传输演示
│   ├── http/           # HTTP/SSE 传输演示
│   ├── streamable/     # Streamable HTTP 传输演示
│   ├── multiprovider/  # 多 MCP Provider 演示
│   └── aitools/        # AI Service + MCP 标准模式
├── src/main/resources/
│   ├── mcp-server/     # 内置 Node.js MCP Server
│   └── static/         # Web Dashboard
```

## 快速开始

### 1. 前置准备

- JDK 17+
- Maven 3.8+
- Node.js 18+ (用于运行 MCP Server)

### 2. 安装 MCP Server 依赖

```bash
cd src/main/resources/mcp-server
npm install
```

### 3. 启动应用

```bash
# 在项目根目录
mvn spring-boot:run
```

### 4. 访问 Web Dashboard

打开浏览器访问: http://localhost:9060

### 5. API 测试

```bash
# 健康检查
curl http://localhost:9060/api/health

# stdio 传输 - 查询天气
curl -X POST "http://localhost:9060/api/stdio/ask?question=北京天气怎么样"

# stdio 传输 - 数学计算
curl -X POST "http://localhost:9060/api/stdio/ask?question=计算 2+3*4"

# AI Service 模式
curl -X POST "http://localhost:9060/api/weather/ask?question=成都天气，需要穿外套吗？"
```

## API 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/health | GET | 健康检查 |
| /api/stdio/ask?question= | POST | stdio 传输 MCP 演示 |
| /api/http/ask?question= | POST | HTTP/SSE 传输 MCP 演示 |
| /api/streamable/ask?question= | POST | Streamable HTTP 传输 MCP 演示 |
| /api/multiprovider/ask?question= | POST | 多 MCP Provider 演示 |
| /api/weather/ask?question= | POST | AI Service + MCP 标准模式 |

## MCP Server

项目内置了一个 Node.js MCP Server (`src/main/resources/mcp-server/weather-mcp-server.js`)，提供两个工具：

- **get_weather**: 查询城市天气 (北京、上海、广州、深圳、杭州、成都)
- **calculate**: 执行数学计算

## 教程文档

详细的 MCP 教程请参阅 [TUTORIAL.md](TUTORIAL.md)。

## 参考资源

- [LangChain4j MCP 文档](https://docs.langchain4j.dev/tutorials/mcp)
- [MCP 协议官网](https://modelcontextprotocol.io/)
- [MCP 规范](https://modelcontextprotocol.io/specification)
