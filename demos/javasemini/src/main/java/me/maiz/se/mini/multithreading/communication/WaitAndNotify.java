
package me.maiz.se.mini.multithreading.communication;

import java.util.concurrent.TimeUnit;

public class WaitAndNotify {

    private boolean parcelArrived=false;

    private final Object lock=new Object();

    public Thread postMan = new Thread(()->{

        while(true){
            sleep(3);
            System.out.println("邮递员：包裹已经送到");
            parcelArrived=true;
            synchronized (lock){
                lock.notify();
            }
        }

    });

    public Thread client = new Thread(()->{
        while(true){
            if(parcelArrived){
                System.out.println("客户：包裹已取");
                parcelArrived=false;
            }else{
                System.out.println("客户：我的快递啥时候到啊");
                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    });

    private void sleep(long s) {
        try {
            TimeUnit.SECONDS.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startAll(){
        postMan.start();
        client.start();
    }

    public static void main(String[] args) {
        new WaitAndNotify().startAll();
    }


}
