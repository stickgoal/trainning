package me.maiz.se.mini.collection.collectionframework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class CollectionIteration {
    public static void main(String[] args) {
        Collection<String> collection = new ArrayList<>();
        collection.addAll(Arrays.asList("Hello",",","This","is","Lucas","!"));
        //while循环
        Iterator<String> iter = collection.iterator();
        //iterator + while循环
        while(iter.hasNext()){
            String next = iter.next();
            System.out.println(next);
            if(next==null||next.equals("!")){
                iter.remove();
            }
        }

        //增强for循环 (foreach)
        for(String s : collection){
            System.out.println(s);
        }
    }
}
