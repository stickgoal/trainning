package me.maiz.trainning.framework.spring.aop.proxy.cglib;

import me.maiz.trainning.framework.spring.aop.services.UserServiceImpl;
import org.springframework.cglib.proxy.Enhancer;

public class DynamicProxyCglib {

    public Object getProxy(Object target){

        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new PerformanceMethodInterceptor(target));
        enhancer.setSuperclass(target.getClass());
        return enhancer.create();
    }

    public static void main(String[] args) {
        UserServiceImpl userService = (UserServiceImpl) new DynamicProxyCglib().getProxy(new UserServiceImpl());
        userService.findById(1);

    }

}
