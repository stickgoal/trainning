"""HTTP 客户端单元测试。

关键技巧：用 httpx.MockTransport 拦截所有请求 —— 不发真实网络请求，
但走的是完整的 httpx 代码路径（序列化、超时、连接池配置都会被执行）。

对应 Java：MockWebServer / WireMock。
"""
import httpx
import pytest

from taskflow.utils.http_client import HttpClient, HttpClientError


def build_client(handler: httpx.AsyncBaseTransport | None = None, **kwargs) -> HttpClient:
    """构造一个"打不到真实网络"的客户端。"""
    kwargs.setdefault("max_retries", 3)
    kwargs.setdefault("backoff_base", 0.001)  # 退避时间压到 1ms，测试才跑得快
    return HttpClient(
        "http://user-service.test",
        transport=httpx.MockTransport(handler) if handler else None,
        **kwargs,
    )


def counting_handler(responses: list[int | Exception]) -> tuple:
    """返回一个 (handler, calls) 元组，handler 按顺序返回预设响应。"""
    calls: list[httpx.Request] = []

    def handler(request: httpx.Request) -> httpx.Response:
        calls.append(request)
        item = responses[min(len(calls) - 1, len(responses) - 1)]
        if isinstance(item, Exception):
            raise item
        return httpx.Response(item, json={"ok": True} if item < 400 else {"error": "boom"})

    return handler, calls


# ---------------- 正常路径 ----------------


async def test_get_parses_json():
    handler, calls = counting_handler([200])
    client = build_client(handler)

    data = await client.get("/users/alice")
    assert data == {"ok": True}
    assert calls[0].url.path == "/users/alice"
    assert str(calls[0].url).startswith("http://user-service.test")  # base_url 生效
    await client.aclose()


async def test_query_params_are_sent():
    handler, calls = counting_handler([200])
    client = build_client(handler)

    await client.get("/users", params={"page": 2})
    assert "page=2" in str(calls[0].url)
    await client.aclose()


async def test_post_sends_json_body():
    handler, calls = counting_handler([201])
    client = build_client(handler)

    await client.post("/users", json={"name": "alice"})
    assert b'"name": "alice"' in calls[0].content.replace(b" ", b"") or b'"name":"alice"' in calls[0].content.replace(b" ", b"")
    await client.aclose()


async def test_204_returns_none():
    """204 No Content 没有响应体，json() 会报错，所以必须特判。"""
    client = build_client(lambda request: httpx.Response(204))
    assert await client.delete("/users/alice") is None
    await client.aclose()


# ---------------- 错误路径 ----------------


async def test_4xx_fails_fast_without_retry():
    """4xx 是"请求本身有问题"，重试也没用 —— 只应调用一次。"""
    handler, calls = counting_handler([404])
    client = build_client(handler, max_retries=3)

    with pytest.raises(HttpClientError) as exc:
        await client.get("/users/nobody")

    assert exc.value.status_code == 404
    assert len(calls) == 1, "4xx 不应该重试"
    await client.aclose()


async def test_5xx_retries_then_succeeds():
    """503 → 503 → 200：前两次失败后重试成功。"""
    handler, calls = counting_handler([503, 503, 200])
    client = build_client(handler, max_retries=3)

    data = await client.get("/users/alice")
    assert data == {"ok": True}
    assert len(calls) == 3
    await client.aclose()


async def test_5xx_gives_up_after_max_retries():
    handler, calls = counting_handler([500])
    client = build_client(handler, max_retries=3)

    with pytest.raises(HttpClientError) as exc:
        await client.get("/users/alice")

    assert exc.value.status_code == 500
    assert len(calls) == 3, "应该正好重试 3 次后放弃"
    await client.aclose()


async def test_connection_error_has_no_status_code():
    """连不上时没有 HTTP 状态码 —— 用 status_code is None 区分"没连上"和"被拒绝"。"""
    handler, calls = counting_handler([httpx.ConnectError("connection refused")])
    client = build_client(handler, max_retries=2)

    with pytest.raises(HttpClientError) as exc:
        await client.get("/users/alice")

    assert exc.value.status_code is None
    assert "ConnectError" in str(exc.value)
    assert len(calls) == 2
    await client.aclose()


async def test_timeout_is_retried():
    handler, calls = counting_handler([httpx.ReadTimeout("too slow"), 200])
    client = build_client(handler, max_retries=3)

    assert await client.get("/slow") == {"ok": True}
    assert len(calls) == 2
    await client.aclose()


# ---------------- 生命周期 ----------------


async def test_async_context_manager_closes_client():
    client = build_client(lambda request: httpx.Response(200, json={"ok": True}))
    async with client as c:
        assert await c.get("/ping") == {"ok": True}
    assert client._client.is_closed


async def test_user_service_degrades_on_failure():
    """连不上外部服务时按 fail_open 降级放行。"""
    from taskflow.services.user_service import UserService

    client = build_client(lambda request: httpx.Response(500, json={}), max_retries=1)
    service = UserService(client, enabled=True, fail_open=True)
    assert await service.exists("alice") is True   # 降级：当用户存在
    await client.aclose()


async def test_user_service_strict_mode_raises():
    from taskflow.core.exceptions import ExternalServiceException
    from taskflow.services.user_service import UserService

    client = build_client(lambda request: httpx.Response(500, json={}), max_retries=1)
    service = UserService(client, enabled=True, fail_open=False)
    with pytest.raises(ExternalServiceException):
        await service.exists("alice")
    await client.aclose()
