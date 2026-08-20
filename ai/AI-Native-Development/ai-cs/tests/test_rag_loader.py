"""rag.loader / vectorstore 模块单元测试。

离线；只验证加载切分与 metadata 注入，不加载 embedding 模型。
"""

import pytest

from src.core.config import Config
from src.rag.loader import _list_docs, load_documents, load_and_split


def test_list_docs_finds_six_docs():
    docs = _list_docs()
    assert len(docs) == 6


def test_load_documents_metadata_injected():
    """每个文档注入 source/domain/doc 元数据。"""
    docs = load_documents()
    assert len(docs) == 6
    for d in docs:
        src = d.metadata.get("source", "")
        assert src.startswith("knowledge_docs/")
        assert d.metadata.get("domain") in {"hr", "finance", "admin"}
        assert d.metadata.get("doc").endswith(".txt")


def test_load_documents_domain_inferred_from_dir():
    """domain 按一级子目录推断。"""
    docs = load_documents()
    by_src = {d.metadata["source"]: d.metadata["domain"] for d in docs}
    assert by_src["knowledge_docs/finance/travel_policy.txt"] == "finance"
    assert by_src["knowledge_docs/hr/leave_policy.txt"] == "hr"
    assert by_src["knowledge_docs/admin/it_support.txt"] == "admin"


def test_split_chunks_size_within_bounds():
    """chunk 长度不超 chunk_size + overlap（中文按字符切）。"""
    chunks = load_and_split()
    assert len(chunks) >= 10
    for c in chunks:
        assert len(c.page_content) <= Config.CHUNK_SIZE + Config.CHUNK_OVERLAP
        assert c.metadata.get("domain") in {"hr", "finance", "admin"}
        assert c.metadata.get("source", "").startswith("knowledge_docs/")


def test_split_covers_all_domains():
    """切分后三个域都有 chunk。"""
    chunks = load_and_split()
    domains = {c.metadata["domain"] for c in chunks}
    assert {"hr", "finance", "admin"} <= domains


def test_vectorstore_missing_returns_none(tmp_path, monkeypatch):
    """向量库不存在时 load_vectorstore 返回 None（不抛异常）。"""
    from src.rag.vectorstore import load_vectorstore

    monkeypatch.setattr(Config, "CHROMA_DIR", tmp_path / "no_such_db")
    assert load_vectorstore() is None
