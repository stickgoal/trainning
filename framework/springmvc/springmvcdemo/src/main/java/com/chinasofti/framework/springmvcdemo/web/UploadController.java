package com.chinasofti.framework.springmvcdemo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.chinasofti.framework.springmvcdemo.service.FileService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class UploadController extends ControllerBase {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "upload", method = RequestMethod.GET)
    public String toUpload() {
        return "upload";
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public String upload(String username, MultipartFile myFile) {
        System.out.println("username ： " + username + " file:" + myFile.getOriginalFilename());

        fileService.save(myFile, "test");
        return "upload";
    }

    @RequestMapping(value = "download", method = RequestMethod.GET)
    public String download(String filename, HttpServletResponse resp) {

        String filePath = fileService.getFilePath(filename);

        resp.setContentType("text/plain");
        resp.setHeader("Content-disposition", "attachment; filename="+filename);

        BufferedInputStream bis = null;
        try {
            ServletOutputStream out = resp.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(filePath));
            byte[] buff = new byte[1024];
            int readSize = 0;
            while ((readSize = bis.read(buff)) != 0) {
                out.write(buff);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "upload";
    }

}
