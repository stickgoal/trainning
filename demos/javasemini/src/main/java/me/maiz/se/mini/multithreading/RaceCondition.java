package me.maiz.se.mini.multithreading;

public class RaceCondition {
     int a = 0;

    public void add() {
        a = a + 1;
    }

    public static void main(String[] args) {
        RaceCondition rc = new RaceCondition();
        MyThread t1 = new MyThread(rc);
        MyThread t2 = new MyThread(rc);
        t1.start();
        t2.start();
    }
}

class MyThread extends Thread {
    private RaceCondition rc;

    MyThread(RaceCondition rc) {
        this.rc = rc;
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rc.add();
        }
        System.out.println(rc.a);
    }
}