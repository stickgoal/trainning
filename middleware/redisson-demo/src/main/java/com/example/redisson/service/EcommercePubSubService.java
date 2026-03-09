package com.example.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcommercePubSubService {

    private final RedissonClient redissonClient;
    
    // 存储订阅者信息
    private final ConcurrentHashMap<String, List<String>> subscribers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RTopic> topics = new ConcurrentHashMap<>();

    /**
     * 订单状态通知 - 基础发布订阅
     * 场景：订单状态变更时通知用户
     */
    public String publishOrderStatusChange(String orderId, String userId, String status) {
        String topicName = "order:status:" + userId;
        RTopic topic = redissonClient.getTopic(topicName);
        
        String message = String.format("%s:%s:%s:%s", orderId, status, LocalDateTime.now(), "订单状态更新");
        long listeners = topic.publish(message);
        
        log.info("发布订单状态通知: 订单={}, 用户={}, 状态={}, 监听者数={}", 
                orderId, userId, status, listeners);
        return String.format("订单状态通知已发送给 %d 个监听者", listeners);
    }
    
    public String subscribeOrderStatus(String userId, String subscriberName) {
        String topicName = "order:status:" + userId;
        RTopic topic = redissonClient.getTopic(topicName);
        
        topic.addListener(String.class, (channel, message) -> {
            log.info("订阅者 {} 收到订单状态通知: {}", subscriberName, message);
            // 这里可以推送给WebSocket、短信等
        });
        
        // 记录订阅者
        subscribers.computeIfAbsent(topicName, k -> new CopyOnWriteArrayList<>()).add(subscriberName);
        topics.put(topicName, topic);
        
        log.info("订阅者 {} 已订阅订单状态通知: {}", subscriberName, userId);
        return String.format("订阅者 %s 已订阅用户 %s 的订单状态通知", subscriberName, userId);
    }
    
    /**
     * 商品价格变动通知 - 模式订阅
     * 场景：用户关注商品价格变动，使用模式匹配批量订阅
     */
    public String publishPriceChange(String productId, Double oldPrice, Double newPrice) {
        String topicName = "product:price:" + productId;
        RTopic topic = redissonClient.getTopic(topicName);
        
        String message = String.format("%s:%.2f:%.2f:%s:%s", 
                productId, oldPrice, newPrice, LocalDateTime.now(), "价格变动通知");
        long listeners = topic.publish(message);
        
        log.info("发布价格变动通知: 商品={}, 原价={}, 新价={}, 监听者数={}", 
                productId, oldPrice, newPrice, listeners);
        return String.format("价格变动通知已发送给 %d 个监听者", listeners);
    }
    
    public String subscribePriceChanges(String pattern, String subscriberName) {
        RPatternTopic patternTopic = redissonClient.getPatternTopic("product:price:*");
        
        int listenerId = patternTopic.addListener(String.class, (pattern, channel, message) -> {
            String productId = channel.split(":")[2];
            log.info("订阅者 {} 收到商品 {} 价格变动通知: {}", subscriberName, productId, message);
            // 可以发送邮件、推送通知等
        });
        
        log.info("订阅者 {} 已订阅价格变动模式: {}", subscriberName, pattern);
        return String.format("订阅者 %s 已订阅价格变动模式，监听器ID: %d", subscriberName, listenerId);
    }
    
    /**
     * 促销活动广播 - 广播消息
     * 场景：向所有在线用户广播促销信息
     */
    public String broadcastPromotion(String promotionId, String title, String content, String discount) {
        String topicName = "promotion:broadcast";
        RTopic topic = redissonClient.getTopic(topicName);
        
        String message = String.format("%s:%s:%s:%s:%s:%s", 
                promotionId, title, content, discount, LocalDateTime.now(), "促销广播");
        long listeners = topic.publish(message);
        
        log.info("广播促销活动: 活动={}, 标题={}, 折扣={}, 监听者数={}", 
                promotionId, title, discount, listeners);
        return String.format("促销活动已广播给 %d 个用户", listeners);
    }
    
    public String subscribePromotionBroadcast(String subscriberName) {
        String topicName = "promotion:broadcast";
        RTopic topic = redissonClient.getTopic(topicName);
        
        topic.addListener(String.class, (channel, message) -> {
            log.info("订阅者 {} 收到促销广播: {}", subscriberName, message);
            // 可以显示在用户首页、发送推送等
        });
        
        subscribers.computeIfAbsent(topicName, k -> new CopyOnWriteArrayList<>()).add(subscriberName);
        topics.put(topicName, topic);
        
        log.info("订阅者 {} 已订阅促销广播", subscriberName);
        return String.format("订阅者 %s 已订阅促销广播", subscriberName);
    }
    
    /**
     * 库存预警通知 - 延迟发布
     * 场景：商品库存不足时延迟发送预警通知
     */
    public String publishLowStockAlert(String productId, Integer currentStock, Integer threshold) {
        String topicName = "inventory:alert";
        RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(
                redissonClient.getQueue("inventory:delayed"));
        
        String message = String.format("%s:%d:%d:%s:%s", 
                productId, currentStock, threshold, LocalDateTime.now(), "库存预警");
        
        // 延迟5分钟发布，避免频繁预警
        delayedQueue.offer(message, 5, TimeUnit.MINUTES);
        
        log.info("发布库存预警通知（延迟5分钟）: 商品={}, 当前库存={}, 阈值={}", 
                productId, currentStock, threshold);
        return String.format("商品 %s 库存预警通知已安排在5分钟后发送", productId);
    }
    
    public String subscribeInventoryAlert(String subscriberName) {
        String topicName = "inventory:alert";
        RTopic topic = redissonClient.getTopic(topicName);
        
        topic.addListener(String.class, (channel, message) -> {
            log.info("订阅者 {} 收到库存预警: {}", subscriberName, message);
            // 可以通知采购部门、自动补货等
        });
        
        subscribers.computeIfAbsent(topicName, k -> new CopyOnWriteArrayList<>()).add(subscriberName);
        topics.put(topicName, topic);
        
        log.info("订阅者 {} 已订阅库存预警", subscriberName);
        return String.format("订阅者 %s 已订阅库存预警", subscriberName);
    }
    
    /**
     * 用户行为流 - 实时数据流
     * 场景：实时收集和分析用户行为数据
     */
    public String publishUserBehavior(String userId, String action, String target, Object data) {
        String topicName = "user:behavior:stream";
        RTopic topic = redissonClient.getTopic(topicName);
        
        String message = String.format("%s:%s:%s:%s:%s:%s", 
                userId, action, target, data, LocalDateTime.now(), "用户行为");
        long listeners = topic.publish(message);
        
        log.info("发布用户行为: 用户={}, 行为={}, 目标={}, 监听者数={}", 
                userId, action, target, listeners);
        return String.format("用户行为数据已发送给 %d 个监听者", listeners);
    }
    
    public String subscribeUserBehaviorStream(String subscriberName) {
        String topicName = "user:behavior:stream";
        RTopic topic = redissonClient.getTopic(topicName);
        
        topic.addListener(String.class, (channel, message) -> {
            log.info("订阅者 {} 收到用户行为: {}", subscriberName, message);
            // 可以写入数据仓库、实时分析等
        });
        
        subscribers.computeIfAbsent(topicName, k -> new CopyOnWriteArrayList<>()).add(subscriberName);
        topics.put(topicName, topic);
        
        log.info("订阅者 {} 已订阅用户行为流", subscriberName);
        return String.format("订阅者 %s 已订阅用户行为流", subscriberName);
    }
    
    /**
     * 系统监控事件 - 可靠消息传递
     * 场景：系统关键事件的可靠传递，确保不丢失
     */
    public String publishSystemEvent(String eventType, String source, String details) {
        String topicName = "system:events";
        RReliableTopic reliableTopic = redissonClient.getReliableTopic(topicName);
        
        String message = String.format("%s:%s:%s:%s:%s", 
                eventType, source, details, LocalDateTime.now(), "系统事件");
        
        reliableTopic.publish(message);
        
        log.info("发布系统事件: 类型={}, 来源={}, 详情={}", eventType, source, details);
        return String.format("系统事件已发布: %s", eventType);
    }
    
    public String subscribeSystemEvents(String subscriberName) {
        String topicName = "system:events";
        RReliableTopic reliableTopic = redissonClient.getReliableTopic(topicName);
        
        reliableTopic.addListener(String.class, (channel, message) -> {
            log.info("订阅者 {} 收到系统事件: {}", subscriberName, message);
            // 可以写入日志、触发告警等
        });
        
        log.info("订阅者 {} 已订阅系统事件", subscriberName);
        return String.format("订阅者 %s 已订阅系统事件", subscriberName);
    }
    
    /**
     * 实时聊天客服 - 多房间聊天
     * 场景：用户与客服的实时聊天
     */
    public String joinChatRoom(String roomId, String userName) {
        String topicName = "chat:room:" + roomId;
        RTopic topic = redissonClient.getTopic(topicName);
        
        // 监听房间消息
        topic.addListener(String.class, (channel, message) -> {
            log.info("房间 {} 用户 {} 收到消息: {}", roomId, userName, message);
        });
        
        // 记录订阅者
        subscribers.computeIfAbsent(topicName, k -> new CopyOnWriteArrayList<>()).add(userName);
        topics.put(topicName, topic);
        
        // 发送加入消息
        String joinMessage = String.format("%s:%s:%s:%s", 
                "SYSTEM", userName, "加入房间", LocalDateTime.now());
        topic.publish(joinMessage);
        
        log.info("用户 {} 加入聊天室: {}", userName, roomId);
        return String.format("用户 %s 已加入聊天室 %s", userName, roomId);
    }
    
    public String sendMessageToRoom(String roomId, String userName, String messageContent) {
        String topicName = "chat:room:" + roomId;
        RTopic topic = redissonClient.getTopic(topicName);
        
        String message = String.format("%s:%s:%s:%s", 
                userName, messageContent, "USER_MESSAGE", LocalDateTime.now());
        long listeners = topic.publish(message);
        
        log.info("用户 {} 在房间 {} 发送消息: {}, 监听者数={}", userName, roomId, messageContent, listeners);
        return String.format("消息已发送给房间 %s 中的 %d 个用户", roomId, listeners);
    }
    
    public String leaveChatRoom(String roomId, String userName) {
        String topicName = "chat:room:" + roomId;
        RTopic topic = redissonClient.getTopic(topicName);
        
        // 发送离开消息
        String leaveMessage = String.format("%s:%s:%s:%s", 
                "SYSTEM", userName, "离开房间", LocalDateTime.now());
        topic.publish(leaveMessage);
        
        // 移除订阅者记录
        List<String> roomSubscribers = subscribers.get(topicName);
        if (roomSubscribers != null) {
            roomSubscribers.remove(userName);
            if (roomSubscribers.isEmpty()) {
                subscribers.remove(topicName);
                topics.remove(topicName);
            }
        }
        
        log.info("用户 {} 离开聊天室: {}", userName, roomId);
        return String.format("用户 %s 已离开聊天室 %s", userName, roomId);
    }
    
    /**
     * 获取订阅者统计信息
     */
    public String getSubscriberStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("订阅者统计信息:\n");
        
        subscribers.forEach((topic, subscriberList) -> {
            stats.append(String.format("- 主题 %s: %d 个订阅者\n", topic, subscriberList.size()));
            subscriberList.forEach(subscriber -> 
                stats.append(String.format("  - %s\n", subscriber)));
        });
        
        return stats.toString();
    }
    
    /**
     * 清理所有订阅
     */
    public String cleanupAllSubscriptions() {
        topics.values().forEach(topic -> {
            // 移除所有监听器
            topic.removeAllListeners();
        });
        
        int topicCount = topics.size();
        topics.clear();
        subscribers.clear();
        
        log.info("清理所有订阅，清理的主题数: {}", topicCount);
        return String.format("已清理 %d 个主题的所有订阅", topicCount);
    }
}
