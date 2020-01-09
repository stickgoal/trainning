package com.woniuxy.esquickstart.controller;

import com.woniuxy.esquickstart.dao.ProductESRepo;
import com.woniuxy.esquickstart.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductESRepo productESRepo;

    @GetMapping("add")
    public Product add(int id,String name,String info,String desc){
        //保存数据库
        //保存到elasticsearch
        Product p = new Product(id,name,info,desc);
        return productESRepo.save(p);
    }

    @GetMapping("search")
    public List<Product> search(String kw){
        return productESRepo.search(kw);
    }


}
