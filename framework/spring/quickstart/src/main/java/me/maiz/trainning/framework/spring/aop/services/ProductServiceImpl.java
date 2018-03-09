package me.maiz.trainning.framework.spring.aop.services;

import org.springframework.stereotype.Service;

/**
 * Created by Lucas on 2017-01-12.
 */
@Service("productService")
public class ProductServiceImpl implements ProductService {
    public void insertProduct(int productId) {
        System.out.println(productId);
    }
}
