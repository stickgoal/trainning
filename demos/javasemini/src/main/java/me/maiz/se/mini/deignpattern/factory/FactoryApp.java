package me.maiz.se.mini.deignpattern.factory;

import me.maiz.se.mini.deignpattern.factory.cars.Car;
import me.maiz.se.mini.deignpattern.factory.factorys.AbstractCarFactory;
import me.maiz.se.mini.deignpattern.factory.factorys.BenzCarFactory;
import me.maiz.se.mini.deignpattern.factory.factorys.TeslaCarFactory;

public class FactoryApp {

    public static void main(String[] args) {
        AbstractCarFactory carFactory = new BenzCarFactory();
        Car car = carFactory.createCar();
        System.out.println(car.getBrand()+":"+car.getType());

        AbstractCarFactory carFactory1 = new TeslaCarFactory();
        Car car1 = carFactory1.createCar();
        System.out.println(car1.getBrand()+":"+car1.getType());
    }

}
