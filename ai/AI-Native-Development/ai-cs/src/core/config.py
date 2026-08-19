"""配置模块（M0）。

所有路径/模型/检索参数收敛于此，读环境变量时可覆盖默认值。
设计依据：docs/02_架构设计.md §3.1。
"""

import os
from pathlib import Path


class Config:
    # ---------- 路径 ----------
    ROOT = Path(__file__).resolve().parents[2]          # ai-cs/
    KNOWLEDGE_DIR = ROOT / "knowledge_docs"
    CHROMA_DIR = ROOT / "chroma_db"                     # 持久化向量库
    LOG_DIR = ROOT / "logs"
    CHAT_LOG = LOG_DIR / "chat.log"
    WEB_DIR = ROOT / "web"

    # ---------- 模型（环境变量优先，默认与课程一致）----------
    LLM_MODEL = os.getenv("DEEPSEEK_MODEL", "deepseek-chat")
    EMBED_MODEL = os.getenv("EMBED_MODEL", "BAAI/bge-small-zh-v1.5")
    RERANK_MODEL = os.getenv("RERANK_MODEL", "BAAI/bge-reranker-base")

    # ---------- LLM ----------
    LLM_TEMPERATURE = 0.1                                # 问答要稳定（架构文档 4.4）

    # ---------- 检索参数 ----------
    BASIC_K = 3                    # P0 纯向量检索条数
    RECALL_K = 20                  # 多路召回每路条数（hybrid，给 rerank 留空间）
    RERANK_TOP_N = 4               # 精排保留

    # ---------- 切分参数（中文友好，实施注意 3.1）----------
    CHUNK_SIZE = 400               # 中文字符
    CHUNK_OVERLAP = 60             # 15%
    SEPARATORS = ["\n\n", "\n", "。", "；", " ", ""]     # 中文友好分隔符

    # ---------- 运行开关 ----------
    # TOOLS_MODE: inprocess(默认) | mcp（M7 独立进程形态）
    TOOLS_MODE = os.getenv("TOOLS_MODE", "inprocess")
    # RETRIEVER_MODE: basic | hybrid（M3 起生效）
    RETRIEVER_MODE = os.getenv("RETRIEVER_MODE", "hybrid")

    # ---------- 服务 ----------
    API_HOST = os.getenv("API_HOST", "0.0.0.0")
    API_PORT = int(os.getenv("API_PORT", "8100"))

    @classmethod
    def ensure_dirs(cls) -> None:
        """确保运行时需要的目录存在（日志目录等）。"""
        cls.LOG_DIR.mkdir(parents=True, exist_ok=True)
