"""rag.retriever / chain 模块单元测试（纯逻辑，不加载 embedding/LLM）。

retriever 的分派与错误处理通过 monkeypatch 假向量库验证，避免加载模型。
"""

import pytest
from langchain_core.documents import Document
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.retrievers import BaseRetriever

from src.core.config import Config
from src.rag.chain import RAG_PROMPT, format_docs_with_sources


# ---------- chain ----------

def test_format_docs_with_sources_inline_source():
    """格式化片段：内联 [来源: ...] 行，供 LLM 引用与 API 解析。"""
    docs = [
        Document(
            page_content="年假满 3 年享 10 天。",
            metadata={"source": "knowledge_docs/hr/leave_policy.txt"},
        ),
        Document(
            page_content="住宿一线城市 500 元。",
            metadata={"source": "knowledge_docs/finance/travel_policy.txt"},
        ),
    ]
    text = format_docs_with_sources(docs)
    assert "[来源: knowledge_docs/hr/leave_policy.txt]" in text
    assert "[来源: knowledge_docs/finance/travel_policy.txt]" in text
    assert "年假满 3 年享 10 天" in text


def test_format_docs_unknown_source_fallback():
    """缺 source 时给出"未知来源"占位，不抛异常。"""
    text = format_docs_with_sources([Document(page_content="内容")])
    assert "未知来源" in text


def test_rag_prompt_anti_hallucination_requirements():
    """防幻觉 Prompt 必须包含：禁止编造 + 未找到兜底。"""
    template = RAG_PROMPT.messages[0].prompt.template
    assert "{context}" in template and "{question}" in template
    assert "禁止编造" in template
    assert "未找到" in template


# ---------- retriever ----------

class _FakeRetriever(BaseRetriever):
    """不加载模型的假检索器（满足 BaseRetriever 接口）。"""

    def _get_relevant_documents(self, query, *, run_manager=None):
        return [Document(page_content="假片段", metadata={"source": "fake.txt"})]


class _FakeVectorStore:
    """假向量库：不加载模型，仅验证分派逻辑。"""

    def as_retriever(self, search_kwargs=None):
        return _FakeRetriever()


def test_build_retriever_basic_mode(monkeypatch):
    """basic 模式返回纯向量检索器（k=BASIC_K）。"""
    monkeypatch.setattr(Config, "RETRIEVER_MODE", "basic")
    from src.rag import retriever as r

    monkeypatch.setattr(r, "load_vectorstore", lambda: _FakeVectorStore())
    got = r.build_retriever()
    assert isinstance(got, BaseRetriever)


def test_build_retriever_basic_without_vectorstore(monkeypatch):
    """basic 模式且向量库不存在 → FileNotFoundError（提示先 ingest）。"""
    monkeypatch.setattr(Config, "RETRIEVER_MODE", "basic")
    from src.rag import retriever as r

    monkeypatch.setattr(r, "load_vectorstore", lambda: None)
    with pytest.raises(FileNotFoundError):
        r.build_retriever()


def test_build_retriever_hybrid_mode(monkeypatch):
    """hybrid 模式返回 ContextualCompressionRetriever（含 rerank）。"""
    monkeypatch.setattr(Config, "RETRIEVER_MODE", "hybrid")
    from src.rag import retriever as r

    monkeypatch.setattr(r, "load_vectorstore", lambda: _FakeVectorStore())
    monkeypatch.setattr(r, "load_and_split", lambda: [])

    # 用假模型类替换，避免真实加载 1.1GB reranker（当前环境 C 盘空间不足会崩）
    import langchain_community.cross_encoders as ce
    from langchain_classic.retrievers.document_compressors import (
        CrossEncoderReranker,
    )

    monkeypatch.setattr(ce, "HuggingFaceCrossEncoder", lambda *a, **k: object())
    monkeypatch.setattr(
        CrossEncoderReranker, "__init__", lambda self, *a, **k: None
    )

    chunks = [Document(page_content="测试文档", metadata={"source": "x", "domain": "hr"})]
    got = r.build_retriever(chunks)
    from langchain_classic.retrievers import ContextualCompressionRetriever

    assert isinstance(got, ContextualCompressionRetriever)


def test_build_retriever_hybrid_fallback_on_reranker_error(monkeypatch):
    """reranker 加载失败 → 降级为 Ensemble（不精排），不崩溃。"""
    monkeypatch.setattr(Config, "RETRIEVER_MODE", "hybrid")
    from src.rag import retriever as r

    monkeypatch.setattr(r, "load_vectorstore", lambda: _FakeVectorStore())
    monkeypatch.setattr(r, "load_and_split", lambda: [])
    # 强制 reranker 构造抛异常
    import langchain_community.cross_encoders as ce
    from langchain_classic.retrievers.document_compressors import (
        CrossEncoderReranker,
    )

    def _boom(*a, **k):
        raise OSError("模拟 os error 1455")

    monkeypatch.setattr(ce, "HuggingFaceCrossEncoder", _boom)
    monkeypatch.setattr(CrossEncoderReranker, "__init__", _boom)

    chunks = [Document(page_content="测试文档", metadata={"source": "x", "domain": "hr"})]
    got = r.build_retriever(chunks)
    from langchain_classic.retrievers import EnsembleRetriever

    assert isinstance(got, EnsembleRetriever)


def test_build_retriever_invalid_mode(monkeypatch):
    """未知 RETRIEVER_MODE → ValueError。"""
    monkeypatch.setattr(Config, "RETRIEVER_MODE", "quantum")
    from src.rag import retriever as r

    with pytest.raises(ValueError, match="未知 RETRIEVER_MODE"):
        r.build_retriever()


# ---------- vectorstore 建库与加载一致性 ----------

def test_vectorstore_build_and_load_roundtrip(tmp_path, monkeypatch):
    """建库后 load 能打开；且返回的 Chroma 实例可检索（需 embedding，用 tmp 建小型库）。

    本用例会加载 bge-small 模型（一次性），验证持久化 roundtrip。
    """
    pytest.importorskip("sentence_transformers")
    monkeypatch.setattr(Config, "CHROMA_DIR", tmp_path / "chroma")
    from src.rag.vectorstore import build_vectorstore, load_vectorstore

    chunk = Document(
        page_content="年假：入职满 3 年不满 10 年，年假 10 天/年。",
        metadata={"source": "knowledge_docs/hr/leave_policy.txt", "domain": "hr"},
    )
    vs = build_vectorstore([chunk])
    assert vs._collection.count() == 1

    loaded = load_vectorstore()
    assert loaded is not None
    hits = loaded.similarity_search("年假有几天", k=1)
    assert hits and "年假" in hits[0].page_content
