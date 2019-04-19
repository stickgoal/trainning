package me.maiz.videodemo.service;

import java.util.Map;

/**
 * 多媒体服务
 */
public interface MediaService {
    /**
     * 获取信息
     * @param videoPath
     * @return
     */
    Map<String, String> getInfo(String videoPath);

    /**
     * 转码
     * @param videoPath
     * @param target
     * @return
     */
    void convert(String videoPath,String target);

    /**
     * 提取关键帧
     * @param videoPath
     * @param fileName
     * @return
     */
    void cutPic(String videoPath,String fileName);

}
