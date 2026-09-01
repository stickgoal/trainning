# Code Reviewer 技能（Spring Boot 3.5）

## When To Use

对 Java 17 + Spring Boot 3.5 代码库、PR、文件、模块做结构化代码审查时。关注：迁移风险、架构边界违规、空安全、安全缺陷、性能回退、数据访问陷阱。

> 与 `code-review` 技能分工：`code-review` 是项目专属快速检查清单（分层/Result/事务/中间件边界）；本技能是**通用深度审查**，覆盖 API 正确性、数据访问、安全、性能、空安全、架构。

## Critical Rules

- 没有代码上下文不评审：先要文件、diff 或模块。
- 发现项必须带文件路径与行号。
- 目标基线：**Java 17 + Spring Boot 3.5.x + Spring Framework 6.x**（不是 Boot 4 / Java 25）。
- 建议虚拟线程/响应式重写前先分析实际负载，不拍脑袋。

## Workflow

### Step 1: 确认范围
收集：范围（单文件/模块/PR/全库）、目标版本（从 pom 确认）、关注点（迁移/架构/数据访问/安全/性能/空安全）、是否要修复建议。

### Step 2: 按需加载参考
本项目技能库中相关规范：`alibaba-java-guidelines`、`effective-java-core`、`effective-java-concurrency`、`database`、`api-design`、`springboot`。按评审焦点加载，不全量读。

### Step 3: 跑评审 Pass（按需，完整评审按下序）

#### Pass A: 构建与配置
- 确认 `pom.xml` 的 Java 17 与 Spring Boot 3.5.x。
- starter 用 Spring Boot 3 名：`spring-boot-starter-web` / `-aop` / `-test`（不是 Boot 4 的 `webmvc`/`aspectj`）。
- Jackson 2：`com.fasterxml.jackson.*`（不是 `tools.jackson.*`）。
- 依赖版本统一 `dependencyManagement`，无版本漂移。

#### Pass B: API 正确性
- Controller/Service 边界、参数校验、错误处理、统一 `Result<T>`。
- 公开 API 与重写方法的空安全标注。

#### Pass C: 架构与分包
- 识别架构风格，检查包结构一致。
- 标记边界泄漏：Controller 直调 Mapper、Entity 暴露到 API、业务逻辑堆在 Controller。

#### Pass D: 数据访问
- Repository 位置与聚合边界。
- N+1 查询、缺分页、投影错配、事务误用（`@Transactional` 失效场景）。

#### Pass E: 安全
- 鉴权授权、输入校验、不安全查询、密钥处理、敏感日志。

#### Pass F: 性能与韧性
- 缓存策略、无界读、异步使用、远程调用行为。
- 超时、重试、连接池假设。

## Quick Triggers（Spring Boot 3.5 专属）

- **误用 Boot 4 语法**：`spring-boot-starter-webmvc`、`@MockitoBean`、`tools.jackson.*`、`@ImportHttpServices`、原生 `org.springframework.resilience.annotation.*` —— 这些都是 Boot 4/Framework 7 才有的，Boot 3.5 项目里不该出现。
- **测试注解**：Boot 3.5 用 `@MockBean` / `@SpyBean`（不是 `@MockitoBean`）；`@WebMvcTest` 在 `org.springframework.boot.test.autoconfigure.web.servlet` 包。
- **重试**：Boot 3.5 用 spring-retry 的 `@Retryable` + `@EnableRetry`（`org.springframework.retry.annotation.*`），并需 `spring-boot-starter-aop`。
- **虚拟线程**：Java 17 不支持。若见 `spring.threads.virtual.enabled` 或虚拟线程代码，提示需升级 Java 21+ 并评估负载。
- **Jackson**：`com.fasterxml.jackson.*` 正确；不应出现 `tools.jackson.*`。
- **Problem Details**：SB3.2+ 支持，`spring.mvc.problemdetails.enabled=true` 可用。

### 空安全
- 缺 `@Nullable`/`@NonNull` 标注、重写方法缺空安全标注继承。

### 架构
- Controller 直调 Repository、Entity 暴露 API、业务逻辑堆在 Controller。

### 性能
- 循环内实体遍历、缺分页、读多路径忽略投影、无负载证据就建议虚拟线程。

### 安全
- 特权操作缺鉴权、SQL/NoSQL 注入、源码或日志泄露密钥、错误信息过度暴露。

## Report Format

按严重度排序：

```markdown
## Critical
- **[分类]**：问题摘要
  - **文件**：`path/File.java:123`
  - **影响**：可能失败/泄露/回退什么
  - **修复**：具体改法

## High / Medium / Low
...
```

无发现则明说，并指出盲区（未评审的模块、缺测试、无运行上下文）。

## Common Review Modes

### 快速 PR 评审
1. 读改动文件。
2. 对照 `springboot`、`database`、`alibaba-java-guidelines` 的 Rules。
3. diff 触及安全/性能时加载对应技能。
4. 只报带文件与行号的发现。

### 安全评审
读 `alibaba-java-guidelines` 的安全节，聚焦 Controller、Service 入口、安全配置、持久层边界，报可利用性与受影响入口。

### 架构评审
对照 `AGENTS.md` 的分层约定，报边界错配与耦合问题。

### 迁移评审
聚焦遗留 API、过时注解、部分迁移问题；如需分阶段升级计划而非评审，用 `springboot-migration`。

## When Not to Use

- 纯 Kotlin 代码库、非 Spring 框架（Micronaut/Quarkus）、无代码上下文的泛泛评审、纯前端改动（用 `web-design-guidelines`）。
