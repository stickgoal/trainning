package me.maiz.se.mini.exceptions;

import java.util.Scanner;

public class TryCatchFinally {
    public static void main(String[] args) {
        System.out.println("0 main try前");
        Scanner scanner = new Scanner(System.in);

        try{
            int i = scanner.nextInt();
            System.out.println("1 try 异常前");
            if(i%2==0) {
                int a = 100 / 0;
            }else{
                int a = 100/10;
            }
            System.out.println("2 try 异常后");
        }catch (Exception e){
            System.out.println("3 catch");
            e.printStackTrace();
        }finally{
            System.out.println("4 finally");
        }
    }
}
