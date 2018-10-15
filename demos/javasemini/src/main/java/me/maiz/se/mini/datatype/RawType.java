package me.maiz.se.mini.datatype;

public class RawType {
    public static void main(String[] args) {
        byte b = -126;
        short s = 32767;
        int i = 3000000;
        long l1 = 4000000000000l;
        long l2 = 4000000000000L;
        //整数默认类型 为 int
        //浮点数默认类型 为 double
        float f = 2.0f;
        double d = 2.4d;

        char c = 'a';
        char c1 = 100;
        System.out.println(c1);
        //true | false
        boolean bool = true;


        //可以使用==、！=、>、<、>=、<=对基本数据类型的数值进行比较运算；
        System.out.println(s>b);
        System.out.println(l1==l2);
        System.out.println(l1>=l2);
        System.out.println(i+3);
        System.out.println(i-3);
        System.out.println(i*3);
        System.out.println(i/3);
        System.out.println(i%3);

    }
}
