# 项目脚手架技能（Create Project · 分阶段）

## When To Use

本技能用于在**已拷贝本版文档的空文件夹**中，从零搭建 Spring Boot 3.5 + Vue 3 + Spring Cloud Alibaba 微服务骨架。典型场景：用户把 `microservices/` 目录整体复制到某空目录，然后说"帮我建个微服务项目"，你据此分阶段推进——**一次只执行一个阶段**。

本版已绑定技术栈（Nacos / OpenFeign / Gateway / Sentinel / Seata / Zipkin + 全局三模块 + 单服务五模块），故阶段 3 的"细节"收敛为服务拆分与治理开关。

## Rules

### 总原则：三阶段，一次一个

| 阶段 | 名称 | 输入 | 产出 | 是否追问项目细节 |
|------|------|------|------|------------------|
| 1 | `init` | 项目名 + 一句话简介 | 文档骨架初始化（替换 `demall`/名称占位） | 否 |
| 2 | `requirement_clarify` | 与用户对话 | `roadmap.md` + `docs/requirements.md` | 否（只谈需求，去电商化） |
| 3 | `start_implement` | groupId / 服务清单 / 网关 / Seata / 版本 | 代码骨架 + `docs/tasks/` 任务卡 | 是 |

**逆向禁止**：阶段 2 未产出 `roadmap.md` 与 `docs/requirements.md` 前，不得拆分任务、不得写实现代码。

---

### 阶段 1 · init（仅项目名 + 简介）

1. **读文档**：`AGENTS.md`（第 0 节、第 7.6 节、单服务五模块/全局三模块约束）→ `README.md` → `docs/*.md`。
2. **只问两项**：
   - Q1：项目名（英文小写，全局替换文档中的 `demall` 占位 → `{{项目名}}`）
   - Q2：一句话简介
3. **初始化文档骨架**：把 `AGENTS.md` / `README.md` / `docs/*.md` 中名称/简介类占位符填为实际值；`demall` 全量替换为 `{{项目名}}`（保留 `{{}}` 占位，不臆测服务名）；技术/服务相关占位保持。
4. **不动代码**：不创建 parent/common/starter 或任何服务模块。
5. **告知下一步**：「项目已初始化：`<项目名>` —— <简介>。下一步进入需求澄清（requirement_clarify）。」

---

### 阶段 2 · requirement_clarify（与用户讨论需求，产出 roadmap + 需求文档，去电商化）

1. **读阶段 1 成果**：确认 `AGENTS.md` 中 `demall` 已替换为 `{{项目名}}`、项目名/简介已就位。
2. **与用户对话澄清需求**，覆盖：核心用户与场景、关键功能域清单、跨服务交互诉求、非功能诉求（可用性/一致性/性能）、范围边界、成功标准。
3. **产出两份文档**：
   - `roadmap.md`（项目根目录）：顶层开发路线图。按里程碑划分（如 M1 基础平台、M2 核心域服务、M3 网关与治理），每阶段含目标/范围/顺序/交付物。
   - `docs/requirements.md`：功能需求（FR-xx + 验收要点）、非功能需求（NFR）、范围边界。
4. **去电商化**：将文档里的 `order/product/user` 等示例，按本阶段澄清的实际业务域重命名（如 `account/contract/billing`），不得保留电商语义。
5. **不写代码、不拆分任务**。
6. **门禁**：两份文档经用户确认后，方可进入阶段 3。

---

### 阶段 3 · start_implement（索取服务细节，搭骨架，拆任务）

1. **依据 `roadmap.md` + `docs/requirements.md` 拆分任务**：在 `docs/tasks/` 按里程碑写任务卡，每张含可验证验收标准，映射到路线图阶段。
2. **向用户索取服务相关细节**：
   - Q1：groupId（如 `com.demall` → `com.{{项目名}}`）
   - Q2：服务清单（如 `user, product, order`，须对齐阶段 2 的业务域命名）
   - Q3：是否需要 API 网关（gateway 服务，默认需要）
   - Q4：是否启用分布式事务（Seata，默认旁路）
   - Q5：JDK / MySQL / Nacos 版本（默认 17 / 8.0 / 2025.0.0.0）
3. **搭最小可运行骨架**：
   - 全局三模块：`{{项目名}}-parent`（dependencyManagement 统一版本）/ `{{项目名}}-common` / `{{项目名}}-spring-boot-starter`
   - 每个服务的五模块：`{{服务名}}-client/app/service/infra/common`，依赖单向 `app→service→infra→外部client`
   - 网关（Q3=是）：`gateway/` 模块
   - 前端：`{{项目名}}-front/`（Vue3 + Vite，proxy → gateway）
4. **收尾**：跑父 POM 编译；`CHANGELOG.md` 追加 `init: 初始化 {{项目名}} 微服务骨架（服务：xxx；网关：是/否）`；坑回写 `docs/skills/`。

## Examples

```
用户：把 microservices 拷到 D:/proj/crm，建项目。
AI[阶段1 init]：项目名？简介？ → crm / 合同管理系统（替换 demall→crm）
  → 文档占位填名称/简介，demall 全替为 crm，无源码。
AI[阶段2 requirement_clarify]：聊需求 → roadmap.md（M1 平台/M2 域服务/M3 网关）+ docs/requirements.md（account/contract/billing 替代 order/product/user）
  → 用户确认。
AI[阶段3 start_implement]：groupId？服务？网关？ → com.crm / account,contract,billing / 是
  → 生成 crm-parent/common/starter + 三个五模块 + gateway + crm-front/，mvn compile 通过。
```

## Common Mistakes

- **阶段 1 就问服务清单/groupId**：init 只收名称+简介；细节留到阶段 3。
- **跳过需求澄清**：未产出 `roadmap.md` + `docs/requirements.md` 就写代码，违反 7.6。
- **保留电商语义**：`order/product/user` 是示例，必须按阶段 2 实际业务重命名。
- **依赖方向错**：违反 `app→service→infra→外部client` 单向。
- **版本不统一**：各模块版本必须由 `-parent` 的 `dependencyManagement` 统一。
- **占位符残留**：交付前全量检查 `demall` / `{{项目名}}` / `<!-- ... -->` 必须清零。
