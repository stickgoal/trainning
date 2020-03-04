package me.maiz.se.mini.deignpattern.decorator;

public class DecoratorApp {

    public static void main(String[] args) {
        //原味奶茶
        MilkTea milkTea = new OriginalMilkTea();
        //使用珍珠装饰器装饰=>珍珠奶茶
        PearlMilkTea pearlMilkTea = new PearlMilkTea(milkTea);
        //使用芋圆装饰器装饰=>芋圆珍珠奶茶
        YuyuanMilkTea yuyuanMilkTea = new YuyuanMilkTea(pearlMilkTea);

        yuyuanMilkTea.make();


    }

}
