package com.example.redisson.controller;

import com.example.redisson.service.RedissonPubSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/api/redisson/pubsub")
@RequiredArgsConstructor
@Slf4j
public class RedissonPubSubController {

    private final RedissonPubSubService redissonPubSubService;

    // Basic publish/subscribe
    @PostMapping("/publish")
    public ResponseEntity<Long> publishMessage(@RequestParam String topicName, @RequestParam String message) {
        long listeners = redissonPubSubService.publishMessage(topicName, message);
        return ResponseEntity.ok(listeners);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToTopic(@RequestParam String topicName, @RequestParam String subscriberName) {
        String result = redissonPubSubService.subscribeToTopic(topicName, subscriberName);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribeFromTopic(@RequestParam String topicName, @RequestParam int listenerId) {
        String result = redissonPubSubService.unsubscribeFromTopic(topicName, listenerId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/unsubscribe/all")
    public ResponseEntity<String> unsubscribeAllFromTopic(@RequestParam String topicName) {
        String result = redissonPubSubService.unsubscribeAllFromTopic(topicName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/listeners")
    public ResponseEntity<Integer> getTopicListenersCount(@RequestParam String topicName) {
        int count = redissonPubSubService.getTopicListenersCount(topicName);
        return ResponseEntity.ok(count);
    }

    // Pattern subscription
    @PostMapping("/subscribe/pattern")
    public ResponseEntity<String> subscribeToPattern(@RequestParam String pattern, @RequestParam String subscriberName) {
        String result = redissonPubSubService.subscribeToPattern(pattern, subscriberName);
        return ResponseEntity.ok(result);
    }

    // Broadcast operations
    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcastToTopics(@RequestParam String[] topicNames, @RequestParam String message) {
        String result = redissonPubSubService.broadcastToTopics(topicNames, message);
        return ResponseEntity.ok(result);
    }

    // Delayed publish
    @PostMapping("/publish/delayed")
    public ResponseEntity<String> publishWithDelay(
            @RequestParam String topicName,
            @RequestParam String message,
            @RequestParam long delayMillis) {
        String result = redissonPubSubService.publishWithDelay(topicName, message, delayMillis);
        return ResponseEntity.ok(result);
    }

    // Chat room operations
    @PostMapping("/chat/join")
    public ResponseEntity<String> joinChatRoom(@RequestParam String roomName, @RequestParam String userName) {
        String result = redissonPubSubService.joinChatRoom(roomName, userName);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/chat/leave")
    public ResponseEntity<String> leaveChatRoom(@RequestParam String roomName, @RequestParam String userName) {
        String result = redissonPubSubService.leaveChatRoom(roomName, userName);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/chat/send")
    public ResponseEntity<String> sendMessageToChatRoom(
            @RequestParam String roomName,
            @RequestParam String userName,
            @RequestParam String message) {
        String result = redissonPubSubService.sendMessageToChatRoom(roomName, userName, message);
        return ResponseEntity.ok(result);
    }

    // Management operations
    @GetMapping("/subscriptions")
    public ResponseEntity<ConcurrentMap<String, Integer>> getActiveSubscriptions() {
        ConcurrentMap<String, Integer> subscriptions = redissonPubSubService.getActiveSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanupAllSubscriptions() {
        String result = redissonPubSubService.cleanupAllSubscriptions();
        return ResponseEntity.ok(result);
    }
}
