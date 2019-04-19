package me.maiz.se.mini.oo.interfaces;

/**
 * 电脑
 */
public class Computer {

    public void sendDisplayToVGA(VGA vga){
        vga.transferVideo("从电脑来的视频数据");
    }

}
