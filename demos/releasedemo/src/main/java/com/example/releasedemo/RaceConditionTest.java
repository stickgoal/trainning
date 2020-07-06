package com.example.releasedemo;

import java.util.Date;

public class RaceConditionTest {

    private volatile int a;

    public static void main(String[] args) {
        RaceCondition rc = new RaceCondition();
        MyThread t1 = new MyThread(rc);
        MyThread t2 = new MyThread(rc);
        t1.start();
        t2.start();
    }

}

class RaceCondition{

    public int a =0;
    public  void add(){
        synchronized(RaceCondition.class) {
            a = a + 1;
        }
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rc.add();
            System.out.println(Thread.currentThread().getName()+" : "+rc.a);
        }
        System.out.println(rc.a);
    }
}
