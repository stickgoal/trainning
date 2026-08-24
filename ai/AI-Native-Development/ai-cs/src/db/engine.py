"""业务数据库（替代 M1 mock_data，架构决策 D5 的演进）。

职责：
- employees 表：工号 → 姓名/部门/职位/入职日期
- tickets 表：单号 → 类型/状态/提交日期/金额
- query_employee / query_ticket：按号查，返回纯文本（与旧 mock_data 格式一致，
  工具层与测试集 testdata.py 的口径不变）

首次 init_db() 自动建表并从 SEED 灌入示例数据；真实部署时由外部系统同步写入。
"""

from datetime import date

import sqlite3

from src.core.config import Config

# ---------- 种子数据（与旧 mock_data.py 口径一致，保证测试集 T9/T10/T12 通过）----------
SEED_EMPLOYEES = [
    ("E001", "张三", "研发部", "高级工程师", date(2022, 3, 1)),   # 工龄~4.5y → 年假10天
    ("E002", "李四", "产品部", "产品经理", date(2024, 6, 1)),     # 工龄~2y   → 年假5天
    ("E003", "王五", "设计部", "UI 设计师", date(2015, 1, 1)),    # 工龄~11y  → 年假15天
    ("E004", "赵六", "市场部", "市场专员", date(2026, 2, 1)),     # 工龄<1y   → 无年假
    ("E005", "孙七", "研发部", "测试工程师", date(2023, 8, 1)),   # 工龄~3y   → 年假10天
]

SEED_TICKETS = [
    ("RB2024001", "报销单", "审批中", date(2026, 8, 1), 1800.0),
    ("RB2024002", "报销单", "已通过", date(2026, 7, 20), 3200.0),
    ("IT20240011", "IT 工单", "处理中", date(2026, 8, 15), 0.0),
]


def _connect() -> sqlite3.Connection:
    Config.DATA_DIR.mkdir(parents=True, exist_ok=True)
    conn = sqlite3.connect(str(Config.BUSINESS_DB))
    conn.row_factory = sqlite3.Row
    return conn


# 进程内自举守卫：首次查询时确保表已建（幂等，仅一次）。
_INITIALIZED = False


def _ensure() -> None:
    """懒初始化：直接调用工具也安全，不依赖服务启动钩子。"""
    global _INITIALIZED
    if not _INITIALIZED:
        init_db()
        _INITIALIZED = True


def init_db() -> None:
    """建表（幂等）+ 首次种子灌入。"""
    conn = _connect()
    try:
        conn.executescript(
            """
            CREATE TABLE IF NOT EXISTS employees (
                id         TEXT PRIMARY KEY,
                name       TEXT NOT NULL,
                department TEXT NOT NULL,
                position   TEXT NOT NULL,
                hire_date  TEXT NOT NULL       -- ISO date
            );
            CREATE TABLE IF NOT EXISTS tickets (
                id          TEXT PRIMARY KEY,
                type        TEXT NOT NULL,
                status      TEXT NOT NULL,
                submit_date TEXT NOT NULL,     -- ISO date
                amount      REAL NOT NULL DEFAULT 0
            );
            """
        )
        cur = conn.execute("SELECT COUNT(*) AS n FROM employees")
        if cur.fetchone()["n"] == 0:
            conn.executemany(
                "INSERT INTO employees(id, name, department, position, hire_date) "
                "VALUES (?, ?, ?, ?, ?)",
                [(e[0], e[1], e[2], e[3], e[4].isoformat()) for e in SEED_EMPLOYEES],
            )
        cur = conn.execute("SELECT COUNT(*) AS n FROM tickets")
        if cur.fetchone()["n"] == 0:
            conn.executemany(
                "INSERT INTO tickets(id, type, status, submit_date, amount) "
                "VALUES (?, ?, ?, ?, ?)",
                [(t[0], t[1], t[2], t[3].isoformat(), t[4]) for t in SEED_TICKETS],
            )
        conn.commit()
    finally:
        conn.close()


def query_employee(emp_id: str) -> str:
    """按工号查询员工，返回纯文本（含友好错误提示，不抛异常）。"""
    _ensure()
    emp_id = (emp_id or "").strip().upper()
    if not emp_id:
        return "未提供工号，请提供如 E001 的工号"
    conn = _connect()
    try:
        row = conn.execute(
            "SELECT * FROM employees WHERE id = ?", (emp_id,)
        ).fetchone()
    finally:
        conn.close()
    if row is None:
        return f"工号 {emp_id} 不存在，请确认工号格式（如 E001）"
    return (
        f"{row['name']} / {row['department']} / {row['position']} / "
        f"入职日期 {row['hire_date']}"
    )


def query_ticket(ticket_id: str) -> str:
    """按单号查询工单/报销单状态，返回纯文本。"""
    _ensure()
    ticket_id = (ticket_id or "").strip().upper()
    if not ticket_id:
        return "未提供单号，请提供如 RB2024001 的单号"
    conn = _connect()
    try:
        row = conn.execute(
            "SELECT * FROM tickets WHERE id = ?", (ticket_id,)
        ).fetchone()
    finally:
        conn.close()
    if row is None:
        return f"单号 {ticket_id} 不存在，请确认单号格式（如 RB2024001）"
    return (
        f"{row['type']} {ticket_id} 当前状态：{row['status']}，"
        f"提交日期 {row['submit_date']}，金额 {row['amount']:.2f} 元"
    )


def list_employees() -> list[dict]:
    """管理接口用：列出全部员工。"""
    _ensure()
    conn = _connect()
    try:
        rows = conn.execute(
            "SELECT id, name, department, position, hire_date FROM employees "
            "ORDER BY id"
        ).fetchall()
    finally:
        conn.close()
    return [dict(r) for r in rows]


def list_tickets() -> list[dict]:
    """管理接口用：列出全部工单。"""
    _ensure()
    conn = _connect()
    try:
        rows = conn.execute(
            "SELECT id, type, status, submit_date, amount FROM tickets ORDER BY id"
        ).fetchall()
    finally:
        conn.close()
    return [dict(r) for r in rows]
