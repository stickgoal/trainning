package me.maiz.spring.component;

import org.aspectj.lang.JoinPoint;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;

public class BeforeAdvisor implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("执行方法["+method.getName()+"] 参数为"+ Arrays.toString(args));
    }

    public void doBefore(JoinPoint joinPoint){
        System.out.println(joinPoint);
    }
}
