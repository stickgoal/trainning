package com.example.agentic.common.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.agentic.common.entity.OrderEntity;
import com.example.agentic.common.entity.OrderItemEntity;
import com.example.agentic.common.entity.ProductEntity;
import com.example.agentic.common.entity.UserEntity;
import com.example.agentic.common.mapper.OrderItemMapper;
import com.example.agentic.common.mapper.OrderMapper;
import com.example.agentic.common.mapper.ProductMapper;
import com.example.agentic.common.mapper.UserMapper;
import com.example.agentic.common.model.Order;
import com.example.agentic.common.model.OrderItem;
import com.example.agentic.common.model.OrderStatus;
import com.example.agentic.common.model.Product;
import com.example.agentic.common.model.User;
import com.example.agentic.common.model.VipLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 售后数据服务：通过 MyBatis-Plus 访问 MySQL 真实数据库，
 * 提供订单 / 商品 / 用户查询能力。对外接口与 Mock 实现保持一致。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DBDataService implements DataService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public Order getOrder(String orderId) {
        log.info("根据ID查询订单：{}",orderId);
        OrderEntity entity = orderMapper.selectById(orderId);
        if (entity == null) {
            return null;
        }
        List<OrderItemEntity> itemEntities = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderId, orderId));
        return toOrder(entity, itemEntities);
    }

    public Product getProduct(String productId) {
        log.info("根据ID查询商品：{}",productId);
        ProductEntity entity = productMapper.selectById(productId);
        return entity == null ? null : toProduct(entity);
    }

    public User getUser(String userId) {
        log.info("根据ID查询用户：{}",userId);
        UserEntity entity = userMapper.selectById(userId);
        return entity == null ? null : toUser(entity);
    }

    /**
     * 根据订单ID反查用户
     */
    public User getUserByOrderId(String orderId) {
        log.info("根据订单ID查询用户：{}",orderId);
        OrderEntity order = orderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        return getUser(order.getUserId());
    }

    // ----------------------------- 实体 -> 领域模型 转换 -----------------------------

    private Order toOrder(OrderEntity e, List<OrderItemEntity> itemEntities) {
        List<OrderItem> items = Optional.ofNullable(itemEntities).orElse(List.of()).stream()
            .map(i -> OrderItem.builder()
                .productId(i.getProductId())
                .productName(i.getProductName())
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .build())
            .collect(Collectors.toList());
        return Order.builder()
            .orderId(e.getOrderId())
            .userId(e.getUserId())
            .items(items)
            .status(OrderStatus.valueOf(e.getStatus()))
            .totalPrice(e.getTotalPrice())
            .build();
    }

    private Product toProduct(ProductEntity e) {
        return Product.builder()
            .productId(e.getProductId())
            .name(e.getName())
            .category(e.getCategory())
            .price(e.getPrice())
            .stock(e.getStock())
            .build();
    }

    private User toUser(UserEntity e) {
        return User.builder()
            .userId(e.getUserId())
            .name(e.getName())
            .vipLevel(VipLevel.valueOf(e.getVipLevel()))
            .orderCount(e.getOrderCount())
            .refundCount(e.getRefundCount())
            .build();
    }
}
