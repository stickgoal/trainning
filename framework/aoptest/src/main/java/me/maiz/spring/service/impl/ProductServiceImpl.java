package me.maiz.spring.service.impl;

import me.maiz.spring.service.ProductService;

public class ProductServiceImpl implements ProductService {
    @Override
    public void deleteProduct(int productId) {
        System.out.println("删除商品");
    }
}
