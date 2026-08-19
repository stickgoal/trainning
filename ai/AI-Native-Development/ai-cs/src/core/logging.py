"""统一日志模块（M0）。

职责：控制台 + 文件双输出；提供聊天日志专用记录器（question/tool_calls/耗时）。
设计依据：docs/02_架构设计.md §6 横切关注点。
"""

import logging
import sys
from logging.handlers import RotatingFileHandler

from src.core.config import Config

_FORMAT = "%(asctime)s [%(levelname)s] %(name)s: %(message)s"
_DATEFMT = "%Y-%m-%d %H:%M:%S"


def get_logger(name: str = "ai-cs") -> logging.Logger:
    """获取统一日志记录器：输出到控制台 + logs/ai-cs.log（滚动）。"""
    logger = logging.getLogger(name)
    if logger.handlers:               # 已初始化过则复用，避免重复 handler
        return logger

    Config.ensure_dirs()
    logger.setLevel(logging.INFO)

    fmt = logging.Formatter(_FORMAT, datefmt=_DATEFMT)

    # 控制台
    console = logging.StreamHandler(sys.stdout)
    console.setFormatter(fmt)
    logger.addHandler(console)

    # 滚动文件（10MB × 3）
    file_handler = RotatingFileHandler(
        Config.LOG_DIR / "ai-cs.log",
        maxBytes=10 * 1024 * 1024,
        backupCount=3,
        encoding="utf-8",
    )
    file_handler.setFormatter(fmt)
    logger.addHandler(file_handler)

    return logger


def get_chat_logger() -> logging.Logger:
    """聊天日志专用：记录每轮 question/tool_calls/sources/耗时。

    与主日志分开落盘（logs/chat.log），便于事后分析对话质量。
    """
    logger = logging.getLogger("chat")
    if logger.handlers:
        return logger

    Config.ensure_dirs()
    logger.setLevel(logging.INFO)
    logger.propagate = False          # 不重复进 ai-cs.log

    handler = RotatingFileHandler(
        Config.CHAT_LOG,
        maxBytes=20 * 1024 * 1024,
        backupCount=5,
        encoding="utf-8",
    )
    handler.setFormatter(logging.Formatter("%(asctime)s | %(message)s", datefmt=_DATEFMT))
    logger.addHandler(handler)
    return logger
