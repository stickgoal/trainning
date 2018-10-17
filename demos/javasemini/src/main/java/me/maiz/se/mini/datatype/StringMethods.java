package me.maiz.se.mini.datatype;

import java.util.Arrays;

public class StringMethods {
	public static void main(String[] args) {
		String src = "HELLOWORLD";
		//charAt 指定索引处的字符
		char c = src.charAt(5);
		System.out.println(c);
//		System.out.println(src.charAt(100)); 100超过最大索引位置，报错
		System.out.println(src.charAt(src.length()-1));
		//indexOf 指定字符或者字符串第一次出现的索引位置
		int idx = src.indexOf('L');
		System.out.println(idx);
		System.out.println(src.indexOf('x'));
//		if(src.indexOf('F')!=-1){
//
//		}
		System.out.println(src.indexOf("WORLD"));
		//lastIndexOf 指定字符或者字符串倒数第一次出现的索引位置
		System.out.println(src.lastIndexOf("L"));

		//开头 / 结尾
		System.out.println(src.startsWith("HE"));
		System.out.println(src.startsWith("SHE"));
		System.out.println(src.endsWith("SHE"));
		System.out.println(src.endsWith("ORLD"));

		//长度 length()  / 是否为空 isEmpty()
		System.out.println(src.length());
		System.out.println(src.isEmpty());
		String someStr = "";
		System.out.println(someStr!=null&& someStr.isEmpty());

		//trim() 去除前后的空白
		String str1 = " abc 123  ";
		System.out.println(str1.trim());
		System.out.println(str1.trim().length());
		System.out.println(str1);

		//subString/subSequece  前包后不包
		String str2 = src.substring(0, 5);
		System.out.println(str2);
		System.out.println(src.substring(5));
		//末尾两个
		System.out.println(src.substring(src.length()-2));
		//开头两个
		System.out.println(src.substring(0,2));

		//split 拆分
		String src2 = "Father::Mother::Me";
		String[] parts = src2.split("::");
		System.out.println(Arrays.toString(parts));
		String[] parts2 = src.split("L");
		System.out.println(Arrays.toString(parts2));
		System.out.println(src);

		//replace 替换
		String str3 = "Hello my world";
		String r = str3.replace("my","your");
		System.out.println(r);
		System.out.println(str3);
		//正则表达式
		String r2 = str3.replaceAll("\\s", "=");
		System.out.println(r2);

		//大小写
		System.out.println(str3.toUpperCase());
		System.out.println(str3.toLowerCase());

		//equals相等
		String str4 = "爱与被爱";
		String str5 = new String("爱与被爱");
		System.out.println(str4==str5);
		System.out.println(str4.equals(str5));

		String str6 = "HelloWorld";
		System.out.println(src.equalsIgnoreCase(str6));

		//compareTo比较
		String a = "abc";
		String b = "def";
		System.out.println(a.compareTo(b));
		System.out.println(b.compareTo(a));
		System.out.println(a.compareTo(a));

		String c1 = "abc";
		String d1 = "abcd";
		System.out.println(c1.compareTo(d1));

	}
}
