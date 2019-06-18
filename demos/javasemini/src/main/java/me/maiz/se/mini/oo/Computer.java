package me.maiz.se.mini.oo;

public class Computer {
    //品牌
    private String brand;
    //预装操作系统
    private boolean osPreInstalled;

    //构造一台指定了品牌的电脑
    public Computer(String brand) {
        this.brand = brand;
    }

    //构造一台指定了品牌和外部指定是否预装系统的电脑
    public Computer(String brand, boolean osPreInstalled) {
        this.brand = brand;
        this.osPreInstalled = osPreInstalled;
    }
}
