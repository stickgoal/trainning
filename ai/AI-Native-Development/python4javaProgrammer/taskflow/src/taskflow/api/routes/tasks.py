"""任务路由层。

职责边界：只做三件事 —— 接参数、调 Service、组装响应。
任何 if 业务规则都不应该出现在这里（那是 Service 的活）。

对应 Java：@RestController。
"""
import asyncio
import json
from collections.abc import AsyncIterator
from typing import Literal

from fastapi import APIRouter, BackgroundTasks, File, Query, UploadFile
from fastapi.responses import Response, StreamingResponse
from loguru import logger

from taskflow.api.deps import AuditDep, SettingsDep, TaskServiceDep
from taskflow.core.exceptions import ValidationException
from taskflow.models.base import ApiResponse, Page
from taskflow.models.task import (
    TaskCreate,
    TaskImportResult,
    TaskPriority,
    TaskQuery,
    TaskRead,
    TaskStats,
    TaskStatus,
    TaskTransitionRequest,
    TaskUpdate,
)

router = APIRouter()

# ⚠️ 路由顺序很重要：/stats、/import、/export/stream 必须写在 /{task_id} 之前。
#    FastAPI 按注册顺序匹配，若 /{task_id} 在前，请求 /api/v1/tasks/stats
#    会被当成 task_id="stats" 命中详情接口。


# ---------------- 查询类 ----------------


@router.get("", response_model=ApiResponse[Page[TaskRead]], summary="任务列表（分页 + 多条件筛选）")
async def list_tasks(
    service: TaskServiceDep,
    status: TaskStatus | None = Query(default=None, description="按状态筛选"),
    priority: TaskPriority | None = Query(default=None, description="按优先级筛选"),
    assignee: str | None = Query(default=None, max_length=50, description="按负责人筛选"),
    tag: str | None = Query(default=None, description="按标签筛选"),
    keyword: str | None = Query(default=None, description="标题/描述模糊匹配"),
    only_overdue: bool = Query(default=False, description="只看逾期"),
    page: int = Query(default=1, ge=1, description="页码，从 1 开始"),
    page_size: int = Query(default=20, ge=1, le=100, description="每页条数"),
    sort_by: Literal["created_at", "updated_at", "priority", "due_date"] = Query(
        default="created_at", description="排序字段"
    ),
    desc: bool = Query(default=True, description="是否倒序"),
) -> ApiResponse[Page[TaskRead]]:
    """所有查询参数都通过 Query() 声明校验规则，超范围直接 422。"""
    query = TaskQuery(
        status=status,
        priority=priority,
        assignee=assignee,
        tag=tag,
        keyword=keyword,
        only_overdue=only_overdue,
        page=page,
        page_size=page_size,
        sort_by=sort_by,
        desc=desc,
    )
    result = await service.list_tasks(query)
    return ApiResponse.ok(result)


@router.get("/stats", response_model=ApiResponse[TaskStats], summary="任务统计")
async def get_stats(service: TaskServiceDep) -> ApiResponse[TaskStats]:
    return ApiResponse.ok(await service.get_stats())


@router.get("/export/stream", summary="流式导出（SSE）")
async def export_tasks_stream(service: TaskServiceDep) -> StreamingResponse:
    """以 Server-Sent Events 逐条推送任务。

    为什么不用一次性返回 JSON？数据量大时，流式可以：
    - 边查边推，首字节时间（TTFB）极短；
    - 服务端内存占用恒定。

    注意：本项目的日志中间件是纯 ASGI 实现，不会缓冲响应体，
    否则 SSE 会被"攒"到最后一次性发出（详见 api/middleware.py）。
    """

    async def event_stream() -> AsyncIterator[bytes]:
        count = 0
        yield b'event: start\ndata: {"status":"start"}\n\n'
        async for record in service.iter_all():
            payload = TaskRead.from_record(record).model_dump(mode="json")
            line = f"event: task\ndata: {json.dumps(payload, ensure_ascii=False)}\n\n"
            yield line.encode("utf-8")
            count += 1
            await asyncio.sleep(0.05)  # 故意慢一点，方便肉眼看到"流"的效果
        yield f'event: done\ndata: {{"count":{count}}}\n\n'.encode("utf-8")

    return StreamingResponse(
        event_stream(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no",  # 告诉 Nginx 不要缓冲
        },
    )


# ---------------- 写入类 ----------------


@router.post(
    "",
    response_model=ApiResponse[TaskRead],
    status_code=201,
    summary="创建任务",
)
async def create_task(
    payload: TaskCreate,
    service: TaskServiceDep,
    audit: AuditDep,
    background_tasks: BackgroundTasks,
) -> ApiResponse[TaskRead]:
    """status_code=201 表示"资源已创建"，这是 REST 惯例（别一律返回 200）。"""
    task = await service.create_task(payload)
    # 后台任务：响应先返回，之后再执行。适合发通知、写审计流水这类非关键路径操作。
    background_tasks.add_task(_write_audit_log, "create_task", task.id, audit.path)
    return ApiResponse.ok(task, message="创建成功")


@router.post("/import", response_model=ApiResponse[TaskImportResult], summary="CSV 批量导入")
async def import_tasks(
    service: TaskServiceDep,
    settings: SettingsDep,
    file: UploadFile = File(..., description="CSV 文件，列：title,description,priority,assignee,tags,due_date"),
) -> ApiResponse[TaskImportResult]:
    """multipart/form-data 上传。

    需要安装 python-multipart，否则 FastAPI 会报
    "Form data requires python-multipart to be installed"。
    """
    filename = (file.filename or "").lower()
    if not filename.endswith((".csv", ".txt")):
        raise ValidationException("只支持 .csv / .txt 文件", {"filename": file.filename})

    # utf-8-sig：Excel 导出的 CSV 常带 BOM，直接用 utf-8 解码会在首列出现 \ufeff
    content = (await file.read()).decode("utf-8-sig")

    result = await service.import_from_csv(content, max_rows=settings.max_import_rows)
    return ApiResponse.ok(result, message=f"导入完成，成功 {result.created} 条")


# ---------------- 单资源操作（注意：必须放在固定路径之后） ----------------


@router.get("/{task_id}", response_model=ApiResponse[TaskRead], summary="任务详情")
async def get_task(task_id: str, service: TaskServiceDep) -> ApiResponse[TaskRead]:
    return ApiResponse.ok(await service.get_task(task_id))


@router.patch("/{task_id}", response_model=ApiResponse[TaskRead], summary="更新任务（部分更新）")
async def update_task(
    task_id: str, payload: TaskUpdate, service: TaskServiceDep
) -> ApiResponse[TaskRead]:
    return ApiResponse.ok(await service.update_task(task_id, payload), message="更新成功")


@router.post("/{task_id}/transition", response_model=ApiResponse[TaskRead], summary="状态流转")
async def transition_task(
    task_id: str, payload: TaskTransitionRequest, service: TaskServiceDep
) -> ApiResponse[TaskRead]:
    """走状态机，非法流转返回 409 Conflict。"""
    task = await service.transition_status(task_id, payload.target)
    return ApiResponse.ok(task, message=f"状态已变更为 {payload.target.value}")


@router.delete("/{task_id}", status_code=204, summary="删除任务")
async def delete_task(task_id: str, service: TaskServiceDep) -> Response:
    """204 No Content：删除成功但没有响应体。

    注意：返回 Response 实例时 FastAPI 不再做 response_model 校验，
    这正是我们想要的 —— 204 本来就规定不能有 body。
    """
    await service.delete_task(task_id)
    return Response(status_code=204)


# ---------------- 内部函数 ----------------


def _write_audit_log(action: str, task_id: str, path: str) -> None:
    """后台任务：模拟写审计流水（真实场景可能是写库或发 MQ）。

    BackgroundTasks 只适合"轻量、允许失败"的操作：
    它没有重试、没有持久化，进程一挂任务就没了。
    关键业务请用 Celery / ARQ 这类真正的任务队列。
    """
    logger.info(f"[audit] action={action} task_id={task_id} path={path}")
