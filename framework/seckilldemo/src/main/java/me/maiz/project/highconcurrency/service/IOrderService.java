package me.maiz.project.highconcurrency.service;

import me.maiz.project.highconcurrency.model.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lucas
 * @since 2020-03-04
 */
public interface IOrderService extends IService<Order> {

    public Order findByProductIdAndUserId(int productId,int userId);

}
