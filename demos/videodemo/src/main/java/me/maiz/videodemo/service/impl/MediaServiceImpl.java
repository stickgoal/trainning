package me.maiz.videodemo.service.impl;

import me.maiz.videodemo.service.MediaService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MediaServiceImpl implements MediaService {

    private static final String FFMPEG_INSTALLATION_PATH="D:\\apps\\ffmpeg\\bin\\ffmpeg.exe";
    private static final String VIDEO_CONVERTED_PATH="D:\\tmp\\video-converted\\";
    private static final String PIC_CUTTED_PATH="D:\\tmp\\pic-cutted\\";

    @Override
    public Map<String, String> getInfo(String videoPath) {
        // 创建一个List集合来保存转换视频文件为flv格式的命令
        List<String> convert = new ArrayList<String>();
        convert.add(FFMPEG_INSTALLATION_PATH); // 添加转换工具路径
        convert.add("-i"); // 添加参数＂-i＂，该参数指定要转换的文件
        convert.add(videoPath); // 添加要转换格式的视频文件的路径

        ProcessBuilder builder = new ProcessBuilder();
        Map<String,String> meta = new HashMap<>();

        try {
            builder.command(convert);
            // 如果此属性为 true，则任何由通过此对象的 start() 方法启动的后续子进程生成的错误输出都将与标准输出合并，
            //因此两者均可使用 Process.getInputStream() 方法读取。这使得关联错误消息和相应的输出变得更容易
            builder.redirectErrorStream(true);
           Process process =  builder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine())!=null){
                if(line.contains(": ")){
                    String[] parts =line.split(": ");
                    if(parts.length>1) {
                        meta.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("转码失败",e);
        }
        return meta;
    }

    @Override
    public void convert(String videoPath, String fileName) {
        String newFileName = fileName.replace("mp4","flv");
        // 创建一个List集合来保存转换视频文件为flv格式的命令
        List<String> convert = new ArrayList<String>();
        convert.add(FFMPEG_INSTALLATION_PATH); // 添加转换工具路径
        convert.add("-i"); // 添加参数＂-i＂，该参数指定要转换的文件
        convert.add(videoPath); // 添加要转换格式的视频文件的路径
        convert.add("-qscale");     //指定转换的质量
        convert.add("6");
        convert.add("-ab");        //设置音频码率
        convert.add("64");
        convert.add("-ac");        //设置声道数
        convert.add("2");
        convert.add("-ar");        //设置声音的采样频率
        convert.add("22050");
        convert.add("-r");        //设置帧频
        convert.add("24");
        convert.add("-y"); // 添加参数＂-y＂，该参数指定将覆盖已存在的文件
        convert.add(VIDEO_CONVERTED_PATH+newFileName);
        ProcessBuilder builder = new ProcessBuilder();
        try {
            builder.command(convert);
            // 如果此属性为 true，则任何由通过此对象的 start() 方法启动的后续子进程生成的错误输出都将与标准输出合并，
            //因此两者均可使用 Process.getInputStream() 方法读取。这使得关联错误消息和相应的输出变得更容易
            builder.redirectErrorStream(true);
            builder.start();

        } catch (Exception e) {
            throw new RuntimeException("转码失败",e);
        }

    }

    @Override
    public void cutPic(String videoPath,String fileName) {
        System.out.println("开始提取图片");
        // 创建一个List集合来保存转换视频文件为flv格式的命令
        List<String> cutpic = new ArrayList<String>();
        cutpic.add(FFMPEG_INSTALLATION_PATH);
        cutpic.add("-i");
        cutpic.add(videoPath); // 同上（指定的文件即可以是转换为flv格式之前的文件，也可以是转换的flv文件）
        cutpic.add("-y");
        cutpic.add("-f");
        cutpic.add("4cif");
        cutpic.add("-ss"); // 添加参数＂-ss＂，该参数指定截取的起始时间
        cutpic.add("17"); // 添加起始时间为第17秒
        cutpic.add("-t"); // 添加参数＂-t＂，该参数指定持续时间
        cutpic.add("0.001"); // 添加持续时间为1毫秒
        cutpic.add("-s"); // 添加参数＂-s＂，该参数指定截取的图片大小
        cutpic.add("800*280"); // 添加截取的图片大小为350*240
        cutpic.add(PIC_CUTTED_PATH+fileName+".jpg"); // 添加截取的图片的保存路径,包含文件名

        ProcessBuilder builder = new ProcessBuilder();
        try {
            builder.command(cutpic);
            // 如果此属性为 true，则任何由通过此对象的 start() 方法启动的后续子进程生成的错误输出都将与标准输出合并，
            //因此两者均可使用 Process.getInputStream() 方法读取。这使得关联错误消息和相应的输出变得更容易
            builder.redirectErrorStream(true);
            builder.start();

        } catch (Exception e) {
            throw new RuntimeException("提取关键帧失败",e);
        }


    }
}
