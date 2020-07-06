package me.maiz.spring;

import me.maiz.spring.service.User;
import me.maiz.spring.service.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       /* ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("aop-config.xml");
        UserService userService = context.getBean("userService", UserService.class);
        userService.addUser(new User());*/
        
        
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("aop-anno.xml");
        UserService userService1 = ctx.getBean("userService", UserService.class);
        userService1.addUser(new User());

        userService1.addUser(null);
    }
}
