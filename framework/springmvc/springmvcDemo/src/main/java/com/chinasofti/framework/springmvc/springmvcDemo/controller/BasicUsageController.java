package com.chinasofti.framework.springmvc.springmvcDemo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/basic")
public class BasicUsageController extends BaseController {
	
	/**
	 * 指定请求方法
	 * @param mm
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String basic(ModelMap mm) {
		mm.put(MSG_KEY, "基本使用-POST");
		return "basic";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String basicGet(ModelMap mm) {
		mm.put(MSG_KEY, "基本使用-get方法");
		return "basic";
	}
	

	@RequestMapping("sub")
	public String basicSub(ModelMap mm) {
		mm.put(MSG_KEY, "基本使用-子路径");
		return "basic";
	}

	@RequestMapping("params")
	public String basicParams(ModelMap mm, String name, Date birthday) {
		mm.put("name", name);
		mm.put("birthday", new SimpleDateFormat(DATE_FORMAT_STRING).format(birthday));
		return "basicParams";
	}
	
	@RequestMapping(value="/path/{id}")
	public String basicPathVar(ModelMap mm,@PathVariable int id,Date birthday){
		mm.put("id", id);
		mm.put("birthday", new SimpleDateFormat(DATE_FORMAT_STRING).format(birthday));
		return "basicParams";
	}

}
