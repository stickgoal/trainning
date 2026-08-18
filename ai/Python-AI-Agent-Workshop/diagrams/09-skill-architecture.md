# 09 - Skill 架构图

```mermaid
graph TB
    subgraph "Agent Skill 架构"
        Agent[Agent Core<br/>Agent 核心] --> SkillRouter[Skill Router<br/>技能路由器]
        
        SkillRouter -->|匹配| Skill1[Coding Skill<br/>编码技能]
        SkillRouter -->|匹配| Skill2[Database Skill<br/>数据库技能]
        SkillRouter -->|匹配| Skill3[Testing Skill<br/>测试技能]
        SkillRouter -->|匹配| Skill4[Deployment Skill<br/>部署技能]
        SkillRouter -->|匹配| SkillN[Custom Skill<br/>自定义技能]

        subgraph "Skill 内部结构"
            Procedure[Procedure<br/>执行流程<br/>步骤定义]
            Prompt[Prompt Template<br/>提示词模板<br/>技能专属提示]
            Tools[Tools<br/>工具集合<br/>技能所需工具]
            Knowledge[Knowledge<br/>领域知识<br/>技能背景知识]
            Validation[Validation<br/>输出校验<br/>结果验证规则]
            
            Procedure --> Prompt
            Procedure --> Tools
            Procedure --> Knowledge
            Procedure --> Validation
        end

        Skill1 -.-> Procedure
        Skill2 -.-> Procedure
        Skill3 -.-> Procedure
        Skill4 -.-> Procedure
    end

    style SkillRouter fill:#e1f5fe
    style Procedure fill:#fff3e0
    style Prompt fill:#e8f5e9
    style Tools fill:#fce4ec
```

## Skill vs Tool vs Prompt

| 维度 | Prompt | Tool | Skill |
|------|--------|------|-------|
| 本质 | 文本指令 | 函数/方法 | 完整能力封装 |
| 粒度 | 单次交互 | 单个操作 | 完整工作流 |
| 组成 | 纯文本 | 函数定义 | Procedure + Prompt + Tool + Knowledge |
| 复杂度 | 低 | 中 | 高 |
| 复用性 | 低 | 中 | 高 |
| 可分享 | 文本复制 | 代码复制 | 包/模块分发 |
| Java 类比 | 配置参数 | Service 方法 | Spring Bean (含依赖和方法) |

## Skill 注册与调用流程

```mermaid
sequenceDiagram
    participant Agent as Agent
    participant Router as Skill Router
    participant Registry as Skill Registry
    participant Skill as Coding Skill
    participant Tool as Code Tool

    Agent->>Router: 用户请求："帮我写一个 Python 函数"
    Router->>Registry: 查找匹配的 Skill
    Registry-->>Router: 匹配到 Coding Skill
    Router->>Skill: 激活 Coding Skill
    
    Skill->>Skill: 1. 加载专属 Prompt 模板
    Skill->>Skill: 2. 注入领域知识
    Skill->>Tool: 3. 注册所需工具
    Skill->>Agent: 4. 返回技能执行计划
    
    Agent->>Skill: 执行技能
    Skill->>Tool: 调用代码生成工具
    Tool-->>Skill: 返回代码
    Skill->>Skill: 5. 校验输出格式
    Skill-->>Agent: 返回最终结果
```

## Skill 设计原则

1. **单一职责**：每个 Skill 聚焦一个领域
2. **自包含**：包含所需 Prompt、Tool、Knowledge
3. **可组合**：Skill 之间可以协作
4. **可复用**：跨 Agent 跨项目复用
5. **可测试**：有明确的输入输出验证规则
