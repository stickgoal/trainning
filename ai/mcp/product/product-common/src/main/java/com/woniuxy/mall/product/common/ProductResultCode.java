package com.woniuxy.mall.product.common;

import com.woniuxy.mall.common.result.Messagable;

public enum ProductResultCode implements Messagable {
    STOCK_INSUFFICIENT("8002001","库存不足"),
    ;


    private String code;

    private String message;

    ProductResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
