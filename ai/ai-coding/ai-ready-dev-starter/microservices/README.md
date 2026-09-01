# {{项目名}}

<!-- 一句话介绍项目：是什么、解决什么问题、目标用户。由 AI 按实际项目填写，不预设业务。 -->

## 技术栈

**后端**：Java 17 · Spring Boot 3.5 · Spring Cloud 2025.0.x · Spring Cloud Alibaba 2025.0.0.0 · MyBatis-Plus 3.5 · MySQL 8.0 · JUnit 5
**微服务治理**：Nacos（注册+配置）· OpenFeign · LoadBalancer · Gateway · Sentinel · Seata · Zipkin
**前端**：Vue 3 · Element Plus · Vue Router · Vite
**构建**：Maven 多模块（后端）、npm（前端）

## 快速开始

> 开工前先确认项目名与业务服务清单（见 `AGENTS.md` 第 0 节）。

### 依赖中间件

```bash
# Nacos（注册 + 配置）、MySQL（各服务独立库）
# 可选：Seata、Zipkin、Redis、RocketMQ（配置见 deploy/）
```

### 后端（多模块，先启服务后启网关）

```bash
cd {{项目名}}
mvn compile
mvn -pl {{服务名}}/{{服务名}}-app spring-boot:run
mvn -pl gateway/gateway-app spring-boot:run     # 最后启动网关，默认 :8080
```

### 前端

```bash
cd {{项目名}}-front
npm install
npm run dev                  # 默认 :5173
```

## 文档使用方式

本项目文档已与源码分离，全部放在 `docs/` 目录。除根目录 `README.md`、`CHANGELOG.md`、`AGENTS.md` 三个文件外，其余文档一律在 `docs/` 下。

### 文档导航

| 文件 | 作用 | 何时读 |
|------|------|--------|
| `AGENTS.md` | 项目宪法（怎么做） | AI 每次对话首先读 |
| `docs/product.md` | 产品说明（是什么） | 了解业务背景时 |
| `docs/architecture.md` | 架构说明（怎么组织） | 了解架构与服务拆分时 |
| `docs/development.md` | 开发指南（怎么跑） | 搭建环境、启动、构建时 |
| `docs/verification.md` | 质量保障（怎么验证） | 写测试、评审、交付前 |
| `docs/bugfix-log.md` | bug 修复记录（怎么不再犯） | 修复 bug 前后必读 |
| `docs/decisions/` | 架构决策（为什么） | 质疑或改设计前 |
| `docs/tasks/` | 任务卡库 | 拆需求时 |
| `docs/plans/` | 计划库 | 复杂改动动手前 |
| `docs/skills/` | 技能库 | 写代码、评审时 |

### 给 AI 编码工具的阅读顺序

1. 先读 `AGENTS.md`（宪法，必读），确认第 0 节的项目名与服务清单。
2. 再按任务类型读 `docs/` 对应文档。
3. 写代码前读 `docs/skills/` 对应技能。
4. 修复 bug 前先读 `docs/bugfix-log.md`，修复后登记。

## 变更记录

迭代里程碑与 bug 修复摘要见 `CHANGELOG.md`，bug 详细记录见 `docs/bugfix-log.md`。
