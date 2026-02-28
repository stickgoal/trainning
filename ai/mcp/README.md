## Spring AI Alibaba MCP 演示：产品搜索与下单

本项目用于演示如何使用 **spring-ai-alibaba** 的 **MCP（Model Context Protocol）** 能力，把“商品搜索 / 下单”这类业务能力以 **Tool** 的形式暴露给大模型，并由 **MCP Client** 在对话过程中自动调用。

---

## 目录结构

项目代码位于 `mcp/` 目录下：

- `mcp/demo-ai/`：**MCP Client** + DashScope 大模型对话服务（对外提供 HTTP 接口，端口 **8080**）
- `mcp/demo-mcp-server/`：**MCP Server（WebMVC + SSE）**，对外暴露商品工具（端口 **8001**，SSE 端点 **`/sse`**）
- `mcp/product/`：商品微服务（提供 `/sku/search` 等接口，供 MCP Server 转调）

---

## 整体架构与调用链

- **用户** 调用 `demo-ai` 的 `/ai/chat` 或 `/ai/chat/sse` 发起对话
- `demo-ai`（MCP Client）在模型推理过程中，根据需要调用 MCP 工具
- MCP 工具来自 `demo-mcp-server`（MCP Server）通过 SSE 暴露的方法：
  - `search(keyword)`：根据关键字搜索商品
  - `createOrder(skuId, quantity)`：下单（当前示例里返回 `Result.success()`，作为下单流程的演示入口）
- `demo-mcp-server` 内部的 `search` 会通过 Feign（服务名 `product`）转调商品微服务：
  - `GET /sku/search?keyword=xxx`

---

## 环境要求

- **JDK 17**
- **Maven 3.8+**
- **DashScope API Key**（用于 `demo-ai` 调用大模型）
  - 环境变量名：`AI_DASHSCOPE_API_KEY`
- **Nacos**（`product` 与 `demo-mcp-server` 使用服务发现/配置，默认地址 `localhost:8848`）
- **MySQL**（商品数据示例库，默认账号 `root/root123`）

> 注意：`product-app` 未在配置文件中固定 `server.port`，默认会使用 **8080**，这会与 `demo-ai(8080)` 冲突。请按下文启动命令通过参数覆盖端口。

---

## 快速开始（推荐顺序）

### 1) 启动 Nacos（本地 8848）

确保 Nacos 运行在：

- `http://localhost:8848`

### 2) 初始化 MySQL 数据

执行建库建表与示例数据（包含 `prd_sku` 与多条 SKU）：

- SQL 文件：`mcp/product/docs/product124_prd_sku.sql`

默认连接信息参考：`mcp/product/product-app/src/main/resources/application.yml` 与 `bootstrap.yml`。

### 3) 启动商品服务 `product-app`

为避免端口冲突，建议把商品服务端口改成 **9001**（或任意未占用端口）：

```bash
mvn -f "mcp/product/product-app/pom.xml" -DskipTests spring-boot:run ^
  -Dspring-boot.run.arguments="--server.port=9001"
```

商品搜索接口（用于验证服务可用）：

- `GET http://localhost:9001/sku/search?keyword=iphone`

### 4) 启动 MCP Server：`demo-mcp-server`（8001）

```bash
mvn -f "mcp/demo-mcp-server/pom.xml" -DskipTests spring-boot:run
```

关键配置（可在 `mcp/demo-mcp-server/src/main/resources/application.yml` 查看）：

- `server.port: 8001`
- `spring.ai.mcp.server.base-url: http://localhost:8001`

### 5) 启动 MCP Client：`demo-ai`（8080）

先设置 DashScope API Key（PowerShell 示例）：

```powershell
$env:AI_DASHSCOPE_API_KEY="你的DashScopeKey"
```

启动：

```bash
mvn -f "mcp/demo-ai/pom.xml" -DskipTests spring-boot:run
```

关键配置（可在 `mcp/demo-ai/src/main/resources/application.yml` 查看）：

- `server.port: 8080`
- MCP SSE 连接：
  - `spring.ai.mcp.client.sse.connections.server1.url: http://localhost:8001`
  - `spring.ai.mcp.client.sse.connections.server1.sseEndpoint: /sse`
- `spring.ai.mcp.client.toolcallback.enabled: true`

---

## 演示用接口（demo-ai）

### 获取会话 ID

- `GET /ai/getSessionId`

示例：

```bash
curl "http://localhost:8080/ai/getSessionId"
```

### 同步对话（服务端返回文本）

- `GET /ai/chat?question=...&conversationId=...`

示例：

```bash
curl "http://localhost:8080/ai/chat?conversationId=你的会话ID&question=帮我搜索一下iphone"
```

### SSE 流式对话

- `GET /ai/chat/sse?question=...&conversationId=...`

示例（保持连接）：

```bash
curl -N "http://localhost:8080/ai/chat/sse?conversationId=你的会话ID&question=我想买一台macbook，帮我搜下并推荐库存多的"
```

---

## 业务工具（MCP Server 暴露）

工具定义位置：`mcp/demo-mcp-server/src/main/java/com/wn/demomcpserver/tools/product/ProductTools.java`

- `search(keyword)`
  - 描述：根据产品名称关键字，返回产品信息
  - 实现：通过 `SkuQueryClient.search(keyword)` 转调 `product` 服务的 `/sku/search`
- `createOrder(skuId, quantity)`
  - 描述：根据产品 ID 与数量执行下单
  - 说明：当前示例实现为 `Result.success()`，用于演示“模型触发下单工具调用”的链路

---

## 常见问题

### 1) `product-app` 与 `demo-ai` 端口冲突

`demo-ai` 固定使用 `8080`，`product-app` 默认也会用 `8080`。

- 解决：启动 `product-app` 时增加 `--server.port=9001`（或修改 `application.yml` 固定端口）。

### 2) 搜索工具调用失败 / 404 / Feign 调用异常

`demo-mcp-server` 的 `search` 依赖 `product` 服务可用，并通过 Nacos 发现服务。

- 确认 Nacos 正常（`localhost:8848`）
- 确认 `product-app` 已启动并注册服务名为 `product`（见 `bootstrap.yml` 的 `spring.application.name: product`）
- 确认数据库已导入 `product124_prd_sku.sql`

### 3) 大模型不触发工具调用

请确认 `demo-ai` 已启用 tool callback：

- `spring.ai.mcp.client.toolcallback.enabled: true`

并尝试在问题中明确表达意图，例如：

- “帮我**搜索**一下关键字：macbook”
- “请对 skuId=1 **下单**，数量 2”

