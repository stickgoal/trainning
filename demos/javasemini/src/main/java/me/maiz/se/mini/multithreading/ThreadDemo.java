package me.maiz.se.mini.multithreading;


public class ThreadDemo extends Thread {
    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        for (int i = 0; i < 10; i++) {
            System.out.println(name + "在独立线程中运行的代码 "+i);
        }
    }
}
