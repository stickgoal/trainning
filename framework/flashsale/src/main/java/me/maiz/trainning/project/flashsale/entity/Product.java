package me.maiz.trainning.project.flashsale.entity;

import javax.persistence.*;

@Entity
@Table(name = "emall_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    private String productName;

    private int stock;

    private double price;

}
