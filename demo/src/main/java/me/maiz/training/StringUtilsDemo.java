package me.maiz.training;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2017/8/1.
 */
public class StringUtilsDemo {


    public static void main(String[] args) {
        //判断方法
        print(StringUtils.isAlpha("abc") );
        print(StringUtils.isAlpha("abc1") );
        print(StringUtils.isAlphanumeric("abc1") );
        print(StringUtils.isAlphanumericSpace("ab c1") );
        print(StringUtils.isAlphanumericSpace("ab c1") );
        print(StringUtils.isNumeric("123") );
        print(StringUtils.isNumeric("123a") );
        print(StringUtils.isNumeric("123 ") );
        print(StringUtils.isNumericSpace("123 ") );
        print(StringUtils.isEmpty(null));
        print(StringUtils.isEmpty(""));
        print(StringUtils.isEmpty(" "));
        print(StringUtils.isBlank(null));
        print(StringUtils.isBlank(""));
        print(StringUtils.isBlank(" "));
        print(StringUtils.isBlank("     "));
        print(StringUtils.isBlank(" x"));
        print(StringUtils.isNotBlank("     "));
        print(StringUtils.containsWhitespace("     "));
        print(StringUtils.containsWhitespace(" 123"));
        print(StringUtils.containsWhitespace("12 3"));


        //操作方法


    }

    public static void print(Object data){
        System.out.println(data);
    }

}
