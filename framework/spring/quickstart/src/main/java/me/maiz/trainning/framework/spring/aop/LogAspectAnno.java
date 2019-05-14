package me.maiz.trainning.framework.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by Lucas on 2017-01-10.
 */
@Component
@Aspect
public class LogAspectAnno {

    @Pointcut("execution(* me.maiz.trainning.framework.spring.aop.services.*.*(..))")
    public void servicePointcut(){}

    @Before("servicePointcut()")
    public void doSomethingBefore(){
        System.out.println("before方法执行中...");
    }

    @Around("servicePointcut()")
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
