# -*- coding: utf-8 -*-
"""
示例 03: LangGraph Agent 工作流
================================
展示 LangGraph 核心：Graph、Node、Edge、State

实现一个简单的 ReAct (Reasoning + Acting) Agent 工作流：
理解 → 计划 → 执行 → 观察 → 反思 → 输出

适合 Java 工程师阅读：State、Node、Edge 机制均有中文注释
"""

from typing import TypedDict, Annotated, Literal
from langgraph.graph import StateGraph, START, END
from langgraph.graph.message import add_messages
from langchain_openai import ChatOpenAI
from langchain_core.tools import tool
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage


# ============================================
# 1. State 定义 — Agent 的共享数据容器
# ============================================

# TypedDict：Python 类型提示，定义一个字典的键和值类型
# 类似 Java 的 Record 或 Typed Map
# State 在 LangGraph 中类似 Spring StateMachine 的 State Object

class AgentState(TypedDict):
    """
    Agent 工作流的状态对象。
    在每个 Node 之间传递，Node 读取并更新这个状态。
    """
    # messages: 对话历史，使用 add_messages reducer
    # Annotated[..., add_messages] 表示这个字段使用 add_messages 函数来合并更新
    # reducer 的作用：当 Node 返回新的 messages 时，不是替换而是追加
    # Java 类比：类似 Redux 的 reducer 模式
    messages: Annotated[list, add_messages]
    
    # 当前执行步骤
    current_step: str
    
    # 执行计划
    plan: list[str]
    
    # 工具执行结果
    results: list[str]
    
    # 循环次数（防止无限循环）
    iterations: int


# ============================================
# 2. Tool 定义 — Agent 可调用的工具
# ============================================

@tool
def search_web(query: str) -> str:
    """搜索网络获取信息"""
    return f"搜索结果：关于 '{query}' 的最新信息..."


@tool
def write_code(spec: str) -> str:
    """根据需求描述生成代码"""
    return f"根据需求 '{spec}' 生成的代码：\n```python\ndef solution():\n    pass\n```"


@tool
def run_tests(code: str) -> str:
    """运行测试并返回结果"""
    return f"测试结果：全部通过 (3/3)"


tools = [search_web, write_code, run_tests]

# 初始化 LLM 并绑定工具
llm = ChatOpenAI(model="gpt-4o", temperature=0)
llm_with_tools = llm.bind_tools(tools)


# ============================================
# 3. Node 定义 — 工作流的执行单元
# ============================================

# 每个 Node 是一个函数：
# 输入：当前 State
# 输出：State 的部分更新（不需要返回完整 State）
# Java 类比：StateMachine 的 Action 方法

def understand_node(state: AgentState) -> dict:
    """
    节点1：理解用户意图
    类似 Controller 接收请求后进行预处理
    """
    user_input = state["messages"][-1].content  # 获取最后一条用户消息

    system_msg = SystemMessage(content="""你是一个需求分析专家。
    分析用户需求，拆解为2-3个执行步骤。返回步骤列表。""")

    response = llm.invoke([system_msg, HumanMessage(content=user_input)])

    # 返回 State 的部分更新
    # LangGraph 会把这些更新合并到当前 State 中
    return {
        "current_step": "understood",
        "plan": [response.content],
        "iterations": state["iterations"] + 1,
    }


def execute_node(state: AgentState) -> dict:
    """
    节点2：执行工具调用
    LLM 决定调用哪些工具，应用层执行
    """
    # 构建包含历史和计划的上下文
    context = f"执行计划: {state['plan']}\n当前迭代: {state['iterations']}"

    response = llm_with_tools.invoke([
        SystemMessage(content="你是一个执行 Agent。根据计划选择工具执行。"),
        HumanMessage(content=context),
    ])

    # 检查是否有工具调用
    if response.tool_calls:
        # 执行工具调用
        results = []
        for tool_call in response.tool_calls:
            tool_name = tool_call["name"]
            tool_args = tool_call["args"]

            # 在工具列表中查找并执行
            tool_map = {t.name: t for t in tools}
            if tool_name in tool_map:
                result = tool_map[tool_name].invoke(tool_args)
                results.append(f"[{tool_name}] {result}")

        return {
            "results": results,
            "messages": [response],  # 通过 reducer 追加到 messages
            "current_step": "executed",
        }

    return {
        "messages": [response],
        "current_step": "executed",
    }


def reflect_node(state: AgentState) -> dict:
    """
    节点3：反思执行结果
    判断任务是否完成，是否需要继续迭代
    """
    results_str = "\n".join(state["results"]) if state["results"] else "无结果"

    response = llm.invoke([
        SystemMessage(content="""你是一个反思 Agent。
        分析执行结果，判断任务是否完成。
        只回答 "完成" 或 "继续"。"""),
        HumanMessage(content=f"执行结果:\n{results_str}\n\n迭代次数: {state['iterations']}"),
    ])

    # 更新状态
    return {
        "current_step": response.content.strip(),  # "完成" 或 "继续"
        "iterations": state["iterations"] + 1,
    }


# ============================================
# 4. Edge 定义 — 控制流
# ============================================

# Edge 连接 Node，决定执行顺序
# 条件 Edge (Conditional Edge)：根据 State 动态决定下一个 Node
# Java 类比：StateMachine 的 Transition + Guard Condition

def should_continue(state: AgentState) -> Literal["execute", "synthesize"]:
    """
    条件判断函数：决定工作流的下一步走向
    
    返回值是下一个 Node 的名称
    Literal 类型提示表示返回值只能是这两个字符串之一
    Java 类比：enum Route { EXECUTE, SYNTHESIZE }
    """
    if state["iterations"] >= 3:
        # 防止无限循环：最多迭代3次
        return "synthesize"

    if "完成" in state.get("current_step", ""):
        return "synthesize"
    else:
        return "execute"


def synthesize_node(state: AgentState) -> dict:
    """节点4：综合所有结果，生成最终回答"""
    all_results = "\n".join(state["results"]) if state["results"] else "无工具调用结果"

    response = llm.invoke([
        SystemMessage(content="你是一个总结 Agent。根据执行结果生成最终回答。"),
        HumanMessage(content=f"所有结果:\n{all_results}\n\n请总结。"),
    ])

    return {
        "messages": [response],
        "current_step": "done",
    }


# ============================================
# 5. Graph 构建 — 组装工作流
# ============================================

# StateGraph：LangGraph 的核心图构建器
# Java 类比：StateMachineBuilder
workflow = StateGraph(AgentState)

# 添加节点
workflow.add_node("understand", understand_node)
workflow.add_node("execute", execute_node)
workflow.add_node("reflect", reflect_node)
workflow.add_node("synthesize", synthesize_node)

# 添加边（固定边）
# START → understand → execute → reflect
workflow.add_edge(START, "understand")
workflow.add_edge("understand", "execute")
workflow.add_edge("execute", "reflect")
workflow.add_edge("synthesize", END)

# 添加条件边
# reflect 节点根据 should_continue 的返回值决定走向
# 如果返回 "execute" → 回到 execute 节点（形成循环）
# 如果返回 "synthesize" → 进入 synthesize 节点（结束）
workflow.add_conditional_edges(
    "reflect",           # 源节点
    should_continue,     # 条件判断函数
    {
        "execute": "execute",      # 条件返回 "execute" 时 → 路由到 execute 节点
        "synthesize": "synthesize", # 条件返回 "synthesize" 时 → 路由到 synthesize 节点
    },
)

# 编译图
# compile() 返回一个可执行的图实例
# 类似 StateMachine 的 build() 方法
app = workflow.compile()


# ============================================
# 6. 执行 Agent 工作流
# ============================================

print("=== LangGraph Agent 工作流 ===")
print("工作流结构：START → understand → execute → reflect → (循环 or synthesize) → END\n")

# 初始状态
initial_state: AgentState = {
    "messages": [HumanMessage(content="帮我搜索 Python 异步编程的最佳实践，然后写一个示例代码")],
    "current_step": "",
    "plan": [],
    "results": [],
    "iterations": 0,
}

# invoke 执行整个工作流
# LangGraph 会按照图的定义自动执行节点和路由
final_state = app.invoke(initial_state)

print("\n=== 执行结果 ===")
print(f"总迭代次数: {final_state['iterations']}")
print(f"执行步骤: {final_state['current_step']}")
print(f"工具结果数量: {len(final_state['results'])}")
for i, result in enumerate(final_state["results"]):
    print(f"  结果 {i+1}: {result[:80]}...")

# 最后一条消息是最终回答
if final_state["messages"]:
    last_msg = final_state["messages"][-1]
    print(f"\n最终回答: {last_msg.content[:200]}...")


# ============================================
# 7. 可视化工作流（伪代码）
# ============================================

print("\n=== 工作流可视化 ===")
print("""
    ┌─────────┐
    │  START  │
    └────┬────┘
         ▼
    ┌─────────────┐
    │ understand  │  ← 理解用户意图
    └─────┬───────┘
          ▼
    ┌─────────────┐
    │  execute    │  ← 执行工具调用 ◄────┐
    └─────┬───────┘                     │
          ▼                             │
    ┌─────────────┐                     │
    │  reflect    │  ← 反思判断          │
    └─────┬───────┘                     │
          │                             │
     ┌────┴────┐                        │
     │ 条件判断 │                        │
     └────┬────┘                        │
      继续?├──是──────────────────────────┘
          │
          否
          ▼
    ┌─────────────┐
    │ synthesize  │  ← 综合结果
    └─────┬───────┘
          ▼
    ┌─────────┐
    │   END   │
    └─────────┘
""")
