package me.maiz.trainning.project.flashsale.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "emall_user")
public class User {
    private int userId;

    private String username;

    private String password;


}
