package me.maiz.se.mini.collection.collectionframework.collections;

public class Person implements Comparable<Person> {

    private String name;
    private int age;
    private String address;


    @Override
    public int compareTo(Person o) {
        return this.age - o.age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
