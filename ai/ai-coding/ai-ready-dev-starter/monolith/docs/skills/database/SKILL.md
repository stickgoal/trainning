# 数据库技能

## When To Use

设计表结构、写 SQL / Mapper、排查数据一致性或并发问题时。

## Rules

- **字符集统一 `utf8mb4`**：库、表、连接均 `utf8mb4`；`spring.sql.init.encoding=UTF-8` 必须配置，否则初始化 SQL 中文乱码。
- **主键**：`bigint` 自增（`@TableId(type = IdType.AUTO)`），不用 UUID 做主键（索引体积大）。
- **时间字段**：`create_time` / `update_time` 用 `datetime`，MyBatis-Plus 字段填充自动维护。
- **状态字段**：用枚举（`common/enums`），不在表里散落魔法数字。
- **并发扣减用条件更新**：扣库存/余额用 `update ... where field >= qty`，返回影响行数判断成功，不"先查后改"。
- **索引**：高频查询字段（如 `buyer_id`、`status`、`create_time`）建索引；不在低基数字段（如布尔）建索引。
- **表名前缀**：按域前缀区分，便于后续按域拆分。
- **复杂查询走 XML**：多表 JOIN、批量条件写 `resources/mapper/*.xml`，单表 CRUD 走 `BaseMapper`。

## Examples

扣库存防超卖（条件更新）：

```java
int affected = skuMapper.update(null,
    new LambdaUpdateWrapper<Sku>()
        .eq(Sku::getId, skuId)
        .ge(Sku::getStock, qty)                 // 库存 >= 购买量 才更新
        .setSql("stock = stock - " + qty));
if (affected == 0) throw new BusinessException("库存不足");
```

## Common Mistakes

- **先查后改的并发漏洞**：`if (stock >= qty) update` 在并发下会超卖。必须用条件更新一步到位。
- **忽略字符集导致乱码**：库 utf8 但连接或 `spring.sql.init.encoding` 缺失，中文落库变乱码。链路三处都要 utf8mb4/UTF-8。
- **物理删除业务数据**：用 `delete` 清业务数据，丢失审计与历史。一律软删除。
- **魔法数字状态值**：表里存 `1/2/3` 而无枚举映射。状态走枚举。
- **在低基数字段建索引**：如 `deleted` 只有 0/1，索引选择率低，效果差。
- **N+1 查询**：循环里逐条查关联数据。用 JOIN 或批量查询。
