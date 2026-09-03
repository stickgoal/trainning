"""任务领域模型。

三层模型各司其职（这是 Pydantic 项目里最重要的约定）：

    TaskCreate  → 入参模型（客户端传什么）
    TaskRecord  → 存储实体（内部长什么样，含 id / 时间戳 / 状态）
    TaskRead    → 出参模型（服务端愿意暴露什么）

对应 Java：DTO（入参） / Entity（实体） / VO（出参）。
"""
from collections import Counter
from dataclasses import dataclass, field
from datetime import date, datetime
from enum import Enum

from pydantic import BaseModel, ConfigDict, Field


class TaskStatus(str, Enum):
    """任务状态。str 枚举：既能当枚举用，又能直接 JSON 序列化。"""

    TODO = "todo"
    IN_PROGRESS = "in_progress"
    DONE = "done"
    CANCELLED = "cancelled"


class TaskPriority(str, Enum):
    """任务优先级。"""

    LOW = "low"
    MEDIUM = "medium"
    HIGH = "high"
    URGENT = "urgent"


# 终态：进入后不能再流转
TERMINAL_STATUSES = frozenset({TaskStatus.DONE, TaskStatus.CANCELLED})

# 状态机：key 是当前状态，value 是允许流转到的状态
ALLOWED_TRANSITIONS: dict[TaskStatus, frozenset[TaskStatus]] = {
    TaskStatus.TODO: frozenset({TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED}),
    TaskStatus.IN_PROGRESS: frozenset({TaskStatus.TODO, TaskStatus.DONE, TaskStatus.CANCELLED}),
    TaskStatus.DONE: frozenset(),          # 已完成：终态
    TaskStatus.CANCELLED: frozenset(),     # 已取消：终态
}


class TaskBase(BaseModel):
    """任务公共字段。"""

    title: str = Field(
        ...,
        min_length=1,
        max_length=200,
        description="任务标题",
        examples=["修复登录页 500 错误"],
    )
    description: str | None = Field(default=None, max_length=2000, description="任务描述")
    priority: TaskPriority = Field(default=TaskPriority.MEDIUM, description="优先级")
    assignee: str | None = Field(default=None, max_length=50, description="负责人用户名")
    tags: list[str] = Field(default_factory=list, max_length=10, description="标签")
    due_date: date | None = Field(default=None, description="截止日期 YYYY-MM-DD")


class TaskCreate(TaskBase):
    """创建任务的入参模型。

    继承 TaskBase：如果将来"创建"比"公共字段"多一个字段（如 project_id），
    在这里加即可，不影响其它模型。
    """


class TaskUpdate(BaseModel):
    """更新任务的入参模型（PATCH 语义：给什么改什么）。

    所有字段都是 Optional —— 但"没传"和"传了 null"必须区分开，
    靠的是 model_dump(exclude_unset=True)（见 services/task_service.py）。
    """

    title: str | None = Field(default=None, min_length=1, max_length=200)
    description: str | None = Field(default=None, max_length=2000)
    priority: TaskPriority | None = None
    assignee: str | None = Field(default=None, max_length=50)
    tags: list[str] | None = Field(default=None, max_length=10)
    due_date: date | None = None


class TaskRecord(TaskBase):
    """存储层实体：内部使用，不直接返回给客户端。"""

    model_config = ConfigDict(validate_assignment=True)  # 赋值时也做校验

    id: str = Field(description="任务ID")
    status: TaskStatus = Field(default=TaskStatus.TODO, description="状态")
    created_at: datetime = Field(description="创建时间")
    updated_at: datetime = Field(description="更新时间")
    completed_at: datetime | None = Field(default=None, description="完成时间")


class TaskRead(TaskBase):
    """出参模型：只暴露客户端需要的字段，并补充派生字段。"""

    id: str
    status: TaskStatus
    created_at: datetime
    updated_at: datetime
    completed_at: datetime | None = None
    is_overdue: bool = Field(default=False, description="是否已逾期（未完成且过了截止日）")

    @classmethod
    def from_record(cls, record: TaskRecord, *, today: date | None = None) -> "TaskRead":
        """实体 → 出参模型的转换。

        from_record 用 model_validate(record) 而不是手写字段拷贝，
        这样给 TaskRecord 加字段时，只要 TaskRead 也有同名字段就自动带上。
        """
        today = today or datetime.now().date()
        overdue = (
            record.due_date is not None
            and record.due_date < today
            and record.status not in TERMINAL_STATUSES
        )
        return cls(
            **{
                **record.model_dump(exclude={"completed_at"}),
                "completed_at": record.completed_at,
                "is_overdue": overdue,
            }
        )


class TaskTransitionRequest(BaseModel):
    """状态流转请求体。

    为什么状态变更要单独开一个接口，而不是塞进 PATCH /{task_id}？
    因为状态是"受控字段"：它有状态机约束、要留痕、可能触发通知。
    把"改标题"和"标记完成"混在一个 PATCH 里，业务规则会越来越难维护。
    """

    target: TaskStatus = Field(..., description="目标状态", examples=["in_progress"])
    comment: str | None = Field(default=None, max_length=200, description="流转备注")


class TaskStats(BaseModel):
    """任务统计。"""

    total: int = 0
    by_status: dict[str, int] = Field(default_factory=dict)
    by_priority: dict[str, int] = Field(default_factory=dict)
    overdue: int = 0
    completion_rate: float = Field(default=0.0, description="完成率 0~1")

    @classmethod
    def from_records(cls, records: list[TaskRecord], *, today: date | None = None) -> "TaskStats":
        today = today or datetime.now().date()
        by_status = Counter(r.status.value for r in records)
        by_priority = Counter(r.priority.value for r in records)
        done = by_status.get(TaskStatus.DONE.value, 0)
        return cls(
            total=len(records),
            by_status=dict(by_status),
            by_priority=dict(by_priority),
            overdue=sum(
                1
                for r in records
                if r.due_date and r.due_date < today and r.status not in TERMINAL_STATUSES
            ),
            completion_rate=round(done / len(records), 4) if records else 0.0,
        )


class TaskImportResult(BaseModel):
    """批量导入结果。"""

    created: int = Field(default=0, description="成功创建的条数")
    skipped: int = Field(default=0, description="跳过的条数")
    errors: list[str] = Field(default_factory=list, description="每行的问题（行号: 原因）")
    task_ids: list[str] = Field(default_factory=list, description="新建任务的 id")


@dataclass(slots=True)
class TaskQuery:
    """查询条件对象（不是 HTTP 入参模型，而是传给仓储层的查询对象）。

    用 dataclass 而不是 Pydantic 模型：它只在内部流转，不需要校验和序列化。
    """

    status: TaskStatus | None = None
    priority: TaskPriority | None = None
    assignee: str | None = None
    tag: str | None = None
    keyword: str | None = None
    page: int = 1
    page_size: int = 20
    sort_by: str = "created_at"
    desc: bool = True
    only_overdue: bool = False
    # "逾期"是一个相对概念，必须由调用方给定"今天"是哪天，
    # 仓储层自己调 datetime.now() 会让测试不可复现。
    overdue_before: date | None = None
    extra: dict[str, object] = field(default_factory=dict)
