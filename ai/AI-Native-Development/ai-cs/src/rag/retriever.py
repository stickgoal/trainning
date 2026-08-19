"""检索器组装（M2 P0：纯向量；M3 升级：多路召回 + Rerank）。

按 Config.RETRIEVER_MODE 分派：
- basic:  向量检索 k=3（P0）
- hybrid: 向量 + BM25 双路召回 → Rerank 精排 top4（M3 实现）
"""

from langchain_core.vectorstores import VectorStoreRetriever

from src.core.config import Config
from src.rag.loader import load_and_split
from src.rag.vectorstore import load_vectorstore


def _basic_retriever() -> VectorStoreRetriever:
    """P0：纯向量检索。"""
    vs = load_vectorstore()
    if vs is None:
        raise FileNotFoundError(
            "向量库不存在，请先运行: python scripts/ingest.py"
        )
    return vs.as_retriever(search_kwargs={"k": Config.BASIC_K})


def _hybrid_retriever(chunks=None):
    """M3 完整链路：Ensemble(向量, BM25) → Rerank(top_n=4)。"""
    from langchain_community.cross_encoders import HuggingFaceCrossEncoder
    from langchain_community.retrievers import BM25Retriever
    from langchain_classic.retrievers import (
        ContextualCompressionRetriever,
        EnsembleRetriever,
    )
    from langchain_classic.retrievers.document_compressors import (
        CrossEncoderReranker,
    )

    vs = load_vectorstore()
    if vs is None:
        raise FileNotFoundError("向量库不存在，请先运行: python scripts/ingest.py")

    vec_retriever = vs.as_retriever(search_kwargs={"k": Config.RECALL_K})

    if chunks is None:
        chunks = load_and_split()          # BM25 需要原始文本，现切即可
    bm25 = BM25Retriever.from_documents(chunks, k=Config.RECALL_K)

    ensemble = EnsembleRetriever(
        retrievers=[vec_retriever, bm25],
        weights=[0.5, 0.5],
    )

    reranker = CrossEncoderReranker(
        model=HuggingFaceCrossEncoder(model_name=Config.RERANK_MODEL),
        top_n=Config.RERANK_TOP_N,
    )
    return ContextualCompressionRetriever(
        base_compressor=reranker,
        base_retriever=ensemble,
    )


def build_retriever(chunks=None):
    """按配置返回检索器。chunks 可选，仅 hybrid 模式需要。"""
    mode = Config.RETRIEVER_MODE
    if mode == "hybrid":
        return _hybrid_retriever(chunks)
    if mode == "basic":
        return _basic_retriever()
    raise ValueError(f"未知 RETRIEVER_MODE: {mode}（可选 basic|hybrid）")
