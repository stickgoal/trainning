package me.maiz.trainning.demo.redisbootdemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomIt {

    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        System.out.println(numbers);
    }
}
