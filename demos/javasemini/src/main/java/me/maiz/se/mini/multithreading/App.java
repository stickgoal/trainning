package me.maiz.se.mini.multithreading;

public class App {
    public static void main(String[] args) {
        //继续Thread方式启动
        ThreadDemo threadDemo = new ThreadDemo();
        threadDemo.start();
        //实现Runnable方式启动
        RunnableDemo target = new RunnableDemo();
        Thread thread = new Thread(target);
        thread.start();

    }
}
