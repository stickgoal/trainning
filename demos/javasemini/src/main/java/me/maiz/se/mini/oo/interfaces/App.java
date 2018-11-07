package me.maiz.se.mini.oo.interfaces;

public class App {
    public static void main(String[] args) {
        Computer computer = new Computer();
        Projector projector = new Projector();
        computer.sendDisplayToVGA(projector);

        Monitor monitor = new Monitor();
        computer.sendDisplayToVGA(monitor);
    }
}
