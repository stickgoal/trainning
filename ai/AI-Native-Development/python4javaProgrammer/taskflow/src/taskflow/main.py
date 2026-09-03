"""应用入口：组装所有组件。

对应 Java：@SpringBootApplication 启动类 + WebMvcConfigurer（注册拦截器）。

这里用 create_app() 工厂函数而不是"模块级 app = FastAPI(...)"，
好处是测试可以给不同参数创建不同实例（例如关掉限流、换掉配置）。
"""
from collections.abc import AsyncIterator
from contextlib import asynccontextmanager
from datetime import datetime, timedelta

from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from loguru import logger
from starlette.exceptions import HTTPException as StarletteHTTPException

from taskflow import __version__
from taskflow.api.middleware import RateLimitMiddleware, RequestContextMiddleware
from taskflow.api.routes import health, tasks
from taskflow.core.exceptions import (
    AppException,
    app_exception_handler,
    http_exception_handler,
    unhandled_exception_handler,
    validation_exception_handler,
)
from taskflow.core.logging import setup_logging
from taskflow.core.settings import get_settings
from taskflow.models.task import TaskPriority, TaskRecord, TaskStatus
from taskflow.repositories.task_repo import InMemoryTaskRepository
from taskflow.services.user_service import UserService
from taskflow.utils.http_client import HttpClient


async def seed_demo_data(repository: InMemoryTaskRepository) -> None:
    """预置几条演示数据，方便打开 /docs 就能试。

    真实项目里不会有这段代码 —— 这里是为了让第四章的例子开箱即用。
    """
    now = datetime.now()
    samples = [
        ("修复登录页 500 错误", TaskPriority.URGENT, TaskStatus.TODO, "alice",
         ["backend", "bug"], now.date() + timedelta(days=1)),
        ("补充单元测试覆盖率到 80%", TaskPriority.HIGH, TaskStatus.IN_PROGRESS, "bob",
         ["test", "quality"], now.date() + timedelta(days=7)),
        ("整理季度技术分享材料", TaskPriority.MEDIUM, TaskStatus.TODO, None,
         ["docs"], None),
        ("升级 FastAPI 到 0.115", TaskPriority.LOW, TaskStatus.DONE, "alice",
         ["backend", "upgrade"], now.date() - timedelta(days=2)),
        ("清理废弃的 feature flag", TaskPriority.MEDIUM, TaskStatus.TODO, "carol",
         ["refactor"], now.date() - timedelta(days=3)),  # 这条已逾期
    ]

    for index, (title, priority, status, assignee, tags, due_date) in enumerate(samples, start=1):
        await repository.add(
            TaskRecord(
                id=f"demo-{index:03d}",
                title=title,
                description=None,
                priority=priority,
                assignee=assignee,
                tags=tags,
                due_date=due_date,
                status=status,
                created_at=now - timedelta(hours=index),
                updated_at=now - timedelta(hours=index),
                completed_at=now if status == TaskStatus.DONE else None,
            )
        )
    logger.debug(f"已预置 {len(samples)} 条演示任务")


def create_app() -> FastAPI:
    """应用工厂。"""
    settings = get_settings()

    # 日志只配一次：生产用 JSON + INFO，开发用彩色 + DEBUG
    setup_logging(
        level="INFO" if settings.is_production else "DEBUG",
        json_logs=settings.is_production,
    )

    @asynccontextmanager
    async def lifespan(app: FastAPI) -> AsyncIterator[None]:
        """应用生命周期：yield 之前是启动，yield 之后是关闭。

        对应 Java：@PostConstruct / @PreDestroy。

        为什么要放这里而不是写在模块顶层？
        因为模块顶层代码在 import 时就执行，测试里 import 多次就会初始化多次，
        而且没法在关闭时优雅释放连接池。
        """
        # ===== 启动 =====
        app.state.repository = InMemoryTaskRepository()
        await seed_demo_data(app.state.repository)

        app.state.http_client = HttpClient(
            base_url=settings.user_service_url,
            timeout=settings.user_service_timeout,
        )
        app.state.user_service = UserService(
            app.state.http_client, enabled=settings.user_service_enabled
        )
        logger.info(
            f"{settings.app_name} 启动完成 (env={settings.app_env}, debug={settings.debug})"
        )

        yield   # ← 应用在这里对外提供服务

        # ===== 关闭 =====
        await app.state.http_client.aclose()
        logger.info("应用已关闭，资源已释放")

    app = FastAPI(
        title=settings.app_name,
        description="第四章贯穿案例：团队协作任务 API（创建 → 组件 → 测试 全流程演示）",
        version=__version__,
        lifespan=lifespan,
        docs_url="/docs",
        redoc_url="/redoc",
        # openapi_url=None  # 生产环境可以关掉文档
    )

    # ===== 中间件 =====
    # ⚠️ 执行顺序与注册顺序相反：最后添加的在最外层，最先接触请求。
    #    实际顺序：CORS → RequestContext → RateLimit → 路由
    app.add_middleware(
        RateLimitMiddleware,
        max_requests=settings.rate_limit_max_requests,
        window_seconds=settings.rate_limit_window_seconds,
    )
    app.add_middleware(RequestContextMiddleware)
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"] if settings.debug else [],   # 生产环境必须写具体域名
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # ===== 异常处理器 =====
    # 匹配顺序：精确匹配 → 父类匹配。所以 AppException 的子类都会走 app_exception_handler。
    app.add_exception_handler(AppException, app_exception_handler)
    app.add_exception_handler(RequestValidationError, validation_exception_handler)
    app.add_exception_handler(StarletteHTTPException, http_exception_handler)
    app.add_exception_handler(Exception, unhandled_exception_handler)

    # ===== 路由 =====
    app.include_router(health.router, tags=["健康检查"])
    app.include_router(tasks.router, prefix="/api/v1/tasks", tags=["任务"])

    @app.get("/", tags=["根"])
    async def root() -> dict[str, str]:
        return {"name": settings.app_name, "docs": "/docs", "health": "/health"}

    return app


# uvicorn 入口：uvicorn taskflow.main:app --reload
app = create_app()
