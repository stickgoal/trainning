package com.chinasofti.framework.springmvcdemo.web;

import com.chinasofti.framework.springmvcdemo.dao.model.User;
import com.chinasofti.framework.springmvcdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
//路径分级，这里提供一个上下文路径/user
@Controller
@RequestMapping("user")
public class ContextLevelController {
	
	@Autowired
	private UserService userService;

	@RequestMapping("add")
	public String add(){
		return "hello";
	}
	
	@RequestMapping("remove")
	public String lock(){
		return "hello";
	}
	
	@RequestMapping("query")
	public String query(String username,ModelMap mm){
		List<User> users = userService.findByUserName(username);
		mm.put("users", users);
		return "users";
	}
	
	//...
	
}
