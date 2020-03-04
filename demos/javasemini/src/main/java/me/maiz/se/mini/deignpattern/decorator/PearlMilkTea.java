package me.maiz.se.mini.deignpattern.decorator;

public class PearlMilkTea extends MilkTeaStylish {

    public PearlMilkTea(MilkTea milkTea) {
        super(milkTea);
    }

    @Override
    public void make() {
        System.out.println("添加珍珠");
        super.make();
    }
}
