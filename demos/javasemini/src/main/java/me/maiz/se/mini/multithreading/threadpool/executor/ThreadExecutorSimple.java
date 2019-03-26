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
public class ThreadExecutorSimple {

    private List<WorkThreadSimple> workThreads = Collections.synchronizedList(new ArrayList<WorkThreadSimple>());

    /**
     * 初始化线程池
     * <ol>
     * <li>创建线程</li>
     * <li>启动线程</li>
     * </ol>
     *
     * @param threadCount 初始线程池大小
     */
    public ThreadExecutorSimple(int threadCount) {
        System.out.println("初始化线程池开始");
        for (int i = 0; i < threadCount; i++) {
            WorkThreadSimple wt = new WorkThreadSimple();
            workThreads.add(wt);
            wt.start();
        }
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

        //忙等待处理，如果taskQueue不空，就持续循环直到找到可以执行任务的线程为止
        while(!taskQueue.isEmpty()) {
            for (WorkThreadSimple t : workThreads) {
                if(!t.isRunning()) {
                    t.submit(taskQueue.poll());
                    break;
                }
            }
        }

    }

}
