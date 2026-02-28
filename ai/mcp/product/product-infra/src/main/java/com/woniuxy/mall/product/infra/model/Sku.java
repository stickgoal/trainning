package com.woniuxy.mall.product.infra.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品SKU表
 * </p>
 *
 * @author Lucas
 * @since 2026-01-23
 */
@Getter
@Setter
@ToString
@TableName("prd_sku")
public class Sku implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sku_id", type = IdType.AUTO)
    private Integer skuId;

    /**
     * 商品SPU ID
     */
    private Integer spuId;

    /**
     * SKU名字
     */
    private String name;

    /**
     * SKU价格
     */
    private BigDecimal price;

    /**
     * SKU图片
     */
    private String img;

    /**
     * SKU介绍
     */
    private String description;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 状态 0-下架 1-上架
     */
    private Byte status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
