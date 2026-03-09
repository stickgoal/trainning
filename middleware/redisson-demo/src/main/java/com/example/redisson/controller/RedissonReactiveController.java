package com.example.redisson.controller;

import com.example.redisson.service.RedissonReactiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redisson/reactive")
@RequiredArgsConstructor
@Slf4j
public class RedissonReactiveController {

    private final RedissonReactiveService redissonReactiveService;

    // Reactive Bucket operations
    @PostMapping("/string")
    public Mono<ResponseEntity<String>> setStringReactive(@RequestParam String key, @RequestParam String value) {
        return redissonReactiveService.setStringReactive(key, value)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/string/ttl")
    public Mono<ResponseEntity<String>> setStringWithTTLReactive(
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam long ttlSeconds) {
        return redissonReactiveService.setStringWithTTLReactive(key, value, ttlSeconds)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/string")
    public Mono<ResponseEntity<String>> getStringReactive(@RequestParam String key) {
        return redissonReactiveService.getStringReactive(key)
                .map(ResponseEntity::ok);
    }

    // Reactive Map operations
    @PostMapping("/map")
    public Mono<ResponseEntity<String>> putToMapReactive(@RequestParam String mapKey, @RequestParam String field, @RequestParam String value) {
        return redissonReactiveService.putToMapReactive(mapKey, field, value)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/map")
    public Mono<ResponseEntity<String>> getFromMapReactive(@RequestParam String mapKey, @RequestParam String field) {
        return redissonReactiveService.getFromMapReactive(mapKey, field)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/map/all")
    public Mono<ResponseEntity<Map<String, String>>> getAllFromMapReactive(@RequestParam String mapKey) {
        return redissonReactiveService.getAllFromMapReactive(mapKey)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/map")
    public Mono<ResponseEntity<String>> removeFromMapReactive(@RequestParam String mapKey, @RequestParam String field) {
        return redissonReactiveService.removeFromMapReactive(mapKey, field)
                .map(ResponseEntity::ok);
    }

    // Reactive List operations
    @PostMapping("/list")
    public Mono<ResponseEntity<Boolean>> addToListReactive(@RequestParam String listKey, @RequestParam String value) {
        return redissonReactiveService.addToListReactive(listKey, value)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/list")
    public Mono<ResponseEntity<String>> getFromListReactive(@RequestParam String listKey, @RequestParam int index) {
        return redissonReactiveService.getFromListReactive(listKey, index)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/list/all")
    public Flux<String> getAllFromListReactive(@RequestParam String listKey) {
        return redissonReactiveService.getAllFromListReactive(listKey);
    }

    // Reactive Set operations
    @PostMapping("/set")
    public Mono<ResponseEntity<Boolean>> addToSetReactive(@RequestParam String setKey, @RequestParam String value) {
        return redissonReactiveService.addToSetReactive(setKey, value)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/set")
    public Flux<String> getAllFromSetReactive(@RequestParam String setKey) {
        return redissonReactiveService.getAllFromSetReactive(setKey);
    }

    @DeleteMapping("/set")
    public Mono<ResponseEntity<Boolean>> removeFromSetReactive(@RequestParam String setKey, @RequestParam String value) {
        return redissonReactiveService.removeFromSetReactive(setKey, value)
                .map(ResponseEntity::ok);
    }

    // Reactive batch operations
    @PostMapping("/batch")
    public Mono<ResponseEntity<Void>> batchSetReactive(@RequestBody Map<String, String> keyValues) {
        return redissonReactiveService.batchSetReactive(keyValues)
                .map(ResponseEntity::ok);
    }

    // Reactive chain operations
    @PostMapping("/chain")
    public Mono<ResponseEntity<String>> reactiveChainExample(@RequestParam String key) {
        return redissonReactiveService.reactiveChainExample(key)
                .map(ResponseEntity::ok);
    }

    // Reactive timeout example
    @GetMapping("/timeout")
    public Mono<ResponseEntity<String>> reactiveTimeoutExample(@RequestParam String key) {
        return redissonReactiveService.reactiveTimeoutExample(key)
                .map(ResponseEntity::ok);
    }

    // Reactive retry example
    @GetMapping("/retry")
    public Mono<ResponseEntity<String>> reactiveRetryExample(@RequestParam String key, @RequestParam(defaultValue = "3") int maxRetries) {
        return redissonReactiveService.reactiveRetryExample(key, maxRetries)
                .map(ResponseEntity::ok);
    }

    // Reactive stream processing example
    @PostMapping("/stream")
    public Flux<String> reactiveStreamExample(@RequestParam String listKey, @RequestParam String[] values) {
        return redissonReactiveService.reactiveStreamExample(listKey, values);
    }

    // Reactive backpressure example
    @PostMapping("/backpressure")
    public Flux<String> reactiveBackpressureExample(@RequestParam String prefix, @RequestParam(defaultValue = "10") int count) {
        return redissonReactiveService.reactiveBackpressureExample(prefix, count);
    }

    // Reactive combine example
    @GetMapping("/combine")
    public Mono<ResponseEntity<Map<String, Object>>> reactiveCombineExample(@RequestParam String key1, @RequestParam String key2) {
        return redissonReactiveService.reactiveCombineExample(key1, key2)
                .map(ResponseEntity::ok);
    }

    // Reactive cache warming example
    @PostMapping("/cache/warm")
    public Mono<ResponseEntity<Void>> reactiveCacheWarming(@RequestParam String prefix, @RequestBody List<String> keys) {
        return redissonReactiveService.reactiveCacheWarming(prefix, keys)
                .map(ResponseEntity::ok);
    }

    // Reactive cleanup example
    @DeleteMapping("/cleanup")
    public Mono<ResponseEntity<Void>> reactiveCleanup(@RequestParam String[] keys) {
        return redissonReactiveService.reactiveCleanup(keys)
                .map(ResponseEntity::ok);
    }
}
