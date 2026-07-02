# LangChain4j Agentic Workflow Demo 规划文档

## 项目概述

在统一电商售后场景下，用 LangChain4j 1.16.0-beta26 的 Agentic API 实现 6 种工作流模式 Demo。

**技术栈：** Spring Boot 3.5.3 + LangChain4j 1.16.0-beta26 + JDK 17  
**LLM：** Agnes AI (OpenAI 兼容格式, base-url: https://apihub.agnes-ai.com/v1)  
**业务场景：** 电商智能售后处理平台

---

## 统一领域模型

所有 Demo 共享以下模型和工具：

### 实体类 (`common/model`)
- **Order**: orderId, userId, items, status(PAID/SHIPPED/DELIVERED), totalPrice
- **Product**: productId, name, category, price, stock
- **User**: userId, name, vipLevel(NORMAL/VIP/SVIP), orderCount, refundHistory
- **RefundRequest**: requestId, orderId, reason, amount, status(PENDING/APPROVED/REJECTED), category

### 模拟数据服务 (`common/service/MockDataService`)
提供订单查询、商品查询、用户查询、退款历史查询等模拟数据，不依赖数据库。

### 工具类 (`common/tool/AfterSalesTools`)
用 @Tool 注解暴露给 LLM：
- queryOrder(orderId) → 订单信息
- queryProduct(productId) → 商品信息
- queryUser(userId) → 用户信息
- calculateRefund(orderId, reason) → 退款金额计算
- checkStock(productId) → 库存检查

---

## 六种模式详细规划

### 1. Sequential — 顺序审批链 ✅ 一期先实现

**业务需求：** 用户申请退款，依次经过三个 Agent 审批，每步依赖前一步输出。

**流程：**
```
用户退款申请 → RiskAgent → FinanceAgent → ServiceAgent → 最终结果
```

**Agent 定义：**

| Agent | 描述 | 输入 | 输出 |
|-------|------|------|------|
| RiskAgent | 风控检查：识别欺诈/高频退款 | orderId, reason | RiskResult(riskScore, riskTag, riskComment) |
| FinanceAgent | 财务核算：根据风险等级计算退款金额 | orderId, RiskResult | FinanceResult(refundAmount, deductionReason) |
| ServiceAgent | 客服确认：综合评估生成处理结果 | orderId, RiskResult, FinanceResult | FinalResult(approved, resultComment, notifyText) |

**LangChain4j 实现：**
```java
UntypedAgent workflow = AgenticServices.sequenceBuilder()
    .subAgents(riskAgent, financeAgent, serviceAgent)
    .outputName("finalResult")
    .build();
```

**测试场景：**
- 低风险订单 → 三步顺利通过，全额退款
- 高风险订单 → 风控标记，财务扣手续费，客服生成拒绝通知

---

### 2. Parallel — 并行核验 ✅ 二期已实现

**业务需求：** 退款申请同时核验多个维度，互不依赖，并行后汇总。

**流程：**
```
用户退款申请
  → OrderCheckAgent   ─┐
  → CreditCheckAgent  ─┤ parallelBuilder
  → StockCheckAgent   ─┘
  → SummaryAgent (sequenceBuilder 串联汇总)
```

**LangChain4j 实现：**
```java
// 并行核验块
UntypedAgent parallelBlock = AgenticServices.parallelBuilder()
    .subAgents(orderCheckAgent, creditCheckAgent, stockCheckAgent)
    .outputKey("parallelCheckResult")
    .build();

// 串联：并行核验 → 汇总
ParallelCheckWorkflow workflow = AgenticServices.sequenceBuilder(ParallelCheckWorkflow.class)
    .subAgents(parallelBlock, summaryAgent)
    .outputKey("summaryResult")
    .build();
```

**实测结果（ORD-001）：**
- 订单状态: WARNING（已签收，需核实质量问题）
- 用户信用: PASS（信用良好，退款风险低）
- 库存状况: WARNING（无法确定库存）
- 最终决策: NEEDS_MANUAL_REVIEW

---

### 3. Loop — 迭代优化 ✅ 二期已实现

**业务需求：** 自动生成售后回复文案，通过"起草→评审"循环迭代，直到评分达标。

**流程：**
```
用户投诉 → DraftAgent(起草) → ReviewAgent(评审) → 评分≥8(APPROVED)则退出, 否则继续迭代 → 最终文案
```

**关键设计：**
- DraftAgent 使用 `@Agent(summarizedContext = {"reviewResult"})` 自动注入上一轮评审结果
- 首次迭代时 AgenticScope 中没有 reviewResult，不会抛 MissingArgumentException
- ReviewAgent 输出 "APPROVED" 或 "NEEDS_IMPROVEMENT + 改进建议"

**LangChain4j 实现：**
```java
LoopReplyWorkflow workflow = AgenticServices.loopBuilder(LoopReplyWorkflow.class)
    .subAgents(draftAgent, reviewAgent)
    .exitCondition(scope -> scope.readState("reviewResult", "").contains("APPROVED"))
    .maxIterations(3)
    .outputKey("reviewResult")
    .build();
```

**实测结果（ORD-001）：**
- 第1轮 DraftAgent 起草文案
- 第1轮 ReviewAgent 评分 9.75，输出 APPROVED
- 1轮即达标，退出循环

---

### 4. Conditional — 条件分流 ✅ 三期已实现

**业务需求：** 根据退款原因分类，走不同处理路径。

**流程：**
```
退款申请 → ClassifyAgent（分类）→ 条件路由:
  refundCategory 含 QUALITY_ISSUE    → AutoRefundAgent（快速退款）
  refundCategory 含 PERSONAL_REASON  → ManualReviewAgent（人工审核）
  refundCategory 含 EXCHANGE_REQUEST → ExchangeAgent（换货处理）
```

**LangChain4j 实现：**
```java
// 条件路由块
UntypedAgent conditionalBlock = AgenticServices.conditionalBuilder()
    .subAgents(isQuality, autoRefundAgent)
    .subAgents(isPersonal, manualReviewAgent)
    .subAgents(isExchange, exchangeAgent)
    .outputKey("processResult")
    .build();

// 串联：分类 → 条件路由
ConditionalRefundWorkflow workflow = AgenticServices
    .sequenceBuilder(ConditionalRefundWorkflow.class)
    .subAgents(classifyAgent, conditionalBlock)
    .outputKey("processResult")
    .build();
```

**实测结果：**
- ORD-001 + "商品质量问题" → 分类 QUALITY_ISSUE → 快速退款通道 ✅
- ORD-002 + "不喜欢这个颜色" → 分类 PERSONAL_REASON → 人工审核通道 ✅

---

### 5. Supervisor / Planner — 主管调度 ✅ 三期已实现

**业务需求：** 复杂售后诉求（投诉+赔偿+情绪激烈），由 Supervisor Agent 动态编排执行计划。

**流程：**
```
复杂诉求 → Supervisor Agent（LLM驱动，自主决策）
  → 动态调度: EmotionAgent / FactAgent / SolutionAgent / NotifyAgent
  → 自主决定调用顺序、是否重复调用、何时完成
  → 最终输出
```

**关键设计：**
- Supervisor 本身是一个 LLM 驱动的 Agent，通过 `supervisorContext` 告知可用子 Agent
- `requestGenerator` 从方法参数生成 Supervisor 的初始输入
- `maxAgentsInvocations(8)` 限制最大调用次数防止无限循环
- `output()` 提取最终结果（优先 notifyResult，其次 solutionResult）
- Supervisor 自主决定调用顺序，不固定流程

**LangChain4j 实现：**
```java
SupervisorWorkflow workflow = AgenticServices
    .supervisorBuilder(SupervisorWorkflow.class)
    .chatModel(chatModel)
    .name("AfterSalesSupervisor")
    .supervisorContext(supervisorContext)
    .requestGenerator(requestGenerator)
    .subAgents(emotionAgent, factAgent, solutionAgent, notifyAgent)
    .maxAgentsInvocations(8)
    .output(outputExtractor)
    .outputKey("finalResult")
    .build();
```

**实测结果：**
- ORD-001 + 激烈投诉（质量问题+要求赔偿+威胁投诉工商局）→ Supervisor 自主编排执行，返回客户通知文案 ✅

---

### 6. HumanInTheLoop — 人工介入 ✅ 四期已实现

**业务需求：** 大额退款需人工审批，Agent 完成前期准备后暂停等待。

**流程：**
```
大额退款 → PreCheckAgent（前置检查）
         → HumanInTheLoop（暂停，写入 PendingResponse("managerApproval")）
         → ExecuteAgent（读取 managerApproval 时阻塞 → 恢复后执行）
```

**关键设计：**
- HumanInTheLoop 是通过 `humanInTheLoopBuilder()` 构建的一个 Agent，
  作为 `sequenceBuilder` 的一个子 Agent 串在 PreCheckAgent 与 ExecuteAgent 之间。
- `responseProvider` 返回 `new PendingResponse<>("managerApproval")`，写入 AgenticScope
  的 `managerApproval`，此时并不阻塞。
- 工作流在**后台线程**运行；当 ExecuteAgent 读取 `managerApproval` 输入时，
  `readStateBlocking` 触发 `PendingResponse.blockingGet()`，工作流「暂停」。
- 工作流方法用 `@MemoryId` 绑定 requestId 到唯一 AgenticScope，
  外部通过 `((AgenticScopeAccess) workflow).getAgenticScope(requestId)` 定位会话，
  调用 `completePendingResponse("managerApproval", ...)` 注入结果，工作流「恢复」。

**LangChain4j 实现：**
```java
HumanInTheLoop humanApprovalAgent = AgenticServices.humanInTheLoopBuilder()
    .outputKey("managerApproval")
    .responseProvider(scope -> new PendingResponse<>("managerApproval"))
    .build();

HumanApprovalWorkflow workflow = AgenticServices.sequenceBuilder(HumanApprovalWorkflow.class)
    .subAgents(preCheckAgent, humanApprovalAgent, executeAgent)
    .outputKey("executionResult")
    .build();

// 后台线程运行（会在 ExecuteAgent 读取 managerApproval 时阻塞）:
executor.submit(() -> workflow.process(requestId, orderId, reason, amount));

// 人工审批，恢复执行:
AgenticScope scope = ((AgenticScopeAccess) workflow).getAgenticScope(requestId);
scope.completePendingResponse("managerApproval", "APPROVED");
```

**REST 交互（两步）：**
- `POST /api/humanintheloop/refund?orderId=ORD-003&reason=不喜欢了&amount=918`
  → 运行至审批环节暂停，返回 requestId + 前置检查材料
- `POST /api/humanintheloop/approve?requestId=REQ-xxxx&decision=APPROVED`
  → 注入审批结论，恢复执行，返回最终结果

---

## 项目目录结构

```
langchain4j-agentic/
├── pom.xml
├── PLAN.md                    ← 本规划文档
├── README.md                  ← 使用说明(后期生成)
├── src/main/java/com/example/agentic/
│   ├── AgenticDemoApplication.java
│   ├── common/
│   │   ├── model/             → Order, Product, User, RefundRequest, 各类Result
│   │   ├── service/           → MockDataService
│   │   └── tool/              → AfterSalesTools (@Tool)
│   ├── sequential/
│   │   ├── agent/             → RiskAgent, FinanceAgent, ServiceAgent (接口定义)
│   │   ├── model/             → RiskResult, FinanceResult, FinalResult
│   │   ├── config/            → SequentialConfig (组装 workflow)
│   │   ├── controller/        → SequentialController (REST 入口)
│   │   └── SequentialDemo.java (独立运行 main)
│   ├── parallel/              → (二期)
│   ├── loop/                  → (二期)
│   ├── conditional/           → (二期)
│   ├── supervisor/            → (三期)
│   └── humanintheloop/        → (四期) PreCheckAgent + HumanInTheLoop + ExecuteAgent
├── src/main/resources/
│   ├── application.yml
│   └── prompts/               → 各 Agent 的 prompt 模板文件
└── src/test/java/com/example/agentic/
    └── sequential/
        └── SequentialWorkflowTest.java
```

---

## 一期交付：Sequential Demo

### 交付物
1. common 层：模型、MockDataService、AfterSalesTools
2. sequential 层：三个 Agent 接口 + workflow 组装 + REST API + 独立运行 main
3. 测试：编译通过 + 启动通过 + REST API 调用返回正确结果

### 测试用例
- **正常退款**：orderId=ORD-001, reason="商品质量问题" → 风控低风险 → 全额退款
- **高风险退款**：orderId=ORD-003, reason="不喜欢了" → 风控标记 → 扣手续费 → 拒绝/部分退款

---

## 交付节奏

| 阶段 | 内容 | 里程碑 |
|------|------|--------|
| 一期 | 项目骨架 + Sequential Demo | ✅ 编译通过 + API 调用成功 |
| 二期 | Parallel + Loop | ✅ 各自独立测试通过 |
| 三期 | Conditional + Supervisor | ✅ 各自独立测试通过 |
| 四期 | HumanInTheLoop | ✅ 编译通过 + 两步 REST 暂停/恢复 |
| 五期 | README + 流程图 + 综合入口 | ✅ 完成 |
