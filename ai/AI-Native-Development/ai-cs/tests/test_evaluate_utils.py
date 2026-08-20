"""scripts/evaluate.py 纯逻辑测试：指标聚合与测试集口径一致性。

不调 LLM（不 import _eval_case 的 LLM 依赖，仅测 _aggregate 等纯函数）。
"""

import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from scripts.evaluate import OUT_OF_SCOPE_IDS, _aggregate
from tests.testdata import TESTCASES


def _detail(cid, domain="hr/leave", cp=1.0, fa=1.0, ta=True, oos=False, answer="有答案"):
    return {
        "id": cid,
        "domain": domain,
        "out_of_scope": oos,
        "context_precision": cp,
        "faithfulness": fa,
        "tool_accuracy": ta,
        "answer": answer,
        "tool_calls": [],
        "expect_tool": "search_policy",
    }


def test_aggregate_all_pass():
    details = [
        _detail("T1", cp=1.0, fa=1.0, ta=True),
        _detail("T2", cp=0.5, fa=0.8, ta=True),
    ]
    m = _aggregate(details)
    assert m["context_precision"] == 0.75
    assert m["faithfulness"] == 0.9
    assert m["tool_accuracy"] == 1.0
    assert m["anti_hallucination_pass"] is True


def test_aggregate_excludes_out_of_scope_from_cp():
    """context_precision 均值排除 out_of_scope 用例。"""
    details = [
        _detail("T1", cp=1.0),
        _detail("T4", oos=True, answer="知识库中未找到相关内容"),
    ]
    m = _aggregate(details)
    assert m["context_precision"] == 1.0          # 只有 T1 计入
    assert m["anti_hallucination_pass"] is True


def test_aggregate_all_out_of_scope_cp_zero():
    """全部为 out_of_scope 时 context_precision 取 0（无退化崩溃）。"""
    details = [_detail("T4", oos=True, answer="知识库中未找到")]
    m = _aggregate(details)
    assert m["context_precision"] == 0.0


def test_aggregate_anti_hallucination_fail():
    """有 out_of_scope 用例未答"未找到" → 门禁判定失败。"""
    details = [
        _detail("T4", oos=True, answer="食堂 12 点开饭"),   # 编造
    ]
    m = _aggregate(details)
    assert m["anti_hallucination_pass"] is False


def test_aggregate_tool_accuracy_partial():
    """部分工具选错 → tool_accuracy 按比例。"""
    details = [
        _detail("T1", ta=True),
        _detail("T9", ta=False),
    ]
    assert _aggregate(details)["tool_accuracy"] == 0.5


def test_testdata_out_of_scope_consistency():
    """T4/T11 口径：expect_tool 允许检索但 gold_answer 必须含"未找到"。"""
    for t in TESTCASES:
        if t["id"] in OUT_OF_SCOPE_IDS:
            assert "未找到" in t["gold_answer"], f"{t['id']} 防幻觉口径不一致"
            assert t["expect_tool"] == "search_policy"


def test_testdata_has_required_coverage():
    """M6 定稿要求：≥12 条，覆盖工具类 + 知识库外 + 档位推理。"""
    assert len(TESTCASES) >= 12
    tools = {t["expect_tool"] for t in TESTCASES}
    assert "query_employee_db" in tools
    assert "query_ticket_status" in tools
    assert any("combo" in t["domain"] for t in TESTCASES)      # 组合题
    assert sum(1 for t in TESTCASES if t["domain"] == "out_of_scope") >= 2


def test_testdata_ids_unique():
    ids = [t["id"] for t in TESTCASES]
    assert len(ids) == len(set(ids))
