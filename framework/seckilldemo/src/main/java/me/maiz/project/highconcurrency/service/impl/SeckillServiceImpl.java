package me.maiz.project.highconcurrency.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.maiz.project.highconcurrency.config.RabbitMqConfig;
import me.maiz.project.highconcurrency.dao.OrderMapper;
import me.maiz.project.highconcurrency.dao.ProductMapper;
import me.maiz.project.highconcurrency.model.Order;
import me.maiz.project.highconcurrency.model.Product;
import me.maiz.project.highconcurrency.model.SecKillOrder;
import me.maiz.project.highconcurrency.model.Seckill;
import me.maiz.project.highconcurrency.dao.SeckillMapper;
import me.maiz.project.highconcurrency.service.IOrderService;
import me.maiz.project.highconcurrency.service.ISeckillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lucas
 * @since 2020-03-03
 */
@Service
@Slf4j
public class SeckillServiceImpl extends ServiceImpl<SeckillMapper, Seckill> implements ISeckillService {

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Cacheable(value = "seckill_info", key = "#id")
    public Seckill getSecKill(int id) {

        return seckillMapper.selectById(id);
    }


    @Transactional
    public void process(SecKillOrder secKillOrder) {
        try {
            //异步创建订单
            Order order = new Order();
            order.setCreateTime(LocalDateTime.now());
            order.setOrderStatus("CREATE");
            order.setProductId(secKillOrder.getProductId());
            order.setSeckillId(secKillOrder.getSecKillId());
            order.setUserId(secKillOrder.getUserId());
            order.setAmount(secKillOrder.getCount());
            orderMapper.insert(order);
            //正式修改库存
            Product product = productMapper.selectById(secKillOrder.getProductId());
            product.setStock(product.getStock() - secKillOrder.getCount());
            productMapper.updateStock(product);
        }catch (DuplicateKeyException e){
            log.warn("唯一索引重复，请检查数据",e);
        }
    }

}
