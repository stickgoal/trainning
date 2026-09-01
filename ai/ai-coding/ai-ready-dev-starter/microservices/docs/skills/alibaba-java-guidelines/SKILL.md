# 阿里巴巴 Java 开发规范技能

## When To Use

创建、编辑、评审或解释 Java / JVM 后端代码时：Java、Spring / Spring Boot、MyBatis、Maven POM、SQL/XML mapper、MySQL DDL/DML、单元测试、异常处理、日志、安全敏感接口、分层 Service/DAO/Controller 代码。

## Rules（强制规则，违反即缺陷）

### 命名与结构
- 命名不得以 `_` 或 `$` 开头或结尾；不用拼音、中文标识符、随意缩写。
- 类名 `UpperCamelCase`；方法/参数/字段/局部变量 `lowerCamelCase`；常量 `UPPER_UNDERSCORE`。
- 抽象类以 `Abstract`/`Base` 开头；异常以 `Exception` 结尾；测试类以 `Test` 结尾。
- POJO 的 Boolean 属性不得以 `is` 开头（数据库布尔字段用 `is_xxx` 并显式映射）。
- Service/DAO 暴露接口时，实现类用 `Impl` 结尾。
- 数据对象遵循 `DO`/`DTO`/`BO`/`VO`/`Query` 约定，不命名为 `XxxPOJO`。

### 常量与格式
- 禁止魔法值，常量就近按作用域分类定义。
- long 字面量用大写 `L`。
- 缩进 4 空格，行宽 ≤ 120；`if/else/for/while/do` 一律加大括号。
- 关键字后、二元/三元运算符两侧、逗号后加空格。

### OOP 与空安全
- 覆盖方法加 `@Override`；废弃 API 用 `@Deprecated` 并指向替代。
- `equals` 用常量或已知非空对象调用，或用 `Objects.equals`；包装类型比较用 `equals` 不用 `==`。
- POJO 字段用包装类型，RPC 参数/返回值用包装类型，局部变量优先基本类型。
- POJO 字段不设默认值；构造函数不含业务逻辑；getter/setter 不含业务逻辑。
- 访问修饰符尽量窄；工具类构造函数私有。

### 集合与并发
- 重写 `equals` 必须同时重写 `hashCode`。
- 集合转数组用 `toArray(new T[list.size()])`；增强 for 循环中不得修改集合。
- 已知大小时指定集合初始容量；遍历 Map 用 `entrySet` 而非 `keySet`+get。
- 线程用显式 `ThreadPoolExecutor` 创建，不用 `Executors` 工厂方法；线程/线程池要有意义的名字。
- `SimpleDateFormat` 不得共享不安全使用；用 `java.time` 或 ThreadLocal。
- 并发更新同一记录用乐观锁/悲观锁。

### 异常与日志
- 不捕获异常用于正常流程控制；不 catch 后忽略（必须处理/日志/包装/重抛）。
- 事务代码中捕获异常后仍要保证回滚。
- 资源用 `try-with-resources` 关闭；`finally` 中不得 return。
- 业务错误抛自定义异常，不抛裸 `RuntimeException`/`Exception`。
- 日志用 SLF4J 占位符，不用 `System.out`；`error` 留给系统/逻辑失败，`warn` 给用户输入错误。

### 单元测试
- 测试自动、独立、可重复；用断言验证，不用 `System.out`。
- 测试不依赖执行顺序；粒度到方法/类级，不依赖外部网络/服务/中间件。
- 覆盖边界、正确、错误场景；DB 测试程序化准备数据 + 回滚。

### 安全
- 用户自有资源操作必须做授权校验；敏感数据展示前脱敏。
- SQL 参数绑定（`#{}`），禁止拼接用户输入；外部输入参数必须校验。
- 短信/邮件/订单/支付等平台资源加防刷、限频、疲劳控制。

### MySQL 与 ORM
- 布尔字段 `is_xxx`（unsigned tinyint）；非负数值字段 unsigned。
- 表名字段名小写字母数字下划线，不数字开头，避开保留字；表名单数名词。
- 索引名 `pk_`/`uk_`/`idx_` 前缀；金额用 `decimal`，不用 `float`/`double`。
- 业务唯一字段加唯一索引；JOIN 不超过三表，关联字段同类型同索引。
- 不用外键、级联、存储过程（分布式/高并发场景）。
- 显式列名，不用 `select *`；MyBatis 用 `#{}` 不用 `${}` 传用户值；更新记录同时更新 `gmt_modified`。

### 分层与 Maven
- Controller 路由与基本校验、Service 业务逻辑、Manager 复用编排、DAO 数据访问，分层清晰。
- Maven 版本 `major.minor.patch`；线上不依赖 `SNAPSHOT`（安全修复除外）；依赖版本统一 `dependencyManagement` 管理。

## Review Gate（出现以下任一高危项必须修复）

- SQL 注入风险、不安全 HTML 输出、用户自有资源缺鉴权、外部输入缺校验。
- `Executors` 工厂线程池、匿名线程、`SimpleDateFormat` 不安全共享、增强 for 中修改集合。
- catch 忽略异常、异常当正常流程、`finally` 中 return、事务异常捕获后不回滚。
- MyBatis `${}` 传用户值、`select *`、更新漏 `gmt_modified`、Boolean 属性以 `is` 开头。
- `if/else/for/while/do` 缺大括号、漏 `@Override`、包装类型用 `==` 比较。

## Common Mistakes

- **魔法值散落**：状态码、阈值写死在业务代码。集中为常量/枚举。
- **先查后改并发漏洞**：`if (stock >= qty) update` 并发超卖。用条件更新。
- **catch 吞异常**：`catch (Exception e) {}` 掩盖问题。处理/日志/包装/重抛四选一。
- **`System.out` 打日志**：用 SLF4J。
- **`Executors.newFixedThreadPool`**：OOM 风险。用显式 `ThreadPoolExecutor`。
- **Boolean 属性 `isXxx`**：序列化（Jackson/Fastjson）出问题。字段改 `xxx`，数据库 `is_xxx` 显式映射。

> 完整规则目录见阿里巴巴《Java 开发手册》，本文为常用强制规则的紧凑检查清单。
