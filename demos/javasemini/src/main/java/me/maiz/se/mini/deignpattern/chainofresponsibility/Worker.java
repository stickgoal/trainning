package me.maiz.se.mini.deignpattern.chainofresponsibility;


public abstract class Worker {

    private Worker next;

     void process(Car car){
        workOn(car);
        if(this.next!=null) {
            this.next.process(car);
        }
    }

    protected abstract void workOn(Car car);

    void setNext(Worker worker){
        this.next =worker;
    }
}
