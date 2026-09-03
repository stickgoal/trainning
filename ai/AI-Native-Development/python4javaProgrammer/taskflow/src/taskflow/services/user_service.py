"""外部「用户服务」客户端。

用于演示"调用别人的 HTTP API"这件事在 FastAPI 项目里怎么组织，
以及外部服务挂了之后怎么降级。
"""
from loguru import logger

from taskflow.core.exceptions import ExternalServiceException
from taskflow.utils.http_client import HttpClient, HttpClientError


class UserService:
    """校验任务负责人是否真实存在。"""

    def __init__(
        self,
        http: HttpClient,
        *,
        enabled: bool = True,
        fail_open: bool = True,
    ) -> None:
        self._http = http
        self._enabled = enabled
        self._fail_open = fail_open  # 外部服务挂了是"放行"还是"拒绝"

    async def exists(self, username: str) -> bool:
        """用户是否存在。

        降级策略（fail_open=True）：查不动就当用户存在，保证主流程可用。
        金融/风控类场景应设成 False —— 宁可拒绝，也不能放过脏数据。
        """
        if not self._enabled:
            return True

        try:
            data = await self._http.get(f"/users/{username}")
        except HttpClientError as exc:
            logger.warning(f"用户服务不可用（{exc.status_code}）: {exc}")
            if self._fail_open:
                return True
            raise ExternalServiceException("user-service", str(exc)) from exc

        if not isinstance(data, dict):
            return True
        return bool(data.get("exists", True))
