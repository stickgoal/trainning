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
 * @since 2019-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Pig implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "pig_id", type = IdType.AUTO)
    private Integer pigId;

    private String alias;

    private String pigColor;

    private String pigName;

    private Double price;


}
