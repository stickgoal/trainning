package me.maiz.se.mini.collection.generictype;

public interface ContextHolder<T> {
    //存入
    void put(T context);
    //取出
    T get();
}
