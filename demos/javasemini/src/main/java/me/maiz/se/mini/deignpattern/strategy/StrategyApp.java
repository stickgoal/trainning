package me.maiz.se.mini.deignpattern.strategy;

public class StrategyApp {
    public static void main(String[] args) {
        PromotionManager pm = new PromotionManager(new SVIPDiscountStrategy());
        System.out.println(pm.getFinalPrice(100));
    }
}
