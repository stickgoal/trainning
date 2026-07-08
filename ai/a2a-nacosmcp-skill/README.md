# Spring AI Alibaba 综合教程：A2A + Nacos MCP + Skill

基于 **Spring AI Alibaba 1.1.2.0** 版本，在一个代码库中完整演示三大核心功能：

- **A2A（Agent-to-Agent）**：基于 Nacos 的分布式 Agent 远程调用
- **Nacos MCP（Model Context Protocol）**：基于 Nacos 的 MCP 工具注册与发现
- **Skill（技能系统）**：渐进式披露的领域知识封装

## 项目结构

```
a2a-nacosmcp-skill/
├── pom.xml                          # 父 POM（Spring Boot 3.4.8 + Spring AI Alibaba 1.1.2.0）
│
├── a2a-math-agent/                  # A2A 服务提供者：数学计算 Agent（端口 8081）
├── a2a-weather-agent/               # A2A 服务提供者：天气查询 Agent（端口 8082）
├── a2a-client/                      # A2A 服务消费者：远程调用演示（端口 8083）
│
├── mcp-weather-server/              # MCP 服务端：天气工具（端口 8091）
├── mcp-calc-server/                 # MCP 服务端：计算器工具（端口 8092）
├── mcp-client/                      # MCP 客户端：工具调用演示（端口 8093）
│
├── skill-demo/                      # Skill 演示：技能系统 + 渐进式披露（端口 8084）
│   └── src/main/resources/skills/
│       ├── copywriting/SKILL.md     # 商品文案写作技能
│       ├── product-selection/SKILL.md # 选品分析技能
│       └── code-reviewer/           # 代码审查技能（含参考文档和脚本）
│
└── integrated-demo/                 # 综合演示：A2A + MCP + Skill 联合使用（端口 8080）
```

## 环境准备

### 1. 基础要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | Spring Boot 3.x 要求 |
| Maven | 3.6+ | 构建工具 |
| Nacos | 2.0+ | 服务注册中心（推荐 2.4.3+） |
| DashScope API Key | - | 阿里云灵积模型服务 |

### 2. 启动 Nacos

```bash
# Docker 方式（推荐）
docker run -d --name nacos \
  -e MODE=standalone \
  -p 8848:8848 \
  -p 9848:9848 \
  nacos/nacos-server:v2.4.3

# 验证：访问 http://localhost:8848/nacos
# 默认用户名/密码：nacos/nacos
```

### 3. 配置 API Key

```bash
# Windows PowerShell
$env:AI_DASHSCOPE_API_KEY="your-api-key"

# Linux/Mac
export AI_DASHSCOPE_API_KEY="your-api-key"
```

## 快速开始

### 编译项目

```bash
cd D:\workspace\处理\trainning\ai\a2a-nacosmcp-skill
mvn clean compile -DskipTests
```

### 启动顺序

```bash
# 1. 先启动 A2A 服务提供者（Agent 注册到 Nacos）
mvn -pl a2a-math-agent spring-boot:run
mvn -pl a2a-weather-agent spring-boot:run

# 2. 启动 MCP 服务端（工具注册到 Nacos MCP Registry）
mvn -pl mcp-weather-server spring-boot:run
mvn -pl mcp-calc-server spring-boot:run

# 3. 启动消费者/演示模块
mvn -pl a2a-client spring-boot:run
mvn -pl mcp-client spring-boot:run
mvn -pl skill-demo spring-boot:run
mvn -pl integrated-demo spring-boot:run
```

---

## 一、A2A（Agent-to-Agent）教程

### 核心概念

A2A 是 Spring AI Alibaba 提供的 Agent 间通信协议，通过 Nacos 实现 Agent 的自动注册与发现，让远程 Agent 调用像本地调用一样简单。

```
┌──────────────┐     Nacos      ┌──────────────┐
│ A2A Client   │ ── 发现 ──→   │ A2A Server   │
│ (消费者)     │ ←── 调用 ──   │ (提供者)     │
│ 端口 8083    │                │ 端口 8081/82  │
└──────────────┘                └──────────────┘
```

### 功能演示

#### 1. 数学 Agent（提供者 — `a2a-math-agent`）

**核心代码**：`MathAgentConfig.java`

```java
@Bean
public ReactAgent mathAgent(ChatModel chatModel, MathTools mathTools) {
    return ReactAgent.builder()
            .name("math_agent")
            .description("专业数学计算智能体")
            .model(chatModel)
            .tools(mathTools)  // 加减乘除、幂运算、阶乘
            .build();
}
```

**功能点**：
- 通过 `@Tool` 注解定义 6 个数学工具函数
- `ReactAgent` 注册为 Bean 后，A2A 框架自动注册到 Nacos
- 无需手动编写 REST 端点

#### 2. 天气 Agent（提供者 — `a2a-weather-agent`）

**核心代码**：`WeatherAgentConfig.java`

```java
@Bean
public ReactAgent weatherAgent(ChatModel chatModel, WeatherTools weatherTools) {
    return ReactAgent.builder()
            .name("weather_agent")
            .description("天气查询智能体")
            .model(chatModel)
            .tools(weatherTools)  // 天气查询、空气质量、出行建议
            .build();
}
```

**功能点**：
- 模拟 10 个城市的天气数据
- 提供天气查询、空气质量、出行建议三个工具
- 与 math_agent 并行注册到 Nacos，互不干扰

#### 3. A2A 客户端（消费者 — `a2a-client`）

**核心代码**：`A2aClientConfig.java` + `A2aController.java`

```java
// 通过名称从 Nacos 自动发现远程 Agent
@Bean
public A2aRemoteAgent remoteMathAgent(AgentCardProvider agentCardProvider) {
    return A2aRemoteAgent.builder()
            .name("math_agent")           // 与提供者的 Agent name 一致
            .agentCardProvider(agentCardProvider)
            .build();
}

// 调用方式与本地 Agent 完全一致
remoteMathAgent.invoke("计算 15 乘以 32 再加上 100");
```

**API 测试**：

```bash
# 调用远程数学 Agent
curl "http://localhost:8083/api/a2a/math?question=计算15的阶乘"

# 调用远程天气 Agent
curl "http://localhost:8083/api/a2a/weather?question=查询北京和上海的天气"

# 综合调用：先查天气，再做温度转换
curl "http://localhost:8083/api/a2a/combined?question=北京今天天气怎么样"
```

**功能点**：
- 透明的远程调用：`A2aRemoteAgent.invoke()` 与本地 `ReactAgent.invoke()` API 完全一致
- 自动服务发现：通过 `AgentCardProvider` 从 Nacos 获取服务地址
- 负载均衡：Nacos 自动在多个实例间分配请求

---

## 二、Nacos MCP 教程

### 核心概念

MCP（Model Context Protocol）是工具级远程调用协议。与 A2A 的 Agent 级调用不同，MCP 将单个工具函数注册为服务，客户端通过 ChatClient 的 tools 机制自动发现和调用。

```
┌──────────────┐   Nacos MCP     ┌──────────────────┐
│ MCP Client   │   Registry      │ MCP Server       │
│ (消费者)     │ ── 发现工具 ──→ │ (提供者)         │
│ 端口 8093    │ ←── 调用工具 ── │ 端口 8091/8092    │
└──────────────┘                 └──────────────────┘
```

### 功能演示

#### 1. MCP 天气服务（`mcp-weather-server`）

**核心代码**：`WeatherMcpService.java`

```java
@Service
public class WeatherMcpService {
    @Tool(description = "查询指定城市的实时天气信息")
    public String queryWeather(@ToolParam(description = "城市名称") String city) { ... }

    @Tool(description = "查询指定城市的空气质量指数(AQI)")
    public String queryAirQuality(@ToolParam(description = "城市名称") String city) { ... }

    @Tool(description = "根据天气情况给出出行建议")
    public String getTravelAdvice(@ToolParam(description = "城市名称") String city) { ... }
}
```

**功能点**：
- 使用 `@Tool` 注解定义 MCP 工具，自动注册到 Nacos MCP Registry
- 支持运行时动态开关工具（通过 Nacos 控制台）
- 工具描述支持热更新，无需重启服务

#### 2. MCP 计算器服务（`mcp-calc-server`）

**核心代码**：`CalculatorMcpService.java`

```java
@Service
public class CalculatorMcpService {
    @Tool(description = "计算两个数的和")  public double add(...) { ... }
    @Tool(description = "计算两个数的差")  public double subtract(...) { ... }
    @Tool(description = "计算两个数的积")  public double multiply(...) { ... }
    @Tool(description = "计算两个数的商")  public double divide(...) { ... }
    @Tool(description = "计算 a 的 b 次幂") public double power(...) { ... }
    @Tool(description = "计算 n 的阶乘")   public long factorial(...) { ... }
    @Tool(description = "计算平方根")      public double sqrt(...) { ... }
}
```

**功能点**：
- 7 个数学工具函数，与 A2A MathAgent 形成对比
- MCP 是工具级注册（单个函数），A2A 是 Agent 级注册（完整 Agent）

#### 3. MCP 客户端（`mcp-client`）

**核心代码**：`McpController.java`

```java
// 大模型自动发现并调用 MCP 工具
@GetMapping("/chat")
public String chat(@RequestParam String question) {
    return chatClient.prompt()
            .user(question)
            .tools(toolCallbackProvider.getToolCallbacks())  // 注入所有 MCP 工具
            .call()
            .content();
}
```

**API 测试**：

```bash
# 天气查询（大模型自动调用 weather-mcp-service）
curl "http://localhost:8093/api/mcp/chat?question=查询北京的天气和空气质量"

# 数学计算（大模型自动调用 calculator-mcp-service）
curl "http://localhost:8093/api/mcp/calculate?question=计算15的阶乘和256的平方根"

# 跨服务调用（大模型依次调用两个 MCP 服务）
curl "http://localhost:8093/api/mcp/cross?question=北京今天天气怎么样？如果温度超过25度，帮我算一下华氏温度"
```

**功能点**：
- 大模型自动决策调用哪个 MCP 工具
- 支持跨服务组合调用（先查天气，再算温度）
- 无需手动编写服务发现和调用代码

---

## 三、Skill（技能系统）教程

### 核心概念

Skill 采用**渐进式披露**设计：
1. 系统提示中仅注入技能摘要（名称 + 描述）
2. 模型判断需要某技能时，调用 `read_skill(skill_name)` 加载完整 SKILL.md
3. 按技能指导完成用户任务

```
用户请求 → Agent 查看技能列表 → 判断匹配 → read_skill → 加载完整指令 → 执行任务
```

### 技能目录结构

```
src/main/resources/skills/
├── copywriting/
│   └── SKILL.md              # 必需：技能定义（YAML 头 + Markdown 正文）
├── product-selection/
│   └── SKILL.md
└── code-reviewer/
    ├── SKILL.md
    ├── references/
    │   └── java-code-standards.md   # 可选：参考文档
    └── scripts/
        └── check_style.sh           # 可选：执行脚本
```

### 功能演示

#### 1. 技能配置（`SkillConfig.java`）

```java
// 从 classpath 加载技能
@Bean
public SkillRegistry skillRegistry() {
    return ClasspathSkillRegistry.builder()
            .classpathPath("skills")
            .autoLoad(true)
            .build();
}

// 将技能注入 Agent
@Bean
public SkillsAgentHook skillsAgentHook(SkillRegistry skillRegistry) {
    return SkillsAgentHook.builder()
            .skillRegistry(skillRegistry)
            .autoReload(true)
            .build();
}
```

**功能点**：
- `ClasspathSkillRegistry`：从 classpath 自动扫描加载技能
- `SkillsAgentHook`：自动注册 `read_skill` / `search_skills` / `disable_skill` 工具
- 支持 `autoReload`：每次调用前自动重新加载技能

#### 2. 三个演示技能

| 技能 | 名称 | 功能 |
|------|------|------|
| 商品文案写作 | `copywriting` | 根据商品信息生成 3 种风格营销文案 |
| 选品分析 | `product-selection` | 根据目标人群分析推荐商品品类 |
| 代码审查 | `code-reviewer` | 对 Java 代码进行质量审查（含参考文档和脚本） |

**API 测试**：

```bash
# 查看可用技能
curl "http://localhost:8084/api/skill/list"

# 让 Agent 自我介绍技能
curl "http://localhost:8084/api/skill/discover"

# 文案写作
curl "http://localhost:8084/api/skill/copywriting?product=智能保温杯，350ml，不锈钢材质，12小时保温"

# 选品分析
curl "http://localhost:8084/api/skill/selection?target=25-35岁女性，月消费3000-5000元，关注时尚和健康"

# 代码审查
curl "http://localhost:8084/api/skill/review?code=public class User { private String name; public String getName() { return name; } }"
```

**功能点**：
- 渐进式披露：初始只注入技能摘要，节省 Token
- 按需加载：模型决定需要时才调用 `read_skill`
- 技能自包含：每个技能可包含参考文档和可执行脚本

---

## 四、综合演示（`integrated-demo`）

### 功能对比

| 维度 | A2A | Nacos MCP | Skill |
|------|-----|-----------|-------|
| 定位 | Agent 级远程调用 | 工具级远程调用 | 本地指令+工具封装 |
| 注册方式 | Nacos 服务注册 | Nacos MCP Registry | Classpath/文件系统 |
| 调用方式 | A2aRemoteAgent | ChatClient + tools | read_skill 工具 |
| 适用场景 | 多 Agent 协作 | 工具服务化 | 领域知识封装 |
| 协议 | A2A (HTTP/REST) | MCP (SSE/STDIO) | 渐进式披露 |
| 粒度 | 完整 Agent | 单个工具函数 | 技能包(文档+脚本) |

### API 测试

```bash
# 能力对比
curl "http://localhost:8080/api/integrated/compare"

# A2A + MCP 联合演示
curl "http://localhost:8080/api/integrated/a2a-mcp?question=查询北京天气并计算温度转换"

# 场景化综合演示
curl "http://localhost:8080/api/integrated/scenario?task=北京今天天气适合户外活动吗？如果适合，帮我算一下跑步10公里消耗的卡路里"
```

---

## 选择指南

```
需要完整 Agent 能力（推理+工具+记忆）？
  └─ 是 → 使用 A2A（Agent 级远程调用）
  └─ 否 → 继续

需要将工具服务化，跨语言/跨服务调用？
  └─ 是 → 使用 Nacos MCP（工具级远程调用）
  └─ 否 → 继续

需要封装领域知识和最佳实践？
  └─ 是 → 使用 Skill（渐进式披露）
  └─ 否 → 直接使用 ChatClient + 本地工具
```

**三者可以组合使用，互不冲突。** 例如：
- 用 A2A 调用远程专家 Agent
- 用 MCP 调用通用工具服务（天气、计算、翻译等）
- 用 Skill 封装本地领域知识（文案模板、审查规范等）

## 版本信息

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.4.8 |
| Spring AI Alibaba | 1.1.2.0 |
| Spring AI | 1.1.2 |
| JDK | 17+ |
| Nacos | 2.0+ |

## 参考资料

- [Spring AI Alibaba 官方文档](https://java2ai.com/)
- [Nacos MCP 自动注册手册](https://nacos.io/docs/v3.1/manual/user/ai/mcp-auto-register/)
- [Spring AI Alibaba A2A 使用指南](https://blog.csdn.net/xiaoxualg/article/details/156064085)
- [Spring AI Alibaba Skills 技能体系](https://blog.csdn.net/qq_43437874/article/details/160256173)