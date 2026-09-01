# 项目脚手架技能（Create Project · 分阶段）

## When To Use

本技能用于在**已拷贝本版文档的空文件夹**中，从零搭建 Spring Boot 3.5 + Vue 3 单体应用骨架。典型场景：用户把 `monolith/` 目录整体复制到某空目录，然后说"帮我建个单体项目"，你据此分阶段推进——**一次只执行一个阶段**。

本版已绑定技术栈（Java 17 + Spring Boot 3.5 + MyBatis-Plus + MySQL + Vue 3），故阶段 3 的"栈"收敛为"项目相关细节"（groupId / 中间件 / 参考项目等）。

## Rules

### 总原则：三阶段，一次一个

| 阶段 | 名称 | 输入 | 产出 | 是否追问项目细节 |
|------|------|------|------|------------------|
| 1 | `init` | 项目名 + 一句话简介 | 文档骨架初始化（占位符填名称/简介） | 否 |
| 2 | `requirement_clarify` | 与用户对话 | `roadmap.md` + `docs/requirements.md` | 否（只谈需求） |
| 3 | `start_implement` | groupId / 中间件 / 参考项目 / 版本 | 代码骨架 + `docs/tasks/` 任务卡 | 是 |

**逆向禁止**：阶段 2 未产出 `roadmap.md` 与 `docs/requirements.md` 前，不得拆分任务、不得写实现代码。

---

### 阶段 1 · init（仅项目名 + 简介）

1. **读文档**：`AGENTS.md`（第 0 节、第 7.6 节）→ `README.md` → `docs/*.md`。
2. **只问两项**：
   - Q1：项目名（英文小写中划线，决定后端目录名 `{{项目名}}/`，前端 `{{项目名}}-front/`）
   - Q2：一句话简介
3. **初始化文档骨架**：把 `AGENTS.md` / `README.md` / `docs/*.md` 中名称/简介类占位符填为实际值；技术栈/中间件/目录相关占位**保持**，留待阶段 3。
4. **不动代码**：不创建 `{{项目名}}/`、`{{项目名}}-front/` 源码目录。
5. **告知下一步**：「项目已初始化：`<项目名>` —— <简介>。下一步进入需求澄清（requirement_clarify）。」

---

### 阶段 2 · requirement_clarify（与用户讨论需求，产出 roadmap + 需求文档）

1. **读阶段 1 成果**：确认 `AGENTS.md` 中项目名/简介已就位。
2. **与用户对话澄清需求**，覆盖：核心用户与场景、关键功能清单、非功能诉求（并发/性能/安全）、范围边界、成功标准。
3. **产出两份文档**：
   - `roadmap.md`（项目根目录）：顶层开发路线图。按里程碑划分，每阶段含目标/范围/顺序/交付物。
   - `docs/requirements.md`：功能需求（FR-xx + 描述 + 验收要点）、非功能需求（NFR）、范围边界、术语表。
4. **不写代码、不拆分任务**。
5. **门禁**：两份文档经用户确认后，方可进入阶段 3。

---

### 阶段 3 · start_implement（索取项目细节，搭骨架，拆任务）

1. **依据 `roadmap.md` + `docs/requirements.md` 拆分任务**：在 `docs/tasks/` 按里程碑写任务卡，每张含可验证验收标准，映射到路线图阶段。
2. **向用户索取项目相关细节**（技术栈本版已定，问的是落地参数）：
   - Q1：groupId（如 `com.automall`）
   - Q2：需要启用哪些可选中间件（Redis / RocketMQ / ES，可多选）
   - Q3：参考项目路径（含 common 模块）—— **必问**，用于读取并适当修改 common
   - Q4：JDK / MySQL 版本（默认 17 / 8.0）
3. **搭最小可运行骨架**：
   - 后端 `{{项目名}}/`（Spring Boot 3.5 骨架 + 来自参考项目的 common 模块适当改名 + `Result<T>`/`BusinessException`/`@Transactional` 约定 + 一条冒烟接口）
   - 前端 `{{项目名}}-front/`（Vue 3 + Element Plus + Vite，devServer proxy → 后端 :8080）
   - 启用的中间件：在 `pom.xml` 引入并建 `deploy/`（docker-compose + init-sql），配置进 `application.yml`，敏感信息环境变量化
4. **收尾**：跑 `cd {{项目名}} && mvn test`；`CHANGELOG.md` 追加 `init: 初始化 {{项目名}} 单体骨架（中间件：xxx）`；坑回写 `docs/skills/`。

## Examples

```
用户：把 monolith 拷到 D:/proj/shop，建项目。
AI[阶段1 init]：项目名？简介？ → shop / 门店管理
  → 文档占位填名称/简介，无源码。
AI[阶段2 requirement_clarify]：聊需求 → roadmap.md（M1 门店/商品 CRUD / M2 库存 / M3 统计）+ docs/requirements.md
  → 用户确认。
AI[阶段3 start_implement]：groupId？中间件？参考项目？ → com.shop / Redis / C:/ref/oldmall
  → 生成 shop/（含来自 oldmall 的 common 适当改名）+ shop-front/ + tasks/，mvn test 通过。
```

## Common Mistakes

- **阶段 1 就问 groupId/中间件**：init 只收名称+简介；细节留到阶段 3。
- **跳过需求澄清**：未产出 `roadmap.md` + `docs/requirements.md` 就写代码，违反 7.6。
- **common 凭空发明**：必须来自阶段 3 的参考项目并适当修改。
- **默认引入微服务治理组件**：单体版不引入 Nacos/Gateway/Seata；Redis/MQ/ES 属可选且需用户选。
- **前端目录命名错**：必须是 `{{项目名}}-front`。
- **占位符残留**：交付前全量检查 `<!-- ... -->` 必须清零。
