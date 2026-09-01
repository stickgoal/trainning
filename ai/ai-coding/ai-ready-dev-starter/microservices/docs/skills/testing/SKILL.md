# 测试技能

## When To Use

为后端代码写测试、补测试、排查测试失败、评估覆盖率时。

## Rules

- **框架**：JUnit 5 + Spring Boot Test。
- **单元测试为必须项**：新增/修改 Service 方法必须配套单元测试，不允许只写实现不写测试。
- **单元测试用 Mockito**：mock Mapper 和 Feign 客户端，隔离 Service 逻辑分支，不连 DB、不连下游服务。
- **集成测试用 `@SpringBootTest`**：单服务走 Controller → Service → Mapper → DB。
- **写数据测试加回滚**：单服务 `@Transactional`，跨服务 `@GlobalTransactional`。
- **跨服务测试隔离**：被依赖服务用 mock 或 Testcontainers（起 Nacos/MySQL），不依赖生产环境。
- **覆盖率门槛**：核心业务（事务、状态机、计算）行覆盖率 ≥ 80%，新增代码不低于既有基线。
- **覆盖优先级**：事务一致性（本地 + 分布式）> 边界与异常 > 正常路径。
- **命名**：`方法名_场景_预期`。
- **断言具体值**：不用 `assertNotNull` 凑数；异常断言到具体类型与错误码。
- **验证 mock 调用**：`verify(feignClient, times(1)).deduct(...)`。
- **测试独立**：测试间不共享状态、不依赖顺序，自带数据构造（given/when/then）。

## Examples

单元测试（mock Feign 与 Mapper）：

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock ProductClient productClient;   // mock 跨服务依赖
    @Mock UserClient userClient;
    @Mock OrderMapper orderMapper;
    @InjectMocks OrderServiceImpl orderService;

    @Test
    void createOrder_库存不足_回滚() {
        when(productClient.deductStock(any())).thenReturn(Result.fail("库存不足"));
        assertThrows(BusinessException.class, () -> orderService.createOrder(req));
        verify(productClient, times(1)).deductStock(any());
    }

    @Test
    void createOrder_商品服务不可用_走降级() {
        when(productClient.deductStock(any())).thenThrow(new FeignException.ServiceUnavailable());
        // 断言走 fallback 返回兜底结果，而非抛裸异常
    }
}
```

集成测试验证下单一致性：

```java
@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired OrderService orderService;
    @MockBean ProductClient productClient;

    @Test
    void createOrder_正常下单_扣库存扣余额() {
        // given / when / then 断言库存减少、余额减少、订单写入
    }
}
```

## Common Mistakes

- **缺回滚导致数据污染**：写数据测试没加事务回滚，测试间互相影响。
- **只测正常路径**：边界（空、超量、非法状态）全漏。
- **跨服务依赖真实服务**：测试连真实下游服务，环境一变就挂。用 mock。
- **模糊断言**：`assertNotNull` 凑数。断言到具体值。
- **跳过失败测试**：掩盖问题。先修再说。
- **忽略 Feign 降级测试**：没测下游不可用时的降级行为。
- **不写单元测试直接上集成测试**：集成测试慢、难定位，先用单元测试锁住逻辑分支。
- **覆盖率造假**：删断言、写空测试凑覆盖率。
