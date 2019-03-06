package me.maiz.se.mini.multithreading;

public class RunnableDemo implements Runnable {

    @Override
    public void run() {
        System.out.println("在runnable内部运行 "+System.currentTimeMillis());
    }
}
