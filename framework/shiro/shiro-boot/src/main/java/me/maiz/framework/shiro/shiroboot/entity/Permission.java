package me.maiz.framework.shiro.shiroboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Permission {
    @TableId
    private int permId;

    private String permName;

    private String permValue;

}
