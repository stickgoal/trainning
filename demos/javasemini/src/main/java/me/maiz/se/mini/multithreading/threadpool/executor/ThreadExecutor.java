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

    /*
     * 协调任务执行的信号量，与线程池大小相等。
     * 利用semaphore机制，每次调用工作线程处理任务前acquire,结束后release<br/>
     * 没有进程可用时，SupervisorThread等待，减少性能消耗;<br/>
     * 当有线程执行完任务后SupervisorThread继续执行
     */
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
        //通知Supervisor线程处理
        synchronized (taskQueue){
            taskQueue.notify();
        }

    }

    /**
     * 监控线程，用于将任务队列中的任务提交给工作线程处理
     */
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
                    System.out.println("监控线程：等待任务出现");
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
