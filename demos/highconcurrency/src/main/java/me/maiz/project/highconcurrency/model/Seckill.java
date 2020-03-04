package me.maiz.project.highconcurrency.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lucas
 * @since 2020-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sk_seckill")
public class Seckill implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "seckill_id", type = IdType.AUTO)
    private Integer seckillId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 秒杀状态, INIT | PROCESSING | END
     */
    private String seckillStatus;


}
