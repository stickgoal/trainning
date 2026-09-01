# AGENTS.md

> 本文件是项目宪法。AI 编码工具每次对话首先读本文件，再动手。改动前先读本文，改动后保持本文与代码一致。
> 本文为**微服务架构**版本：Spring Boot 3.5 + Vue 3，基于 Spring Cloud Alibaba，采用「全局公共模块 + 单服务五模块」的组织形式。

## 0. 启动必问

**AI 在开始任何开发前，必须先向用户确认以下信息，未确认不得臆测：**

| 项 | 说明 | 示例 |
|----|------|------|
| 项目名 | 用于命名所有模块前缀、目录、groupId | `mall`（替换本文所有 `{{项目名}}` 占位符） |
| groupId | Maven 坐标，通常 `com.{{项目名}}` | `com.mall` |
| 业务服务清单 | 按实际业务拆分的服务及职责 | 如 `user`、`product`、`order` |

> 本文所有 `{{项目名}}`、`{{服务名}}` 均为占位符，须在开工前替换为真实值。

## 1. 项目介绍

<!-- 一句话说明项目是什么、解决什么问题、目标用户。由 AI 按实际项目填写，不预设业务。 -->

## 2. 技术栈

**后端**

- Java 17
- Spring Boot 3.5.x
- Spring Cloud 2025.0.x（OpenFeign / LoadBalancer / Gateway / 链路追踪）
- Spring Cloud Alibaba 2025.0.0.0（Nacos 注册配置 / Sentinel / Seata）
- MyBatis-Plus 3.5.x + Druid（数据访问）
- MySQL 8.0（主数据库）
- JUnit 5 + Spring Boot Test（测试）

**微服务治理组件**

| 能力 | 选型 |
|------|------|
| 注册中心 / 服务发现 | Nacos（nacos-discovery） |
| 配置中心 | Nacos（nacos-config） |
| 服务调用 | OpenFeign |
| 负载均衡 | Spring Cloud LoadBalancer |
| 服务网关 | Spring Cloud Gateway |
| 服务容错 | Sentinel |
| 分布式事务 | Seata（AT 模式） |
| 链路追踪 | Zipkin |

**可选中间件**（按需引入，引入前先更新本文件与 `docs/decisions/`）

| 中间件 | 用途 | 引入位置 |
|--------|------|----------|
| Redis / Redisson | 缓存、分布式锁、防重提交 | 公共 starter 或服务 infra |
| RocketMQ | 消息队列、延时任务、削峰 | 服务 infra |
| Elasticsearch | 搜索、日志分析 | 服务 infra |

**前端**

- Vue 3 + Element Plus + Vue Router + Vite

## 3. 目录结构

### 3.1 全局组织（多模块 Maven）

```
项目根/
├── README.md               项目介绍与文档使用方式
├── CHANGELOG.md            变更记录
├── AGENTS.md               本文件：项目宪法
├── docs/                   全部文档（与源码分离）
│   ├── product.md          产品说明
│   ├── architecture.md     架构说明
│   ├── development.md      开发指南
│   ├── verification.md     质量保障体系
│   ├── bugfix-log.md       bug 修复记录
│   ├── decisions/          架构决策记录（ADR）
│   ├── tasks/              任务卡库
│   ├── plans/              计划库
│   └── skills/             技能库
├── {{项目名}}-parent/              全局父 POM：统一管理所有依赖与版本
├── {{项目名}}-common/              全局公共模块：统一返回、异常、常量、公共 DTO
├── {{项目名}}-spring-boot-starter/ 公共 starter：自动装配（异常处理/上下文透传/防重/日志）
├── gateway/                        网关服务
├── {{服务名1}}/                     业务服务（如 user / product / order，五模块结构）
├── {{服务名2}}/                     业务服务
└── {{项目名}}-front/                前端（Vue 3）
```

### 3.2 单个服务内部（五模块，参考 archegen 架构生成器约定）

每个业务服务（含 gateway）统一为**五模块**结构：

```
{{服务名}}/
├── pom.xml                   外层 POM：packaging=pom，继承 {{项目名}}-parent，声明五个子模块
├── {{服务名}}-client/        对外接口定义：仅接口 + 入参出参 DTO，不含任何实现
├── {{服务名}}-app/           打包模块：spring-boot，mainClass 入口，依赖 service + client
├── {{服务名}}-service/       业务模块：JavaSE 项目，承载全部业务逻辑
├── {{服务名}}-infra/         基础设施：数据库访问（Mapper）+ 外部服务集成（Feign 调用）
└── {{服务名}}-common/        服务内通用工具：本服务私有的常量、工具类、枚举
```

**模块职责约束（硬性）**

| 模块 | 格式 | 职责 | 关键依赖 |
|------|------|------|----------|
| `-client` | jar | 对外 API 契约（Feign 接口 + DTO），**不包含任何实现** | openfeign（provided）、jakarta.validation |
| `-app` | spring-boot jar | 应用入口 `mainClass`，装配与启动，**不写业务逻辑** | service、client、{{项目名}}-spring-boot-starter、nacos-discovery/config、openfeign、loadbalancer |
| `-service` | jar | 业务逻辑实现，编排 infra 与 client | infra |
| `-infra` | jar | DB 访问（MyBatis-Plus Mapper）、调用外部服务的 Feign 客户端 | 外部 `xxx-client`、本服务 `-common`、mysql、druid、mybatis-plus |
| `-common` | jar | 服务内私有常量/工具/枚举 | 无内部依赖 |

**依赖方向（单向，禁止反向）**

```
app → service → infra → (外部 xxx-client + 本服务 -common)
  └→ client（对外接口实现，app 的 apiImpl 实现 client 定义的接口）
```

- 业务模块之间**禁止直连数据库**：服务 A 需要服务 B 的数据，只能通过 B 的 `client` 接口调用。
- 全局公共代码只放 `{{项目名}}-common` 与 `{{项目名}}-spring-boot-starter`，不在各服务重复定义。

## 4. 编码规范

- 包路径统一 `com.{{项目名}}.<服务名>.*`，如 `com.{{项目名}}.order.app`。
- 所有 API 返回统一 `Result<T>`（见 `{{项目名}}-common`）。
- `app` 层薄：Controller 只做参数校验、调 Service、包装返回；业务逻辑一律在 `service`。
- 实体类用 Lombok `@Data`，字段与表列一致。
- 幂等键、状态枚举优先用枚举，禁止散落魔法数字。
- API 路径前缀统一 `/api`。
- 分页用 MyBatis-Plus `Page<T>`。
- 复杂查询写 XML，单表 CRUD 走 BaseMapper。
- **跨服务调用必须走 Feign 客户端**（定义在依赖方的 `infra` 模块），禁止手写 RestTemplate 直连。
- 公共 DTO / 常量 / 工具类放 `{{项目名}}-common`，服务私有工具放本服务 `-common`。

## 5. 架构规则

### 5.1 服务拆分原则

- 按业务域拆分，每个服务独立数据库（Database per Service）。
- 服务间禁止直连数据库，只能通过 `client` 接口通信。
- 服务内依赖单向：app → service → infra → 外部 client。

### 5.2 服务调用

- 跨服务调用用 OpenFeign；接口定义在被依赖服务的 `-client` 模块，实现放其 `-app` 的 apiImpl。
- 依赖方在 `-infra` 引入被依赖方的 `-client`。
- 所有 Feign 客户端必须配置降级（fallback）与超时。
- 负载均衡由 LoadBalancer 自动处理，用服务名调用，不写死地址。

### 5.3 分布式事务

- 跨服务写操作用 Seata（AT 模式），发起方 Service 方法加 `@GlobalTransactional`。
- 单服务内多表写仍用本地事务 `@Transactional`。
- 优先用幂等 + 补偿规避分布式事务；确需强一致才用 Seata。

### 5.4 容错

- 高并发入口配 Sentinel 限流熔断，规则用 Nacos 配置中心动态下发。
- 降级逻辑返回兜底结果，不抛裸异常。

### 5.5 异常处理

- 业务校验失败抛业务异常，由 `{{项目名}}-spring-boot-starter` 中的 `GlobalExceptionHandler` 统一翻译为 `Result`。
- 网关统一处理未授权、限流、熔断的响应格式。

### 5.6 鉴权

- 鉴权在网关层统一做（JWT），下游服务信任网关透传的用户信息（透传机制由 starter 的 Context 组件提供）。

## 6. 测试规则

- 测试框架：JUnit 5 + Spring Boot Test。
- **单元测试为必须项**：新增/修改 Service 业务逻辑必须配套单元测试（mock Mapper / Feign），不允许"只写实现不写测试"。
- 集成测试：`@SpringBootTest`；写数据测试加 `@Transactional` 或 `@GlobalTransactional` 回滚。
- **覆盖率门槛**：核心业务逻辑（事务、状态机、计算）行覆盖率 ≥ 80%，新增代码覆盖率不得低于既有基线。
- 跨服务集成测试：被依赖服务用 mock 或 Testcontainers。
- 重点覆盖：本地事务一致性、Seata 分布式事务回滚、Feign 降级、Sentinel 限流、非法状态迁移。
- 命名：`方法名_场景_预期`，断言到具体值。
- 详见 `docs/verification.md` 与 `docs/skills/testing/`。

## 7. AI 行为规则

> 基于 Karpathy 的 LLM 编码规范（降低 AI 编程常见坑）。这些规则偏向「谨慎优于速度」，简单任务可酌情判断。

### 7.1 先想后做（Think Before Coding）

**不假设、不隐藏困惑、主动暴露取舍。**

- 动手前显式陈述假设；不确定就问，不静默猜测。
- 存在多种理解时，列出选项让用户选，不私自挑一个。
- 有更简单的方案时主动指出，必要时提出异议。
- 遇到模糊点，停下来说清哪里不清楚，提问。

### 7.2 最小改动（Simplicity First）

**只写解决问题所需的最少代码，不投机。**

- 不写需求之外的功能。
- 不为一次性代码引入抽象。
- 不添加未要求的「灵活性 / 可配置性」。
- 不为不可能发生的场景写错误处理。
- 200 行能压到 50 行，就重写。
- 自问：「资深工程师会不会觉得这过度设计？」若是，简化。

### 7.3 外科手术式（Surgical Changes）

**只碰必须碰的，只清理自己造成的烂摊子。**

- 不「顺手改进」相邻代码、注释、格式。
- 不重构没坏的东西。
- 匹配现有风格，即使自己会写得更不同。
- 发现无关的废弃代码，提出来，但不删。
- 只删除自己改动导致的失效 import/变量/函数；不动既有的死代码（除非被要求）。
- 检验标准：每一行改动都能追溯到用户需求。

### 7.4 目标驱动（Goal-Driven Execution）

**定义成功标准，循环直到验证通过。**

- 「加校验」→「先写非法输入测试，再让它通过」。
- 「修 bug」→「先写复现测试，再让它通过」。
- 多步任务先写分步计划，每步带验证点。
- 强成功标准才能独立闭环；弱标准（「让它能跑」）会反复需要澄清。

### 7.5 其他硬规则

- **不编造**：不臆测 API / 库行为；缺上下文先查代码，查不到再问。
- **破坏性操作先确认**：删文件、改表结构、改公开 API 前先告知并获确认。
- **先定服务归属再动手**：新功能先确认属于哪个服务；跨服务改动先写计划。
- **启动必问**：开工前必须确认项目名、groupId、服务清单（见第 0 节），不得用占位符或臆测值生成代码。
- **bug 修复必登记**：修复 bug 后必须在 `docs/bugfix-log.md` 登记（现象/根因/修复/预防）；若属"本应早知道"的模式问题，同时回写 `docs/skills/` 对应技能的 Common Mistakes。修复前先查 `docs/bugfix-log.md`，同类 bug 不得再次发生。

### 7.6 分阶段开工（先澄清后实现）

新项目从零开始或启动重大功能时，**必须按三阶段推进，一次只执行一个阶段**（详见 `docs/skills/create-project`）：

- **阶段 1 · init**：仅收集「项目名 + 一句话简介」，初始化文档骨架（填充占位符中的名称/简介，替换 `demall` 等示例占位），不追问技术栈等细节。
- **阶段 2 · requirement_clarify**：与用户讨论需求，产出 `roadmap.md`（顶层开发路线图）与 `docs/requirements.md`（需求文档）；同步将文档中的 `order/product/user` 等示例按实际业务重命名。**二者未产出前，不得进入实现、不得拆分任务。**
- **阶段 3 · start_implement**：依据 `roadmap.md` 与 `docs/requirements.md` 拆分任务到 `docs/tasks/`，此阶段才向用户索取 groupId / 服务清单 / 是否网关 / Seata 开关 / JDK·MySQL·Nacos 版本等具体信息，再搭代码骨架。

逆向禁止：未经阶段 2 的需求澄清，不得直接写实现代码或拆分任务卡。

## 8. Git 规范

- 提交信息 `type: 简述`：`feat/fix/refactor/docs/test/chore`。
- 分支：主干开发，特性 `feature/xxx`，修复 `fix/xxx`，合并前通过测试。
- 删除/重构公开 API、表结构、枚举、服务拆分边界变更前先沟通，并在 `docs/decisions/` 记录。
- 修复 bug 的提交在 `CHANGELOG.md` 记录摘要，详细登记见 `docs/bugfix-log.md`。

## 9. 常用命令

```bash
# 后端（多模块）
cd {{项目名}} && mvn -pl {{项目名}}-parent install -N   # 先安装父 POM（或按需）
mvn compile                                            # 编译全部模块
mvn test                                               # 测试
mvn -pl {{服务名}}/{{服务名}}-app spring-boot:run       # 启动单个服务（替换 {{服务名}}）

# 依赖中间件（Nacos / Seata / Zipkin / MySQL）需先启动

# 前端
cd {{项目名}}-front && npm install && npm run dev
```

> 本地启动顺序：Nacos / MySQL → 各业务服务 → Gateway → 前端。
