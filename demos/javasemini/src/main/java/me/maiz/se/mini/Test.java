package me.maiz.se.mini;

import java.util.Arrays;
import java.util.Date;

public class Test {

    public static void main(String[] args) {
        int[] data = {12,44,999,1,8,7};
        int[] result = bubbleSort(data);
        System.out.println(Arrays.toString(result));
    }


    private static int[] bubbleSort(int[] s) {

        for (int i = 0; i < s.length-1; i++) {
            //次数减去1是因为不需要和自己比较,减去i是因为最大的都交换到了前面
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

    /**
     * 将传入的日期加上传入的天数并返回计算后的日期
     * @param source  起始时间
     * @param days  加减的日期
     * @return
     */
    private static Date calcDate(Date source,int days){
        //你的代码
        return null;
    }



}


