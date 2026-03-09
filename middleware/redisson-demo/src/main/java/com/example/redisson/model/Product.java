package com.example.redisson.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private Integer stock;
    private Double price;
    private String category;
    private Integer version; // 用于乐观锁
    
    public Product(Long id, String name, Integer stock, Double price, String category) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.category = category;
        this.version = 0;
    }
}
