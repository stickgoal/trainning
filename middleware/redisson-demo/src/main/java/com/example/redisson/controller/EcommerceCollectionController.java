package com.example.redisson.controller;

import com.example.redisson.service.EcommerceCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/redisson/ecommerce/collection")
@RequiredArgsConstructor
@Slf4j
public class EcommerceCollectionController {

    private final EcommerceCollectionService collectionService;

    // ========== 购物车管理 ==========
    
    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(
            @RequestParam String userId,
            @RequestParam String productId,
            @RequestParam Integer quantity) {
        String result = collectionService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/cart/update")
    public ResponseEntity<String> updateCartQuantity(
            @RequestParam String userId,
            @RequestParam String productId,
            @RequestParam Integer quantity) {
        String result = collectionService.updateCartQuantity(userId, productId, quantity);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/cart/{userId}")
    public ResponseEntity<Map<String, Integer>> getCart(@PathVariable String userId) {
        Map<String, Integer> cart = collectionService.getCart(userId);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/cart/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable String userId) {
        String result = collectionService.clearCart(userId);
        return ResponseEntity.ok(result);
    }
    
    // ========== 收藏夹管理 ==========
    
    @PostMapping("/favorites/add")
    public ResponseEntity<String> addToFavorites(
            @RequestParam String userId,
            @RequestParam String productId) {
        String result = collectionService.addToFavorites(userId, productId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/favorites/{userId}")
    public ResponseEntity<Set<String>> getFavorites(@PathVariable String userId) {
        Set<String> favorites = collectionService.getFavorites(userId);
        return ResponseEntity.ok(favorites);
    }
    
    @DeleteMapping("/favorites/remove")
    public ResponseEntity<String> removeFromFavorites(
            @RequestParam String userId,
            @RequestParam String productId) {
        String result = collectionService.removeFromFavorites(userId, productId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/favorites/common")
    public ResponseEntity<Set<String>> getCommonFavorites(@RequestParam String[] userIds) {
        Set<String> commonFavorites = collectionService.getCommonFavorites(userIds);
        return ResponseEntity.ok(commonFavorites);
    }
    
    // ========== 浏览历史 ==========
    
    @PostMapping("/history/add")
    public ResponseEntity<String> addToBrowseHistory(
            @RequestParam String userId,
            @RequestParam String productId) {
        String result = collectionService.addToBrowseHistory(userId, productId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<String>> getBrowseHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<String> history = collectionService.getBrowseHistory(userId, page, size);
        return ResponseEntity.ok(history);
    }
    
    // ========== 订单处理队列 ==========
    
    @PostMapping("/order/submit")
    public ResponseEntity<String> submitOrder(
            @RequestParam String orderId,
            @RequestParam String userId,
            @RequestParam Double amount) {
        String result = collectionService.submitOrder(orderId, userId, amount);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/order/process")
    public ResponseEntity<String> processNextOrder() {
        String result = collectionService.processNextOrder();
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/order/pending/count")
    public ResponseEntity<Integer> getPendingOrderCount() {
        int count = collectionService.getPendingOrderCount();
        return ResponseEntity.ok(count);
    }
    
    // ========== VIP优先级订单 ==========
    
    @PostMapping("/order/priority/submit")
    public ResponseEntity<String> submitPriorityOrder(
            @RequestParam String orderId,
            @RequestParam String userId,
            @RequestParam Double amount,
            @RequestParam String vipLevel) {
        String result = collectionService.submitPriorityOrder(orderId, userId, amount, vipLevel);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/order/priority/process")
    public ResponseEntity<String> processNextPriorityOrder() {
        String result = collectionService.processNextPriorityOrder();
        return ResponseEntity.ok(result);
    }
    
    // ========== 商品排行榜 ==========
    
    @PostMapping("/ranking/sales")
    public ResponseEntity<String> recordProductSale(
            @RequestParam String productId,
            @RequestParam Integer quantity) {
        String result = collectionService.recordProductSale(productId, quantity);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/ranking/top")
    public ResponseEntity<List<Map<String, Object>>> getTopProducts(@RequestParam(defaultValue = "10") int topN) {
        List<Map<String, Object>> topProducts = collectionService.getTopProducts(topN);
        return ResponseEntity.ok(topProducts);
    }
    
    @GetMapping("/ranking/product/{productId}")
    public ResponseEntity<Integer> getProductRank(@PathVariable String productId) {
        Integer rank = collectionService.getProductRank(productId);
        return ResponseEntity.ok(rank);
    }
    
    // ========== 推荐缓存 ==========
    
    @PostMapping("/recommendation/add")
    public ResponseEntity<String> addToRecommendationCache(@RequestParam String productId) {
        String result = collectionService.addToRecommendationCache(productId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/recommendation/list")
    public ResponseEntity<List<String>> getRecommendations(@RequestParam(defaultValue = "10") int count) {
        List<String> recommendations = collectionService.getRecommendations(count);
        return ResponseEntity.ok(recommendations);
    }
    
    // ========== 地理位置服务 ==========
    
    @PostMapping("/geo/store")
    public ResponseEntity<String> addStoreLocation(
            @RequestParam String storeId,
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        String result = collectionService.addStoreLocation(storeId, longitude, latitude);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/geo/nearby")
    public ResponseEntity<List<Map<String, Object>>> findNearbyStores(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam Double radiusKm) {
        List<Map<String, Object>> nearbyStores = collectionService.findNearbyStores(longitude, latitude, radiusKm);
        return ResponseEntity.ok(nearbyStores);
    }
}
