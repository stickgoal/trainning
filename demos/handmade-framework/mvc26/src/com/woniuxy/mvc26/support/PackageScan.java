package com.woniuxy.mvc26.support;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import com.woniuxy.mvc26.exceptions.MVCException;

public class PackageScan {

	/**
	 * 扫描包下的所有实体类
	 * 
	 * @param packageName
	 * @return
	 */
	public static List<Class<?>> scan(String packageName) {

		String path = packageName.replace(".", File.separator);
		System.out.println(path);
		List<File> files = new ArrayList<File>();
		List<Class<?>> allClasses = new ArrayList<>();

		try {
			// 从指定的路径下获取到资源
			Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(path);
			// 将URL转换为File列表
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				files.add(new File(new URI(url.toString())));
			}

		} catch (IOException | URISyntaxException e) {
			throw new MVCException("扫描包下的类出错", e);
		}
		// 调用findClass方法获取到路径下的所有类
		allClasses.addAll(findClass(files.get(0), packageName));

		return allClasses;
	}

	private static Collection<? extends Class<?>> findClass(File packageFile, String packageName) {
		List<Class<?>> classList = new ArrayList<>();

		// 是文件夹
		File[] files = packageFile.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				// com.woniuxy.orm24
				classList.addAll(findClass(f, packageName + "." + f.getName()));
			} else {
				if (f.getName().endsWith(".class")) {
					// com.woniuxy.orm24.User.class
					String fileName = f.getName();
					String className = packageName + "." + fileName.replace(".class", "");
					try {
						classList.add(Class.forName(className));
					} catch (ClassNotFoundException e) {
						throw new MVCException("加载类" + className + "时出错", e);
					}
				}
			}
		}

		return classList;
	}

}
