"""知识库管理 API 测试（Phase D，离线、隔离）。

- 业务库走临时目录（真实引擎，验证 /api/db/*）。
- KB 路由的 add_file/list_documents/delete_document 用 mock，避免触发
  reindex 的 embedding（需要模型/密钥）；reindex 同样 mock。
- 仅覆盖管理面接口，不涉及 /api/chat（需要 LLM Key）。
"""

import io

import pytest
from fastapi.testclient import TestClient

from src.api import app as app_module
from src.api.app import app
from src.api import kb_routes
from src.kb.parsers import UnsupportedFormatError
from src.core.config import Config


@pytest.fixture
def client(monkeypatch, tmp_path):
    # 业务库 / KB 注册表 / 上传目录全部落到临时目录，互不污染。
    monkeypatch.setattr(Config, "DATA_DIR", tmp_path)
    monkeypatch.setattr(Config, "KB_DB", tmp_path / "kb.db")
    monkeypatch.setattr(Config, "BUSINESS_DB", tmp_path / "business.db")
    monkeypatch.setattr(Config, "UPLOAD_DIR", tmp_path / "uploads")

    # 内存版 KB 文档表（替代真实 service，避免 reindex embedding）
    store: dict = {}

    def fake_add(filename, data, domain=None):
        doc_id = len(store) + 1
        ext = ("." + filename.rsplit(".", 1)[-1].lower()) if "." in filename else ""
        store[doc_id] = {
            "id": doc_id,
            "filename": filename,
            "ext": ext,
            "size": len(data),
            "domain": domain or "kb",
            "status": "parsed",
            "chunks": 0,
            "created_at": "2026-08-19T00:00:00",
        }
        return store[doc_id]

    def fake_list():
        # 对齐真实 kb.list_documents 的 ORDER BY id DESC
        return [store[k] for k in sorted(store, reverse=True)]

    def fake_del(doc_id):
        return store.pop(doc_id, None) is not None

    monkeypatch.setattr(kb_routes.kb, "add_file", fake_add)
    monkeypatch.setattr(kb_routes.kb, "list_documents", fake_list)
    monkeypatch.setattr(kb_routes.kb, "delete_document", fake_del)
    monkeypatch.setattr(app_module.kb_service, "reindex", lambda: 7)

    with TestClient(app) as c:
        yield c


def _upload(c, name="policy.txt", content=b"hello kb", domain=""):
    return c.post(
        "/api/kb/upload",
        files={"file": (name, io.BytesIO(content), "text/plain")},
        data={"domain": domain},
    )


def test_health(client):
    r = client.get("/health")
    assert r.status_code == 200
    assert r.json()["status"] == "ok"


def test_upload_success(client):
    r = _upload(client, "manual.txt", "这是一份员工手册".encode("utf-8"), "HR")
    assert r.status_code == 200
    doc = r.json()["document"]
    assert doc["filename"] == "manual.txt"
    assert doc["ext"] == ".txt"
    assert doc["domain"] == "HR"
    assert doc["status"] == "parsed"
    assert doc["id"] == 1


def test_upload_empty_file_400(client):
    r = _upload(client, "empty.txt", b"")
    assert r.status_code == 400


def test_upload_no_filename_400(client):
    # 无文件名的 multipart 部分会被解析为普通字段 → 422；保证请求被拒绝（非 200）。
    r = client.post(
        "/api/kb/upload",
        files={"file": ("", io.BytesIO(b"x"), "text/plain")},
        data={"domain": ""},
    )
    assert r.status_code in (400, 422)


def test_upload_unsupported_format_400(client, monkeypatch):
    def boom(filename, data, domain=None):
        raise UnsupportedFormatError("不支持的格式 .xyz")

    monkeypatch.setattr(kb_routes.kb, "add_file", boom)
    r = _upload(client, "note.xyz", b"data")
    assert r.status_code == 400
    assert "不支持" in r.json()["detail"]


def test_list_documents(client):
    _upload(client, "a.txt", b"aaa")
    _upload(client, "b.md", b"bbb")
    r = client.get("/api/kb/documents")
    assert r.status_code == 200
    body = r.json()
    assert body["count"] == 2
    assert [d["filename"] for d in body["documents"]] == ["b.md", "a.txt"]  # id DESC


def test_delete_document(client):
    _upload(client, "a.txt", b"aaa")
    r = client.delete("/api/kb/documents/1")
    assert r.status_code == 200
    assert r.json()["id"] == 1
    assert client.get("/api/kb/documents").json()["count"] == 0


def test_delete_missing_404(client):
    r = client.delete("/api/kb/documents/999")
    assert r.status_code == 404


def test_rebuild_returns_chunks(client):
    r = client.post("/api/rebuild")
    assert r.status_code == 200
    body = r.json()
    assert body["status"] == "ok"
    assert body["chunks"] == 7


def test_db_employees(client):
    r = client.get("/api/db/employees")
    assert r.status_code == 200
    emps = r.json()
    assert len(emps) == 5
    assert emps[0]["id"] == "E001"
    assert emps[0]["name"] == "张三"


def test_db_tickets(client):
    r = client.get("/api/db/tickets")
    assert r.status_code == 200
    ticks = r.json()
    assert len(ticks) == 3
    by_id = {t["id"]: t for t in ticks}
    assert by_id["RB2024001"]["amount"] == 1800.0
    assert by_id["IT20240011"]["status"] == "处理中"
