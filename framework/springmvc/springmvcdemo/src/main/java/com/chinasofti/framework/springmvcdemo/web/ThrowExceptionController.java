package com.chinasofti.framework.springmvcdemo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ThrowExceptionController {

	@RequestMapping("exception")
	public void throwEx(){
		throw new RuntimeException("没油了...");
	}
	
}
