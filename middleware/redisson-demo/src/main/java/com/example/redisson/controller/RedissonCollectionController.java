package com.example.redisson.controller;

import com.example.redisson.service.RedissonCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redisson/collection")
@RequiredArgsConstructor
@Slf4j
public class RedissonCollectionController {

    private final RedissonCollectionService redissonCollectionService;

    // RMap operations
    @PostMapping("/map")
    public ResponseEntity<String> putToMap(@RequestParam String mapKey, @RequestParam String key, @RequestParam String value) {
        String result = redissonCollectionService.putToMap(mapKey, key, value);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/map")
    public ResponseEntity<String> getFromMap(@RequestParam String mapKey, @RequestParam String key) {
        String result = redissonCollectionService.getFromMap(mapKey, key);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/map/all")
    public ResponseEntity<Map<String, String>> getAllFromMap(@RequestParam String mapKey) {
        Map<String, String> result = redissonCollectionService.getAllFromMap(mapKey);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/map")
    public ResponseEntity<String> removeFromMap(@RequestParam String mapKey, @RequestParam String key) {
        String result = redissonCollectionService.removeFromMap(mapKey, key);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/map/size")
    public ResponseEntity<Integer> getMapSize(@RequestParam String mapKey) {
        int size = redissonCollectionService.getMapSize(mapKey);
        return ResponseEntity.ok(size);
    }

    @PostMapping("/map/ttl")
    public ResponseEntity<String> putToMapWithTTL(
            @RequestParam String mapKey,
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam long ttl,
            @RequestParam TimeUnit timeUnit) {
        String result = redissonCollectionService.putToMapWithTTL(mapKey, key, value, ttl, timeUnit);
        return ResponseEntity.ok(result);
    }

    // RSet operations
    @PostMapping("/set")
    public ResponseEntity<Boolean> addToSet(@RequestParam String setKey, @RequestParam String value) {
        boolean result = redissonCollectionService.addToSet(setKey, value);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/set")
    public ResponseEntity<Boolean> removeFromSet(@RequestParam String setKey, @RequestParam String value) {
        boolean result = redissonCollectionService.removeFromSet(setKey, value);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/set")
    public ResponseEntity<Set<String>> getAllFromSet(@RequestParam String setKey) {
        Set<String> result = redissonCollectionService.getAllFromSet(setKey);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/set/size")
    public ResponseEntity<Integer> getSetSize(@RequestParam String setKey) {
        int size = redissonCollectionService.getSetSize(setKey);
        return ResponseEntity.ok(size);
    }

    // Set operations (union, intersection)
    @PostMapping("/set/union")
    public ResponseEntity<Set<String>> setUnion(
            @RequestParam String setKey1,
            @RequestParam String setKey2,
            @RequestParam String resultKey) {
        Set<String> result = redissonCollectionService.setUnion(setKey1, setKey2, resultKey);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/set/intersection")
    public ResponseEntity<Set<String>> setIntersection(
            @RequestParam String setKey1,
            @RequestParam String setKey2,
            @RequestParam String resultKey) {
        Set<String> result = redissonCollectionService.setIntersection(setKey1, setKey2, resultKey);
        return ResponseEntity.ok(result);
    }

    // RList operations
    @PostMapping("/list")
    public ResponseEntity<Boolean> addToList(@RequestParam String listKey, @RequestParam String value) {
        boolean result = redissonCollectionService.addToList(listKey, value);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<String> getFromList(@RequestParam String listKey, @RequestParam int index) {
        String result = redissonCollectionService.getFromList(listKey, index);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<String>> getAllFromList(@RequestParam String listKey) {
        List<String> result = redissonCollectionService.getAllFromList(listKey);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/list")
    public ResponseEntity<String> removeFromList(@RequestParam String listKey, @RequestParam int index) {
        String result = redissonCollectionService.removeFromList(listKey, index);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list/size")
    public ResponseEntity<Integer> getListSize(@RequestParam String listKey) {
        int size = redissonCollectionService.getListSize(listKey);
        return ResponseEntity.ok(size);
    }

    // RQueue operations
    @PostMapping("/queue")
    public ResponseEntity<Boolean> offerToQueue(@RequestParam String queueKey, @RequestParam String value) {
        boolean result = redissonCollectionService.offerToQueue(queueKey, value);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/queue")
    public ResponseEntity<String> pollFromQueue(@RequestParam String queueKey) {
        String result = redissonCollectionService.pollFromQueue(queueKey);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/queue/peek")
    public ResponseEntity<String> peekFromQueue(@RequestParam String queueKey) {
        String result = redissonCollectionService.peekFromQueue(queueKey);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/queue/size")
    public ResponseEntity<Integer> getQueueSize(@RequestParam String queueKey) {
        int size = redissonCollectionService.getQueueSize(queueKey);
        return ResponseEntity.ok(size);
    }

    // RScoredSortedSet operations
    @PostMapping("/sortedset")
    public ResponseEntity<Boolean> addToSortedSet(@RequestParam String sortedSetKey, @RequestParam String member, @RequestParam double score) {
        boolean result = redissonCollectionService.addToSortedSet(sortedSetKey, member, score);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sortedset")
    public ResponseEntity<Collection<org.redisson.client.protocol.ScoredEntry<String>>> getRangeFromSortedSet(
            @RequestParam String sortedSetKey,
            @RequestParam int startIndex,
            @RequestParam int endIndex) {
        Collection<org.redisson.client.protocol.ScoredEntry<String>> result = redissonCollectionService.getRangeFromSortedSet(sortedSetKey, startIndex, endIndex);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sortedset/score")
    public ResponseEntity<Double> getScoreFromSortedSet(@RequestParam String sortedSetKey, @RequestParam String member) {
        Double result = redissonCollectionService.getScoreFromSortedSet(sortedSetKey, member);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/sortedset")
    public ResponseEntity<Boolean> removeFromSortedSet(@RequestParam String sortedSetKey, @RequestParam String member) {
        boolean result = redissonCollectionService.removeFromSortedSet(sortedSetKey, member);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sortedset/size")
    public ResponseEntity<Integer> getSortedSetSize(@RequestParam String sortedSetKey) {
        int size = redissonCollectionService.getSortedSetSize(sortedSetKey);
        return ResponseEntity.ok(size);
    }

    // RDeque operations
    @PostMapping("/deque/first")
    public ResponseEntity<Void> addFirstToDeque(@RequestParam String dequeKey, @RequestParam String value) {
        redissonCollectionService.addFirstToDeque(dequeKey, value);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deque/last")
    public ResponseEntity<Void> addLastToDeque(@RequestParam String dequeKey, @RequestParam String value) {
        redissonCollectionService.addLastToDeque(dequeKey, value);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/deque/first")
    public ResponseEntity<String> getFirstFromDeque(@RequestParam String dequeKey) {
        String result = redissonCollectionService.getFirstFromDeque(dequeKey);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/deque/last")
    public ResponseEntity<String> getLastFromDeque(@RequestParam String dequeKey) {
        String result = redissonCollectionService.getLastFromDeque(dequeKey);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/deque/first")
    public ResponseEntity<String> removeFirstFromDeque(@RequestParam String dequeKey) {
        String result = redissonCollectionService.removeFirstFromDeque(dequeKey);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/deque/last")
    public ResponseEntity<String> removeLastFromDeque(@RequestParam String dequeKey) {
        String result = redissonCollectionService.removeLastFromDeque(dequeKey);
        return ResponseEntity.ok(result);
    }

    // Cleanup operations
    @DeleteMapping("/collection")
    public ResponseEntity<String> deleteCollection(@RequestParam String collectionKey) {
        String result = redissonCollectionService.deleteCollection(collectionKey);
        return ResponseEntity.ok(result);
    }
}
