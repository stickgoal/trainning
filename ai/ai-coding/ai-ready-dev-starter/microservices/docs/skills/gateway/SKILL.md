# 网关技能（Spring Cloud Gateway）

## When To Use

配置路由、网关鉴权、限流，排查网关 404/502/鉴权失败时。

## Rules

- **统一入口**：前端只访问网关，不直连各服务。
- **路由用服务名**：路由 `uri: lb://service-name`（通过 Nacos 发现），不写死 IP:端口。
- **路径规则**：对外 `/api/xxx/**` 路由到对应服务；路由名与 Nacos 注册名一致。
- **鉴权上移**：JWT 校验在网关全局过滤器做，校验通过后把用户信息透传（Header）给下游，下游信任即可。
- **限流**：网关层用 Sentinel（或 Redis 限流器）对高并发入口限流。
- **统一响应**：未授权、限流、熔断时返回统一 `Result` 格式，不暴露框架默认错误页。

## Examples

路由配置（application.yml）：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: service-order
          uri: lb://service-order
          predicates:
            - Path=/api/orders/**
        - id: service-product
          uri: lb://service-product
          predicates:
            - Path=/api/products/**
```

## Common Mistakes

- **路由名与注册名不一致**：`uri` 里的服务名写错，网关 404/503。
- **前端直连服务绕过网关**：鉴权、限流形同虚设。前端只配网关地址。
- **鉴权下沉到每个服务**：重复校验，且漏配一个服务就出现安全漏洞。统一在网关做。
- **限流/熔断返回裸错误页**：前端拿到 HTML 报错。统一返回 `Result` JSON。
- **路由顺序问题**：通配路由放在精确路由前，导致精确路由被拦截。注意 predicates 匹配顺序。
