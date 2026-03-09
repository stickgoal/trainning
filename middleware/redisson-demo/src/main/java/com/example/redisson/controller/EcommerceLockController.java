package com.example.redisson.controller;

import com.example.redisson.service.EcommerceLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redisson/ecommerce")
@RequiredArgsConstructor
@Slf4j
public class EcommerceLockController {

    private final EcommerceLockService ecommerceLockService;

    // ========== 读写锁案例 - 商品信息管理 ==========
    
    @GetMapping("/product/view")
    public ResponseEntity<String> viewProductInfo(@RequestParam String productId) {
        log.info("查看商品信息请求: {}", productId);
        String result = ecommerceLockService.viewProductInfo(productId);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/product/update")
    public ResponseEntity<String> updateProductInfo(
            @RequestParam String productId,
            @RequestParam String newName,
            @RequestParam Double newPrice) {
        log.info("更新商品信息请求: {} -> {}", productId, newName);
        String result = ecommerceLockService.updateProductInfo(productId, newName, newPrice);
        return ResponseEntity.ok(result);
    }
    
    // ========== 公平锁案例 - 秒杀抢购 ==========
    
    @PostMapping("/seckill/fair")
    public ResponseEntity<String> fairSeckill(@RequestParam String userId, @RequestParam String productId) {
        log.info("公平秒杀请求: 用户={}, 商品={}", userId, productId);
        String result = ecommerceLockService.fairSeckill(userId, productId);
        return ResponseEntity.ok(result);
    }
    
    // ========== 多重锁案例 - 跨店购物车结算 ==========
    
    @PostMapping("/checkout/multi-store")
    public ResponseEntity<String> multiStoreCheckout(
            @RequestParam String userId,
            @RequestParam String[] storeIds,
            @RequestParam Integer[] quantities) {
        log.info("跨店结算请求: 用户={}, 店铺数量={}", userId, storeIds.length);
        String result = ecommerceLockService.multiStoreCheckout(userId, storeIds, quantities);
        return ResponseEntity.ok(result);
    }
    
    // ========== 普通锁案例 - 订单创建 ==========
    
    @PostMapping("/order/create")
    public ResponseEntity<String> createOrder(
            @RequestParam String userId,
            @RequestParam String productId,
            @RequestParam Integer quantity) {
        log.info("创建订单请求: 用户={}, 商品={}, 数量={}", userId, productId, quantity);
        String result = ecommerceLockService.createOrder(userId, productId, quantity);
        return ResponseEntity.ok(result);
    }
    
    // ========== 分布式信号量案例 - 限流控制 ==========
    
    @GetMapping("/product/view-rate-limit")
    public ResponseEntity<String> viewProductWithRateLimit(@RequestParam String userId, @RequestParam String productId) {
        log.info("限流查看商品请求: 用户={}, 商品={}", userId, productId);
        String result = ecommerceLockService.viewProductWithRateLimit(userId, productId);
        return ResponseEntity.ok(result);
    }
    
    // ========== 分布式闭锁案例 - 聚合支付 ==========
    
    @PostMapping("/payment/aggregate")
    public ResponseEntity<String> aggregatePayment(
            @RequestParam String orderId,
            @RequestParam String[] paymentChannels) {
        log.info("聚合支付请求: 订单={}, 支付渠道数量={}", orderId, paymentChannels.length);
        String result = ecommerceLockService.aggregatePayment(orderId, paymentChannels);
        return ResponseEntity.ok(result);
    }
    
    // ========== 库存管理 ==========
    
    @GetMapping("/stock/current")
    public ResponseEntity<Integer> getCurrentStock() {
        int stock = ecommerceLockService.getCurrentStock();
        log.info("查询当前库存: {}", stock);
        return ResponseEntity.ok(stock);
    }
    
    @PostMapping("/stock/reset")
    public ResponseEntity<String> resetStock() {
        log.info("重置库存");
        ecommerceLockService.resetStock();
        return ResponseEntity.ok("库存已重置为100");
    }
    
    // ========== 场景说明接口 ==========
    
    @GetMapping("/scenarios/read-write-lock")
    public ResponseEntity<String> getReadWriteLockScenario() {
        String description = """
            读写锁场景 - 商品信息管理：
            
            场景描述：
            - 多个用户同时查看商品详情（读操作）
            - 管理员更新商品信息（写操作）
            
            特点：
            - 读锁：多个用户可以同时查看，提高并发性能
            - 写锁：管理员更新时，其他用户需要等待，确保数据一致性
            
            测试方法：
            1. 同时调用 GET /api/redisson/ecommerce/product/view?productId=1001
            2. 在查看过程中调用 POST /api/redisson/ecommerce/product/update?productId=1001&name=新商品名&price=399.99
            3. 观察读操作和写操作的互斥效果
            """;
        return ResponseEntity.ok(description);
    }
    
    @GetMapping("/scenarios/fair-lock")
    public ResponseEntity<String> getFairLockScenario() {
        String description = """
            公平锁场景 - 秒杀抢购：
            
            场景描述：
            - 多个用户同时抢购限量商品
            - 按请求顺序公平处理，避免插队
            
            特点：
            - FIFO（先进先出）顺序获取锁
            - 避免线程饥饿问题
            - 确保公平性，但性能略低于非公平锁
            
            测试方法：
            1. 重置库存：POST /api/redisson/ecommerce/stock/reset
            2. 快速连续调用 POST /api/redisson/ecommerce/seckill/fair?userId=user1&productId=1001
            3. 观察用户按请求顺序获取商品
            """;
        return ResponseEntity.ok(description);
    }
    
    @GetMapping("/scenarios/multi-lock")
    public ResponseEntity<String> getMultiLockScenario() {
        String description = """
            多重锁场景 - 跨店购物车结算：
            
            场景描述：
            - 用户同时购买多个店铺的商品
            - 需要同时锁定多个店铺的库存
            
            特点：
            - 原子性锁定多个资源
            - 要么全部成功，要么全部失败
            - 避免部分锁定导致的资源不一致
            
            测试方法：
            1. 调用 POST /api/redisson/ecommerce/checkout/multi-store?userId=user1&storeIds=store1,store2,store3&quantities=1,2,1
            2. 观察多个店铺库存被同时锁定和扣减
            3. 如果任一店铺库存不足，整个结算失败
            """;
        return ResponseEntity.ok(description);
    }
    
    @GetMapping("/scenarios/semaphore")
    public ResponseEntity<String> getSemaphoreScenario() {
        String description = """
            分布式信号量场景 - 商品详情页限流：
            
            场景描述：
            - 限制同时查看商品详情页的用户数量
            - 防止服务器过载
            
            特点：
            - 控制并发访问数量
            - 超过限制的用户需要等待
            - 保护系统资源，提供稳定服务
            
            测试方法：
            1. 快速连续调用 GET /api/redisson/ecommerce/product/view-rate-limit?userId=user1&productId=1001
            2. 超过10个并发用户时，后续请求会收到限流提示
            3. 等待用户查看完成后，新的请求可以继续访问
            """;
        return ResponseEntity.ok(description);
    }
    
    @GetMapping("/scenarios/countdown-latch")
    public ResponseEntity<String> getCountDownLatchScenario() {
        String description = """
            分布式闭锁场景 - 聚合支付：
            
            场景描述：
            - 订单需要等待多个支付渠道响应
            - 所有渠道响应后才完成支付
            
            特点：
            - 等待多个异步操作完成
            - 主线程阻塞直到所有子操作完成
            - 适用于需要聚合结果的操作
            
            测试方法：
            1. 调用 POST /api/redisson/ecommerce/payment/aggregate?orderId=ORD001&paymentChannels=alipay,wechat,bank
            2. 观察系统等待所有支付渠道响应
            3. 所有渠道响应后返回支付成功结果
            """;
        return ResponseEntity.ok(description);
    }
}
