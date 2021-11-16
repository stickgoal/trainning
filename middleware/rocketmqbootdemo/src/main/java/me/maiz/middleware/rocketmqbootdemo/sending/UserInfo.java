package me.maiz.middleware.rocketmqbootdemo.sending;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UserInfo implements Serializable {
    private int userId;
    private String username;
    private String mobile;
    private String avatar;
    private Date regTime;
}
