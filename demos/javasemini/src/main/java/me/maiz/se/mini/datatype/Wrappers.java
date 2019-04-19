package me.maiz.se.mini.datatype;

public class Wrappers {
    public static void main(String[] args) {
        //拆装箱
        // 原始值  =>  包装器 : 装箱
        Character c = Character.valueOf('A');
        Character c1= new Character('A');
        //自动装箱，使用valueOf方法
        Character c2 = 'A';

        // 包装器  =>  原始值 : 拆箱
        int i = new Integer(20).intValue();
        //自动拆箱
        int i1= new Integer(2);

        //自动拆装箱
        int y = 5;
//		Integer z = Integer.valueOf(y);
        Integer a = y;
//		int e1 = a.intValue();
        int e = a;
    }
}
