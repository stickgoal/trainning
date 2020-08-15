package me.maiz.trainning.framework.spring.tx;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TxApp {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = ctx.getBean(UserService.class);

//        userService.doSth(1,"liwu");
        userService.doSth(2,"ggggg");

    }


}
