package me.maiz.se.mini.oo.animal;

public class Animal {
    private int age;

    public Animal(){
        System.out.println("parent constructor");
    }

    public  Animal(int age){
        this();
        System.out.println("a");
        this.age=age;
    }

    public void goHome(){
        this.move();
    }
    /**
     * 移动
     */
    public void move(){
        System.out.println("animal moving");
    }

}
