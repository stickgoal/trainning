package com.example.agentic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单项表实体，对应数据库 {@code order_item} 表。
 * 通过 {@code order_id} 关联到 {@code `order`} 表。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("order_item")
public class OrderItemEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderId;

    private String productId;

    private String productName;

    private Integer quantity;

    private Double unitPrice;
}
