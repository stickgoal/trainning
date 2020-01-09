package me.maiz.trainning.framework.esdemo.service.impl;

import me.maiz.trainning.framework.esdemo.model.ESProduct;
import me.maiz.trainning.framework.esdemo.repo.ProductDAO;
import me.maiz.trainning.framework.esdemo.repo.ProductESRepository;
import me.maiz.trainning.framework.esdemo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private ProductESRepository productESRepository;

    @Override
    public void add(ESProduct product) {
        productDAO.insert(product);
        productESRepository.save(product);
    }

    @Override
    public Page<ESProduct> search(String keyWord, PageRequest of) {
        return productESRepository.search(keyWord,of);
    }
}
