package me.maiz.trainning.framework.spring.anno;

import me.maiz.trainning.framework.spring.anno.impl.UserDAOImpl;
import me.maiz.trainning.framework.spring.anno.impl.UserServiceImpl;
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
        ApplicationContext context =
                new ClassPathXmlApplicationContext("beans-anno.xml");

        UserService userService = (UserService) context.getBean("userService");

        System.out.println(userService.findById(1));
    }



}
