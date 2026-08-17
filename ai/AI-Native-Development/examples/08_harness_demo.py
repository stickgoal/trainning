"""
============================================================
 08_harness_demo.py — Agent Harness 迷你运行时
 对应章节：第9章 Harness Engineering
============================================================

演示 7 个子能力中最关键的 3 个（精简版）：
 - Context Management：上下文超窗口自动压缩
 - Tool Runtime + Permission：工具调用前权限校验
 - Monitoring：每次决策落日志

 结合 Claude Code / Codex 说明真实 Harness 形态。

【运行流程】
 pip install openai
 set DEEPSEEK_API_KEY=sk-xxx
 python examples/08_harness_demo.py

【应用场景】
 任何要上生产的 Agent，都得有 Harness，否则只是一个 demo。
 "Agent = LLM + Harness"，没有 Harness 的 Agent 不稳定。

【Python 特殊语法提示】
 - @dataclass             : 自动构造器
 - contextlib.contextmanager : 把普通生成器函数变 Context Manager（Java try-with）
 - yield                  : 生成器关键字，产出值后暂停（Java 无等价，类似协程 yield）
 - Callable 类型提示      : 函数签名类型（Java functional interface）
"""

import os
import json
from dataclasses import dataclass, field
from datetime import datetime
from contextlib import contextmanager
from typing import Callable, Any


# =================================================================
# 1. 工具定义 + 权限模型
# =================================================================
@dataclass
class ToolDef:
    """工具定义：函数 + 权限等级。"""
    name: str
    func: Callable[..., Any]
    risk_level: str  # "safe" / "sensitive" / "dangerous"


class ToolRuntime:
    """工具运行时：负责权限校验 + 执行 + 超时/重试（这里只实现权限）。"""

    def __init__(self):
        self.tools: dict[str, ToolDef] = {}

    def register(self, tool: ToolDef) -> None:
        self.tools[tool.name] = tool

    def call(self, name: str, args: dict, require_approval: bool = False) -> Any:
        """
        调用工具前先校验权限。
        dangerous 级必须 require_approval=True；否则拒绝。
        """
        tool = self.tools.get(name)
        if not tool:
            raise ValueError(f"未注册工具: {name}")

        if tool.risk_level == "dangerous" and not require_approval:
            raise PermissionError(f"工具 {name} 是危险操作，需人工审批")

        # 真实 Harness 这里还应有：超时、重试、沙箱、审计
        return tool.func(**args)


# =================================================================
# 2. Context Management：上下文窗口管理
# =================================================================
@dataclass
class ContextWindow:
    """
    上下文窗口：管理对话历史，超 token 上限自动压缩。
    Java 类比：一个有容量上限的 LRU 缓存，满了就淘汰最旧的。
    """
    max_tokens: int = 4000
    messages: list = field(default_factory=list)

    def _estimate_tokens(self, text: str) -> int:
        # 粗略估算：1 个 token ≈ 3.5 字符（中文）/ 4 字符（英文）
        return max(1, len(text) // 3)

    def total_tokens(self) -> int:
        return sum(self._estimate_tokens(m["content"]) for m in self.messages)

    def add(self, role: str, content: str) -> None:
        self.messages.append({"role": role, "content": content})
        # 超窗口 → 压缩：保留 system + 最近 N 条
        if self.total_tokens() > self.max_tokens:
            self._compress()

    def _compress(self) -> None:
        """压缩策略：保留首条 system，把中间消息摘要，保留最近 2 条。"""
        if len(self.messages) <= 3:
            return
        system = self.messages[0]
        recent = self.messages[-2:]
        summary_text = " ".join(m["content"][:50] for m in self.messages[1:-2])
        self.messages = [
            system,
            {"role": "system", "content": f"[历史摘要] {summary_text}"},
            *recent,
        ]


# =================================================================
# 3. Monitoring：决策日志
# =================================================================
class DecisionLogger:
    """记录每次 Agent 决策，便于追溯。"""

    def __init__(self, log_file: str = "agent_decisions.jsonl"):
        self.log_file = log_file

    @contextmanager
    def log_step(self, step_name: str):
        """
        @contextmanager + yield：把函数变 Context Manager。
        用法：with logger.log_step("调工具") as log: log.add(...)
        进入时记开始时间，退出时记结束 + 写文件。
        """
        entry: dict = {"step": step_name, "start": datetime.now().isoformat()}
        # 用一个简单对象收集过程中信息
        class _Log:
            def __init__(self): self.data = {}
            def add(self, k, v): self.data[k] = v
        log = _Log()

        yield log  # ← yield 暂停，with 块内代码在此执行

        entry["end"] = datetime.now().isoformat()
        entry["data"] = log.data
        with open(self.log_file, "a", encoding="utf-8") as f:
            f.write(json.dumps(entry, ensure_ascii=False) + "\n")


# =================================================================
# 4. 迷你 Harness：把上面三件套组装
# =================================================================
class MiniHarness:
    """
    Agent Harness：把 LLM + 工具 + 上下文 + 日志包在一起。
    真实 Harness（Claude Code / Codex）远比这复杂，但骨架一致。
    """

    def __init__(self, model, tools: list[ToolDef]):
        self.model = model
        self.context = ContextWindow(max_tokens=60)  # 故意调小，演示压缩触发
        self.tool_runtime = ToolRuntime()
        self.logger = DecisionLogger()
        for t in tools:
            self.tool_runtime.register(t)

        self.context.add("system", "你是一个能调用工具的助手。")

    def run(self, user_input: str) -> str:
        """一个完整的 Agent 循环：调 LLM → 选工具 → 执行 → 回灌。"""
        self.context.add("user", user_input)

        with self.logger.log_step("LLM 推理") as log:
            # 调 LLM（带工具）
            response = self.model.invoke(self.context.messages)
            log.add("llm_response", response.content[:80])

        # 模拟 LLM 决定调用工具
        # 真实场景：解析 response.tool_calls
        if "查询" in user_input or "删除" in user_input:
            tool_name = "delete_file" if "删除" in user_input else "query_db"
            with self.logger.log_step(f"调工具 {tool_name}") as log:
                try:
                    # 危险工具需要审批
                    require_approval = "删除" in user_input
                    result = self.tool_runtime.call(
                        tool_name,
                        {"arg": user_input},
                        require_approval=require_approval,
                    )
                    log.add("result", str(result))
                except PermissionError as e:
                    result = f"❌ 权限拒绝: {e}"
                    log.add("blocked", str(e))
        else:
            result = "无需工具"

        self.context.add("assistant", response.content + f"\n[工具结果] {result}")
        return response.content


# =================================================================
# 演示工具
# =================================================================
def query_db(arg: str) -> str:
    return f"DB 查询结果: '与 {arg} 相关的数据'"

def delete_file(arg: str) -> str:
    return f"已删除（模拟）: {arg}"


# =================================================================
# 主流程
# =================================================================
def main() -> None:
    print("=" * 55)
    print("Mini Harness 演示（上下文 / 权限 / 日志）")
    print("=" * 55)

    # 准备一个假的 model（不调真 LLM，演示 Harness 机制）
    class FakeModel:
        def invoke(self, messages):
            class R: content = "我决定调用工具完成这个任务。"
            return R()

    tools = [
        ToolDef("query_db", query_db, "safe"),
        ToolDef("delete_file", delete_file, "dangerous"),
    ]

    harness = MiniHarness(FakeModel(), tools)

    # 案例 1：安全查询
    print("\n[案例1] 用户: 帮我查询 销售数据")
    print("Agent:", harness.run("帮我查询 销售数据")[:60], "...")

    # 案例 2：危险操作，未审批 → 拒绝
    print("\n[案例2] 用户: 帮我删除 文件 a.txt")
    print("Agent:", harness.run("帮我删除 文件 a.txt")[:60], "...")

    # 案例 3：制造长上下文，触发压缩
    print("\n[案例3] 制造长对话触发上下文压缩...")
    for i in range(10):
        harness.context.add("user", f"这是第 {i} 轮对话，内容内容内容内容内容。")
    print(f"压缩后消息数: {len(harness.context.messages)}（包含摘要）")

    print(f"\n→ 决策日志已写入: {harness.logger.log_file}")
    print("  可用 cat agent_decisions.jsonl 查看")


if __name__ == "__main__":
    main()
