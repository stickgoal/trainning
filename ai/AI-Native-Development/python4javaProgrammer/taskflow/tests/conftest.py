"""pytest 公共夹具（fixtures）。

对应 Java：@SpringBootTest + @MockBean。

四个核心夹具：
- repository        每个用例一个全新的空仓储 → 用例之间零耦合
- user_service      假的用户服务 → 测试永不联网
- client            测试客户端（空数据）
- seeded_repository 预置 5 条数据的仓储（供 Service 单测用）
- seeded_client     预置 5 条数据的测试客户端（供 API 集成测试用）

注意：填充数据用的是普通函数 seed_tasks()，不是 fixture 套 fixture。
fixture 之间互相依赖层级一多，很容易出现"拿到的是 fixture 定义对象而不是值"的坑。
"""
import asyncio
import itertools
from collections.abc import Iterator
from datetime import datetime, timedelta

import pytest
from fastapi.testclient import TestClient

from taskflow.api.deps import get_repository, get_task_service, get_user_service
from taskflow.main import create_app
from taskflow.models.task import TaskPriority, TaskRecord, TaskStatus
from taskflow.repositories.task_repo import InMemoryTaskRepository
from taskflow.services.task_service import TaskService

# 固定"当前时间"，让涉及日期的断言可复现（例如逾期判断）
FIXED_NOW = datetime(2026, 1, 15, 10, 0, 0)


class FakeUserService:
    """用户服务的测试替身：不联网，只认一份固定名单。

    对应 Java：@MockBean UserService。
    """

    def __init__(self, known: set[str] | None = None, *, enabled: bool = True) -> None:
        self.known = known or {"alice", "bob", "carol"}
        self.enabled = enabled
        self.calls: list[str] = []

    async def exists(self, username: str) -> bool:
        self.calls.append(username)
        if not self.enabled:
            return True
        return username in self.known


# ---------------------------------------------------------------------------
# 数据填充（普通函数，不是 fixture）
# ---------------------------------------------------------------------------

# 相对 FIXED_NOW(2026-01-15)：
#   task-001 逾期（截止 2026-01-10）且未完成
#   task-002 进行中
#   task-003 已完成（有 completed_at）
#   task-004 已取消
#   task-005 待办，无截止日期
SEED_ROWS = [
    ("task-001", "修复登录 500", TaskPriority.URGENT, TaskStatus.TODO,
     "alice", ["backend"], FIXED_NOW.date() - timedelta(days=5), 5),
    ("task-002", "补充单元测试", TaskPriority.HIGH, TaskStatus.IN_PROGRESS,
     "bob", ["test"], FIXED_NOW.date() + timedelta(days=10), 4),
    ("task-003", "升级依赖", TaskPriority.MEDIUM, TaskStatus.DONE,
     "alice", ["ops"], FIXED_NOW.date() + timedelta(days=10), 3),
    ("task-004", "下线旧接口", TaskPriority.LOW, TaskStatus.CANCELLED,
     None, [], None, 2),
    ("task-005", "写技术周报", TaskPriority.MEDIUM, TaskStatus.TODO,
     "carol", ["docs"], None, 1),
]


def seed_tasks(repository: InMemoryTaskRepository) -> InMemoryTaskRepository:
    """往仓储里塞 5 条固定数据。

    仓储的 add 是 async 方法，这里在同步上下文里用 asyncio.run 跑一次。
    """
    async def _fill() -> None:
        for task_id, title, priority, status, assignee, tags, due_date, hours_ago in SEED_ROWS:
            created = FIXED_NOW - timedelta(hours=hours_ago)
            await repository.add(
                TaskRecord(
                    id=task_id,
                    title=title,
                    description=None,
                    priority=priority,
                    assignee=assignee,
                    tags=tags,
                    due_date=due_date,
                    status=status,
                    created_at=created,
                    updated_at=created,
                    completed_at=FIXED_NOW if status == TaskStatus.DONE else None,
                )
            )

    asyncio.run(_fill())
    return repository


def build_test_client(
    repository: InMemoryTaskRepository, user_service: FakeUserService
) -> Iterator[TestClient]:
    """创建测试客户端，并把真实依赖替换成测试替身。

    dependency_overrides 是 FastAPI 官方提供的"依赖替换"机制：
    不改一行业务代码，就能把仓储换成内存版、把外部服务换成假实现。

    这里额外替换了 get_task_service —— 目的有两个：
    1. 把"当前时间"固定成 FIXED_NOW，否则"SSE/逾期"这类断言会随运行日期漂移；
    2. 让新建任务的 ID 可预测，便于断言。
    这也说明"把时间和 ID 生成器做成构造参数"是有回报的。
    """
    counter = itertools.count(1)

    def fake_task_service() -> TaskService:
        return TaskService(
            repository,
            user_service,
            now=lambda: FIXED_NOW,
            id_factory=lambda: f"gen-{next(counter):03d}",
        )

    app = create_app()
    app.dependency_overrides[get_repository] = lambda: repository
    app.dependency_overrides[get_user_service] = lambda: user_service
    app.dependency_overrides[get_task_service] = fake_task_service

    # with 语句会触发 lifespan（启动 → yield → 关闭），和真实启动流程一致
    with TestClient(app) as test_client:
        yield test_client

    app.dependency_overrides.clear()


# ---------------------------------------------------------------------------
# 夹具
# ---------------------------------------------------------------------------


@pytest.fixture
def fixed_now() -> datetime:
    return FIXED_NOW


@pytest.fixture
def repository() -> InMemoryTaskRepository:
    """空仓储。每个用例独立，避免"上一个用例的数据影响下一个"。"""
    return InMemoryTaskRepository()


@pytest.fixture
def user_service() -> FakeUserService:
    return FakeUserService()


@pytest.fixture
def seeded_repository(repository: InMemoryTaskRepository) -> InMemoryTaskRepository:
    """预置了 5 条数据的仓储。"""
    return seed_tasks(repository)


@pytest.fixture
def client(
    repository: InMemoryTaskRepository, user_service: FakeUserService
) -> Iterator[TestClient]:
    """空数据的 HTTP 测试客户端。"""
    yield from build_test_client(repository, user_service)


@pytest.fixture
def seeded_client(
    repository: InMemoryTaskRepository, user_service: FakeUserService
) -> Iterator[TestClient]:
    """预置 5 条数据的 HTTP 测试客户端。

    和 client 夹具共用同一个 repository 实例，只是先用 seed_tasks 填了数据。
    """
    seed_tasks(repository)
    yield from build_test_client(repository, user_service)
