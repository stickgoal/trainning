package me.maiz.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TxAspect {
    @Pointcut("execution(* me.maiz.spring.service.impl.*.*(..))")
    public void servicePointcut(){}

    @Before("servicePointcut()")
    public void startTx(JoinPoint joinPoint){
        System.out.println("前置通知"+joinPoint);
    }

    @AfterReturning("servicePointcut()")
    public void commit(JoinPoint joinPoint){
        System.out.println("后置通知"+joinPoint);
    }

    @AfterThrowing("servicePointcut()")
    public void rollback(JoinPoint joinPoint){
        System.out.println("异常通知"+joinPoint);
    }

    @After("servicePointcut()")
    public void close(JoinPoint joinPoint){
        System.out.println("finally通知"+joinPoint);
    }

    @Around("servicePointcut()")
    public void doIt(ProceedingJoinPoint proceedingJoinPoint){

        try {
            System.out.println("before");
            proceedingJoinPoint.proceed();
            System.out.println("after");
        } catch (Throwable throwable) {
            System.out.println("异常");
            throwable.printStackTrace();
        }finally {
            System.out.println("finally");
        }
    }

}
