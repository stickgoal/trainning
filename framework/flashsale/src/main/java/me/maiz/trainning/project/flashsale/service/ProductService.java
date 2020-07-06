package me.maiz.trainning.project.flashsale.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.maiz.trainning.project.flashsale.entity.FlashSaleProduct;

import java.util.List;

public interface ProductService {

    List<FlashSaleProduct> findProductOfFlashSale(int flashSaleId) ;

    FlashSaleProduct findProductById(int productId);

    /**
     * 减少库存
     * @param fsPid  商品ID
     * @param decrNum 减少的量
     */
    FlashSaleProduct decrFlashSaleProductStock(int fsPid,int decrNum);

}
