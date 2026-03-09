package com.example.redisson.controller;

import com.example.redisson.service.RedissonLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redisson/lock")
@RequiredArgsConstructor
@Slf4j
public class RedissonLockController {

    private final RedissonLockService redissonLockService;

    // Basic Lock operations
    @PostMapping("/try")
    public ResponseEntity<String> tryLock(
            @RequestParam String lockKey,
            @RequestParam long waitTime,
            @RequestParam long leaseTime,
            @RequestParam TimeUnit timeUnit) {
        String result = redissonLockService.tryLock(lockKey, waitTime, leaseTime, timeUnit);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/unlock")
    public ResponseEntity<String> unlock(@RequestParam String lockKey) {
        String result = redissonLockService.unlock(lockKey);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/execute")
    public ResponseEntity<String> executeWithLock(
            @RequestParam String lockKey,
            @RequestParam long leaseTime,
            @RequestParam TimeUnit timeUnit,
            @RequestParam String taskMessage) {
        String result = redissonLockService.executeWithLock(lockKey, leaseTime, timeUnit, () -> {
            log.info("Executing task: {}", taskMessage);
            try {
                Thread.sleep(1000); // Simulate task execution
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return ResponseEntity.ok(result);
    }

    // ReadWriteLock operations
    @PostMapping("/read")
    public ResponseEntity<String> readWithLock(@RequestParam String lockKey, @RequestParam String readMessage) {
        String result = redissonLockService.readWithLock(lockKey, () -> {
            log.info("Reading: {}", readMessage);
            try {
                Thread.sleep(500); // Simulate read operation
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return ResponseEntity.ok(result);
    }

    @PostMapping("/write")
    public ResponseEntity<String> writeWithLock(@RequestParam String lockKey, @RequestParam String writeMessage) {
        String result = redissonLockService.writeWithLock(lockKey, () -> {
            log.info("Writing: {}", writeMessage);
            try {
                Thread.sleep(1000); // Simulate write operation
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return ResponseEntity.ok(result);
    }

    // Fair Lock operations
    @PostMapping("/fair/try")
    public ResponseEntity<String> tryFairLock(
            @RequestParam String lockKey,
            @RequestParam long waitTime,
            @RequestParam long leaseTime,
            @RequestParam TimeUnit timeUnit) {
        String result = redissonLockService.tryFairLock(lockKey, waitTime, leaseTime, timeUnit);
        return ResponseEntity.ok(result);
    }

    // MultiLock operations
    @PostMapping("/multi/try")
    public ResponseEntity<String> tryMultiLock(
            @RequestParam String[] lockKeys,
            @RequestParam long waitTime,
            @RequestParam long leaseTime,
            @RequestParam TimeUnit timeUnit) {
        String result = redissonLockService.tryMultiLock(lockKeys, waitTime, leaseTime, timeUnit);
        return ResponseEntity.ok(result);
    }

    // Lock status operations
    @GetMapping("/status/locked")
    public ResponseEntity<Boolean> isLocked(@RequestParam String lockKey) {
        boolean locked = redissonLockService.isLocked(lockKey);
        return ResponseEntity.ok(locked);
    }

    @GetMapping("/status/held")
    public ResponseEntity<Boolean> isHeldByCurrentThread(@RequestParam String lockKey) {
        boolean held = redissonLockService.isHeldByCurrentThread(lockKey);
        return ResponseEntity.ok(held);
    }

    @GetMapping("/status/holdcount")
    public ResponseEntity<Integer> getHoldCount(@RequestParam String lockKey) {
        int count = redissonLockService.getHoldCount(lockKey);
        return ResponseEntity.ok(count);
    }

    // Counter operations
    @PostMapping("/counter/increment")
    public ResponseEntity<Integer> incrementCounter(@RequestParam String counterKey) {
        int newValue = redissonLockService.incrementCounter(counterKey);
        return ResponseEntity.ok(newValue);
    }

    @GetMapping("/counter")
    public ResponseEntity<Integer> getCounter(@RequestParam String counterKey) {
        int value = redissonLockService.getCounter(counterKey);
        return ResponseEntity.ok(value);
    }

    @PostMapping("/counter/reset")
    public ResponseEntity<Void> resetCounter(@RequestParam String counterKey) {
        redissonLockService.resetCounter(counterKey);
        return ResponseEntity.ok().build();
    }
}
