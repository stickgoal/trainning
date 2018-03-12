package com.chinasofti.framework.springmvc.springmvcDemo.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.chinasofti.framework.springmvc.springmvcDemo.controller.form.RegisterForm;

@Controller
@RequestMapping("form")
public class FormController extends BaseController{
	@Autowired
    private Validator validator;

	@RequestMapping(value="reg",method=RequestMethod.POST)
	public String reg(RegisterForm form,ModelMap mm){
		
		System.out.println(form);
		//spring mvc 自动将form对象放在modelmap中，可以用类名首字母小写的方式来获取
		
		return "regResult";
	}
	
	@RequestMapping("toReg")
	public String toReg(){
		return "reg2";
	}
	
	/**
	 * 数据验证
	 * @param form
	 * @param bindingResult
	 * @param mm
	 * @return
	 */
	@RequestMapping(value="regValid",method=RequestMethod.POST)
	public String regValid(@Valid RegisterForm form,BindingResult bindingResult,ModelMap mm){
		
		System.out.println(form);
		System.out.println(bindingResult);
		if(bindingResult.hasErrors()){
			return "reg2";
		}
		
		return "regResult";
	}
	
	
}
