package me.maiz.trainning.framework.spring.aop.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect//标识是一个切面
public class TxAspect {

    //pointcut 切入点
    //切入点表达式
    //execution(* com.woniuxy.cq.service.*.*(..))
    //       返回值                 所有类 所有方法 所有参数
    @Pointcut("execution(* com.woniuxy.cq.service.*.*(..))")
    public void allService(){}


    //通知: 要加入的功能
    @Before("allService()")
    public void doBefore(JoinPoint joinPoint){
        //方法签名
        String methodName = joinPoint.getSignature().getName();
        //参数
        Object[] args = joinPoint.getArgs();
        //目标对象
       String targetName= joinPoint.getTarget().getClass().getName();

        System.out.println("访问"+targetName+"."+methodName+" 参数为 "+(args!=null? Arrays.toString(args):""));

    }

}
