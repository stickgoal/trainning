package me.maiz.trainning.framework.spring.injection;

import me.maiz.trainning.framework.spring.injection.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        UserService userService= ac.getBean("userService", UserService.class);
        userService.findByName("xxxx");
    }
}
