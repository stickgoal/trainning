package me.maiz.shardingjdbcreadwrite.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author Lucas
 * @since 2025-11-18
 */
@Getter
@Setter
@ToString
@TableName("user_balance")
public class Balance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("user_id")
    private Long userId;

    private BigDecimal balance;
}
