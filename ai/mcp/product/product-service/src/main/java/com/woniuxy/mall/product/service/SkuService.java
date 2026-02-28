package com.woniuxy.mall.product.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.woniuxy.mall.product.infra.model.Sku;
import com.woniuxy.mall.product.service.serviceparam.CheckStockServiceParam;

import java.util.List;

/**
 * <p>
 * 商品SKU表 服务类
 * </p>
 *
 * @author Lucas
 * @since 2026-01-23
 */
public interface SkuService extends IService<Sku> {

    void checkStockBatch(List<CheckStockServiceParam> checkStockParams);

    void deduct(Integer skuId, Integer count);
}
