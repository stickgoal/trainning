package com.wn.demomcpserver.tools.product;

import com.woniuxy.mall.common.result.Result;
import com.woniuxy.mall.product.client.SkuQueryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductTools {
    @Autowired
    private SkuQueryClient skuQueryClient;

    @Tool(description = "根据产品名称关键字，返回产品信息；")
    public Result search(String keyword) {
        log.info("search: {}", keyword);
        Result search = skuQueryClient.search(keyword);
        log.info("search result: {}", search);
        return search;
    }

    @Tool(description = "根据产品ID和数量，执行下单操作")
    public Result createOrder(Integer skuId,Integer quantity){
        log.info("createOrder: {} {}", skuId, quantity);
        return Result.success();
    }
}
