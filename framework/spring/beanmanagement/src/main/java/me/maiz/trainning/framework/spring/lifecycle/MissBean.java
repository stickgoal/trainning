package me.maiz.trainning.framework.spring.lifecycle;

/**
 * Created by Lucas on 2018-03-09.
 */
public class MissBean {

    public void init(){
        System.out.println("a");
    }

    public void initial(){
        System.out.println("initial MissBean");
    }

    public void finish(){
        System.out.println("finish MissBean");
    }


}
