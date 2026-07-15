package com.example.agentic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单表实体，对应数据库 {@code `order`} 表。
 * 订单项通过 {@code order_item} 表以一对多方式关联。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("`order`")
public class OrderEntity {

    @TableId(type = IdType.INPUT)
    private String orderId;

    private String userId;

    /** 订单状态：PAID / SHIPPED / DELIVERED / CANCELLED */
    private String status;

    private Double totalPrice;
}
