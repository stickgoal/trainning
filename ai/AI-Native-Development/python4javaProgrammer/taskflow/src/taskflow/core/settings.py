"""应用配置：pydantic-settings。

对应 Java：@ConfigurationProperties（Spring Boot）。
环境变量带前缀 TASKFLOW_，例如 TASKFLOW_DEBUG=true、TASKFLOW_PORT=9000。
"""
from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """应用配置。字段默认值 = 开发环境默认值，可被环境变量/.env 覆盖。"""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        env_prefix="TASKFLOW_",      # 避免和系统里其它项目的同名变量冲突
        case_sensitive=False,        # TASKFLOW_DEBUG 和 taskflow_debug 都认
        extra="ignore",              # .env 里有陌生变量时不要报错
    )

    # ---- 应用 ----
    app_name: str = "TaskFlow API"
    app_env: str = "development"     # development | testing | production
    debug: bool = False
    host: str = "127.0.0.1"
    port: int = 8000

    # ---- 外部「用户服务」（演示 httpx 调用外部 API）----
    user_service_url: str = "http://127.0.0.1:9999"
    user_service_timeout: float = 2.0
    user_service_enabled: bool = False   # 关闭时跳过校验（离线也能跑）

    # ---- 限流 ----
    rate_limit_max_requests: int = 100
    rate_limit_window_seconds: int = 60

    # ---- 批量导入 ----
    max_import_rows: int = 1000

    @property
    def is_production(self) -> bool:
        return self.app_env == "production"


@lru_cache
def get_settings() -> Settings:
    """配置单例。lru_cache 保证全进程只解析一次 .env。

    Java 类比：@Bean + 单例作用域。
    """
    return Settings()
