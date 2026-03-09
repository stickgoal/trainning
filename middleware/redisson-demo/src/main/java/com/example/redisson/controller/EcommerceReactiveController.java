package com.example.redisson.controller;

import com.example.redisson.service.EcommerceReactiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/redisson/ecommerce/reactive")
@RequiredArgsConstructor
@Slf4j
public class EcommerceReactiveController {

    private final EcommerceReactiveService reactiveService;

    // ========== 响应式商品搜索 ==========
    
    @GetMapping("/search")
    public Mono<List<String>> reactiveProductSearch(@RequestParam String keyword) {
        return reactiveService.reactiveProductSearch(keyword);
    }
    
    // ========== 响应式用户会话管理 ==========
    
    @PostMapping("/session")
    public Mono<String> reactiveUserSession(
            @RequestParam String userId,
            @RequestParam String sessionId) {
        return reactiveService.reactiveUserSession(userId, sessionId);
    }
    
    // ========== 响应式库存更新 ==========
    
    @PostMapping("/stock/batch")
    public Mono<Map<String, Integer>> reactiveBatchStockUpdate(@RequestBody Map<String, Integer> stockUpdates) {
        return reactiveService.reactiveBatchStockUpdate(stockUpdates);
    }
    
    // ========== 响应式购物车操作 ==========
    
    @PostMapping(value = "/cart/operations", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> reactiveCartOperations(
            @RequestParam String userId,
            @RequestBody List<String> operations) {
        return reactiveService.reactiveCartOperations(userId, operations);
    }
    
    // ========== 响应式订单处理 ==========
    
    @PostMapping("/order/process")
    public Mono<String> reactiveOrderProcessing(
            @RequestParam String orderId,
            @RequestParam String userId,
            @RequestBody List<String> productIds) {
        return reactiveService.reactiveOrderProcessing(orderId, userId, productIds);
    }
    
    // ========== 响应式实时推荐 ==========
    
    @GetMapping(value = "/recommendations/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> reactiveRealTimeRecommendations(@PathVariable String userId) {
        return reactiveService.reactiveRealTimeRecommendations(userId);
    }
    
    // ========== 响应式缓存预热 ==========
    
    @PostMapping("/cache/warmup")
    public Mono<Integer> reactiveCacheWarmup(@RequestBody List<String> productIds) {
        return reactiveService.reactiveCacheWarmup(productIds);
    }
    
    // ========== 响应式数据同步 ==========
    
    @PostMapping("/sync")
    public Mono<String> reactiveDataSync(
            @RequestParam String syncType,
            @RequestBody Map<String, String> data) {
        return reactiveService.reactiveDataSync(syncType, data);
    }
    
    // ========== 响应式监控指标 ==========
    
    @GetMapping(value = "/metrics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> reactiveMetricsCollection() {
        return reactiveService.reactiveMetricsCollection();
    }
    
    // ========== RxJava风格操作 ==========
    
    @GetMapping(value = "/search/rx/{keyword}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public org.reactivestreams.Publisher<String> rxProductSearchStream(@PathVariable String keyword) {
        return reactiveService.rxProductSearchStream(keyword);
    }
    
    // ========== 响应式限流控制 ==========
    
    @GetMapping("/rate-limit")
    public Mono<Boolean> reactiveRateLimit(
            @RequestParam String userId,
            @RequestParam String action) {
        return reactiveService.reactiveRateLimit(userId, action);
    }
    
    // ========== 响应式分布式事务 ==========
    
    @PostMapping("/transaction")
    public Mono<String> reactiveDistributedTransaction(
            @RequestParam String transactionId,
            @RequestBody List<String> operations) {
        return reactiveService.reactiveDistributedTransaction(transactionId, operations);
    }
}
