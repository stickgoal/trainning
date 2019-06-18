package me.maiz.se.mini.exceptions;

public class Throw {
    public static void main(String[] args) {
        //运行程序报错：Exception in thread "main" java.lang.RuntimeException: 测试throw关键字的用法
        testThrow();
    }

    private static void testThrow() {
        throw new RuntimeException("测试throw关键字的用法");
    }
}
