package me.maiz.se.mini.deignpattern.singleton;

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
