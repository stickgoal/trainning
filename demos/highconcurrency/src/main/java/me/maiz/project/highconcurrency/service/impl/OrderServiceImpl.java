package me.maiz.project.highconcurrency.service.impl;

import me.maiz.project.highconcurrency.model.Order;
import me.maiz.project.highconcurrency.dao.OrderMapper;
import me.maiz.project.highconcurrency.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lucas
 * @since 2020-03-04
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order findByProductIdAndUserId(int productId, int userId) {
        return orderMapper.findByProductIdAndUserId(productId,userId);
    }
}
