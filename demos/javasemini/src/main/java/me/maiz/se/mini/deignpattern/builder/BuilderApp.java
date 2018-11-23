package me.maiz.se.mini.deignpattern.builder;

public class BuilderApp {
    public static void main(String[] args) {
        ComputerDirector computerDirector = new ComputerDirector();
        computerDirector.construct();
    }
}
