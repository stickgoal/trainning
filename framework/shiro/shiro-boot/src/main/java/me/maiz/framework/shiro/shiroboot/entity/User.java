package me.maiz.framework.shiro.shiroboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Lucas
 * @since 2019-11-12
 */
@Data
public class User implements Serializable {
    @TableId
    private Integer userId;

    private String username;

    private String password;

    private String status;

}
