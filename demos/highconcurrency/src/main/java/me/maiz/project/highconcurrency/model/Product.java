package me.maiz.project.highconcurrency.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品
 * </p>
 *
 * @author lucas
 * @since 2020-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sk_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "product_id", type = IdType.AUTO)
    private Integer productId;

    /**
     * 秒杀ID
     */
    private Integer seckillId;

    /**
     * 主图
     */
    private String mainImg;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 建议价格
     */
    private BigDecimal suggestPrice;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 模型，json格式保存
     */
    private String model;

    /**
     * 存货量
     */
    private Integer stock;

    /**
     * 介绍
     */
    private String description;

    /**
     * 销量
     */
    private Integer salesCount;


}
