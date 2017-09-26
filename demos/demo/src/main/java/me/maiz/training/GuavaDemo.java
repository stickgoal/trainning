package me.maiz.training;

import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucas on 2017-08-02.
 */
public class GuavaDemo {

    public static void main(String[] args) {
        //字符串的一些操作
        strings();
        //集合类的一些操作
        collections();
        //前提条件判断
        preconditions();

    }

    private static void collections() {
        //静态工厂方法创建Map，自带类型推断
        Map<String,Map<Integer,List<String>>> map = Maps.newHashMap();
        List<Map<String,Date>> list = Lists.newArrayList();

        //可以指定起始数据
        List<String> list2 = Lists.newArrayList("a","b","c");

        //支持生成某类List
        List countUp = Ints.asList(1, 2, 3, 4, 5);

        //倒转
        List countDown = Lists.reverse(countUp); // {5, 4, 3, 2, 1}

        //分片
        List<List> parts = Lists.partition(countUp, 2);//{{1,2}, {3,4}, {5}}

        //根据某个属性生成map
        List<String> strings = Lists.newArrayList("Sring","A","Fx");
        ImmutableMap<Integer, String> stringsByIndex = Maps.uniqueIndex(strings,
                new Function<String, Integer>() {
                    public Integer apply(String string) {
                        return string.length();
                    }
                });
        System.out.println("stringsByIndex"+stringsByIndex);

        //方便的生成Map
        ImmutableMap<String,Integer> im = ImmutableMap.of("a",1,"b",2,"c",3);
        System.out.println(im.get("a"));
        System.out.println(im);

        //方便的生成List，builder风格
        ImmutableList<Object> s = ImmutableList.builder().add("a").add("b").add("c").build();
        System.out.println(s.get(1));
        System.out.println(s);

        //方便的生成List，of风格
        ImmutableList<String> list1 = ImmutableList.of("c", "d", "e", "f");
        System.out.println(list1);
    }

    private static void preconditions() {
        //前提条件判断，如果参数不合法则抛出异常,其中的判断条件为boolean值
        //此例会抛出异常
        Preconditions.checkArgument("".length()>10,"长度没达到要求");
    }

    private static void strings() {
        //guava中的Strings操作
        //是否为null或者空字符串
        System.out.println(Strings.isNullOrEmpty(""));
        System.out.println(Strings.isNullOrEmpty(null));
        System.out.println(Strings.isNullOrEmpty(" "));

        //如果是空字符串转换成null
        System.out.println(Strings.emptyToNull(""));

        //如果是null转成空字符串
        System.out.println(Strings.nullToEmpty(null));

        //从头开始比较一样的开头，此例输出th
        System.out.println(Strings.commonPrefix("this.java", "that.java"));

        //从头开始比较一样的结尾，此例输出.java
        System.out.println(Strings.commonSuffix("this.java", "that.java"));

        //使用给定的字符填满指定的长度，填充头部
        System.out.println(Strings.padStart("xxx", 10, '*'));

        //使用给定的字符填满指定的长度，填充尾部
        System.out.println(Strings.padEnd("xxx", 10, '*'));

        //字符串拼接
        Joiner joiner = Joiner.on(",").skipNulls();
        String joined = joiner.join("Harry", null, "Ron", "Hermione");
        System.out.println(joined);

        //字符串拆分
        Iterable<String> splitted = Splitter.on(",").trimResults().omitEmptyStrings().split("foo,bar,,   qux");
        System.out.println(splitted);

    }

}
