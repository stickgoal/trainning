package me.maiz.shardingjdbcdemo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户登录表
 * </p>
 *
 * @author Lucas
 * @since 2025-11-17
 */
@Getter
@Setter
@ToString
@TableName("user_user_login")
public class UserLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 登录密码
     */
    private String loginPassword;

    /**
     * 登录状态
     */
    private Byte userStatus;

    /**
     * 手机号
     */
    private String loginMobile;

    /**
     * 默认登录方式,0：密码
     */
    private Byte defaultLoginType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
