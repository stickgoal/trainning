"""业务层单元测试。

注意这里没有 TestClient、没有 uvicorn —— 直接 new 一个 TaskService 调用方法。
因为 Service 只依赖 TaskRepository 和 UserServiceLike 两个抽象，
所以给它一个内存仓储 + 一个假用户服务就能测，速度是集成测试的几十倍。

对应 Java：@ExtendWith(MockitoExtension.class) 的纯单元测试。
"""
import itertools
from datetime import date

import pytest

from taskflow.core.exceptions import (
    ConflictException,
    NotFoundException,
    ValidationException,
)
from taskflow.models.task import (
    TaskCreate,
    TaskPriority,
    TaskQuery,
    TaskStatus,
    TaskUpdate,
)
from taskflow.repositories.task_repo import InMemoryTaskRepository
from taskflow.services.task_service import TaskService
from tests.conftest import FIXED_NOW, FakeUserService


def build_service(
    repository: InMemoryTaskRepository,
    user_service: FakeUserService | None = None,
) -> TaskService:
    """构造一个"确定性"的 TaskService：时间固定、ID 可预测。

    这就是构造函数注入 now / id_factory 的意义 ——
    否则测试里到处是 freezegun 和正则匹配 uuid。
    """
    counter = itertools.count(1)
    return TaskService(
        repository,
        user_service,
        now=lambda: FIXED_NOW,
        id_factory=lambda: f"task-{next(counter):03d}",
    )


# ---------------- 创建 ----------------


async def test_create_task_sets_defaults():
    service = build_service(InMemoryTaskRepository())
    task = await service.create_task(TaskCreate(title="修复登录 500"))

    assert task.id == "task-001"
    assert task.status == TaskStatus.TODO
    assert task.priority == TaskPriority.MEDIUM
    assert task.created_at == FIXED_NOW
    assert task.is_overdue is False


async def test_create_task_validates_assignee():
    service = build_service(InMemoryTaskRepository(), FakeUserService({"alice"}))

    ok = await service.create_task(TaskCreate(title="x", assignee="alice"))
    assert ok.assignee == "alice"

    with pytest.raises(ValidationException) as exc:
        await service.create_task(TaskCreate(title="x", assignee="nobody"))
    assert "负责人不存在" in exc.value.message


async def test_create_task_skips_validation_without_user_service():
    """没注入用户服务 = 不校验负责人（离线/演示场景）。"""
    service = build_service(InMemoryTaskRepository())
    task = await service.create_task(TaskCreate(title="x", assignee="anyone"))
    assert task.assignee == "anyone"


# ---------------- 查询 ----------------


async def test_get_task_not_found():
    service = build_service(InMemoryTaskRepository())
    with pytest.raises(NotFoundException) as exc:
        await service.get_task("not-exist")
    assert exc.value.status_code == 404


async def test_list_tasks_pagination(seeded_repository: InMemoryTaskRepository):
    service = build_service(seeded_repository)

    page1 = await service.list_tasks(TaskQuery(page=1, page_size=2))
    assert page1.total == 5
    assert page1.total_pages == 3
    assert len(page1.items) == 2

    page3 = await service.list_tasks(TaskQuery(page=3, page_size=2))
    assert len(page3.items) == 1


async def test_list_tasks_filters(seeded_repository: InMemoryTaskRepository):
    service = build_service(seeded_repository)

    done = await service.list_tasks(TaskQuery(status=TaskStatus.DONE))
    assert done.total == 1 and done.items[0].id == "task-003"

    urgent = await service.list_tasks(TaskQuery(priority=TaskPriority.URGENT))
    assert urgent.total == 1

    by_assignee = await service.list_tasks(TaskQuery(assignee="alice"))
    assert by_assignee.total == 2

    by_tag = await service.list_tasks(TaskQuery(tag="test"))
    assert by_tag.total == 1

    by_keyword = await service.list_tasks(TaskQuery(keyword="单元测试"))
    assert by_keyword.total == 1

    overdue = await service.list_tasks(TaskQuery(only_overdue=True))
    # 相对 FIXED_NOW，只有 task-001 逾期（task-003 虽过期但已完成）
    assert overdue.total == 1 and overdue.items[0].id == "task-001"


# ---------------- 更新 ----------------


async def test_update_only_touched_fields():
    repository = InMemoryTaskRepository()
    service = build_service(repository, FakeUserService())
    created = await service.create_task(
        TaskCreate(title="原标题", description="原描述", assignee="alice", priority=TaskPriority.HIGH)
    )

    updated = await service.update_task(created.id, TaskUpdate(title="新标题"))
    # 只改了标题，其它字段原样保留（这就是 exclude_unset 的价值）
    assert updated.title == "新标题"
    assert updated.description == "原描述"
    assert updated.assignee == "alice"
    assert updated.priority == TaskPriority.HIGH


async def test_update_accepts_null_to_clear_field():
    repository = InMemoryTaskRepository()
    service = build_service(repository, FakeUserService())
    created = await service.create_task(TaskCreate(title="x", description="待删除", tags=["a", "b"]))

    updated = await service.update_task(
        created.id, TaskUpdate(description=None, tags=None)
    )
    assert updated.description is None
    assert updated.tags == []


async def test_update_rejects_empty_body():
    service = build_service(InMemoryTaskRepository())
    created = await service.create_task(TaskCreate(title="x"))

    with pytest.raises(ValidationException) as exc:
        await service.update_task(created.id, TaskUpdate())
    assert "没有可更新的字段" in exc.value.message


async def test_update_rejects_invalid_null_value():
    """priority 是必填枚举，传 null 属于"入参非法"，应报 422 而不是 500。"""
    service = build_service(InMemoryTaskRepository())
    created = await service.create_task(TaskCreate(title="x"))

    with pytest.raises(ValidationException):
        await service.update_task(created.id, TaskUpdate(priority=None))


async def test_update_unknown_task():
    service = build_service(InMemoryTaskRepository())
    with pytest.raises(NotFoundException):
        await service.update_task("nope", TaskUpdate(title="x"))


# ---------------- 状态流转 ----------------


async def test_transition_todo_to_in_progress():
    service = build_service(InMemoryTaskRepository())
    created = await service.create_task(TaskCreate(title="x"))

    moved = await service.transition_status(created.id, TaskStatus.IN_PROGRESS)
    assert moved.status == TaskStatus.IN_PROGRESS
    assert moved.completed_at is None


async def test_transition_to_done_records_completed_at():
    service = build_service(InMemoryTaskRepository())
    created = await service.create_task(TaskCreate(title="x"))
    await service.transition_status(created.id, TaskStatus.IN_PROGRESS)

    done = await service.transition_status(created.id, TaskStatus.DONE)
    assert done.completed_at == FIXED_NOW


async def test_transition_rejects_illegal_move():
    service = build_service(InMemoryTaskRepository())
    created = await service.create_task(TaskCreate(title="x"))

    # TODO 不能直接跳到 DONE
    with pytest.raises(ConflictException) as exc:
        await service.transition_status(created.id, TaskStatus.DONE)
    assert exc.value.status_code == 409
    assert exc.value.details["allowed"] == ["cancelled", "in_progress"]


async def test_transition_from_terminal_state_always_fails():
    service = build_service(InMemoryTaskRepository())
    created = await service.create_task(TaskCreate(title="x"))
    await service.transition_status(created.id, TaskStatus.CANCELLED)

    with pytest.raises(ConflictException):
        await service.transition_status(created.id, TaskStatus.TODO)


# ---------------- 删除与统计 ----------------


async def test_delete_task():
    repository = InMemoryTaskRepository()
    service = build_service(repository)
    created = await service.create_task(TaskCreate(title="x"))

    await service.delete_task(created.id)
    assert await repository.get(created.id) is None


async def test_delete_missing_task_raises_404():
    service = build_service(InMemoryTaskRepository())
    with pytest.raises(NotFoundException):
        await service.delete_task("nope")


async def test_stats(seeded_repository: InMemoryTaskRepository):
    service = build_service(seeded_repository)
    stats = await service.get_stats()

    assert stats.total == 5
    assert stats.by_status["done"] == 1
    assert stats.overdue == 1
    assert stats.completion_rate == pytest.approx(0.2)


# ---------------- CSV 导入 ----------------


async def test_import_from_csv():
    service = build_service(InMemoryTaskRepository(), FakeUserService())
    csv_text = (
        "title,description,priority,assignee,tags,due_date\n"
        "任务A,描述A,high,alice,backend;bug,2026-03-01\n"
        "任务B,,low,bob,,2026-04-01\n"
    )
    result = await service.import_from_csv(csv_text, max_rows=10)

    assert result.created == 2
    assert result.skipped == 0
    assert result.errors == []

    tasks = await service.list_tasks(TaskQuery())
    assert tasks.total == 2
    by_title = {t.title: t for t in tasks.items}
    assert by_title["任务A"].priority == TaskPriority.HIGH
    assert by_title["任务A"].tags == ["backend", "bug"]
    assert by_title["任务A"].due_date == date(2026, 3, 1)


async def test_import_skips_bad_rows():
    service = build_service(InMemoryTaskRepository(), FakeUserService())
    csv_text = (
        "title,description,priority,assignee,tags,due_date\n"
        ",没有标题,high,alice,,\n"                    # 标题为空 → 跳过
        "任务B,,super-urgent,bob,,\n"                # 优先级非法 → 跳过
        "任务C,,low,bob,,2026/04/01\n"               # 日期格式错 → 跳过
        "任务D,,medium,alice,,\n"                    # 正常
    )
    result = await service.import_from_csv(csv_text, max_rows=10)

    assert result.created == 1
    assert result.skipped == 3
    assert len(result.errors) == 3
    assert all("第" in err for err in result.errors)


async def test_import_respects_row_limit():
    service = build_service(InMemoryTaskRepository(), FakeUserService())
    rows = ["title,priority", "A,high", "B,low", "C,medium"]
    result = await service.import_from_csv("\n".join(rows), max_rows=2)

    assert result.created == 2
    assert any("超出单次导入上限" in err for err in result.errors)


# ---------------- 流式迭代 ----------------


async def test_iter_all_yields_every_record(seeded_repository: InMemoryTaskRepository):
    service = build_service(seeded_repository)
    ids = [record.id async for record in service.iter_all()]
    assert sorted(ids) == ["task-001", "task-002", "task-003", "task-004", "task-005"]
