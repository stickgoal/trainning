package com.woniuxy.orm26.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定某个列为主键
 * 
 * @author Administrator
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
	/**
	 * 该主键是否自增
	 * 
	 * @return
	 */
	boolean isAutoIncrement() default true;

}
