package me.maiz.trainning.framework.spring.aop.declaretivetx;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Aspect
@Component
public class TxAspect {
    @Pointcut("@annotation(me.maiz.trainning.framework.spring.aop.declaretivetx.Transactional)")
    public void txAnno(){}

    @Around("txAnno()")
    public Object exec(ProceedingJoinPoint pjp){
        Object result = null;
        Connection connection = null;
        try {
            //打开事务
            connection = DBUtils.getConnection();
            connection.setAutoCommit(false);
            
            result = pjp.proceed();
             //关闭事务
            connection.commit();
            return result;
        } catch (Throwable throwable) {
            if(connection!=null) {
                try {
                    connection.rollback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            throw new RuntimeException(throwable);
        }finally {
            DBUtils.close(connection);
        }
    }

}
