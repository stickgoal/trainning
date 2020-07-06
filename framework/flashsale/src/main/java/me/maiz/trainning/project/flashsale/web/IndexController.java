package me.maiz.trainning.project.flashsale.web;

import me.maiz.trainning.project.flashsale.entity.FlashSaleProduct;
import me.maiz.trainning.project.flashsale.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/index")
    public String index(ModelMap modelMap){
        List<FlashSaleProduct> productOfFlashSale = productService.findProductOfFlashSale(1);
        modelMap.put("products",productOfFlashSale);

        return "index";
    }
}
