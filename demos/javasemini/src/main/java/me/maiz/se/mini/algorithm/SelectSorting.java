package me.maiz.se.mini.algorithm;

import java.util.Arrays;

/**
 * 动画演示：http://www.webhek.com/post/comparison-sort.html
 * 算法分析：https://www.cnblogs.com/herozhi/p/5880939.html
 */
public class SelectSorting extends AlgorithmBase {

    public static void main(String[] args) {
        int[] data = {3, 44, 38, 5, 47, 15, 36, 26,27,2,46,4,19,50,48};

        selectSort(Arrays.copyOf(data,data.length));

    }


    /**
     * <ul>
     *     <li>初始时在序列中找到最小（大）元素，放到序列的起始位置作为已排序序列；</li>
     *     <li>再从剩余未排序元素中继续寻找最小（大）元素，放到已排序序列的末尾。</li>
     *     <li>以此类推，直到所有元素均排序完毕。</li>
     * </ul>
     *
     * 注意选择排序与冒泡排序的区别：冒泡排序通过依次交换相邻两个顺序不合法的元素位置，从而将当前最小（大）元素放到合适的位置；
     * 而选择排序每遍历一次都记住了当前最小（大）元素的位置，最后仅需一次交换操作即可将其放到合适的位置。
     * @param s
     */
    private static void selectSort(int[] s){
        printBefore(s);

        for(int i=0;i<s.length;i++){
            int minIndex = i;
            for(int j=i+1;j<s.length;j++){
                //找出最小的索引
                if(s[j]<s[minIndex]){
                    minIndex=j;
                }
            }
            //将最小的索引位置的数与当前数据对换
            int tmp = s[i];
            s[i]=s[minIndex];
            s[minIndex]=tmp;
            printInSorting(s, i, minIndex);

        }

        printAfter(s);
    }

}
