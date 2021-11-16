package me.maiz.tool.ftpdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FtpController {


    @Autowired
    private FtpUtil ftpUtil;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadImg() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("code", "0");
        map.put("msg", "上传文件失败");
        File file = new File("d:/tmp/1111.mp3");
        String fileName = "upload1.mp3";
        log.info("上传文件:{}", fileName);
        InputStream inputStream = new FileInputStream(file);
        String filePath = null;
        Boolean flag = ftpUtil.uploadFile(fileName, inputStream);
        if (flag == true) {
            log.info("上传文件成功!");
            filePath = ftpUtil.FTP_BASEPATH + fileName;
            map.put("code", "1");
            map.put("msg", "上传文件成功");
        }
        map.put("path", filePath);
        return map; //该路径图片名称，前端框架可用ngnix指定的路径+filePath,即可访问到ngnix图片服务器中的图片
    }

}
