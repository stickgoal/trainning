# 微服务基础技能（Spring Cloud Alibaba）

## When To Use

搭建或修改微服务基础设施、配置 Nacos、规划模块结构、排查服务注册/配置问题时。

## Rules

- **五模块结构**：每个服务统一 `-client / -app / -service / -infra / -common` 五模块，职责与依赖方向见 `docs/architecture.md`，不得随意增删模块。
- **服务注册**：每个服务 `application.yml` 配 `spring.cloud.nacos.discovery.server-addr`，服务名用小写中划线（如 `order`），与网关路由名、Feign 调用名一致。
- **配置管理**：`spring.cloud.nacos.config.server-addr` 配配置中心；公共配置放共享 dataId，服务私有配置放独立 dataId。
- **版本统一收口 parent**：所有依赖版本在 `{{项目名}}-parent` 的 `dependencyManagement` 声明，子模块不重复声明版本号。
- **公共代码分层**：全局公共放 `{{项目名}}-common` 与 `{{项目名}}-spring-boot-starter`，服务私有放本服务 `-common`，不重复造轮子。
- **服务间不共享库**：每服务独立数据库，服务 A 需要服务 B 的数据只能调 B 的 client 接口。
- **端口规划**：各服务独立端口，网关统一对外 8080。

## Examples

服务注册配置：

```yaml
spring:
  application:
    name: order
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml
```

## Common Mistakes

- **服务名不一致**：注册名、网关路由名、Feign 调用名写得不一致，导致 404 或找不到服务。
- **版本号散落**：子模块各自声明依赖版本，升级时漏改。版本统一收口到 parent。
- **公共代码重复造轮子**：每个服务都写一份 `Result`/异常。全局公共放 `{{项目名}}-common`。
- **跨库直连**：服务 A 直接连服务 B 的数据库，破坏服务自治。只能走 client 接口。
- **模块职责混淆**：把业务写进 app、把 DB 访问写进 service、把实现写进 client。严守五模块边界。
- **本地忘了起 Nacos**：服务启动报注册失败，先确认 Nacos 是否运行、地址是否正确。
