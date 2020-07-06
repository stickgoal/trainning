package me.maiz.se.mini.multithreading.juc;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicValueDemo {

    public static void main(String[] args) {
        AtomicInteger i = new AtomicInteger();
        i.set(1);
        //CAS  compare and set 算法
        System.out.println(i.addAndGet(10));
    }

}
