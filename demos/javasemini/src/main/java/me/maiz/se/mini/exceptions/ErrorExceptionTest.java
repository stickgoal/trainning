package me.maiz.se.mini.exceptions;

import java.sql.SQLException;

public class ErrorExceptionTest {
    public static void main(String[] args) {

        testException();

        testError();

        testCheckedException();

    }

    private static void testCheckedException() {
//        throw new SQLException();
    }

    private static void testError() {
       int[] arr =  new int[1024*1024*1024];
    }

    private static void testException() {
        System.out.println("开始异常测试");
        System.out.println(100/0);
        System.out.println("结束异常测试");
    }
}
