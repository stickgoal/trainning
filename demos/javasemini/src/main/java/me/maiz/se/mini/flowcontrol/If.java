package me.maiz.se.mini.flowcontrol;

import java.util.Scanner;

public class If {

    public static void main(String[] args) {
        int score = 81;
        int money = 200;
        if (score > 80) {
            System.out.println("成绩很优秀");
        } else {
            System.out.println("成绩不行，无心玩乐....");
        }
        if (money > 50) {
            System.out.println("50块够请吃饭了");
        }
        if (score > 80 && money > 50) {
            System.out.println("考得不错，今天心情很好，请大家吃饭");
        }

        if (score > 90) {
            System.out.println("很优秀");
        } else {
            if (score > 80) {
                System.out.println("优秀");
            } else {
                if (score > 60) {
                    System.out.println("及格");
                } else {
                    System.out.println("oh,no");
                }
            }
        }

        if (score > 90) {
            System.out.println("很优秀");
        } else if (score > 80) {
            System.out.println("优秀");
        } else if (score > 60) {
            System.out.println("及格");
        } else {
            System.out.println("oh,no");
        }
    recruitment();
    }

    public static void recruitment() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入姓名:");
        String name = scanner.next();
        System.out.println("请输入性别[男/女]:");
        String gender = scanner.next();
        System.out.println("请输入年龄:");
        int age = scanner.nextInt();
        System.out.println("请输入身高[cm]");
        int height = scanner.nextInt();

        //这里判断是否符合要求


        scanner.close();
    }

}
