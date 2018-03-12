package com.chinasofti.framework.springmvc.springmvcDemo.json;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinasofti.framework.springmvc.springmvcDemo.controller.BaseController;

@Controller
@RequestMapping("json")
public class JsonController extends BaseController{
	
	@ResponseBody
	@RequestMapping("/getJson")
	public Map<String,User> getJson(){
		User u = new User();
		u.setUserId(1);
		u.setUsername("wanger");
		u.setPassword("xxx");
		
		Map<String,User> m = new HashMap<>();
		m.put("a", u);
		return m;
	}

}
