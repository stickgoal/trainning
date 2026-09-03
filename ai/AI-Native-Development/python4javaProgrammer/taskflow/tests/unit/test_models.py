"""模型层单元测试。

特点：不碰 FastAPI、不碰网络、不碰磁盘 —— 纯内存计算，毫秒级。
这类测试应该占测试金字塔的最底层（数量最多、跑得最快）。
"""
from datetime import date, datetime

import pytest
from pydantic import ValidationError

from taskflow.models.base import ApiResponse, Page
from taskflow.models.task import (
    ALLOWED_TRANSITIONS,
    TERMINAL_STATUSES,
    TaskCreate,
    TaskPriority,
    TaskQuery,
    TaskRead,
    TaskRecord,
    TaskStats,
    TaskStatus,
    TaskUpdate,
)

TODAY = date(2026, 1, 15)
NOW = datetime(2026, 1, 15, 10, 0, 0)


def make_record(
    task_id: str = "t-1",
    *,
    status: TaskStatus = TaskStatus.TODO,
    priority: TaskPriority = TaskPriority.MEDIUM,
    due_date: date | None = None,
    assignee: str | None = None,
    tags: list[str] | None = None,
) -> TaskRecord:
    """构造一个测试用实体。"""
    return TaskRecord(
        id=task_id,
        title=f"任务 {task_id}",
        description="描述",
        priority=priority,
        assignee=assignee,
        tags=tags or [],
        due_date=due_date,
        status=status,
        created_at=NOW,
        updated_at=NOW,
    )


# ---------------- 入参校验 ----------------


def test_create_uses_defaults():
    """没传的字段走默认值：优先级 medium、状态在主流程里补 TODO、tags 空列表。"""
    task = TaskCreate(title="写文档")
    assert task.priority == TaskPriority.MEDIUM
    assert task.tags == []
    assert task.description is None


@pytest.mark.parametrize("bad_title", ["", "   "[:0]])  # 空字符串
def test_create_rejects_empty_title(bad_title: str):
    with pytest.raises(ValidationError) as exc:
        TaskCreate(title=bad_title)
    assert "title" in str(exc.value)


def test_create_rejects_unknown_priority():
    with pytest.raises(ValidationError):
        TaskCreate(title="x", priority="super-urgent")  # type: ignore[arg-type]


def test_create_rejects_too_many_tags():
    with pytest.raises(ValidationError):
        TaskCreate(title="x", tags=[f"t{i}" for i in range(11)])


def test_due_date_is_parsed_from_string():
    """Pydantic 会把 "2026-03-01" 自动转成 date 对象。"""
    task = TaskCreate(title="x", due_date="2026-03-01")  # type: ignore[arg-type]
    assert task.due_date == date(2026, 3, 1)


# ---------------- PATCH 语义 ----------------


def test_update_exclude_unset_distinguishes_missing_from_null():
    """PATCH 的核心：没传的字段不出现在 dump 结果里，传了 null 才会出现。

    这是 FastAPI + Pydantic 做部分更新时最容易写错的地方。
    """
    only_title = TaskUpdate(title="新标题")
    assert only_title.model_dump(exclude_unset=True) == {"title": "新标题"}
    # 如果误用 model_dump()（不带 exclude_unset），会把没传的字段全变成 None
    assert only_title.model_dump()["description"] is None

    clear_desc = TaskUpdate(description=None)
    assert clear_desc.model_dump(exclude_unset=True) == {"description": None}


# ---------------- 出参与派生字段 ----------------


def test_task_read_marks_overdue():
    record = make_record(due_date=date(2026, 1, 10), status=TaskStatus.TODO)
    assert TaskRead.from_record(record, today=TODAY).is_overdue is True


def test_task_read_not_overdue_when_done():
    """已完成的任务即使过了截止日也不算逾期。"""
    record = make_record(due_date=date(2026, 1, 10), status=TaskStatus.DONE)
    assert TaskRead.from_record(record, today=TODAY).is_overdue is False


def test_task_read_not_overdue_without_due_date():
    assert TaskRead.from_record(make_record(), today=TODAY).is_overdue is False


# ---------------- 状态机 ----------------


def test_terminal_statuses_have_no_transitions():
    for status in TERMINAL_STATUSES:
        assert ALLOWED_TRANSITIONS[status] == frozenset()


def test_done_cannot_go_back():
    assert TaskStatus.TODO not in ALLOWED_TRANSITIONS[TaskStatus.DONE]


def test_todo_can_start_or_cancel():
    assert ALLOWED_TRANSITIONS[TaskStatus.TODO] == {
        TaskStatus.IN_PROGRESS,
        TaskStatus.CANCELLED,
    }


# ---------------- 分页与统计 ----------------


@pytest.mark.parametrize(
    "total,page_size,expected_pages",
    [(0, 20, 0), (1, 20, 1), (20, 20, 1), (21, 20, 2), (100, 20, 5)],
)
def test_page_total_pages(total: int, page_size: int, expected_pages: int):
    page = Page.of(items=list(range(total)), total=total, page=1, page_size=page_size)
    assert page.total_pages == expected_pages


def test_page_of_clamps_items():
    page: Page[str] = Page.of(["a", "b", "c"], total=10, page=2, page_size=3)
    assert page.page == 2 and page.total_pages == 4


def test_stats_counts_by_status_and_priority():
    records = [
        make_record("t-1", status=TaskStatus.DONE, priority=TaskPriority.HIGH),
        make_record("t-2", status=TaskStatus.TODO, priority=TaskPriority.HIGH),
        make_record("t-3", status=TaskStatus.TODO, priority=TaskPriority.LOW,
                    due_date=date(2026, 1, 1)),
    ]
    stats = TaskStats.from_records(records, today=TODAY)
    assert stats.total == 3
    assert stats.by_status == {"done": 1, "todo": 2}
    assert stats.by_priority == {"high": 2, "low": 1}
    assert stats.overdue == 1
    assert stats.completion_rate == pytest.approx(1 / 3, abs=1e-4)  # 保留 4 位小数


def test_stats_on_empty_list():
    stats = TaskStats.from_records([], today=TODAY)
    assert stats.total == 0
    assert stats.completion_rate == 0.0   # 不能除零


def test_api_response_ok_is_generic():
    """泛型模型：ApiResponse[TaskRead] 的 data 会按 TaskRead 校验。"""
    read = TaskRead.from_record(make_record(), today=TODAY)
    response: ApiResponse[TaskRead] = ApiResponse.ok(read, message="成功")
    assert response.code == 0
    assert response.data is not None
    assert response.data.id == "t-1"


def test_query_defaults():
    query = TaskQuery()
    assert query.page == 1 and query.page_size == 20 and query.desc is True
