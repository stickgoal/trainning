package com.woniuxy.mall.product.app.api.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.woniuxy.mall.common.result.Result;
import com.woniuxy.mall.product.client.SkuQueryClient;
import com.woniuxy.mall.product.client.params.CheckStockParam;
import com.woniuxy.mall.product.infra.model.Sku;
import com.woniuxy.mall.product.service.SkuService;
import com.woniuxy.mall.product.service.serviceparam.CheckStockServiceParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class SkuQueryAPIImpl implements SkuQueryClient {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SkuService skuService;

    @Override
    public Result checkStockBatch(String checkStockBatchParams) {
        log.info("批量检查库存:{}", checkStockBatchParams);
        try {
            List<CheckStockParam> checkStockParams = objectMapper.readValue(checkStockBatchParams, objectMapper.getTypeFactory().constructCollectionType(List.class, CheckStockParam.class));
            skuService.checkStockBatch(BeanUtil.copyToList(checkStockParams, CheckStockServiceParam.class));
        } catch (JsonProcessingException e) {
            log.error("批量检查库存失败:{}", e.getMessage());
            throw new RuntimeException(e);
        }catch (RuntimeException e){
            log.error("批量检查库存失败:{}", e.getMessage());
            return Result.fail();
        }
        return Result.success();
    }

    @Override
    public Result search(String keyword) {
        log.info("搜索:{}", keyword);
        //不考虑安全问题
        List<Sku> list = skuService.lambdaQuery().like(Sku::getName, "%" + keyword + "%").list();
        return Result.success(list);
    }
}
