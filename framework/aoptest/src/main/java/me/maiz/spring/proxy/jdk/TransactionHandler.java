package me.maiz.spring.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 调用处理器，在方法调用时会先进入该handler，拦截方法的执行和返回
 */
public class TransactionHandler implements InvocationHandler {


    private Object target;

    public TransactionHandler(Object target){
        this.target=target;
    }

      /**
     *
     * @param proxy   代理实例
     * @param method  被代理的方法
     * @param args    方法参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("方法执行前...");
        Object result = method.invoke(target, args);
        System.out.println("方法执行后后...");
        return result;
    }
}
