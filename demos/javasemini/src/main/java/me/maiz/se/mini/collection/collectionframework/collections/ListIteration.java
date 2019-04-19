package me.maiz.se.mini.collection.collectionframework.collections;

import java.util.ArrayList;
import java.util.List;

public class ListIteration {
    public static void main(String[] args) {
        List<String>  todo = new ArrayList<>();
        todo.add("buy food");
        todo.add("clean house");
        todo.add("call mum&daddy");
        todo.add("take a snap");
        System.out.println(todo);
        System.out.println(todo.get(2));
        for (int i = 0; i < todo.size(); i++) {
            System.out.println(todo.get(i));
        }
        todo.remove(1);
        System.out.println(todo);
    }
}
