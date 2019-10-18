package com.woniuxy.orm26.core;

import java.util.List;

import com.woniuxy.orm26.support.PageResult;

/**
 * 持久化操作
 * 
 * @author Administrator
 *
 */
public interface Session<T> {

	/**
	 * 获取指定的类型的指定id的数据
	 * 
	 * @param <T>
	 * @param clazz
	 * @param id
	 * @return
	 */
	T findById(Class<T> clazz, int id);

	/**
	 * 查询所有
	 * 
	 * @param clazz
	 * @return
	 */
	List<T> findAll(Class<T> clazz);

	/**
	 * 根据参数更新对象 User{id=1,username="wanger"}
	 * 
	 * @param entity
	 */
	void updateById(Object entity);

	/**
	 * 保存对象
	 * 
	 * @param entity
	 */
	void save(Object entity);

	/**
	 * 根据ID删除数据
	 * 
	 * @param clazz
	 * @param id
	 */
	void delete(Class<T> clazz, int id);
	
	/**
	 * 根据sql查询一个数据
	 * @param clazz
	 * @param sql
	 * @param params
	 * @return
	 */
	T queryOne(Class<T> clazz,String sql,Object... params);
	
	/**
	 * 根据sql查询一批数据
	 * @param clazz
	 * @param sql
	 * @param params
	 * @return
	 */
	List<T> queryList(Class<T> clazz,String sql,Object... params);

	/**
	 * 根据sql执行增删改操作
	 * @param clazz
	 * @param sql
	 * @param params
	 */
	void update(Class<T> clazz,String sql,Object... params);
	
	/**
	 * 分页操作
	 * @param clazz     模型类=>表
	 * @param countSql  统计条数sql  select count(*) from product where xx=? and xx=? 
	 * @param querySql  查询数据sql  select * from product where xx=? and xx=? limit ?,?
	 * @param currIndex 当前页码
	 * @param pageSize  每页大小
	 * @param params    条件参数
	 */
	PageResult<T> page(Class<T> clazz,String countSql,String querySql,int currIndex,int pageSize,Object... params);

}
