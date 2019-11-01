package me.maiz.spring.proxy.cglib;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MyMethodInterceptor implements MethodInterceptor {
    /**
     * 拦截方法，添加
     * @param o  目标对象
     * @param method  被拦截的方法
     * @param args 参数
     * @param methodProxy cglib方法代理对象
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("====CGLIB执行前====");
        Object result = methodProxy.invokeSuper(o, args);
        System.out.println("====CGLIB执行后====");
        return result;
    }
}
