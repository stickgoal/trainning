"""Chroma 向量库：建库 + 加载（M2，T2.2）。

- build_vectorstore(chunks): 全量重建（文档更新/切分参数变更后必须重建，实施注意 2.5）；
- load_vectorstore():       库已存在时直接打开，不重复 embedding（建库与查询必须同一模型）。
"""

from pathlib import Path

from langchain_community.embeddings import SentenceTransformerEmbeddings
from langchain_community.vectorstores import Chroma

from src.core.config import Config


def _embeddings() -> SentenceTransformerEmbeddings:
    """统一 embedding 实例（建库与查询共用，保证向量空间一致）。"""
    return SentenceTransformerEmbeddings(model_name=Config.EMBED_MODEL)


def build_vectorstore(chunks) -> Chroma:
    """全量重建向量库并持久化到 chroma_db/。返回 Chroma 实例。"""
    Config.CHROMA_DIR.mkdir(parents=True, exist_ok=True)
    return Chroma.from_documents(
        chunks,
        _embeddings(),
        persist_directory=str(Config.CHROMA_DIR),
    )


def load_vectorstore() -> Chroma:
    """打开已存在的向量库；不存在时返回 None（由调用方决定重建）。"""
    if not (Config.CHROMA_DIR / "chroma.sqlite3").exists():
        return None
    return Chroma(
        persist_directory=str(Config.CHROMA_DIR),
        embedding_function=_embeddings(),
    )
