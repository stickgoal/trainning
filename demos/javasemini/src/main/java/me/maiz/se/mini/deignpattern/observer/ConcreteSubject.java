package me.maiz.se.mini.deignpattern.observer;

import java.util.ArrayList;
import java.util.List;

public class ConcreteSubject implements Subject {

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyChanged(String message) {
        for (Observer o:observers){
            o.update(message);
        }
    }
}
