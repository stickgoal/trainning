package me.maiz.trainning.project.flashsale.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "emall_flashsale_product")
@Data
public class FlashSaleProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int flashSaleProductId;

    /**
     * 关联到秒杀ID
     */
    private int flashSaleId;

    private int productId;

    private String productName;

    private int stock;

    private double price;

    private String mainImage;

}
