"""依赖注入（DI）。

对应 Java：@Autowired / @Bean / 构造器注入。

FastAPI 的 DI 有三个特点值得 Java 程序员注意：
1. 它是**函数级**的：路由函数"声明参数"就等于"声明依赖"，没有容器、没有配置文件；
2. 依赖可以层层嵌套（子依赖），框架自动按依赖图逐个解析；
3. 同一个请求内，同一个依赖默认只解析一次（有缓存）；跨请求不共享。
"""
from collections.abc import AsyncIterator
from dataclasses import dataclass
from datetime import datetime
from typing import Annotated

from fastapi import Depends, Request
from loguru import logger

from taskflow.core.settings import Settings, get_settings
from taskflow.repositories.task_repo import TaskRepository
from taskflow.services.task_service import TaskService
from taskflow.services.user_service import UserService
from taskflow.utils.http_client import HttpClient


# ---------------------------------------------------------------------------
# 一、基础依赖：从 app.state 里取"应用级单例"
#     这些对象在 lifespan 里创建（见 main.py），活在整个进程生命周期。
# ---------------------------------------------------------------------------


def get_repository(request: Request) -> TaskRepository:
    """取仓储实例。测试时通过 app.dependency_overrides 换成假实现。"""
    return request.app.state.repository


def get_user_service(request: Request) -> UserService:
    """取外部用户服务客户端。"""
    return request.app.state.user_service


def get_http_client(request: Request) -> HttpClient:
    """取 HTTP 客户端（连接池共享，不要每个请求新建）。"""
    return request.app.state.http_client


# ---------------------------------------------------------------------------
# 二、组合依赖：依赖别的依赖
# ---------------------------------------------------------------------------


def get_task_service(
    repository: Annotated[TaskRepository, Depends(get_repository)],
    user_service: Annotated[UserService, Depends(get_user_service)],
) -> TaskService:
    """构造业务服务。

    TaskService 本身无状态（状态都在 repository 里），
    所以每次请求新建一个即可，创建成本约等于 new 一个普通对象。
    """
    return TaskService(repository, user_service)


# ---------------------------------------------------------------------------
# 三、yield 型依赖：带"释放逻辑"的依赖
# ---------------------------------------------------------------------------


@dataclass(slots=True)
class RequestAudit:
    """一次请求的审计上下文。"""

    path: str
    started_at: datetime


async def get_audit(request: Request) -> AsyncIterator[RequestAudit]:
    """yield 型依赖：yield 之前 = 获取资源，yield 之后 = 释放资源。

    对应 Java：try-with-resources，或 Spring 的 @Transactional 环绕通知。

    实战中最常见的用法是数据库会话：
        async def get_db():
            session = SessionLocal()
            try:
                yield session          # 路由函数在这里执行
                await session.commit()
            except Exception:
                await session.rollback()
                raise
            finally:
                await session.close()  # 一定会执行，哪怕路由抛异常
    """
    audit = RequestAudit(path=request.url.path, started_at=datetime.now())
    logger.debug(f"审计开始: {audit.path}")
    try:
        yield audit
    finally:
        elapsed_ms = (datetime.now() - audit.started_at).total_seconds() * 1000
        logger.debug(f"审计结束: {audit.path} 耗时 {elapsed_ms:.1f}ms")


# ---------------------------------------------------------------------------
# 四、类型别名：路由里只用写一个名字
#     Annotated[X, Depends(fn)] 是官方推荐写法（FastAPI 0.95+），
#     好处是类型信息和依赖声明绑在一起，IDE 能正确推断类型。
# ---------------------------------------------------------------------------

SettingsDep = Annotated[Settings, Depends(get_settings)]
RepositoryDep = Annotated[TaskRepository, Depends(get_repository)]
TaskServiceDep = Annotated[TaskService, Depends(get_task_service)]
UserServiceDep = Annotated[UserService, Depends(get_user_service)]
AuditDep = Annotated[RequestAudit, Depends(get_audit)]
