# 04 - LangGraph Agent 工作流图

```mermaid
graph TB
    subgraph "LangGraph Agent 工作流"
        START((START)) --> Understand

        Understand[Understand Node<br/>理解用户意图<br/>LLM 分析输入]
        Understand --> Plan

        Plan[Plan Node<br/>制定执行计划<br/>拆解子任务]
        Plan --> Decide

        Decide{Decide Edge<br/>条件判断<br/>是否需要工具?}
        Decide -->|需要工具| Execute
        Decide -->|不需要| Synthesize
        Decide -->|需要人工确认| HumanCheck

        HumanCheck[Human-in-the-loop<br/>人工介入节点<br/>等待用户确认]
        HumanCheck -->|确认| Execute
        HumanCheck -->|取消| END_CANCEL((CANCEL))

        Execute[Execute Node<br/>执行工具调用<br/>Search / Code / DB]
        Execute --> Observe

        Observe[Observe Node<br/>观察执行结果<br/>更新 State]
        Observe --> Reflect

        Reflect{Reflect Edge<br/>反思判断<br/>任务是否完成?}
        Reflect -->|未完成| Plan
        Reflect -->|已完成| Synthesize

        Synthesize[Synthesize Node<br/>综合所有结果<br/>生成最终回答]
        Synthesize --> END_OK((END))
    end

    subgraph "State 状态对象"
        State["State<br/>{<br/>  messages: [],<br/>  plan: [],<br/>  results: [],<br/>  status: str,<br/>  iterations: int<br/>}"]
    end

    Understand -.->|读写| State
    Plan -.->|读写| State
    Execute -.->|读写| State
    Observe -.->|读写| State
    Synthesize -.->|读写| State
```

## LangGraph vs LangChain Chain

| 特性 | LangChain Chain | LangGraph |
|------|----------------|-----------|
| 控制流 | 线性管道 | 图（有环 / 条件 / 并行） |
| 状态管理 | 无状态 / 外部注入 | 内置 State 管理 |
| 循环 | 不支持 | 支持（ReAct 循环） |
| 条件分支 | 不支持 | 条件 Edge |
| 人工介入 | 不支持 | 支持（interrupt） |
| 检查点 | 不支持 | 支持（Checkpointing） |
| Java 类比 | Stream Pipeline | Spring StateMachine |

## 核心概念

| 概念 | 说明 | Java 类比 |
|------|------|-----------|
| Graph | 工作流图，包含所有节点和边 | 状态机定义 |
| Node | 执行单元，接收 State 返回更新 | State Action |
| Edge | 控制流，固定或条件 | Transition |
| State | 在节点间共享的数据容器 | State Object |
| Checkpoint | 状态快照，支持恢复 | 持久化快照 |
| Interrupt | 中断执行等待外部输入 | 等待用户输入 |
