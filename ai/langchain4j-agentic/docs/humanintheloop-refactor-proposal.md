# HumanInTheLoop 生产级改造建议

> 包路径：`com.example.agentic.humanintheloop`
> 目标：把原本「Demo 味」的人工介入流程，改造成可上生产的实现。
> 两个硬性约束（来自改造诉求）：
> 1. **数据存入数据库，不使用 `Map`**；
> 2. **不使用「线程池 + `Future` 阻塞等待」这种低效方式**。

---

## 一、原实现的三个生产级缺陷

原 `HumanInTheLoopService` 的关键代码：

```java
private final Map<String, Future<String>> runningWorkflows = new ConcurrentHashMap<>();
private final ExecutorService executor = Executors.newCachedThreadPool();

// submit 时后台跑整条工作流，然后忙等到达暂停点
Future<String> future = executor.submit(() -> workflow.process(...));
runningWorkflows.put(requestId, future);
AgenticScope scope = awaitPause(requestId, future);   // 内部 sleep(200) 轮询 60s

// approve 时 future.get(120s) 把 HTTP 线程也阻塞住
result = future.get(120, TimeUnit.SECONDS);
```

| # | 缺陷 | 后果 |
|---|------|------|
| 1 | **内存 `Map<String, Future>` 保存运行态** | 进程重启 / 多实例部署时，所有在途审批丢失，无法恢复、无法审计、无法跨节点。 |
| 2 | **`awaitPause()` 用 `sleep(200)` 忙等轮询暂停点** | 一个线程空转最多 60s 啥也不干，只为探测「是否已暂停」，纯属浪费。 |
| 3 | **`future.get(120s)` 阻塞 HTTP 线程** | 人工审批可能几分钟甚至几小时，Web 容器线程被长期占用；缓存线程池 `newCachedThreadPool` 还会在长时间等待里无限膨胀线程。 |

更深层的问题是：**库内 `PendingResponse.blockingGet()` 把一条工作流线程一直挂起，直到人工审批完成**。这是「用线程模拟暂停」，在人工等待这种不确定时长场景下本质不成立。

---

## 二、生产级改造方案（已落地）

核心思想：**把「暂停」从线程里拿掉，交给数据库状态机；把「执行」放在有界异步线程池；把「结果获取」交给轮询。**

### 2.1 状态机持久化（取代 `Map`）

新增 `approval_request` 表（见 `docs/init.sql`），状态字段贯穿全程：

```
submitRefund ──► PENDING_PRECHECK ──► AWAITING_APPROVAL
                                            │ approve(原子翻转)
                                            ▼
                                       EXECUTING ──► EXECUTED   (通过并退款)
                                            │
                                            └────────► REJECTED  (驳回)
  (任意阶段异常) ─────────────────────────────► FAILED
```

- 实体 `ApprovalRequestEntity` + Mapper `ApprovalRequestMapper`（沿用项目既有的 MyBatis-Plus 风格）。
- 状态真相来源是数据库，重启可恢复、多实例共享、可审计。

### 2.2 去除阻塞等待（取代 `Future.get` / 忙等）

- **提交** `submitRefund`：只在请求线程跑「前置检查」（有界 LLM 调用，数秒），落库为 `AWAITING_APPROVAL` 后立即返回。**不后台挂线程、不忙等。**
- **审批** `approve`：用 `UPDATE ... WHERE status='AWAITING_APPROVAL'` 的**条件更新**原子接管控权，把状态翻成 `EXECUTING`，调度异步执行后**立刻返回 202**，HTTP 线程零阻塞。
- **执行** `runExecution`：仅在专用、有界、带背压策略的线程池 `humanApprovalTaskExecutor` 中跑「执行 Agent」（LLM 调用），完成后把结果落库。
- **获取结果** `GET /status`：客户端轮询拿到 `EXECUTED / REJECTED / FAILED`。

> 关键收益：人工等待期间**不占用任何线程**；Web 线程只在有界 LLM 调用上停留，可水平扩展。

### 2.3 并发安全

审批用条件更新天然防并发重复审批；若两人同时审批，只有一人能把状态从 `AWAITING_APPROVAL` 翻成 `EXECUTING`（影响行数 = 1），另一人拿到「当前状态不可审批」提示。

### 2.4 可恢复（可选）

`recoverStuckExecutions()`（`@Scheduled`，默认关闭，由 `humanintheloop.recovery.enabled` 开启）：把卡在 `EXECUTING` 超过 10 分钟的请求重新入队，处理「执行阶段进程崩溃」的极端情况。

### 2.5 对外契约（改为非阻塞三步）

| 方法 | 路径 | 行为 |
|------|------|------|
| POST | `/api/humanintheloop/refund?orderId=&reason=&amount=` | 前置检查后进入等待，返回 200 + `AWAITING_APPROVAL` |
| POST | `/api/humanintheloop/approve?requestId=&decision=&comment=&approver=` | 受理并调度，返回 **202 Accepted** + `EXECUTING` |
| GET  | `/api/humanintheloop/status?requestId=` | 轮询返回当前快照（含 `executionResult`） |

---

## 三、若仍想保留 LangChain4j 的 `HumanInTheLoop` 能力

如果你希望继续用库内 `humanInTheLoopBuilder()`（而不是拆成两段 Agent），可走 **`async(true)` + 完成回调** 路线，同样能避免阻塞 HTTP 线程：

```java
HumanInTheLoop agent = AgenticServices.humanInTheLoopBuilder()
        .outputKey(APPROVAL_KEY)
        .async(true)                       // 不再阻塞调用方线程
        .responseProvider(scope -> new PendingResponse<>(APPROVAL_KEY))
        .build();
```

- `async(true)` 下工作流以 `CompletableFuture` 形式返回，暂停/恢复由回调驱动，`approve` 只需 `scope.completePendingResponse(...)` 并注册 `thenApply` 把结果落库，**无需 `future.get()`**。
- 但注意：库内仍会用一条（调度器）线程在 `readStateBlocking` 处等待人工审批，只是不再占用你的 Web 线程。**若要彻底「零线程等待」，仍应选择二节的「数据库状态机」方案**——这也是本次落地的方案。
- 无论哪种，都建议把 `requestId → 状态` 的映射从内存 `Map` 迁到数据库（本次已做）。

---

## 四、进一步的增强方向（未在本轮实现，按需取用）

1. **审批超时自动驳回**：`@Scheduler` 扫描 `AWAITING_APPROVAL` 且 `created_at` 超 N 分钟的请求，自动置为 `REJECTED`（或升级告警）。
2. **审批人/角色/权限**：`approver` 字段扩展为审批人工号 + 角色校验，结合 Spring Security。
3. **事件驱动替代轮询**：执行完成时发领域事件 / Webhook / 消息队列，前端订阅推送，免去轮询。
4. **执行幂等**：`ExecuteAgent` 真正的退款动作应保证幂等（以 `request_id` 去重），防止恢复调度导致的重复退款。
5. **可观测性**：在状态流转处打 Metrics（进行中数量、平均审批时长、失败率），接 Prometheus。
6. **前置检查异步化**：若前置检查也较慢，可同样异步化，`submitRefund` 直接返回 `PENDING_PRECHECK`，由 `/status` 观察其完成。

---

## 五、改动文件清单

- 新增 `humanintheloop/entity/ApprovalRequestEntity.java`
- 新增 `humanintheloop/mapper/ApprovalRequestMapper.java`
- 新增 `humanintheloop/model/ApprovalStatus.java`（状态枚举）
- 新增 `humanintheloop/model/ApprovalResponse.java`（响应 DTO）
- 新增 `humanintheloop/config/HumanInTheLoopConfig.java`（线程池 + 调度开关）
- 重写 `humanintheloop/service/HumanInTheLoopService.java`
- 重写 `humanintheloop/controller/HumanInTheLoopController.java`（新增 `GET /status`）
- 删除 `humanintheloop/HumanApprovalWorkflow.java`（不再依赖库内阻塞 `PendingResponse`）
- 调整 `AgenticDemoApplication.java`：`@MapperScan` 范围放宽到 `com.example.agentic`
- `docs/init.sql`：追加 `approval_request` 建表语句
