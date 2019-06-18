package me.maiz.se.mini.algorithm;

import java.util.Arrays;

/**
 * 动画演示：http://www.webhek.com/post/comparison-sort.html
 * 算法分析：https://www.cnblogs.com/herozhi/p/5880939.html
 */
public class BubbleSorting extends AlgorithmBase {

    public static void main(String[] args) {
        int[] data = {3, 44, 38, 5, 47, 15, 36, 26,27,2,46,4,19,50,48};
        bubbleSort(Arrays.copyOf(data, data.length));

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
        printBefore(s);
        //循环数组长度减1次，不需要和自己比较，外层循环确保每个元素都被比较
        // 每次外层循环结束后，最大值会排到最后
        for (int i = 0; i < s.length-1; i++) {
            //内层循环的次数随着每次循环完成排序的数据增加而减少，
            // 如，第1次找到了最大值，第2次就少循环1次，所以需要减去i
            for (int j = 0; j < s.length- 1 - i ; j++) {
                //交换两个数据
                if(s[j]>s[j+1]){
                    int tmp = s[j];
                    s[j]=s[j+1];
                    s[j+1]=tmp;
                }
                printInSorting(s, i, j);
            }
        }
        printAfter(s);

        return s;
    }


}
