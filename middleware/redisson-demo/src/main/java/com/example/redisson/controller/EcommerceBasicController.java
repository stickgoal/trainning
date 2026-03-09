package com.example.redisson.controller;

import com.example.redisson.service.EcommerceBasicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/redisson/ecommerce/basic")
@RequiredArgsConstructor
@Slf4j
public class EcommerceBasicController {

    private final EcommerceBasicService basicService;

    // ========== 用户会话管理 ==========
    
    @PostMapping("/session")
    public ResponseEntity<String> setUserSession(
            @RequestParam String userId,
            @RequestParam String sessionId,
            @RequestParam long ttlHours) {
        String result = basicService.setUserSession(userId, sessionId, ttlHours);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/session/{userId}")
    public ResponseEntity<String> getUserSession(@PathVariable String userId) {
        String session = basicService.getUserSession(userId);
        return ResponseEntity.ok(session);
    }
    
    @GetMapping("/session/{userId}/online")
    public ResponseEntity<Boolean> isUserOnline(@PathVariable String userId) {
        boolean isOnline = basicService.isUserOnline(userId);
        return ResponseEntity.ok(isOnline);
    }
    
    // ========== 商品信息缓存 ==========
    
    @PostMapping("/product/cache")
    public ResponseEntity<String> cacheProductInfo(
            @RequestParam String productId,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam String category,
            @RequestParam Integer stock) {
        String result = basicService.cacheProductInfo(productId, name, price, category, stock);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/product/cache/{productId}")
    public ResponseEntity<Map<String, String>> getProductInfo(@PathVariable String productId) {
        Map<String, String> productInfo = basicService.getProductInfo(productId);
        return ResponseEntity.ok(productInfo);
    }
    
    @PostMapping("/product/cache/{productId}")
    public ResponseEntity<String> updateProductField(
            @PathVariable String productId,
            @RequestParam String field,
            @RequestParam String value) {
        String result = basicService.updateProductField(productId, field, value);
        return ResponseEntity.ok(result);
    }
    
    // ========== 用户浏览历史 ==========
    
    @PostMapping("/history/add")
    public ResponseEntity<String> addToBrowseHistory(
            @RequestParam String userId,
            @RequestParam String productId) {
        String result = basicService.addToBrowseHistory(userId, productId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<String>> getBrowseHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<String> history = basicService.getBrowseHistory(userId, limit);
        return ResponseEntity.ok(history);
    }
    
    @DeleteMapping("/history/{userId}")
    public ResponseEntity<String> clearBrowseHistory(@PathVariable String userId) {
        String result = basicService.clearBrowseHistory(userId);
        return ResponseEntity.ok(result);
    }
    
    // ========== 用户标签管理 ==========
    
    @PostMapping("/tags/add")
    public ResponseEntity<String> addUserTag(
            @RequestParam String userId,
            @RequestParam String tag) {
        String result = basicService.addUserTag(userId, tag);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/tags/{userId}")
    public ResponseEntity<Set<String>> getUserTags(@PathVariable String userId) {
        Set<String> tags = basicService.getUserTags(userId);
        return ResponseEntity.ok(tags);
    }
    
    @DeleteMapping("/tags/remove")
    public ResponseEntity<String> removeUserTag(
            @RequestParam String userId,
            @RequestParam String tag) {
        String result = basicService.removeUserTag(userId, tag);
        return ResponseEntity.ok(result);
    }
    
    // ========== 商品评分排行 ==========
    
    @PostMapping("/rating")
    public ResponseEntity<String> addProductRating(
            @RequestParam String productId,
            @RequestParam Double rating) {
        String result = basicService.addProductRating(productId, rating);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/rating/top")
    public ResponseEntity<List<Map<String, Object>>> getTopRatedProducts(@RequestParam(defaultValue = "10") int topN) {
        List<Map<String, Object>> topProducts = basicService.getTopRatedProducts(topN);
        return ResponseEntity.ok(topProducts);
    }
    
    // ========== 消息队列 ==========
    
    @PostMapping("/queue/enqueue")
    public ResponseEntity<String> enqueueMessage(
            @RequestParam String queueName,
            @RequestParam String message) {
        String result = basicService.enqueueMessage(queueName, message);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/queue/dequeue")
    public ResponseEntity<String> dequeueMessage(@RequestParam String queueName) {
        String message = basicService.dequeueMessage(queueName);
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/queue/size/{queueName}")
    public ResponseEntity<Integer> getQueueSize(@PathVariable String queueName) {
        int size = basicService.getQueueSize(queueName);
        return ResponseEntity.ok(size);
    }
    
    // ========== 分布式计数器 ==========
    
    @PostMapping("/clicks/increment")
    public ResponseEntity<String> incrementProductClick(@RequestParam String productId) {
        String result = basicService.incrementProductClick(productId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/clicks/{productId}")
    public ResponseEntity<Long> getProductClicks(@PathVariable String productId) {
        long clicks = basicService.getProductClicks(productId);
        return ResponseEntity.ok(clicks);
    }
    
    @PostMapping("/clicks/reset")
    public ResponseEntity<String> resetProductClicks(@RequestParam String productId) {
        String result = basicService.resetProductClicks(productId);
        return ResponseEntity.ok(result);
    }
    
    // ========== 分布式位图 - 用户签到 ==========
    
    @PostMapping("/checkin")
    public ResponseEntity<String> userCheckIn(
            @RequestParam String userId,
            @RequestParam int dayOfYear) {
        String result = basicService.userCheckIn(userId, dayOfYear);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/checkin/{userId}/{dayOfYear}")
    public ResponseEntity<Boolean> isUserCheckedIn(
            @PathVariable String userId,
            @PathVariable int dayOfYear) {
        boolean checked = basicService.isUserCheckedIn(userId, dayOfYear);
        return ResponseEntity.ok(checked);
    }
    
    @GetMapping("/checkin/{userId}")
    public ResponseEntity<Integer> getUserCheckInDays(@PathVariable String userId) {
        int days = basicService.getUserCheckInDays(userId);
        return ResponseEntity.ok(days);
    }
    
    // ========== 分布式布隆过滤器 ==========
    
    @PostMapping("/bloom/add")
    public ResponseEntity<String> addToRecommendationFilter(@RequestParam String productId) {
        String result = basicService.addToRecommendationFilter(productId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/bloom/check/{productId}")
    public ResponseEntity<Boolean> mightBeRecommended(@PathVariable String productId) {
        boolean mightContain = basicService.mightBeRecommended(productId);
        return ResponseEntity.ok(mightContain);
    }
    
    // ========== 分布式HyperLogLog ==========
    
    @PostMapping("/hll/record")
    public ResponseEntity<String> recordPageView(
            @RequestParam String pageId,
            @RequestParam String userId) {
        String result = basicService.recordPageView(pageId, userId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/hll/uv/{pageId}")
    public ResponseEntity<Long> getPageViewUV(@PathVariable String pageId) {
        long uv = basicService.getPageViewUV(pageId);
        return ResponseEntity.ok(uv);
    }
    
    // ========== 批量操作 ==========
    
    @PostMapping("/batch/import")
    public ResponseEntity<String> batchImportProducts(@RequestBody Map<String, Map<String, String>> products) {
        String result = basicService.batchImportProducts(products);
        return ResponseEntity.ok(result);
    }
    
    // ========== 数据清理 ==========
    
    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanExpiredData(@RequestParam String pattern) {
        String result = basicService.cleanExpiredData(pattern);
        return ResponseEntity.ok(result);
    }
    
    // ========== 管道操作 ==========
    
    @PostMapping("/pipeline/activity")
    public ResponseEntity<String> pipelineUserActivity(
            @RequestParam String userId,
            @RequestBody List<String> activities) {
        String result = basicService.pipelineUserActivity(userId, activities);
        return ResponseEntity.ok(result);
    }
}
