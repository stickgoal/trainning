package me.maiz.se.mini.deignpattern.observer;

public interface Subject {

    void attach(Observer observer);

    void detach(Observer observer);

    void notifyChanged(String message);

}
