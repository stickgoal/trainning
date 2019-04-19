package me.maiz.se.mini.datatype;

public class StringTest {
    public static void main(String[] args) {
//        String s1="Hello";
//        s1="World";

        String s2="Hello";
        String s3="Hello";
        String s4=new String("Hello");
        String s5=new String("Hello");
        System.out.println("s2==s3 =>"+(s2==s3));
        System.out.println("s3==s4 =>"+(s3==s4));
        System.out.println("s4==s5 =>"+(s4==s5));
    }
}
