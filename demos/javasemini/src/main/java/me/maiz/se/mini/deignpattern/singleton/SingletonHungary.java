package me.maiz.se.mini.deignpattern.singleton;

/**
 * 饿汉式单例，创建类时创建单例，线程也不安全
 */
public class SingletonHungary {

    private static final SingletonHungary instance = new SingletonHungary();

    private SingletonHungary(){}

    public static SingletonHungary getInstance() {
        return instance;
    }

}
