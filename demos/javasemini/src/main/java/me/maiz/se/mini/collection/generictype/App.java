package me.maiz.se.mini.collection.generictype;

import java.util.Date;

public class App {
    public static void main(String[] args) {
        Container c = new Container();
        c.add(13);
        Integer i = (Integer) c.get();
        System.out.println(i);
        c.add(new Date());
        Date date = (Date) c.get();

//        c.add(new Date());
//        Integer d = (Integer) c.get();

        TypeSafeContainer<Integer> tsc = new TypeSafeContainer<>();
        tsc.add(13);
        Integer i1 = tsc.get();
//        tsc.add(new Date());

        TypeSafeContainer<Date> tsc2 = new TypeSafeContainer<>();
        tsc2.add(new Date());
        Date date2 = tsc2.get();
//        tsc2.add(13);


        TypeSafeContainer tsc3 = new TypeSafeContainer();
        tsc3.add(new Date());
        Integer i3 = (Integer) tsc3.get();
    }
}
