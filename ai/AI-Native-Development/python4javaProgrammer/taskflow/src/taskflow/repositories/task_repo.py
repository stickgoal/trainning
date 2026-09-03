"""任务仓储：接口 + 内存实现。

分层意义：Service 只依赖 TaskRepository 这个"接口"，不关心底下是内存、
SQLite 还是 PostgreSQL。换存储 = 换一个实现类，Service 一行不用改。

对应 Java：interface TaskRepository + @Repository 实现类。
"""
import threading
from typing import Protocol

from taskflow.models.task import TaskPriority, TaskQuery, TaskRecord

# 优先级排序权重。
# 注意这里是"越低越不重要"：默认倒序（desc=True）时 URGENT 排在最前面，
# 符合"先处理最紧急的"这个直觉。
_PRIORITY_ORDER = {
    TaskPriority.LOW: 0,
    TaskPriority.MEDIUM: 1,
    TaskPriority.HIGH: 2,
    TaskPriority.URGENT: 3,
}


class TaskRepository(Protocol):
    """仓储接口。

    用 Protocol 而不是 ABC：
    实现类不需要显式继承它，只要"长得像"就算实现了（结构化子类型）。
    对应 Java 的 interface —— 但鸭子类型，耦合更低。
    """

    async def add(self, task: TaskRecord) -> TaskRecord:
        """新增任务；id 已存在时抛出 ValueError。"""
        ...

    async def get(self, task_id: str) -> TaskRecord | None:
        """按 id 查询，不存在返回 None（不要抛异常，让 Service 决定语义）。"""
        ...

    async def update(self, task: TaskRecord) -> TaskRecord:
        """整体替换（调用方先 get 再改再 update）。"""
        ...

    async def delete(self, task_id: str) -> bool:
        """删除，返回是否真的删掉了。"""
        ...

    async def search(self, query: TaskQuery) -> tuple[list[TaskRecord], int]:
        """条件查询 + 分页，返回 (当前页数据, 符合条件的总数)。"""
        ...

    async def list_all(self) -> list[TaskRecord]:
        """返回全部（仅用于统计和导出，数据量小的时候）。"""
        ...


class InMemoryTaskRepository:
    """内存实现：用一个 dict 存数据。

    注意点（教程正文会展开）：
    1. 方法都是 async，但内部没有 await —— 这是"为了接口统一"留下的异步外壳，
       将来换成数据库实现时，调用方代码不用改。
    2. 用了 threading.Lock：FastAPI 会把同步 def 的路由丢进线程池并发执行，
       多条请求同时改 dict 仍有竞争风险。
    """

    def __init__(self) -> None:
        self._tasks: dict[str, TaskRecord] = {}
        self._lock = threading.Lock()

    # ---------------- 写操作 ----------------

    async def add(self, task: TaskRecord) -> TaskRecord:
        with self._lock:
            if task.id in self._tasks:
                raise ValueError(f"任务已存在: {task.id}")
            self._tasks[task.id] = task
            return task

    async def update(self, task: TaskRecord) -> TaskRecord:
        with self._lock:
            if task.id not in self._tasks:
                raise KeyError(task.id)
            self._tasks[task.id] = task
            return task

    async def delete(self, task_id: str) -> bool:
        with self._lock:
            return self._tasks.pop(task_id, None) is not None

    # ---------------- 读操作 ----------------

    async def get(self, task_id: str) -> TaskRecord | None:
        return self._tasks.get(task_id)

    async def list_all(self) -> list[TaskRecord]:
        return list(self._tasks.values())

    async def search(self, query: TaskQuery) -> tuple[list[TaskRecord], int]:
        """过滤 → 排序 → 分页。

        真实项目里这三步应该下推到数据库（WHERE / ORDER BY / LIMIT OFFSET）；
        内存实现只是把逻辑照抄一遍，方便本地跑通。
        """
        records = [r for r in self._tasks.values() if self._matches(r, query)]

        # 排序：None 值统一排到最后，避免 datetime 和 None 比较报错
        records.sort(
            key=lambda r: self._sort_key(r, query.sort_by),
            reverse=query.desc,
        )

        total = len(records)
        start = (query.page - 1) * query.page_size
        end = start + query.page_size
        return records[start:end], total

    # ---------------- 内部辅助 ----------------

    @staticmethod
    def _matches(record: TaskRecord, query: TaskQuery) -> bool:
        if query.status is not None and record.status != query.status:
            return False
        if query.priority is not None and record.priority != query.priority:
            return False
        if query.assignee is not None and record.assignee != query.assignee:
            return False
        if query.tag is not None and query.tag not in record.tags:
            return False
        if query.keyword is not None:
            keyword = query.keyword.lower()
            haystack = f"{record.title} {record.description or ''}".lower()
            if keyword not in haystack:
                return False
        if query.overdue_before is not None:
            if record.due_date is None:
                return False
            if record.due_date >= query.overdue_before:
                return False
        return True

    @staticmethod
    def _sort_key(record: TaskRecord, sort_by: str) -> tuple[int, object]:
        """返回 (是否为None, 值) 的元组：None 永远排在有值后面。"""
        if sort_by == "priority":
            return (0, _PRIORITY_ORDER[record.priority])
        raw = getattr(record, sort_by, None)
        if raw is None:
            return (1, "")
        return (0, raw)
