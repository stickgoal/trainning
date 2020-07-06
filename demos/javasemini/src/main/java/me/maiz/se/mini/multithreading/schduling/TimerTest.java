package me.maiz.se.mini.multithreading.schduling;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {

    public static void main(String[] args) {

        Timer timer = new Timer();
        //延迟10秒后执行
        System.out.println("当前时间："+new Date());
        timer.schedule(new PrintTask("延迟10秒"),10000);

        //在某个时间点(当前时间之后的2秒)执行
        System.out.println("当前时间："+new Date());
        timer.schedule(new PrintTask("延迟2秒"),new Date(System.currentTimeMillis()+2000));

        //1秒之后间隔每5秒执行1次
        timer.schedule(new PrintTask("间隔5秒"),1000,5000);
    }

}

//创建一个简单任务，用于打印数据
class PrintTask extends TimerTask{
    //用于区分内容
    private String name;

    PrintTask(String name) {
        this.name = name;
    }
    @Override
    public void run() {
        System.out.println(name+"执行于"+ LocalDateTime.now());
    }
}