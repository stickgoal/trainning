# Sentinel 限流熔断技能

## When To Use

配置限流/熔断/降级规则、保护高并发入口、排查流控问题时。

## Rules

- **高并发入口必配限流**：下单、秒杀、热点查询等入口配流控规则。
- **规则动态下发**：流控/熔断规则尽量放 Nacos 配置中心动态管理，少用硬编码 `FlowRule`。
- **降级与 Feign fallback 协同**：Sentinel 熔断触发后走 Feign fallback 兜底，返回可读结果。
- **资源名统一**：`@SentinelResource` 的 value 与规则里的 resource 一致，避免配了不生效。
- **合理阈值**：QPS/线程数阈值基于压测定，不拍脑袋；过高形同虚设，过低误伤正常流量。
- **降级逻辑不抛裸异常**：降级返回兜底 `Result`，记录日志便于定位。

## Examples

接口限流注解：

```java
@SentinelResource(value = "createOrder", blockHandler = "createOrderBlock")
public Result<OrderVO> createOrder(OrderCreateRequest req) {
    // 正常逻辑
}

public Result<OrderVO> createOrderBlock(OrderCreateRequest req, BlockException e) {
    return Result.fail("系统繁忙，请稍后重试");
}
```

## Common Mistakes

- **资源名不一致**：注解 value 与规则 resource 写错，流控不生效。
- **硬编码规则**：流控规则写死在代码里，改阈值要发版。放配置中心动态管理。
- **降级抛异常**：blockHandler 里抛异常，前端拿到 500 而非友好提示。
- **阈值拍脑袋**：没压测就定 QPS，要么误伤要么失效。
- **忽略熔断后的恢复**：只熔断不配置半开恢复，服务一直处于降级态。
- **所有接口无差别限流**：对低频管理接口也限流，浪费资源还影响运维。
