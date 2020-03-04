package me.maiz.se.mini.deignpattern.chainofresponsibility;

public class WashWorker extends Worker {
    @Override
    public void workOn(Car car) {
        System.out.println("清洗"+car);
    }

}
