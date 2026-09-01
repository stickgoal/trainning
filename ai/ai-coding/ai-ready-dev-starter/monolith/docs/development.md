# 开发指南

## 1. 开发环境

| 依赖 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | 后端运行与编译 |
| Maven | 3.9+ | 后端构建 |
| MySQL | 8.0 | 主数据库，默认账号 `root` / `root123` |
| Node.js | 18+ | 前端运行与构建 |
| Redis | 7.x | 可选，缓存/锁 |
| RocketMQ | 5.x | 可选，消息队列 |
| Elasticsearch | 8.x | 可选，搜索 |

## 2. 启动方式

### 2.1 后端

```bash
cd {{项目名}}
mvn spring-boot:run
```

- 默认端口：`8080`
- 数据库：连接串 `jdbc:mysql://localhost:3306/<库名>?...&createDatabaseIfNotExist=true`，首次启动自动建库、建表、初始化数据。
- 初始化数据来源：`src/main/resources/db/schema.sql` 与 `data.sql`。
- 修改数据库账号/密码：`src/main/resources/application.yml` 的 `spring.datasource` 段。

### 2.2 前端

```bash
cd {{项目名}}-front
npm install
npm run dev
```

- 默认端口：`5173`
- 后端地址：`src/api/index.js` 中的 `baseURL`（默认 `http://localhost:8080`）。

## 3. 测试方式

```bash
cd {{项目名}}
mvn test
```

- 测试框架：JUnit 5 + Spring Boot Test。
- 集成测试用 `@SpringBootTest`，写数据测试加 `@Transactional` 防污染。
- 重点覆盖：事务一致性、库存不足回滚、余额不足回滚、非法状态迁移。
- 测试依赖本地 MySQL 可用。

## 4. 构建方式

```bash
# 后端打可执行 jar
cd {{项目名}} && mvn clean package -DskipTests

# 前端打静态产物
cd {{项目名}}-front && npm run build    # 产物在 dist/
```

## 5. Git 规范

- 提交信息 `type: 简述`：`feat/fix/refactor/docs/test/chore`。
- 分支策略：主干开发，特性分支 `feature/xxx`，修复分支 `fix/xxx`，合并前通过测试。
- 公开 API、表结构、枚举的删除/重构需先沟通并在 `docs/decisions/` 记录。

## 6. 常见问题

- **启动报 `Connection refused`**：本地 MySQL 未运行，或账号/密码与 `application.yml` 不符。
- **中文乱码**：确保 `application.yml` 中 `spring.sql.init.encoding=UTF-8`，数据库字符集为 `utf8mb4`。
- **端口被占用**：修改 `application.yml` 的 `server.port` 或关闭占用进程。

## 7. 中间件配置版本化

引入的可选中间件（Redis / RocketMQ / ES 等）配置必须版本化存放：

```
{{项目名}}/
├── deploy/                   部署与中间件配置（版本化）
│   ├── docker-compose.yml    MySQL / Redis / RocketMQ / ES 等容器编排
│   └── init-sql/             初始化脚本
└── src/main/resources/       application.yml 连接参数
```

- 连接账号/密码等敏感信息走环境变量或本地 profile（`application-local.yml` 不入库），但模板与占位符必须入库。
- 新增中间件依赖版本统一在 `pom.xml` 的 `properties` 或 `dependencyManagement` 声明，不散落硬编码版本。
