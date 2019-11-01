package me.maiz.spring.proxy.jdk;

import java.lang.reflect.Proxy;

/**
 * 代理工厂，创建动态代理
 */
public class ProxyFactory {
    /**
     * 为传入的参数对象创建代理对象
     * @param target 目标对象
     * @return
     */
    public static Object createProxy(Object target){

        Class<?> targetClass = target.getClass();
        //参数详解：
        // classloader 类加载器
        // interfaces  该类实现的接口
        // invocationHandler 调用处理器  包含了方法调用要增加的功能
        return Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(),new TransactionHandler(target));

    }

}
