package me.maiz.se.mini.multithreading.threadpool.buildin;

public class MyRunnable implements Runnable{

    @Override
    public void run() {
       //打印当前对象，以确认线程是否被复用
        System.out.println(Thread.currentThread().getName());
    }
}
