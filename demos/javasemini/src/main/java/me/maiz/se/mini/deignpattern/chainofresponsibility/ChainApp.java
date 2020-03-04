package me.maiz.se.mini.deignpattern.chainofresponsibility;

public class ChainApp {

    public static void main(String[] args) {
        Car car = new Car();
        Worker zhangsan = new RepairWorker();
        Worker lisi = new RepairWorker();
        Worker wangwu = new WashWorker();
        zhangsan.setNext(lisi);
        lisi.setNext(wangwu);

        zhangsan.process(car);
    }

}
