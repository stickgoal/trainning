package me.maiz.trainning.framework.spring.xml;

import me.maiz.trainning.framework.spring.xml.impl.UserDAOImpl;
import me.maiz.trainning.framework.spring.xml.impl.UserServiceImpl;
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
        springVersion();
//    stupidVersion();
//        javaSEVersion();
    }

    /**
     * 容器帮你管
     */
    private static void springVersion() {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"beans.xml"});

        UserService userService = (UserService) context.getBean("userService");
        UserService userService0 = (UserService) context.getBean("userService");
        UserService userService1 = (UserService) context.getBean("userService1");
        UserService factoryMadeUserService = (UserService) context.getBean("factoryUserServiceImpl");
        UserService factoryMadeUserService2 = (UserService) context.getBean("factoryUserServiceImpl2");

        System.out.println(userService);
        System.out.println(userService0);
        System.out.println(userService1);
        System.out.println(factoryMadeUserService);
        System.out.println(factoryMadeUserService2);

        System.out.println(userService.findById(1));
    }


    public static void stupidVersion(){
        UserService userService = new UserServiceImpl();
        userService.findById(1);//NullPointerException
    }

    /**
     * 手动填充各种实现，依赖管理复杂
     */
    public static  void javaSEVersion(){
        UserDAOImpl userDAOImpl = new UserDAOImpl();
        userDAOImpl.setUsername("wanger");

        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.setUserDAO(userDAOImpl);
        UserService u = userServiceImpl;
        System.out.println(u.findById(1));
    }

}
