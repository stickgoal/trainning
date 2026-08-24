"""知识库注册表 + 服务层单元测试（Phase C）。

离线：KB DB / 上传目录指向临时路径；reindex 的 embedding 与静态语料加载被 mock，
仅验证"装配逻辑"（文档记录、解析文本、重建时合并 KB 文档）。
"""

import pytest

from src.kb import db, service


@pytest.fixture
def kb_env(tmp_path, monkeypatch):
    """KB DB / 上传目录指向临时路径并初始化。"""
    monkeypatch.setattr(service.Config, "DATA_DIR", tmp_path)
    monkeypatch.setattr(service.Config, "KB_DB", tmp_path / "kb.db")
    monkeypatch.setattr(service.Config, "UPLOAD_DIR", tmp_path / "uploads")
    service.init_db()
    return service


def test_db_crud(tmp_path, monkeypatch):
    monkeypatch.setattr(db.Config, "DATA_DIR", tmp_path)
    monkeypatch.setattr(db.Config, "KB_DB", tmp_path / "kb.db")
    db.init_db()
    did = db.add_document("a.txt", "x.txt", ".txt", 10, "内容", "h1", "kb", "parsed")
    rec = db.get_document(did)
    assert rec["filename"] == "a.txt" and rec["parsed_text"] == "内容"
    assert db.count_documents() == 1
    db.mark_indexed(did, 5)
    assert db.get_document(did)["status"] == "indexed"
    assert db.delete_document(did) is True
    assert db.get_document(did) is None


def test_add_file_parses_and_registers(kb_env, monkeypatch):
    monkeypatch.setattr(service, "reindex", lambda: 0)   # 避免真实 embedding
    rec = service.add_file("制度.txt", "年假：满3年10天。".encode("utf-8"))
    assert rec["filename"] == "制度.txt"
    assert "年假" in rec["parsed_text"]
    assert rec["status"] == "parsed"
    # 落盘文件存在
    from pathlib import Path
    assert (kb_env.Config.UPLOAD_DIR / rec["stored_name"]).exists()
    assert service.db.count_documents() == 1


def test_add_file_unsupported_format_raises(kb_env, monkeypatch):
    from src.kb.parsers import UnsupportedFormatError

    monkeypatch.setattr(service, "reindex", lambda: 0)
    with pytest.raises(UnsupportedFormatError):
        service.add_file("x.rtf", b"data")


def test_build_kb_documents_metadata(kb_env, monkeypatch):
    monkeypatch.setattr(service, "reindex", lambda: 0)
    service.add_file("手册.txt", "员工手册内容".encode("utf-8"))
    docs = service.build_kb_documents()
    assert len(docs) == 1
    d = docs[0]
    assert "员工手册内容" in d.page_content
    assert d.metadata["doc"] == "手册.txt"
    assert d.metadata["domain"] == "kb"
    assert "doc_id" in d.metadata


def test_delete_document_removes_row_and_triggers_reindex(kb_env, monkeypatch):
    calls = {"n": 0}

    def _fake_reindex():
        calls["n"] += 1
        return 0

    monkeypatch.setattr(service, "reindex", _fake_reindex)
    rec = service.add_file("a.txt", "内容".encode("utf-8"))
    assert service.delete_document(rec["id"]) is True
    assert service.db.get_document(rec["id"]) is None
    assert calls["n"] == 2   # add 时 1 次 + delete 时 1 次


def test_reindex_assembly_merges_kb_docs(kb_env, monkeypatch):
    """reindex 应合并静态语料与上传文档，并触发向量库构建。"""
    captured = {}

    def _fake_load():
        from langchain_core.documents import Document

        return [Document(page_content="静态制度条款", metadata={"source": "s", "doc": "s"})]

    def _fake_build(chunks):
        captured["chunks"] = chunks
        return None

    monkeypatch.setattr(
        "src.rag.loader.load_documents", _fake_load, raising=False
    )
    monkeypatch.setattr(
        "src.rag.vectorstore.build_vectorstore", _fake_build, raising=False
    )
    # 直接注册一条 KB 文档，再调用真实 reindex（load/build 已 mock）
    did = service.db.add_document(
        "kb.txt", "kb.txt", ".txt", 5, "上传的KB知识", "h", "kb", "parsed"
    )
    n = service.reindex()
    texts = [c.page_content for c in captured["chunks"]]
    assert any("静态制度条款" in t for t in texts)
    assert any("上传的KB知识" in t for t in texts)
    assert service.db.get_document(did)["status"] == "indexed"
