package me.maiz.ittrainning.restdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    private int cardId;

    private String reciever;

    private String sender;

    private Date date;

}
