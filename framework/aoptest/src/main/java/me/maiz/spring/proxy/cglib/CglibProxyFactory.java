package me.maiz.spring.proxy.cglib;

import org.springframework.cglib.proxy.Enhancer;

public class CglibProxyFactory  {
    /**
     * 创建一个cglib代理
     * @param target
     * @return
     */
    public static Object createProxy(Object target){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new MyMethodInterceptor());
        return enhancer.create();
    }

}
