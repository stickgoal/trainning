# 开发指南

## 1. 开发环境

| 依赖 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | 后端运行与编译 |
| Maven | 3.9+ | 后端多模块构建 |
| MySQL | 8.0 | 各服务独立库 |
| Nacos | 2.x | 注册中心 + 配置中心 |
| Seata | 2.x | 分布式事务（可选，按需启动） |
| Zipkin | 2.x | 链路追踪（可选） |
| Redis | 7.x | 缓存/锁（可选） |
| RocketMQ | 5.x | 消息队列（可选） |
| Node.js | 18+ | 前端运行与构建 |

## 2. 启动方式

### 2.1 依赖中间件

```bash
# Nacos（注册 + 配置）、MySQL（各服务独立库）
# 可选：Seata、Zipkin、Redis、RocketMQ
```

> 中间件相关配置（docker-compose、初始化脚本、连接参数）**必须版本化存放在项目仓库**，见第 7 节。

### 2.2 后端（多模块，先启服务后启网关）

```bash
cd {{项目名}}

# 编译全部模块
mvn compile

# 启动各业务服务（分终端执行）
mvn -pl {{服务名1}}/{{服务名1}}-app spring-boot:run
mvn -pl {{服务名2}}/{{服务名2}}-app spring-boot:run

# 最后启动网关
mvn -pl gateway/gateway-app spring-boot:run
```

- 服务端口：各服务独立端口，网关统一对外 8080。
- 注册与配置：`spring.cloud.nacos.*` 指向本地 Nacos。
- 数据库：各服务 `application.yml` 指向各自独立库。

### 2.3 前端

```bash
cd {{项目名}}-front
npm install
npm run dev                  # 默认 :5173
```

## 3. 测试方式

```bash
cd {{项目名}}
mvn test                 # 全模块测试
mvn -pl {{服务名}} test  # 单服务测试
```

- 单元测试：mock Mapper / Feign 客户端。
- 集成测试：`@SpringBootTest`；写数据加 `@Transactional` 或 `@GlobalTransactional`。
- 跨服务测试：用 mock 或 Testcontainers 起 Nacos/MySQL。

## 4. 构建方式

```bash
cd {{项目名}} && mvn clean package -DskipTests   # 各服务打独立 jar
cd {{项目名}}-front && npm run build              # 前端产物 dist/
```

## 5. Git 规范

- 提交信息 `type: 简述`：`feat/fix/refactor/docs/test/chore`。
- 分支：主干开发，特性 `feature/xxx`，修复 `fix/xxx`，合并前通过测试。
- 公开 API、表结构、枚举、服务拆分边界的变更需先沟通并在 `docs/decisions/` 记录。

## 6. 常见问题

- **服务注册不上**：Nacos 未启动，或 `server-addr` 配置错误。
- **Feign 调用 404/超时**：目标服务未启动或未注册；检查 fallback 与超时配置。
- **Seata 回滚失败**：undo_log 表未建，或 `@GlobalTransactional` 未加在发起方。
- **中文乱码**：`spring.sql.init.encoding=UTF-8`，库字符集 `utf8mb4`。
- **网关 404**：路由配置的服务名与注册名不一致。

## 7. 中间件配置版本化

所有中间件（Nacos / Seata / Redis / RocketMQ / ES 等）的配置必须随项目版本化，不得只存在于开发者本机：

```
{{项目名}}/
├── deploy/                   部署与中间件配置（版本化）
│   ├── docker-compose.yml    MySQL / Nacos / Redis / RocketMQ / Seata 等容器编排
│   ├── nacos/                Nacos 初始化配置
│   └── init-sql/             各库初始化脚本
└── 各服务的 application.yml   连接参数（本地值可进 gitignore 的 profile，但模板必须入库）
```

- 连接账号/密码等敏感信息走环境变量或本地 profile（`application-local.yml` 不入库），但**模板与占位符必须入库**。
- 新增中间件依赖统一在 `{{项目名}}-parent` 的 `dependencyManagement` 声明版本，服务模块不重复声明版本号。
