package com.example.redisson.service;

import com.example.redisson.model.Order;
import com.example.redisson.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcommerceLockService {

    private final RedissonClient redissonClient;
    
    // 模拟商品库存存储
    private final AtomicInteger productStock = new AtomicInteger(100);
    private final AtomicLong orderIdGenerator = new AtomicLong(1);
    
    /**
     * 读写锁案例 - 商品信息查询与更新
     * 场景：多个用户同时查看商品详情，管理员更新商品信息
     */
    public String viewProductInfo(String productId) {
        String lockKey = "product:info:" + productId;
        return readWithLock(lockKey, () -> {
            log.info("用户查看商品信息: {}", productId);
            try {
                Thread.sleep(500); // 模拟数据库查询
                int stock = productStock.get();
                return String.format("商品ID: %s, 库存: %d, 价格: ¥299.99", productId, stock);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "查询被中断";
            }
        });
    }
    
    public String updateProductInfo(String productId, String newName, Double newPrice) {
        String lockKey = "product:info:" + productId;
        return writeWithLock(lockKey, () -> {
            log.info("管理员更新商品信息: {} -> {}", productId, newName);
            try {
                Thread.sleep(1000); // 模拟数据库更新
                return String.format("商品 %s 信息更新成功: 名称=%s, 价格=¥%.2f", productId, newName, newPrice);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "更新被中断";
            }
        });
    }
    
    /**
     * 公平锁案例 - 秒杀抢购
     * 场景：确保用户按请求顺序公平获取商品，避免插队
     */
    public String fairSeckill(String userId, String productId) {
        String lockKey = "seckill:fair:" + productId;
        return tryFairLock(lockKey, 5, 10, TimeUnit.SECONDS, () -> {
            int currentStock = productStock.get();
            if (currentStock <= 0) {
                return "商品已售罄";
            }
            
            // 模拟扣减库存
            if (productStock.compareAndSet(currentStock, currentStock - 1)) {
                log.info("用户 {} 秒杀成功，剩余库存: {}", userId, currentStock - 1);
                return String.format("用户 %s 秒杀成功！剩余库存: %d", userId, currentStock - 1);
            } else {
                return "库存扣减失败";
            }
        });
    }
    
    /**
     * 多重锁案例 - 跨店购物车结算
     * 场景：用户同时购买多个店铺的商品，需要同时锁定多个店铺的库存
     */
    public String multiStoreCheckout(String userId, String[] storeIds, Integer[] quantities) {
        String[] lockKeys = new String[storeIds.length];
        for (int i = 0; i < storeIds.length; i++) {
            lockKeys[i] = "store:stock:" + storeIds[i];
        }
        
        return tryMultiLock(lockKeys, 3, 10, TimeUnit.SECONDS, () -> {
            log.info("用户 {} 开始跨店结算，店铺数量: {}", userId, storeIds.length);
            
            try {
                // 模拟检查所有店铺库存
                Thread.sleep(500);
                
                // 模拟扣减所有店铺库存
                int totalQuantity = 0;
                for (int qty : quantities) {
                    totalQuantity += qty;
                }
                
                int currentStock = productStock.get();
                if (currentStock < totalQuantity) {
                    return "库存不足，无法完成跨店结算";
                }
                
                productStock.addAndGet(-totalQuantity);
                
                return String.format("用户 %s 跨店结算成功！购买数量: %d，剩余库存: %d", 
                                   userId, totalQuantity, currentStock - totalQuantity);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "跨店结算被中断";
            }
        });
    }
    
    /**
     * 普通锁案例 - 订单创建
     * 场景：防止重复订单创建
     */
    public String createOrder(String userId, String productId, Integer quantity) {
        String lockKey = "order:create:" + userId + ":" + productId;
        return executeWithLock(lockKey, 5, TimeUnit.SECONDS, () -> {
            String orderId = "ORD" + orderIdGenerator.getAndIncrement();
            
            log.info("创建订单: {} - 用户: {}, 商品: {}, 数量: {}", orderId, userId, productId, quantity);
            
            try {
                // 模拟订单处理
                Thread.sleep(800);
                
                // 检查库存
                int currentStock = productStock.get();
                if (currentStock < quantity) {
                    return "库存不足，无法创建订单";
                }
                
                // 扣减库存
                productStock.addAndGet(-quantity);
                
                return String.format("订单创建成功！订单号: %s, 用户: %s, 商品: %s, 数量: %d, 剩余库存: %d",
                                   orderId, userId, productId, quantity, currentStock - quantity);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "订单创建被中断";
            }
        });
    }
    
    /**
     * 分布式信号量案例 - 限流控制
     * 场景：限制同时访问商品详情页的用户数量
     */
    public String viewProductWithRateLimit(String userId, String productId) {
        String semaphoreKey = "product:view:limit:" + productId;
        RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKey);
        
        try {
            // 设置信号量许可数（最多10个用户同时查看）
            semaphore.trySetPermits(10);
            
            if (semaphore.tryAcquire(1, 2, TimeUnit.SECONDS)) {
                try {
                    log.info("用户 {} 获取查看许可，商品: {}", userId, productId);
                    Thread.sleep(1000); // 模拟查看商品详情
                    return String.format("用户 %s 成功查看商品 %s 详情", userId, productId);
                } finally {
                    semaphore.release();
                    log.info("用户 {} 释放查看许可，商品: {}", userId, productId);
                }
            } else {
                return String.format("商品 %s 访问人数过多，请稍后再试", productId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "查看请求被中断";
        }
    }
    
    /**
     * 分布式闭锁案例 - 聚合支付
     * 场景：等待多个支付渠道都响应后再完成订单
     */
    public String aggregatePayment(String orderId, String[] paymentChannels) {
        String countDownLatchKey = "payment:countdown:" + orderId;
        RCountDownLatch latch = redissonClient.getCountDownLatch(countDownLatchKey);
        
        try {
            // 设置等待的支付渠道数量
            latch.trySetCount(paymentChannels.length);
            
            log.info("订单 {} 等待 {} 个支付渠道响应", orderId, paymentChannels.length);
            
            // 模拟各个支付渠道异步处理
            for (String channel : paymentChannels) {
                new Thread(() -> {
                    try {
                        Thread.sleep((long) (Math.random() * 2000) + 500); // 随机延迟
                        log.info("支付渠道 {} 处理完成，订单: {}", channel, orderId);
                        latch.countDown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
            // 等待所有支付渠道完成
            boolean completed = latch.await(5, TimeUnit.SECONDS);
            
            if (completed) {
                return String.format("订单 %s 聚合支付成功！所有支付渠道已响应", orderId);
            } else {
                return String.format("订单 %s 支付超时，部分渠道未响应", orderId);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return String.format("订单 %s 支付处理被中断", orderId);
        }
    }
    
    // 获取当前库存
    public int getCurrentStock() {
        return productStock.get();
    }
    
    // 重置库存
    public void resetStock() {
        productStock.set(100);
        log.info("库存已重置为: {}", productStock.get());
    }
    
    // 以下是基础锁方法的封装
    
    private String readWithLock(String lockKey, java.util.function.Supplier<String> operation) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock readLock = rwLock.readLock();
        try {
            readLock.lock();
            log.info("获取读锁: {}", lockKey);
            return operation.get();
        } catch (Exception e) {
            log.error("读操作异常: {}", lockKey, e);
            return "读操作失败: " + e.getMessage();
        } finally {
            if (readLock.isHeldByCurrentThread()) {
                readLock.unlock();
                log.info("释放读锁: {}", lockKey);
            }
        }
    }
    
    private String writeWithLock(String lockKey, java.util.function.Supplier<String> operation) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock writeLock = rwLock.writeLock();
        try {
            writeLock.lock();
            log.info("获取写锁: {}", lockKey);
            return operation.get();
        } catch (Exception e) {
            log.error("写操作异常: {}", lockKey, e);
            return "写操作失败: " + e.getMessage();
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
                log.info("释放写锁: {}", lockKey);
            }
        }
    }
    
    private String tryFairLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, 
                              java.util.function.Supplier<String> operation) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        try {
            boolean acquired = fairLock.tryLock(waitTime, leaseTime, timeUnit);
            if (acquired) {
                log.info("获取公平锁: {}", lockKey);
                return operation.get();
            } else {
                log.warn("获取公平锁失败: {}", lockKey);
                return "系统繁忙，请稍后再试";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("公平锁获取被中断: {}", lockKey, e);
            return "请求被中断";
        } finally {
            if (fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
                log.info("释放公平锁: {}", lockKey);
            }
        }
    }
    
    private String tryMultiLock(String[] lockKeys, long waitTime, long leaseTime, TimeUnit timeUnit,
                               java.util.function.Supplier<String> operation) {
        RLock[] locks = new RLock[lockKeys.length];
        for (int i = 0; i < lockKeys.length; i++) {
            locks[i] = redissonClient.getLock(lockKeys[i]);
        }
        
        RLock multiLock = redissonClient.getMultiLock(locks);
        try {
            boolean acquired = multiLock.tryLock(waitTime, leaseTime, timeUnit);
            if (acquired) {
                log.info("获取多重锁: {}", (Object) lockKeys);
                return operation.get();
            } else {
                log.warn("获取多重锁失败: {}", (Object) lockKeys);
                return "资源锁定失败，请稍后再试";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("多重锁获取被中断: {}", (Object) lockKeys, e);
            return "请求被中断";
        } finally {
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
                log.info("释放多重锁: {}", (Object) lockKeys);
            }
        }
    }
    
    private String executeWithLock(String lockKey, long leaseTime, TimeUnit timeUnit, 
                                 java.util.function.Supplier<String> operation) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock(leaseTime, timeUnit);
            log.info("获取普通锁: {}", lockKey);
            return operation.get();
        } catch (Exception e) {
            log.error("锁操作异常: {}", lockKey, e);
            return "操作失败: " + e.getMessage();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("释放普通锁: {}", lockKey);
            }
        }
    }
}
