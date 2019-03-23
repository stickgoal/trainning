package me.maiz.se.mini.multithreading;

public class RaceCondition {
    private int a = 0;
    public void add(){
        a=a+1;
    }
}
