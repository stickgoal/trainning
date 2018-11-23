package me.maiz.se.mini.deignpattern.builder;

import me.maiz.se.mini.deignpattern.builder.component.CPU;
import me.maiz.se.mini.deignpattern.builder.component.HardDisk;
import me.maiz.se.mini.deignpattern.builder.component.KeyBoard;
import me.maiz.se.mini.deignpattern.builder.component.Screen;

public class Computer {

    //CPU
    private CPU cpu;

    //屏幕
    private Screen screen;

    //硬盘
    private HardDisk hardDisk;

    //键盘
    private KeyBoard keyboard;

    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public HardDisk getHardDisk() {
        return hardDisk;
    }

    public void setHardDisk(HardDisk hardDisk) {
        this.hardDisk = hardDisk;
    }

    public KeyBoard getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(KeyBoard keybord) {
        this.keyboard = keybord;
    }

    @Override
    public String toString() {
        return "Computer{" +
                "cpu=" + cpu +
                ", screen=" + screen +
                ", hardDisk=" + hardDisk +
                ", keyboard=" + keyboard +
                '}';
    }
}
