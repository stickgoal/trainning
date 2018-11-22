package me.maiz.se.mini.deignpattern.Builder.component;

public class CPU implements Component {

    private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "CPU{" +
                "brand='" + brand + '\'' +
                '}';
    }
}
