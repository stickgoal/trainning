package me.maiz.se.mini.collection.collectionframework;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionTest {
    public static void main(String[] args) {
        Collection<Integer> c = new ArrayList<>();
        //添加
        c.add(1);
        c.add(3);
        c.add(5);
        System.out.println(c);
        //移除
        c.remove(2);
        c.remove(3);
        System.out.println(c);
        //包含
        System.out.println("c.contains(3) "+c.contains(3));
        System.out.println("c.contains(5) "+c.contains(5));
        //判断是否为空
        System.out.println(c.isEmpty());
        //获得元素个数
        System.out.println(c.size());
        //清空
        c.clear();
        System.out.println(c);
        System.out.println(c.isEmpty());
        System.out.println(c.size());

        Collection<Integer> c2 = new ArrayList<>();
        c2.add(7);
        c2.add(8);
        c2.add(9);
        //把另一个集合添加进本集合
        c.addAll(c2);
        System.out.println(c);

        c.add(2);
        c.add(3);
        c.add(5);
        System.out.println(c);
        //把另一个集合中的元素全都移除
        c.removeAll(c2);
        System.out.println(c);
        c.addAll(c2);
        System.out.println(c);
        //把另一个集合中的元素保留,移除其他的元素
        c.retainAll(c2);
        System.out.println(c);
    }
}
