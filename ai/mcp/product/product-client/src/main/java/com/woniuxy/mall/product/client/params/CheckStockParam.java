package com.woniuxy.mall.product.client.params;

import lombok.Data;

@Data
public class CheckStockParam {
    private Integer skuId;
    private Integer count;
}
