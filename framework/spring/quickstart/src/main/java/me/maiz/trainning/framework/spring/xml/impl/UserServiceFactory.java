package me.maiz.trainning.framework.spring.xml.impl;

import me.maiz.trainning.framework.spring.xml.UserService;

/**
 * Created by Lucas on 2017-08-23.
 */
public class UserServiceFactory {

    public UserService makeUserService(){
        System.out.println("工厂制造userService中");
        return new FactoryMadeUserServiceImpl();
    }

}
