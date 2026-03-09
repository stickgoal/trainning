package com.example.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.StringCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonReactiveService {

    private final RedissonReactiveClient redissonReactiveClient;
    private final StringCodec stringCodec = new StringCodec();

    // Reactive Bucket operations
    public Mono<String> setStringReactive(String key, String value) {
        RBucketReactive<String> bucket = redissonReactiveClient.getBucket(key, stringCodec);
        return bucket.set(value)
                .doOnSuccess(v -> log.info("Reactive set key: {} with value: {}", key, value))
                .map(v -> "OK");
    }

    public Mono<String> setStringWithTTLReactive(String key, String value, long ttlSeconds) {
        RBucketReactive<String> bucket = redissonReactiveClient.getBucket(key, stringCodec);
        return bucket.set(value, Duration.ofSeconds(ttlSeconds))
                .doOnSuccess(v -> log.info("Reactive set key: {} with value: {} and TTL: {}s", key, value, ttlSeconds))
                .map(v -> "OK");
    }

    public Mono<String> getStringReactive(String key) {
        RBucketReactive<String> bucket = redissonReactiveClient.getBucket(key, stringCodec);
        return bucket.get()
                .doOnSuccess(value -> log.info("Reactive get key: {} value: {}", key, value));
    }

    // Reactive Map operations
    public Mono<String> putToMapReactive(String mapKey, String field, String value) {
        RMapReactive<String, String> map = redissonReactiveClient.getMap(mapKey, stringCodec);
        return map.put(field, value)
                .doOnSuccess(previous -> log.info("Reactive put to map: {} field: {} value: {} previous: {}", mapKey, field, value, previous));
    }

    public Mono<String> getFromMapReactive(String mapKey, String field) {
        RMapReactive<String, String> map = redissonReactiveClient.getMap(mapKey, stringCodec);
        return map.get(field)
                .doOnSuccess(value -> log.info("Reactive get from map: {} field: {} value: {}", mapKey, field, value));
    }

    public Mono<Map<String, String>> getAllFromMapReactive(String mapKey) {
        RMapReactive<String, String> map = redissonReactiveClient.getMap(mapKey, stringCodec);
        return map.readAllMap()
                .doOnSuccess(result -> log.info("Reactive get all from map: {} size: {}", mapKey, result.size()));
    }

    public Mono<String> removeFromMapReactive(String mapKey, String field) {
        RMapReactive<String, String> map = redissonReactiveClient.getMap(mapKey, stringCodec);
        return map.remove(field)
                .doOnSuccess(removed -> log.info("Reactive remove from map: {} field: {} removed: {}", mapKey, field, removed));
    }

    // Reactive List operations
    public Mono<Boolean> addToListReactive(String listKey, String value) {
        RListReactive<String> list = redissonReactiveClient.getList(listKey, stringCodec);
        return list.add(value)
                .doOnSuccess(added -> log.info("Reactive add to list: {} value: {} added: {}", listKey, value, added));
    }

    public Mono<String> getFromListReactive(String listKey, int index) {
        RListReactive<String> list = redissonReactiveClient.getList(listKey, stringCodec);
        return list.get(index)
                .doOnSuccess(value -> log.info("Reactive get from list: {} index: {} value: {}", listKey, index, value));
    }

    public Flux<String> getAllFromListReactive(String listKey) {
        RListReactive<String> list = redissonReactiveClient.getList(listKey, stringCodec);
        return list.readAll()
                .doOnComplete(() -> log.info("Reactive get all from list: {} completed", listKey));
    }

    // Reactive Set operations
    public Mono<Boolean> addToSetReactive(String setKey, String value) {
        RSetReactive<String> set = redissonReactiveClient.getSet(setKey, stringCodec);
        return set.add(value)
                .doOnSuccess(added -> log.info("Reactive add to set: {} value: {} added: {}", setKey, value, added));
    }

    public Flux<String> getAllFromSetReactive(String setKey) {
        RSetReactive<String> set = redissonReactiveClient.getSet(setKey, stringCodec);
        return set.readAll()
                .doOnComplete(() -> log.info("Reactive get all from set: {} completed", setKey));
    }

    public Mono<Boolean> removeFromSetReactive(String setKey, String value) {
        RSetReactive<String> set = redissonReactiveClient.getSet(setKey, stringCodec);
        return set.remove(value)
                .doOnSuccess(removed -> log.info("Reactive remove from set: {} value: {} removed: {}", setKey, value, removed));
    }

    // Reactive batch operations
    public Mono<Void> batchSetReactive(Map<String, String> keyValues) {
        List<Mono<String>> operations = keyValues.entrySet().stream()
                .map(entry -> setStringReactive(entry.getKey(), entry.getValue()))
                .toList();
        
        return Flux.merge(operations)
                .then()
                .doOnSuccess(v -> log.info("Reactive batch set completed for {} keys", keyValues.size()));
    }

    // Reactive chain operations example
    public Mono<String> reactiveChainExample(String key) {
        return setStringReactive(key, "initial")
                .then(getStringReactive(key))
                .flatMap(value -> setStringReactive(key, value + "_modified"))
                .then(getStringReactive(key))
                .doOnSuccess(finalValue -> log.info("Reactive chain example completed. Final value: {}", finalValue));
    }

    // Reactive timeout example
    public Mono<String> reactiveTimeoutExample(String key) {
        return getStringReactive(key)
                .timeout(Duration.ofSeconds(2))
                .onErrorResume(throwable -> {
                    log.warn("Timeout occurred while getting key: {}", key);
                    return Mono.just("Timeout occurred");
                })
                .doOnSuccess(result -> log.info("Reactive timeout example result: {}", result));
    }

    // Reactive retry example
    public Mono<String> reactiveRetryExample(String key, int maxRetries) {
        return getStringReactive(key)
                .retry(maxRetries)
                .doOnSuccess(result -> log.info("Reactive retry example succeeded after retries: {}", result))
                .onErrorResume(throwable -> {
                    log.error("Reactive retry example failed after {} retries", maxRetries, throwable);
                    return Mono.just("Failed after retries");
                });
    }

    // Reactive stream processing example
    public Flux<String> reactiveStreamExample(String listKey, String... values) {
        return Flux.fromArray(values)
                .flatMap(value -> addToListReactive(listKey, value))
                .thenMany(getAllFromListReactive(listKey))
                .doOnComplete(() -> log.info("Reactive stream example completed for list: {}", listKey));
    }

    // Reactive backpressure example
    public Flux<String> reactiveBackpressureExample(String prefix, int count) {
        return Flux.range(1, count)
                .delayElements(Duration.ofMillis(100)) // Simulate slow processing
                .map(i -> setStringReactive(prefix + i, "value_" + i))
                .flatMap(mono -> mono)
                .doOnComplete(() -> log.info("Reactive backpressure example completed for {} items", count));
    }

    // Reactive combine example
    public Mono<Map<String, Object>> reactiveCombineExample(String key1, String key2) {
        Mono<String> value1 = getStringReactive(key1);
        Mono<String> value2 = getStringReactive(key2);
        
        return Mono.zip(value1, value2)
                .map(tuple -> {
                    Map<String, Object> result = Map.of(
                        "key1", tuple.getT1(),
                        "key2", tuple.getT2(),
                        "combined", tuple.getT1() + "_" + tuple.getT2()
                    );
                    return result;
                })
                .doOnSuccess(result -> log.info("Reactive combine example result: {}", result));
    }

    // Reactive cache warming example
    public Mono<Void> reactiveCacheWarming(String prefix, List<String> keys) {
        return Flux.fromIterable(keys)
                .flatMap(key -> setStringReactive(prefix + key, "cached_value_" + key))
                .then()
                .doOnSuccess(v -> log.info("Reactive cache warming completed for {} keys", keys.size()));
    }

    // Reactive cleanup example
    public Mono<Void> reactiveCleanup(String... keys) {
        return Flux.fromArray(keys)
                .flatMap(key -> redissonReactiveClient.getBucket(key, stringCodec).delete())
                .then()
                .doOnSuccess(v -> log.info("Reactive cleanup completed for {} keys", keys.length));
    }
}
