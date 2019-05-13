package me.maiz.se.multithreading;

public class Test {

    public static void main(String[] args) {
        new MyThread().start();
        new MyThread().start();
        //runable作为构造器的参数传入Thread
        MyRunnable mr = new MyRunnable();
        new Thread(mr).start();
        new Thread(mr).start();
    }

}
class MyThread extends Thread{
    public void run(){
        final Thread t = Thread.currentThread();
        //支派多线程要执行的任务
        for( int i = 0;i<10;i++){
            System.out.println(t.getName()+"  "+i);
        }
    }

}
class MyRunnable implements Runnable{

    @Override
    public void run() {
        final Thread t = Thread.currentThread();
        //支派多线程要执行的任务
        for( int i = 0;i<10;i++){
            System.out.println(t.getName()+"  "+i);
        }
    }
}
