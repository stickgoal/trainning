package com.example.redisson.controller;

import com.example.redisson.service.RedissonBasicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redisson/basic")
@RequiredArgsConstructor
@Slf4j
public class RedissonBasicController {

    private final RedissonBasicService redissonBasicService;

    // String operations
    @PostMapping("/string")
    public ResponseEntity<String> setString(@RequestParam String key, @RequestParam String value) {
        String result = redissonBasicService.setString(key, value);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/string/ttl")
    public ResponseEntity<String> setStringWithTTL(
            @RequestParam String key, 
            @RequestParam String value,
            @RequestParam long ttl,
            @RequestParam TimeUnit timeUnit) {
        String result = redissonBasicService.setStringWithTTL(key, value, ttl, timeUnit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/string")
    public ResponseEntity<String> getString(@RequestParam String key) {
        String value = redissonBasicService.getString(key);
        return ResponseEntity.ok(value);
    }

    // Hash operations
    @PostMapping("/hash")
    public ResponseEntity<String> setHashField(@RequestParam String key, @RequestParam String field, @RequestParam String value) {
        String result = redissonBasicService.setHashField(key, field, value);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/hash")
    public ResponseEntity<String> getHashField(@RequestParam String key, @RequestParam String field) {
        String value = redissonBasicService.getHashField(key, field);
        return ResponseEntity.ok(value);
    }

    @GetMapping("/hash/all")
    public ResponseEntity<Map<String, String>> getAllHashFields(@RequestParam String key) {
        Map<String, String> result = redissonBasicService.getAllHashFields(key);
        return ResponseEntity.ok(result);
    }

    // List operations
    @PostMapping("/list")
    public ResponseEntity<String> addToList(@RequestParam String key, @RequestParam String[] values) {
        String result = redissonBasicService.addToList(key, values);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> getList(@RequestParam String key) {
        List<String> result = redissonBasicService.getList(key);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/list")
    public ResponseEntity<String> removeFromList(@RequestParam String key, @RequestParam String value) {
        String result = redissonBasicService.removeFromList(key, value);
        return ResponseEntity.ok(result);
    }

    // Set operations
    @PostMapping("/set")
    public ResponseEntity<String> addToSet(@RequestParam String key, @RequestParam String[] values) {
        String result = redissonBasicService.addToSet(key, values);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/set")
    public ResponseEntity<Set<String>> getSet(@RequestParam String key) {
        Set<String> result = redissonBasicService.getSet(key);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/set")
    public ResponseEntity<String> removeFromSet(@RequestParam String key, @RequestParam String value) {
        String result = redissonBasicService.removeFromSet(key, value);
        return ResponseEntity.ok(result);
    }

    // Sorted Set operations
    @PostMapping("/sortedset")
    public ResponseEntity<String> addToSortedSet(@RequestParam String key, @RequestBody Map<String, Double> scoreMembers) {
        String result = redissonBasicService.addToSortedSet(key, scoreMembers);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sortedset")
    public ResponseEntity<Set<String>> getSortedSetRange(@RequestParam String key, @RequestParam int start, @RequestParam int end) {
        Set<String> result = redissonBasicService.getSortedSetRange(key, start, end);
        return ResponseEntity.ok(result);
    }

    // Queue operations
    @PostMapping("/queue")
    public ResponseEntity<String> enqueue(@RequestParam String key, @RequestParam String value) {
        String result = redissonBasicService.enqueue(key, value);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/queue")
    public ResponseEntity<String> dequeue(@RequestParam String key) {
        String result = redissonBasicService.dequeue(key);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/queue/size")
    public ResponseEntity<Integer> getQueueSize(@RequestParam String key) {
        int size = redissonBasicService.getQueueSize(key);
        return ResponseEntity.ok(size);
    }

    // Batch operations
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchOperations(@RequestBody Map<String, String> operations) {
        Map<String, Object> result = redissonBasicService.batchOperations(operations);
        return ResponseEntity.ok(result);
    }

    // Utility operations
    @DeleteMapping("/key")
    public ResponseEntity<String> deleteKey(@RequestParam String key) {
        String result = redissonBasicService.deleteKey(key);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/key/exists")
    public ResponseEntity<Boolean> keyExists(@RequestParam String key) {
        boolean exists = redissonBasicService.keyExists(key);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/keys")
    public ResponseEntity<Set<String>> findKeysByPattern(@RequestParam String pattern) {
        Set<String> result = redissonBasicService.findKeysByPattern(pattern);
        return ResponseEntity.ok(result);
    }
}
