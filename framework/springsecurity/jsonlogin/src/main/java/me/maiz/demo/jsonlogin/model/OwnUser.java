package me.maiz.demo.jsonlogin.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("dtx_user")
@Data
public class OwnUser {

    @TableId(value = "user_id",type = IdType.AUTO)
    private Integer userId;

    private String username;

    private String password;

    private double amount;

    private Date rawAddTime;

    private Date rawUpdateTime;

}
