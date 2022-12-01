package me.maiz.project.highconcurrency.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lucas
 * @since 2020-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sk_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 抢购到的商品
     */
    private Integer productId;

    /**
     * 关联的秒杀活动
     */
    private Integer seckillId;

    /**
     * 数量
     */
    private Integer amount;

    /**
     * 订单状态  CREATE | PAID | SENT | FINISH
     */
    private String orderStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 发货时间
     */
    private LocalDateTime sentTime;

    /**
     * 结束时间
     */
    private LocalDateTime finishTime;


}
