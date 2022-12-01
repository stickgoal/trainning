package me.maiz.demo.websocketdemo.websocket.model;

import lombok.Data;

@Data
public class OfferPriceMessage {
    /**
     * jwt
     */
    private String jwt;

    /**
     * 拍品ID
     */
    private int auctionId;

    /**
     * 出价
     */
    private double price;

}
