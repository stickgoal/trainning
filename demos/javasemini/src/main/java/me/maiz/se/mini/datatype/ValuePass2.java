package me.maiz.se.mini.datatype;

import java.util.Arrays;

public class ValuePass2 {


    public static void main(String[] args) {
        int[] x = {2, 3, 5};
        calc(x);
        System.out.println(Arrays.toString(x));
    }

    private static void calc(int[] x) {
        x = new int[10];
        x[0] = 999;
    }


}
