package me.maiz.project.highconcurrency.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.TreeSet;

@Data
@AllArgsConstructor
public class Dog implements Comparable<Dog>{

    String name;

    int age;

    String category;

    @Override
    public int compareTo(Dog o) {
        //按年龄排序
        return this.age-o.age;
    }

    public static void main(String[] args) {
        Dog d1 = new Dog("Lash",10,"苏格兰牧羊犬");
        Dog d2 = new Dog("Snoopy",120,"阿拉斯加");
        Dog d3 = new Dog("Wangcai",5,"中华田园犬");

        TreeSet<Dog> dogs = new TreeSet<>();
        dogs.add(d1);
        dogs.add(d2);
        dogs.add(d3);
        System.out.println(dogs);

        TreeSet<Dog> dogs2 = new TreeSet<>((o1, o2) -> o1.name.compareTo(o2.name));

        dogs2.add(d2);
        dogs2.add(d3);
        dogs2.add(d1);
        System.out.println(dogs2);

    }
}
