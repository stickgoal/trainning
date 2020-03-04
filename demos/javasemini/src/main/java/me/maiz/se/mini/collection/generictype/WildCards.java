package me.maiz.se.mini.collection.generictype;

import java.util.Arrays;
import java.util.List;

public class WildCards {

    public static double sumOfList(List<? extends Number> list) {
        double s = 0.0;
        for (Number n : list)
            s += n.doubleValue();
        return s;
    }

    public static void addNumbers(List<? super Integer> list) {
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
    }

    public static void printList1(List<Object> list) {
        for (Object elem : list)
            System.out.println(elem + " ");
        System.out.println();
    }

    public static void printList2(List<?> list) {
        for (Object elem : list)
            System.out.println(elem + " ");
        System.out.println();
    }

    public static void main(String[] args) {
        List<Integer> li = Arrays.asList(1, 2, 3);
        System.out.println("sum = " + sumOfList(li));

        List<Double> ld = Arrays.asList(1.2, 2.3, 3.5);
        System.out.println("sum = " + sumOfList(ld));

// 编译报错，因为 List<Integer> 与 List<Object> 并不是继承关系，无法传入
//       printList1(li);
        //可以传入
        printList2(li);
    }
}
