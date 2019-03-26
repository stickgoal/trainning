package me.maiz.se.mini.multithreading.threadpool.executor;

import java.util.concurrent.Semaphore;

/**
 * 工作线程
 *
 * @author Administrator
 */
public class WorkThreadSimple extends Thread {
    /**
     * 标志是否运行
     */
    private volatile boolean running;

    /**
     * 任务
     */
    private Runnable target;


    public boolean isRunning() {
        return running;
    }

    /**
     * 提交任务
     *
     * @param runnable
     */
    public void submit(Runnable runnable) {
        System.out.println(Thread.currentThread().getName() + running);
        if (!running) {
            this.target = runnable;
            setRunning(true);
            System.out.println(Thread.currentThread().getName() + "提交任务成功");

        } else {
            System.out.println("任务提交失败，线程正在执行...");
        }
    }

    @Override
    public void run() {
        while (true) {
            // 如果是运行状态
            if (running) {
                target.run();
                // 任务执行完毕后暂停线程
                setRunning(false);
            } else {
                // 唤醒线程并执行任务
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void setRunning(boolean setRunning) {
        if (setRunning) {
            synchronized (this) {
                this.notify();
            }
        }
        this.running = setRunning;
    }
}
