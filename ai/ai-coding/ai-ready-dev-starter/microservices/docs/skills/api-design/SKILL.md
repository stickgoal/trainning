# API 设计技能

## When To Use

新增或修改对外 API（Controller / 路径 / 请求响应结构）时。

## Rules

- **路径前缀 `/api`**：所有对外接口路径以 `/api` 开头，经网关路由后保留。
- **资源命名用复数名词**：`/api/products`、`/api/orders`，不用动词路径。
- **HTTP 动词语义**：`GET` 查询、`POST` 新建、`PUT/PATCH` 更新、`DELETE` 删除。
- **统一 `Result<T>` 包装**：成功 `code=0` + `data`；失败 `code != 0` + `message`。
- **入参用 DTO**：不直接暴露 Entity；出参用响应对象，不泄露内部字段。
- **错误码集中管理**：业务码在 common 模块统一管理。
- **分页**：返回 `total / records / current / size`。
- **幂等**：创建类接口考虑幂等键（跨服务重试尤其重要）。

## Examples

下单接口：

```java
@PostMapping("/orders")
public Result<OrderVO> createOrder(@RequestBody @Valid OrderCreateRequest req) {
    return Result.success(orderService.createOrder(req));
}
```

## Common Mistakes

- **动词路径**：`/api/createOrder`。改用名词 + HTTP 动词。
- **泄露 Entity**：直接返回实体，暴露内部字段。用 VO 隔离。
- **业务错误滥用 HTTP 状态**：库存不足返回 500。用 200 + 业务码。
- **错误码散落**：各服务各写一套错误码。集中到 common 模块。
- **缺幂等**：跨服务调用重试导致重复操作。关键创建接口加幂等键。
- **路径不统一前缀**：有的 `/api/xxx`，有的 `/xxx`。统一 `/api`。
