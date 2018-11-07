package me.maiz.se.mini.oo.device;

/**
 * 设备，泛指所有电子设备
 */
public class Device {

    //电源输入瓦数
    public int powerIn;

    //重量
    public int weight;

    void powerOn(){
        System.out.println("开机");
    }

    void powerOff(){
        System.out.println("关机");
    }

}
