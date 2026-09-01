# <!-- 项目名 -->

<!-- 一句话介绍项目：是什么、解决什么问题、目标用户 -->

## 技术栈

<!-- 简列技术栈，详见 docs/architecture.md -->

## 快速开始

```bash
# 后端
<!-- 启动命令 -->

# 前端
<!-- 启动命令 -->
```

<!-- 前置依赖、默认端口、默认账号等 -->

## 文档使用方式

本项目文档已与源码分离，全部放在 `docs/` 目录。除根目录 `README.md`、`CHANGELOG.md`、`AGENTS.md` 三个文件外，其余文档一律在 `docs/` 下。

### 文档导航

| 文件 | 作用 | 何时读 |
|------|------|--------|
| `AGENTS.md` | 项目宪法（怎么做） | AI 每次对话首先读 |
| `docs/product.md` | 产品说明（是什么） | 了解业务背景时 |
| `docs/architecture.md` | 架构说明（怎么组织） | 了解架构与模块时 |
| `docs/development.md` | 开发指南（怎么跑） | 搭建环境、启动、构建时 |
| `docs/verification.md` | 质量保障（怎么验证） | 写测试、评审、交付前 |
| `docs/bugfix-log.md` | bug 修复记录（怎么不再犯） | 修复 bug 前后必读 |
| `docs/decisions/` | 架构决策（为什么） | 质疑或改设计前 |
| `docs/tasks/` | 任务卡库 | 拆需求时 |
| `docs/plans/` | 计划库 | 复杂改动动手前 |
| `docs/skills/` | 技能库 | 写代码、评审时 |

### 给 AI 编码工具的阅读顺序

1. 先读 `AGENTS.md`（宪法，必读）。
2. 再按任务类型读 `docs/` 对应文档：改业务读 `product.md` + `architecture.md`，改环境读 `development.md`，写测试/交付读 `verification.md`。
3. 写代码前读 `docs/skills/` 对应技能。
4. 修复 bug 前先读 `docs/bugfix-log.md`，修复后登记。

## 变更记录

迭代里程碑与 bug 修复摘要见 `CHANGELOG.md`，bug 详细记录见 `docs/bugfix-log.md`。
