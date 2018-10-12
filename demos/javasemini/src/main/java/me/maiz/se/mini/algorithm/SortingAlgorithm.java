package me.maiz.se.mini.algorithm;

import java.util.Arrays;

/**
 * 动画演示：http://www.webhek.com/post/comparison-sort.html
 */
public class SortingAlgorithm {

    public static void main(String[] args) {
        int[] data = {12, 31, 1, 109, 77, 13, 1, 6};
//        bubbleSort(Arrays.copyOf(data, data.length));
//        selectSort(Arrays.copyOf(data,data.length));
        printBefore(data);
        int[] result = quickSort(Arrays.copyOf(data,data.length),0,data.length-1);
        printAfter(result);
    }

    private static void printBefore(int[] arr){
        System.out.println("排序前："+Arrays.toString(arr));
    }
    private static void printAfter(int[] arr){
        System.out.println("排序后："+Arrays.toString(arr));
    }
    private static void printInSorting(int[] s, int i, int j) {
        System.out.println(i+":"+j+"=>"+Arrays.toString(s));
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

    /**
     * 快速排序使用分治策略(Divide and Conquer)来把一个序列分为两个子序列。步骤为：
     *<ul>
     * <li>从序列中挑出一个元素，作为"基准"(pivot).</li>
     * <li>把所有比基准值小的元素放在基准前面，所有比基准值大的元素放在基准的后面（相同的数可以到任一边），这个称为分区(partition)操作。</li>
     * <li>对每个分区递归地进行步骤1~2，递归的结束条件是序列的大小是0或1，这时整体已经被排好序了。</li>
     * </ul>
     * @param a
     */
    private static int[] quickSort(int[] a,int start,int end){
        if(start<end){
            int baseNum=a[start];//选基准值
            int midNum;//记录中间值
            int i=start;
            int j=end;
            do{
                while((a[i]<baseNum)&&i<end){
                    i++;
                }
                while((a[j]>baseNum)&&j>start){
                    j--;
                }
                if(i<=j){
                    midNum=a[i];
                    a[i]=a[j];
                    a[j]=midNum;
                    i++;
                    j--;
                }
            }while(i<=j);
            if(start<j){
                quickSort(a,start,j);
            }
            if(end>i){
                quickSort(a,i,end);
            }
        }
        return a;
    }



}
