"""评估脚本（M6，T6.2/T6.3/T6.4）。

跑全量测试集，输出三项指标（keyless 迷你评估，参考课程 05_rag.md 5.4.3）：
- context_precision: gold 关键词出现在召回上下文中的平均命中率
- faithfulness:      回答中 gold 关键词被上下文支持的平均比例（防幻觉量化）
- tool_accuracy:     工具选择与 expect_tool 一致率

用法：
  python scripts/evaluate.py            # 跑评估 + 保存/对比基线
  python scripts/evaluate.py --verbose  # 打印每条用例明细

阈值门禁：faithfulness ≥ 0.8、tool_accuracy ≥ 0.9、S4 防幻觉（T4/T11）必须通过。
"""

import argparse
import json
import os
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from tests.testdata import TESTCASES

BASELINE_PATH = Path(__file__).resolve().parents[1] / "tests" / "baseline.json"

# 知识库外用例 id（防幻觉一票否决）
OUT_OF_SCOPE_IDS = {"T4", "T11"}


def _load_agent():
    from src.rag.retriever import build_retriever
    from src.agents.customer_service import build_agent
    from src.tools.langchain_tools import build_tools
    from src.rag.vectorstore import load_vectorstore
    from src.rag.loader import load_and_split

    if load_vectorstore() is None:
        from src.rag.vectorstore import build_vectorstore

        build_vectorstore(load_and_split())
    retriever = build_retriever()
    return build_agent(build_tools(retriever))


def _ask(agent, question: str) -> tuple[str, str, list[str]]:
    """调用 Agent，返回 (answer, context, tool_calls)。context = 所有工具返回拼接。"""
    result = agent.invoke({"messages": [{"role": "user", "content": question}]})
    answer = result["messages"][-1].content
    tool_calls: list[str] = []
    context_parts: list[str] = []
    for msg in result["messages"]:
        for tc in (getattr(msg, "tool_calls", None) or []):
            tool_calls.append(tc["name"])
        content = getattr(msg, "content", None)
        if isinstance(content, str) and content:
            context_parts.append(content)
    return answer, "\n".join(context_parts), tool_calls


def _eval_case(case: dict, agent) -> dict:
    """单条用例评估，返回明细。"""
    answer, context, tool_calls = _ask(agent, case["question"])
    cid = case["id"]
    out_of_scope = cid in OUT_OF_SCOPE_IDS
    kws = case["keywords"]

    # context_precision：gold 关键词在上下文中的命中率（out_of_scope 不计入均值）
    cp = sum(1 for kw in kws if kw in context) / len(kws) if kws else 0.0

    # faithfulness：回答中的 gold 关键词被上下文支持的比例
    if out_of_scope:
        fa = 1.0 if "未找到" in answer else 0.0   # 防幻觉：答"未找到"即忠实
    else:
        ans_hits = [kw for kw in kws if kw in answer]
        fa = (sum(1 for kw in ans_hits if kw in context) / len(ans_hits)
              if ans_hits else 0.0)

    # tool_accuracy：期望工具（支持 "A+B" 组合）
    expect = case["expect_tool"]
    if "+" in expect:
        ta = all(e in tool_calls for e in expect.split("+"))
    else:
        ta = expect in tool_calls

    return {
        "id": cid,
        "domain": case["domain"],
        "out_of_scope": out_of_scope,
        "context_precision": round(cp, 3),
        "faithfulness": round(fa, 3),
        "tool_accuracy": ta,
        "answer": answer,
        "tool_calls": tool_calls,
        "expect_tool": expect,
    }


def _aggregate(details: list[dict]) -> dict:
    """汇总指标。context_precision 均值排除 out_of_scope 用例。"""
    cp_cases = [d for d in details if not d["out_of_scope"]]
    cp = sum(d["context_precision"] for d in cp_cases) / len(cp_cases) if cp_cases else 0.0
    fa = sum(d["faithfulness"] for d in details) / len(details)
    ta = sum(d["tool_accuracy"] for d in details) / len(details)
    halluc_ok = all(
        "未找到" in d["answer"] for d in details if d["out_of_scope"]
    )
    return {
        "context_precision": round(cp, 3),
        "faithfulness": round(fa, 3),
        "tool_accuracy": round(ta, 3),
        "anti_hallucination_pass": halluc_ok,
    }


def _print_report(metrics: dict, details: list[dict], baseline: dict | None, verbose: bool):
    print("=" * 60)
    print("ai-cs 评估报告（testdata.py 全量）")
    print("=" * 60)
    for d in details:
        if verbose:
            mark = "OK" if (d["faithfulness"] >= 0.8 and d["tool_accuracy"]) else "FAIL"
            print(f"  [{mark}] {d['id']:<4} {d['domain']:<14} tools={d['tool_calls']}")
            print(f"        cp={d['context_precision']:.2f} fa={d['faithfulness']:.2f} "
                  f"expect={d['expect_tool']}")
    print("-" * 60)
    print(f"context_precision : {metrics['context_precision']:.3f}")
    print(f"faithfulness      : {metrics['faithfulness']:.3f}   (阈值 ≥ 0.8)")
    print(f"tool_accuracy     : {metrics['tool_accuracy']:.3f}   (阈值 ≥ 0.9)")
    print(f"防幻觉(S4/T11)    : {'通过 ✓' if metrics['anti_hallucination_pass'] else '失败 ✗'}")
    if baseline:
        print("-" * 60)
        print("对比基线（↑ 提升 / ↓ 下降 / → 持平）:")
        for k in ("context_precision", "faithfulness", "tool_accuracy"):
            diff = metrics[k] - baseline.get(k, 0)
            arrow = "↑" if diff > 0.005 else ("↓" if diff < -0.005 else "→")
            print(f"  {k:<18} {baseline.get(k, 0):.3f} → {metrics[k]:.3f}  {arrow}")
    print("=" * 60)


def main() -> None:
    if not os.getenv("DEEPSEEK_API_KEY"):
        print("需要 DEEPSEEK_API_KEY 才能跑评估（调 LLM）。")
        sys.exit(1)

    parser = argparse.ArgumentParser(description="ai-cs 评估脚本")
    parser.add_argument("--verbose", action="store_true", help="打印每条用例明细")
    args = parser.parse_args()

    agent = _load_agent()
    details = [_eval_case(c, agent) for c in TESTCASES]
    metrics = _aggregate(details)

    baseline = None
    if BASELINE_PATH.exists():
        baseline = json.loads(BASELINE_PATH.read_text(encoding="utf-8"))

    _print_report(metrics, details, baseline, args.verbose)

    # 落盘基线（首次）或更新
    BASELINE_PATH.write_text(
        json.dumps(metrics, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    print(f"基线已保存: {BASELINE_PATH}")

    # 阈值门禁（T6.4）
    gates = [
        (metrics["faithfulness"] >= 0.8, "faithfulness ≥ 0.8"),
        (metrics["tool_accuracy"] >= 0.9, "tool_accuracy ≥ 0.9"),
        (metrics["anti_hallucination_pass"], "防幻觉用例必须通过"),
    ]
    failed = [name for ok, name in gates if not ok]
    if failed:
        print("\n❌ 门禁未通过: " + ", ".join(failed))
        sys.exit(2)
    print("\n✅ 全部门禁通过。")


if __name__ == "__main__":
    main()
