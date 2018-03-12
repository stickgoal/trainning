package com.chinasofti.framework.springmvc.springmvcDemo.hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/hello")
public class HelloController {

	@RequestMapping("/")
	public String hello(ModelMap mm) {
		mm.put("msg", "你好");
		return "hello";
	}
	
}
