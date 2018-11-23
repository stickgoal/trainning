package me.maiz.se.mini.deignpattern.strategy;

public class DefaultDiscountStrategy implements DiscountStrategy {
    @Override
    public double calcPrice(double productPrice) {
        System.out.println("普通会员不打折");
        return productPrice;
    }
}
