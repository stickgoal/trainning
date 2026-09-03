"""第三章（5.3）：LangGraph —— 当流程必须可控时，用状态机代替"模型自由发挥"。

案例背景：技术文档助手要承担一个**发布流程**：

    收到草稿需求 → 判断文档类型 → 生成内容 → 人工审批
                                      ↑          │
                                      └── 驳回 ───┤（最多重写 2 次）
                                                 ↓ 通过
                                               发布 → 结束

这个流程有四个硬约束，Agent 一个都做不到（见 02 的第四节）：
  ① 必须先分类再生成（顺序固定）
  ② 生成后必须暂停等人审批（可中断）
  ③ 驳回要回到上一步重写，且最多 2 次（可控循环）
  ④ 审批可能等两天（可持久化、断点续跑）

本文件回答五个问题：
1. State（状态）怎么定义？为什么叫"共享白板"？
2. 节点函数能做什么、不能做什么？
3. 条件边如何实现"路由"？
4. 循环 + 计数 怎么避免死循环？
5. interrupt + checkpointer 如何实现"暂停两天再继续"？

运行：
    python ai_demo/03_langgraph_flow.py
"""
from __future__ import annotations

from typing import Any, Literal, TypedDict, get_type_hints

from langchain_core.messages import AIMessage
from langgraph.checkpoint.memory import InMemorySaver
from langgraph.graph import END, START, StateGraph
from langgraph.types import Command, interrupt

from llm_factory import build_scripted_model

MAX_ATTEMPTS = 2  # 最多生成 2 次；2 次都没通过就转人工兜底，不再循环

# ===========================================================================
# 1. State：所有节点共享的"白板"
# ===========================================================================


class DocState(TypedDict):
    """文档发布流程的状态。

    每个节点读取它、往里写内容、返回**增量**。
    框架负责把增量合并回全局状态。

    Java 类比：这就是一次请求上下文（Context）对象，
    只不过由框架负责并发安全地合并各环节的写入。
    """

    topic: str  # 原始需求
    doc_type: str  # 分类结果：tutorial / reference / news
    draft: str  # 当前草稿
    feedback: str  # 审批意见
    approved: bool  # 是否通过
    rewrite_count: int  # 已重写次数（防死循环的关键）
    published: bool  # 是否已发布


def demo_1_state() -> None:
    print("\n" + "=" * 68)
    print("1. State：节点之间唯一的沟通方式")
    print("=" * 68)

    print("状态字段（DocState）：")
    for key, hint in get_type_hints(DocState).items():
        print(f"  · {key:14} : {hint.__name__}")

    print("""
要点：
  · 节点函数**不互相调用**。它们只读写这块共享白板。
    这是 LangGraph 和"函数调用链"最本质的区别。
  · 节点返回的是**增量字典**（要改哪些字段），不是完整状态。
    框架合并后传给下一个节点。
  · 好处：任何时刻都能把整块状态打印出来 / 存进数据库 / 回放。
    排查线上问题时，你能精确知道"卡在哪一步、当时状态是什么"。
  · Java 类比：State ≈ 一个不可变的 RequestContext，
    每个 Filter 往里塞东西再传给下一个。""")


# ===========================================================================
# 2. 节点：普通函数，签名固定为 (state) -> dict
# ===========================================================================
def classify(state: DocState) -> dict[str, Any]:
    """节点 1：判断文档类型。"""
    topic = state["topic"]

    llm = build_scripted_model([AIMessage(content="tutorial")])
    doc_type = str(llm.invoke(f"判断类型（tutorial/reference/news）：{topic}").content).strip()

    return {"doc_type": doc_type}


def generate(state: DocState) -> dict[str, Any]:
    """节点 2：生成草稿。带上审批意见（如果有）。"""
    feedback = state.get("feedback") or "（首次生成，无反馈）"
    topic = state["topic"]
    doc_type = state.get("doc_type", "tutorial")

    llm = build_scripted_model(
        [AIMessage(content=f"【{doc_type}草稿】关于「{topic}」的内容，已参考反馈：{feedback}")]
    )
    draft = str(llm.invoke(f"写一篇{doc_type}文档：{topic}；参考反馈：{feedback}").content)

    # 生成一次，计数加一。这个数字是循环能否退出的唯一依据。
    return {"draft": draft, "feedback": "", "rewrite_count": state.get("rewrite_count", 0) + 1}


def human_review(state: DocState) -> dict[str, Any]:
    """节点 3：人工审批 —— 图会在这里**暂停**。

    interrupt() 抛出特殊异常，框架把当前状态存进 checkpointer 后退出。
    之后用 Command(resume=...) 从这一行**继续执行**。
    """
    decision = interrupt(
        {
            "question": "请审批以下草稿，输入 approved（通过）或 feedback:你的意见",
            "draft": state["draft"],
            # generate 节点已把计数加过 1，所以这里直接读即为"当前是第几稿"
            "attempt": state.get("rewrite_count", 0),
            "max_attempts": MAX_ATTEMPTS,
        }
    )

    # ↓↓↓ 这一行在第一次运行时不会执行；恢复后才从此处继续 ↓↓↓
    text = str(decision)
    if text.strip().lower() == "approved":
        return {"approved": True, "feedback": ""}
    return {"approved": False, "feedback": text}


def publish(state: DocState) -> dict[str, Any]:
    """节点 4：发布。"""
    return {"published": True}


def demo_2_nodes() -> None:
    print("\n" + "=" * 68)
    print("2. 节点：普通函数，签名 (state) -> 增量字典")
    print("=" * 68)

    state: DocState = {
        "topic": "FastAPI 中间件执行顺序",
        "doc_type": "",
        "draft": "",
        "feedback": "",
        "approved": False,
        "rewrite_count": 0,
        "published": False,
    }

    print("单独调用 classify 节点（节点可以脱离图单测，这很重要）：")
    print("  输入 topic =", state["topic"])
    print("  返回增量 =", classify(state))

    print("""
要点：
  · 节点就是函数：输入整个 state，返回要修改的字段。
    **没有基类要继承，没有装饰器必须加**——所以极易单测。
  · 节点里可以做任何事：调模型、查数据库、发消息、纯计算。
  · 唯一约定：返回的 key 必须在 State 里声明过，否则框架报错
    （这个"编译期检查"能挡掉大量拼写错误）。""")


# ===========================================================================
# 3. 路由函数：条件边的"决策者"
# ===========================================================================
def route_after_review(state: DocState) -> Literal["publish", "generate", "end_manual"]:
    """审批之后往哪走——这是整个流程的策略中枢。

    注意：这里**没有任何模型参与**，是纯代码判断。
    这正是 LangGraph 的价值：把"必须遵守的规则"从模型手里拿回来。
    """
    if state.get("approved"):
        return "publish"

    if state.get("rewrite_count", 0) >= MAX_ATTEMPTS:
        return "end_manual"  # 次数用尽，转人工兜底，避免死循环

    return "generate"


def build_graph() -> StateGraph:
    """组装图：节点 → 边 → 编译。"""
    graph = StateGraph(DocState)

    # ① 注册节点
    graph.add_node("classify", classify)
    graph.add_node("generate", generate)
    graph.add_node("human_review", human_review)
    graph.add_node("publish", publish)

    # ② 普通边：固定跳转
    graph.add_edge(START, "classify")
    graph.add_edge("classify", "generate")
    graph.add_edge("generate", "human_review")
    graph.add_edge("publish", END)

    # ③ 条件边：运行时决定下一步
    graph.add_conditional_edges(
        "human_review",  # 从哪个节点出来
        route_after_review,  # 决策函数
        {  # 决策值 → 目标节点（END 是内置终止点）
            "publish": "publish",
            "generate": "generate",   # ← 回到 generate，形成循环
            "end_manual": END,
        },
    )
    return graph


def demo_3_graph_structure() -> None:
    print("\n" + "=" * 68)
    print("3. 条件边与循环：把规则从模型手里拿回来")
    print("=" * 68)

    graph = build_graph()
    checkpointer = InMemorySaver()
    app = graph.compile(checkpointer=checkpointer)

    g = app.get_graph()

    print("节点：")
    for name in g.nodes:
        print(f"  ● {name}")

    print("\n边：")
    for e in g.edges:
        mark = "  ← 循环回边" if e.source == "human_review" and e.target == "generate" else ""
        print(f"  {e.source} ──▶ {e.target}{mark}")

    print("""
  可视化提示：装了 pygraphviz 后可用 app.get_graph().draw_ascii()
  或 draw_mermaid_png() 出图。这里用文本打印，是为了保证零额外依赖也能跑。""")

    print("""
关键观察：
  · generate → human_review → generate 这条回边就是**循环**。
    在 LCEL 链里你画不出这条边——链是单向的 DAG。
  · 循环的退出条件写在 route_after_review 里：
    通过 → publish；未通过且重写 < 2 次 → generate；
    重写次数用尽 → end_manual（转人工）。
  · **这个退出条件是纯代码，不是 Prompt。**
    模型再怎么"想再试一次"，也突破不了 rewrite_count >= 2 这道闸。
    这就是"可控"的确切含义。

  Java 类比：整张图 ≈ 一个状态机（如 Spring StateMachine），
  条件边 ≈ 状态转移表里的 guard 表达式。""")


# ===========================================================================
# 4. 跑起来：中断 → 人工决策 → 恢复
# ===========================================================================
def demo_4_run_with_interrupt() -> None:
    print("\n" + "=" * 68)
    print("4. interrupt：让流程暂停，等人（可能等两天）")
    print("=" * 68)

    app = build_graph().compile(checkpointer=InMemorySaver())
    config = {"configurable": {"thread_id": "doc-2026-0903"}}

    initial: DocState = {
        "topic": "FastAPI 中间件执行顺序",
        "doc_type": "",
        "draft": "",
        "feedback": "",
        "approved": False,
        "rewrite_count": 0,
        "published": False,
    }

    # ---- 第一次运行：跑到 human_review 就停 ----
    print("▶ 第 1 次运行（跑到审批节点会自动暂停）：")
    result = app.invoke(initial, config=config)
    interrupts = result.get("__interrupt__") or []
    if interrupts:
        payload = interrupts[0].value
        print(f"  第 {payload['attempt']}/{payload['max_attempts']} 稿：", payload["draft"])
        print("  等待人工决策：", payload["question"])
    else:
        print("  draft:", result.get("draft"))

    print("""
  注意：invoke 正常返回了，没有抛异常。
  中断信息在 result["__interrupt__"] 里，把这段内容展示给人看即可。""")

    # ---- 人工决策：驳回 ----
    print("\n▶ 人工决策（驳回，要求补充示例）")
    result = app.invoke(
        Command(resume="feedback:需要补充一个中间件顺序的实际示例"), config=config
    )
    interrupts = result.get("__interrupt__") or []
    if interrupts:
        payload = interrupts[0].value
        print(f"  → 已根据反馈重新生成，第 {payload['attempt']}/{payload['max_attempts']} 稿：")
        print("   ", payload["draft"])
        print("   （再次暂停，等待审批）")
    else:
        print("  → 流程结束，未再中断。最终状态：", {k: result.get(k) for k in ("approved", "published")})

    # ---- 人工决策：通过 ----
    print("\n▶ 人工决策（第 2 次：通过）")
    result = app.invoke(Command(resume="approved"), config=config)
    print("  approved  =", result.get("approved"))
    print("  published =", result.get("published"))

    print("""
机制说明：
  · interrupt(payload) 抛出 GraphInterrupt，框架捕获后
    把**当前完整状态**写入 checkpointer，然后正常返回。
  · 恢复时用 Command(resume=答案) 再次 invoke，
    框架从 checkpointer 读出状态，**从 interrupt 那一行继续往下执行**。
  · 因为状态落在 checkpointer 里，这两次 invoke 之间可以隔两天、
    可以在另一个进程、甚至另一台机器上。
  · thread_id 是关键：它是这条流程实例的身份证。
    不同用户的审批靠它隔离。

  Java 类比：这是把方法栈帧**序列化存库**再恢复——
  Java 里做不到（栈帧无法序列化），所以通常要自己写"状态表 + 步骤枚举"。
  LangGraph 把这个能力内置了，这是它最不可替代的价值。""")


def demo_5_rewrite_limit() -> None:
    print("\n" + "=" * 68)
    print("5. 循环上限：怎么保证不死循环")
    print("=" * 68)

    print("连续驳回 3 次，看 rewrite_count 如何封顶：\n")

    app = build_graph().compile(checkpointer=InMemorySaver())
    config = {"configurable": {"thread_id": "doc-limit-demo"}}
    initial: DocState = {
        "topic": "限流中间件配置",
        "doc_type": "",
        "draft": "",
        "feedback": "",
        "approved": False,
        "rewrite_count": 0,
        "published": False,
    }

    result = app.invoke(initial, config=config)
    for i in range(1, MAX_ATTEMPTS + 2):
        print(f"  第 {i} 次审批：驳回")
        result = app.invoke(Command(resume=f"feedback:第 {i} 次修改意见"), config=config)
        count = result.get("rewrite_count", 0)
        interrupted = "__interrupt__" in result
        print(f"     → 已生成 {count} 稿；仍在等待审批={interrupted}")
        if not interrupted:
            print(f"     → 达到上限 {MAX_ATTEMPTS} 稿，走 end_manual 转人工，不再循环")
            break

    print(f"\n  最终：published={result.get('published')}, approved={result.get('approved')}")
    print(f"  达到上限（{MAX_ATTEMPTS} 次）后走 end_manual 直接结束，转人工兜底。")

    print("""
要点：
  · 图里的循环必须有明确的退出条件，且条件要能用**数字**表达。
  · 只靠"模型觉得可以了"当退出条件，等于把系统稳定性交给概率。
  · 兜底路径（end_manual）比死循环或超时好得多：
    它保证了"最坏情况下有人接手"，而不是"最坏情况下卡死"。""")


def main() -> None:
    print("=" * 68)
    print("LangGraph：用状态机表达必须被遵守的流程")
    print("=" * 68)
    demo_1_state()
    demo_2_nodes()
    demo_3_graph_structure()
    demo_4_run_with_interrupt()
    demo_5_rewrite_limit()
    print("\n" + "=" * 68)
    print("小结")
    print("  ✅ LangGraph 擅长：固定顺序、条件路由、可控循环、人工中断、断点续跑")
    print("  ❌ 不适合：完全没有固定结构的开放式探索（那种用 Agent 更省事）")
    print("  📌 记住：LangChain 1.x 的 create_agent 返回的也是 CompiledStateGraph")
    print("     —— Agent 就是框架预置的一张图，两者不是替代关系，是抽象层级关系")
    print("=" * 68)


if __name__ == "__main__":
    main()
