package me.maiz.training;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2017/8/1.
 */
public class StringUtilsDemo {


    public static void main(String[] args) {
        //判断方法
        println("是否为字母 StringUtils.isAlpha(\"abc\") "+StringUtils.isAlpha("abc") );
        println("是否为字母 StringUtils.isAlpha(\"abc1\") "+StringUtils.isAlpha("abc1") );
        println("是否为字母或数字 StringUtils.isAlphanumeric(\"abc1\") "+StringUtils.isAlphanumeric("abc1") );
        println("是否为字母或数字或空格 StringUtils.isAlphanumericSpace(\"ab c1\") "+StringUtils.isAlphanumericSpace("ab c1") );
        println("是否为字母或数字或空格 StringUtils.isAlphanumericSpace(\"ab c1\") "+StringUtils.isAlphanumericSpace("ab c1") );
        println("是否为数字 StringUtils.isNumeric(\"123\") "+StringUtils.isNumeric("123") );
        println("是否为数字 StringUtils.isNumeric(\"123a\") "+StringUtils.isNumeric("123a") );
        println("是否为数字 StringUtils.isNumeric(\"123 \") "+StringUtils.isNumeric("123 ") );
        println("是否为数字或空格 StringUtils.isNumericSpace(\"123 \") "+StringUtils.isNumericSpace("123 ") );
        println("是否为空字符串 StringUtils.isEmpty(null)"+StringUtils.isEmpty(null));
        println("是否为空字符串 StringUtils.isEmpty(\"\")"+StringUtils.isEmpty(""));
        println("是否为空字符串 StringUtils.isEmpty(\" \")"+StringUtils.isEmpty(" "));
        println("是否为空字符串或空白字符串 StringUtils.isBlank(null) "+StringUtils.isBlank(null));
        println("是否为空字符串或空白字符串 StringUtils.isBlank(\"\") "+StringUtils.isBlank(""));
        println("是否为空字符串或空白字符串 StringUtils.isBlank(\" \") "+StringUtils.isBlank(" "));
        println("是否为空字符串或空白字符串 StringUtils.isBlank(\"     \") "+StringUtils.isBlank("     "));
        println("是否为空字符串或空白字符串 StringUtils.isBlank(\" x\") "+StringUtils.isBlank(" x"));
        println("是否为空字符串或空白字符串 StringUtils.isNotBlank(\"     \") "+StringUtils.isNotBlank("     "));
        println("是否包含空白字符 StringUtils.containsWhitespace(\"     \") "+StringUtils.containsWhitespace("     "));
        println("是否包含空白字符 StringUtils.containsWhitespace(\" 123\") "+StringUtils.containsWhitespace(" 123"));
        println("是否包含空白字符 StringUtils.containsWhitespace(\"12 3\") "+StringUtils.containsWhitespace("12 3"));


        //操作方法
        println("StringUtils.join 连在一起join : "+StringUtils.join(new Object[]{"Hello",5,3.5,'f'}));
        println("StringUtils.join 用，连在一起 join : "+StringUtils.join(new Object[]{"Hello",5,3.5,'f'},","));
        println("StringUtils.leftPad 用*填充左边，直到满10位 : "+StringUtils.leftPad("1997",10,"*"));
        println("StringUtils.repeat 重复10次x : "+StringUtils.repeat("x",10));
        println("StringUtils.capitalize 首字母大写 : "+StringUtils.capitalize("great"));
        println("StringUtils.uncapitalize 首字母小写 : "+StringUtils.uncapitalize("GREAT"));
        println("StringUtils.upperCase 全大写 : "+StringUtils.upperCase("HelloWorld"));
        println("StringUtils.lowerCase 全小写 : "+StringUtils.lowerCase("HelloWorld"));
        println("StringUtils.countMatches 统计life is good中有多少个i : "+StringUtils.countMatches("life is good","i"));
        println("StringUtils.abbreviate 把\"life is good\"省略到5个字符 : "+StringUtils.abbreviate("life is good",5));


    }

    public static void println(Object data){
        System.out.println(data);
    }

}
