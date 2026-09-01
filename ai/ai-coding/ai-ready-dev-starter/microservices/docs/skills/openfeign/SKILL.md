# OpenFeign 跨服务调用技能

## When To Use

新增或修改跨服务调用、定义对外 client 接口、配置降级、排查 Feign 调用失败时。

## Rules

- **契约放 client 模块**：对外 Feign 接口定义在被依赖服务的 `-client` 模块，`@FeignClient(name = "服务名")`，用服务名不写死 URL。
- **实现放 app 模块**：被依赖方在 `-app` 模块的 `apiImpl` 包实现 client 接口。
- **依赖方在 infra 引入**：服务 A 调服务 B，在 A 的 `-infra` 模块引入 B 的 `-client` 依赖。
- **路径对齐**：Feign 方法路径与目标 Controller 路径一致，统一 `/api` 前缀。
- **DTO 复用**：跨服务传输的 DTO 放全局 `{{项目名}}-common`，调用方与被调方共用，避免双份定义漂移。
- **必须配降级**：每个 `@FeignClient` 配 `fallback`（或 `fallbackFactory`），下游故障时返回兜底结果。
- **超时配置**：合理设置 connect/read 超时，避免下游慢导致线程堆积。
- **参数传递**：GET 多参数用 `@SpringQueryMap`，POST 用 `@RequestBody` DTO。

## Examples

client 模块定义接口（`order-client` 为例）：

```java
@FeignClient(name = "product", fallback = ProductClientFallback.class)
public interface ProductClient {
    @PostMapping("/api/products/deduct-stock")
    Result<Void> deductStock(@RequestBody DeductStockRequest req);
}
```

infra 模块调用（`order-infra` 引入 `product-client`）：

```java
// order-service 依赖 order-infra，通过 infra 暴露的 Repository/Client 调外部服务
```

降级实现（放 client 或 infra）：

```java
@Component
public class ProductClientFallback implements ProductClient {
    @Override
    public Result<Void> deductStock(DeductStockRequest req) {
        return Result.fail("商品服务不可用，请稍后重试");
    }
}
```

## Common Mistakes

- **不配 fallback**：下游宕机时上游直接报 500，雪崩。
- **DTO 双份定义**：调用方与被调方各写一份同名 DTO，字段漂移。放 `{{项目名}}-common` 共用。
- **Feign 方法路径写错**：与目标 Controller 路径不一致，调用 404。
- **返回裸对象**：Feign 返回类型不统一用 `Result<T>`，下游异常信息丢失。
- **忽略超时**：默认超时过长，下游慢时线程池耗尽。
- **在 service 层直连**：绕过 infra，在 service 直接引入外部 client。依赖应走 infra 收口。
