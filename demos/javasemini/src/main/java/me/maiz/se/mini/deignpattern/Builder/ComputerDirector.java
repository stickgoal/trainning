package me.maiz.se.mini.deignpattern.Builder;

public class ComputerDirector {

    public Computer construct(){
        ComputerBuilder builder = ComputerBuilder.newBuilder();
        Computer computer = builder.buildCPU("Intel")
                .buildScreen(1280,900)
                .buildHardDisk(500).build();
        return computer;
    }

}
