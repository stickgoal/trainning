# Seata 分布式事务技能

## When To Use

跨服务写操作需要数据一致性、配置 Seata、排查回滚问题时。

## Rules

- **克制使用**：优先通过业务设计（幂等 + 补偿 + 本地消息）规避分布式事务；确需强一致才用 Seata。
- **AT 模式**：多数场景用 AT，侵入小；发起方 Service 方法加 `@GlobalTransactional`，参与方无需改代码（靠 undo_log 自动补偿）。
- **undo_log 表**：每个参与事务的库都要建 `undo_log` 表，否则回滚失败。
- **发起方定位**：`@GlobalTransactional` 加在事务入口（发起方）的 Service 方法上，不是被调方。
- **单服务事务仍用 `@Transactional`**：服务内多表写仍走本地事务，跨服务才用全局事务。
- **回滚边界**：只有抛出异常才触发全局回滚；吞异常会导致不完整回滚。

## Examples

下单跨服务事务（订单服务发起，调商品扣库存 + 用户扣余额）：

```java
@GlobalTransactional   // 发起方：全局事务
public OrderVO createOrder(OrderCreateRequest req) {
    productClient.deductStock(req);   // Feign 调商品服务
    userClient.deductBalance(req);    // Feign 调用户服务
    // 写订单 + 订单项（本地 @Transactional）
    return vo;
}
```

## Common Mistakes

- **`@GlobalTransactional` 加错位置**：加在被调方或非入口方法，事务边界错误。
- **漏建 undo_log**：回滚时报错"undo_log 不存在"。
- **吞异常导致不回滚**：catch 异常后不抛，全局事务误判成功，数据不一致。
- **滥用分布式事务**：本来用补偿就能解决，硬上 Seata 增加锁与性能开销。
- **混用本地与全局事务边界不清**：把服务内多表写也塞进全局事务，扩大锁范围。
- **忽略 Seata 与数据源代理**：未配置 Seata 数据源代理，`@GlobalTransactional` 不生效。
