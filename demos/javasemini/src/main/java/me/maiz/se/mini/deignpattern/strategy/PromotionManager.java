package me.maiz.se.mini.deignpattern.strategy;

public class PromotionManager {
    //策略引用
    private DiscountStrategy discountStrategy;
    //初始化策略
    public PromotionManager(DiscountStrategy discountStrategy){
        this.discountStrategy=discountStrategy;
    }
    //应用策略
    public double getFinalPrice(double price){
        return discountStrategy.calcPrice(price);
    }

}
