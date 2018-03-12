package com.chinasofti.framework.springmvc.springmvcDemo.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("exception")
public class ThrowExceptionController {

	@RequestMapping("throw")
	public String throwEx(){
		throw new RuntimeException("系统出现问题");
	}
}
