package me.maiz.trainning.framework.spring.aop.proxy.cglib;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

public class PerformanceMethodInterceptor implements MethodInterceptor {
    private Object target;

    public PerformanceMethodInterceptor(Object target) {
        this.target = target;
    }

    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("执行操作前处理,参数为" + Arrays.toString(args));
        Object result = method.invoke(target, args);
        System.out.println("执行操作后处理"+result);
        return result;
    }
}
