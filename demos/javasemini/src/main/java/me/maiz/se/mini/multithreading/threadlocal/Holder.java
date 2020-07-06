package me.maiz.se.mini.multithreading.threadlocal;

public class Holder {
    //静态持有ThreadLocal
    private static ThreadLocal<Object> data = new ThreadLocal<>();

    //取出ThreadLocal中的值
    public static Object get() {
        return data.get();
    }

    //放入ThreadLocal中的值
    public static void set(Object v) {
        data.set(v);
    }

    //移除ThreadLocal中的值
    public static void remove(Object v) {
        data.remove();
    }

}
