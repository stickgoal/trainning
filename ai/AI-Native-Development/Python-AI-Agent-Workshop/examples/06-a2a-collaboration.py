# -*- coding: utf-8 -*-
"""
示例 06: A2A 多 Agent 协作
===========================
展示多 Agent 协作模式：Research Agent + Coding Agent + Testing Agent

使用伪代码 + 部分可运行代码演示 A2A 协作概念
实际生产环境可使用 AutoGen / CrewAI 等框架

适合 Java 工程师阅读：Agent 通信和任务委派机制均有注释
"""

import json
from typing import TypedDict, Optional
from dataclasses import dataclass, field
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, SystemMessage


# ============================================
# 1. Agent 定义 — 基础 Agent 数据结构
# ============================================

# @dataclass：Python 数据类装饰器
# 自动生成 __init__, __repr__ 等方法
# Java 类比：类似 Lombok 的 @Data 注解
# field(default_factory=list)：字段默认值为空列表（不能用可变默认值）
@dataclass
class AgentMessage:
    """Agent 间通信的消息结构"""
    from_agent: str           # 发送方 Agent 名称
    to_agent: str             # 接收方 Agent 名称
    message_type: str         # 消息类型：task / result / query / notification
    content: str              # 消息内容
    metadata: dict = field(default_factory=dict)  # 附加元数据


@dataclass
class Task:
    """任务定义"""
    task_id: str
    description: str
    assigned_to: str          # 负责的 Agent
    status: str = "pending"   # pending / running / completed / failed
    result: Optional[str] = None  # Optional 表示可以为 None，类似 Java 的 @Nullable
    dependencies: list[str] = field(default_factory=list)  # 依赖的其他任务 ID


# ============================================
# 2. Base Agent — Agent 基类
# ============================================

class BaseAgent:
    """
    Agent 基类，所有具体 Agent 继承此类。
    
    Java 类比：abstract class BaseAgent
    """
    
    def __init__(self, name: str, role: str, llm: ChatOpenAI):
        """
        Python 构造函数。
        __init__ 类似 Java 的构造器。
        self 类似 Java 的 this。
        """
        self.name = name      # Agent 名称
        self.role = role      # Agent 角色描述
        self.llm = llm        # 使用的 LLM
        self.inbox: list[AgentMessage] = []   # 收件箱
        self.outbox: list[AgentMessage] = []  # 发件箱
    
    def receive(self, message: AgentMessage) -> None:
        """接收来自其他 Agent 的消息"""
        self.inbox.append(message)
        print(f"  [{self.name}] 收到来自 [{message.from_agent}] 的消息: {message.message_type}")
    
    def send(self, to_agent: str, message_type: str, content: str, **metadata) -> AgentMessage:
        """向其他 Agent 发送消息"""
        # **metadata：Python 的关键字参数收集
        # 收集额外的关键字参数为字典
        # Java 没有直接等价物，类似 Map<String, Object> metadata
        msg = AgentMessage(
            from_agent=self.name,
            to_agent=to_agent,
            message_type=message_type,
            content=content,
            metadata=metadata,
        )
        self.outbox.append(msg)
        print(f"  [{self.name}] 发送消息到 [{to_agent}]: {message_type}")
        return msg
    
    def execute(self, task: Task) -> str:
        """执行任务（子类实现）"""
        raise NotImplementedError("子类必须实现 execute 方法")
        # Python 抛出异常：raise 类似 Java 的 throw


# ============================================
# 3. 具体 Agent 实现
# ============================================

class ResearchAgent(BaseAgent):
    """
    研究 Agent：负责需求调研和信息收集。
    类似项目中的需求分析师。
    """
    
    def execute(self, task: Task) -> str:
        print(f"\n🔬 [{self.name}] 开始研究任务: {task.description}")
        
        response = self.llm.invoke([
            SystemMessage(content=f"你是 {self.role}。请分析需求并输出调研报告。"),
            HumanMessage(content=task.description),
        ])
        
        result = response.content
        task.status = "completed"
        task.result = result
        
        print(f"  [{self.name}] 研究完成，报告长度: {len(result)} 字符")
        return result


class CodingAgent(BaseAgent):
    """
    编码 Agent：负责代码实现。
    类似项目中的开发工程师。
    """
    
    def execute(self, task: Task) -> str:
        print(f"\n💻 [{self.name}] 开始编码任务: {task.description}")
        
        # 获取依赖任务的结果（来自 Research Agent）
        context = task.metadata.get("research_result", "无调研报告")
        
        response = self.llm.invoke([
            SystemMessage(content="""你是一个高级开发工程师。
            根据需求调研报告，编写 Python 代码实现。
            只输出代码，不要解释。"""),
            HumanMessage(content=f"调研报告:\n{context}\n\n任务: {task.description}"),
        ])
        
        result = response.content
        task.status = "completed"
        task.result = result
        
        print(f"  [{self.name}] 编码完成，代码长度: {len(result)} 字符")
        return result


class TestingAgent(BaseAgent):
    """
    测试 Agent：负责编写和执行测试。
    类似项目中的测试工程师。
    """
    
    def execute(self, task: Task) -> str:
        print(f"\n🧪 [{self.name}] 开始测试任务: {task.description}")
        
        # 获取依赖任务的结果（来自 Coding Agent）
        code = task.metadata.get("code_result", "无代码")
        
        response = self.llm.invoke([
            SystemMessage(content="""你是一个测试工程师。
            根据代码实现，编写单元测试。
            只输出测试代码，不要解释。"""),
            HumanMessage(content=f"待测试代码:\n{code}\n\n任务: {task.description}"),
        ])
        
        result = response.content
        task.status = "completed"
        task.result = result
        
        print(f"  [{self.name}] 测试编写完成，测试代码长度: {len(result)} 字符")
        return result


# ============================================
# 4. Coordinator — 协调者 Agent
# ============================================

class Coordinator:
    """
    协调者：负责任务拆解、分发和结果汇总。
    类似项目经理 / 技术负责人。
    
    这是 A2A 协作的中心化协调模式。
    Java 类比：类似 Orchestrator 模式（微服务编排）。
    """
    
    def __init__(self):
        self.agents: dict[str, BaseAgent] = {}   # Agent 注册表
        self.tasks: list[Task] = []               # 任务列表
        self.results: dict[str, str] = {}         # 结果存储
    
    def register_agent(self, agent: BaseAgent) -> None:
        """注册 Agent 到协调器"""
        self.agents[agent.name] = agent
        print(f"已注册 Agent: {agent.name} ({agent.role})")
    
    def decompose_task(self, user_request: str) -> list[Task]:
        """
        将用户请求拆解为子任务。
        类似项目经理将大需求拆分为 Story / Task。
        """
        task_id_counter = 0
        
        tasks = [
            Task(
                task_id=f"task_{task_id_counter + 1}",
                description=f"调研需求: {user_request}",
                assigned_to="research_agent",
            ),
            Task(
                task_id=f"task_{task_id_counter + 2}",
                description="根据调研结果实现代码",
                assigned_to="coding_agent",
                dependencies=["task_1"],
            ),
            Task(
                task_id=f"task_{task_id_counter + 3}",
                description="根据代码编写单元测试",
                assigned_to="testing_agent",
                dependencies=["task_2"],
            ),
        ]
        
        self.tasks = tasks
        return tasks
    
    def execute_workflow(self, user_request: str) -> str:
        """执行完整的 A2A 协作工作流"""
        print(f"\n{'='*60}")
        print(f"用户请求: {user_request}")
        print(f"{'='*60}")
        
        # 步骤 1: 任务拆解
        print("\n📋 步骤 1: 任务拆解")
        tasks = self.decompose_task(user_request)
        for t in tasks:
            print(f"  - {t.task_id}: {t.description} → {t.assigned_to}")
        
        # 步骤 2: 按依赖顺序执行
        print("\n📋 步骤 2: 按顺序执行任务（A2A 协作）")
        
        # 任务 1: 研究
        research_task = tasks[0]
        research_agent = self.agents[research_task.assigned_to]
        research_result = research_agent.execute(research_task)
        self.results["research"] = research_result
        
        # A2A 通信：Research Agent → Coordinator → Coding Agent
        print("\n📧 A2A 通信:")
        research_agent.send("coordinator", "result", research_result[:50] + "...")
        
        # 任务 2: 编码（依赖研究结果）
        coding_task = tasks[1]
        coding_task.metadata["research_result"] = research_result  # 传递依赖结果
        coding_agent = self.agents[coding_task.assigned_to]
        
        # Coordinator → Coding Agent 的任务委派
        coding_agent.receive(AgentMessage(
            from_agent="coordinator",
            to_agent="coding_agent",
            message_type="task",
            content=coding_task.description,
            metadata={"research_result": research_result[:100]},
        ))
        
        code_result = coding_agent.execute(coding_task)
        self.results["code"] = code_result
        
        coding_agent.send("coordinator", "result", code_result[:50] + "...")
        
        # 任务 3: 测试（依赖代码结果）
        testing_task = tasks[2]
        testing_task.metadata["code_result"] = code_result  # 传递依赖结果
        testing_agent = self.agents[testing_task.assigned_to]
        
        testing_agent.receive(AgentMessage(
            from_agent="coordinator",
            to_agent="testing_agent",
            message_type="task",
            content=testing_task.description,
            metadata={"code_result": code_result[:100]},
        ))
        
        test_result = testing_agent.execute(testing_task)
        self.results["test"] = test_result
        
        # 步骤 3: 结果汇总
        print(f"\n{'='*60}")
        print("📋 步骤 3: 结果汇总")
        print(f"{'='*60}")
        
        final_report = f"""
=== A2A 协作最终报告 ===

1. 调研报告 (Research Agent):
   {research_result[:200]}...

2. 代码实现 (Coding Agent):
   {code_result[:200]}...

3. 测试代码 (Testing Agent):
   {test_result[:200]}...

协作流程:
  Research Agent →(结果)→ Coordinator →(任务+上下文)→ Coding Agent
  Coding Agent   →(结果)→ Coordinator →(任务+代码)→   Testing Agent
  Testing Agent  →(结果)→ Coordinator →(汇总)→      最终交付
"""
        return final_report


# ============================================
# 5. 运行 A2A 协作示例
# ============================================

def main():
    print("=" * 60)
    print("A2A 多 Agent 协作示例")
    print("=" * 60)
    
    # 初始化 LLM
    llm = ChatOpenAI(model="gpt-4o", temperature=0)
    
    # 创建并注册 Agent
    coordinator = Coordinator()
    
    coordinator.register_agent(ResearchAgent(
        name="research_agent",
        role="需求调研专家，擅长分析技术需求",
        llm=llm,
    ))
    
    coordinator.register_agent(CodingAgent(
        name="coding_agent",
        role="高级开发工程师，擅长 Python 编程",
        llm=llm,
    ))
    
    coordinator.register_agent(TestingAgent(
        name="testing_agent",
        role="测试工程师，擅长编写单元测试",
        llm=llm,
    ))
    
    # 执行协作工作流
    user_request = "实现一个用户注册 API，包含参数校验和密码加密"
    final_report = coordinator.execute_workflow(user_request)
    print(final_report)
    
    # 展示 A2A 架构
    print("\n=== A2A 协作架构 ===")
    print("""
    用户请求
        │
        ▼
    ┌──────────────┐
    │ Coordinator  │  ← 协调者：任务拆解、分发、汇总
    └──────┬───────┘
           │ 委派任务
    ┌──────┼──────────────┐
    │      │              │
    ▼      ▼              ▼
    ┌─────────┐  ┌─────────┐  ┌─────────┐
    │ Research│  │ Coding  │  │ Testing │
    │  Agent  │  │  Agent  │  │  Agent  │
    └─────────┘  └─────────┘  └─────────┘
         │            │            │
         └────────────┴────────────┘
                      │
                      ▼ 返回结果
                ┌──────────┐
                │Coordinator│  ← 结果汇总
                └──────────┘
                      │
                      ▼
                  最终交付物
    """)


if __name__ == "__main__":
    main()
