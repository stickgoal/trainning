package me.maiz.se.mini.oo.interfaces;

//显示器
public class Monitor implements VGA{
    @Override
    public void transferVideo(String videoData) {
        System.out.println("显示器显示："+videoData);
    }
}
