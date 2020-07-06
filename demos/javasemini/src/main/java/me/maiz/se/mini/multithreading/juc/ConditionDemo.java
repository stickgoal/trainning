package me.maiz.se.mini.multithreading.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionDemo {

}
class BoundedBuffer {

    final Lock lock = new ReentrantLock();
    //设置两个锁定条件
    // notFull在队列满时锁定 等待队列不满
    final Condition notFull  = lock.newCondition();
    //notEmpty在队列为空时锁定 等待队列不空
    final Condition notEmpty = lock.newCondition();
    //固定长度为100的存储队列
    final Object[] items = new Object[100];
    //三个int值维护队列状态
    //当前放入的位置下标
    int putptr;
    //当前取出的位置下标
    int takeptr;
    //队列内部的总长度
    int count;

    //放入数据
    public void put(Object x) throws InterruptedException {
        lock.lock();
        try {
            //队列满了，并发情况下用while代替if判断
            while (count == items.length) {
                //等待，直到队列不满
                notFull.await();
            }
            items[putptr] = x;
            if (++putptr == items.length) putptr = 0;
            ++count;
            //通知队列不空
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            //队列为空，不能取出，等待
            while (count == 0)
                notEmpty.await();
            Object x = items[takeptr];
            if (++takeptr == items.length) takeptr = 0;
            --count;
            //通知队列此时不满
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}
