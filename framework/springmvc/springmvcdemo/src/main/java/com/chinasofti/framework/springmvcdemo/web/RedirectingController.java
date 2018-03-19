package com.chinasofti.framework.springmvcdemo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("redirect")
public class RedirectingController {

	@RequestMapping("a")
	public String a(){
		System.out.println("a");
//		return "redirect:/hi";
		return "redirect:b";
	}
	
	@RequestMapping("aa")
	public String aa(String name){
		System.out.println("aa");
		return "forward:b";
	}
	
	@RequestMapping("b")
	public String b(String name){
		System.out.println("b name:"+name);
		return "skip";
	}
	
	
}
