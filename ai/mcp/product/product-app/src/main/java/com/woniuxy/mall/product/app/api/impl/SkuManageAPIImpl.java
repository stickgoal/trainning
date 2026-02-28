package com.woniuxy.mall.product.app.api.impl;

import com.woniuxy.mall.common.result.Result;
import com.woniuxy.mall.product.client.SkuManageClient;
import com.woniuxy.mall.product.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@Slf4j
public class SkuManageAPIImpl implements SkuManageClient {

    @Autowired
    private SkuService skuService;


    public Result deduct(Integer skuId, Integer count) {
        log.info("扣减库存:skuId={},count={}", skuId, count);
//        try {
            skuService.deduct(skuId, count);
//        }catch (Exception e){
//            log.error("扣减库存异常:{}", e.getMessage());
//            return Result.fail(500,e.getMessage());
//        }

        return Result.success();
    }
}
