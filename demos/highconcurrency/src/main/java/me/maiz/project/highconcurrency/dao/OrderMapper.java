package me.maiz.project.highconcurrency.dao;

import me.maiz.project.highconcurrency.model.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lucas
 * @since 2020-03-04
 */
public interface OrderMapper extends BaseMapper<Order> {

    @Select("select * from sk_order where product_id=#{productId} and user_id=#{userId}")
    Order findByProductIdAndUserId(@Param("productId") int productId, @Param("userId")int userId);

}
