package me.maiz.se.mini.multithreading.juc.tool;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreDemo {
    public static void main(String[] args) {
        //只有两个车位
        Semaphore parkingLot = new Semaphore(2);
        //有5辆车要来停车
        for (int i = 0; i < 5; i++) {
            new Car(parkingLot).start();
        }
    }
}

class Car extends Thread {
    //同一个停车场
    private Semaphore parkingLot;

    public Car(Semaphore parkingLot) {
        this.parkingLot = parkingLot;
    }

    //定义一个打印的方法，用于输出线程名和时间
    private void  print(long current,String message){
        String name = Thread.currentThread().getName();
        System.out.println(current+" : "+name+" : "+message);
    }

    @Override
    public void run() {
        print(System.currentTimeMillis(),"试图停车");
        try {
            //获取许可，得到就可以执行，得不到则阻塞等待
            parkingLot.acquire();

            print(System.currentTimeMillis(),"进场停车>>>>>>");
            TimeUnit.SECONDS.sleep(5);
            //释放许可，释放后其他的线程可以得到
            parkingLot.release();

           print(System.currentTimeMillis(),"离开<<<<<<<");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

