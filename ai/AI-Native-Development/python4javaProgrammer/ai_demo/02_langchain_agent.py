"""第二章（5.2）：Tool 与 Agent —— 让模型「动手」而不只是「动嘴」。

案例背景：技术文档助手接到一个新需求——
"帮我查一下文档里怎么配置限流，顺便看看 T-1024 这个任务谁负责、还剩几天。"

这类问题的特点：**答案不在模型脑子里，在公司系统里**。
必须让模型能调用工具。这就是 Agent。

本文件回答四个问题：
1. 工具（Tool）在 LangChain 里到底是什么？
2. Agent 的"思考 → 调工具 → 看结果 → 再思考"循环是怎么跑起来的？
3. 模型凭什么知道该调哪个工具？（提示：靠你写的 docstring）
4. Agent 有什么做不到的？什么时候该换 LangGraph？（见 03）

运行：
    python ai_demo/02_langchain_agent.py
"""
from __future__ import annotations

from datetime import date

from langchain.agents import create_agent
from langchain_core.messages import AIMessage, HumanMessage, ToolMessage
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.tools import tool

from llm_factory import build_scripted_model

# ===========================================================================
# 1. 工具定义：docstring 和类型注解就是给模型看的说明书
# ===========================================================================


@tool
def search_docs(query: str) -> str:
    """在公司技术文档库中检索相关内容。

    Args:
        query: 检索关键词，例如「限流配置」「部署流程」
    """
    # 真实实现会查向量库；这里用固定数据演示
    corpus = {
        "限流": "限流通过 RATE_LIMIT_MAX_REQUESTS 与 RATE_LIMIT_WINDOW_SECONDS 两个配置项控制，"
        "在 api/middleware.py 中实现，默认 100 次 / 60 秒。",
        "部署": "生产部署使用 Gunicorn 管理 uvicorn worker，worker 数建议等于 CPU 核数。",
    }
    for key, text in corpus.items():
        if key in query:
            return text
    return "未检索到相关文档。"


@tool
def get_task(task_id: str) -> str:
    """查询任务详情，包括负责人、状态、截止日期。

    Args:
        task_id: 任务编号，形如 T-1024
    """
    tasks = {
        "T-1024": {"assignee": "alice", "status": "进行中", "due": "2026-09-10"},
        "T-2048": {"assignee": "bob", "status": "已完成", "due": "2026-08-20"},
    }
    t = tasks.get(task_id)
    if not t:
        return f"未找到任务 {task_id}"
    return f"任务 {task_id}：负责人 {t['assignee']}，状态 {t['status']}，截止 {t['due']}"


@tool
def days_until(target_date: str) -> str:
    """计算从今天到指定日期还有多少天。

    Args:
        target_date: 目标日期，格式 YYYY-MM-DD
    """
    y, m, d = map(int, target_date.split("-"))
    delta = (date(y, m, d) - date.today()).days
    if delta < 0:
        return f"已逾期 {-delta} 天"
    return f"还剩 {delta} 天"


TOOLS = [search_docs, get_task, days_until]


def demo_1_tool_schema() -> None:
    print("\n" + "=" * 68)
    print("1. 工具是什么：一个带 schema 的可调用对象")
    print("=" * 68)

    print("工具清单：")
    for t in TOOLS:
        print(f"  · {t.name:12} 参数={list(t.args.keys())}")
        print(f"    说明：{t.description.splitlines()[0]}")

    print("\nget_task 的完整 schema（这段 JSON 会被发给模型）：")
    import json

    print(json.dumps(get_task.args, ensure_ascii=False, indent=2)[:400])

    print("\n直接调用工具（不经过模型，用于测试和调试）：")
    print("  get_task.invoke({'task_id': 'T-1024'}) →", get_task.invoke({"task_id": "T-1024"}))
    print("  days_until.invoke(...) →", days_until.invoke({"target_date": "2026-12-31"}))

    print("""
要点：
  · @tool 把普通函数包装成「带元数据的工具」。元数据来自三处：
      - 函数名   → 工具名
      - 类型注解 → 参数 schema（模型照此填参数）
      - docstring → 功能说明（**模型靠它决定要不要调这个工具**）
  · 所以 docstring 不是写给同事看的，是写给模型看的。
    写「查询任务」不如写「查询任务详情，包括负责人、状态、截止日期」——
    后者模型的调用准确率明显更高。
  · Java 类比：@tool ≈ 给方法加 @Bean + 自动生成 OpenAPI schema。
    区别是"调用方"是模型，不是前端。""")


# ===========================================================================
# 2. Agent：工具调用循环
# ===========================================================================
def demo_2_agent_loop() -> None:
    print("\n" + "=" * 68)
    print("2. Agent：模型与工具之间的循环")
    print("=" * 68)

    # 剧本：模拟一个"想三步"的模型
    #   第1轮：决定查文档
    #   第2轮：决定查任务
    #   第3轮：信息够了，给出最终回答
    script = [
        AIMessage(
            content="",
            tool_calls=[
                {"name": "search_docs", "args": {"query": "限流配置"}, "id": "call_1"}
            ],
        ),
        AIMessage(
            content="",
            tool_calls=[{"name": "get_task", "args": {"task_id": "T-1024"}, "id": "call_2"}],
        ),
        AIMessage(content="限流由两个配置项控制（100 次/60 秒）；T-1024 由 alice 负责，截止 2026-09-10。"),
    ]
    llm = build_scripted_model(script)

    agent = create_agent(
        model=llm,
        tools=TOOLS,
        system_prompt="你是技术文档助手。需要外部信息时先调用工具，不要凭空猜测。",
    )

    print("Agent 对象类型：", type(agent).__name__)
    print("（注意：它不是黑盒对象，而是一个 CompiledStateGraph —— 编译好的 LangGraph 图。）")

    result = agent.invoke(
        {"messages": [HumanMessage(content="限流怎么配？顺便查下 T-1024 谁负责")]}
    )

    print("\n完整消息轨迹（Agent 的思考过程就藏在这里）：")
    for i, m in enumerate(result["messages"], 1):
        label = type(m).__name__
        if isinstance(m, ToolMessage):
            print(f"  {i}. [{label}] ← 工具返回：{str(m.content)[:50]}")
        elif isinstance(m, AIMessage) and getattr(m, "tool_calls", None):
            calls = ", ".join(f"{c['name']}({c['args']})" for c in m.tool_calls)
            print(f"  {i}. [{label}] → 决定调用：{calls}")
        elif isinstance(m, AIMessage):
            print(f"  {i}. [{label}] 最终回答：{str(m.content)[:60]}")
        else:
            print(f"  {i}. [{label}] {str(m.content)[:60]}")

    final = result["messages"][-1]
    print("\n最终答案：", final.content)

    print("""
循环机制（这是 Agent 的核心，务必理解）：
    ┌─────────────────────────────────────────┐
    │  模型收到：系统提示 + 历史消息 + 工具清单   │
    └──────────────────┬──────────────────────┘
                       ▼
              模型输出里有没有 tool_calls？
                 ╱              ╲
              有                 没有
             ╱                      ╲
    执行工具，结果包装成         输出就是最终答案
    ToolMessage 追加到历史          循环结束
             ╲                      ╱
              └──────→ 回到顶部 ────┘

  · 关键：**"要不要调工具"是模型自己决定的**，框架只负责执行和回填。
    所以工具描述写得含糊，模型就会该调不调、不该调乱调。
  · 每轮的模型输入都包含完整历史——模型"记得"上一步查到了什么。
  · Java 类比：这是一个 while 循环，退出条件是"模型不再请求工具"。
    LangChain 帮你写了这个循环、工具分发、消息回填。""")


# ===========================================================================
# 3. 结构化输出：让 Agent 返回 Pydantic 对象
# ===========================================================================
def demo_3_structured_agent() -> None:
    """结构化输出：把「搜集信息」和「格式化」拆成两步。

    这里用的是**比 response_format 更可控**的做法，也是生产里更常见的写法。
    """
    import json

    from langchain_core.output_parsers import JsonOutputParser
    from pydantic import BaseModel, Field

    print("\n" + "=" * 68)
    print("3. 结构化输出：Agent 搜集 + 独立一步格式化")
    print("=" * 68)

    class TaskBrief(BaseModel):
        """任务简报（Pydantic 即契约）。"""

        task_id: str = Field(description="任务编号")
        assignee: str = Field(description="负责人")
        key_points: list[str] = Field(description="要点列表，2-3 条")

    # ---- 第一步：Agent 负责搜集 ----
    llm_agent = build_scripted_model(
        [
            AIMessage(
                content="",
                tool_calls=[{"name": "get_task", "args": {"task_id": "T-2048"}, "id": "c1"}],
            ),
            AIMessage(content="T-2048 由 bob 负责，状态已完成，截止 2026-08-20，已正常交付。"),
        ]
    )
    agent = create_agent(model=llm_agent, tools=[get_task], system_prompt="你是任务简报助手。")
    result = agent.invoke({"messages": [HumanMessage(content="整理 T-2048 的简报")]})
    conversation = result["messages"][-1].content
    print("Agent 搜集到的信息：", conversation)

    # ---- 第二步：独立的格式化链，把自然语言压成 Pydantic ----
    parser = JsonOutputParser(pydantic_object=TaskBrief)
    format_prompt = ChatPromptTemplate.from_messages(
        [
            ("system", "把下面的任务信息整理成 JSON。"),
            ("human", "{format_instructions}\n\n原始信息：\n{info}"),
        ]
    ).partial(format_instructions=parser.get_format_instructions())

    llm_format = build_scripted_model(
        [
            AIMessage(
                content=json.dumps(
                    {
                        "task_id": "T-2048",
                        "assignee": "bob",
                        "key_points": ["状态：已完成", "截止日期 2026-08-20", "已正常交付"],
                    },
                    ensure_ascii=False,
                )
            )
        ]
    )

    brief: TaskBrief = TaskBrief.model_validate(
        (format_prompt | llm_format | parser).invoke({"info": conversation})
    )

    print("\n格式化后的 Pydantic 对象：")
    print("  task_id   :", brief.task_id)
    print("  assignee  :", brief.assignee)
    for i, p in enumerate(brief.key_points, 1):
        print(f"  key_points[{i}]: {p}")

    print("""
为什么要拆成两步（而不是用 response_format 一把梭）：

  · response_format 让**同一个模型**既搜集又格式化，任何一环出错都难定位。
  · 拆开之后：Agent 的输出可以人工抽查，格式化链可以单独写单元测试，
    中间结果能落库、能重放。
  · 格式化这一步还可以换个更小更便宜的模型——它不需要工具能力。

  这就是"关注点分离"在 Agent 工程里的具体形态。
  Java 类比：Agent ≈ Service 层，格式化链 ≈ 一个独立的 Converter。""")


# ===========================================================================
# 4. Agent 的边界：什么时候它不够用
# ===========================================================================
def demo_4_limits() -> None:
    print("\n" + "=" * 68)
    print("4. Agent 的边界：为什么还需要 LangGraph")
    print("=" * 68)
    print("""
Agent 把"下一步做什么"的决策权**完全交给了模型**。这在探索性任务里很灵活，
但在下面这些场景会失控：

  ① 流程有硬性规定
     "必须先检索、再生成、最后经合规检查才能发布"
     → Agent 可能跳步，你拦不住（只能靠 Prompt 恳求它）。

  ② 需要人工介入
     "生成后暂停，等人审批，批准才继续"
     → Agent 的循环里没有"暂停等人"这个动作。

  ③ 需要循环重试且带计数
     "质量不达标就重写，最多 3 次，第 4 次转人工"
     → 用 Agent 实现，重试次数散落在消息历史里，无法可靠控制。

  ④ 需要断点续跑 / 长时间挂起
     "这个审批可能要等两天"
     → Agent 进程不能挂两天。

共同点：**这些需求都是"流程结构"层面的，而 Agent 的流程结构是不可控的。**

解法：把流程从"模型自己决定"改成"你用状态机显式定义"——这就是 LangGraph。
而且注意：LangChain 1.x 的 create_agent 返回的就是一个 CompiledStateGraph，
**Agent 本身就是用 LangGraph 搭的**。所以从 Agent 迁移到 LangGraph，
不是换框架，是"把预置图换成自己画的图"。

→ 继续看 03_langgraph_flow.py""")


def main() -> None:
    print("=" * 68)
    print("Tool 与 Agent：让模型能调用外部能力")
    print("=" * 68)
    demo_1_tool_schema()
    demo_2_agent_loop()
    demo_3_structured_agent()
    demo_4_limits()
    print("\n" + "=" * 68)
    print("小结")
    print("  ✅ Agent 擅长：开放式任务、工具数量不多、无需严格流程约束")
    print("  ❌ Agent 不擅长：硬性流程、人工审批、可控重试、长时挂起")
    print("  → 后者交给 LangGraph（03）")
    print("=" * 68)


if __name__ == "__main__":
    main()
