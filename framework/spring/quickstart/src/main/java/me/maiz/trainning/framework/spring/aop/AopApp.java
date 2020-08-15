package me.maiz.trainning.framework.spring.aop;

import me.maiz.trainning.framework.spring.aop.services.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AopApp {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AopConfig.class);
        ProductService productService = context.getBean("productService", ProductService.class);
        productService.insertProduct(1);

    }
}
