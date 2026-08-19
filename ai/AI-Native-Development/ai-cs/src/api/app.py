"""FastAPI 服务入口（M5，T5.2）。

- POST /api/chat   主问答接口（agent 单例复用，embedding/LLM 只加载一次）
- POST /api/rebuild 重建向量索引（串行加锁）
- GET  /health     健康检查（不调 LLM）

错误码约定（架构文档 5.2）：400 参数校验失败；503 LLM Key 未配置；500 内部错误。
"""

import re
import threading
import time
from functools import lru_cache

from fastapi import FastAPI, HTTPException
from fastapi.staticfiles import StaticFiles

from src.core.config import Config
from src.core.logging import get_chat_logger, get_logger
from src.api.schemas import ChatRequest, ChatResponse

logger = get_logger("api")
chat_log = get_chat_logger()

app = FastAPI(title="ai-cs 企业智能客服", version="1.0.0")

_rebuild_lock = threading.Lock()
_SOURCE_RE = re.compile(r"\[来源:\s*([^\]]+)\]")


@lru_cache(maxsize=1)
def _get_agent():
    """构建 Agent（单例）。embedding/LLM 加载很慢，禁止每请求重建。"""
    from src.agents.customer_service import build_agent
    from src.rag.retriever import build_retriever
    from src.tools.langchain_tools import build_tools

    retriever = build_retriever()
    return build_agent(build_tools(retriever))


def _parse_result(result) -> tuple[str, list[str], list[str]]:
    """从 agent.invoke 结果中提取 (answer, sources, tool_calls)。"""
    answer = result["messages"][-1].content

    tool_calls: list[str] = []
    sources: list[str] = []
    for msg in result["messages"]:
        for tc in (getattr(msg, "tool_calls", None) or []):
            tool_calls.append(tc["name"])
        # ToolMessage 的返回文本中内联来源行（search_policy 格式）
        content = getattr(msg, "content", None)
        if isinstance(content, str):
            sources.extend(_SOURCE_RE.findall(content))

    # 去重保序
    return answer, list(dict.fromkeys(sources)), list(dict.fromkeys(tool_calls))


@app.post("/api/chat", response_model=ChatResponse)
def chat(req: ChatRequest) -> ChatResponse:
    """主问答接口。"""
    if not _agent_ready():
        raise HTTPException(503, "LLM API Key 未配置（DEEPSEEK_API_KEY）")

    t0 = time.time()
    agent = _get_agent()
    result = agent.invoke({"messages": [{"role": "user", "content": req.question}]})
    answer, sources, tool_calls = _parse_result(result)
    elapsed = int((time.time() - t0) * 1000)

    chat_log.info(
        "question=%s | tools=%s | sources=%s | %dms",
        req.question, ",".join(tool_calls), ",".join(sources), elapsed,
    )
    return ChatResponse(
        answer=answer, sources=sources, tool_calls=tool_calls, elapsed_ms=elapsed,
    )


@app.post("/api/rebuild")
def rebuild() -> dict:
    """重建向量索引（内部调 ingest 逻辑，串行加锁）。"""
    with _rebuild_lock:
        from src.rag.loader import load_and_split
        from src.rag.vectorstore import build_vectorstore

        chunks = load_and_split()
        build_vectorstore(chunks)
        _get_agent.cache_clear()       # 重建后失效旧 retriever 缓存
        return {"status": "ok", "chunks": len(chunks)}


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


def _agent_ready() -> bool:
    import os

    return bool(os.getenv("DEEPSEEK_API_KEY"))


# 静态 Web 页（/ 与 /web 均指向 index.html）
app.mount("/web", StaticFiles(directory=str(Config.WEB_DIR), html=True), name="web")


@app.get("/")
def index():
    from fastapi.responses import FileResponse

    return FileResponse(Config.WEB_DIR / "index.html")
