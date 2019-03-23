package me.maiz.se.mini.multithreading;

public class RunnableDemo implements Runnable {

    @Override
    public void run() {
        String name = Thread.currentThread().getName();

        System.out.println(name+" 在runnable内部运行 "+System.currentTimeMillis());
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
