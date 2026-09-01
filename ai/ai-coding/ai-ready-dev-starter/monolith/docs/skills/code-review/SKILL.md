# Code Review 技能

## When To Use

提交前自查、评审他人代码时。Review 是质量保障的最后一道关。

## Rules（检查清单）

### 分层与结构
- [ ] Controller 是薄层，无业务逻辑
- [ ] 业务逻辑在 Service 实现类
- [ ] 没有反向或跨层调用（Mapper 不调 Service，Entity 不含行为）
- [ ] DTO 与 Entity 分离，不泄露内部字段

### 返回与异常
- [ ] 所有 API 返回 `Result<T>`
- [ ] 业务校验抛 `BusinessException`，无裸异常、无吞异常
- [ ] 错误码集中，无散落字符串

### 事务与并发
- [ ] 跨多表写操作在一个 `@Transactional` 内
- [ ] `@Transactional` 无失效场景（自调用 / 非 public / 受检异常未配 rollbackFor）
- [ ] 扣减类操作用条件更新（`ge`）防超卖，无"先查后改"

### 数据库
- [ ] 字符集 `utf8mb4`，`spring.sql.init.encoding=UTF-8`
- [ ] 状态字段走枚举，无魔法数字
- [ ] 复杂查询走 XML，单表 CRUD 走 BaseMapper
- [ ] 业务数据软删除，无物理删

### API 契约
- [ ] 路径 `/api` 前缀，资源复数名词
- [ ] HTTP 动词语义正确
- [ ] 创建类接口考虑幂等

### 架构边界
- [ ] 未引入微服务治理组件（Nacos / Gateway / Feign / Seata / Sentinel）
- [ ] 可选中间件（Redis / MQ / ES）引入前已补 ADR，配置已版本化（deploy/ 或 application.yml 模板入库）
- [ ] 新依赖/中间件引入前已补 ADR

### 测试
- [ ] 新增/修改 Service 有配套单元测试，覆盖率达标

### 基础项
- [ ] 命名清晰，无魔法数字
- [ ] 无空指针风险（Optional / Objects.requireNonNull / 显式判空）
- [ ] 日志合理（关键路径有日志，不刷屏）
- [ ] 无调试残留（print、注释掉的代码）

## Common Mistakes

- **只看功能不看分层**：功能对但 Controller 塞业务，埋下技术债。分层是硬约束。
- **放过并发隐患**：`if (stock >= qty) update` 在 Review 时看着对，并发下却超卖。扣减必查条件更新。
- **忽略事务失效**：`@Transactional` 加了但自调用，以为有事务实则没有。
- **漏看字符集**：新增表/字段没指定字符集，中文落库乱码。
- **不查中间件清单**：顺手引入微服务治理组件（Nacos/Gateway 等），或引入可选中间件却不补 ADR、不版本化配置。
- **放过调试残留**：`System.out.println`、注释代码块混在提交里。
