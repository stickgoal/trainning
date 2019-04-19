package me.maiz.se.mini.multithreading;

import java.util.concurrent.TimeUnit;

public class Communication {

    volatile boolean hasEggs = false;
    Thread man = new Thread(() -> {
        while (true) {
            if (hasEggs) {
                System.out.println("收鸡蛋");
                hasEggs = false;
            } else {
                System.out.println("等鸡下蛋");
            }
        }
    });

    Thread hen = new Thread(() -> {
        while (true) {

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("鸡在下蛋");
            hasEggs = true;

        }
    });

    void start() {
        hen.start();
        man.start();
    }

    public static void main(String[] args) {
        new Communication().start();
    }
}
