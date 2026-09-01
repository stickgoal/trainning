# 架构说明

## 1. 系统架构

本项目采用**单体分层架构**：一个 Spring Boot 应用承载全部后端业务，前端 Vue 3 通过 RESTful API 与其通信。不引入注册中心、网关、RPC、分布式事务等微服务治理组件。

```
┌──────────────┐         HTTP/JSON          ┌─────────────────────────────┐
│  前端 Vue 3  │ ───────────────────────►  │  {{项目名}}（Spring Boot）    │
│ {{项目名}}-front│ ◄───────────────────────  │                             │
└──────────────┘                           │  Controller → Service →    │
                                           │  Mapper → MySQL             │
                                           └─────────────────────────────┘
                                                      │
                                                      ▼
                                                MySQL 8.0
                                        （可选：Redis / RocketMQ / ES）
```

## 2. 模块职责

| 层 | 包 | 职责 |
|----|----|------|
| API 层 | `controller` | 参数校验、调 Service、统一 `Result<T>` 返回，无业务逻辑 |
| 业务层 | `service` / `service/impl` | 核心业务逻辑、事务边界、规则编排 |
| 数据层 | `mapper` | MyBatis-Plus 数据访问，单表 CRUD 走 `BaseMapper`，复杂查询走 XML |
| 实体层 | `entity` | 与表一一对应的 POJO |
| DTO 层 | `dto/request`、`dto/response` | 入参与出参，隔离内部实体与外部契约 |
| 配置层 | `config` | MyBatis-Plus 分页、跨域等框架配置 |
| 通用层 | `common` | 统一返回 `Result`、异常、常量、枚举（取自参考项目并适当修改） |

业务域划分：

- **<!-- 域 1 -->**：<!-- 职责 -->
- **<!-- 域 2 -->**：<!-- 职责 -->

## 3. 数据流

### 3.1 <!-- 查询链路 -->

```
前端 → GET /api/xxx/{id}
     → XxxController
     → XxxService.getXxx(id)
     → XxxMapper（单表查询）
     → 组装 XxxVO
     → Result<XxxVO>
```

### 3.2 <!-- 核心写链路 -->

```
前端 → POST /api/xxx {入参}
     → XxxController
     → XxxService（@Transactional）
          1. 校验
          2. 条件更新（防超卖/防超扣）
          3. 写主记录 + 关联记录
     → XxxVO
     → Result<XxxVO>
```

整个写链在**一个本地事务**内完成，任一环节失败整体回滚。

## 4. 设计原则

- **分层单向依赖**：Controller → Service → Mapper，禁止反向或跨层调用。
- **薄 Controller**：API 层只做校验与包装，业务规则下沉 Service。
- **统一返回与异常处理**：`Result<T>` 统一契约，业务异常由全局处理器翻译。
- **事务内聚**：跨多表写操作放在同一本地事务，条件更新防并发超卖。
- **不引入微服务治理组件**：保持单体简单性，可选中间件（Redis/MQ/ES）按需引入并配置版本化。

## 5. 数据模型

| 表 | 实体 | 说明 |
|----|------|------|
| <!-- 表 1 --> | <!-- 实体 1 --> | <!-- 说明 --> |

## 6. 关键设计决策

- **单体而非微服务**：当前业务规模下单体开发部署成本显著低于微服务。见 `docs/decisions/`。
- **MySQL 而非内存库**：生产可读、数据可持久。
- **条件更新防超卖**：扣库存/余额用 `ge(字段, 数量)` 条件更新而非先查后改。
- **中间件按需引入**：Redis/RocketMQ/ES 等可选，引入前补 ADR，配置版本化。
