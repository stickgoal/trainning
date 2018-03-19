package com.chinasofti.framework.springmvcdemo.web;

import com.chinasofti.framework.springmvcdemo.web.form.RegForm;
import com.chinasofti.framework.springmvcdemo.web.form.UserForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class BasicUsageController {
	
	@RequestMapping("hi")
	public String hello(){
		return "hello";
	}
	
	@RequestMapping("p")
	public String param(String name,int age){
		System.out.println("name : "+name+" age:"+age);
		return "hello";
	}
	
	@RequestMapping("p2")
	public String param(RegForm form){
		System.out.println("form : "+form);
		return "hello";
	}
	
	@RequestMapping("p3")
	public void req(HttpServletRequest req,HttpServletResponse resp){
		System.out.println(req.toString()+resp.toString());
	}
	
	@RequestMapping("attr")
	public String hello(String name,ModelMap modelMap){
		modelMap.put("x", name);
		modelMap.put("now", new Date());
		return "hello";
	}
	
//	路径变量
	@RequestMapping("/user/{id}")
	public String userDetail(@PathVariable String id){
		System.out.println("id="+id);
		return "hello";
	}

	//指定请求方法来区分请求
	@RequestMapping(value="add",method= RequestMethod.GET)
	public String add(){
		//
		System.out.println("get方式访问");
		return "user/add";
	}

	@RequestMapping(value="add",method=RequestMethod.POST)
	public String doAdd(UserForm userForm){
		System.out.println("访问到后台 userForm:"+userForm);
		return "user/add";
	}


	
	
	
}
