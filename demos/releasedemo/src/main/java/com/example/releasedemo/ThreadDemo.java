package com.example.releasedemo;

public class ThreadDemo extends Thread {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+"Hello~~~");
        Thread b = new Thread();
        try {
            //阻塞当前线程，等待B执行完毕后再继续执行当前线程
            b.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ThreadDemo threadDemo = new ThreadDemo();
        threadDemo.run();
        threadDemo.start();
    }
}
