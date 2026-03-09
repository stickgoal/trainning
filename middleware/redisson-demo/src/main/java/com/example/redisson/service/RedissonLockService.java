package com.example.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonLockService {

    private final RedissonClient redissonClient;
    private final AtomicInteger counter = new AtomicInteger(0);

    // Basic Lock
    public String tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean acquired = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (acquired) {
                log.info("Lock acquired for key: {}", lockKey);
                return "Lock acquired successfully";
            } else {
                log.warn("Failed to acquire lock for key: {}", lockKey);
                return "Failed to acquire lock";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while trying to acquire lock: {}", lockKey, e);
            return "Interrupted while trying to acquire lock";
        }
    }

    public String unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Lock released for key: {}", lockKey);
                return "Lock released successfully";
            } else {
                log.warn("Lock not held by current thread for key: {}", lockKey);
                return "Lock not held by current thread";
            }
        } catch (IllegalMonitorStateException e) {
            log.error("Failed to unlock lock: {}", lockKey, e);
            return "Failed to unlock lock - not locked by current thread";
        }
    }

    // Lock with automatic execution
    public String executeWithLock(String lockKey, long leaseTime, TimeUnit timeUnit, Runnable task) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock(leaseTime, timeUnit);
            log.info("Executing task with lock: {}", lockKey);
            task.run();
            log.info("Task completed with lock: {}", lockKey);
            return "Task executed successfully";
        } catch (Exception e) {
            log.error("Error executing task with lock: {}", lockKey, e);
            return "Error executing task: " + e.getMessage();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Lock released after task completion: {}", lockKey);
            }
        }
    }

    // ReadWriteLock
    public String readWithLock(String lockKey, Runnable readTask) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock readLock = rwLock.readLock();
        try {
            readLock.lock();
            log.info("Acquired read lock for key: {}", lockKey);
            readTask.run();
            log.info("Read operation completed for key: {}", lockKey);
            return "Read operation completed successfully";
        } catch (Exception e) {
            log.error("Error during read operation with lock: {}", lockKey, e);
            return "Error during read operation: " + e.getMessage();
        } finally {
            if (readLock.isHeldByCurrentThread()) {
                readLock.unlock();
                log.info("Read lock released for key: {}", lockKey);
            }
        }
    }

    public String writeWithLock(String lockKey, Runnable writeTask) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock writeLock = rwLock.writeLock();
        try {
            writeLock.lock();
            log.info("Acquired write lock for key: {}", lockKey);
            writeTask.run();
            log.info("Write operation completed for key: {}", lockKey);
            return "Write operation completed successfully";
        } catch (Exception e) {
            log.error("Error during write operation with lock: {}", lockKey, e);
            return "Error during write operation: " + e.getMessage();
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
                log.info("Write lock released for key: {}", lockKey);
            }
        }
    }

    // Fair Lock
    public String tryFairLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        try {
            boolean acquired = fairLock.tryLock(waitTime, leaseTime, timeUnit);
            if (acquired) {
                log.info("Fair lock acquired for key: {}", lockKey);
                return "Fair lock acquired successfully";
            } else {
                log.warn("Failed to acquire fair lock for key: {}", lockKey);
                return "Failed to acquire fair lock";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while trying to acquire fair lock: {}", lockKey, e);
            return "Interrupted while trying to acquire fair lock";
        }
    }

    // MultiLock
    public String tryMultiLock(String[] lockKeys, long waitTime, long leaseTime, TimeUnit timeUnit) {
        RLock[] locks = new RLock[lockKeys.length];
        for (int i = 0; i < lockKeys.length; i++) {
            locks[i] = redissonClient.getLock(lockKeys[i]);
        }
        
        RLock multiLock = redissonClient.getMultiLock(locks);
        try {
            boolean acquired = multiLock.tryLock(waitTime, leaseTime, timeUnit);
            if (acquired) {
                log.info("Multi-lock acquired for keys: {}", (Object) lockKeys);
                return "Multi-lock acquired successfully";
            } else {
                log.warn("Failed to acquire multi-lock for keys: {}", (Object) lockKeys);
                return "Failed to acquire multi-lock";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while trying to acquire multi-lock: {}", (Object) lockKeys, e);
            return "Interrupted while trying to acquire multi-lock";
        }
    }

    // Lock status
    public boolean isLocked(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = lock.isLocked();
        log.info("Lock status for key: {} isLocked: {}", lockKey, locked);
        return locked;
    }

    public boolean isHeldByCurrentThread(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean held = lock.isHeldByCurrentThread();
        log.info("Lock held by current thread for key: {} isHeld: {}", lockKey, held);
        return held;
    }

    public int getHoldCount(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        int count = lock.getHoldCount();
        log.info("Lock hold count for key: {} count: {}", lockKey, count);
        return count;
    }

    // Counter with lock
    public int incrementCounter(String counterKey) {
        RLock lock = redissonClient.getLock(counterKey + ":lock");
        try {
            lock.lock();
            int newValue = counter.incrementAndGet();
            log.info("Incremented counter: {} new value: {}", counterKey, newValue);
            return newValue;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public int getCounter(String counterKey) {
        return counter.get();
    }

    public void resetCounter(String counterKey) {
        RLock lock = redissonClient.getLock(counterKey + ":lock");
        try {
            lock.lock();
            counter.set(0);
            log.info("Reset counter: {} to 0", counterKey);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
