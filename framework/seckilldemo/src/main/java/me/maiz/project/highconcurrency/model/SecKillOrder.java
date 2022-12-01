package me.maiz.project.highconcurrency.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecKillOrder {

    private int productId;

    private int secKillId;

    private int userId;

    private int count;



}
