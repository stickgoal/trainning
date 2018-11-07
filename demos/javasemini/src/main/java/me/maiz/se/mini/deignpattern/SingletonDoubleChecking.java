package me.maiz.se.mini.deignpattern;

/**
 * 懒汉式单例，需要时创建单例（线程安全版本,使用了双重检查锁DCL机制）
 */
public class SingletonDoubleChecking {

    private static SingletonDoubleChecking instance ;

    private SingletonDoubleChecking(){}

    public static SingletonDoubleChecking getInstance() {
        //检查是否为空，不为空时不检查，节省性能消耗;为空时可能有并发问题
        if(instance==null){
            //同步代码块，保证不会被并发获取
            synchronized(SingletonDoubleChecking.class) {
                //进入时再次判断，若不为空则不建实例，保证单例
                if (instance==null) {
                    instance = new SingletonDoubleChecking();
                }
            }
        }
        return instance;
    }

}
