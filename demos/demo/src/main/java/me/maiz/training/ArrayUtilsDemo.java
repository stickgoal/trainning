package me.maiz.training;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

/**
 * Created by Lucas on 2017-08-02.
 */
public class ArrayUtilsDemo {

    public static void main(String[] args) {
       int[] src1 = new int[]{1,2,3} ;
        int[] src2 = new int[]{4,5,6} ;
        //加入某个元素
       println(Arrays.toString(ArrayUtils.add(src1,4)));
        //加入某个数组
        println(Arrays.toString(ArrayUtils.addAll(src1,src2)));
        //是否包含元素1
        println((ArrayUtils.contains(src1,1)));
        //是否包含元素5
        println((ArrayUtils.contains(src1,5)));
        //src1是否为空
        println((ArrayUtils.isEmpty(src1)));
        //src1是否不为空
        println((ArrayUtils.isNotEmpty(src1)));

    }

    public static void println(Object data){
        System.out.println(data);
    }
}
