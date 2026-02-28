package com.woniuxy.mall.product.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.woniuxy.mall.product.infra.mapper.SkuMapper;
import com.woniuxy.mall.product.infra.model.Sku;
import com.woniuxy.mall.product.service.SkuService;
import com.woniuxy.mall.product.service.serviceparam.CheckStockServiceParam;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 商品SKU表 服务实现类
 * </p>
 *
 * @author Lucas
 * @since 2026-01-23
 */
@Service
@Slf4j
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {

    @Override
    public void checkStockBatch(List<CheckStockServiceParam> checkStockParams) {

        for (CheckStockServiceParam checkStockParam : checkStockParams) {
            Optional<Sku> optionalSku = this.getOptById(checkStockParam.getSkuId());
            if(!optionalSku.isPresent()){
                log.info("商品不存在:{}",checkStockParam.getSkuId());
                throw new RuntimeException("商品不存在");
            }

            Sku sku = optionalSku.get();
            if(sku.getStatus().equals(0)){
                log.info("商品已下架:{}",sku.getSkuId());
                throw new RuntimeException("商品已下架");
            }

            if(sku.getStock()<checkStockParam.getCount()){
                log.info("商品库存不足:{}",sku.getSkuId());
                throw new RuntimeException("商品库存不足");
            }
        }
    }

    @Override
    @GlobalTransactional
    public void deduct(Integer skuId, Integer count) {
        Sku sku = getById(skuId);
        sku.setStock(sku.getStock() - count);
        updateById(sku);

    }
}
