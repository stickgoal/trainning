# AI-Ready 开发文档模板（Spring Boot 3.5 + Vue 3）

本目录提供三套可直接放入项目、供 AI 编码工具阅读后即可开工的开发文档模板。均为「轻量骨架 + 增强制品模板」风格，三套各自独立完整，任选其一拿走即用。

## 三套模板

| 目录 | 定位 | 适用场景 |
|------|------|----------|
| [`framework-agnostic/`](./framework-agnostic/) | 语言与框架无关版 | 技术栈未定、非 Java/Vue 项目、纯方法论 |
| [`monolith/`](./monolith/) | 单体版 | Spring Boot 3.5 + Vue 3，单体分层，单库单应用 |
| [`microservices/`](./microservices/) | 微服务版 | Spring Boot 3.5 + Vue 3 + Spring Cloud Alibaba |

## 三套的差异

| 维度 | 语言框架无关 | 单体 | 微服务 |
|------|-------------|------|--------|
| 技术栈 | `<!-- 占位 -->` | Java 17 + Spring Boot 3.5 + MyBatis-Plus + MySQL | 单体 + Spring Cloud Alibaba（Nacos/Gateway/OpenFeign/LoadBalancer/Sentinel/Seata/Zipkin） |
| 目录结构 | 占位 | `{{项目名}}` + `{{项目名}}-front` | 全局三公共模块 + 单服务五模块（client/app/service/infra/common） |
| 事务 | 本地事务 | 本地事务 `@Transactional` | 本地事务 + Seata 分布式事务 |
| 调用方式 | 占位 | 进程内调用 | OpenFeign 跨服务调用 |
| 中间件约束 | 占位 | 不引入微服务治理组件；Redis/MQ/ES 可选 | 全套微服务治理组件 |
| skills | 4 个（语言无关） | 11 个（含 Java 规范/审查/create-project） | 15 个（含微服务专项 + Java 规范/审查/create-project） |
| 项目名 | 占位 | 启动时 AI 询问用户填入 | 启动时 AI 询问用户填入（替换 demall） |

三套的**共同骨架**一致，仅内容按架构差异填充。

## 通用骨架说明

每套模板遵循「文档与源码分离」原则：除根目录三个文件外，所有文档收进 `docs/`。

```
<模板目录>/
├── README.md              项目介绍与文档使用方式
├── CHANGELOG.md           变更记录（迭代里程碑 + bug 修复摘要）
├── AGENTS.md              项目宪法：技术栈、目录、编码/架构/测试规则、AI 行为规则、Git 规范
├── docs/                  全部文档（与源码分离）
│   ├── product.md         产品说明（是什么、业务规则、非目标）
│   ├── architecture.md    架构说明（架构图、模块职责、数据流、设计原则、数据模型、关键决策）
│   ├── development.md     开发指南（环境、启动、测试、构建、Git、常见问题）
│   ├── verification.md    质量保障（验证闭环、测试分层、单元测试要求、质量门禁）
│   ├── bugfix-log.md      bug 修复记录（同类 bug 不再发生）
│   ├── decisions/         架构决策记录（ADR）含模板
│   ├── tasks/             任务卡库（含模板）
│   ├── plans/             计划库（含模板）
│   └── skills/            技能库（四段式 SKILL.md）
├── {{项目名}}/             后端源码（目录名 = 项目名，单体）
│                          （微服务版此目录为多模块 Maven）
└── {{项目名}}-front/        前端源码（目录名 = 项目名 + front）
```

> 目录命名约定（单体/微服务通用）：后端目录 = 项目名，前端目录 = 项目名 + `-front`。
> 项目名由 AI 在开工前向用户询问填入，替换文档中所有 `{{项目名}}` 占位符。

## 如何使用

1. **选型**：按上表「适用场景」选一套。
2. **拷贝**：把选中的目录内容复制到你的项目根目录（保留目录结构）。
3. **填充占位符**：把 `<!-- ... -->`、`{{项目名}}`、`{{服务名}}` 占位符替换为项目实际信息（项目名、业务域、技术栈、命令等）。单体版 `common` 模块内容从用户提供的参考项目读取并适当修改；微服务版参考 demall 结构（见 `AGENTS.md`）。
4. **让 AI 开工**：AI 会优先读根目录 `AGENTS.md`，确认第 0 节的项目名/服务清单，再按需读 `docs/` 对应文档开始开发。

## 关键设计原则

- **AGENTS.md 是宪法**：AI 每次对话先读它，它承载"必须遵守什么、禁止什么"。
- **文档与源码分离**：文档统一收进 `docs/`，根目录只留 README/CHANGELOG/AGENTS 三个入口文件。
- **制品模板是契约**：任务卡的可验证验收标准、API 规格契约、质量门禁，让 AI 的产出可测、可评审。
- **bug 修复必登记**：`docs/bugfix-log.md` 记录每笔 bug 的现象/根因/修复/预防，配合 skills 回写，确保同类 bug 不再发生。
- **单元测试是底线**：新增/修改业务逻辑必须配套单元测试，核心逻辑覆盖率 ≥ 80%。
- **skills 是经验库**：四段式（When To Use / Rules / Examples / Common Mistakes），随项目持续回写，越用越准。
- **ADR 是决策史**：记录"为什么这样选"，避免 AI 重复踩坑或推翻既有决策。

## 参考来源

本套模板基于 auto-mall 项目实践，并吸收了多个 AI 编码文档模板的精华（arc42 架构结构、ADR/MADR 决策格式、AI 行为规则、任务分级与质量门禁等）。详见项目根目录 `reference/EVALUATION.md`。
