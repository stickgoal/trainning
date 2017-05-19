package com.chinasofti.se.bookstore;

import java.math.BigDecimal;

/**
 * Created by Lucas on 2017-03-30.
 */
public class Book {

    /* 书名 */
    private String name;

    /* 编号*/
    private String ISBN;

    /*价格*/
    private BigDecimal price;

    /*库存*/
    private int stock;

    public Book() {
    }

    public Book(String name, String ISBN, BigDecimal price, int stock) {
        this.name = name;
        this.ISBN = ISBN;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", ISBN='" + ISBN + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
