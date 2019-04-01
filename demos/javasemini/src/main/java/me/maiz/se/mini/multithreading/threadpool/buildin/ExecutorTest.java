package me.maiz.se.mini.multithreading.threadpool.buildin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("+++++++++++缓存线程的线程池++++++++++");
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        //提交五个任务给线程池执行
        for (int i = 0; i < 5; i++) {
            cachedThreadPool.execute(new MyRunnable());
        }
        cachedThreadPool.shutdown();

        Thread.sleep(1000);

        System.out.println("+++++++++++固定长度的线程池++++++++++");
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        //提交五个任务给线程池执行
        for (int i = 0; i < 5; i++) {
            fixedThreadPool.execute(new MyRunnable());
        }
        fixedThreadPool.shutdown();

        Thread.sleep(1000);

        System.out.println("+++++++++++定时线程池++++++++++");
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);
        //提交五个任务给线程池执行
        for (int i = 0; i < 5; i++) {
            //3秒后再执行
            scheduledThreadPool.schedule(new MyRunnable(),3, TimeUnit.SECONDS);
        }
        scheduledThreadPool.shutdown();


        Thread.sleep(4000);

        System.out.println("+++++++++++单一线程线程池++++++++++");
        ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
        //提交五个任务给线程池执行
        for (int i = 0; i < 5; i++) {
            //3秒后再执行
            singleThreadPool.execute(new MyRunnable());
        }
        singleThreadPool.shutdown();

    }
}
