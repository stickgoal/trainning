# RocketMQ 消息发送示例 - 快速测试指南

## 新增文件说明

本次更新添加了以下文件来演示 RocketMQ 的各种消息发送方式：

### 1. MessageSendService.java
消息发送服务类，封装了6种消息发送方式：
- 同步发送
- 异步发送
- Oneway 发送
- 顺序消息
- 延迟消息
- 事务消息

### 2. MessageTestController.java
测试控制器，为每种消息发送方式提供独立的 REST API 接口

### 3. TransactionListenerImpl.java
事务消息监听器实现，用于处理事务消息的本地事务和状态回查

### 4. LoginController.java (已更新)
更新了原有的登录控制器，集成所有消息发送方式的演示

### 5. ROCKETMQ_EXAMPLES.md
详细的使用文档和说明

---

## 快速测试步骤

### 1. 确保 RocketMQ 已启动

```bash
# 启动 NameServer
mqnamesrv

# 启动 Broker
mqbroker -n localhost:9876
```

### 2. 启动 Spring Boot 应用

```bash
mvn spring-boot:run
```

### 3. 测试各种消息发送方式

#### 方式一：使用登录接口（一次性演示所有方式）

在浏览器访问或使用 curl：
```bash
curl "http://localhost:8080/login?username=test&password=123"
```

这会在一次请求中演示所有6种消息发送方式。

#### 方式二：使用独立测试接口（推荐）

每个接口单独测试一种消息发送方式：

**1. 同步发送**
```bash
curl http://localhost:8080/message/sync
```

**2. 异步发送**
```bash
curl http://localhost:8080/message/async
```

**3. Oneway 发送**
```bash
curl http://localhost:8080/message/oneway
```

**4. 顺序消息**
```bash
# 使用默认 orderKey
curl http://localhost:8080/message/orderly

# 指定 orderKey
curl "http://localhost:8080/message/orderly?orderKey=order_001"
```

**5. 延迟消息**
```bash
# 使用默认延迟级别 3（10秒）
curl http://localhost:8080/message/delay

# 指定延迟级别（1-18）
curl "http://localhost:8080/message/delay?delayLevel=5"
```

延迟级别对照：
- 1 = 1秒
- 2 = 5秒
- 3 = 10秒
- 4 = 30秒
- 5 = 1分钟
- ... 更多见 ROCKETMQ_EXAMPLES.md

**6. 事务消息**
```bash
curl http://localhost:8080/message/transaction
```

### 4. 查看日志

观察应用控制台输出，可以看到消息发送的详细日志：

```
2024-XX-XX XX:XX:XX  INFO --- 同步发送成功：SendResult [sendStatus=SEND_OK, ...]
2024-XX-XX XX:XX:XX  INFO --- 异步发送请求已发出
2024-XX-XX XX:XX:XX  INFO --- 异步发送成功：SendResult [sendStatus=SEND_OK, ...]
2024-XX-XX XX:XX:XX  INFO --- Oneway 发送完成
2024-XX-XX XX:XX:XX  INFO --- 顺序消息发送成功：SendResult [sendStatus=SEND_OK, ...]
2024-XX-XX XX:XX:XX  INFO --- 延迟消息发送成功，延迟级别：3，结果：SendResult [...]
2024-XX-XX XX:XX:XX  INFO --- 事务消息发送完成：...
```

---

## 代码结构

```
sending/
├── LoginController.java          # 原始控制器（集成演示）
├── MessageSendService.java       # 消息发送服务（核心逻辑）
├── MessageTestController.java    # 测试控制器（独立接口）
├── TransactionListenerImpl.java  # 事务监听器
└── UserInfo.java                 # 用户实体类
```

---

## 关键代码示例

### 同步发送
```java
SendResult result = rocketMQTemplate.syncSend("topic-name", message);
```

### 异步发送
```java
rocketMQTemplate.asyncSend("topic-name", message, new SendCallback() {
    @Override
    public void onSuccess(SendResult result) {
        // 处理成功
    }
    @Override
    public void onException(Throwable e) {
        // 处理异常
    }
});
```

### Oneway 发送
```java
rocketMQTemplate.sendOneWay("topic-name", message);
```

### 顺序消息
```java
SendResult result = rocketMQTemplate.syncSendOrderly(
    "order-topic", 
    message, 
    "order_123"  // 业务标识
);
```

### 延迟消息
```java
Message<?> msg = MessageBuilder.withPayload(message).build();
SendResult result = rocketMQTemplate.syncSend(
    "delay-topic", 
    msg, 
    3000,      // 超时时间
    3          // 延迟级别
);
```

### 事务消息
```java
// 发送端
rocketMQTemplate.sendMessageInTransaction(
    "transaction-topic",
    MessageBuilder.withPayload(message).build(),
    businessArg
);

// 需要配合 TransactionListener 实现
@RocketMQTransactionListener
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // 执行本地事务
        return RocketMQLocalTransactionState.COMMIT;
    }
    
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        // 回查事务状态
        return RocketMQLocalTransactionState.COMMIT;
    }
}
```

---

## 注意事项

1. **Topic 需要提前创建**
   - 在 RocketMQ Console 中创建以下 Topic：
     - login-success-topic
     - order-topic
     - delay-topic
     - transaction-topic

2. **确保 RocketMQ 正常运行**
   - NameServer 地址配置正确（默认：localhost:9876）
   - Broker 已启动

3. **事务消息**
   - 必须实现 TransactionListener
   - TransactionListenerImpl 已自动注册（使用 @RocketMQTransactionListener 注解）

4. **顺序消息**
   - 消费者也需要使用顺序消费模式
   - 相同 orderKey 的消息保证顺序

5. **延迟消息**
   - 只支持预设的延迟级别（1-18）
   - 不支持任意时间的延迟

---

## 常见问题

**Q: 消息发送失败？**
- 检查 RocketMQ 是否启动
- 检查 application.yaml 中的 name-server 配置
- 检查 Topic 是否已创建

**Q: 事务消息报错？**
- 确认 TransactionListenerImpl 已被 Spring 扫描到
- 检查是否有多个 TransactionListener 导致冲突

**Q: 延迟消息没有延迟效果？**
- 检查 delayLevel 是否在 1-18 范围内
- 确认 Broker 支持延迟消息功能

---

## 下一步

1. 查看 [ROCKETMQ_EXAMPLES.md](ROCKETMQ_EXAMPLES.md) 获取更详细的文档
2. 创建消费者来接收和处理这些消息
3. 根据实际业务需求调整代码

祝使用愉快！
