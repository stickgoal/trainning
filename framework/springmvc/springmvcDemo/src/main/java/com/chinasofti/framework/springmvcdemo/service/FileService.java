package com.chinasofti.framework.springmvcdemo.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	String save(MultipartFile file,String type);
	
	String getFilePath(String filename);
}
