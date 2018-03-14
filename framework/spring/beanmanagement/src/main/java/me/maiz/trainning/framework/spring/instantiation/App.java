package me.maiz.trainning.framework.spring.instantiation;

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
    	ApplicationContext ctx = new ClassPathXmlApplicationContext("bean-instantiation.xml");
    	Object car = ctx.getBean("car");
    	System.out.println(">>>"+car);
    	
    	Object carFactory = ctx.getBean("carFactory");
    	System.out.println(">>>"+carFactory);
    	
    	Object car2 = ctx.getBean("car2");
    	System.out.println(">>>"+car2);

    }
}
