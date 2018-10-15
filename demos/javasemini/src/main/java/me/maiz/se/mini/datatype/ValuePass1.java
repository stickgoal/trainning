package me.maiz.se.mini.datatype;

public class ValuePass1 {


    public static void main(String[] args) {
        int x=2;
//        calc(x);
//        System.out.println(x);

        x = calc1(x);
        System.out.println(x);
    }

    private static  int  calc1(int x){
        return x+=10;
    }

    private static void calc(int x){
        x=x+10;
    }


//


}
