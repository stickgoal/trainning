package com.chinasofti.framework.springmvc.springmvcDemo.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("jump")
public class JumpController {
	@RequestMapping("a")
	public String a(){
		System.out.println("a");
		return "redirect:b";
	}
	
	
	@RequestMapping("b")
	@ResponseBody
	public String b(){
		System.out.println("b");
		return "跳转成功";
	}
	
	@RequestMapping("a1")
	public String a1(ModelMap mm){
		mm.addAttribute("param", new Date());
		System.out.println("a1");
		return "forward:b1";
	}
	
	@RequestMapping("b1")
	@ResponseBody
	public String b1(HttpServletRequest req,ModelMap mm,Date param){
		System.out.println("b1");
		System.out.println(mm.get("param"));
		System.out.println(req.getAttribute("param"));
		System.out.println(param);
		return "success";
	}
	
}
