package me.maiz.trainning.framework.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;

/**
 * Created by Lucas on 2017-01-10.
 */
public class LogAspectXML {


    public void doSomethingBefore(){
        System.out.println("before方法执行中...");
    }

    public Object doAround(ProceedingJoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println(className+"  around方法执行中，"+ Arrays.toString(args));
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }



}
