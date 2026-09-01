# API 设计技能

## When To Use

新增或修改对外 API（Controller / 路径 / 请求响应结构）时。

## Rules

- **路径前缀 `/api`**：所有对外接口路径以 `/api` 开头。
- **资源命名用复数名词**：`/api/products`、`/api/orders`，不用动词路径如 `/api/createOrder`。
- **HTTP 动词语义**：`GET` 查询、`POST` 新建、`PUT/PATCH` 更新、`DELETE` 删除。
- **统一 `Result<T>` 包装**：成功 `code=0` + `data`；失败 `code != 0` + `message`，HTTP 状态保持 200（业务错误不滥用 4xx/5xx）。
- **入参用 DTO**：`dto/request/*`，不直接暴露 Entity；出参用 `dto/response/*`，不泄露内部字段。
- **错误码集中管理**：业务码在 `common/result/ResultCode`，不在 Controller 里散落字符串。
- **分页**：用 MyBatis-Plus `Page<T>`，返回 `total / records / current / size`。
- **幂等**：创建类接口考虑幂等键，避免重复提交。

## Examples

商品详情接口：

```java
@GetMapping("/products/{spuId}")
public Result<ProductVO> getProduct(@PathVariable Long spuId) {
    return Result.success(productService.getProduct(spuId));
}
```

下单接口：

```java
@PostMapping("/orders")
public Result<OrderVO> createOrder(@RequestBody @Valid OrderCreateRequest req) {
    return Result.success(orderService.createOrder(req));
}
```

## Common Mistakes

- **动词路径**：`/api/createOrder`、`/api/getProduct`。改用名词 + HTTP 动词。
- **泄露 Entity**：直接返回实体，暴露 `deleted`、内部审计字段。用 VO 隔离。
- **业务错误滥用 HTTP 状态**：库存不足返回 500，让前端误判系统故障。用 200 + 业务码。
- **错误码散落**：在 Controller 里写死错误码。集中到 `ResultCode`。
- **缺幂等**：下单接口无幂等，网络重试导致重复下单。
- **路径不统一前缀**：有的 `/api/xxx`，有的 `/xxx`。统一 `/api`。
