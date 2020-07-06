package me.maiz.trainning.project.flashsale.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.maiz.trainning.project.flashsale.dao.FlashSaleProductRepo;
import me.maiz.trainning.project.flashsale.entity.FlashSaleProduct;
import me.maiz.trainning.project.flashsale.service.ProductService;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private FlashSaleProductRepo flashSaleProductRepo;

    @Autowired
    private ObjectMapper objectMapper;

    //预热，从数据库中查询出所有商品,放入缓存
    @Cacheable(value = "flashSaleProducts", key = "#flashSaleId", unless = "#result==null")
    @Override
    public List<FlashSaleProduct> findProductOfFlashSale(int flashSaleId) {
        log.info("查询数据库中id为{}的秒杀数据", flashSaleId);
        return flashSaleProductRepo.findAllByFlashSaleId(flashSaleId);
    }

    //从数据库查询出某个商品的详情放入缓存
    @Override
    @Cacheable(value = "flashSaleProduct", key = "#productId", unless = "#result==null")
    public FlashSaleProduct findProductById(int productId) {
        return flashSaleProductRepo.findById(productId).get();
    }

    //减少缓存
    @Override
    @CachePut(value = "flashSaleProduct", key = "#fsPid")
    public FlashSaleProduct decrFlashSaleProductStock(int fsPid, int decrNum) {
        FlashSaleProduct product = findProductById(fsPid);
        if(product.getStock()-decrNum<=0){
            throw new RuntimeException("InsufficientStock");
        }
        product.setStock(product.getStock()-decrNum);

        return product;
    }
}
