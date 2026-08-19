"""Pydantic 请求/响应模型（M5，T5.1）。"""

from pydantic import BaseModel, Field


class ChatRequest(BaseModel):
    question: str = Field(..., min_length=1, max_length=500, description="用户问题")


class ChatResponse(BaseModel):
    answer: str
    sources: list[str] = Field(default_factory=list)    # ["knowledge_docs/finance/travel_policy.txt"]
    tool_calls: list[str] = Field(default_factory=list)  # ["search_policy"]
    elapsed_ms: int
