package com.woniuxy.mvc26test.controller;

import com.woniuxy.mvc26.annotations.Controller;
import com.woniuxy.mvc26.annotations.RequestMapping;

@Controller
public class ProductController {

	@RequestMapping("product/query")
	public String queryProduct() {
		return null;
	}
	
}
