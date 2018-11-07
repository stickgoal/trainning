package me.maiz.se.mini.oo.interfaces;

//投影仪
public class Projector implements VGA,HDMI {

    @Override
    public void transferVideo(String videoData) {
        System.out.println("投影仪显示："+videoData);
    }

    @Override
    public void transferVideoHDMI(String videoData) {
        System.out.println("投影仪显示："+videoData);
    }

    @Override
    public void transferAudioHDMI(String audioData) {
        System.out.println("投影仪播放："+audioData);
    }
}
