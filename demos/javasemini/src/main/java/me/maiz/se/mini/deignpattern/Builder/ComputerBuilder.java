package me.maiz.se.mini.deignpattern.Builder;

import me.maiz.se.mini.deignpattern.Builder.component.CPU;
import me.maiz.se.mini.deignpattern.Builder.component.HardDisk;
import me.maiz.se.mini.deignpattern.Builder.component.Screen;

public class ComputerBuilder {

    private Computer computer;

    public ComputerBuilder(){
        this.computer=new Computer();
    }

    public static ComputerBuilder newBuilder(){
        return new ComputerBuilder();
    }

    public ComputerBuilder buildCPU(String brand){
        CPU cpu = new CPU();
        cpu.setBrand(brand);
        computer.setCpu(cpu);
        return this;
    }

    public ComputerBuilder buildScreen(int width , int height){
        Screen screen = new Screen();
        screen.setWidth(width);
        screen.setHeight(height);
        computer.setScreen(screen);
        return this;
    }

    public ComputerBuilder buildHardDisk(int capacity){
        HardDisk hardDisk = new HardDisk();
        hardDisk.setCapacity(capacity);
        computer.setHardDisk(hardDisk);
        return this;
    }

    //...


    public Computer build(){
        return computer;
    }


}
