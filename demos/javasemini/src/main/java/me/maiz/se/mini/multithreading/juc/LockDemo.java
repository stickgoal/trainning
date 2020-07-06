package me.maiz.se.mini.multithreading.juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        //启动两个线程，使用同一把锁来锁定
        new Worker(lock).start();
        new Worker(lock).start();
    }
}

//一个持有锁的线程类
class Worker extends Thread {
    private Lock lock;
    public Worker(Lock lock) {
        this.lock = lock;
    }
    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        //加锁
        lock.lock();
        try {
            //假装做一些事情...
            for (int i = 0; i < 5; i++) {
                System.out.println(name + "执行" + i);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //一定要解锁
            lock.unlock();
        }
    }
}
