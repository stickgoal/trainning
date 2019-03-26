package me.maiz.se.mini.multithreading.threadpool.executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * 线程池实现
 *
 * @author Administrator
 */
public class ThreadExecutor {

    private List<WorkThread> workThreads = Collections.synchronizedList(new ArrayList<WorkThread>());

    private Semaphore semaphore;

    /**
     * 初始化线程池
     * <ol>
     * <li>创建线程</li>
     * <li>启动线程</li>
     * </ol>
     *
     * @param threadCount 初始线程池大小
     */
    public ThreadExecutor(int threadCount) {
        System.out.println("初始化线程池开始");
        semaphore = new Semaphore(threadCount);
        for (int i = 0; i < threadCount; i++) {
            WorkThread wt = new WorkThread(semaphore);
            workThreads.add(wt);
            wt.start();
        }
        new SupervisorThread(taskQueue,semaphore,workThreads).start();
        System.out.println("初始化线程池结束");

    }

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();

    /**
     * 执行任务
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {

        taskQueue.add(runnable);
        System.out.println("taskQueue.size() :"+taskQueue.size());
        synchronized (taskQueue){
            taskQueue.notify();
        }
//忙等待处理
//        while(!taskQueue.isEmpty()) {
//            for (WorkThread t : workThreads) {
//                if(!t.isRunning()) {
//                    t.submit(taskQueue.poll());
//                    break;
//                }
//            }
//        }

    }

    private static class SupervisorThread extends Thread {

        private BlockingQueue<Runnable> taskQueue;

        private Semaphore semaphore;

        private List<WorkThread> workThreads;

        public SupervisorThread(BlockingQueue<Runnable> taskQueue, Semaphore semaphore,List<WorkThread> workThreads) {
            this.taskQueue = taskQueue;
            this.semaphore = semaphore;
            this.workThreads=workThreads;
        }

        @Override
        public void run() {
            while (true) {
                if(!taskQueue.isEmpty()) {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("====》taskQueue.size() :"+taskQueue.size());
                    for (WorkThread t : workThreads) {
                        if (!t.isRunning()) {
                            t.submit(taskQueue.poll());
                            break;
                        }
                    }
                }else{
                    System.out.println("等待任务出现");
                    synchronized (taskQueue){
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
