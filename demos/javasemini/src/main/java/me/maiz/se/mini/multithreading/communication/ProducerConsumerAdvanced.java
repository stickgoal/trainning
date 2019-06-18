package me.maiz.se.mini.multithreading.communication;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProducerConsumerAdvanced {

    public static void main(String[] args) {
        //blockingQueue扮演仓库角色,ArrayBlockingQueue是定长的，可以演示出队列已满的情况
        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<Integer>(20);

        new Producer(buffer).start();
        new Consumer(buffer).start();

    }


}

class Producer extends  Thread{
    private BlockingQueue<Integer> buffer;

    public Producer(BlockingQueue buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        System.out.println("生产者生产...");
        while(true) {
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName() + " 生产数据" + i);
                try {
                    //BlockingQueue的put方法会在队列已满时阻塞等待
                    buffer.put(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " 加入数据" + i);
            }
        }
    }
}

class Consumer extends Thread{
    private BlockingQueue<Integer> buffer;

    public Consumer(BlockingQueue buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while(true) {
            for (int i = 0; i < 10; i++) {
                try {
                    //BlockingQueue的take方法会在没有数据的时候阻塞等待
                    System.out.println(Thread.currentThread().getName() + " 消费数据" + buffer.take());
                    TimeUnit.SECONDS.sleep(1);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
