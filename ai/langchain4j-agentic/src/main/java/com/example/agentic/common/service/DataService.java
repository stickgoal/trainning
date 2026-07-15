package com.example.agentic.common.service;

import com.example.agentic.common.model.Order;
import com.example.agentic.common.model.Product;
import com.example.agentic.common.model.User;

/**
 * 售后数据服务抽象接口。
 * 定义订单 / 商品 / 用户的查询能力，解耦上层工具与具体数据来源，
 * 目前有内存 Mock 与 MySQL（DBDataService）两种实现。
 */
public interface DataService {

    /**
     * 根据订单ID查询订单（含订单项）
     */
    Order getOrder(String orderId);

    /**
     * 根据商品ID查询商品
     */
    Product getProduct(String productId);

    /**
     * 根据用户ID查询用户
     */
    User getUser(String userId);

    /**
     * 根据订单ID反查下单用户
     */
    User getUserByOrderId(String orderId);
}
