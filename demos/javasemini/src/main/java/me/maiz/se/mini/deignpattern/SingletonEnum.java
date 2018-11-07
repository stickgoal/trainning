package me.maiz.se.mini.deignpattern;

/**
 * 枚举式单例，枚举的创建由JVM保证，因此不会出现并发问题，构造器自动私有，外部不能创建，只能引用
 * 用法为：SingletonEnum.INSTANCE.doSomething();
 */
public enum SingletonEnum {
    //唯一实例，默认为public static final的
    INSTANCE;

    public String name;

    public void doSomething(){
        System.out.println("doSomething");
    }

    public static void main(String[] args) {
        SingletonEnum.INSTANCE.doSomething();
    }

}
