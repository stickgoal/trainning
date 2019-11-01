package me.maiz.spring.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TxAspect {
    @Pointcut("execution(* me.maiz.spring.service.*.*(..))")
    public void servicePointcut(){}

    @Before("servicePointcut()")
    public void startTx(){
        System.out.println("前置通知");
    }


}
