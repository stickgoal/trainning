package me.maiz.videodemo;

import me.maiz.videodemo.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Controller

public class VideoController {

    @Autowired
    private MediaService mediaService;


    @RequestMapping("video/upload")
    @ResponseBody
    public String upload(MultipartFile video){
        try {
            String filePath =  "d:\\tmp\\" + video.getOriginalFilename();
            video.transferTo(new File(filePath));
            Map<String, String> meta = mediaService.getInfo(filePath);
            System.out.println(meta);
            mediaService.convert(filePath,video.getOriginalFilename());
            mediaService.cutPic(filePath,video.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
        }


        return "";
    }


}
