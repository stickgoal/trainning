package me.maiz.se.mini.multithreading.juc.tool;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {

    public static void main(String[] args) {
        //每车坐5人，发车操作是通过barrierAction指定的
        CyclicBarrier bus = new CyclicBarrier(5,()->{
            System.out.println(System.currentTimeMillis()+" 人数够了，发车！");
        });
        //上车
        for (int i = 0; i < 10; i++) {
            new Passenger(bus).start();
        }
    }

}
//乘客
class Passenger extends Thread{
    private CyclicBarrier bus;

    Passenger(CyclicBarrier bus) {
        this.bus = bus;
    }
    @Override
    public void run() {
        try {
            //等发车
            System.out.println(Thread.currentThread().getName()+":车啥时候开哟");
            bus.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
