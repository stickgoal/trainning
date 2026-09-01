# 架构说明

## 1. 系统架构

本项目采用**微服务架构**，基于 Spring Cloud Alibaba。前端经统一网关访问后端各服务，服务间通过 OpenFeign 调用，治理能力由 Nacos / Sentinel / Seata / Zipkin 提供。组织上采用「全局公共模块 + 单服务五模块」结构。

```
┌──────────────┐      HTTP/JSON       ┌─────────────────────┐
│  前端 Vue 3  │ ───────────────────► │  Gateway（网关）     │
└──────────────┘                      │  路由/鉴权/限流      │
                                      └──────────┬──────────┘
                                                 │ 服务名路由
               ┌──────────────┬──────────────────┼──────────────────┬──────────────┐
               ▼              ▼                  ▼                  ▼              ▼
        ┌───────────┐  ┌───────────┐      ┌───────────┐      ┌───────────┐
        │ {{服务名}} │  │ {{服务名}} │      │ {{服务名}} │      │  (其他)    │
        └─────┬─────┘  └─────┬─────┘      └─────┬─────┘      └─────┬─────┘
              │              │                  │                  │
        ┌─────▼─────┐  ┌─────▼─────┐      ┌─────▼─────┐      ┌─────▼─────┐
        │  MySQL    │  │  MySQL    │      │  MySQL    │      │  MySQL    │
        │ (独立库)  │  │ (独立库)  │      │ (独立库)  │      │ (独立库)  │
        └───────────┘  └───────────┘      └───────────┘      └───────────┘

治理组件：Nacos（注册/配置）· Sentinel（限流熔断）· Seata（分布式事务）· Zipkin（链路追踪）
```

## 2. 全局模块职责

| 模块 | 职责 |
|------|------|
| `{{项目名}}-parent` | 全局父 POM：`dependencyManagement` 统一管理所有依赖与版本（Spring Boot / Spring Cloud / SCA / MyBatis-Plus / MySQL 等） |
| `{{项目名}}-common` | 全局公共代码：`Result<T>`、`ResultCode` 接口、公共错误码枚举、业务异常、公共常量 |
| `{{项目名}}-spring-boot-starter` | 公共 starter：通过 `AutoConfiguration.imports` 自动装配，提供异常处理、上下文透传、防重提交、日志 AOP 等横切能力 |

### 公共模块关键内容（参考 demall-common / demall-spring-boot-starter）

`{{项目名}}-common` 至少包含：

- `Result<T>`：统一返回体（`code` / `msg` / `data`，链式 setter，`isSuccess()`）。
- `ResultCode` 接口：`getCode()` / `getMessage()`，各服务实现自己的错误码枚举。
- 公共错误码枚举（如 `SUCCESS` / `GENERIC_ERROR` / `ILLEGAL_ARGUMENTS` / `DATA_NOT_FOUND` / `NOT_AUTHORIZED` / `DUPLICATE_SUBMISSION`）。
- 业务异常类（携带 `code`，供全局处理器翻译）。

`{{项目名}}-spring-boot-starter` 至少包含：

- `GlobalExceptionHandler`：把业务异常统一翻译为 `Result`。
- 上下文透传组件（`AppContext` / `ContextInfoHolder` / 过滤器 + Feign 拦截器）：把用户信息从网关透传到下游服务。
- 防重复提交（注解 + AOP 切面 + 幂等键）。
- 日志切面（记录关键调用）。
- `META-INF/spring/...AutoConfiguration.imports` 声明自动装配类。

> 以上为**结构与职责约束**，具体代码由 AI 在开工时实现，或从用户提供的参考项目读取并适当修改。

## 3. 单个服务五模块

每个业务服务（含 gateway）统一五模块结构，职责与依赖方向如下：

```
{{服务名}}/
├── pom.xml                   外层 POM：packaging=pom，继承 {{项目名}}-parent，声明五个子模块
├── {{服务名}}-client/        对外 API 契约：Feign 接口 + DTO，无实现
├── {{服务名}}-app/           应用入口：mainClass，装配与启动，无业务逻辑
├── {{服务名}}-service/       业务逻辑：编排 infra 与 client
├── {{服务名}}-infra/         基础设施：Mapper + 调用外部服务的 Feign 客户端
└── {{服务名}}-common/        服务内私有常量/工具/枚举
```

**依赖方向（单向）**：

```
app → service → infra → (外部 xxx-client + 本服务 -common)
  └→ client（app 的 apiImpl 实现 client 定义的接口）
```

## 4. 数据流

### 4.1 查询链路（同服务内）

```
前端 → Gateway → /api/xxx/{id}
     → {{服务名}}-app Controller
     → {{服务名}}-service Service
     → {{服务名}}-infra Mapper → MySQL
     → 组装 VO → Result<VO>
```

### 4.2 跨服务写链路（示例：下单，仅作示意，按实际业务调整）

```
前端 → Gateway → POST /api/orders
     → order-app Controller
     → order-service Service（@GlobalTransactional，Seata AT）
          1. 校验入参
          2. 通过 product-client（在 order-infra 引入）调商品服务扣库存
          3. 通过 user-client 调用户服务扣余额
          4. 写订单（本地事务，经 order-infra Mapper）
     → 任一环节失败，Seata 全局回滚
     → Result<OrderVO>
```

> 上例中的 `order` / `product` / `user` 仅为**示例服务名**，实际按第 0 节确认的服务清单替换。

## 5. 设计原则

- **服务自治**：每服务独立库、独立部署；不跨库直连。
- **契约与实现分离**：`-client` 只定义接口与 DTO，实现放 `-app`，依赖方只看契约。
- **薄 Controller**：`-app` 层只做校验与包装，业务下沉 `-service`。
- **统一返回与异常**：`Result<T>` 统一契约，异常由公共 starter 统一处理。
- **跨服务调用走 Feign**：不手写 RestTemplate 直连。
- **降级优先**：Feign/Sentinel 配 fallback，下游故障不拖垮上游。
- **分布式事务克制使用**：优先幂等 + 补偿，确需强一致才用 Seata AT。
- **鉴权上移网关**：下游信任网关透传的用户信息。

## 6. 数据模型（按服务）

| 服务 | 库 | 主要表 | 说明 |
|------|----|--------|------|
| {{服务名1}} | {{库名}} | <!-- 表 --> | <!-- 说明 --> |

## 7. 关键设计决策

- **微服务而非单体**：多团队并行、业务域独立、需独立扩容。见 `docs/decisions/`。
- **Nacos 而非 Eureka**：注册与配置一体。见对应 ADR。
- **Seata AT 而非 TCC/Saga**：AT 侵入小、上手快。见对应 ADR。
- **五模块单服务结构**：契约（client）/入口（app）/业务（service）/基础设施（infra）/私有工具（common）职责分离，便于契约复用与依赖单向。
- **条件更新防超卖**：扣库存/余额用 `ge(字段, 数量)` 条件更新，跨服务场景配合 Seata 回滚。
