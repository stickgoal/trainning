package me.maiz.spring.proxy;

import me.maiz.spring.proxy.cglib.CglibProxyFactory;
import me.maiz.spring.proxy.jdk.ProxyFactory;
import me.maiz.spring.service.ProductService;
import me.maiz.spring.service.User;
import me.maiz.spring.service.UserService;
import me.maiz.spring.service.impl.ProductServiceImpl;
import me.maiz.spring.service.impl.UserServiceImpl;

public class App {
    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        UserService userService = new UserServiceImpl();
        UserService proxy = (UserService) ProxyFactory.createProxy(userService);
        proxy.addUser(new User());

        ProductService ps = new ProductServiceImpl();
        ProductService productService = (ProductService) ProxyFactory.createProxy(ps);
        productService.deleteProduct(12);

        UserService proxy2 = (UserService) CglibProxyFactory.createProxy(userService);
        proxy2.addUser(new User());
        ProductService productService2 =(ProductService) CglibProxyFactory.createProxy(ps);

        productService2.deleteProduct(2);



    }
}
