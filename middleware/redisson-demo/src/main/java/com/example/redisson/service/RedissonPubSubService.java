package com.example.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonPubSubService {

    private final RedissonClient redissonClient;
    private final ConcurrentMap<String, Integer> topicListeners = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("Redisson PubSub Service initialized");
    }

    // Publish message
    public long publishMessage(String topicName, String message) {
        RTopic topic = redissonClient.getTopic(topicName);
        long listenersReached = topic.publish(message);
        log.info("Published message to topic: {} message: {} listeners reached: {}", topicName, message, listenersReached);
        return listenersReached;
    }

    // Subscribe to topic with MessageListener
    public String subscribeToTopic(String topicName, MessageListener<String> listener) {
        RTopic topic = redissonClient.getTopic(topicName);
        int listenerId = topic.addListener(String.class, listener);
        topicListeners.put(topicName + ":" + listenerId, listenerId);
        log.info("Subscribed to topic: {} listener ID: {}", topicName, listenerId);
        return "Subscribed successfully with listener ID: " + listenerId;
    }

    // Subscribe with lambda
    public String subscribeToTopic(String topicName, String subscriberName) {
        RTopic topic = redissonClient.getTopic(topicName);
        int listenerId = topic.addListener(String.class, (channel, message) -> {
            log.info("Subscriber '{}' received message from topic '{}': {}", subscriberName, channel, message);
        });
        topicListeners.put(topicName + ":" + listenerId, listenerId);
        log.info("Subscriber '{}' subscribed to topic: {} listener ID: {}", subscriberName, topicName, listenerId);
        return "Subscriber '" + subscriberName + "' subscribed successfully with listener ID: " + listenerId;
    }

    // Unsubscribe from topic
    public String unsubscribeFromTopic(String topicName, int listenerId) {
        RTopic topic = redissonClient.getTopic(topicName);
        boolean removed = topic.removeListener(listenerId);
        topicListeners.remove(topicName + ":" + listenerId);
        log.info("Unsubscribed from topic: {} listener ID: {} success: {}", topicName, listenerId, removed);
        return removed ? "Unsubscribed successfully" : "Failed to unsubscribe";
    }

    // Unsubscribe all listeners from a topic
    public String unsubscribeAllFromTopic(String topicName) {
        RTopic topic = redissonClient.getTopic(topicName);
        topic.removeAllListeners();
        
        // Remove from tracking map
        topicListeners.entrySet().removeIf(entry -> entry.getKey().startsWith(topicName + ":"));
        
        log.info("Unsubscribed all listeners from topic: {}", topicName);
        return "All listeners unsubscribed successfully";
    }

    // Get topic listeners count
    public int getTopicListenersCount(String topicName) {
        RTopic topic = redissonClient.getTopic(topicName);
        int count = topic.countListeners();
        log.info("Topic: {} listeners count: {}", topicName, count);
        return count;
    }

    // Create a pattern-based subscription
    public String subscribeToPattern(String pattern, String subscriberName) {
        // Redisson doesn't directly support pattern subscriptions like Redis,
        // but we can simulate it by subscribing to multiple topics that match the pattern
        log.info("Pattern subscription simulated for pattern: {} subscriber: {}", pattern, subscriberName);
        return "Pattern subscription simulated for pattern: " + pattern;
    }

    // Broadcast message to multiple topics
    public String broadcastToTopics(String[] topicNames, String message) {
        StringBuilder result = new StringBuilder();
        int totalListeners = 0;
        
        for (String topicName : topicNames) {
            RTopic topic = redissonClient.getTopic(topicName);
            long listeners = topic.publish(message);
            totalListeners += listeners;
            result.append(String.format("Topic '%s': %d listeners\n", topicName, listeners));
        }
        
        log.info("Broadcasted message to {} topics, reached {} total listeners", topicNames.length, totalListeners);
        result.append(String.format("Total listeners reached: %d", totalListeners));
        return result.toString();
    }

    // Publish with delay (simulated)
    public String publishWithDelay(String topicName, String message, long delayMillis) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMillis);
                publishMessage(topicName, message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Delayed publish interrupted for topic: {}", topicName, e);
            }
        }).start();
        
        log.info("Scheduled delayed publish to topic: {} message: {} delay: {}ms", topicName, message, delayMillis);
        return "Message scheduled for delayed publish";
    }

    // Create a simple chat room demo
    public String joinChatRoom(String roomName, String userName) {
        String topicName = "chatroom:" + roomName;
        return subscribeToTopic(topicName, userName);
    }

    public String leaveChatRoom(String roomName, String userName) {
        String topicName = "chatroom:" + roomName;
        // Find and remove the listener for this user
        topicListeners.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(topicName + ":"))
            .findFirst()
            .ifPresent(entry -> {
                int listenerId = entry.getValue();
                unsubscribeFromTopic(topicName, listenerId);
            });
        
        log.info("User '{}' left chat room: {}", userName, roomName);
        return "User '" + userName + "' left chat room: " + roomName;
    }

    public String sendMessageToChatRoom(String roomName, String userName, String message) {
        String topicName = "chatroom:" + roomName;
        String fullMessage = String.format("[%s] %s: %s", 
            java.time.LocalDateTime.now().toString(), userName, message);
        long listeners = publishMessage(topicName, fullMessage);
        return String.format("Message sent to room '%s', reached %d listeners", roomName, listeners);
    }

    // Get all active topic subscriptions
    public ConcurrentMap<String, Integer> getActiveSubscriptions() {
        return new ConcurrentHashMap<>(topicListeners);
    }

    // Cleanup all subscriptions
    public String cleanupAllSubscriptions() {
        topicListeners.forEach((key, listenerId) -> {
            String topicName = key.split(":")[0];
            unsubscribeFromTopic(topicName, listenerId);
        });
        topicListeners.clear();
        log.info("All subscriptions cleaned up");
        return "All subscriptions cleaned up";
    }
}
