package me.maiz.trainning.framework.spring.xml.impl;


import me.maiz.trainning.framework.spring.xml.User;
import me.maiz.trainning.framework.spring.xml.UserService;

/**
 * Created by Lucas on 2017-08-23.
 */
public class FactoryMadeUserServiceImpl implements UserService {

    public static FactoryMadeUserServiceImpl makeOne(){
        System.out.println("自己造自己的方法");
        return new FactoryMadeUserServiceImpl();
    }

    public User findById(int userId) {
        return null;
    }
}
