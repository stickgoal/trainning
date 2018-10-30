package me.maiz.se.mini.deignpattern;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 懒汉式单例，需要时创建单例（线程不安全版本）
 */
public class SingletonLazy{

    private static SingletonLazy instance ;

    private SingletonLazy(){}

    public static SingletonLazy getInstance() {
        if(instance==null){
            instance=new SingletonLazy();
        }
        return instance;
    }

}
