package com.woniuxy.mall.product.client;

import com.woniuxy.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product", contextId = "skuManageClient",path = "api")
public interface SkuManageClient {

    @PutMapping("/sku/deduct")
    Result deduct(@RequestParam("skuId") Integer skuId, @RequestParam("count")Integer count);

}
