package me.maiz.se.mini.test;

public class EqualsTest {
    public static void main(String[] args) {
        int a = 128;
        int b= 128;
        Integer c = 128;
        Integer d = 128;
        String e = new String("128");
        String f = new String("128");
        System.out.println(a==b);
        System.out.println(c==d);
        System.out.println(e==f);
    }
}
