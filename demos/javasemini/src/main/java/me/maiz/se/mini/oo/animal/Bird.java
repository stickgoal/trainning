package me.maiz.se.mini.oo.animal;

public class Bird extends Animal {

    public Bird(){
        System.out.println("child constructor ");
    }

    @Override
    public void move() {
        System.out.println("bird flying︿(￣︶￣)︿");
    }
}
