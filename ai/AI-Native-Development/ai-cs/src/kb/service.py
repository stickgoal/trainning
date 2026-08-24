"""知识库服务层（Phase C）：上传解析、注册、重建向量索引。

职责：
- add_file：解析上传字节 → 落盘 → 注册到 documents 表 → 触发 reindex
- list_documents / get_document / delete_document：管理面
- build_kb_documents：把注册表中已解析文档转为 LangChain Document（RAG 切分源）
- reindex：静态语料(knowledge_docs) + 上传文档 合并切分 → 重建 Chroma

设计：静态 knowledge_docs 与用户上传文档共同构成知识库；每次新增/删除都重建
索引，保证向量库与管理表一致（数据量小，重建成本低）。
"""

import hashlib
import uuid
from pathlib import Path

from langchain_core.documents import Document

from src.core.config import Config
from src.kb import db, parsers


def init_db() -> None:
    """初始化知识库注册表与上传目录（幂等）。"""
    db.init_db()
    Config.UPLOAD_DIR.mkdir(parents=True, exist_ok=True)


def add_file(filename: str, data: bytes, domain: str | None = None) -> dict:
    """上传文件：解析 → 落盘 → 注册 → 重建索引。返回文档记录。"""
    text = parsers.parse_bytes(filename, data)          # 可能抛 UnsupportedFormatError/ParseError
    content_hash = hashlib.sha256(data).hexdigest()
    ext = Path(filename).suffix.lower()
    stored_name = f"{uuid.uuid4().hex}{ext}"
    (Config.UPLOAD_DIR / stored_name).write_bytes(data)

    doc_id = db.add_document(
        filename=filename,
        stored_name=stored_name,
        ext=ext,
        size=len(data),
        parsed_text=text,
        content_hash=content_hash,
        domain=domain or "kb",
        status="parsed",
    )
    reindex()                                            # 索引保持一致
    rec = db.get_document(doc_id)
    assert rec is not None
    return rec


def get_document(doc_id: int) -> dict | None:
    return db.get_document(doc_id)


def list_documents() -> list[dict]:
    return db.list_documents()


def delete_document(doc_id: int) -> bool:
    """删除文档：移除落盘文件 + 注册表行 → 重建索引（移除向量）。"""
    rec = db.get_document(doc_id)
    if rec is None:
        return False
    file_path = Config.UPLOAD_DIR / rec["stored_name"]
    if file_path.exists():
        file_path.unlink()
    ok = db.delete_document(doc_id)
    reindex()                                            # 从索引中剔除
    return ok


def build_kb_documents() -> list[Document]:
    """把注册表中已解析文档转为 Document（带来源/域/doc_id 元数据）。"""
    docs: list[Document] = []
    for rec in db.list_documents():
        text = rec.get("parsed_text") or ""
        if not text.strip():
            continue
        docs.append(
            Document(
                page_content=text,
                metadata={
                    "source": f"kb/{rec['filename']}",
                    "doc": rec["filename"],
                    "domain": rec["domain"],
                    "doc_id": rec["id"],
                },
            )
        )
    return docs


def reindex() -> int:
    """合并静态语料与上传文档，切分后重建 Chroma 向量库。

    返回重建后的 chunk 数。embedding 由 vectorstore 内部完成。
    """
    from src.rag.loader import load_documents, split_documents
    from src.rag.vectorstore import build_vectorstore

    static_docs = load_documents()           # knowledge_docs/**/*.txt
    kb_docs = build_kb_documents()           # 上传文档
    chunks = split_documents(static_docs + kb_docs)
    build_vectorstore(chunks)

    # 标记上传文档为已索引
    for rec in db.list_documents():
        db.mark_indexed(rec["id"], 0)
    return len(chunks)
