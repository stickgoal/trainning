"""业务库单元测试（Phase A）：种子灌入、按号查询、缺失处理、列表。

全部离线，使用临时 SQLite 文件，不污染 Config.BUSINESS_DB。
"""

import pytest

from src.db import engine


@pytest.fixture
def biz_db(tmp_path, monkeypatch):
    """把业务库指向临时文件并初始化。"""
    db_file = tmp_path / "business.db"
    monkeypatch.setattr(engine.Config, "BUSINESS_DB", db_file)
    monkeypatch.setattr(engine.Config, "DATA_DIR", tmp_path)
    engine.init_db()
    return engine


def test_init_db_creates_and_seeds(biz_db):
    emps = biz_db.list_employees()
    tickets = biz_db.list_tickets()
    assert len(emps) == 5
    assert len(tickets) == 3


def test_query_employee_found(biz_db):
    out = biz_db.query_employee("E001")
    assert "张三" in out and "研发部" in out and "2022-03-01" in out


def test_query_employee_case_insensitive(biz_db):
    assert "李四" in biz_db.query_employee("e002")


def test_query_employee_missing(biz_db):
    out = biz_db.query_employee("E999")
    assert "不存在" in out


def test_query_employee_empty(biz_db):
    out = biz_db.query_employee("")
    assert "未提供工号" in out


def test_query_ticket_found(biz_db):
    out = biz_db.query_ticket("RB2024001")
    assert "审批中" in out and "1800.00" in out


def test_query_ticket_missing(biz_db):
    assert "不存在" in biz_db.query_ticket("RB0000000")


def test_query_ticket_empty(biz_db):
    assert "未提供单号" in biz_db.query_ticket("")


def test_employee_seed_matches_testdata_contract(biz_db):
    """E001 工龄须支持 T12 档位推理（满3年不满10年→年假10天）。"""
    e1 = next(e for e in biz_db.list_employees() if e["id"] == "E001")
    assert e1["hire_date"] == "2022-03-01"
    rb1 = next(t for t in biz_db.list_tickets() if t["id"] == "RB2024001")
    assert rb1["status"] == "审批中"
