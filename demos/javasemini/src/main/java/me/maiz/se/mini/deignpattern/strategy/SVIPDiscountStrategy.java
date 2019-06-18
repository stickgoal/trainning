package me.maiz.se.mini.deignpattern.strategy;

public class SVIPDiscountStrategy implements DiscountStrategy {
    @Override
    public double calcPrice(double productPrice) {
        System.out.println("SVIP 6折 ");
        return productPrice*0.6;
    }
}
