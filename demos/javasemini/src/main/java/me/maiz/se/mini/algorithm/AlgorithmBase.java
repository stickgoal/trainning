package me.maiz.se.mini.algorithm;

import java.util.Arrays;

public class AlgorithmBase {

    public static void printBefore(int[] arr){
        System.out.println("排序前："+Arrays.toString(arr));
    }
    public static void printAfter(int[] arr){
        System.out.println("排序后："+Arrays.toString(arr));
    }
    public static void printInSorting(int[] s, int i, int j) {
        System.out.println(i+":"+j+"=>"+Arrays.toString(s));
    }

}
