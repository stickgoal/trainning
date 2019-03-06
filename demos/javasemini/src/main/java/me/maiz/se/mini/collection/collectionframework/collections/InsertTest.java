package me.maiz.se.mini.collection.collectionframework.collections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InsertTest {
    public static final int  TIMES = 100000;


    public static void main(String[] args) {
        long before = System.currentTimeMillis();
        List<String> l1 = new ArrayList<>();
        for(int i=0;i<TIMES;i++){
            l1.add(i+"a");
        }
        long after = System.currentTimeMillis();
        long arrayTime = after-before;
        System.out.println(arrayTime);
        List<String> l2 = new LinkedList<>();
        for(int i=0;i<TIMES;i++){
            l2.add(i+"b");
        }
        long after2 = System.currentTimeMillis();
        long linkTime = after2-after;

        System.out.println(linkTime);
    }
}
