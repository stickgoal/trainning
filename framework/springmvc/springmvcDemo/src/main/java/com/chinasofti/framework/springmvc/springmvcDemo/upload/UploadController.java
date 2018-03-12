package com.chinasofti.framework.springmvc.springmvcDemo.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.chinasofti.framework.springmvc.springmvcDemo.controller.BaseController;

@Controller
public class UploadController extends BaseController {

	private static final String UPLOAD_FILE_PATH = "c:/tmp/upload/";

	@RequestMapping(value="/upload",method=RequestMethod.POST)
	public String upload(MultipartFile file,String username) {
		System.out.println("file="+file.getOriginalFilename()+" : username="+username);
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			if (!file.isEmpty()) {
				String originalFilname = file.getOriginalFilename();
				System.out.println("处理：" + originalFilname);
				File f = new File(UPLOAD_FILE_PATH + originalFilname);
				
				bos = new BufferedOutputStream(new FileOutputStream(f));
				byte[] buffer = new byte[1204];
				bis = new BufferedInputStream(file.getInputStream());
				while ((bis.read(buffer)) >0) {
					bos.write(buffer);
				}
				bos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return "uploadSuccess";
	}

}
