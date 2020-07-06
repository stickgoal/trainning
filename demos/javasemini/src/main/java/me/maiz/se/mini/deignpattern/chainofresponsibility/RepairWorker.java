package me.maiz.se.mini.deignpattern.chainofresponsibility;

public class RepairWorker extends Worker {
    @Override
    public void workOn(Car car) {
        System.out.println("修理"+car);
    }
}
