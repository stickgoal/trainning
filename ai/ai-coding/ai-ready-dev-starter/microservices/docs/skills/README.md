# 技能库 · `docs/skills/`

本目录沉淀反复出现的模式、最佳实践与易错点，避免重复踩坑。

## 结构

按主题分目录，每个主题一个 `SKILL.md`，统一四段式：

```
## When To Use      何时用本技能
## Rules            规则
## Examples         示例
## Common Mistakes  常见错误
```

## 回写规则

- 每次评审或排错中发现"本应早知道"的点，回写进对应技能的 Common Mistakes。
- 引入新的稳定模式时，回写进 Examples。
- 技能之间不重叠，重叠的合并。

## 当前技能

| 技能 | 主题 |
|------|------|
| [`springcloud/`](./springcloud/SKILL.md) | 微服务基础：Nacos 注册/配置、服务治理 |
| [`openfeign/`](./openfeign/SKILL.md) | 跨服务调用与降级 |
| [`distributed-transaction/`](./distributed-transaction/SKILL.md) | Seata 分布式事务 |
| [`gateway/`](./gateway/SKILL.md) | 网关路由、鉴权、限流 |
| [`sentinel/`](./sentinel/SKILL.md) | 限流熔断降级 |
| [`database/`](./database/SKILL.md) | 数据库设计与 SQL 规范 |
| [`api-design/`](./api-design/SKILL.md) | RESTful API 设计 |
| [`testing/`](./testing/SKILL.md) | 测试写法与覆盖策略 |
| [`code-review/`](./code-review/SKILL.md) | 项目专属 Code Review 检查清单 |
| [`code-reviewer/`](./code-reviewer/SKILL.md) | Spring Boot 3.5 通用深度代码审查 |
| [`alibaba-java-guidelines/`](./alibaba-java-guidelines/SKILL.md) | 阿里巴巴 Java 开发规范 |
| [`effective-java-core/`](./effective-java-core/SKILL.md) | Effective Java 核心最佳实践 |
| [`effective-java-concurrency/`](./effective-java-concurrency/SKILL.md) | Effective Java 并发最佳实践 |
| [`web-design-guidelines/`](./web-design-guidelines/SKILL.md) | Web 界面设计规范（前端审查） |
| [`create-project/`](./create-project/SKILL.md) | 从空文件夹按文档自动搭建微服务骨架 |
