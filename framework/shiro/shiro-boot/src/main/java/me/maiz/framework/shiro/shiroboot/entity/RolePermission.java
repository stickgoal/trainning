package me.maiz.framework.shiro.shiroboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class RolePermission {

    @TableId
    private int rolePermId;

    private int permId;

    private int roleId;

}
