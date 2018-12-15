package me.maiz.se.mini.collection.collectionframework.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CmparatorTest {
    public static void main(String[] args) {
        List<Person> personList = new ArrayList<>();
        //在类外部，指定排序方式
        Collections.sort(personList, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.getName().compareTo(o1.getName());
            }
        });
    }
}
