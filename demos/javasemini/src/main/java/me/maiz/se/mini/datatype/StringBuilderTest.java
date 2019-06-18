package me.maiz.se.mini.datatype;

import java.util.Date;

public class StringBuilderTest {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(1).append('a').append("zzzz").append(new Date());
        System.out.println(sb.toString());

        StringBuilder strB = new StringBuilder("Hello,World");
        //删除字符
//		strB.deleteCharAt(5);
        strB.delete(5, 6);
        System.out.println(strB);
        //indexOf
        System.out.println(strB.indexOf("o", 5));
        System.out.println(strB.lastIndexOf("o"));
        //在指定位置插入字符
        strB.insert(5, ' ');
        System.out.println(strB);
        //替换
        strB.setCharAt(5, '+');
        System.out.println(strB);
        strB.replace(5, 6, " My ");
        System.out.println(strB);
        //翻转
        System.out.println(strB.reverse());

    }

}
	
	
