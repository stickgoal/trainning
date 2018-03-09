package me.maiz.trainning.framework.spring.lifecycle;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Lucas on 2018-03-09.
 */
public class App {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean-lifecycle.xml");
       /* SimpleBean simpleBean = ac.getBean("simpleBean",SimpleBean.class);
        MrBean mrBean = ac.getBean("mrBean",MrBean.class);
        ac.getBean()*/
        System.out.println("准备关闭...");
        ac.close();
    }
}
