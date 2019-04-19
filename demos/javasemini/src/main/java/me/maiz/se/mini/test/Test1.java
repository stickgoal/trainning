package me.maiz.se.mini.test;

public class Test1 {
    public static void main(String args[]){
        char digit = 'a';
        int j=0;
        for (int i = 0; i < 10; i++){
            switch (digit)
            {
                case 'x' :
                {
                    j = 0;
                    System.out.println(j);
                }
                default :
                {
                    j = 100;
                    System.out.println(j);
                }
            }
        }

        int i = j;
        System.out.println(i);
    }

}
