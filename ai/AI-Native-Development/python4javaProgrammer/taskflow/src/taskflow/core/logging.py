"""日志配置：loguru。

对应 Java：logback + SLF4J。
只在应用启动时 setup_logging() 一次，测试里不再重复配置。
"""
import sys

from loguru import logger


def setup_logging(level: str = "DEBUG", json_logs: bool = False) -> None:
    """配置全局日志。

    loguru 默认就有一个输出到 stderr 的 sink，
    所以第一步必须先 remove()，否则日志会打印两遍。
    """
    logger.remove()

    if json_logs:
        # 生产环境：输出 JSON，方便 ELK / Loki 采集
        logger.add(sys.stdout, level=level, serialize=True)
    else:
        # 开发环境：彩色、单行、带时间
        logger.add(
            sys.stdout,
            level=level,
            colorize=True,
            format=(
                "<green>{time:HH:mm:ss.SSS}</green> | "
                "<level>{level: <8}</level> | "
                "<cyan>{name}</cyan>:<cyan>{function}</cyan> - "
                "<level>{message}</level>"
            ),
        )
