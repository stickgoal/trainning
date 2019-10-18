package com.woniuxy.orm26.core;

import java.lang.reflect.Field;

import com.woniuxy.orm26.annotations.Id;
import com.woniuxy.orm26.support.StringUtils;

public class DMLGenerator {

	/**
	 * 使用反射根据一个类生成查询语句
	 * 
	 * @param clazz
	 * @return
	 */
	// select * from 表名 where id列 = ?
	public static String findById(Class<?> clazz) {

		String tableName = clazz.getSimpleName();

		Field[] fs = clazz.getDeclaredFields();
		// 找到ID列的字段名
		String idColumn = null;
		for (Field f : fs) {
			if (f.isAnnotationPresent(Id.class)) {
				idColumn = f.getName();
				break;
			}
		}

		String sql = "select * from " + tableName + " where " + StringUtils.camelCaseToUnderscore(idColumn) + "= ?";
		System.out.println("findById : " + sql);
		return sql;
	}

	// select * from 表名 where id列 = ?
	public static String findAll(Class<?> clazz) {

		String tableName = clazz.getSimpleName();

		String sql = "select * from " + tableName;
		System.out.println("findAll : " + sql);
		return sql;
	}

	// update 表名 set 列1=?,列2=?... where id列=?
	public static String update(Class<?> clazz) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("update ").append(clazz.getSimpleName()).append(" set ");

		Field[] fs = clazz.getDeclaredFields();
		String idColumn = null;
		for (Field f : fs) {
			if (f.isAnnotationPresent(Id.class)) {
				idColumn = StringUtils.camelCaseToUnderscore(f.getName());
			} else {
				sqlBuilder.append(StringUtils.camelCaseToUnderscore(f.getName()) + "=?,");
			}
		}
		sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
		sqlBuilder.append(" where " + idColumn + "=?");

		System.out.println("update : " + sqlBuilder.toString());

		return sqlBuilder.toString();
	}

	public static String delete(Class<?> clazz) {

		String tableName = clazz.getSimpleName();

		Field[] fs = clazz.getDeclaredFields();
		// 找到ID列的字段名
		String idColumn = null;
		for (Field f : fs) {
			if (f.isAnnotationPresent(Id.class)) {
				idColumn = f.getName();
				break;
			}
		}

		String sql = "delete from " + tableName + " where " + StringUtils.camelCaseToUnderscore(idColumn) + "= ?";
		System.out.println("delete : " + sql);
		return sql;
	}

	// insert into 表名 (字段名) values(?,?,?...)
	// insert into user (username,password) values(?,?);
	// insert into user (username,password,user_id) values(?,?,default);
	public static String insert(Class<?> clazz) {
		StringBuilder sqlBuilder = new StringBuilder();
		String tableName = StringUtils.camelCaseToUnderscore(clazz.getSimpleName());

		sqlBuilder.append("insert into " + tableName + "(");
		//处理列名
		Field[] fs = clazz.getDeclaredFields();
		boolean isAutoIncrement = false;
		String idColumn = null;
		for (Field f : fs) {
			Id id = f.getAnnotation(Id.class);
			// 如果不是ID字段，直接加入sql中
			if (id == null) {
				sqlBuilder.append(StringUtils.camelCaseToUnderscore(f.getName())).append(",");
			} else {
				// 保存id字段名
				idColumn = StringUtils.camelCaseToUnderscore(f.getName());
				isAutoIncrement = id.isAutoIncrement();
			}
		}
		sqlBuilder.append(idColumn).append(") values (");
		// 处理values的？
		// (?,?,default)
		if (isAutoIncrement) {
			for (int i = 0; i < fs.length - 1; i++) {
				sqlBuilder.append("?,");
			}
			sqlBuilder.append("default)");
		} else {
			// (?,?,?,
			for (int i = 0; i < fs.length; i++) {
				sqlBuilder.append("?,");
			}
			sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
			sqlBuilder.append(")");
		}

		System.out.println("insert 语句：" + sqlBuilder.toString());
		return sqlBuilder.toString();
	}

}
