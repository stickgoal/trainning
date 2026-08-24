"""Pydantic 请求/响应模型（M5，T5.1）。"""

from pydantic import BaseModel, Field


class ChatRequest(BaseModel):
    question: str = Field(..., min_length=1, max_length=500, description="用户问题")


class ChatResponse(BaseModel):
    answer: str
    sources: list[str] = Field(default_factory=list)    # ["knowledge_docs/finance/travel_policy.txt"]
    tool_calls: list[str] = Field(default_factory=list)  # ["search_policy"]
    elapsed_ms: int


# ---------- 知识库管理（Phase D）----------
class KBDocument(BaseModel):
    id: int
    filename: str
    ext: str
    size: int
    domain: str
    status: str
    chunks: int
    created_at: str


class KBListResponse(BaseModel):
    documents: list[KBDocument]
    count: int


class KBUploadResponse(BaseModel):
    document: KBDocument


class DBEmployee(BaseModel):
    id: str
    name: str
    department: str
    position: str
    hire_date: str


class DBTicket(BaseModel):
    id: str
    type: str
    status: str
    submit_date: str
    amount: float
