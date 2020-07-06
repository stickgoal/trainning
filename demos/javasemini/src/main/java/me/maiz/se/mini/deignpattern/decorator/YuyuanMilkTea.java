package me.maiz.se.mini.deignpattern.decorator;

public class YuyuanMilkTea extends MilkTeaStylish {
    public YuyuanMilkTea(MilkTea milkTea) {
        super(milkTea);
    }

    @Override
    public void make() {

        System.out.println("添加芋圆");

        super.make();
    }
}
