"""
============================================================
 10_enterprise_arch.py — 企业级 Agent 架构（说明性骨架）
 对应章节：第12章 企业级 Agent 架构
============================================================

不是可运行的业务逻辑，而是 "分层架构骨架代码"：
 把每层的职责用类 + 注释表达出来，方便架构评审 / CTO 汇报时讲解。

【运行流程】
 python examples/11_enterprise_arch.py
 → 打印完整分层架构 + 各层职责

【应用场景】
 向架构评审会、CTO 汇报 Agent 系统设计时的 "代码版架构图"。

【Python 特殊语法提示】
 - 抽象基类 ABC        : 定义接口契约
 - @dataclass          : 数据载体
 - Protocol（鸭子类型）: 结构化子类型（Java 的接口，但隐式实现）
"""

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Protocol, Any


# =================================================================
# 第 1 层：用户 / 业务前端接口
# =================================================================
@dataclass
class UserRequest:
    """用户请求载体。"""
    user_id: str
    session_id: str
    content: str
    metadata: dict = field(default_factory=dict)


# =================================================================
# 第 2 层：Agent Application（编排入口）
# =================================================================
class AgentApplication:
    """
    对外暴露的 Agent 编排入口。
    职责：接收请求 → 调编排 → 调能力层 → 调工程治理层 → 返回。
    Java 类比：Spring Boot 的 Controller + Service。
    """
    def __init__(self, orchestrator, capability_layer, governance_layer):
        self.orchestrator = orchestrator
        self.capabilities = capability_layer
        self.governance = governance_layer

    def handle(self, req: UserRequest) -> str:
        # 工程治理层先做权限 / 限流 / 评估准备
        self.governance.pre_check(req)

        # 编排层（LangGraph / 工作流）跑 Agent
        result = self.orchestrator.run(req, self.capabilities)

        # 工程治理层做事后监控 / 日志 / 反馈
        self.governance.post_record(req, result)
        return result


# =================================================================
# 第 3 层：编排层（Workflow）
# =================================================================
class Orchestrator(ABC):
    """编排器抽象：决定 Agent 怎么跑。LangGraph 是一种实现。"""
    @abstractmethod
    def run(self, req: UserRequest, capabilities) -> str: ...


class LangGraphOrchestrator(Orchestrator):
    """LangGraph 实现：状态机 + 节点 + 条件边。"""
    def run(self, req: UserRequest, capabilities) -> str:
        # 伪流程：调 LLM → 选 Skill/Tool → 可能 A2A → 返回
        llm_resp = capabilities.llm.chat(req.content)
        if "需要工具" in llm_resp:
            llm_resp = capabilities.tools.invoke(llm_resp)
        return llm_resp


# =================================================================
# 第 4 层：能力层（LLM / Memory / Knowledge / Tools / MCP / A2A）
# =================================================================
class CapabilityLayer:
    """把 6 类能力打包：LLM / Memory / Knowledge / Tools / MCP / A2A。"""
    def __init__(self, llm, memory, knowledge, tools, mcp, a2a):
        self.llm = llm
        self.memory = memory
        self.knowledge = knowledge
        self.tools = tools
        self.mcp = mcp
        self.a2a = a2a


class LLMService(Protocol):
    """Protocol：鸭子类型接口，不强制继承（Java 的 interface 类似但隐式）。"""
    def chat(self, prompt: str) -> str: ...


class MemoryService(Protocol):
    def get(self, session_id: str) -> list: ...
    def save(self, session_id: str, msg: dict) -> None: ...


class KnowledgeService(Protocol):
    """RAG / 向量库。"""
    def retrieve(self, query: str, k: int) -> list: ...


class ToolService(Protocol):
    def invoke(self, call: str) -> str: ...


class MCPGateway(Protocol):
    """MCP 网关：发现并调用远端 MCP Server 的工具。"""
    def list_tools(self) -> list: ...
    def call_tool(self, name: str, args: dict) -> Any: ...


class A2ABus(Protocol):
    """A2A 通信总线：与其他 Agent 协作。"""
    def delegate(self, target_agent: str, task: str) -> str: ...


# =================================================================
# 第 5 层：工程治理层（Workflow / Evaluation / Observability）
# =================================================================
class GovernanceLayer:
    """工程治理：让 Agent 可控、可观测、可评估。"""
    def __init__(self, eval_svc, observability, permission):
        self.eval = eval_svc
        self.obs = observability
        self.perm = permission

    def pre_check(self, req: UserRequest) -> None:
        # 权限校验 + 限流 + 上下文准备
        self.perm.check(req.user_id, req.content)

    def post_record(self, req: UserRequest, result: str) -> None:
        # 监控 / 日志 / 评估
        self.obs.record(req, result)
        score = self.eval.score(req.content, result)
        self.obs.metric("answer_quality", score)


# =================================================================
# 第 6 层：Enterprise Systems（企业既有系统）
# =================================================================
class EnterpriseSystems:
    """Agent 通过 MCP / A2A 访问的既有系统。"""
    db = "Oracle / MySQL"
    erp = "SAP / 用友"
    git = "GitLab / Bitbucket"
    docs = "Wiki / Confluence"


# =================================================================
# 主流程：打印架构
# =================================================================
def main() -> None:
    print("=" * 60)
    print("  企业级 Agent 架构 —— 分层职责说明")
    print("=" * 60)

    layers = [
        ("1. 用户 / 业务前端", "Web / 移动 / 内嵌聊天的入口", "UserRequest"),
        ("2. Agent Application", "编排入口，对外暴露", "AgentApplication"),
        ("3. 编排层 Workflow", "LangGraph 状态机 + 节点 + 条件边", "Orchestrator"),
        ("4. 能力层", "LLM / Memory / Knowledge / Tools / MCP / A2A", "CapabilityLayer"),
        ("5. 工程治理层", "Evaluation / Observability / Permission", "GovernanceLayer"),
        ("6. Enterprise Systems", "DB / ERP / Git / Wiki（通过 MCP+A2A 访问）", "EnterpriseSystems"),
    ]

    for name, role, code_class in layers:
        print(f"\n┌─ {name}")
        print(f"│  职责: {role}")
        print(f"│  代码类: {code_class}")

    print("\n" + "=" * 60)
    print("  关键论断：Agent 应用 ≠ 一个 LLM 调用")
    print("  上生产的 Agent = LLM + 编排 + 6 类能力 + 工程治理")
    print("=" * 60)


if __name__ == "__main__":
    main()
