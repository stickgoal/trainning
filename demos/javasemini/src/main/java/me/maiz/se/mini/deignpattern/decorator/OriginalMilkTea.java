package me.maiz.se.mini.deignpattern.decorator;

public class OriginalMilkTea implements MilkTea {
    @Override
    public void make() {
        System.out.println("加奶，加茶，奶茶出炉");
    }
}
