package me.maiz.trainning.framework.esdemo.controller;


import me.maiz.trainning.framework.esdemo.model.ESProduct;
import me.maiz.trainning.framework.esdemo.repo.ProductESRepository;
import me.maiz.trainning.framework.esdemo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping("add")
    public String add(String id, String name , String shortDesc, String desc){
       productService.add(new ESProduct(id,name,shortDesc,desc));
       return "success";
    }

    @RequestMapping("search")
    public Object search(String keyWord){
        return productService.search(keyWord,PageRequest.of(0,10)).getContent();
    }


}
