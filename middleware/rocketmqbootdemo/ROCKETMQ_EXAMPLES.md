# RocketMQ Spring Boot Starter 消息发送示例

本项目演示了如何使用 RocketMQ Spring Boot Starter 进行各种类型的消息发送。

## 消息发送方式

### 1. 同步发送 (Sync Send)

**特点：**
- 发送方发送消息后，等待 Broker 返回确认结果
- 可靠性高，适用于重要通知场景

**使用场景：**
- 重要通知短信
- 邮件发送
- 关键业务数据同步

**示例代码：**
```java
SendResult sendResult = rocketMQTemplate.syncSend("topic-name", message);
```

**测试接口：**
```
GET http://localhost:8080/message/sync
```

---

### 2. 异步发送 (Async Send)

**特点：**
- 发送方发送消息后立即返回，不等待 Broker 响应
- 通过回调函数处理发送结果
- 响应时间短，吞吐量高

**使用场景：**
- 对响应时间敏感的业务
- 高并发场景
- 日志收集

**示例代码：**
```java
rocketMQTemplate.asyncSend("topic-name", message, new SendCallback() {
    @Override
    public void onSuccess(SendResult sendResult) {
        // 处理成功
    }

    @Override
    public void onException(Throwable e) {
        // 处理异常
    }
});
```

**测试接口：**
```
GET http://localhost:8080/message/async
```

---

### 3. Oneway 发送

**特点：**
- 只负责发送消息，不等待 Broker 响应
- 没有回调函数
- 速度最快，但可靠性最低

**使用场景：**
- 日志收集
- 不重要的数据统计
- 可以容忍丢失的消息

**示例代码：**
```java
rocketMQTemplate.sendOneWay("topic-name", message);
```

**测试接口：**
```
GET http://localhost:8080/message/oneway
```

---

### 4. 顺序消息 (Orderly Message)

**特点：**
- 保证同一业务标识的消息按照发送顺序被消费
- 通过将相同 key 的消息发送到同一队列实现

**使用场景：**
- 订单状态变更（创建→支付→发货→完成）
- 账户余额变动
- 需要严格顺序的业务流程

**示例代码：**
```java
// orderKey 相同的消息会发送到同一队列，保证顺序
SendResult sendResult = rocketMQTemplate.syncSendOrderly(
    "order-topic", 
    message, 
    "order_123"  // 业务标识，如订单ID
);
```

**测试接口：**
```
GET http://localhost:8080/message/orderly?orderKey=order_001
```

**注意事项：**
- 消费者也需要使用顺序消费模式
- 相同 orderKey 的消息会被路由到同一队列
- 不同 orderKey 的消息之间不保证顺序

---

### 5. 定时/延迟消息 (Delay Message)

**特点：**
- 消息发送后，在指定延迟时间后才投递给消费者
- RocketMQ 不支持任意时间的延迟，只支持预设的延迟级别

**延迟级别对照表：**

| 级别 | 延迟时间 | 级别 | 延迟时间 |
|------|----------|------|----------|
| 1    | 1s       | 10   | 6m       |
| 2    | 5s       | 11   | 7m       |
| 3    | 10s      | 12   | 8m       |
| 4    | 30s      | 13   | 9m       |
| 5    | 1m       | 14   | 10m      |
| 6    | 2m       | 15   | 20m      |
| 7    | 3m       | 16   | 30m      |
| 8    | 4m       | 17   | 1h       |
| 9    | 5m       | 18   | 2h       |

**使用场景：**
- 订单超时取消（30分钟后）
- 定时提醒
- 延迟任务处理

**示例代码：**
```java
Message<UserInfo> message = MessageBuilder.withPayload(userInfo).build();
SendResult sendResult = rocketMQTemplate.syncSend(
    "delay-topic", 
    message, 
    3000,      // 超时时间（毫秒）
    3          // 延迟级别，3表示10秒后投递
);
```

**测试接口：**
```
GET http://localhost:8080/message/delay?delayLevel=3
```

---

### 6. 事务消息 (Transaction Message)

**特点：**
- 保证本地事务和消息发送的原子性
- 采用两阶段提交机制
- 需要实现 TransactionListener 接口

**使用场景：**
- 分布式事务场景
- 需要保证数据库操作和消息发送一致性
- 跨系统的数据同步

**示例代码：**

发送端：
```java
TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction(
    "transaction-topic",
    MessageBuilder.withPayload(message).build(),
    businessArg  // 业务参数，传递给 TransactionListener
);
```

事务监听器：
```java
@Slf4j
@RocketMQTransactionListener
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {
    
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        try {
            // 执行本地事务（如数据库操作）
            // ...
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        // 回查本地事务状态
        // 根据业务逻辑判断事务是否成功
        return RocketMQLocalTransactionState.COMMIT;
    }
}
```

**事务流程：**
1. 发送 Half 消息到 Broker
2. 执行本地事务
3. 根据本地事务结果提交或回滚消息
4. 如果 Broker 未收到确认，会定期回查事务状态

**测试接口：**
```
GET http://localhost:8080/message/transaction
```

---

## 项目结构

```
src/main/java/me/maiz/middleware/rocketmqbootdemo/sending/
├── LoginController.java          # 原始登录控制器（集成所有发送方式）
├── MessageSendService.java       # 消息发送服务（封装各种发送方式）
├── MessageTestController.java    # 测试控制器（独立接口测试每种方式）
├── TransactionListenerImpl.java  # 事务消息监听器
└── UserInfo.java                 # 用户信息实体类
```

## 使用方法

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 测试各种消息发送方式

#### 方式一：使用登录接口（一次性演示所有方式）
```
GET http://localhost:8080/login?username=test&password=123
```

#### 方式二：使用独立测试接口
```bash
# 同步发送
curl http://localhost:8080/message/sync

# 异步发送
curl http://localhost:8080/message/async

# Oneway 发送
curl http://localhost:8080/message/oneway

# 顺序消息
curl http://localhost:8080/message/orderly?orderKey=order_001

# 延迟消息（延迟级别3 = 10秒）
curl http://localhost:8080/message/delay?delayLevel=3

# 事务消息
curl http://localhost:8080/message/transaction
```

### 3. 查看日志
观察控制台输出，查看消息发送结果。

---

## 配置说明

在 `application.yaml` 中配置 RocketMQ：

```yaml
rocketmq:
  name-server: 127.0.0.1:9876  # NameServer 地址
  producer:
    group: my-producer-group    # 生产者组名
```

---

## 注意事项

1. **确保 RocketMQ 已启动**
   - NameServer 和 Broker 都需要正常运行
   - 默认 NameServer 端口：9876

2. **Topic 需要提前创建**
   - 可以在 RocketMQ Console 中创建
   - 或者启用自动创建 Topic（不推荐生产环境使用）

3. **顺序消息的消费者也要使用顺序消费**
   ```java
   @RocketMQMessageListener(
       topic = "order-topic",
       consumerGroup = "order-consumer-group",
       consumeMode = ConsumeMode.ORDERLY  // 顺序消费模式
   )
   ```

4. **事务消息必须实现 TransactionListener**
   - 否则事务消息无法正常工作

5. **延迟消息的级别只能是 1-18**
   - 不能设置任意时间的延迟

---

## 常见问题

### Q1: 消息发送失败？
- 检查 RocketMQ 是否正常启动
- 检查 NameServer 地址配置是否正确
- 检查 Topic 是否已创建

### Q2: 顺序消息不保证顺序？
- 确认消费者使用了 `ConsumeMode.ORDERLY`
- 确认相同业务标识使用了相同的 orderKey

### Q3: 事务消息一直回查？
- 检查 TransactionListener 的实现
- 确认本地事务是否正确执行并返回状态

### Q4: 延迟消息立即被消费？
- 检查 delayLevel 是否在 1-18 范围内
- 确认 Broker 支持延迟消息功能

---

## 参考资料

- [RocketMQ 官方文档](https://rocketmq.apache.org/)
- [RocketMQ Spring Boot Starter](https://github.com/apache/rocketmq-spring)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
