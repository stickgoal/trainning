"""知识库管理路由（Phase D）。

- POST /api/kb/upload        上传文件 → 解析 → 注册 → 重建索引
- GET  /api/kb/documents     列出已管理文档
- DELETE /api/kb/documents/{id}  删除文档（含落盘文件与索引）

业务库查询接口（员工/工单）见 app.py 的 /api/db/*。
"""

from fastapi import APIRouter, File, Form, HTTPException, UploadFile

from src.api.schemas import KBDocument, KBListResponse, KBUploadResponse
from src.kb import service as kb
from src.kb.parsers import ParseError, UnsupportedFormatError

router = APIRouter(prefix="/api/kb", tags=["kb"])


def _to_model(rec: dict) -> KBDocument:
    return KBDocument(
        id=rec["id"],
        filename=rec["filename"],
        ext=rec["ext"],
        size=rec["size"],
        domain=rec["domain"],
        status=rec["status"],
        chunks=rec["chunks"],
        created_at=rec["created_at"],
    )


@router.post("/upload", response_model=KBUploadResponse)
def upload(file: UploadFile = File(...), domain: str = Form("")) -> KBUploadResponse:
    """上传文档并接入知识库。支持 txt/md/pdf/docx/csv/xlsx。domain 可选。"""
    if not file.filename:
        raise HTTPException(400, "未提供文件名")
    data = file.file.read()
    if not data:
        raise HTTPException(400, "空文件")
    try:
        rec = kb.add_file(file.filename, data, domain.strip())
    except UnsupportedFormatError as e:
        raise HTTPException(400, str(e))
    except ParseError as e:
        raise HTTPException(422, str(e))
    return KBUploadResponse(document=_to_model(rec))


@router.get("/documents", response_model=KBListResponse)
def documents() -> KBListResponse:
    recs = kb.list_documents()
    return KBListResponse(documents=[_to_model(r) for r in recs], count=len(recs))


@router.delete("/documents/{doc_id}", status_code=200)
def delete(doc_id: int) -> dict:
    if not kb.delete_document(doc_id):
        raise HTTPException(404, "文档不存在")
    return {"status": "ok", "id": doc_id}
