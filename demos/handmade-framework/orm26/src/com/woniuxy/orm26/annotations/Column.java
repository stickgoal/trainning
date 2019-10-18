package com.woniuxy.orm26.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对数据库列的数据定义
 * @author Administrator
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
	
	/**
	 * 可空
	 * @return
	 */
	boolean nullable() default true;
	
	/**
	 * 长度
	 * @return
	 */
	int length() ;
	
	/**
	 * 注释
	 * @return
	 */
	String comment() default "";
	
}
