package me.maiz.se.mini.multithreading.juc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RWLockDemo {
    public static void main(String[] args) {
        Runnable put = () -> {
            //1秒放入1次，此时写锁被获取
            for (int i = 0; i < 5; i++) {
                Cache.put("k" + i, "v" + i);
                System.out.println(System.currentTimeMillis()+" 写入k"+i+"结束");
            }
        };
        Runnable get = () -> {
            //不停获取20，此时会获取读锁
            for (int i = 0; i < 10; i++) {
                System.out.println(System.currentTimeMillis()+"读取到："+Cache.get("k0"));
            }
        };

        new Thread(put).start();
        new Thread(get).start();
    }
}

class Cache {
    //缓存存储
    private static Map<String, Object> store = new HashMap<>();
    //读写锁
    private static ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
    ;
    //获得读锁
    private static Lock rLock = rwlock.readLock();
    //获得写锁
    private static Lock wLock = rwlock.writeLock();

    public static final void put(String k, Object v) {
        wLock.lock();
        try {
            store.put(k, v);
        } finally {
            wLock.unlock();
        }
    }

    public static final Object get(String k) {
        rLock.lock();
        try {
            return store.get(k);
        } finally {
            rLock.unlock();
        }
    }

}