package com.woniuxy.boot.ssmboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Lucas
 * @since 2019-11-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RolePerm implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "role_perm_id", type = IdType.AUTO)
    private Integer rolePermId;

    private Integer roleId;

    private Integer permId;


}
