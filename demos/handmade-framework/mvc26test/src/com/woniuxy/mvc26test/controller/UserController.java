package com.woniuxy.mvc26test.controller;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.woniuxy.mvc26.annotations.Controller;
import com.woniuxy.mvc26.annotations.P;
import com.woniuxy.mvc26.annotations.RequestMapping;
import com.woniuxy.mvc26.annotations.ResponseBody;

@Controller
public class UserController {

	@RequestMapping("hello")
	public String sayHello(@P("username")String username
			,@P("pwd")String password,
			@P("age")int age,
			@P("birthday") Date date,
			HttpServletRequest req,HttpSession session) {
		System.out.println(username +"\n"+password+"\n"+age+"\n"+req+"\n"+session+"\n"+date);
		
		req.setAttribute("username", username);
		req.setAttribute("password", password);
		req.setAttribute("age", age);
		
		return "hello";
	}
	
	@RequestMapping("login")
	public String doLogin() {
		return "redirect:index.jsp";
	}
	
	@RequestMapping("getData")
	@ResponseBody
	public User getData() {
		User user = new User("wanger","zhangsan",Arrays.asList("骑车","游泳"),new Date(),123,new Address("重庆", "江北"));
		return user;
	}
}
