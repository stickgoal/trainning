# 测试技能

## When To Use

为后端代码写测试、补测试、排查测试失败、评估覆盖率时。

## Rules

- **框架**：JUnit 5 + Spring Boot Test。
- **单元测试为必须项**：新增/修改 Service 方法必须配套单元测试，不允许只写实现不写测试。
- **单元测试用 Mockito**：mock Mapper，隔离 Service 逻辑分支，不依赖 DB。
- **集成测试用 `@SpringBootTest`**：真实走 Controller → Service → Mapper → DB，验证链路。
- **写数据测试加 `@Transactional`**：测试结束自动回滚，防跨测试数据污染。
- **覆盖率门槛**：核心业务（事务、状态机、计算）行覆盖率 ≥ 80%，新增代码不低于既有基线。
- **覆盖优先级**：事务一致性 > 边界与异常 > 正常路径。
- **命名**：`方法名_场景_预期`，如 `createOrder_库存不足_抛异常`。
- **断言具体值**：`assertEquals(198, sku.getStock())`，不用 `assertNotNull` 凑数；异常断言到具体类型与错误码。
- **验证 mock 调用**：`verify(mapper, times(1)).deduct(...)`。
- **测试独立**：测试间不共享状态、不依赖顺序，自带数据构造（given/when/then）。
- **不依赖网络**：测试只依赖本地 MySQL，不连外部服务。

## Examples

集成测试验证下单一致性：

```java
@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired OrderService orderService;

    @Test
    void createOrder_正常下单_扣库存扣余额() {
        // given / when / then 断言库存减少、余额减少、订单写入
    }

    @Test
    void createOrder_库存不足_回滚() {
        // 构造超量请求，断言抛 BusinessException 且数据未变
    }
}
```

单元测试（mock Mapper）：

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock SkuMapper skuMapper;
    @InjectMocks OrderServiceImpl orderService;

    @Test
    void createOrder_库存不足_抛业务异常() {
        when(skuMapper.update(any(), any())).thenReturn(0);
        assertThrows(BusinessException.class, () -> orderService.createOrder(req));
        verify(skuMapper, times(1)).update(any(), any());
    }
}
```

## Common Mistakes

- **缺 `@Transactional` 导致数据污染**：测试间互相影响，偶发失败难定位。写数据测试必加。
- **只测正常路径**：覆盖率高但边界（空、超量、非法状态）全漏。先测边界。
- **模糊断言**：`assertNotNull(result)` 凑数。断言到具体值。
- **测试依赖顺序**：A 测试设置的数据 B 测试用，A 改了 B 就挂。测试应独立。
- **跳过失败的测试**：`@Ignore` 掩盖问题。先修再说。
- **依赖外部服务**：测试连第三方 API，环境一变就挂。用本地或 mock。
- **不写单元测试直接上集成测试**：集成测试慢、难定位，先用单元测试锁住逻辑分支。
- **覆盖率造假**：删断言、写空测试凑覆盖率。
