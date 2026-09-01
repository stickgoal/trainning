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
| 单元测试 | 单个 Service 方法逻辑分支（mock Mapper/Feign） | JUnit 5 + Mockito | `mvn test -Dtest=XxxTest` |
| 集成测试 | 单服务跨层链路（Controller→Service→Mapper→DB） | `@SpringBootTest` | `mvn -pl service-x test` |
| 跨服务测试 | 服务间调用、Seata 回滚、降级 | Testcontainers / mock | `mvn test` |
| API 测试 | 对外契约（经网关） | 手动 curl 或 RestAssured | 见 `docs/development.md` |

## 3. 单元测试要求（完善版）

单元测试是本项目质量的第一道防线，**新增或修改 Service 业务逻辑必须配套单元测试**。

### 3.1 强制项

- **必须写**：任何新增/修改的 Service 方法都要有对应单元测试，不允许"只写实现不写测试"。
- **mock 隔离**：单元测试 mock 掉 Mapper 与 Feign 客户端，不连数据库、不连下游服务，跑得快、可重复。
- **覆盖率门槛**：核心业务逻辑（事务、状态机、计算）行覆盖率 ≥ 80%；新增代码覆盖率不得低于既有基线。
- **命名规范**：`方法名_场景_预期`，如 `createOrder_库存不足_抛异常`。

### 3.2 覆盖场景

每个 Service 方法至少覆盖：

| 场景 | 说明 |
|------|------|
| 正常路径 | 主流程成功 |
| 边界条件 | 空值、零值、超量、临界值 |
| 异常路径 | 库存不足、余额不足、Feign 下游失败 |
| 非法状态 | 状态机不允许的迁移 |

### 3.3 断言要求

- 断言到**具体值**，不用 `assertNotNull` 凑数。
- 异常断言到**具体异常类型与错误码**。
- 验证 mock 的调用次数与参数（`verify(feignClient, times(1)).deduct(...)`）。
- 跨服务降级逻辑单独测试：mock Feign 抛异常，断言走 fallback 返回兜底结果。

### 3.4 测试独立性

- 测试之间不共享状态，不依赖执行顺序。
- 每个测试自带数据构造（given/when/then 三段式）。

## 4. 构建

```bash
cd backend
mvn clean package -DskipTests    # 全模块打包
mvn test                          # 全模块测试（含覆盖率校验）
```

- 构建必须通过，否则不允许提交。
- 单元测试必须全绿，覆盖率不达标不通过。

## 5. 评审（Code Review）

按 `docs/skills/code-review/SKILL.md` 的清单逐项过，微服务额外关注：

- 服务边界是否清晰（有无跨库直连）
- 跨服务调用是否走 Feign（有无手写 RestTemplate）
- Feign 是否配了 fallback 与超时
- 分布式事务边界是否正确（`@GlobalTransactional` 位置）
- 鉴权是否在网关统一处理
- **是否有配套单元测试、覆盖率是否达标**
- 是否引入 `AGENTS.md` 未声明的中间件

## 6. 评估

以任务卡的 **Acceptance Criteria** 为准绳逐条核对：

- 每条标准是否有对应测试覆盖
- 是否触达 Non Goals 排除的范围
- 是否引入新的架构决策（若是，补 ADR）

## 7. 质量门禁

| 门禁 | 条件 | 阻塞交付 |
|------|------|----------|
| G-COMPILE | `mvn compile` 通过 | 是 |
| G-UNIT | 单元测试全绿 + 覆盖率达标 | 是 |
| G-INT | 集成测试全绿 | 是 |
| G-AC | P0 验收标准全覆盖 | 是 |

## 8. 失败处理

- 测试失败：先修测试或实现，不跳过、不掩盖。
- 覆盖率不达标：补测试，不删断言凑数。
- 构建失败：定位编译/依赖问题，不绕过。
- 评审不通过：按意见回开发修订，不改评审标准迁就代码。
