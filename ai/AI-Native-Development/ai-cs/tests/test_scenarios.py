"""M4 端到端场景测试（S1~S5，需 DEEPSEEK_API_KEY）。

验收标准（03_开发计划.md M4）：
- S1~S5 全过；S4 防幻觉（知识库外问题必须答"未找到"）一票否决。
- 断言：answer 含 gold 关键词 + tool_calls 与 expect_tool 一致。

无 Key 时自动跳过（保持 CI 不失败）；有 Key 则真实调用 Agent。
"""

import os

import pytest

# 场景 → 测试集用例映射（S1~S5）
SCENARIO_CASES = [
    "T1",   # S1 出差住宿（search_policy）
    "T2",   # S2 年假档位（search_policy）
    "T9",   # S3 查员工（query_employee_db）
    "T4",   # S4 知识库外/防幻觉（一票否决）
    "T10",  # S5 查工单（query_ticket_status）
]

pytestmark = pytest.mark.skipif(
    not os.getenv("DEEPSEEK_API_KEY"),
    reason="需要 DEEPSEEK_API_KEY 才能跑端到端场景测试",
)


@pytest.fixture(scope="module")
def agent():
    """构建 Agent（module 级复用，避免每用例重建 LLM/检索器）。"""
    from src.agents.customer_service import build_agent
    from src.rag.loader import load_and_split
    from src.rag.retriever import build_retriever
    from src.rag.vectorstore import load_vectorstore
    from src.tools.langchain_tools import build_tools

    if load_vectorstore() is None:
        from src.rag.vectorstore import build_vectorstore

        build_vectorstore(load_and_split())

    retriever = build_retriever()
    tools = build_tools(retriever)
    return build_agent(tools)


def _case(cid: str) -> dict:
    from tests.testdata import TESTCASES

    return next(t for t in TESTCASES if t["id"] == cid)


def _ask(agent, question: str) -> tuple[str, list[str]]:
    """调用 Agent，返回 (answer, tool_calls)。"""
    result = agent.invoke({"messages": [{"role": "user", "content": question}]})
    answer = result["messages"][-1].content
    tool_calls: list[str] = []
    for msg in result["messages"]:
        # AIMessage.tool_calls 记录发起调用的工具名
        calls = getattr(msg, "tool_calls", None) or []
        for tc in calls:
            tool_calls.append(tc["name"])
    return answer, tool_calls


@pytest.mark.parametrize("cid", SCENARIO_CASES)
def test_scenario(agent, cid):
    """每个场景：answer 含 gold 关键词，且工具调用匹配 expect_tool。"""
    case = _case(cid)
    answer, tool_calls = _ask(agent, case["question"])

    print(f"\n[{cid}] Q: {case['question']}")
    print(f"  tools: {tool_calls}")
    print(f"  A: {answer[:120]}")

    # 断言 1：回答包含 gold 关键词（任一命中即可，容忍 LLM 措辞变化）
    assert any(kw in answer for kw in case["keywords"]), (
        f"{cid} 回答不含关键词 {case['keywords']}: {answer}"
    )

    # 断言 2：工具选择与期望一致
    expect = case["expect_tool"]
    if expect == "search_policy":
        assert "search_policy" in tool_calls, (
            f"{cid} 期望调 search_policy，实际 {tool_calls}"
        )
    else:
        assert expect in tool_calls, f"{cid} 期望调 {expect}，实际 {tool_calls}"


def test_s4_anti_hallucination(agent):
    """S4 一票否决：知识库外问题必须答"未找到"，不得编造。"""
    case = _case("T4")
    answer, _ = _ask(agent, case["question"])
    assert "未找到" in answer, f"S4 防幻觉失败：{answer}"
