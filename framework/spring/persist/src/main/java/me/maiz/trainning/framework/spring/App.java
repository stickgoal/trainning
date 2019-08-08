package me.maiz.trainning.framework.spring;

import me.maiz.trainning.framework.spring.model.User;
import me.maiz.trainning.framework.spring.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean-tx.xml");
        UserService userService= ac.getBean("userService", UserService.class);
       /* User user = new User();
        user.setUserId(new Date().getTime()+"");
        user.setName("声明式");
        userService.registerDeclaration(user);*/
//        User user1 = new User();
//        user1.setUserId(new Date().getTime()+"");
//        user1.setName("编程式");
//        userService.registerProgrammatic(user1);

        User user1 = new User();
        user1.setUserId(new Date().getTime()+"");
        user1.setName("required");

        userService.propagationRequired(user1);

    }
}
