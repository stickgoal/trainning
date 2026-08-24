"""Chroma 向量库：建库 + 加载（M2，T2.2）。

- build_vectorstore(chunks): 全量重建（文档更新/切分参数变更后必须重建，实施注意 2.5）；
- load_vectorstore():       库已存在时直接打开，不重复 embedding（建库与查询必须同一模型）。
"""

from pathlib import Path

import chromadb
from langchain_community.embeddings import SentenceTransformerEmbeddings
from langchain_community.vectorstores import Chroma

from src.core.config import Config

# Chroma 默认集合名（Chroma.from_documents 在未指定时使用 "langchain"）。
_DEFAULT_COLLECTION = "langchain"


def _embeddings() -> SentenceTransformerEmbeddings:
    """统一 embedding 实例（建库与查询共用，保证向量空间一致）。"""
    return SentenceTransformerEmbeddings(model_name=Config.EMBED_MODEL)


def build_vectorstore(chunks) -> Chroma:
    """全量重建向量库并持久化到 chroma_db/。返回 Chroma 实例。

    注意：Chroma.from_documents 对「已存在的集合」是**追加**而非替换，
    若直接调用会导致每次 /api/rebuild、每次上传都累积重复 chunk。
    这里先清空旧集合再写入，保证幂等、无重复。
    """
    Config.CHROMA_DIR.mkdir(parents=True, exist_ok=True)
    client = chromadb.PersistentClient(path=str(Config.CHROMA_DIR))
    try:
        # 已存在则清掉重建；不存在时忽略
        client.delete_collection(_DEFAULT_COLLECTION)
    except Exception:
        pass
    return Chroma.from_documents(
        chunks,
        _embeddings(),
        persist_directory=str(Config.CHROMA_DIR),
        collection_name=_DEFAULT_COLLECTION,
    )


def load_vectorstore() -> Chroma:
    """打开已存在的向量库；不存在时返回 None（由调用方决定重建）。"""
    if not (Config.CHROMA_DIR / "chroma.sqlite3").exists():
        return None
    return Chroma(
        persist_directory=str(Config.CHROMA_DIR),
        embedding_function=_embeddings(),
    )
