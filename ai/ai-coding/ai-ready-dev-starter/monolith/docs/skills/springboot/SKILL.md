# Spring Boot 开发技能

## When To Use

写或改后端 Spring Boot 代码时：新增 Controller / Service、调整事务边界、引入配置、排查启动或运行期问题。

## Rules

- **Controller 必须是薄层**：只做参数校验、调用 Service、包装 `Result<T>` 返回；业务逻辑一律放 Service。
- **统一返回 `Result<T>`**：所有 API 返回走 `common/result/Result`，不直接返回裸对象或 Map。
- **异常走 `BusinessException`**：业务校验失败抛 `BusinessException`，由 `GlobalExceptionHandler` 统一翻译；不向上抛裸 `RuntimeException`，不吞异常。
- **事务边界清晰**：跨多表写操作（如下单）用 `@Transactional` 在 Service 实现类方法上；只读操作可不加。
- **依赖注入**：构造器注入（字段加 `final` + `@RequiredArgsConstructor`），不用字段注入。
- **配置**：框架配置放 `config/` 包；业务可调项放 `application.yml`，不用硬编码。
- **启动类**：保持精简，需要定时任务时加 `@EnableScheduling`，其余按需。

## Examples

下单事务示例：

```java
@Transactional
public OrderVO createOrder(OrderCreateRequest req) {
    // 1. 校验 SKU
    // 2. 计算总价，校验余额
    // 3. 扣库存（条件更新 ge）
    // 4. 扣余额（条件更新 ge）
    // 5. 写 Order + OrderItem
    return vo;
}
```

## Common Mistakes

- **Controller 写业务逻辑**：把校验、计算塞进 Controller，导致分层污染、测试困难。一律下沉 Service。
- **`@Transactional` 失效**：
  - 自调用（同类内方法 A 调方法 B，B 的 `@Transactional` 不生效）→ 拆到不同 Bean 或用 `AopContext.currentProxy()`。
  - 方法非 public → 注解不生效。
  - 默认只对 `RuntimeException` 回滚；抛受检异常需 `rollbackFor = Exception.class`。
- **吞异常**：`catch` 后不处理、不抛、不记日志。要么处理、要么抛 `BusinessException`、要么记日志后抛。
- **字段注入**：`@Autowired` 在字段上，导致不可变性与测试困难。用构造器注入。
- **硬编码配置**：把端口、账号、阈值写死在代码里。一律进 `application.yml`。
