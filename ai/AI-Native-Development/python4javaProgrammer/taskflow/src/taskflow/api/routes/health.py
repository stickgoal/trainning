"""健康检查路由。

两个探针要分开，这是 Kubernetes 的惯例：
- /health        → livenessProbe：进程活着就行，不要查依赖
- /health/ready  → readinessProbe：依赖就绪才放行流量
"""
from fastapi import APIRouter

from taskflow.api.deps import RepositoryDep, SettingsDep
from taskflow.models.base import ApiResponse

router = APIRouter()


@router.get("/health", response_model=ApiResponse[dict], summary="存活探针")
async def health(settings: SettingsDep) -> ApiResponse[dict]:
    """只返回进程本身的状态。

    千万别在这里查数据库 —— 数据库抖一下就返回 500，
    K8s 会以为容器挂了并反复重启，把小故障放大成雪崩。
    """
    return ApiResponse.ok({"status": "ok", "env": settings.app_env})


@router.get("/health/ready", response_model=ApiResponse[dict], summary="就绪探针")
async def readiness(repository: RepositoryDep) -> ApiResponse[dict]:
    """检查依赖是否可用。这里用仓储读一次数据来验证。"""
    tasks = await repository.list_all()
    return ApiResponse.ok({"status": "ready", "tasks": len(tasks)})
