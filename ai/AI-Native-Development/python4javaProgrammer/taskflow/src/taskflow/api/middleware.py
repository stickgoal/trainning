"""中间件。

对应 Java：Servlet Filter / Spring Interceptor。

⚠️ 执行顺序（最容易搞反的一点）：
    app.add_middleware(A)  ...  app.add_middleware(B)
    → 实际执行顺序是 B → A → 路由 → A → B
    也就是"后添加的更靠外层"。下面的注册顺序见 main.py。
"""
import time
from collections import defaultdict
from typing import Any, MutableMapping
from uuid import uuid4

from loguru import logger
from starlette.datastructures import MutableHeaders
from starlette.middleware.base import BaseHTTPMiddleware, RequestResponseEndpoint
from starlette.requests import Request
from starlette.responses import Response
from starlette.types import ASGIApp, Receive, Scope, Send


class RequestContextMiddleware:
    """请求上下文中间件：生成 request_id、统计耗时、写访问日志、回写响应头。

    这是**纯 ASGI 中间件**（直接实现 `__call__(scope, receive, send)`），
    没有继承 BaseHTTPMiddleware。

    为什么？BaseHTTPMiddleware 会把响应体先收进内存再转发，
    StreamingResponse / SSE 就"流"不起来了（客户端会等到结束才收到数据）。
    本项目有 SSE 导出接口，所以这个中间件必须写成纯 ASGI。
    """

    def __init__(self, app: ASGIApp) -> None:
        self.app = app

    async def __call__(self, scope: Scope, receive: Receive, send: Send) -> None:
        if scope["type"] != "http":
            # websocket / lifespan 等非 HTTP 请求直接放行
            await self.app(scope, receive, send)
            return

        request_id = uuid4().hex[:12]
        started = time.perf_counter()
        status_holder: dict[str, int] = {"status": 500}

        async def send_wrapper(message: MutableMapping[str, Any]) -> None:
            # 抓住 http.response.start 这一帧，就能拿到状态码并往响应头里塞东西
            if message["type"] == "http.response.start":
                status_holder["status"] = message["status"]
                headers = MutableHeaders(scope=message)
                headers.append("X-Request-ID", request_id)
            await send(message)

        try:
            await self.app(scope, receive, send_wrapper)
        finally:
            elapsed_ms = (time.perf_counter() - started) * 1000
            logger.info(
                f"{scope['method']} {scope['path']} → {status_holder['status']} "
                f"({elapsed_ms:.1f}ms) rid={request_id}"
            )


class RateLimitMiddleware(BaseHTTPMiddleware):
    """滑动窗口限流（内存版）。

    局限：只对本进程有效。多副本部署时必须换成 Redis + Lua。
    这里用 BaseHTTPMiddleware 没问题 —— 限流只处理普通响应，不碰流式。
    """

    def __init__(
        self,
        app: ASGIApp,
        max_requests: int = 100,
        window_seconds: int = 60,
    ) -> None:
        super().__init__(app)
        self.max_requests = max_requests
        self.window_seconds = window_seconds
        self._hits: dict[str, list[float]] = defaultdict(list)

    async def dispatch(
        self, request: Request, call_next: RequestResponseEndpoint
    ) -> Response:
        client_ip = request.client.host if request.client else "unknown"
        now = time.time()

        # 只保留时间窗内的记录
        self._hits[client_ip] = [
            t for t in self._hits[client_ip] if now - t < self.window_seconds
        ]

        if len(self._hits[client_ip]) >= self.max_requests:
            logger.warning(f"限流触发: {client_ip}")
            return Response(
                content='{"code":429,"message":"请求过于频繁，请稍后再试","details":null}',
                status_code=429,
                media_type="application/json",
                headers={"Retry-After": str(self.window_seconds)},
            )

        self._hits[client_ip].append(now)
        return await call_next(request)
