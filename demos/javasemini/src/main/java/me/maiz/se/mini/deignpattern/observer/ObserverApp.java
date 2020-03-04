package me.maiz.se.mini.deignpattern.observer;

public class ObserverApp {

    public static void main(String[] args) {
        Subject subject = new ConcreteSubject();
        Observer o1 = new ConcreteObserver("1号");
        Observer o2 = new ConcreteObserver("2号");
        Observer o3 = new ConcreteObserver("3号");
        Observer o4 = new ConcreteObserver("4号");
        subject.attach(o1);
        subject.attach(o2);
        subject.attach(o3);
        subject.attach(o4);

        subject.notifyChanged("开始招聘啦~");

        subject.detach(o2);

        subject.notifyChanged("结束招聘啦");
    }


}
