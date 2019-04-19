package me.maiz.training;

import me.maiz.training.model.Person;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 2017-08-02.
 */
public class BeanUtilsDemo {


    public static void main(String[] args) {
        //两个对象之间拷贝，属性名字相同则拷贝
        copy();
        //将Map中的属性自动复制给对象
        populate();

    }

    private static void populate() {
        Map<String,String> properties = new HashMap<String, String>();
        properties.put("name","Lucas");
        properties.put("age","12");
        properties.put("address","Shanghai");
        Person p = new Person();
        try {
            BeanUtils.populate(p,properties);
            System.out.println(p);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private static void copy() {
        Person p1 = new Person();
        p1.setName("Lucas");
        p1.setAddress("CQ");
        p1.setAge(28);
        p1.setNickname("maizi");
        Person p2 = new Person();
        try {
            //同名属性赋值并且自动类型转换
            BeanUtils.copyProperties(p2,p1);
            System.out.println(p2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
