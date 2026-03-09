# Redisson Demo Project

这是一个演示 Redisson 各种特性的 Spring Boot 项目，展示了 Redis 分布式锁、分布式集合、发布订阅、响应式编程等功能。

## 项目结构

```
redisson-demo/
├── src/main/java/com/example/redisson/
│   ├── RedissonDemoApplication.java          # 主应用类
│   ├── controller/                           # REST 控制器
│   │   ├── RedissonBasicController.java      # 基础操作控制器
│   │   ├── RedissonLockController.java       # 分布式锁控制器
│   │   ├── RedissonCollectionController.java # 分布式集合控制器
│   │   ├── RedissonPubSubController.java     # 发布订阅控制器
│   │   ├── RedissonReactiveController.java  # 响应式编程控制器
│   │   ├── EcommerceLockController.java      # 电商场景锁控制器
│   │   ├── EcommerceCollectionController.java # 电商场景集合控制器
│   │   ├── EcommercePubSubController.java    # 电商场景发布订阅控制器
│   │   ├── EcommerceReactiveController.java  # 电商场景响应式控制器
│   │   └── EcommerceBasicController.java     # 电商场景基础操作控制器
│   ├── service/                              # 服务层
│   │   ├── RedissonBasicService.java         # 基础操作服务
│   │   ├── RedissonLockService.java          # 分布式锁服务
│   │   ├── RedissonCollectionService.java    # 分布式集合服务
│   │   ├── RedissonPubSubService.java        # 发布订阅服务
│   │   ├── RedissonReactiveService.java      # 响应式编程服务
│   │   ├── EcommerceLockService.java          # 电商场景锁服务
│   │   ├── EcommerceCollectionService.java    # 电商场景集合服务
│   │   ├── EcommercePubSubService.java        # 电商场景发布订阅服务
│   │   ├── EcommerceReactiveService.java      # 电商场景响应式服务
│   │   └── EcommerceBasicService.java         # 电商场景基础操作服务
│   └── model/                                # 数据模型
│       ├── Product.java                      # 商品模型
│       └── Order.java                        # 订单模型
├── src/main/resources/
│   └── application.yml                       # 应用配置
└── pom.xml                                   # Maven 配置
```

## 功能特性

### 1. 基础操作 (RedissonBasicService)
- **String 操作**: 设置、获取、带 TTL 的字符串操作
- **Hash 操作**: 字段设置、获取、批量获取
- **List 操作**: 添加、获取、删除列表元素
- **Set 操作**: 添加、获取、删除集合元素
- **Sorted Set 操作**: 有序集合的添加、范围查询
- **Queue 操作**: 队列的入队、出队、大小查询
- **批量操作**: 批量设置键值对
- **工具操作**: 键存在性检查、模式匹配查找

### 2. 分布式锁 (RedissonLockService)
- **基础锁**: `tryLock`, `unlock`, `executeWithLock`
- **读写锁**: `readWithLock`, `writeWithLock`
- **公平锁**: `tryFairLock`
- **多重锁**: `tryMultiLock`
- **锁状态查询**: `isLocked`, `isHeldByCurrentThread`, `getHoldCount`
- **原子计数器**: 带锁的计数器操作

### 2.1 电商场景锁应用 (EcommerceLockService)
- **读写锁场景**: 商品信息查看与更新
- **公平锁场景**: 秒杀抢购（按请求顺序公平处理）
- **多重锁场景**: 跨店购物车结算（原子性锁定多个店铺库存）
- **普通锁场景**: 订单创建（防止重复订单）
- **分布式信号量**: 商品详情页限流控制
- **分布式闭锁**: 聚合支付（等待多个支付渠道响应）

### 2.2 电商场景分布式集合 (EcommerceCollectionService)
- **分布式Map**: 购物车管理（支持TTL过期）
- **分布式Set**: 商品收藏夹（支持交集、并集操作）
- **分布式List**: 商品浏览历史（支持分页查询）
- **分布式Queue**: 订单处理队列（异步处理）
- **优先级队列**: VIP订单优先处理
- **有序集合**: 商品销量排行榜
- **双端队列**: 商品推荐缓存（LRU策略）
- **地理空间**: 附近店铺查询

### 2.3 电商场景发布订阅 (EcommercePubSubService)
- **基础发布订阅**: 订单状态变更通知
- **模式订阅**: 商品价格变动通知
- **广播消息**: 促销活动广播
- **延迟发布**: 库存预警通知
- **实时数据流**: 用户行为流收集
- **可靠消息**: 系统监控事件
- **多房间聊天**: 实时客服聊天

### 2.4 电商场景响应式编程 (EcommerceReactiveService)
- **响应式搜索**: 基于Reactor的非阻塞商品搜索
- **链式操作**: 用户会话管理（多步骤组合）
- **批量操作**: 响应式库存更新
- **背压处理**: 高并发购物车操作
- **组合操作**: 响应式订单处理
- **流式处理**: 实时商品推荐
- **异步处理**: 缓存预热和数据同步
- **限流控制**: 响应式令牌桶算法
- **分布式事务**: 补偿事务机制

### 2.5 电商场景基础操作 (EcommerceBasicService)
- **String操作**: 用户会话管理
- **Hash操作**: 商品信息缓存
- **List操作**: 用户浏览历史
- **Set操作**: 用户标签管理
- **Sorted Set**: 商品评分排行
- **Queue操作**: 消息队列
- **原子操作**: 商品点击量统计
- **位图操作**: 用户签到记录
- **布隆过滤器**: 商品推荐去重
- **HyperLogLog**: 网站UV统计
- **批量操作**: 商品数据批量导入
- **管道操作**: 提升批量操作性能

### 3. 分布式集合 (RedissonCollectionService)
- **RMap**: 分布式 Map 操作，支持 TTL
- **RSet**: 分布式 Set 操作，支持并集、交集
- **RList**: 分布式 List 操作
- **RQueue**: 分布式队列操作
- **RScoredSortedSet**: 分布式有序集合
- **RDeque**: 分布式双端队列

### 4. 发布订阅 (RedissonPubSubService)
- **基础发布订阅**: 消息发布、订阅、取消订阅
- **模式订阅**: 支持模式匹配的订阅
- **广播**: 向多个主题广播消息
- **延迟发布**: 延迟消息发布
- **聊天室**: 模拟聊天室功能

### 5. 响应式编程 (RedissonReactiveService)
- **响应式操作**: 基于 Reactor 的响应式 API
- **链式操作**: 响应式链式调用
- **超时处理**: 响应式超时控制
- **重试机制**: 响应式重试策略
- **背压处理**: 响应式背压控制
- **组合操作**: 响应式组合多个操作

## 环境要求

- Java 17+
- Maven 3.6+
- Redis 6.0+
- Spring Boot 3.2.0

## 快速开始

### 1. 启动 Redis

```bash
# 使用 Docker 启动 Redis
docker run -d --name redis -p 6379:6379 redis:6-alpine

# 或者使用本地 Redis
redis-server
```

### 2. 配置应用

修改 `src/main/resources/application.yml` 中的 Redis 连接配置：

```yaml
spring:
  redis:
    host: localhost      # Redis 主机地址
    port: 6379           # Redis 端口
    password:            # Redis 密码（如果有）
    database: 0          # Redis 数据库
```

### 3. 启动应用

```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## API 使用示例

### 基础操作

#### 设置字符串
```bash
curl -X POST "http://localhost:8080/api/redisson/basic/string?key=test&value=hello"
```

#### 获取字符串
```bash
curl "http://localhost:8080/api/redisson/basic/string?key=test"
```

#### 设置带 TTL 的字符串
```bash
curl -X POST "http://localhost:8080/api/redisson/basic/string/ttl?key=test_ttl&value=hello_ttl&ttl=60&timeUnit=SECONDS"
```

### 分布式锁

#### 尝试获取锁
```bash
curl -X POST "http://localhost:8080/api/redisson/lock/try?lockKey=mylock&waitTime=10&leaseTime=30&timeUnit=SECONDS"
```

#### 释放锁
```bash
curl -X POST "http://localhost:8080/api/redisson/lock/unlock?lockKey=mylock"
```

#### 使用锁执行任务
```bash
curl -X POST "http://localhost:8080/api/redisson/lock/execute?lockKey=mylock&leaseTime=30&timeUnit=SECONDS&taskMessage=Hello World"
```

### 分布式集合

#### Map 操作
```bash
# 添加到 Map
curl -X POST "http://localhost:8080/api/redisson/collection/map?mapKey=mymap&key=name&value=Redisson"

# 从 Map 获取
curl "http://localhost:8080/api/redisson/collection/map?mapKey=mymap&key=name"

# 获取所有 Map 内容
curl "http://localhost:8080/api/redisson/collection/map/all?mapKey=mymap"
```

#### Set 操作
```bash
# 添加到 Set
curl -X POST "http://localhost:8080/api/redisson/collection/set?setKey=myset&value=member1"

# 获取所有 Set 成员
curl "http://localhost:8080/api/redisson/collection/set?setKey=myset"
```

### 发布订阅

#### 订阅主题
```bash
curl -X POST "http://localhost:8080/api/redisson/pubsub/subscribe?topicName=news&subscriberName=user1"
```

#### 发布消息
```bash
curl -X POST "http://localhost:8080/api/redisson/pubsub/publish?topicName=news&message=Hello World"
```

#### 聊天室功能
```bash
# 加入聊天室
curl -X POST "http://localhost:8080/api/redisson/pubsub/chat/join?roomName=general&userName=alice"

# 发送消息
curl -X POST "http://localhost:8080/api/redisson/pubsub/chat/send?roomName=general&userName=alice&message=Hi everyone!"
```

### 电商场景锁应用

#### 读写锁 - 商品信息管理
```bash
# 查看商品信息（读锁，可并发）
curl "http://localhost:8080/api/redisson/ecommerce/product/view?productId=1001"

# 更新商品信息（写锁，独占访问）
curl -X POST "http://localhost:8080/api/redisson/ecommerce/product/update?productId=1001&name=新商品名&price=399.99"
```

#### 公平锁 - 秒杀抢购
```bash
# 重置库存
curl -X POST "http://localhost:8080/api/redisson/ecommerce/stock/reset"

# 公平秒杀（按请求顺序处理）
curl -X POST "http://localhost:8080/api/redisson/ecommerce/seckill/fair?userId=user1&productId=1001"
```

#### 多重锁 - 跨店购物车结算
```bash
# 跨店结算（同时锁定多个店铺库存）
curl -X POST "http://localhost:8080/api/redisson/ecommerce/checkout/multi-store?userId=user1&storeIds=store1,store2,store3&quantities=1,2,1"
```

#### 分布式信号量 - 限流控制
```bash
# 限流查看商品详情（最多10个并发）
curl "http://localhost:8080/api/redisson/ecommerce/product/view-rate-limit?userId=user1&productId=1001"
```

#### 分布式闭锁 - 聚合支付
```bash
# 聚合支付（等待多个支付渠道响应）
curl -X POST "http://localhost:8080/api/redisson/ecommerce/payment/aggregate?orderId=ORD001&paymentChannels=alipay,wechat,bank"
```

#### 订单创建
```bash
# 创建订单（防止重复订单）
curl -X POST "http://localhost:8080/api/redisson/ecommerce/order/create?userId=user1&productId=1001&quantity=2"
```

#### 场景说明
```bash
# 获取各场景详细说明
curl "http://localhost:8080/api/redisson/ecommerce/scenarios/read-write-lock"
curl "http://localhost:8080/api/redisson/ecommerce/scenarios/fair-lock"
curl "http://localhost:8080/api/redisson/ecommerce/scenarios/multi-lock"
curl "http://localhost:8080/api/redisson/ecommerce/scenarios/semaphore"
curl "http://localhost:8080/api/redisson/ecommerce/scenarios/countdown-latch"
```

### 电商场景分布式集合

#### 购物车管理
```bash
# 添加商品到购物车
curl -X POST "http://localhost:8080/api/redisson/ecommerce/collection/cart/add?userId=user1&productId=1001&quantity=2"

# 获取购物车
curl "http://localhost:8080/api/redisson/ecommerce/collection/cart/user1"

# 更新购物车商品数量
curl -X POST "http://localhost:8080/api/redisson/ecommerce/collection/cart/update?userId=user1&productId=1001&quantity=3"
```

#### 收藏夹管理
```bash
# 添加商品到收藏夹
curl -X POST "http://localhost:8080/api/redisson/ecommerce/collection/favorites/add?userId=user1&productId=1002"

# 获取收藏夹
curl "http://localhost:8080/api/redisson/ecommerce/collection/favorites/user1"

# 获取多个用户共同收藏的商品
curl "http://localhost:8080/api/redisson/ecommerce/collection/favorites/common?userIds=user1,user2,user3"
```

#### 商品排行榜
```bash
# 记录商品销量
curl -X POST "http://localhost:8080/api/redisson/ecommerce/collection/ranking/sales?productId=1001&quantity=5"

# 获取销量排行榜
curl "http://localhost:8080/api/redisson/ecommerce/collection/ranking/top?topN=10"
```

#### 地理位置查询
```bash
# 添加店铺位置
curl -X POST "http://localhost:8080/api/redisson/ecommerce/collection/geo/store?storeId=store1&longitude=116.404&latitude=39.915"

# 查找附近店铺
curl "http://localhost:8080/api/redisson/ecommerce/collection/geo/nearby?longitude=116.404&latitude=39.915&radiusKm=5"
```

### 电商场景发布订阅

#### 订单状态通知
```bash
# 发布订单状态变更
curl -X POST "http://localhost:8080/api/redisson/ecommerce/pubsub/order/status?orderId=ORD001&userId=user1&status=PAID"

# 订阅订单状态
curl -X POST "http://localhost:8080/api/redisson/ecommerce/pubsub/order/status/subscribe?userId=user1&subscriberName=client1"
```

#### 价格变动通知
```bash
# 发布价格变动
curl -X POST "http://localhost:8080/api/redisson/ecommerce/pubsub/price/change?productId=1001&oldPrice=299.99&newPrice=259.99"

# 订阅价格变动
curl -X POST "http://localhost:8080/api/redisson/ecommerce/pubsub/price/subscribe?pattern=product:price:*&subscriberName=user1"
```

#### 促销活动广播
```bash
# 广播促销活动
curl -X POST "http://localhost:8080/api/redisson/ecommerce/pubsub/promotion/broadcast?promotionId=PROMO001&title=双11大促&content=全场5折&discount=50%"

# 订阅促销广播
curl -X POST "http://localhost:8080/api/redisson/ecommerce/pubsub/promotion/subscribe?subscriberName=user1"
```

#### 实时聊天客服
```bash
# 加入聊天室
curl -X POST "http://localhost:8080/api/redisson/ecommerce/pubsub/chat/join?roomId=room1&userName=customer1"

# 发送消息
curl -X POST "http://localhost:8080/api/redisson/ecommerce/pubsub/chat/message?roomId=room1&userName=customer1&message=你好，我需要帮助"
```

### 电商场景响应式编程

#### 响应式搜索
```bash
# 响应式商品搜索
curl "http://localhost:8080/api/redisson/ecommerce/reactive/search?keyword=手机"
```

#### 响应式购物车操作
```bash
# 响应式批量购物车操作
curl -X POST "http://localhost:8080/api/redisson/ecommerce/reactive/cart/operations?userId=user1" \
  -H "Content-Type: application/json" \
  -d '["ADD:1001", "ADD:1002", "REMOVE:1003"]'
```

#### 响应式实时推荐
```bash
# 实时推荐流
curl "http://localhost:8080/api/redisson/ecommerce/reactive/recommendations/user1"
```

#### 响应式监控指标
```bash
# 实时监控指标流
curl "http://localhost:8080/api/redisson/ecommerce/reactive/metrics"
```

### 电商场景基础操作

#### 用户会话管理
```bash
# 设置用户会话
curl -X POST "http://localhost:8080/api/redisson/ecommerce/basic/session?userId=user1&sessionId=session123&ttlHours=2"

# 获取用户会话
curl "http://localhost:8080/api/redisson/ecommerce/basic/session/user1"

# 检查用户在线状态
curl "http://localhost:8080/api/redisson/ecommerce/basic/session/user1/online"
```

#### 商品信息缓存
```bash
# 缓存商品信息
curl -X POST "http://localhost:8080/api/redisson/ecommerce/basic/product/cache?productId=1001&name=iPhone15&price=6999&category=手机&stock=100"

# 获取商品信息
curl "http://localhost:8080/api/redisson/ecommerce/basic/product/cache/1001"
```

#### 用户签到
```bash
# 用户签到
curl -X POST "http://localhost:8080/api/redisson/ecommerce/basic/checkin?userId=user1&dayOfYear=60"

# 检查签到状态
curl "http://localhost:8080/api/redisson/ecommerce/basic/checkin/user1/60"

# 获取签到天数
curl "http://localhost:8080/api/redisson/ecommerce/basic/checkin/user1"
```

#### UV统计
```bash
# 记录页面访问
curl -X POST "http://localhost:8080/api/redisson/ecommerce/basic/hll/record?pageId=homepage&userId=user1"

# 获取页面UV
curl "http://localhost:8080/api/redisson/ecommerce/basic/hll/uv/homepage"
```

### 响应式编程

#### 响应式字符串操作
```bash
curl -X POST "http://localhost:8080/api/redisson/reactive/string?key=reactive_key&value=reactive_value"
```

#### 响应式链式操作
```bash
curl -X POST "http://localhost:8080/api/redisson/reactive/chain?key=chain_key"
```

#### 响应式批量操作
```bash
curl -X POST "http://localhost:8080/api/redisson/reactive/batch" \
  -H "Content-Type: application/json" \
  -d '{"key1": "value1", "key2": "value2", "key3": "value3"}'
```

## 测试建议

### 1. 基础功能测试
- 测试各种数据结构的基本 CRUD 操作
- 验证 TTL 功能是否正常工作
- 测试批量操作的性能

### 2. 分布式锁测试
- 在多线程环境下测试锁的互斥性
- 测试锁的超时和自动释放
- 验证读写锁的并发访问控制
- **电商场景测试**：
  - 测试读写锁：并发查看商品 vs 管理员更新商品
  - 测试公平锁：秒杀场景下的公平性验证
  - 测试多重锁：跨店结算的原子性验证
  - 测试信号量：限流控制的效果验证
  - 测试闭锁：聚合支付的等待机制验证

### 3. 集合功能测试
- 测试各种集合类型的分布式特性
- 验证集合操作的原子性
- 测试大数据量下的性能表现

### 4. 发布订阅测试
- 测试消息的实时传递
- 验证多个订阅者的消息接收
- 测试聊天室功能

### 5. 响应式编程测试
- 测试响应式操作的异步特性
- 验证超时和重试机制
- 测试背压处理

## 性能优化建议

1. **连接池配置**: 根据并发量调整 Redis 连接池大小
2. **序列化选择**: 选择合适的序列化器以提升性能
3. **批量操作**: 尽量使用批量操作减少网络开销
4. **TTL 设置**: 合理设置键的过期时间避免内存泄漏
5. **监控告警**: 添加 Redis 和 Redisson 的监控指标

## 常见问题

### Q: 如何处理 Redis 连接失败？
A: 应用会自动重试连接，可以通过配置调整重试参数。

### Q: 分布式锁会死锁吗？
A: Redisson 的锁都有自动过期时间，不会永久死锁。

### Q: 响应式编程和传统编程有什么区别？
A: 响应式编程基于事件驱动，非阻塞IO，适合高并发场景。

### Q: 如何在生产环境使用？
A: 需要配置 Redis 集群，调整连接池参数，添加监控和告警。

## 参考文档

- [Redisson 官方文档](https://redisson.org/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Redis 官方文档](https://redis.io/documentation)

## 许可证

本项目仅用于学习和演示目的。
