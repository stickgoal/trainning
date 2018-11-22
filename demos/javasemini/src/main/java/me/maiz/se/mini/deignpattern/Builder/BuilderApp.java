package me.maiz.se.mini.deignpattern.Builder;

public class BuilderApp {
    public static void main(String[] args) {
        ComputerDirector computerDirector = new ComputerDirector();
        computerDirector.construct();
    }
}
