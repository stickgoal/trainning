package me.maiz.se.mini.flowcontrol;

public class For {
    public static void main(String[] args) {
        int sum = 0;
        for (int i = 1; i <= 100; i++) {
            sum = sum + i;
        }
        System.out.println("从1加到100的总和为：" + sum);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < row; col++) {
                System.out.print("*");
            }
            System.out.println();
        }


//        for(;;){
//            System.out.println("6");
//        }
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print(i + "*" + j + "=" + (i * j) + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < 5; i++) {
            //判断i是偶数
            if (i % 2 == 0) {
                //如果i是偶数，则继续下一次循环
                continue;
            }
            //输出i的值
            System.out.println("i=" + i);
        }

        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                //当i==j时，继续j循环
                if(i==j){
                    continue;
                }
                System.out.println("i="+i+" j="+j);
            }
            System.out.println("结束i循环的第"+i+"次循环");
        }
        System.out.println("结束i循环");

        for(int i=0;i<5;i++){
            //判断i是偶数
            if(i%2!=0){
            //如果i不是偶数，则终止循环
                break;
            }
            //输出i的值
            System.out.println("i="+i);
        }

        System.out.println("=======");

        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
            //当i==j时，终止j循环
                if(i==j){
                    break;
                }
                System.out.println("i="+i+" j="+j);
            }
            System.out.println("结束i循环的第"+i+"次循环");
        }
        System.out.println("结束i循环");

    }


}
