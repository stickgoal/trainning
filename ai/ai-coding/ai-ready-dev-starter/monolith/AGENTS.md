# AGENTS.md

> 本文件是项目宪法。AI 编码工具（Cursor / Claude / Codex 等）每次对话首先读本文件，再动手。改动前先读本文，改动后保持本文与代码一致。
> 本文为**单体架构**版本：Spring Boot 3.5 + Vue 3 前后端分离。业务部分以 `<!-- -->` 占位。

## 0. 启动必问

**AI 在开始任何开发前，必须先向用户确认以下信息，未确认不得臆测：**

| 项 | 说明 | 示例 |
|----|------|------|
| 项目名 | 用于命名后端目录、groupId、包名 | `shop`（替换本文所有 `{{项目名}}` 占位符） |
| 参考项目路径 | 用于读取 `common` 模块代码的参考项目 | `D:/path/to/reference-project` |

> 本文所有 `{{项目名}}` 均为占位符，须在开工前替换为真实值。

## 1. 项目介绍

<!-- 一句话说明项目是什么、解决什么问题、目标用户 -->

## 2. 技术栈

**后端**

- Java 17
- Spring Boot 3.5.x
- MyBatis-Plus 3.5.x（数据访问）
- MySQL 8.0（主数据库）
- JUnit 5 + Spring Boot Test（测试）

**前端**

- Vue 3 + Element Plus + Vue Router + Vite

**构建**：Maven（后端）、npm（前端）

**可选中间件**（按需引入，引入前先更新本文件与 `docs/decisions/`）

| 中间件 | 用途 |
|--------|------|
| Redis | 缓存、分布式锁 |
| RocketMQ | 消息队列、异步解耦 |
| Elasticsearch | 搜索、日志分析 |

> 本单体项目**不引入微服务治理组件**（Nacos / Gateway / OpenFeign / Seata / Sentinel）。上述可选中间件是数据/消息类基础设施，与微服务治理无关，可按需引入；引入时其配置必须版本化存放（见第 9 节与 `docs/development.md`）。

## 3. 目录结构

```
项目根/
├── README.md               项目介绍与文档使用方式
├── CHANGELOG.md            变更记录（迭代里程碑 + bug 修复摘要）
├── AGENTS.md               本文件：项目宪法
├── docs/                   全部文档（与源码分离，见下方说明）
│   ├── product.md          产品说明
│   ├── architecture.md     架构说明
│   ├── development.md      开发指南
│   ├── verification.md     质量保障体系
│   ├── bugfix-log.md       bug 修复记录
│   ├── decisions/          架构决策记录（ADR）
│   ├── tasks/              任务卡库
│   ├── plans/              计划库
│   └── skills/             技能库
├── {{项目名}}/              后端源码（目录名 = 项目名）
│   └── src/main/java/com/{{项目名}}/
│       ├── controller/    API 层：仅参数校验 + 调 Service + 包装 Result
│       ├── service/       业务层：接口在 service/，实现在 service/impl/
│       ├── mapper/        数据访问层：继承 MyBatis-Plus BaseMapper
│       ├── entity/        数据库实体，@TableName / @TableId 对应表
│       ├── dto/           request/ 入参、response/ 出参
│       ├── config/        框架配置
│       └── common/        统一返回、异常、常量、枚举（内容取自参考项目，见第 4 节）
└── {{项目名}}-front/        前端源码（目录名 = 项目名 + front）
    └── src/{views,api,router,store}
```

> **目录命名约定**：后端目录 = 项目名（`{{项目名}}`），前端目录 = 项目名 + `-front`（`{{项目名}}-front`）。
> **文档与源码分离**：除 `README.md`、`CHANGELOG.md`、`AGENTS.md` 三个根级文件外，所有文档一律放 `docs/`。

## 4. 编码规范

- 包路径统一 `com.{{项目名}}.*`，不允许引入非本项目业务根包。
- 所有 API 返回统一 `Result<T>`（见 `common/result/Result`）。
- Controller 薄层：只做参数校验、调 Service、包装返回；业务逻辑一律在 Service 实现类。
- 实体类用 Lombok `@Data`，字段与表列保持一致。
- 幂等键、状态枚举优先用 `common/enums`，禁止在业务代码散落魔法数字。
- API 路径前缀统一 `/api`。
- 分页用 MyBatis-Plus `Page<T>` + `PaginationInnerInterceptor`（在 `config/` 注册）。
- 复杂查询（多表 JOIN、批量条件）写 XML 放 `resources/mapper/`，单表 CRUD 由 BaseMapper 托管。
- **`common` 模块内容取自参考项目**：开工时向用户索要参考项目路径，读取其 common 模块（统一返回、异常、常量、枚举），套用并**适当修改**（替换包名、去掉无关内容），不凭空发明。

## 5. 架构规则

- 分层依赖方向单向：Controller → Service → Mapper → Entity，不允许反向或跨层调用。
- 跨多表写操作（如下单）在单个本地事务 `@Transactional` 内完成。
- 扣库存、扣余额采用条件更新防超卖：`update().ge(字段, 数量).eq(...)`，返回影响行数判断成功与否；失败抛 `BusinessException`。
- 业务校验失败统一抛业务异常，由全局异常处理器翻译为 `Result`（code 非 0）；不向上抛裸异常、不吞异常。
- 跨域在 `config/WebConfig` 统一配置，Controller 上不单独加 `@CrossOrigin`。

## 6. 测试规则

- 测试框架：JUnit 5 + Spring Boot Test。
- **单元测试为必须项**：新增或修改 Service 业务逻辑必须配套单元测试（Mockito mock Mapper），不允许"只写实现不写测试"。
- 集成测试用 `@SpringBootTest`，写数据测试加 `@Transactional` 防跨测试数据污染。
- **覆盖率门槛**：核心业务逻辑（事务、状态机、计算）行覆盖率 ≥ 80%，新增代码覆盖率不得低于既有基线。
- 业务逻辑重点覆盖：事务一致性、库存不足回滚、余额不足回滚、非法状态迁移。
- 命名：`方法名_场景_预期`，断言到具体值。
- 运行：`cd {{项目名}} && mvn test`。
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
- **启动必问**：开工前必须确认项目名、参考项目路径（见第 0 节），不得用占位符或臆测值生成代码。
- **bug 修复必登记**：修复 bug 后必须在 `docs/bugfix-log.md` 登记（现象/根因/修复/预防）；若属"本应早知道"的模式问题，同时回写 `docs/skills/` 对应技能的 Common Mistakes。修复前先查 `docs/bugfix-log.md`，同类 bug 不得再次发生。

### 7.6 分阶段开工（先澄清后实现）

新项目从零开始或启动重大功能时，**必须按三阶段推进，一次只执行一个阶段**（详见 `docs/skills/create-project`）：

- **阶段 1 · init**：仅收集「项目名 + 一句话简介」，初始化文档骨架（填充占位符中的名称/简介），不追问技术栈等细节。
- **阶段 2 · requirement_clarify**：与用户讨论需求，产出 `roadmap.md`（顶层开发路线图）与 `docs/requirements.md`（需求文档）。**二者未产出前，不得进入实现、不得拆分任务。**
- **阶段 3 · start_implement**：依据 `roadmap.md` 与 `docs/requirements.md` 拆分任务到 `docs/tasks/`，此阶段才向用户索取 groupId / 可选中间件 / 参考项目路径（取 common 模块）/ JDK·MySQL 版本等具体信息，再搭代码骨架。

逆向禁止：未经阶段 2 的需求澄清，不得直接写实现代码或拆分任务卡。

## 8. Git 规范

- 提交信息 `type: 简述`：`feat/fix/refactor/docs/test/chore`。
- 分支策略：主干开发，特性 `feature/xxx`，修复 `fix/xxx`，合并前通过测试。
- 公开 API、表结构、枚举的删除/重构需先沟通，并在 `docs/decisions/` 记录。
- 修复 bug 的提交在 `CHANGELOG.md` 记录摘要，详细登记见 `docs/bugfix-log.md`。

## 9. 常用命令

```bash
# 后端
cd {{项目名}} && mvn compile          # 编译
cd {{项目名}} && mvn test             # 测试
cd {{项目名}} && mvn spring-boot:run  # 启动 :8080

# 前端
cd {{项目名}}-front && npm install && npm run dev   # 前端 :5173
```

## 10. 中间件配置版本化

引入的可选中间件（Redis / RocketMQ / ES 等）配置必须版本化存放，不得只存在于开发者本机：

```
{{项目名}}/
├── deploy/                   部署与中间件配置（版本化）
│   ├── docker-compose.yml    MySQL / Redis / RocketMQ / ES 等容器编排
│   └── init-sql/             初始化脚本
└── src/main/resources/       application.yml 连接参数（敏感信息走环境变量或 local profile）
```

- 连接账号/密码等敏感信息走环境变量或本地 profile（`application-local.yml` 不入库），但模板与占位符必须入库。
