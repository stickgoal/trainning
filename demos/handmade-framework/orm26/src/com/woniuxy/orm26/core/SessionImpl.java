package com.woniuxy.orm26.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.woniuxy.orm26.annotations.Id;
import com.woniuxy.orm26.exceptions.ORMException;
import com.woniuxy.orm26.support.JDBCUtils;
import com.woniuxy.orm26.support.PageResult;

public class SessionImpl<T> implements Session<T> {

	RowProcessor processor = new BasicRowProcessor(new GenerousBeanProcessor());

	@Override
	public T findById(Class<T> clazz, int id) {
		// 反射生成SQL
		String sql = DMLGenerator.findById(clazz);

		// 获得连接
		Connection conn = JDBCUtils.getConnection();

		// 使用QueryRunner查询数据库
		QueryRunner qr = new QueryRunner();
		RowProcessor processor = new BasicRowProcessor(new GenerousBeanProcessor());

		try {
			return qr.query(conn, sql, new BeanHandler<T>(clazz, processor), id);
		} catch (SQLException e) {
			throw new ORMException("findById出现异常", e);
		} finally {
			JDBCUtils.close(conn);
		}
	}

	@Override
	public List<T> findAll(Class<T> clazz) {
		// 反射生成SQL
		String sql = DMLGenerator.findAll(clazz);

		// 获得连接
		Connection conn = JDBCUtils.getConnection();

		// 使用QueryRunner查询数据库
		QueryRunner qr = new QueryRunner();
		RowProcessor processor = new BasicRowProcessor(new GenerousBeanProcessor());

		try {
			return qr.query(conn, sql, new BeanListHandler<T>(clazz, processor));
		} catch (SQLException e) {
			throw new ORMException("findAll出现异常", e);
		} finally {
			JDBCUtils.close(conn);
		}
	}

	// update 表名 set 列名1=值1，列名2=值2.. where id列=id值
	// update user set username=? ,password=? where user_id=?
	@Override
	public void updateById(Object entity) {
		// 反射生成语句
		String sql = DMLGenerator.update(entity.getClass());
		// 获得连接
		Connection conn = JDBCUtils.getConnection();

		// 使用QueryRunner查询数据库
		QueryRunner qr = new QueryRunner();

		try {
			qr.update(conn, sql, getParams(entity));
		} catch (SQLException e) {
			throw new ORMException("update出现异常", e);
		} finally {
			JDBCUtils.close(conn);
		}
	}

	private Object[] getParams(Object entity) {
		List<Object> params = new ArrayList<>();
		Field[] fields = entity.getClass().getDeclaredFields();
		Object idValue = null;
		for (Field f : fields) {
			// {user}.username => [username].get({user})
			try {
				// 强制访问私有属性
				f.setAccessible(true);
				if (!f.isAnnotationPresent(Id.class)) {
					params.add(f.get(entity));
				} else {
					idValue = f.get(entity);
				}

			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		params.add(idValue);

		// 转换为Object数组
		return params.toArray();
	}

	@Override
	public void save(Object entity) {
		String sql = DMLGenerator.insert(entity.getClass());

		// 获得连接
		Connection conn = JDBCUtils.getConnection();

		// 使用QueryRunner查询数据库
		QueryRunner qr = new QueryRunner();

		try {
			qr.update(conn, sql, getInsertParams(entity));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.close(conn);
		}

	}

	// insert into user (username,password,user_id) values(?,?,default);
	private Object[] getInsertParams(Object entity) {
		// ["wanger","wohenshuang",1]
		Object[] array = getParams(entity);
		// 是否自增
		Field[] fs = entity.getClass().getDeclaredFields();
		for (Field f : fs) {
			if (f.isAnnotationPresent(Id.class)) {
				Id id = f.getAnnotation(Id.class);
				if (id.isAutoIncrement()) {
					// 当id自增，sql的values部分填入的是default，不需要参数，减去最后一个id的值
					Object[] newArr = new Object[array.length - 1];
					System.arraycopy(array, 0, newArr, 0, array.length - 1);
					return newArr;
				}
				break;
			}
		}

		return array;
	}

	@Override
	public void delete(Class<T> clazz, int id) {
		// 反射生成SQL
		String sql = DMLGenerator.delete(clazz);
		// 获得连接
		Connection conn = JDBCUtils.getConnection();

		// 使用QueryRunner查询数据库
		QueryRunner qr = new QueryRunner();

		try {
			qr.update(conn, sql, id);
		} catch (SQLException e) {
			throw new ORMException("delete出现异常", e);
		} finally {
			JDBCUtils.close(conn);
		}
	}

	@Override
	public T queryOne(Class<T> clazz, String sql, Object... params) {
		System.out.println("queryOne: " + sql);
		// 获得连接
		Connection conn = JDBCUtils.getConnection();

		// 使用QueryRunner查询数据库
		QueryRunner qr = new QueryRunner();

		try {
			return qr.query(conn, sql, new BeanHandler<>(clazz, processor), params);
		} catch (SQLException e) {
			throw new ORMException("queryOne出现异常", e);
		} finally {
			JDBCUtils.close(conn);
		}

	}

	@Override
	public List<T> queryList(Class<T> clazz, String sql, Object... params) {
		// 获得连接
		Connection conn = JDBCUtils.getConnection();

		// 使用QueryRunner查询数据库
		QueryRunner qr = new QueryRunner();

		try {
			return qr.query(conn, sql, new BeanListHandler<>(clazz, processor), params);
		} catch (SQLException e) {
			throw new ORMException("queryList出现异常", e);
		} finally {
			JDBCUtils.close(conn);
		}
	}

	@Override
	public void update(Class<T> clazz, String sql, Object... params) {
		// 获得连接
		Connection conn = JDBCUtils.getConnection();

		// 使用QueryRunner查询数据库
		QueryRunner qr = new QueryRunner();

		try {
			qr.update(conn, sql, params);
		} catch (SQLException e) {
			throw new ORMException("update出现异常", e);
		} finally {
			JDBCUtils.close(conn);
		}

	}

	@Override
	public PageResult<T> page(Class<T> clazz, String countSql, String querySql, int currIndex, int pageSize,
			Object... params) {
		// 获得连接
		Connection conn = JDBCUtils.getConnection();

		// 使用QueryRunner查询数据库
		QueryRunner qr = new QueryRunner();
		
		try {
			//执行统计,总条数
			long totalCount = qr.query(conn,countSql, new ScalarHandler<>(),params);
			
			// 计算总页码
			long totalPage = (totalCount % pageSize == 0) ? (totalCount / pageSize) : (totalCount / pageSize + 1);
			
			//select * from product where xx=? and xx=? limit ?,?
			//补充分页的limit的两个参数
			int start = (currIndex - 1) * pageSize;
			Object[] allParams = new Object[params.length+2];
			System.arraycopy(params, 0, allParams, 0, params.length);
			//倒数第二个参数是 start，分页起始位置
			allParams[allParams.length-2]=start;
			//倒数第一个参数是pageSize，每页多少条
			allParams[allParams.length-1]=pageSize;
			
			
			//查询分页的数据
			List<T> data= qr.query(conn, querySql,new BeanListHandler<T>(clazz,processor),allParams);
			
			PageResult<T> pr = new PageResult<>();
			pr.setTotalCount(totalCount);
			pr.setTotalPage(totalPage);
			pr.setList(data);
			return pr;
			
		} catch (SQLException e) {
			throw new ORMException("分页查询失败", e);
		}finally {
			JDBCUtils.close(conn);
		}
		
		
	}

}
