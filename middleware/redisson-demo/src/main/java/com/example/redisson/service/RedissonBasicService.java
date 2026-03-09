package com.example.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RDeque;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import org.redisson.api.RSet;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonBasicService {

    private final RedissonClient redissonClient;

    // String operations
    public String setString(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value);
        log.info("Set key: {} with value: {}", key, value);
        return "OK";
    }

    public String setStringWithTTL(String key, String value, long ttl, TimeUnit timeUnit) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value, ttl, timeUnit);
        log.info("Set key: {} with value: {} and TTL: {} {}", key, value, ttl, timeUnit);
        return "OK";
    }

    public String getString(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        String value = bucket.get();
        log.info("Get key: {} value: {}", key, value);
        return value;
    }

    // Hash operations
    public String setHashField(String key, String field, String value) {
        RMap<String, String> map = redissonClient.getMap(key);
        map.put(field, value);
        log.info("Set hash key: {} field: {} value: {}", key, field, value);
        return "OK";
    }

    public String getHashField(String key, String field) {
        RMap<String, String> map = redissonClient.getMap(key);
        String value = map.get(field);
        log.info("Get hash key: {} field: {} value: {}", key, field, value);
        return value;
    }

    public Map<String, String> getAllHashFields(String key) {
        RMap<String, String> map = redissonClient.getMap(key);
        Map<String, String> result = map.readAllMap();
        log.info("Get all hash fields for key: {} result: {}", key, result);
        return result;
    }

    // List operations
    public String addToList(String key, String... values) {
        RList<String> list = redissonClient.getList(key);
        list.addAll(Arrays.asList(values));
        log.info("Added to list key: {} values: {}", key, Arrays.toString(values));
        return "OK";
    }

    public List<String> getList(String key) {
        RList<String> list = redissonClient.getList(key);
        List<String> result = list.readAll();
        log.info("Get list key: {} result: {}", key, result);
        return result;
    }

    public String removeFromList(String key, String value) {
        RList<String> list = redissonClient.getList(key);
        boolean removed = list.remove(value);
        log.info("Remove from list key: {} value: {} success: {}", key, value, removed);
        return removed ? "OK" : "Not found";
    }

    // Set operations
    public String addToSet(String key, String... values) {
        RSet<String> set = redissonClient.getSet(key);
        set.addAll(Arrays.asList(values));
        log.info("Added to set key: {} values: {}", key, Arrays.toString(values));
        return "OK";
    }

    public Set<String> getSet(String key) {
        RSet<String> set = redissonClient.getSet(key);
        Set<String> result = set.readAll();
        log.info("Get set key: {} result: {}", key, result);
        return result;
    }

    public String removeFromSet(String key, String value) {
        RSet<String> set = redissonClient.getSet(key);
        boolean removed = set.remove(value);
        log.info("Remove from set key: {} value: {} success: {}", key, value, removed);
        return removed ? "OK" : "Not found";
    }

    // Sorted Set operations
    public String addToSortedSet(String key, Map<String, Double> scoreMembers) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(key);
        scoreMembers.forEach(sortedSet::add);
        log.info("Added to sorted set key: {} values: {}", key, scoreMembers);
        return "OK";
    }

    public Set<String> getSortedSetRange(String key, int start, int end) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(key);
        Set<String> result = sortedSet.range(start, end);
        log.info("Get sorted set range key: {} start: {} end: {} result: {}", key, start, end, result);
        return result;
    }

    // Queue operations
    public String enqueue(String key, String value) {
        RQueue<String> queue = redissonClient.getQueue(key);
        queue.add(value);
        log.info("Enqueue to key: {} value: {}", key, value);
        return "OK";
    }

    public String dequeue(String key) {
        RQueue<String> queue = redissonClient.getQueue(key);
        String value = queue.poll();
        log.info("Dequeue from key: {} value: {}", key, value);
        return value;
    }

    public int getQueueSize(String key) {
        RQueue<String> queue = redissonClient.getQueue(key);
        int size = queue.size();
        log.info("Get queue size key: {} size: {}", key, size);
        return size;
    }

    // Batch operations
    public Map<String, Object> batchOperations(Map<String, String> operations) {
        RBatch batch = redissonClient.createBatch();
        
        operations.forEach((key, value) -> {
            batch.getBucket(key).setAsync(value);
        });
        
        batch.execute();
        log.info("Batch operations completed for {} keys", operations.size());
        return operations;
    }

    // Cleanup operations
    public String deleteKey(String key) {
        boolean deleted = redissonClient.getKeys().delete(key) > 0;
        log.info("Delete key: {} success: {}", key, deleted);
        return deleted ? "OK" : "Key not found";
    }

    public boolean keyExists(String key) {
        boolean exists = redissonClient.getKeys().countExists(key) > 0;
        log.info("Key exists: {} result: {}", key, exists);
        return exists;
    }

    public Set<String> findKeysByPattern(String pattern) {
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(pattern);
        Set<String> result = redissonClient.getSet("temp_keys");
        keys.forEach(result::add);
        log.info("Find keys by pattern: {} result: {}", pattern, result);
        return result;
    }
}
