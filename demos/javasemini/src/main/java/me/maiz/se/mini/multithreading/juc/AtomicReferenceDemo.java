package me.maiz.se.mini.multithreading.juc;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDemo {

    public static void main(String[] args) {

        AtomicReference<Dog> ar = new AtomicReference<>();
        //将dog放入
        Dog dog = new Dog();
        ar.set(dog);
        //修改值
        Dog dog1 = ar.updateAndGet(d -> {
            d.setAge(d.getAge() + 1);
            d.setName(d.getName() + "!");
            return d;
        });
        System.out.println(dog1);
    }

}

class Dog{
    private String name;
    public volatile int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}