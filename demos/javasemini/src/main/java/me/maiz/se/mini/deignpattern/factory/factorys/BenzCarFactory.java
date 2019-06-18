package me.maiz.se.mini.deignpattern.factory.factorys;

import me.maiz.se.mini.deignpattern.factory.cars.BenzCar;
import me.maiz.se.mini.deignpattern.factory.cars.Car;

public class BenzCarFactory implements AbstractCarFactory {
    @Override
    public Car createCar() {
        BenzCar benzCar = new BenzCar();
        benzCar.setBrand("benz");
        benzCar.setColor("red");
        return benzCar;
    }
}
