package me.maiz.trainning.framework.spring.aop.proxy;

import me.maiz.trainning.framework.spring.aop.proxy.jdk.DynamicProxy;

/**
 * Created by Lucas on 2017-01-12.
 */
public class ProxyApp {


    public static void main(String[] args) {
        //静态代理
        UserService userService = new  UserServiceImplProxy();
        userService.deleteUser(1);

        //动态代理
        DynamicProxy dp = new DynamicProxy();
        UserService userService1 = (UserService) dp.getProxy(new UserServiceImpl());
        userService1.deleteUser(1);
        userService1.deleteUser(2);

    }
}
