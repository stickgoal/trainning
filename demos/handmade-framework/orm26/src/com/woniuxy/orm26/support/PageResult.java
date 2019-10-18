package com.woniuxy.orm26.support;

import java.util.List;

import lombok.Data;

@Data
public class PageResult<T> {
	/**
	 * 总条数
	 */
	private long totalCount;
	
	/**
	 * 总页数
	 */
	private long totalPage;
	
	/**
	 * 查询结果
	 */
	private List<T> list;
	
}
