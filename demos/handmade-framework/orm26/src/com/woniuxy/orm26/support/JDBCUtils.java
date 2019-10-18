package com.woniuxy.orm26.support;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;


/**
 * 
 * @ClassName:  Connections   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author liuyx
 * @date 2019年8月7日
 */
public class JDBCUtils {
	
	private static String driverClass;
	private static String url;
	private static String username;
	private static String password;
	private static DruidDataSource dds;
	
	static {
		readProperties();
		dds = new DruidDataSource();
        dds.setDriverClassName(driverClass);
        dds.setUrl(url);
        dds.setUsername(username);
        dds.setPassword(password);
	}
	
	// 读取配置文件
	public static void readProperties() {
		// 创建Properties对象
		Properties p = new Properties();
		// 获取路径
		String path = JDBCUtils.class.getResource("/").getPath();
//		System.out.println(path);
//		System.out.println(Connections.class.getResource("/").getPath());
		// 文件输入流
		try {
			// D:/LUCA/WorkSpace2/JDBC/bin/com/woniuxy/demo2/databases.properties
			FileInputStream fis = new FileInputStream(path+"databases.properties");
			p.load(fis);
			// 读取信息
			driverClass = p.getProperty("driverClass");
			url = p.getProperty("url");
			username = p.getProperty("username");
			password = p.getProperty("password");
		
		} catch (FileNotFoundException e) {
			System.out.println("配置文件读取失败");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("配置文件加载失败");
			e.printStackTrace();
		}
	}
	
	// 获取连接
	public static Connection getConnection() {
		Connection connection = null;
		
		try {
			 connection = dds.getConnection();
		} catch (SQLException e) {
			System.out.println("获取连接失败");
			e.printStackTrace();
		}
		return connection;
	}
	
	// 关闭连接
	public static void close(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("关闭连接失败");
			e.printStackTrace();
		}
	}
	
	
}
