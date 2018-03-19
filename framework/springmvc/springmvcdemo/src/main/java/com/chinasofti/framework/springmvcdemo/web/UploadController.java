package com.chinasofti.framework.springmvcdemo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class UploadController extends ControllerBase {
	
	@RequestMapping(value = "upload", method = RequestMethod.GET)
	public String toUpload() {
		return "upload";
	}
	
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public String upload(String username,MultipartFile myFile) {
		System.out.println("username ： "+username+" file:"+myFile.getOriginalFilename());
		
		File file = new File("d:/tmp/a.png");
		try {
			myFile.transferTo(file);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		return "upload";
	}

}
