"""任务业务逻辑层。

这一层是整个项目"最值钱"的部分：它不依赖 FastAPI、不依赖 HTTP，
只依赖仓储接口和外部服务接口 —— 所以它可以被单独单元测试，
也可以在 CLI、定时任务里复用。

对应 Java：@Service 标注的业务类。
"""
import csv
import io
import uuid
from collections.abc import AsyncIterator, Callable
from dataclasses import replace
from datetime import date, datetime
from typing import Protocol

from loguru import logger
from pydantic import ValidationError

from taskflow.core.exceptions import (
    ConflictException,
    NotFoundException,
    ValidationException,
)
from taskflow.models.base import Page
from taskflow.models.task import (
    ALLOWED_TRANSITIONS,
    TERMINAL_STATUSES,
    TaskCreate,
    TaskImportResult,
    TaskPriority,
    TaskQuery,
    TaskRead,
    TaskRecord,
    TaskStats,
    TaskStatus,
    TaskUpdate,
)
from taskflow.repositories.task_repo import TaskRepository


class UserServiceLike(Protocol):
    """只声明"我需要什么"，不关心具体实现（测试时换成假对象很容易）。"""

    async def exists(self, username: str) -> bool: ...


class TaskService:
    """任务业务服务。

    构造函数里注入的三样东西，各自都有教学意义：
    - repository：存储抽象，测试和换库时替换；
    - user_service：外部依赖，可传 None 表示"不校验"；
    - now / id_factory：时间和 ID 生成器，注入进来是为了让测试可复现。
    """

    def __init__(
        self,
        repository: TaskRepository,
        user_service: UserServiceLike | None = None,
        *,
        now: Callable[[], datetime] = datetime.now,
        id_factory: Callable[[], str] = lambda: uuid.uuid4().hex[:12],
    ) -> None:
        self._repo = repository
        self._user_service = user_service
        self._now = now
        self._id_factory = id_factory

    # ---------------- 查询 ----------------

    async def get_task(self, task_id: str) -> TaskRead:
        """查单个任务，不存在抛 404（由全局 handler 转成 JSON 响应）。"""
        record = await self._repo.get(task_id)
        if record is None:
            raise NotFoundException("任务", task_id)
        return TaskRead.from_record(record, today=self._now().date())

    async def list_tasks(self, query: TaskQuery) -> Page[TaskRead]:
        """分页查询。"""
        if query.only_overdue:
            # 把"只看逾期"翻译成"截止日期早于今天"。
            # 基准时间来自注入的 self._now()，所以测试里可以固定成某一天。
            query = replace(query, overdue_before=self._now().date())

        records, total = await self._repo.search(query)
        today = self._now().date()
        items = [TaskRead.from_record(r, today=today) for r in records]
        return Page.of(items, total, query.page, query.page_size)

    async def get_stats(self) -> TaskStats:
        """统计概览。"""
        return TaskStats.from_records(await self._repo.list_all(), today=self._now().date())

    async def iter_all(self) -> AsyncIterator[TaskRecord]:
        """异步迭代全部任务（供流式导出使用，不一次性占内存）。"""
        for record in await self._repo.list_all():
            yield record

    # ---------------- 写操作 ----------------

    async def create_task(self, payload: TaskCreate) -> TaskRead:
        """创建任务。"""
        await self._ensure_assignee_valid(payload.assignee)

        now = self._now()
        record = TaskRecord(
            id=self._id_factory(),
            status=TaskStatus.TODO,
            created_at=now,
            updated_at=now,
            **payload.model_dump(),
        )
        await self._repo.add(record)
        logger.info(f"创建任务 {record.id}: {record.title}")
        return TaskRead.from_record(record, today=now.date())

    async def update_task(self, task_id: str, payload: TaskUpdate) -> TaskRead:
        """部分更新（PATCH 语义）。

        关键点：`exclude_unset=True` 只导出"客户端实际传了"的字段。
        如果直接 model_dump()，没传的字段会变成 None 覆盖掉原值 ——
        这是 Pydantic + PATCH 最常见的坑。
        """
        record = await self._require_task(task_id)

        changes = payload.model_dump(exclude_unset=True)
        if not changes:
            raise ValidationException("请求体中没有可更新的字段")

        if "assignee" in changes and changes["assignee"] != record.assignee:
            await self._ensure_assignee_valid(changes["assignee"])

        # 约定：tags 传 null 表示"清空标签"，而不是"赋值为 null"
        if "tags" in changes and changes["tags"] is None:
            changes["tags"] = []

        try:
            for field_name, value in changes.items():
                # TaskRecord 开了 validate_assignment，赋值即校验
                setattr(record, field_name, value)
        except ValidationError as exc:
            # 例如客户端传 priority: null —— 这是"入参非法"，应该报 422 而不是 500
            raise ValidationException(
                "字段值非法", {"errors": exc.errors()}
            ) from exc

        record.updated_at = self._now()
        await self._repo.update(record)
        return TaskRead.from_record(record, today=record.updated_at.date())

    async def transition_status(self, task_id: str, target: TaskStatus) -> TaskRead:
        """状态流转：按状态机校验，非法流转返回 409。"""
        record = await self._require_task(task_id)
        allowed = ALLOWED_TRANSITIONS.get(record.status, frozenset())

        if target not in allowed:
            raise ConflictException(
                f"不允许的状态流转: {record.status.value} → {target.value}",
                {
                    "current": record.status.value,
                    "target": target.value,
                    "allowed": sorted(s.value for s in allowed),
                },
            )

        now = self._now()
        record.status = target
        record.updated_at = now
        record.completed_at = now if target == TaskStatus.DONE else None
        await self._repo.update(record)
        logger.info(f"任务 {task_id} 状态变更为 {target.value}")
        return TaskRead.from_record(record, today=now.date())

    async def delete_task(self, task_id: str) -> None:
        """删除任务，不存在抛 404。"""
        if not await self._repo.delete(task_id):
            raise NotFoundException("任务", task_id)
        logger.info(f"删除任务 {task_id}")

    # ---------------- 批量导入 ----------------

    async def import_from_csv(self, content: str, *, max_rows: int = 1000) -> TaskImportResult:
        """从 CSV 文本批量创建任务。

        CSV 列（首行是表头）：title,description,priority,assignee,tags,due_date
        tags 用分号分隔，例如 "backend;bug"。
        """
        result = TaskImportResult()
        reader = csv.DictReader(io.StringIO(content))

        for line_no, row in enumerate(reader, start=2):  # 第 1 行是表头
            if line_no - 1 > max_rows:
                result.errors.append(f"第{line_no}行: 超出单次导入上限 {max_rows}")
                break

            title = (row.get("title") or "").strip()
            if not title:
                result.skipped += 1
                result.errors.append(f"第{line_no}行: 标题为空")
                continue

            try:
                priority = TaskPriority((row.get("priority") or "medium").strip().lower())
            except ValueError:
                result.skipped += 1
                result.errors.append(f"第{line_no}行: 非法优先级 {row.get('priority')!r}")
                continue

            due_date: date | None = None
            raw_due = (row.get("due_date") or "").strip()
            if raw_due:
                try:
                    due_date = datetime.strptime(raw_due, "%Y-%m-%d").date()
                except ValueError:
                    result.skipped += 1
                    result.errors.append(f"第{line_no}行: 日期格式应为 YYYY-MM-DD，实际 {raw_due!r}")
                    continue

            raw_tags = (row.get("tags") or "").strip()
            tags = [t.strip() for t in raw_tags.replace(",", ";").split(";") if t.strip()]

            record = TaskRecord(
                id=self._id_factory(),
                title=title,
                description=(row.get("description") or "").strip() or None,
                priority=priority,
                assignee=(row.get("assignee") or "").strip() or None,
                tags=tags,
                due_date=due_date,
                created_at=self._now(),
                updated_at=self._now(),
            )
            await self._repo.add(record)
            result.created += 1
            result.task_ids.append(record.id)

        logger.info(f"CSV 导入完成: 成功 {result.created} 条，跳过 {result.skipped} 条")
        return result

    # ---------------- 内部辅助 ----------------

    async def _require_task(self, task_id: str) -> TaskRecord:
        record = await self._repo.get(task_id)
        if record is None:
            raise NotFoundException("任务", task_id)
        return record

    async def _ensure_assignee_valid(self, assignee: str | None) -> None:
        """校验负责人。已完成/已取消的任务不重复校验（避免历史数据被外部变动影响）。"""
        if not assignee or self._user_service is None:
            return
        if not await self._user_service.exists(assignee):
            raise ValidationException(
                f"负责人不存在: {assignee}",
                {"assignee": assignee},
            )


__all__ = ["TaskService", "UserServiceLike", "TERMINAL_STATUSES"]
