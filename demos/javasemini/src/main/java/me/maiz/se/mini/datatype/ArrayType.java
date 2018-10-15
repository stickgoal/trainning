package me.maiz.se.mini.datatype;

import java.util.Arrays;

public class ArrayType {

    public static void main(String[] args) {
        int[] arr1 = new int[]{12, 5, 99};
        int[] arr2 = new int[10];
        int[] arr3 = {1, 3, 5};

        String[] strArr1 = new String[]{"Hello", "World", "!"};
        String[] strArr2 = {"Hello", "World", "!"};
        String[] strArr3 = new String[10];

        String[] data = {"He", "is", "a", "good", "man!"};
        //访问,通过下标访问数组
        System.out.println(data[1]);
        //得到数组的长度
        System.out.println(data.length);

        //设值,通过下标设值
        data[1] = "IS";
        System.out.println(Arrays.toString(data));

        //遍历
        //一般for循环
        for (int i = 0; i < data.length; i++) {
            System.out.println(i + ":" + data[i]);
        }
        //增强for循环
        for (String d : data) {
            System.out.println(d);
        }

        //初始化及设值
        int[][] arr2d = new int[10][];
        arr2d[0]=new int[]{12,8,12};
        System.out.println(Arrays.deepToString(arr2d));

        int[][] arr2d1 = {{1,2,3},{890,10,10},{12,12,100}};
        //访问二维数组的元素
        System.out.println(Arrays.toString(arr2d1[0]));
        //访问二维数组的元素的元素
        System.out.println(arr2d1[2][2]);
        //遍历二维数组
        for(int[] arr:arr2d1){
            System.out.println(">>"+Arrays.toString(arr));
            for(int i=0;i<arr.length;i++){
                arr[i]=arr[i]+1;
            }
        }
        //用Arrays.deepToString()直接打印
        System.out.println(Arrays.deepToString(arr2d1));


    }
}
