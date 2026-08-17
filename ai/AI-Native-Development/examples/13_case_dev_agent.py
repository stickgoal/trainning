"""
============================================================
 12_case_dev_agent.py — 综合案例2：软件研发 Agent
 对应章节：第13章 案例2
============================================================

技术栈：Python + LangGraph + Skill + Tool Calling + Harness

流程：
 1. 用 LangGraph 搭工作流：调研 → 编码 → 测试 → 人工评审 → 部署
 2. 每个节点是一个 Skill（CodingSkill / TestingSkill / DeploymentSkill）
 3. Tool Calling 让 LLM 调具体工具
 4. Harness 管上下文 + 权限 + 日志

【运行流程】
 pip install langgraph langchain-deepseek langchain-core
 set DEEPSEEK_API_KEY=sk-xxx
 python examples/13_case_dev_agent.py

【应用场景】
 软件研发场景：从需求 → 方案 → 代码 → 测试 → 评审 → 部署的端到端 Agent。
 真实形态：Claude Code / Cursor / Devin 的内核。

【Python 特殊语法提示】
 - TypedDict + Annotated   : LangGraph State 定义
 - operator.add            : reducer，列表合并
 - @dataclass              : Skill 数据载体
 - 条件边返回字符串         : 决定下一跳节点
 - yield（无，本文件不用）
"""

import os
import operator
from dataclasses import dataclass, field
from typing import Annotated, TypedDict, Literal

from langgraph.graph import StateGraph, START, END


# =================================================================
# 1. State：研发流程的共享状态
# =================================================================
class DevState(TypedDict):
    """
    研发流程在节点间传递的状态。
    Annotated[list, operator.add]：列表用拼接合并，不是覆盖。
    """
    task: str                              # 原始需求
    messages: Annotated[list, operator.add]  # 消息流（追加）
    artifacts: Annotated[list, operator.add]  # 产出物（方案/代码/报告，追加）
    step: int


# =================================================================
# 2. Harness：上下文 + 权限 + 日志（精简版，复用第9章思路）
# =================================================================
@dataclass
class Harness:
    """研发 Agent 的运行时基础设施。"""
    max_steps: int = 10
    log: list = field(default_factory=list)

    def check_step(self, state: DevState) -> bool:
        """防止 Agent 跑飞。"""
        if state["step"] >= self.max_steps:
            self.log.append("⚠️  超过最大步数，强制停止")
            return False
        return True

    def record(self, node: str, state: DevState) -> None:
        self.log.append(f"[step {state['step']}] 节点 {node} 执行")


# =================================================================
# 3. Skill：能力封装（复用第10章思路）
# =================================================================
class CodingSkill:
    """编码 Skill：调 LLM + Tool 写代码。"""
    name = "CodingSkill"

    @staticmethod
    def run(state: DevState) -> dict:
        task = state["task"]
        # 真实场景：调 LLM + file_write 工具
        code = f"# 实现: {task}\ndef solution():\n    return 'ok'\n"
        return {
            "artifacts": [f"```python\n{code}```"],
            "messages": [f"CodingSkill 产出代码"],
            "step": state["step"] + 1,
        }


class TestingSkill:
    """测试 Skill：生成用例 + 跑测试。"""
    name = "TestingSkill"

    @staticmethod
    def run(state: DevState) -> dict:
        # 取最新代码产物
        latest = state["artifacts"][-1] if state["artifacts"] else ""
        # 真实场景：调 pytest 工具
        report = f"3 用例通过 / 0 失败\n覆盖: {latest[:40]}..."
        return {
            "artifacts": [f"测试报告: {report}"],
            "messages": [f"TestingSkill 测试通过"],
            "step": state["step"] + 1,
        }


class DeploymentSkill:
    """部署 Skill：构建 + 发布。"""
    name = "DeploymentSkill"

    @staticmethod
    def run(state: DevState) -> dict:
        # 真实场景：调 CI/CD 工具，dangerous 级要审批
        return {
            "artifacts": ["部署完成: https://app.example.com/v1.2.3"],
            "messages": ["DeploymentSkill 部署成功"],
            "step": state["step"] + 1,
        }


def human_review(state: DevState) -> dict:
    """人工评审节点：在部署前暂停等人审。"""
    print("\n[人工评审] 当前产出：")
    for a in state["artifacts"]:
        print(f"  - {a[:60]}")
    print("  → 模拟审批通过 ✅")
    return {"step": state["step"] + 1}


# =================================================================
# 4. Node：把 Skill 包成 LangGraph 节点
# =================================================================
harness = Harness()


def node_research(state: DevState) -> dict:
    """调研节点：拆需求。"""
    harness.record("research", state)
    return {
        "artifacts": [f"技术方案: 基于 {state['task']}"],
        "messages": ["调研完成"],
        "step": state["step"] + 1,
    }


def node_coding(state: DevState) -> dict:
    harness.record("coding", state)
    return CodingSkill.run(state)


def node_testing(state: DevState) -> dict:
    harness.record("testing", state)
    return TestingSkill.run(state)


def node_human(state: DevState) -> dict:
    harness.record("human_review", state)
    return human_review(state)


def node_deploy(state: DevState) -> dict:
    harness.record("deploy", state)
    if not harness.check_step(state):
        return {"step": state["step"]}
    return DeploymentSkill.run(state)


# =================================================================
# 5. Edge：条件边决定流向
# =================================================================
def after_testing(state: DevState) -> Literal["human_review", "coding"]:
    """测试通过 → 人工评审；测试失败 → 回到 coding 修。"""
    last = state["artifacts"][-1] if state["artifacts"] else ""
    if "失败" in last:
        return "coding"   # 回环：修代码
    return "human_review"


def after_human(state: DevState) -> Literal["deploy", "__end__"]:
    """审批通过 → 部署；不通过 → 结束。"""
    return "deploy"  # 简化：默认通过


# =================================================================
# 6. 组装 LangGraph 工作流
# =================================================================
def build_dev_agent():
    g = StateGraph(DevState)
    g.add_node("research", node_research)
    g.add_node("coding", node_coding)
    g.add_node("testing", node_testing)
    g.add_node("human_review", node_human)
    g.add_node("deploy", node_deploy)

    g.add_edge(START, "research")
    g.add_edge("research", "coding")
    g.add_edge("coding", "testing")
    g.add_conditional_edges("testing", after_testing, {
        "human_review": "human_review",
        "coding": "coding",
    })
    g.add_conditional_edges("human_review", after_human, {
        "deploy": "deploy",
        "__end__": END,
    })
    g.add_edge("deploy", END)
    return g.compile()


# =================================================================
# 主流程
# =================================================================
def main() -> None:
    print("=" * 55)
    print("综合案例2：软件研发 Agent（无需 API Key，伪逻辑演示流程）")
    print("=" * 55)

    agent = build_dev_agent()

    initial: DevState = {
        "task": "实现用户登录接口",
        "messages": [],
        "artifacts": [],
        "step": 0,
    }

    result = agent.invoke(initial, config={"recursion_limit": 15})

    print("\n" + "=" * 55)
    print("最终产出：")
    for a in result["artifacts"]:
        print(f"  - {a}")
    print(f"\n总步数: {result['step']}")
    print("\nHarness 日志：")
    for line in harness.log:
        print(f"  {line}")


if __name__ == "__main__":
    main()
