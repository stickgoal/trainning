package com.example.releasedemo;

import java.util.concurrent.TimeUnit;

public class KuaiDi {
    //共享快递是否到达变量
    private boolean arrived = false;

    private Object lock = new Object();

    Thread postMan = new Thread(()->{
        while(true){
            sleep(3);
            System.out.println("快递员：快递已到~");
            arrived=true;
            synchronized (lock) {
                //唤醒客户
                lock.notify();
            }

        }
    });

    Thread client = new Thread(()->{
       while (true){
           if(arrived){
               System.out.println("客户：取走快递");
               arrived=false;
           }else{
               System.out.println("客户：焦急等待");
               synchronized (lock){
                   try {
                       lock.wait();//阻塞线程
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }

           }
           sleep(1);
       }
    });

    public void start(){
        postMan.start();
        client.start();
    }

    private void sleep(long s){
        try {
            TimeUnit.SECONDS.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new KuaiDi().start();
    }
}
