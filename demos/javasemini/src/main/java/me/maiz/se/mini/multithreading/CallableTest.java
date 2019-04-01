package me.maiz.se.mini.multithreading;

import me.maiz.se.mini.multithreading.threadpool.buildin.MyRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class  CallableTest{

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> ft = new FutureTask<>(new MyCallable());
        Thread thread = new Thread(ft);
        thread.start();
        String result = ft.get();
        System.out.println(result);
    }

}

class MyCallable implements Callable<String> {

    @Override
    public String call() throws Exception {
        System.out.println("开始调用");
        Thread.sleep(1000);
        return "So,My head is kind of cold.";
    }
}


