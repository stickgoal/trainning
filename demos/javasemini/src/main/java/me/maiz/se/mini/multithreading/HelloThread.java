package me.maiz.se.mini.multithreading;

public class HelloThread {
    public static void main(String[] args) {
        Thread thread = new Thread(()->{//lamda表达式
            System.out.println(Thread.currentThread().getName()+" : hello from thread!");
        }
        );
        thread.start();
        System.out.println(Thread.currentThread().getName()+" : hello from main!");
    }
}
