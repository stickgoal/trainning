package me.maiz.se.mini.multithreading;


public class ThreadDemo extends Thread {
    @Override
    public void run() {
        System.out.println("在独立线程中运行的代码 "+System.currentTimeMillis());
    }
}
