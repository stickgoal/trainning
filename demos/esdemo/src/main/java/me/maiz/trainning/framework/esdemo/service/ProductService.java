package me.maiz.trainning.framework.esdemo.service;

import me.maiz.trainning.framework.esdemo.model.ESProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.net.ContentHandler;

public interface ProductService {

    public void add(ESProduct product);

    Page<ESProduct> search(String keyWord, PageRequest of);
}