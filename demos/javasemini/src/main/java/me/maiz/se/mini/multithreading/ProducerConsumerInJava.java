package me.maiz.se.mini.multithreading;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * http://www.importnew.com/16453.html
 *
 * Simple Java program to demonstrate How to use wait, notify and notifyAll()
 * method in Java by solving producer consumer problem.
 *
 * @author Javin Paul
 */
public class ProducerConsumerInJava {
    public static void main(String args[]) {
        Queue<Integer> buffer = new LinkedList<>();
        int maxSize = 10;
        Thread producer = new Producer(buffer, maxSize, "PRODUCER");
        Thread consumer = new Consumer(buffer, maxSize, "CONSUMER");
        producer.start();
        consumer.start();
    }
}

/**
 * 生产者线程
 */
class Producer extends Thread {
    private Queue<Integer> queue;
    private int maxSize;

    public Producer(Queue<Integer> queue, int maxSize, String name) {
        super(name);
        this.queue = queue;
        this.maxSize = maxSize;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.size() == maxSize) {
                    try {
                        print("队列已满，停止生产，线程进入等待");
                        queue.wait();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                Random random = new Random();
                int i = random.nextInt();
                print("生产: " + i);
                queue.add(i);
                queue.notifyAll();
            }
        }
    }

    private void print(String s) {
        System.out.println("【"+Thread.currentThread().getName()+"】"+s);
    }
}

/**
 * 消费者线程
 */
class Consumer extends Thread {
    private Queue<Integer> queue;
    private int maxSize;

    public Consumer(Queue<Integer> queue, int maxSize, String name) {
        super(name);
        this.queue = queue;
        this.maxSize = maxSize;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    print("空队列，线程进入等待，等待生产者产生数据");
                    try {
                        queue.wait();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                print("消费" + queue.remove());
                queue.notifyAll();
            }
        }
    }

    private void print(String s) {
        System.out.println("【"+Thread.currentThread().getName()+"】"+s);
    }
}

