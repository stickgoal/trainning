package me.maiz.trainning.framework.spring.aop.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Created by Lucas on 2017-01-12.
 */
public class DynamicProxy implements InvocationHandler {

    private Object target;

    /**
     * 获得代理对象
     *
     * @param target 被代理的对象
     * @return
     */
    public Object getProxy(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    /*
     * 执行方法的拦截器，在对象上执行某个方法都会调用该方法
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        Object firstArg = args[0];
        if ((firstArg instanceof Integer) && ((Integer) firstArg) == 1) {
            System.err.println("不允许执行");
        } else {
            System.out.println("执行操作前处理,参数为" + Arrays.toString(args));
            result = method.invoke(target, args);
            System.out.println("执行操作后处理");
        }
        return result;
    }
}
