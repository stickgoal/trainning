package me.maiz.se.mini.oo;

public class RiceCooker {

    private Monitor monitor=new Monitor();

    {
        System.out.println("代码块");
    }

    static{
        System.out.println("静态代码块");
    }

    public RiceCooker(){
        System.out.println("构造器");
    }


    public static void main(String[] args) {

    }

}
