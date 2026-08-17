"""
============================================================
 07_a2a_demo.py — A2A 多 Agent 协作（伪代码 + 最小可运行）
 对应章节：第8章 A2A（Agent To Agent）
============================================================

演示 3 个 Agent 协作完成 "研发一个功能"：
  Research Agent → Coding Agent → Testing Agent
 + Orchestrator 负责任务分发

【运行流程】
 pip install langchain-deepseek langchain-core   # 可选，用真实 LLM 时需要
 python examples/07_a2a_demo.py

【应用场景】
 复杂软件研发、多角色业务流。
 多 Agent 是 "微服务化" 在 Agent 层的重演。

【Python 特殊语法提示】
 - 抽象基类 ABC + @abstractmethod : Java 的 abstract class + abstract method
 - dataclass            : 自动生成构造器（Java Lombok @Data）
 - list[str]            : 泛型容器（Java List<String>）
 - f-string             : 字符串插值
 - dict 字面量           : 等价 Java Map.of(...)
"""

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any


# =================================================================
# Agent 间消息：Agent 通信的载体
# =================================================================
@dataclass
class AgentMessage:
    """
    Agent 之间传递的消息。
    Java 类比：record AgentMessage(String from, String to, String content)。
    """
    sender: str
    receiver: str
    content: str
    context: dict = field(default_factory=dict)  # 携带的上下文


# =================================================================
# Agent 抽象基类：所有 Agent 都实现 run()
# =================================================================
class BaseAgent(ABC):
    """
    抽象基类：Java 的 abstract class。
    @abstractmethod 强制子类实现 run()。
    """
    def __init__(self, name: str):
        self.name = name
        self.inbox: list[AgentMessage] = []   # 收件箱

    def receive(self, msg: AgentMessage) -> None:
        """收消息，塞进收件箱。"""
        self.inbox.append(msg)

    @abstractmethod
    def run(self) -> AgentMessage:
        """处理收到的消息，返回给下一个 Agent 的消息。"""
        ...


# =================================================================
# 三个具体 Agent
# =================================================================
class ResearchAgent(BaseAgent):
    """调研 Agent：把用户需求拆成技术方案。"""

    def run(self) -> AgentMessage:
        task = self.inbox[-1].content  # 取最新一条
        # 真实场景这里调 LLM，此处用模拟逻辑
        plan = f"""
【技术方案】基于需求 '{task}'：
  1. 技术栈：Python + FastAPI
  2. 接口：POST /api/feature
  3. 数据模型：FeatureRequest(name, params)
  4. 依赖：pydantic, fastapi
"""
        return AgentMessage(
            sender=self.name,
            receiver="CodingAgent",
            content=plan,
        )


class CodingAgent(BaseAgent):
    """编码 Agent：根据方案写代码。"""

    def run(self) -> AgentMessage:
        plan = self.inbox[-1].content
        code = f"""
【代码产出】依据方案：
{plan}
```python
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class FeatureRequest(BaseModel):
    name: str
    params: dict

@app.post('/api/feature')
def handle(req: FeatureRequest):
    return {{'ok': True, 'name': req.name}}
```"""
        return AgentMessage(
            sender=self.name,
            receiver="TestingAgent",
            content=code,
        )


class TestingAgent(BaseAgent):
    """测试 Agent：验证代码并出报告。"""

    def run(self) -> AgentMessage:
        code = self.inbox[-1].content
        report = f"""
【测试报告】对代码片段做静态检查：
  - ✅ FastAPI 路由定义规范
  - ✅ Pydantic 模型校验
  - ⚠️  缺少异常处理，建议补 try/except
  - ⚠️  建议加 /health 健康检查
源码：
{code[:100]}..."""
        return AgentMessage(
            sender=self.name,
            receiver="Orchestrator",
            content=report,
        )


# =================================================================
# Orchestrator：任务分发与结果汇总
# =================================================================
class Orchestrator:
    """
    监督者：负责任务拆分、Agent 发现、任务委派。
    解决 A2A 三大问题中的 Task Delegation 与 Discovery。
    """

    def __init__(self):
        # Agent Discovery：注册中心
        self.registry: dict[str, BaseAgent] = {}

    def register(self, agent: BaseAgent) -> None:
        """注册 Agent，类比微服务注册到 Eureka。"""
        self.registry[agent.name] = agent

    def execute(self, user_task: str) -> str:
        """把用户任务派给各 Agent，串成流水线。"""
        # Task Delegation：拆分 + 委派
        research = self.registry["ResearchAgent"]
        coding = self.registry["CodingAgent"]
        testing = self.registry["TestingAgent"]

        # 流水线：Research → Coding → Testing
        research.receive(AgentMessage("Orchestrator", "ResearchAgent", user_task))
        msg1 = research.run()

        coding.receive(msg1)
        msg2 = coding.run()

        testing.receive(msg2)
        msg3 = testing.run()

        return msg3.content


# =================================================================
# 主流程：拼装 + 跑一遍
# =================================================================
def main() -> None:
    print("=" * 55)
    print("A2A 多 Agent 协作演示（伪代码级，无需 API Key）")
    print("=" * 55)

    orch = Orchestrator()
    orch.register(ResearchAgent("ResearchAgent"))
    orch.register(CodingAgent("CodingAgent"))
    orch.register(TestingAgent("TestingAgent"))

    final_report = orch.execute("实现一个接收 JSON 请求的 HTTP 接口")

    print("\n" + "=" * 55)
    print("最终交付：")
    print("=" * 55)
    print(final_report)


if __name__ == "__main__":
    main()
