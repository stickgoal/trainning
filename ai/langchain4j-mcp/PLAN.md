# LangChain4j MCP Tutorial - 开发计划

## 项目概述
本项目是一个 LangChain4j MCP (Model Context Protocol) 教程 Demo，演示如何通过 MCP 协议让 AI Service 使用外部工具。

## 技术栈
- Spring Boot 3.5.3
- LangChain4j 1.16.0-beta26 (含 langchain4j-mcp 模块)
- JDK 17
- Lombok 1.18.36
- Node.js (运行 MCP Server)
- LLM: Agnes AI (agnes-2.0-flash)

## 项目结构

```
langchain4j-mcp/
├── pom.xml                          # Maven 配置
├── PLAN.md                          # 本文件
├── README.md                        # 项目说明
├── TUTORIAL.md                      # 详细教程
├── src/main/java/com/example/mcp/
│   ├── McpDemoApplication.java      # Spring Boot 入口
│   ├── controller/HealthController.java
│   ├── common/
│   │   ├── model/                   # 领域模型 (WeatherInfo, NewsArticle, FileInfo, MathResult)
│   │   └── service/MockDataService.java
│   ├── stdio/                       # stdio 传输演示
│   │   ├── StdioMcpConfig.java      # Transport + Client + ToolProvider Bean
│   │   ├── StdioAgent.java          # AI Service 接口
│   │   ├── StdioService.java        # AiServices 组装
│   │   └── controller/StdioController.java
│   ├── http/                        # HTTP/SSE 传输演示 (legacy)
│   │   ├── HttpMcpConfig.java
│   │   ├── HttpAgent.java
│   │   ├── HttpService.java
│   │   └── controller/HttpController.java
│   ├── streamable/                  # Streamable HTTP 传输演示
│   │   ├── StreamableHttpMcpConfig.java
│   │   ├── StreamableAgent.java
│   │   ├── StreamableService.java
│   │   └── controller/StreamableController.java
│   ├── multiprovider/               # 多 MCP Provider 演示
│   │   ├── MultiProviderConfig.java
│   │   ├── MultiProviderAgent.java
│   │   ├── MultiProviderService.java
│   │   └── controller/MultiProviderController.java
│   └── aitools/                     # AI Service + MCP 标准模式
│       ├── WeatherAssistant.java
│       ├── WeatherService.java
│       └── controller/WeatherController.java
├── src/main/resources/
│   ├── application.yml
│   ├── static/index.html            # Web Dashboard
│   └── mcp-server/                  # 内置 MCP Server
│       ├── package.json
│       └── weather-mcp-server.js
└── src/test/java/com/example/mcp/
    └── McpDemoApplicationTest.java
```

## 开发阶段

### 阶段 1: 项目骨架 ✅
- [x] 创建 pom.xml (依赖: spring-boot-starter-web, langchain4j-mcp, langchain4j-open-ai-spring-boot-starter, lombok)
- [x] 创建 application.yml (端口 9060, Agnes AI 配置)
- [x] 创建 McpDemoApplication.java 入口类
- [x] 创建 HealthController 健康检查

### 阶段 2: 基础设施 ✅
- [x] 创建领域模型 (WeatherInfo, NewsArticle, FileInfo, MathResult)
- [x] 创建 MockDataService 模拟数据服务

### 阶段 3: MCP Server ✅
- [x] 创建 Node.js MCP Server (weather-mcp-server.js)
  - 提供 get_weather 工具 (查询城市天气)
  - 提供 calculate 工具 (数学计算)
- [x] 创建 package.json

### 阶段 4: stdio 传输演示 ✅
- [x] StdioMcpConfig - Transport + Client + ToolProvider
- [x] StdioAgent - AI Service 接口
- [x] StdioService - AiServices 组装
- [x] StdioController - REST 入口

### 阶段 5: HTTP/SSE 传输演示 ✅
- [x] HttpMcpConfig - HttpMcpTransport 配置
- [x] HttpAgent + HttpService + HttpController

### 阶段 6: Streamable HTTP 传输演示 ✅
- [x] StreamableHttpMcpConfig - StreamableHttpMcpTransport 配置
- [x] StreamableAgent + StreamableService + StreamableController

### 阶段 7: 多 MCP Provider 演示 ✅
- [x] MultiProviderConfig - 组合多个 MCP Client
- [x] MultiProviderAgent + MultiProviderService + MultiProviderController

### 阶段 8: AI Service + MCP 标准模式 ✅
- [x] WeatherAssistant - AI Service 接口 (带 @SystemMessage)
- [x] WeatherService - AiServices 组装
- [x] WeatherController - REST 入口

### 阶段 9: Web Dashboard ✅
- [x] index.html - 展示 5 个 MCP 演示模块

### 阶段 10: 文档 ✅
- [x] PLAN.md
- [x] README.md
- [x] TUTORIAL.md

## 后续扩展方向
1. 添加 Docker stdio 传输演示 (DockerMcpTransport)
2. 添加 WebSocket 传输演示
3. 添加 MCP 工具过滤演示 (filterToolNames)
4. 添加工具名称映射演示 (toolNameMapper)
5. 添加动态添加/移除 MCP Client 演示
