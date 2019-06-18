package me.maiz.trainning.framework.spring;

import me.maiz.trainning.framework.spring.config.AppConfig;
import me.maiz.trainning.framework.spring.web.UserController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        UserController userController = (UserController) ctx.getBean("userController");
        userController.login("z","1");
    }
}
