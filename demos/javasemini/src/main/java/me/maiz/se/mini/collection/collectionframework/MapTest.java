package me.maiz.se.mini.collection.collectionframework;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapTest {
    public static void main(String[] args) {
        //创建Map
        Map<String,Double> accountMap = new HashMap<>();
        //以键值对的方式放入数据
        accountMap.put("60002", 400.0);
        accountMap.put("60001", 300.0);
        accountMap.put("60003", 500.0);
        System.out.println(accountMap);
        //根据键取出值
        Double d = accountMap.get("60001");
        System.out.println(d);
        //删除
        accountMap.remove("60003");
        System.out.println(accountMap);

        //判断是否存在该key
        System.out.println(accountMap.containsKey("70001"));
        System.out.println(accountMap.containsKey("60001"));
        System.out.println(accountMap.containsValue(300.0));
        System.out.println(accountMap.containsValue(1300.0));

        //获取所有Key
        Set<String> ks = accountMap.keySet();
        for(String s : ks){
            System.out.println(s);
        }
        //获取所有Value
        Collection<Double> vs =accountMap.values();
        for(Double d1 : vs){
            System.out.println(d1);
        }
    }
}
