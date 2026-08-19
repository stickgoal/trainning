"""模拟业务数据（M1，唯一可替换点）。

职责：进程内模拟 HR 员工库与工单/报销单库。
替换真实系统时只改本文件，工具层与上层调用方不变（架构决策 D5）。

⚠️ 本文件为测试集数据源：
- E001 须匹配 testdata.py T9（张三 / 研发部 / 高级工程师）；
- RB2024001 须匹配 T10（审批中）；
- E001 入职日期配合 T12 档位推理题（满 3 年不满 10 年 → 年假 10 天）。
"""

from datetime import date

# ---------- 模拟员工库 ----------
# key: 工号；value: 姓名 / 部门 / 职位 / 入职日期
EMPLOYEES: dict[str, dict] = {
    "E001": {
        "name": "张三",
        "department": "研发部",
        "position": "高级工程师",
        "hire_date": date(2022, 3, 1),      # 工龄约 4.5 年 → 满 3 年不满 10 年 → 年假 10 天
    },
    "E002": {
        "name": "李四",
        "department": "产品部",
        "position": "产品经理",
        "hire_date": date(2024, 6, 1),      # 工龄约 2 年 → 满 1 年不满 3 年 → 年假 5 天
    },
    "E003": {
        "name": "王五",
        "department": "设计部",
        "position": "UI 设计师",
        "hire_date": date(2015, 1, 1),      # 工龄约 11 年 → 满 10 年及以上 → 年假 15 天
    },
    "E004": {
        "name": "赵六",
        "department": "市场部",
        "position": "市场专员",
        "hire_date": date(2026, 2, 1),      # 工龄不满 1 年 → 无年假
    },
    "E005": {
        "name": "孙七",
        "department": "研发部",
        "position": "测试工程师",
        "hire_date": date(2023, 8, 1),      # 工龄约 3 年 → 满 3 年不满 10 年 → 年假 10 天
    },
}

# ---------- 模拟工单/报销单库 ----------
# key: 单号；value: 类型 / 状态 / 提交日期 / 金额
TICKETS: dict[str, dict] = {
    "RB2024001": {
        "type": "报销单",
        "status": "审批中",
        "submit_date": date(2026, 8, 1),
        "amount": 1800.0,
    },
    "RB2024002": {
        "type": "报销单",
        "status": "已通过",
        "submit_date": date(2026, 7, 20),
        "amount": 3200.0,
    },
    "IT20240011": {
        "type": "IT 工单",
        "status": "处理中",
        "submit_date": date(2026, 8, 15),
        "amount": 0.0,
    },
}


def query_employee(emp_id: str) -> str:
    """按工号查询员工，返回纯文本（工具层直接引用，不抛异常）。"""
    emp = EMPLOYEES.get(emp_id.strip().upper())
    if emp is None:
        return f"工号 {emp_id} 不存在，请确认工号格式（如 E001）"
    return (
        f"{emp['name']} / {emp['department']} / {emp['position']} / "
        f"入职日期 {emp['hire_date'].isoformat()}"
    )


def query_ticket(ticket_id: str) -> str:
    """按单号查询工单/报销单状态，返回纯文本。"""
    t = TICKETS.get(ticket_id.strip().upper())
    if t is None:
        return f"单号 {ticket_id} 不存在，请确认单号格式（如 RB2024001）"
    return (
        f"{t['type']} {ticket_id} 当前状态：{t['status']}，"
        f"提交日期 {t['submit_date'].isoformat()}，金额 {t['amount']:.2f} 元"
    )
