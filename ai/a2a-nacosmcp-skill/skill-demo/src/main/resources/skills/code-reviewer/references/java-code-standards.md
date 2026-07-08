# Java 编码规范参考

## 命名规范
- 类名：大驼峰（PascalCase），如 `UserService`
- 方法名：小驼峰（camelCase），如 `getUserById`
- 常量：全大写+下划线，如 `MAX_RETRY_COUNT`
- 包名：全小写，如 `com.example.service`

## 注释规范
- 公共 API 必须有 Javadoc
- 复杂逻辑需要行内注释
- 不要用注释解释"做什么"，要解释"为什么"

## 异常处理
- 不要捕获异常后不做任何处理
- 不要使用 `printStackTrace()`，使用日志框架
- 业务异常使用自定义异常类

## 安全规范
- 禁止在代码中硬编码密码、密钥
- 用户输入必须做校验和转义
- SQL 查询使用参数化查询，禁止拼接字符串

## 性能规范
- 循环中避免创建对象
- 使用 StringBuilder 拼接大量字符串
- 数据库查询注意 N+1 问题