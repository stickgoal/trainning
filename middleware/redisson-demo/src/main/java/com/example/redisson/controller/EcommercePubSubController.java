package com.example.redisson.controller;

import com.example.redisson.service.EcommercePubSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redisson/ecommerce/pubsub")
@RequiredArgsConstructor
@Slf4j
public class EcommercePubSubController {

    private final EcommercePubSubService pubSubService;

    // ========== 订单状态通知 ==========
    
    @PostMapping("/order/status")
    public ResponseEntity<String> publishOrderStatusChange(
            @RequestParam String orderId,
            @RequestParam String userId,
            @RequestParam String status) {
        String result = pubSubService.publishOrderStatusChange(orderId, userId, status);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/order/status/subscribe")
    public ResponseEntity<String> subscribeOrderStatus(
            @RequestParam String userId,
            @RequestParam String subscriberName) {
        String result = pubSubService.subscribeOrderStatus(userId, subscriberName);
        return ResponseEntity.ok(result);
    }
    
    // ========== 价格变动通知 ==========
    
    @PostMapping("/price/change")
    public ResponseEntity<String> publishPriceChange(
            @RequestParam String productId,
            @RequestParam Double oldPrice,
            @RequestParam Double newPrice) {
        String result = pubSubService.publishPriceChange(productId, oldPrice, newPrice);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/price/subscribe")
    public ResponseEntity<String> subscribePriceChanges(
            @RequestParam String pattern,
            @RequestParam String subscriberName) {
        String result = pubSubService.subscribePriceChanges(pattern, subscriberName);
        return ResponseEntity.ok(result);
    }
    
    // ========== 促销活动广播 ==========
    
    @PostMapping("/promotion/broadcast")
    public ResponseEntity<String> broadcastPromotion(
            @RequestParam String promotionId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String discount) {
        String result = pubSubService.broadcastPromotion(promotionId, title, content, discount);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/promotion/subscribe")
    public ResponseEntity<String> subscribePromotionBroadcast(@RequestParam String subscriberName) {
        String result = pubSubService.subscribePromotionBroadcast(subscriberName);
        return ResponseEntity.ok(result);
    }
    
    // ========== 库存预警通知 ==========
    
    @PostMapping("/inventory/alert")
    public ResponseEntity<String> publishLowStockAlert(
            @RequestParam String productId,
            @RequestParam Integer currentStock,
            @RequestParam Integer threshold) {
        String result = pubSubService.publishLowStockAlert(productId, currentStock, threshold);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/inventory/subscribe")
    public ResponseEntity<String> subscribeInventoryAlert(@RequestParam String subscriberName) {
        String result = pubSubService.subscribeInventoryAlert(subscriberName);
        return ResponseEntity.ok(result);
    }
    
    // ========== 用户行为流 ==========
    
    @PostMapping("/behavior/publish")
    public ResponseEntity<String> publishUserBehavior(
            @RequestParam String userId,
            @RequestParam String action,
            @RequestParam String target,
            @RequestParam String data) {
        String result = pubSubService.publishUserBehavior(userId, action, target, data);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/behavior/subscribe")
    public ResponseEntity<String> subscribeUserBehaviorStream(@RequestParam String subscriberName) {
        String result = pubSubService.subscribeUserBehaviorStream(subscriberName);
        return ResponseEntity.ok(result);
    }
    
    // ========== 系统监控事件 ==========
    
    @PostMapping("/system/event")
    public ResponseEntity<String> publishSystemEvent(
            @RequestParam String eventType,
            @RequestParam String source,
            @RequestParam String details) {
        String result = pubSubService.publishSystemEvent(eventType, source, details);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/system/subscribe")
    public ResponseEntity<String> subscribeSystemEvents(@RequestParam String subscriberName) {
        String result = pubSubService.subscribeSystemEvents(subscriberName);
        return ResponseEntity.ok(result);
    }
    
    // ========== 实时聊天客服 ==========
    
    @PostMapping("/chat/join")
    public ResponseEntity<String> joinChatRoom(
            @RequestParam String roomId,
            @RequestParam String userName) {
        String result = pubSubService.joinChatRoom(roomId, userName);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/chat/message")
    public ResponseEntity<String> sendMessageToRoom(
            @RequestParam String roomId,
            @RequestParam String userName,
            @RequestParam String messageContent) {
        String result = pubSubService.sendMessageToRoom(roomId, userName, messageContent);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/chat/leave")
    public ResponseEntity<String> leaveChatRoom(
            @RequestParam String roomId,
            @RequestParam String userName) {
        String result = pubSubService.leaveChatRoom(roomId, userName);
        return ResponseEntity.ok(result);
    }
    
    // ========== 管理功能 ==========
    
    @GetMapping("/stats")
    public ResponseEntity<String> getSubscriberStats() {
        String stats = pubSubService.getSubscriberStats();
        return ResponseEntity.ok(stats);
    }
    
    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanupAllSubscriptions() {
        String result = pubSubService.cleanupAllSubscriptions();
        return ResponseEntity.ok(result);
    }
}
