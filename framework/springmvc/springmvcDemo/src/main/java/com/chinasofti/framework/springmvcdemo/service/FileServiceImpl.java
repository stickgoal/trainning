package com.chinasofti.framework.springmvcdemo.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("fileService")
public class FileServiceImpl implements FileService {

	private static final String FOLDER_PREFIX = "D:\\tmp\\static\\";
	private static final String SEPERATOR = "_";

	@Override
	public String save(MultipartFile file, String type) {

		String filename = generateFileName(file.getOriginalFilename(), type);
		System.out.println("保存路径"+filename);
		File dest = new File(FOLDER_PREFIX + filename);
		try {
			file.transferTo(dest);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return filename;
	}

	@Override
	public String getFilePath(String filename) {
		return FOLDER_PREFIX + filename;
	}


	private String generateFileName(String originalFilename, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddmmHHss");
		String timestamp = sdf.format(new Date());
		return type + SEPERATOR + timestamp + SEPERATOR + originalFilename;
	}

}
