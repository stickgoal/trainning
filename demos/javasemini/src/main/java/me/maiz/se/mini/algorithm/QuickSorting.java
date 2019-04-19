package me.maiz.se.mini.algorithm;

import java.util.Arrays;

/**
 * 动画演示：http://www.webhek.com/post/comparison-sort.html
 * 算法分析：https://www.cnblogs.com/herozhi/p/5880939.html
 */
public class QuickSorting extends AlgorithmBase {

    public static void main(String[] args) {
        int[] data = {3, 44, 38, 5, 47, 15, 36, 26,27,2,46,4,19,50,48};
        printBefore(data);
        int[] result =quickSort(Arrays.copyOf(data,data.length),0,data.length-1);
        printAfter(result);

    }


    /**
     * 快速排序使用分治策略(Divide and Conquer)来把一个序列分为两个子序列。步骤为：
     *<ul>
     * <li>从序列中挑出一个元素，作为"基准"(pivot).</li>
     * <li>把所有比基准值小的元素放在基准前面，所有比基准值大的元素放在基准的后面（相同的数可以到任一边），这个称为分区(partition)操作。</li>
     * <li>对每个分区递归地进行步骤1~2，递归的结束条件是序列的大小是0或1，这时整体已经被排好序了。</li>
     * </ul>
     * @param a
     */
    public static int[] quickSort(int[] a,int low,int high){
        int start = low;
        int end = high;
        int key = a[low];


        while(end>start){
            //从后往前比较,如果没有比关键值小的，比较下一个，直到有比关键值小的交换位置，然后又从前往后比较
            while(end>start&&a[end]>=key)
                end--;
            if(a[end]<=key){
                int temp = a[end];
                a[end] = a[start];
                a[start] = temp;
                System.out.println("+"+Arrays.toString(a));
            }
            //从前往后比较,如果没有比关键值大的，比较下一个，直到有比关键值大的交换位置
            while(end>start&&a[start]<=key)
                start++;
            if(a[start]>=key){
                int temp = a[start];
                a[start] = a[end];
                a[end] = temp;
                System.out.println("-"+Arrays.toString(a));
            }
            //此时第一次循环比较结束，关键值的位置已经确定了。左边的值都比关键值小，右边的值都比关键值大，但是两边的顺序还有可能是不一样的，进行下面的递归调用
        }
        //递归
        if(start>low){
            System.out.println("递归开始 start="+start+" low="+low);
            quickSort(a,low,start-1);//左边序列。第一个索引位置到关键值索引-1
        }
        if(end<high) {
            System.out.println("递归开始 end="+end+" high="+high);
            quickSort(a,end+1,high);//右边序列。从关键值索引+1到最后一个
        }

        return a;
    }


}
