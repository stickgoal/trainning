/**
 * 
 */
package com.woniuxy.mvc26.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 请求映射到一个方法上
 * 
 * @author Administrator
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface RequestMapping {
	
	/**
	 * url路径
	 * @return
	 */
	String value();
	
	/**
	 * 请求方法
	 * @return
	 */
//	RequestMethod[] method() default {RequestMethod.GET,RequestMethod.POST};
	
}
/*
 * enum RequestMethod{
 * 
 * GET,POST,PUT,DELETE;
 * 
 * }
 */
