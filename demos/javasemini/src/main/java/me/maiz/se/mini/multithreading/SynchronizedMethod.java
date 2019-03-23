package me.maiz.se.mini.multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SynchronizedMethod {

    public static void main(String[] args) {
        Counter counter = new Counter();
        for (int i = 0; i < 3; i++) {
            new Thread(()->{
                counter.syncAdd();
            }).start();
        }

    }

}

class Counter{

    public void syncAdd(){
        String tName = Thread.currentThread().getName();
        System.out.println(tName +"加之前");
        synchronized(Counter.class) {
            for (int i = 0; i < 3; i++) {
                System.out.println(tName + "算到" + i);
                sleep(1);
            }
        }
        System.out.println(tName+"加之后");
    }

    private void sleep(int count) {
        try {
            TimeUnit.SECONDS.sleep(count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}