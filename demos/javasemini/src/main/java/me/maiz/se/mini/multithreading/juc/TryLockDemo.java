package me.maiz.se.mini.multithreading.juc;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TryLockDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==测试tryLock==");
        Lock lock = new ReentrantLock();
        ProudWorker proudWorker1 = new ProudWorker(lock);
        proudWorker1.start();
        ProudWorker proudWorker2 = new ProudWorker(lock);
        proudWorker2.start();
        //等待两个线程都执行完，这里是为了下一步测试输出更为清晰
        proudWorker1.join();
        proudWorker2.join();
        System.out.println("==测试tryLock结束==");

        System.out.println("==测试tryLock超时==");
        LazyWorker lazyWorker1 = new LazyWorker(lock);
        lazyWorker1.start();
        LazyWorker lazyWorker2 = new LazyWorker(lock);
        lazyWorker2.start();

    }
}
//傲娇的worker，一旦得不到锁就不执行
class ProudWorker extends Thread {
    private Lock lock;

    public ProudWorker(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();

        //尝试得到锁，如果得不到就返回false
        if (lock.tryLock()) {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(name + "得到锁，执行");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(name + "没有得到锁，不执行");
        }

    }
}

//懒惰的worker，一旦得不到锁就等着
class LazyWorker extends Thread {
    private Lock lock;

    public LazyWorker(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();

        //尝试得到锁，如果得不到则等待10秒
        //等待期间如果锁被释放就可以尝试获得，获得锁的概率比直接tryLock要高
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(name +"在"+System.currentTimeMillis() +"时得到锁，执行");
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println(name + "没有得到锁，不执行");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}