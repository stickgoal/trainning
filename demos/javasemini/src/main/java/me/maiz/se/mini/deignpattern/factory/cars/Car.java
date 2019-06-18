package me.maiz.se.mini.deignpattern.factory.cars;

public abstract class Car {

    private String brand;

    private String color;

    public abstract String getType();

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


}
