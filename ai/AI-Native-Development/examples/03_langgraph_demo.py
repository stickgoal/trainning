"""
============================================================
 03_langgraph_demo.py — LangGraph Agent 工作流
 对应章节：第4章 LangGraph
============================================================

演示：
 - State / Node / Edge 三原语
 - 条件边（should_continue）
 - 循环（工具结果回灌再调 LLM）
 - 人工介入（interrupt_before）

【运行流程】
 set DEEPSEEK_API_KEY=sk-xxx
 pip install langgraph langchain-deepseek langchain-core
 python examples/03_langgraph_demo.py

【应用场景】
 需要多步推理、循环、人工审批的复杂 Agent（研发、审批、运维）。

【Python 特殊语法提示】
 - TypedDict          : 给 dict 加类型约束（Java 的 Record 类似）
 - Annotated[..., ...]: 给字段附加元信息（LangGraph 用来定义 reducer）
 - operator.add       : 用作 reducer，列表拼接（消息累加）
 - lambda             : 匿名函数
 - 条件边返回节点名     : 字符串即下一跳节点
"""

import os
import operator
from typing import Annotated, TypedDict, Literal

from langchain_deepseek import ChatDeepSeek
from langchain_core.tools import tool
from langchain_core.messages import HumanMessage, AIMessage
from langgraph.graph import StateGraph, START, END


# =================================================================
# 1. State —— 在节点间传递的共享状态（显式化！）
# =================================================================
class AgentState(TypedDict):
    """
    TypedDict：让 dict 有了类型约束。
    Java 类比：record AgentState(List<Message> messages, int step)。
    """
    # Annotated[..., operator.add] 告诉 LangGraph：
    # 这个字段在节点输出后不是覆盖，而是用 operator.add 拼接（列表合并）
    messages: Annotated[list, operator.add]
    step: int


# =================================================================
# 2. Tools —— Agent 可调用的工具
# =================================================================
@tool
def search_docs(query: str) -> str:
    """搜索文档库。当需要查资料时调用。"""
    return f"（模拟检索结果）关于 '{query}' 的相关内容：LangGraph 用图管理 Agent 状态。"


@tool
def write_code(spec: str) -> str:
    """根据需求写代码。当需要生成代码时调用。"""
    return f"（模拟代码）\n```python\ndef solution():\n    # 实现: {spec}\n    pass\n```"


tools = [search_docs, write_code]
tool_map = {t.name: t for t in tools}


# =================================================================
# 3. Node —— 每个节点是一个函数：(state) -> state 的部分更新
# =================================================================
def call_model(state: AgentState) -> dict:
    """节点1：调 LLM，让它决定下一步做什么。"""
    model = ChatDeepSeek(
        model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
        api_key=os.getenv("DEEPSEEK_API_KEY"),
    ).bind_tools(tools)  # bind_tools 把工具列表附给 LLM

    response = model.invoke(state["messages"])
    return {"messages": [response], "step": state["step"] + 1}


def call_tool(state: AgentState) -> dict:
    """节点2：执行 LLM 选定的工具。

    注意：LLM 一次可能返回多个 tool_calls（并行工具调用）。
    必须为每一个 tool_call 都生成一个对应的 ToolMessage，否则回灌给
    LLM 时会触发 400 错误：
        "An assistant message with 'tool_calls' must be followed by
         tool messages responding to each 'tool_call_id'"
    """
    last_msg: AIMessage = state["messages"][-1]
    from langchain_core.messages import ToolMessage

    tool_messages = []
    for tool_call in last_msg.tool_calls:  # 遍历全部工具调用
        tool_fn = tool_map[tool_call["name"]]
        result = tool_fn.invoke(tool_call["args"])
        # 每个 tool_call 都要有对应的 ToolMessage（用 tool_call_id 关联）
        tool_messages.append(
            ToolMessage(content=str(result), tool_call_id=tool_call["id"])
        )
    return {"messages": tool_messages}


def human_review(state: AgentState) -> dict:
    """节点3：人工介入（真实场景这里会暂停，等审批）。"""
    print("\n[人工介入] 当前状态：")
    print(f"  已执行步数: {state['step']}")
    print(f"  最后消息: {state['messages'][-1].content[:80]}...")
    print("  （模拟批准）✅ 通过，继续执行")
    return {"step": state["step"] + 1}


def finalize(state: AgentState) -> dict:
    """节点4：收尾。"""
    return {"step": state["step"] + 1}


# =================================================================
# 4. Edge —— 节点间的跳转（含条件边）
# =================================================================
def should_continue(state: AgentState) -> Literal["tools", "human", "__end__"]:
    """
    条件边：根据当前状态决定下一跳。
    Java 类比：状态机的 transition guard。
    """
    last_msg: AIMessage = state["messages"][-1]

    # 如果 LLM 决定调工具 → 去 tools 节点
    if hasattr(last_msg, "tool_calls") and last_msg.tool_calls:
        return "tools"

    # 步数够了 → 人工审批
    if state["step"] >= 3:
        return "human"

    # 否则结束
    return "__end__"


def should_re_continue(state: AgentState) -> Literal["agent", "finalize"]:
    """人工审批后的下一步。"""
    return "finalize"


# =================================================================
# 5. 组装 Graph —— 把 Node + Edge 连成工作流
# =================================================================
def build_graph():
    graph = StateGraph(AgentState)

    # 加节点
    graph.add_node("agent", call_model)
    graph.add_node("tools", call_tool)
    graph.add_node("human", human_review)
    graph.add_node("finalize", finalize)

    # 加边
    graph.add_edge(START, "agent")                       # 入口 → agent
    graph.add_conditional_edges("agent", should_continue, {  # agent → 条件分支
        "tools": "tools",
        "human": "human",
        "__end__": END,
    })
    graph.add_edge("tools", "agent")                     # tools → 回 agent（循环！）
    graph.add_edge("human", "finalize")                  # human → finalize
    graph.add_edge("finalize", END)                      # finalize → 结束

    # 编译：把声明式图编译成可执行 Runnable
    # interrupt_before=["human"]：进入 human 节点前暂停（人工介入）
    return graph.compile(interrupt_before=["human"])


# =================================================================
# 主流程
# =================================================================
def main() -> None:
    if not os.getenv("DEEPSEEK_API_KEY"):
        print("⚠️  请先 set DEEPSEEK_API_KEY=sk-xxx")
        return

    print("=" * 55)
    print("LangGraph Agent 工作流演示")
    print("=" * 55)

    app = build_graph()

    # 初始状态：一条用户消息
    initial_state: AgentState = {
        "messages": [HumanMessage(content="帮我查 LangGraph 的资料，然后写一段示例代码。")],
        "step": 0,
    }

    # ★ invoke 跑一遍：agent → tools → agent → human(暂停) → finalize
    # 注意：interrupt_before 会让执行在 human 节点前停下，这里直接更新状态模拟审批后继续
    result = app.invoke(initial_state, config={"recursion_limit": 10})

    print("\n" + "=" * 55)
    print(f"{result}")
    print(f"最终步数: {result['step']}")
    print(f"消息轮数: {len(result['messages'])}")
    print("工作流执行完毕（含循环 + 工具调用 + 人工介入）")


if __name__ == "__main__":
    main()
