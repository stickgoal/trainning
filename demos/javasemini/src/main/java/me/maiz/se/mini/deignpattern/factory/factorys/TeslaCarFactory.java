package me.maiz.se.mini.deignpattern.factory.factorys;

import me.maiz.se.mini.deignpattern.factory.cars.Car;
import me.maiz.se.mini.deignpattern.factory.cars.TeslaCar;

public class TeslaCarFactory implements AbstractCarFactory {
    @Override
    public Car createCar() {
        TeslaCar teslaCar = new TeslaCar();
        teslaCar.setBrand("Tesla");
        teslaCar.setColor("red");
        return teslaCar;
    }
}
