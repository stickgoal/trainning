package com.woniuxy.mall.product.client;

import com.woniuxy.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product", contextId = "skuQueryClient")
public interface SkuQueryClient {

    @GetMapping("/sku/stocks")
    Result checkStockBatch(@RequestParam("checkStockBatchParams") String checkStockBatchParams);

    @GetMapping("/sku/search")
    Result search(@RequestParam("keyword") String keyword);
}
