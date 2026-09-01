# 数据库技能

## When To Use

设计表结构、写 SQL / Mapper、排查数据一致性或并发问题时。

## Rules

- **每服务独立库**：服务间不共享表，跨服务数据只能走 API，不跨库 JOIN。
- **字符集统一 `utf8mb4`**：库、表、连接均 `utf8mb4`；`spring.sql.init.encoding=UTF-8` 必须配置。
- **主键**：`bigint` 自增（`@TableId(type = IdType.AUTO)`），不用 UUID 做主键。
- **时间字段**：`create_time` / `update_time` 用 `datetime`，字段填充自动维护。
- **状态字段**：用枚举，不散落魔法数字。
- **并发扣减用条件更新**：`update ... where field >= qty`，返回影响行数判断成功。
- **Seata 参与库建 `undo_log`**：参与分布式事务的库都要建 undo_log 表，否则回滚失败。
- **复杂查询走 XML**：多表 JOIN、批量条件写 XML，单表 CRUD 走 BaseMapper。

## Examples

扣库存防超卖（条件更新）：

```java
int affected = skuMapper.update(null,
    new LambdaUpdateWrapper<Sku>()
        .eq(Sku::getId, skuId)
        .ge(Sku::getStock, qty)
        .setSql("stock = stock - " + qty));
if (affected == 0) throw new BusinessException("库存不足");
```

## Common Mistakes

- **先查后改的并发漏洞**：`if (stock >= qty) update` 并发下超卖。必须条件更新。
- **跨库 JOIN**：服务 A 直接 JOIN 服务 B 的表。应通过 API 获取数据在应用层组装。
- **漏建 undo_log**：Seata 回滚报错"undo_log 不存在"。
- **忽略字符集导致乱码**：中文落库变乱码。链路三处都要 utf8mb4/UTF-8。
- **物理删除业务数据**：用 delete 丢失审计。一律软删除。
- **N+1 查询**：循环里逐条查关联数据。用 JOIN 或批量查询。
