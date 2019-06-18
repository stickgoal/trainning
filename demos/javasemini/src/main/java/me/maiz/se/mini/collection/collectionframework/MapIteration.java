package me.maiz.se.mini.collection.collectionframework;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapIteration {
    public static void main(String[] args) {
        Map<String,Double> accountMap = new HashMap<>();
        accountMap.put("60002", 400.0);
        accountMap.put("60001", 300.0);
        accountMap.put("60003", 500.0);

        Set<String> keys = accountMap.keySet();
        for(String k : keys){
            Double v = accountMap.get(k);
            System.out.println(k+" : "+v);
        }
        System.out.println(accountMap);

        Set<Map.Entry<String, Double>> entrys=  accountMap.entrySet();
        for(Map.Entry<String,Double> e : entrys){
            System.out.println(e.getKey()+"<==>"+e.getValue());
        }
    }
}
