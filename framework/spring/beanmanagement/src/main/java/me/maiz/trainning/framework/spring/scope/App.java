package me.maiz.trainning.framework.spring.scope;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Lucas on 2018-03-09.
 */
public class App {
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean-scope.xml");
        DinnerBean dinnerBean1 = ac.getBean("dinnerBeanSingleton",DinnerBean.class);
        DinnerBean dinnerBean2 = ac.getBean("dinnerBeanSingleton",DinnerBean.class);
        DinnerBean dinnerBean3 = ac.getBean("dinnerBeanSingleton",DinnerBean.class);
        System.out.println("==========singleton==========");
        System.out.println(dinnerBean1);
        System.out.println(dinnerBean2);
        System.out.println(dinnerBean3);
        System.out.println("===========================");

        DinnerBean dinnerBean4 = ac.getBean("dinnerBeanPrototype",DinnerBean.class);
        DinnerBean dinnerBean5 = ac.getBean("dinnerBeanPrototype",DinnerBean.class);
        DinnerBean dinnerBean6 = ac.getBean("dinnerBeanPrototype",DinnerBean.class);
        System.out.println("==========prototype==========");
        System.out.println(dinnerBean4);
        System.out.println(dinnerBean5);
        System.out.println(dinnerBean6);
        System.out.println("===========================");

    }
}
