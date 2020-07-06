package me.maiz.se.mini.multithreading.juc.tool;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        //开饭倒计时，来一个人倒计时一次
        CountDownLatch startEatingCountdown = new CountDownLatch(8);
        for (int i = 0; i < 8; i++) {
            new Man(startEatingCountdown).start();
        }
        //调用await方法，等待所有人到齐
        startEatingCountdown.await();
        System.out.println("人到齐啦，开饭咯");
    }
}
class Man extends Thread{

    private CountDownLatch startEatingCountdown;

    Man(CountDownLatch startEatingCountdown) {
        this.startEatingCountdown = startEatingCountdown;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+":我到啦~");
        //按下计数器，计数器减一
        startEatingCountdown.countDown();
    }
}