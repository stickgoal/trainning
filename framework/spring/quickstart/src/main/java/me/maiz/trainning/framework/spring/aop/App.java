package me.maiz.trainning.framework.spring.aop;

import me.maiz.trainning.framework.spring.aop.services.ProductService;
import me.maiz.trainning.framework.spring.aop.services.UserService;
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
                new ClassPathXmlApplicationContext("beans-aop.xml");

        UserService userService = (UserService) context.getBean("userService");
        ProductService productService = (ProductService) context.getBean("productService");

        System.out.println(userService.findById(1));
        productService.insertProduct(1);


        ApplicationContext context2 =
                new ClassPathXmlApplicationContext("beans-aop2.xml");

        UserService userService2 = (UserService) context2.getBean("userService");
        ProductService productService2 = (ProductService) context2.getBean("productService");

        System.out.println(userService2.findById(1));
        productService2.insertProduct(1);
    }



}
