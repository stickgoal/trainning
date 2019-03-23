package me.maiz.se.mini.multithreading;

public class TheadOperation {
    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            for (int i = 0; i < 10; i++) {
                String tName = Thread.currentThread().getName();
                System.out.println(tName+" "+i+System.currentTimeMillis());
                try {
                    //使得线程休眠
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.setName("counting-thread");
        t1.start();
    }
}
