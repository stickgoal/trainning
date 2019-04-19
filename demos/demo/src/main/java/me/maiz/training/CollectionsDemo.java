package me.maiz.training;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.apache.commons.collections.map.LinkedMap;

/**
 * 参看 http://blog.csdn.net/terryzero/article/details/4321763
 * Created by Lucas on 2017-08-02.
 */
public class CollectionsDemo {

    public static void main(String[] args) {
        //带排序的map
        OrderedMap map = new LinkedMap();
        map.put("FIVE", "5");
        map.put("SIX", "6");
        map.put("SEVEN", "7");
        System.out.println(map);
        //取出后可以找到下一个key
        map.firstKey();  // 返回 "FIVE"
        map.nextKey("FIVE");  // 返回 "SIX"
        map.nextKey("SIX");  // 返回 "SEVEN"

        //双向Map,可以通过键取到值，也可以通过值取到键
        BidiMap bidi = new TreeBidiMap();
        bidi.put("FIVE", "5");
        bidi.put("SIX", "6");
        bidi.put("SEVEN", "7");
        bidi.get("SIX");  // 返回 "6"
        bidi.getKey("6");  // 返回 "SIX"
        bidi.removeValue("6");  // 移除6这个键值对
        System.out.println(bidi);
        //反向
        BidiMap inverse = bidi.inverseBidiMap();
        System.out.println(inverse);
        //遍历
        MapIterator mapIterator = inverse.mapIterator();
        while(mapIterator.hasNext()){
            System.out.print(mapIterator.next()+"="+mapIterator.getValue()+",");
        }
        System.out.println();

        //Bag接口，可以存放若干个备份
        Bag bag = new HashBag();
        bag.add("ONE", 6);  // 添加6个 ONE
        System.out.println(bag);
        bag.remove("ONE", 2);  // 移除2个"ONE"
        System.out.println(bag);
        System.out.println(bag.getCount("ONE"));  // 返回4,6个移除了2个



    }



}
