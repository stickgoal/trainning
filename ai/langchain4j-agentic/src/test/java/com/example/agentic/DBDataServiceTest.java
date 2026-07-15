package com.example.agentic;

import com.example.agentic.common.model.Order;
import com.example.agentic.common.model.Product;
import com.example.agentic.common.model.User;
import com.example.agentic.common.service.DBDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证 DBDataService（原 MyBatis-Plus 版 MockDataService）读取 MySQL 真实数据，
 * 即 AfterSalesTools 在显式注入 DB 实现时底层数据来源为真实数据库。
 */
@SpringBootTest
class DBDataServiceTest {

    @Autowired
    private DBDataService dbDataService;

    @Test
    void orderComesFromDatabase() {
        Order order = dbDataService.getOrder("ORD-001");
        assertNotNull(order, "订单 ORD-001 应能从数据库查到");
        assertEquals("USR-001", order.getUserId());
        assertEquals(299.0, order.getTotalPrice(), 0.001);
        assertFalse(order.getItems().isEmpty(), "订单项应从 order_item 表关联查出");
        assertEquals("蓝牙耳机", order.getItems().get(0).getProductName());
    }

    @Test
    void userComesFromDatabase() {
        User user = dbDataService.getUser("USR-002");
        assertNotNull(user);
        assertEquals("李四", user.getName());
        assertEquals("VIP", user.getVipLevel().name());
    }

    @Test
    void productComesFromDatabase() {
        Product product = dbDataService.getProduct("PRD-003");
        assertNotNull(product);
        assertEquals("运动跑鞋", product.getName());
        assertEquals(459.0, product.getPrice(), 0.001);
    }

    @Test
    void userByOrderResolvesViaDatabase() {
        User user = dbDataService.getUserByOrderId("ORD-003");
        assertNotNull(user);
        assertEquals("王五", user.getName());
    }
}
