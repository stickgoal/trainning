# API 设计技能（语言无关）

## When To Use

新增或修改对外 API（路由/路径/请求响应结构）时。

## Rules

- **统一前缀**：所有对外接口路径统一前缀（如 `/api`）。
- **资源命名用复数名词**：`/api/products`、`/api/orders`，不用动词路径如 `/api/createOrder`。
- **HTTP 动词语义**：`GET` 查询、`POST` 新建、`PUT/PATCH` 更新、`DELETE` 删除。
- **统一响应包装**：成功 `code=0` + `data`；失败 `code != 0` + `message`。业务错误不滥用 HTTP 4xx/5xx（保持 200 + 业务码，或明确约定）。
- **入参用 DTO**：不直接暴露实体；出参用响应对象，不泄露内部字段。
- **错误码集中管理**：业务码集中定义，不在路由层散落字符串。
- **分页**：统一返回 `total / records / current / size`。
- **幂等**：创建类接口考虑幂等键，避免重复提交。

## Examples

<!-- 按实际语言给出示例 -->

```
# 通用示例
GET  /api/products/{id}       查询详情
POST /api/orders              创建订单
PUT  /api/products/{id}       更新商品
```

## Common Mistakes

- **动词路径**：`/api/createOrder`、`/api/getProduct`。改用名词 + HTTP 动词。
- **泄露实体**：直接返回内部实体，暴露审计字段。用响应对象隔离。
- **业务错误滥用 HTTP 状态**：库存不足返回 500，让前端误判系统故障。用业务码区分。
- **错误码散落**：在路由层写死错误码。集中管理。
- **缺幂等**：创建接口无幂等，网络重试导致重复创建。
- **路径前缀不统一**：有的 `/api/xxx` 有的 `/xxx`。统一。
