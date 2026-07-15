package com.example.agentic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品表实体，对应数据库 {@code product} 表。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product")
public class ProductEntity {

    @TableId(type = IdType.INPUT)
    private String productId;

    private String name;

    private String category;

    private Double price;

    private Integer stock;
}
