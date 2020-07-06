package me.maiz.se.mini.deignpattern.singleton;

/**
 * 匿名内部类实现
 */
public class SingletonNestClass {


    private static class InstanceHolder{
        private static SingletonNestClass INSTANCE = new SingletonNestClass();
    }

    private SingletonNestClass(){}

    public static SingletonNestClass getInstance(){
        return InstanceHolder.INSTANCE;
    }

}
