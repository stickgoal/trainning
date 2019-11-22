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
public class Perm implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "perm_id", type = IdType.AUTO)
    private Integer permId;

    private String permName;

    private String permValue;

    /**
     * 附加信息
     */
    private String permAttach;


}
