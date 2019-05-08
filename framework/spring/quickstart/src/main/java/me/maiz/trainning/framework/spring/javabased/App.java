package me.maiz.trainning.framework.spring.javabased;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        UserService userService = (UserService) context.getBean("userService");

        System.out.println(userService.findById(1));

        ApplicationContext context2 =
                new AnnotationConfigApplicationContext(AppConfigScan.class);

        UserService userService2 = (UserService) context.getBean("userService");

        System.out.println(userService2.findById(1));
    }



}
