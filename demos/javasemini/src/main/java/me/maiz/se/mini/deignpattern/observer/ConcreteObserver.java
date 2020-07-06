package me.maiz.se.mini.deignpattern.observer;

public class ConcreteObserver implements Observer {

    private String name;

    public ConcreteObserver(String name){
        this.name=name;
    }

    @Override
    public void update(String message) {
        System.out.println(name+"收到更新信息："+message);
    }
}
