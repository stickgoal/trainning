package me.maiz.se.mini.multithreading.juc;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicIntegerFieldUpdaterDemo {

    public static void main(String[] args) {
        AtomicIntegerFieldUpdater<Dog> atfu = AtomicIntegerFieldUpdater.newUpdater(Dog.class,"age");
        Dog d = new Dog();
        d.setAge(12);
        atfu.incrementAndGet(d);

        System.out.println(d);
    }

}
