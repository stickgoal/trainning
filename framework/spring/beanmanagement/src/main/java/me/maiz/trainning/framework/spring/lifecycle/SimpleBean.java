package me.maiz.trainning.framework.spring.lifecycle;

/**
 * Created by Lucas on 2018-03-09.
 */
public class SimpleBean {


    public void start(){
        System.out.println("bean creation starting");
    }

    public void end(){
        System.out.println("bean is going down~");

    }

}
