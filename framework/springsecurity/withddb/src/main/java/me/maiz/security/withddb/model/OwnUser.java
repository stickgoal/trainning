package me.maiz.security.withddb.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
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
