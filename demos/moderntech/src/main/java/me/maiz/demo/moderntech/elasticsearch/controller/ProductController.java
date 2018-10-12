package me.maiz.demo.moderntech.elasticsearch.controller;

import me.maiz.demo.moderntech.elasticsearch.model.ESProduct;
import me.maiz.demo.moderntech.elasticsearch.repo.ProductESRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductESRepository productESRepository;

    @RequestMapping("add")
    public ESProduct add(String id,String name ,String shortDesc,String desc){
       return productESRepository.save(new ESProduct());
    }

    @RequestMapping("search")
    public Object search(String keyWord){
        return productESRepository.findByNameContainingOrDescContainingOrShortDescContaining(keyWord,keyWord,keyWord,PageRequest.of(0,10)).getContent();
    }


}
