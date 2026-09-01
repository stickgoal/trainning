# 质量保障体系

本文定义从实现到交付的验证闭环。目标——**改动必须可验证、可构建、可评审、可对照验收标准**。

## 1. 验证闭环

```
Task（任务卡）→ Implementation（实现）→ Test（测试）→ Review（评审）→ Evaluation（对照验收标准评估）
```

任何改动交付前都应跑完这条链路。任务卡的 Acceptance Criteria 是评估的最终依据。

## 2. 测试分层

| 层 | 范围 | 框架 | 命令 |
|----|------|------|------|
| 单元测试 | 单个 Service 方法逻辑分支（mock Mapper） | JUnit 5 + Mockito | `mvn test -Dtest=XxxTest` |
| 集成测试 | 跨层链路（Controller→Service→Mapper→DB） | Spring Boot Test `@SpringBootTest` | `mvn test` |
| API 测试 | 对外契约（请求/响应/状态码） | 手动 curl 或 RestAssured | 见 `docs/development.md` |

## 3. 单元测试要求（完善版）

单元测试是本项目质量的第一道防线，**新增或修改 Service 业务逻辑必须配套单元测试**。

### 3.1 强制项

- **必须写**：任何新增/修改的 Service 方法都要有对应单元测试，不允许"只写实现不写测试"。
- **mock 隔离**：单元测试用 Mockito mock Mapper，不连数据库，跑得快、可重复。
- **覆盖率门槛**：核心业务逻辑（事务、状态机、计算）行覆盖率 ≥ 80%；新增代码覆盖率不得低于既有基线。
- **命名规范**：`方法名_场景_预期`，如 `createOrder_库存不足_抛异常`。

### 3.2 覆盖场景

每个 Service 方法至少覆盖：

| 场景 | 说明 |
|------|------|
| 正常路径 | 主流程成功 |
| 边界条件 | 空值、零值、超量、临界值 |
| 异常路径 | 库存不足、余额不足、依赖失败 |
| 非法状态 | 状态机不允许的迁移 |

### 3.3 断言要求

- 断言到**具体值**（`assertEquals(198, sku.getStock())`），不用 `assertNotNull` 凑数。
- 异常断言到**具体异常类型与错误码**（`BusinessException` + 对应 `ResultCode`）。
- 验证 mock 的调用次数与参数（`verify(mapper, times(1)).deduct(...)`）。

### 3.4 测试独立性

- 测试之间不共享状态，不依赖执行顺序。
- 每个测试自带数据构造（given/when/then 三段式）。

## 4. 构建

```bash
cd backend
mvn clean package -DskipTests    # 打可执行 jar
mvn test                          # 跑测试（含覆盖率校验）
```

- 构建必须通过，否则不允许提交。
- 单元测试必须全绿，覆盖率不达标不通过。

## 5. 评审（Code Review）

按 `docs/skills/code-review/SKILL.md` 的清单逐项过：

- 分层是否越界（Controller 写业务、Service 直连表）
- 是否返回统一 `Result<T>`
- 异常是否走 `BusinessException` + 全局处理
- 事务边界是否正确（跨表写是否在一个事务内）
- 并发安全（扣库存/余额是否用条件更新防超卖）
- 是否引入微服务治理组件（Nacos/Gateway 等）
- 可选中间件（Redis/MQ/ES）引入前是否补 ADR、配置是否版本化
- **是否有配套单元测试、覆盖率是否达标**
- 命名、魔法数字、空指针等基础项

## 6. 评估

以任务卡的 **Acceptance Criteria** 为准绳逐条核对：

- 每条标准是否有对应测试覆盖
- 是否触达 Non Goals 明确排除的范围
- 是否引入新的架构决策（若是，补 ADR）

## 7. 质量门禁

| 门禁 | 条件 | 阻塞交付 |
|------|------|----------|
| G-COMPILE | `mvn compile` 通过 | 是 |
| G-UNIT | 单元测试全绿 + 覆盖率达标 | 是 |
| G-INT | 集成测试全绿 | 是 |
| G-AC | P0 验收标准全覆盖 | 是 |

## 8. 失败处理

- 测试失败：先修测试或实现，不跳过、不 `@Ignore` 掩盖。
- 覆盖率不达标：补测试，不删断言凑数。
- 构建失败：定位编译/依赖问题，不绕过。
- 评审不通过：按意见回开发修订，不改评审标准迁就代码。
