"""HTTP 客户端封装。

对应 Java：OpenFeign（声明式调用）+ Resilience4j（重试/超时）+ Apache HttpClient 连接池。

为什么不能每次请求都 `async with httpx.AsyncClient() as client` ？
    每次新建客户端 = 每次重新 TCP 握手 + TLS 握手，且连接不复用。
    正确做法：一个客户端实例活在整个应用生命周期里，由连接池复用连接。
"""
import asyncio
from typing import Any

import httpx
from loguru import logger

# 这几类状态码值得重试：限流和服务端临时故障
RETRYABLE_STATUS = frozenset({429, 500, 502, 503, 504})


class HttpClientError(Exception):
    """外部 HTTP 调用失败。

    status_code 为 None 表示"根本没连上"（超时 / DNS / 连接被拒）。
    """

    def __init__(
        self,
        message: str,
        *,
        status_code: int | None = None,
        url: str = "",
        body: str = "",
    ) -> None:
        self.status_code = status_code
        self.url = url
        self.body = body
        super().__init__(message)


class HttpClient:
    """异步 HTTP 客户端：连接池 + 超时 + 指数退避重试 + 结构化日志。

    用法：
        client = HttpClient("https://api.example.com")
        await client.get("/users/1")
        await client.aclose()          # 应用关闭时必须调用
    """

    def __init__(
        self,
        base_url: str = "",
        *,
        timeout: float = 10.0,
        connect_timeout: float = 5.0,
        headers: dict[str, str] | None = None,
        max_retries: int = 3,
        backoff_base: float = 0.2,
        transport: httpx.AsyncBaseTransport | None = None,
    ) -> None:
        # transport 参数专门为测试留的后门：可以注入 MockTransport，不真发网络请求
        self._client = httpx.AsyncClient(
            base_url=base_url,
            timeout=httpx.Timeout(timeout, connect=connect_timeout),
            limits=httpx.Limits(max_connections=100, max_keepalive_connections=20),
            headers=headers or {},
            transport=transport,
        )
        self._max_retries = max_retries
        self._backoff_base = backoff_base

    # ---------------- 便捷方法 ----------------

    async def get(self, path: str, params: dict[str, Any] | None = None) -> Any:
        return await self._request("GET", path, params=params)

    async def post(self, path: str, json: Any = None) -> Any:
        return await self._request("POST", path, json=json)

    async def put(self, path: str, json: Any = None) -> Any:
        return await self._request("PUT", path, json=json)

    async def delete(self, path: str) -> Any:
        return await self._request("DELETE", path)

    # ---------------- 核心实现 ----------------

    async def _request(self, method: str, path: str, **kwargs: Any) -> Any:
        """发请求，失败按策略重试，最终返回反序列化后的 JSON（204 返回 None）。"""
        url = f"{self._client.base_url}{path}"
        last_error: HttpClientError | None = None

        for attempt in range(1, self._max_retries + 1):
            try:
                response = await self._client.request(method, path, **kwargs)
            except httpx.RequestError as exc:
                # 连不上 / 超时 / DNS 失败
                last_error = HttpClientError(
                    f"请求失败: {type(exc).__name__}: {exc}", url=url
                )
            else:
                if response.status_code in RETRYABLE_STATUS:
                    last_error = HttpClientError(
                        f"服务端返回 {response.status_code}",
                        status_code=response.status_code,
                        url=url,
                        body=response.text[:500],
                    )
                elif response.status_code >= 400:
                    # 4xx 是"我这边的请求有问题"，重试也没用，直接抛
                    raise HttpClientError(
                        f"请求被拒绝: {response.status_code}",
                        status_code=response.status_code,
                        url=url,
                        body=response.text[:500],
                    )
                else:
                    logger.debug(f"HTTP {method} {url} → {response.status_code}")
                    if response.status_code == 204:
                        return None
                    return response.json()

            if attempt < self._max_retries:
                wait = self._backoff_base * (2 ** (attempt - 1))  # 0.2s, 0.4s, 0.8s ...
                logger.warning(
                    f"HTTP {method} {url} 第 {attempt} 次失败，{wait:.2f}s 后重试 | {last_error}"
                )
                await asyncio.sleep(wait)

        assert last_error is not None
        logger.error(f"HTTP {method} {url} 重试 {self._max_retries} 次后仍失败")
        raise last_error

    # ---------------- 生命周期 ----------------

    async def aclose(self) -> None:
        """关闭连接池。应用关闭时必须调用，否则会有连接泄漏。"""
        await self._client.aclose()

    async def __aenter__(self) -> "HttpClient":
        return self

    async def __aexit__(self, *_: object) -> None:
        await self.aclose()
