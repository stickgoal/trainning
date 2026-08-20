"""core 模块单元测试：Config 路径/默认值/环境变量覆盖 + 日志落盘。

离线、不调 LLM、不耗 API 额度。
"""

import logging
import os
from pathlib import Path

import pytest

from src.core.config import Config


# ---------- Config ----------

def test_config_paths_point_to_ai_cs_root():
    """关键路径指向 ai-cs 根及其子目录。"""
    assert Config.ROOT.name == "ai-cs"
    assert Config.KNOWLEDGE_DIR == Config.ROOT / "knowledge_docs"
    assert Config.CHROMA_DIR == Config.ROOT / "chroma_db"
    assert Config.KNOWLEDGE_DIR.exists()          # 语料目录真实存在
    assert len(list(Config.KNOWLEDGE_DIR.rglob("*.txt"))) == 6


def test_config_retrieval_defaults():
    """检索/切分参数默认值对齐开发计划。"""
    assert Config.BASIC_K == 3
    assert Config.RECALL_K == 20
    assert Config.RERANK_TOP_N == 4
    assert Config.CHUNK_SIZE == 400
    assert Config.CHUNK_OVERLAP == 60
    assert Config.LLM_TEMPERATURE == 0.1


def test_config_separators_include_chinese_punctuation():
    """中文友好分隔符（实施注意 3.1）。"""
    assert "。" in Config.SEPARATORS
    assert "；" in Config.SEPARATORS
    assert "\n\n" in Config.SEPARATORS


def _run_in_subprocess(env: dict[str, str] | None, checks: str) -> str:
    """在干净子进程里验证 Config 的环境变量读取（避免 reload 污染 pytest 进程）。"""
    import subprocess
    import sys

    import src.core.config as cfg_mod

    child_env = os.environ.copy()
    if env is not None:
        for k in ("DEEPSEEK_MODEL", "TOOLS_MODE", "RETRIEVER_MODE"):
            child_env.pop(k, None)
        child_env.update(env)
    code = (
        "from src.core.config import Config\n"
        + checks
        + "\nprint('OK')"
    )
    proc = subprocess.run(
        [sys.executable, "-c", code],
        capture_output=True, text=True, cwd=cfg_mod.Config.ROOT, env=child_env,
    )
    assert proc.returncode == 0, f"stderr: {proc.stderr}"
    return proc.stdout


def test_config_env_overrides():
    """环境变量优先于默认值（Config 类属性在 import 时求值，用子进程验证）。"""
    out = _run_in_subprocess(
        {
            "DEEPSEEK_MODEL": "deepseek-reasoner",
            "TOOLS_MODE": "mcp",
            "RETRIEVER_MODE": "hybrid",
        },
        "assert Config.LLM_MODEL == 'deepseek-reasoner'\n"
        "assert Config.TOOLS_MODE == 'mcp'\n"
        "assert Config.RETRIEVER_MODE == 'hybrid'",
    )
    assert "OK" in out


def test_config_env_defaults_when_unset():
    """未设环境变量时回退默认值。"""
    out = _run_in_subprocess(
        {},
        "assert Config.LLM_MODEL == 'deepseek-chat'\n"
        "assert Config.TOOLS_MODE == 'inprocess'\n"
        "assert Config.RETRIEVER_MODE == 'basic'",
    )
    assert "OK" in out


def test_ensure_dirs_creates_log_dir(monkeypatch, tmp_path):
    """ensure_dirs 创建日志目录。"""
    monkeypatch.setattr(Config, "LOG_DIR", tmp_path / "nested" / "logs")
    Config.ensure_dirs()
    assert (tmp_path / "nested" / "logs").exists()


# ---------- logging ----------

def test_get_logger_writes_file_and_console(monkeypatch, tmp_path):
    """get_logger 应同时写控制台与日志文件（滚动文件）。"""
    monkeypatch.setattr(Config, "LOG_DIR", tmp_path)
    logger = logging.getLogger("ai-cs.test")
    for h in list(logger.handlers):        # 清理既有 handler，避免污染
        logger.removeHandler(h)
    from src.core.logging import get_logger

    logger = get_logger("ai-cs.test")
    logger.info("hello-test")
    for h in logger.handlers:
        h.flush()
    log_file = tmp_path / "ai-cs.log"
    assert log_file.exists()
    assert "hello-test" in log_file.read_text(encoding="utf-8")


def test_get_chat_logger_writes_chat_log(monkeypatch, tmp_path):
    """chat 日志独立落盘 logs/chat.log，格式含时间戳分隔。"""
    monkeypatch.setattr(Config, "LOG_DIR", tmp_path)
    monkeypatch.setattr(Config, "CHAT_LOG", tmp_path / "chat.log")   # 静态类属性，需显式替换
    from src.core.logging import get_chat_logger

    chat = get_chat_logger()                 # 固定名 "chat"（签名无参数）
    for h in list(chat.handlers):
        chat.removeHandler(h)
    chat = get_chat_logger()                 # 重建指向新 CHAT_LOG
    chat.info("question=年假 | tools=search_policy")
    for h in chat.handlers:
        h.flush()
    chat_file = tmp_path / "chat.log"
    assert chat_file.exists()
    assert "question=年假" in chat_file.read_text(encoding="utf-8")
