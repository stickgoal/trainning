package me.maiz.se.mini.deignpattern.strategy;

public class VIPDiscountStrategy implements DiscountStrategy {

    @Override
    public double calcPrice(double productPrice) {
        System.out.println("VIP8折");
        return productPrice*0.8;
    }
}
