"""统一异常体系。

对应 Java：自定义 RuntimeException 体系 + @ControllerAdvice / @ExceptionHandler。

设计约定：
- 业务代码只 raise AppException 的子类，不直接返回错误 dict；
- 每个异常自带 status_code（HTTP 状态码）与 code（业务码）；
- 由 main.py 注册的 handler 统一转成 JSON，业务代码不关心序列化。
"""
from typing import Any

from fastapi import Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from loguru import logger
from starlette.exceptions import HTTPException as StarletteHTTPException


class AppException(Exception):
    """应用异常基类。"""

    def __init__(
        self,
        message: str,
        *,
        code: int = 500,
        status_code: int = 500,
        details: dict[str, Any] | None = None,
    ) -> None:
        self.message = message
        self.code = code              # 业务码（响应体里的 code）
        self.status_code = status_code  # HTTP 状态码
        self.details: dict[str, Any] = details or {}
        super().__init__(message)


class NotFoundException(AppException):
    """资源不存在 → 404。"""

    def __init__(self, resource: str, resource_id: str) -> None:
        super().__init__(
            f"{resource}不存在: {resource_id}",
            code=404,
            status_code=404,
            details={"resource": resource, "id": resource_id},
        )


class ValidationException(AppException):
    """业务校验失败 → 422（区别于 Pydantic 的参数校验，这是"业务规则"层面的）。"""

    def __init__(self, message: str, details: dict[str, Any] | None = None) -> None:
        super().__init__(message, code=422, status_code=422, details=details)


class ConflictException(AppException):
    """状态冲突（例如非法状态流转）→ 409。"""

    def __init__(self, message: str, details: dict[str, Any] | None = None) -> None:
        super().__init__(message, code=409, status_code=409, details=details)


class ExternalServiceException(AppException):
    """调用外部服务失败 → 503。"""

    def __init__(self, service: str, message: str) -> None:
        super().__init__(
            f"外部服务异常({service}): {message}",
            code=503,
            status_code=503,
            details={"service": service},
        )


# ---------------------------------------------------------------------------
# 异常处理器：注册在 FastAPI app 上，作用等价于 @ControllerAdvice
# ---------------------------------------------------------------------------


async def app_exception_handler(request: Request, exc: AppException) -> JSONResponse:
    """处理所有 AppException 子类。"""
    logger.warning(
        f"业务异常 [{exc.code}] {exc.message} | path={request.url.path} | details={exc.details}"
    )
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "code": exc.code,
            "message": exc.message,
            "details": exc.details or None,
        },
    )


async def validation_exception_handler(
    request: Request, exc: RequestValidationError
) -> JSONResponse:
    """把 FastAPI 默认的 422 错误改成和 AppException 一致的结构。

    FastAPI 默认返回的 422 长这样（对前端很不友好）：
        {"detail":[{"loc":["body","title"],"msg":"field required","type":"value_error.missing"}]}
    统一之后：
        {"code":422,"message":"参数校验失败","details":[{"field":"title","message":"..."}]}
    """
    errors = [
        {
            # loc 形如 ("body", "title") 或 ("query", "page")，去掉第一段只留字段路径
            "field": ".".join(str(part) for part in err["loc"][1:]) or "body",
            "message": err["msg"],
            "type": err["type"],
        }
        for err in exc.errors()
    ]
    logger.warning(f"参数校验失败 | path={request.url.path} | errors={errors}")
    return JSONResponse(
        status_code=422,
        content={"code": 422, "message": "参数校验失败", "details": errors},
    )


async def http_exception_handler(
    request: Request, exc: StarletteHTTPException
) -> JSONResponse:
    """统一 HTTPException（404/405 等框架抛出的）的响应体。"""
    return JSONResponse(
        status_code=exc.status_code,
        content={"code": exc.status_code, "message": exc.detail, "details": None},
    )


async def unhandled_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    """兜底处理器：避免把堆栈抛给客户端。

    注意：logger.exception 会把完整堆栈写进日志，但响应里只有一句人话。
    生产环境务必保留这个兜底 —— 框架默认行为会把 traceback 直接返回给调用方。
    """
    logger.exception(f"未捕获异常 | path={request.url.path} | {exc}")
    return JSONResponse(
        status_code=500,
        content={"code": 500, "message": "服务器内部错误", "details": None},
    )
