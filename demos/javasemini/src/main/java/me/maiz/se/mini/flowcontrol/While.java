package me.maiz.se.mini.flowcontrol;

public class While {

    public static void main(String[] args) {
        calcDo100();


    }

    private static void calc100() {
        int sum=0;
        int i=0;
        while(i<=100){
            sum=sum+i;
            i++;
        }
        System.out.println("从1加到100的总和为：" + sum);
    }

    private static void calcDo100() {
        int sum=0;
        int i=0;
       do{
            sum=sum+i;
            i++;
        } while(i<=100);
        System.out.println("从1加到100的总和为：" + sum);
    }

}
