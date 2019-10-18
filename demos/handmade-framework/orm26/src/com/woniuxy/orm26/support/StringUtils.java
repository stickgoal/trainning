package com.woniuxy.orm26.support;

/**
 * 字符串工具类
 * 
 * @author Administrator
 *
 */
public class StringUtils {

	/**
	 * 驼峰转为下划线 myHouse => my_house
	 * 
	 * @return
	 */
	public static String camelCaseToUnderscore(String origin) {
		char[] charArr = origin.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < charArr.length; i++) {
			char c = charArr[i];

			if (Character.isUpperCase(c)) {
				if (i == 0) {
					sb.append(Character.toLowerCase(c));
				} else {
					sb.append("_" + Character.toLowerCase(c));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
