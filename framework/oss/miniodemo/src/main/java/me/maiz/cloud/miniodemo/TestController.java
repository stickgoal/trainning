package me.maiz.cloud.miniodemo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class TestController {

        
    @Autowired

    FileStoreService fileStoreService;

        
    @GetMapping("test2")


    public void test2(HttpServletResponse response) throws IOException {
String bucketName = "demo-bucket";

// 创建bucket
boolean created = fileStoreService.createBucket(bucketName);

// 存储文件
String saved = fileStoreService.save(bucketName, new File("C:\asd.txt"), "a.txt");

// 删除文件
boolean deleted = fileStoreService.delete(bucketName, "a.txt");

// 获取文件(会产生本地缓存)
File file = fileStoreService.getFile(bucketName, "a.txt");

// 获取输入流
InputStream inputStream = fileStoreService.getStream(bucketName, "a.txt");

// 下载
response.addHeader("Content-Disposition", "attachment;filename=a.txt");
ServletOutputStream os = response.getOutputStream();
fileStoreService.writeTo(bucketName, "a.txt", os);
}
}
