"""知识库文件注册表（Phase C）。

用 SQLite 记录每个上传/管理的文档元数据，作为知识库的事实来源：
- documents 表：id / 原始文件名 / 落盘名 / 扩展名 / 大小 / 解析后文本 /
  域 / 分块数 / 状态 / 错误 / 哈希 / 时间

查询工具（search_policy）仍走 Chroma 向量库；本表负责"管理面"：
列表、详情、删除、解析文本回源。
"""

import sqlite3

from src.core.config import Config


def _connect() -> sqlite3.Connection:
    Config.DATA_DIR.mkdir(parents=True, exist_ok=True)
    conn = sqlite3.connect(str(Config.KB_DB))
    conn.row_factory = sqlite3.Row
    return conn


def init_db() -> None:
    """建表（幂等）。"""
    conn = _connect()
    try:
        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS documents (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                filename    TEXT NOT NULL,        -- 原始文件名（含扩展名）
                stored_name TEXT NOT NULL,        -- 落盘文件名（uuid + 扩展名）
                ext         TEXT NOT NULL,
                size        INTEGER NOT NULL DEFAULT 0,
                parsed_text TEXT,                 -- 解析后的纯文本（RAG 切分源）
                domain      TEXT NOT NULL DEFAULT 'kb',
                chunks      INTEGER NOT NULL DEFAULT 0,
                status      TEXT NOT NULL DEFAULT 'parsed',  -- parsed|indexed|error
                error       TEXT NOT NULL DEFAULT '',
                content_hash TEXT NOT NULL DEFAULT '',
                created_at  TEXT NOT NULL,
                indexed_at  TEXT
            );
            """
        )
        conn.commit()
    finally:
        conn.close()


def add_document(filename, stored_name, ext, size, parsed_text,
                 content_hash, domain="kb", status="parsed") -> int:
    """插入一条文档记录，返回自增 id。"""
    from datetime import datetime

    conn = _connect()
    try:
        cur = conn.execute(
            """INSERT INTO documents
               (filename, stored_name, ext, size, parsed_text, domain,
                chunks, status, content_hash, created_at)
               VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?, ?)""",
            (filename, stored_name, ext, size, parsed_text, domain,
             status, content_hash, datetime.now().isoformat(timespec="seconds")),
        )
        conn.commit()
        return cur.lastrowid
    finally:
        conn.close()


def get_document(doc_id: int) -> dict | None:
    conn = _connect()
    try:
        row = conn.execute(
            "SELECT * FROM documents WHERE id = ?", (doc_id,)
        ).fetchone()
    finally:
        conn.close()
    return dict(row) if row else None


def list_documents() -> list[dict]:
    conn = _connect()
    try:
        rows = conn.execute(
            "SELECT * FROM documents ORDER BY id DESC"
        ).fetchall()
    finally:
        conn.close()
    return [dict(r) for r in rows]


def delete_document(doc_id: int) -> bool:
    conn = _connect()
    try:
        cur = conn.execute("DELETE FROM documents WHERE id = ?", (doc_id,))
        conn.commit()
        return cur.rowcount > 0
    finally:
        conn.close()


def mark_indexed(doc_id: int, chunks: int) -> None:
    from datetime import datetime

    conn = _connect()
    try:
        conn.execute(
            "UPDATE documents SET status='indexed', chunks=?, indexed_at=? "
            "WHERE id=?",
            (chunks, datetime.now().isoformat(timespec="seconds"), doc_id),
        )
        conn.commit()
    finally:
        conn.close()


def count_documents() -> int:
    conn = _connect()
    try:
        return conn.execute("SELECT COUNT(*) AS n FROM documents").fetchone()["n"]
    finally:
        conn.close()
