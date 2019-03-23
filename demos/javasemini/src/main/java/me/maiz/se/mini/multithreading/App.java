package me.maiz.se.mini.multithreading;

public class App {
    public static void main(String[] args) {
        //继承Thread方式启动
        ThreadDemo threadDemo = new ThreadDemo();
        threadDemo.setName("myThread");
        threadDemo.start();
        System.out.println(Thread.currentThread().getName()+"线程启动完毕");
        //实现Runnable方式启动
        RunnableDemo target = new RunnableDemo();
        Thread thread = new Thread(target);
        thread.setName("myAnotherThread");

        thread.start();

    }
}
