package me.maiz.framework.shiro.shiroboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Role {
    @TableId
    private int roleId;

    private String roleName;

    private String roleValue;

}
