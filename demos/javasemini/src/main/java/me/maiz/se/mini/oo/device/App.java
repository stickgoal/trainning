package me.maiz.se.mini.oo.device;

public class App {
    public static void main(String[] args) {
        CellPhone phone = new CellPhone();
        //使用父类属性，继承得到
        phone.powerIn=10;
        //使用父类方法，继承得到
        phone.powerOn();
        //子类属性
        phone.cameraPixel=1200;
        //子类方法
        phone.call();
    }
}
