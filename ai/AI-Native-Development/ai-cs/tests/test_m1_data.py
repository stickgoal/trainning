"""M1 数据层测试：语料可读性 + mock_data 一致性。

验收标准（03_开发计划.md M1）：
- 6 份语料可被 TextLoader(encoding="utf-8") 读出且非空；
- mock_data 与测试集 testdata.py 口径一致（T9/T10 关键词能命中）。

本测试不调 LLM、不耗 API 额度，可高频回归。
"""

from pathlib import Path

import pytest

from src.core.config import Config
from src.tools.mock_data import EMPLOYEES, TICKETS, query_employee, query_ticket

# 语料目录期望结构：knowledge_docs/{hr,finance,admin}/*.txt，共 6 份
EXPECTED_DOCS = [
    "hr/leave_policy.txt",
    "hr/employee_handbook.txt",
    "finance/travel_policy.txt",
    "finance/expense_policy.txt",
    "admin/it_support.txt",
    "admin/office_admin.txt",
]


def _load_text(rel_path: str) -> str:
    """按 utf-8 读取语料文件（TextLoader 等价验证）。"""
    p = Config.KNOWLEDGE_DIR / rel_path
    assert p.exists(), f"缺少语料文件: {p}"
    return p.read_text(encoding="utf-8")


def test_all_knowledge_docs_readable_and_nonempty():
    """M1 验收：6 份语料均可 utf-8 读出且非空。"""
    for rel in EXPECTED_DOCS:
        text = _load_text(rel)
        assert text.strip(), f"{rel} 为空"


def test_knowledge_docs_structured_sections():
    """语料含结构化分节标记【章节名】，便于切分与检索。"""
    for rel in EXPECTED_DOCS:
        text = _load_text(rel)
        assert "【" in text, f"{rel} 缺少【章节】标记"
        # 每节至少 3 条规则（BM25 生效前提，实施注意 3.3）
        section_rule_lines = [
            line for line in text.splitlines() if line.strip().startswith(("1.", "2.", "3.", "4.", "5."))
        ]
        assert len(section_rule_lines) >= 3, f"{rel} 规则条数不足"


def test_employee_lookup_t9_consistency():
    """T9：查 E001 应返回张三 / 研发部 / 高级工程师。"""
    out = query_employee("E001")
    assert "张三" in out and "研发部" in out and "高级工程师" in out
    assert "入职日期" in out          # T12 档位推理依赖入职日期


def test_ticket_lookup_t10_consistency():
    """T10：查 RB2024001 应为审批中。"""
    out = query_ticket("RB2024001")
    assert "审批中" in out


def test_mock_data_coverage():
    """M1 验收：EMPLOYEES ≥ 5 人（含入职日期），TICKETS ≥ 3 单。"""
    assert len(EMPLOYEES) >= 5
    assert all("hire_date" in v for v in EMPLOYEES.values())
    assert len(TICKETS) >= 3
    statuses = {v["status"] for v in TICKETS.values()}
    assert len(statuses) >= 2, "工单应覆盖不同状态"


def test_lookup_invalid_id_returns_friendly_text():
    """工号/单号不存在时返回友好错误文本，不抛异常（实施注意 4.2）。"""
    assert "不存在" in query_employee("E999")
    assert "不存在" in query_ticket("RB9999999")


if __name__ == "__main__":
    pytest.main([__file__, "-v"])
