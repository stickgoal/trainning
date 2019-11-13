package me.maiz.framework.shiro.shiroboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserRole {

    @TableId
    private int userRoleId;

    private int userId;

    private int roleId;

}
