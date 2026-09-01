# Code Review 技能

## When To Use

提交前自查、评审他人代码时。Review 是质量保障的最后一道关。

## Rules（检查清单）

### 五模块边界
- [ ] `-client` 只含接口 + DTO，无任何实现
- [ ] `-app` 只做入口装配，Controller 薄层，无业务逻辑
- [ ] `-service` 承载业务逻辑，不直连数据库
- [ ] `-infra` 收口 DB 访问（Mapper）与外部 Feign 调用
- [ ] `-common` 只放服务私有常量/工具/枚举
- [ ] 依赖方向单向：app → service → infra → 外部 client，无反向

### 服务边界
- [ ] 无跨服务直连数据库（服务 A 不读服务 B 的库）
- [ ] 跨服务调用走 Feign，无手写 RestTemplate 直连
- [ ] 公共 DTO/常量放 `{{项目名}}-common`，无双份定义

### 返回与异常
- [ ] 所有 API 返回 `Result<T>`
- [ ] 业务校验抛业务异常，无裸异常、无吞异常
- [ ] 错误码集中，无散落字符串

### 事务与并发
- [ ] 服务内多表写在一个 `@Transactional` 内
- [ ] 跨服务写用 `@GlobalTransactional`（发起方），且参与库建了 undo_log
- [ ] 扣减类操作用条件更新防超卖，无"先查后改"

### 容错与降级
- [ ] Feign 客户端配了 fallback 与超时
- [ ] 高并发入口配了 Sentinel 限流/熔断
- [ ] 降级逻辑返回友好 `Result`，无裸异常

### 网关与鉴权
- [ ] 鉴权在网关统一处理，无下沉到每个服务
- [ ] 路由名与 Nacos 注册名一致
- [ ] 限流/熔断返回统一 `Result` 格式

### 数据库
- [ ] 字符集 `utf8mb4`，`spring.sql.init.encoding=UTF-8`
- [ ] 状态字段走枚举，无魔法数字
- [ ] 业务数据软删除，无物理删

### 测试
- [ ] 新增/修改 Service 有配套单元测试
- [ ] 覆盖率达标（核心逻辑 ≥ 80%）

### 架构边界
- [ ] 未引入 `AGENTS.md` 未声明的中间件
- [ ] 新依赖/中间件引入前已补 ADR，版本在 parent 收口

### 基础项
- [ ] 命名清晰，无魔法数字
- [ ] 无空指针风险
- [ ] 日志合理（关键路径有日志，不刷屏）
- [ ] 无调试残留

## Common Mistakes

- **跨库直连**：服务 A 直接查服务 B 的表，破坏服务自治。
- **五模块边界混淆**：业务写进 app、DB 写进 service、实现写进 client。
- **Feign 不配 fallback**：下游故障导致上游雪崩。
- **分布式事务边界错误**：`@GlobalTransactional` 加错位置或漏建 undo_log。
- **鉴权下沉**：每个服务都做一遍 JWT 校验，漏配即漏洞。
- **放过并发隐患**：`if (stock >= qty) update` 并发下超卖。
- **不查中间件清单**：顺手引入未声明的中间件。
