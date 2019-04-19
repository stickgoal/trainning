package me.maiz.se.mini.oo.animal;

public class Fish extends Animal {

    private  int speed;
    private String name;

    public Fish(String name,int age){
        super(age);
        System.out.println("b");

        this.name=name;
    }
    public Fish(String name,int speed,int age){
        this(name,age);
        System.out.println("c");
        this.speed=speed;
    }

    @Override
    public void goHome() {
        super.move();
    }

    public void move(){
        System.out.println("鱼在游");
    }



}
