package com.chinasofti.framework.springmvcdemo.web;

import org.springframework.format.datetime.DateFormatter;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.Date;
import java.util.List;

public abstract class ControllerBase {
	
	private static final String MSG = "MSG";

	// Date类型绑定
	@InitBinder
	public void init(WebDataBinder binder) {
		binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"), Date.class);
	}

	/**
	 * 为子类提供存入信息的快捷方式，以MSG为Key
     */
	protected void msg(ModelMap mm,String msg){
		mm.put(MSG, msg);
	}

	/**
	 * 从BindingResult中解析错误信息，并转换为异常
	 * @param result
     */
	protected void extractErrorMsg(BindingResult result) {
		if(result.hasErrors()){
			StringBuilder b = new StringBuilder();
			List<FieldError> errors = result.getFieldErrors();
			for(FieldError e : errors){
				b.append(e.getField()+":"+e.getDefaultMessage()+";");
			}
			if(b.length()>0){
				b.deleteCharAt(b.length()-1);
			}
			throw new IllegalArgumentException(b.toString());
		}
	}
}
