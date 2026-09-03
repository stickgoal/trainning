"""通用响应模型：统一包装 + 分页。

对应 Java：Result<T> / PageResult<T> 这类统一返回体。
"""
from typing import Generic, TypeVar

from pydantic import BaseModel, Field

# TypeVar 让 ApiResponse 变成"泛型模型"：ApiResponse[TaskRead]、ApiResponse[Page[TaskRead]]
# 对应 Java：class ApiResponse<T> { ... }
T = TypeVar("T")


class ApiResponse(BaseModel, Generic[T]):
    """统一响应包装。

    code=0 表示成功，非 0 表示业务失败（HTTP 状态码另说）。
    注意：4xx/5xx 的错误响应不走这里，走 core/exceptions.py 的 handler。
    """

    code: int = Field(default=0, description="业务码，0 表示成功")
    message: str = Field(default="ok", description="提示信息")
    data: T | None = Field(default=None, description="业务数据")

    @classmethod
    def ok(cls, data: T | None = None, message: str = "ok") -> "ApiResponse[T]":
        """构造成功响应（工厂方法，避免到处写字面量 dict）。"""
        return cls(code=0, message=message, data=data)


class Page(BaseModel, Generic[T]):
    """分页结果。"""

    items: list[T] = Field(default_factory=list, description="当前页数据")
    total: int = Field(default=0, description="总条数")
    page: int = Field(default=1, ge=1, description="当前页码（从 1 开始）")
    page_size: int = Field(default=20, ge=1, le=100, description="每页条数")
    total_pages: int = Field(default=0, description="总页数")

    @classmethod
    def of(cls, items: list[T], total: int, page: int, page_size: int) -> "Page[T]":
        """按总数算页数：注意 total=0 时总页数是 0，不是 1。"""
        total_pages = (total + page_size - 1) // page_size if total > 0 else 0
        return cls(
            items=items,
            total=total,
            page=page,
            page_size=page_size,
            total_pages=total_pages,
        )
