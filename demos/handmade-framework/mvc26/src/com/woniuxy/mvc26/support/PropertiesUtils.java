package com.woniuxy.mvc26.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.woniuxy.mvc26.exceptions.MVCException;

public class PropertiesUtils {

	public static Properties load(String name) {

		InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream(name);

		Properties p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			throw new MVCException("加在配置文件["+name+"]失败",e);
		}

		return p;
	}

}
