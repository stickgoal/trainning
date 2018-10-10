package me.maiz.se.mini.algorithm;

import java.util.Arrays;

public class SortingAlgorithm {

    public static void main(String[] args) {
        int[] data = {12, 31, 1, 109, 77, 13, 1, 6};
        printBefore(data);
        int[] result = bubbleSort(Arrays.copyOf(data, data.length));
        printAfter(result);
    }

    private static void printBefore(int[] arr){
        System.out.println("排序前："+Arrays.toString(arr));
    }
    private static void printAfter(int[] arr){
        System.out.println("排序后："+Arrays.toString(arr));
    }

    /**
     * 冒泡排序
     * <ol>
     * <li>比较相邻的元素，如果前一个比后一个大，就把它们两个调换位置。</li>
     * <li>对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。这步做完后，最后的元素会是最大的数。</li>
     * <li>针对所有的元素重复以上的步骤，除了最后一个。</li>
     * <li>持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。</li>
     * </ol>
     *
     * @param s 原始数组
     * @return 排序过后的数组
     */
    private static int[] bubbleSort(int[] s) {
        for (int i = 0; i < s.length-1; i++) {
            //次数减去1是因为不需要和自己比较
            for (int j = 0; j < s.length- 1 - i ; j++) {
                if(s[j]>s[j+1]){
                    int tmp = s[j];
                    s[j]=s[j+1];
                    s[j+1]=tmp;
                }
            }
        }
        return s;
    }
}
