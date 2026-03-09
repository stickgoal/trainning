package com.example.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcommerceReactiveService {

    private final RedissonReactiveClient redissonReactive;
    private final RedissonRxClient redissonRxClient;

    /**
     * 响应式商品搜索 - 基于Reactor的非阻塞搜索
     * 场景：用户搜索商品时的异步处理
     */
    public Mono<List<String>> reactiveProductSearch(String keyword) {
        return redissonReactive.getList("search:history:" + keyword)
                .addAll(Arrays.asList("商品1:" + keyword, "商品2:" + keyword, "商品3:" + keyword))
                .then(redissonReactive.getList("search:history:" + keyword).readAll())
                .doOnNext(results -> log.info("响应式搜索完成，关键词: {}, 结果数: {}", keyword, results.size()))
                .doOnError(error -> log.error("响应式搜索失败: {}", error.getMessage()))
                .timeout(Duration.ofSeconds(5))
                .onErrorReturn(Collections.emptyList());
    }

    /**
     * 响应式用户会话管理 - 链式操作
     * 场景：用户登录后的会话创建和权限设置
     */
    public Mono<String> reactiveUserSession(String userId, String sessionId) {
        return redissonReactive.getMap("user:sessions")
                .fastPut(userId, sessionId)
                .then(redissonReactive.getMap("session:permissions")
                        .fastPut(sessionId, Arrays.asList("read", "write", "admin")))
                .then(redissonReactive.getBucket("session:expire:" + sessionId)
                        .set(LocalDateTime.now().toString(), Duration.ofHours(2)))
                .then(Mono.just(String.format("用户 %s 会话创建成功，会话ID: %s", userId, sessionId)))
                .doOnSuccess(result -> log.info("响应式会话创建完成: {}", result))
                .doOnError(error -> log.error("响应式会话创建失败: {}", error.getMessage()))
                .retry(3);
    }

    /**
     * 响应式库存更新 - 批量操作
     * 场景：批量更新商品库存的响应式处理
     */
    public Mono<Map<String, Integer>> reactiveBatchStockUpdate(Map<String, Integer> stockUpdates) {
        return Flux.fromIterable(stockUpdates.entrySet())
                .flatMap(entry -> 
                    redissonReactive.getBucket("stock:" + entry.getKey())
                            .set(entry.getValue().toString())
                            .map(result -> Map.entry(entry.getKey(), entry.getValue()))
                )
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .doOnNext(results -> log.info("响应式批量库存更新完成，更新商品数: {}", results.size()))
                .doOnError(error -> log.error("响应式批量库存更新失败: {}", error.getMessage()))
                .timeout(Duration.ofSeconds(10));
    }

    /**
     * 响应式购物车操作 - 背压处理
     * 场景：高并发下的购物车操作，使用背压控制
     */
    public Flux<String> reactiveCartOperations(String userId, List<String> operations) {
        return Flux.fromIterable(operations)
                .onBackpressureBuffer(100, // 缓冲100个操作
                    dropped -> log.warn("购物车操作被丢弃: {}", dropped))
                .flatMap(operation -> 
                    processCartOperation(userId, operation)
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                )
                .doOnNext(result -> log.info("购物车操作完成: {}", result))
                .doOnError(error -> log.error("购物车操作失败: {}", error.getMessage()))
                .take(Duration.ofMinutes(1)); // 最多处理1分钟
    }

    private Mono<String> processCartOperation(String userId, String operation) {
        String[] parts = operation.split(":");
        String action = parts[0];
        String productId = parts[1];
        
        return switch (action) {
            case "ADD" -> 
                redissonReactive.getMap("cart:" + userId)
                        .addAndGet(productId, 1)
                        .map(result -> String.format("商品 %s 添加到购物车", productId));
            case "REMOVE" -> 
                redissonReactive.getMap("cart:" + userId)
                        .remove(productId)
                        .map(result -> String.format("商品 %s 从购物车移除", productId));
            default -> Mono.just("未知操作: " + action);
        };
    }

    /**
     * 响应式订单处理 - 组合操作
     * 场景：订单创建的多个步骤组合处理
     */
    public Mono<String> reactiveOrderProcessing(String orderId, String userId, List<String> productIds) {
        // 步骤1：检查库存
        Mono<Boolean> stockCheck = Flux.fromIterable(productIds)
                .all(productId -> 
                    redissonReactive.getBucket("stock:" + productId)
                            .get()
                            .map(stock -> Integer.parseInt(stock.toString()) > 0)
                );
        
        // 步骤2：扣减库存
        Mono<List<Boolean>> stockDeduction = Flux.fromIterable(productIds)
                .flatMap(productId -> 
                    redissonReactive.getBucket("stock:" + productId)
                            .get()
                            .flatMap(stock -> {
                                int currentStock = Integer.parseInt(stock.toString());
                                return redissonReactive.getBucket("stock:" + productId)
                                        .set(String.valueOf(currentStock - 1));
                            })
                )
                .collectList();
        
        // 步骤3：创建订单记录
        Mono<Boolean> orderCreation = redissonReactive.getMap("orders:" + userId)
                .fastPut(orderId, String.join(",", productIds));
        
        // 组合所有步骤
        return stockCheck
                .filter(hasStock -> hasStock)
                .switchIfEmpty(Mono.error(new RuntimeException("库存不足")))
                .then(stockDeduction)
                .then(orderCreation)
                .map(result -> String.format("订单 %s 处理成功", orderId))
                .doOnSuccess(result -> log.info("响应式订单处理完成: {}", result))
                .doOnError(error -> log.error("响应式订单处理失败: {}", error.getMessage()))
                .timeout(Duration.ofSeconds(30))
                .retry(2);
    }

    /**
     * 响应式实时推荐 - 流式处理
     * 场景：基于用户行为的实时商品推荐
     */
    public Flux<String> reactiveRealTimeRecommendations(String userId) {
        return redissonReactive.getList("user:behavior:" + userId)
                .readAll()
                .flatMapMany(behaviors -> Flux.fromIterable(behaviors))
                .take(10) // 取最近10个行为
                .flatMap(behavior -> generateRecommendation(behavior))
                .distinct()
                .take(5) // 推荐5个商品
                .doOnNext(rec -> log.info("生成推荐: {}", rec))
                .doOnError(error -> log.error("推荐生成失败: {}", error.getMessage()));
    }

    private Mono<String> generateRecommendation(String behavior) {
        // 模拟基于行为生成推荐
        return Mono.just("推荐商品_" + behavior.hashCode() % 100)
                .delayElement(Duration.ofMillis(100));
    }

    /**
     * 响应式缓存预热 - 异步批量处理
     * 场景：系统启动时的缓存预热
     */
    public Mono<Integer> reactiveCacheWarmup(List<String> productIds) {
        return Flux.fromIterable(productIds)
                .window(50) // 每批处理50个商品
                .flatMap(batch -> 
                    batch.flatMap(productId -> 
                        redissonReactive.getBucket("cache:product:" + productId)
                                .set("商品数据_" + productId, Duration.ofHours(1))
                    )
                )
                .count()
                .map(count -> count.intValue())
                .doOnNext(count -> log.info("响应式缓存预热完成，预热商品数: {}", count))
                .doOnError(error -> log.error("响应式缓存预热失败: {}", error.getMessage()))
                .timeout(Duration.ofMinutes(2));
    }

    /**
     * 响应式数据同步 - 可重试操作
     * 场景：数据库到Redis的数据同步
     */
    public Mono<String> reactiveDataSync(String syncType, Map<String, String> data) {
        return Flux.fromIterable(data.entrySet())
                .flatMap(entry -> 
                    redissonReactive.getBucket("sync:" + syncType + ":" + entry.getKey())
                            .set(entry.getValue())
                            .retryWhen(reactor.util.retry.Retry.backoff(3, Duration.ofSeconds(1))
                                    .doBeforeRetry(retry -> 
                                        log.warn("数据同步重试，第{}次: {}", retry.totalTries() + 1, entry.getKey())))
                )
                .then(Mono.just(String.format("数据同步完成，类型: %s，记录数: %d", syncType, data.size())))
                .doOnNext(result -> log.info("响应式数据同步完成: {}", result))
                .doOnError(error -> log.error("响应式数据同步失败: {}", error.getMessage()));
    }

    /**
     * 响应式监控指标 - 实时监控
     * 场景：系统性能指标的实时收集
     */
    public Flux<Map<String, Object>> reactiveMetricsCollection() {
        return Flux.interval(Duration.ofSeconds(5)) // 每5秒收集一次
                .flatMap(tick -> 
                    Mono.zip(
                        redissonReactive.getKeys().count(),
                        redissonReactive.getMap("user:sessions").size(),
                        redissonReactive.getList("orders:pending").size(),
                        (keyCount, sessionCount, pendingCount) -> {
                            Map<String, Object> metrics = new HashMap<>();
                            metrics.put("timestamp", LocalDateTime.now());
                            metrics.put("keyCount", keyCount);
                            metrics.put("activeSessions", sessionCount);
                            metrics.put("pendingOrders", pendingCount);
                            return metrics;
                        }
                    )
                )
                .doOnNext(metrics -> log.info("收集监控指标: {}", metrics))
                .doOnError(error -> log.error("监控指标收集失败: {}", error.getMessage()));
    }

    /**
     * RxJava风格的响应式操作 - 基于RxJava
     * 场景：使用RxJava进行响应式编程
     */
    public io.reactivex.Observable<String> rxProductSearchStream(String keyword) {
        return redissonRxClient.getList("search:history:" + keyword)
                .readAll()
                .toObservable()
                .flatMap(results -> 
                    io.reactivex.Observable.fromIterable(results)
                        .map(result -> result + "_rx_processed")
                )
                .doOnNext(result -> log.info("RxJava搜索结果: {}", result))
                .doOnError(error -> log.error("RxJava搜索失败: {}", error.getMessage()));
    }

    /**
     * 响应式限流控制 - 令牌桶算法
     * 场景：API调用的响应式限流
     */
    public Mono<Boolean> reactiveRateLimit(String userId, String action) {
        String bucketKey = "rate_limit:" + userId + ":" + action;
        
        return redissonReactive.getBucket(bucketKey)
                .get()
                .map(tokens -> Integer.parseInt(tokens.toString()))
                .defaultIfEmpty(10) // 默认10个令牌
                .flatMap(currentTokens -> {
                    if (currentTokens > 0) {
                        return redissonReactive.getBucket(bucketKey)
                                .set(String.valueOf(currentTokens - 1), Duration.ofMinutes(1))
                                .map(result -> true);
                    } else {
                        return Mono.just(false);
                    }
                })
                .doOnNext(allowed -> log.info("用户 {} 操作 {} 限流检查: {}", userId, action, allowed ? "通过" : "拒绝"));
    }

    /**
     * 响应式分布式事务 - 补偿事务
     * 场景：需要补偿机制的分布式事务
     */
    public Mono<String> reactiveDistributedTransaction(String transactionId, List<String> operations) {
        List<Mono<String>> operationMonos = operations.stream()
                .map(op -> executeOperationWithCompensation(transactionId, op))
                .toList();
        
        return Mono.when(operationMonos)
                .then(Mono.just(String.format("分布式事务 %s 执行成功", transactionId)))
                .onErrorResume(error -> {
                    log.error("分布式事务失败，开始补偿: {}", error.getMessage());
                    return compensateTransaction(transactionId, operations);
                })
                .doOnNext(result -> log.info("分布式事务完成: {}", result));
    }

    private Mono<String> executeOperationWithCompensation(String transactionId, String operation) {
        // 执行操作并记录补偿信息
        return redissonReactive.getList("tx:operations:" + transactionId)
                .add(operation)
                .then(Mono.just("操作成功: " + operation));
    }

    private Mono<String> compensateTransaction(String transactionId, List<String> operations) {
        return Flux.fromIterable(operations)
                .flatMap(operation -> 
                    redissonReactive.getList("tx:compensation:" + transactionId)
                            .add("补偿_" + operation)
                )
                .then(Mono.just(String.format("分布式事务 %s 补偿完成", transactionId)));
    }
}
