# 第六章：完整项目 —— AI助手API

> 三框架协同的可运行项目：文档上传 + RAG问答 + 多轮Agent对话

---

## 6.1 完整目录结构

```
ai-native-dev/
├── pyproject.toml
├── .env.example
├── .gitignore
├── Dockerfile
├── docker-compose.yml
├── Makefile
├── README.md
│
├── src/
│   └── ai_native_dev/
│       ├── __init__.py
│       ├── main.py                 # 应用入口
│       ├── config.py               # 配置入口
│       │
│       ├── core/
│       │   ├── __init__.py
│       │   ├── settings.py         # Pydantic Settings
│       │   ├── logging.py          # loguru配置
│       │   └── exceptions.py       # 异常体系
│       │
│       ├── api/
│       │   ├── __init__.py
│       │   ├── deps.py             # 依赖注入
│       │   ├── middleware.py       # 中间件
│       │   └── routes/
│       │       ├── __init__.py
│       │       ├── health.py
│       │       ├── chat.py
│       │       └── documents.py
│       │
│       ├── models/
│       │   ├── __init__.py
│       │   ├── base.py
│       │   ├── chat.py
│       │   └── document.py
│       │
│       ├── services/
│       │   ├── __init__.py
│       │   ├── rag_service.py      # RAG服务（LlamaIndex）
│       │   ├── agent_service.py    # Agent服务（LangGraph）
│       │   └── document_service.py # 文档服务
│       │
│       ├── ai/
│       │   ├── __init__.py
│       │   ├── chains/
│       │   │   ├── __init__.py
│       │   │   └── rag_chain.py    # LangChain RAG链
│       │   ├── graphs/
│       │   │   ├── __init__.py
│       │   │   └── agent_graph.py  # LangGraph编排
│       │   ├── indexes/
│       │   │   ├── __init__.py
│       │   │   └── vector_index.py # LlamaIndex向量索引
│       │   └── tools/
│       │       ├── __init__.py
│       │       └── custom_tools.py # 自定义工具
│       │
│       └── utils/
│           ├── __init__.py
│           └── http_client.py      # httpx封装
│
├── tests/
│   ├── __init__.py
│   ├── conftest.py
│   ├── unit/
│   │   ├── __init__.py
│   │   ├── test_models.py
│   │   └── test_services.py
│   └── integration/
│       ├── __init__.py
│       └── test_api.py
│
├── data/
│   ├── uploads/
│   └── chroma/
│
└── scripts/
    ├── start.sh
    └── start.ps1
```

---

## 6.2 核心源码

### 6.2.1 pyproject.toml

```toml
# pyproject.toml
[project]
name = "ai-native-dev"
version = "0.1.0"
description = "文档上传 + RAG问答 + 多轮Agent对话的AI助手API"
readme = "README.md"
requires-python = ">=3.11"
dependencies = [
    "fastapi>=0.115.0",
    "uvicorn[standard]>=0.30.0",
    "httpx>=0.27.0",
    "pydantic>=2.0",
    "pydantic-settings>=2.0",
    "langchain>=0.3.0",
    "langchain-openai>=0.2.0",
    "langchain-community>=0.3.0",
    "langgraph>=0.2.0",
    "llama-index>=0.12.0",
    "llama-index-vector-stores-chroma>=0.3.0",
    "chromadb>=0.5.0",
    "python-dotenv>=1.0",
    "loguru>=0.7.0",
    "tenacity>=8.0",
]

[project.optional-dependencies]
dev = [
    "pytest>=8.0",
    "pytest-asyncio>=0.24",
    "pytest-cov>=5.0",
    "ruff>=0.6.0",
    "mypy>=1.11",
]

[build-system]
requires = ["hatchling"]
build-backend = "hatchling.build"

[tool.hatch.build.targets.wheel]
packages = ["src/ai_native_dev"]

[tool.ruff]
target-version = "py311"
line-length = 100

[tool.ruff.lint]
select = ["E", "W", "F", "I", "B", "C4", "UP", "N", "SIM"]
ignore = ["E501"]

[tool.pytest.ini_options]
testpaths = ["tests"]
asyncio_mode = "auto"
addopts = "-v --tb=short"
```

### 6.2.2 .env.example

```ini
# .env.example —— 复制为.env并填入实际值
APP_NAME=AI Native Dev
DEBUG=true
APP_ENV=development

OPENAI_API_KEY=sk-your-key-here
OPENAI_BASE_URL=https://api.openai.com/v1
OPENAI_MODEL=gpt-4o-mini
EMBEDDING_MODEL=text-embedding-3-small

CHROMA_PERSIST_DIR=./data/chroma
UPLOAD_DIR=./data/uploads
MAX_UPLOAD_SIZE=10485760
```

### 6.2.3 .gitignore

```gitignore
# Python
__pycache__/
*.py[cod]
*.egg-info/
dist/
build/
.venv/
.env

# IDE
.idea/
.vscode/
*.swp

# Testing
.pytest_cache/
.coverage
htmlcov/

# Data
data/uploads/*
data/chroma/*
!data/uploads/.gitkeep
!data/chroma/.gitkeep

# Logs
logs/
*.log

# OS
.DS_Store
Thumbs.db
```

### 6.2.4 核心模块源码

```python
# src/ai_native_dev/__init__.py
"""AI Native Dev - Python+AI开发实战教程项目"""
__version__ = "0.1.0"
```

```python
# src/ai_native_dev/config.py
"""统一配置入口"""
from .core.settings import settings

__all__ = ["settings"]
```

```python
# src/ai_native_dev/core/__init__.py
```
```python
# src/ai_native_dev/core/settings.py
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    app_name: str = "AI Native Dev"
    debug: bool = False
    app_env: str = "development"

    openai_api_key: str = ""
    openai_base_url: str = "https://api.openai.com/v1"
    openai_model: str = "gpt-4o-mini"
    embedding_model: str = "text-embedding-3-small"

    chroma_persist_dir: str = "./data/chroma"
    upload_dir: str = "./data/uploads"
    max_upload_size: int = 10_485_760

settings = Settings()
```

```python
# src/ai_native_dev/core/logging.py
import sys
from loguru import logger
from .settings import settings

def setup_logging() -> None:
    logger.remove()
    logger.add(
        sys.stdout,
        level="DEBUG" if settings.debug else "INFO",
        format="<green>{time:YYYY-MM-DD HH:mm:ss}</green> | <level>{level: <8}</level> | <cyan>{name}:{line}</cyan> | <level>{message}</level>",
        colorize=True,
    )
    logger.add(
        "logs/app_{time:YYYY-MM-DD}.log",
        level="INFO",
        rotation="00:00",
        retention="7 days",
        compression="zip",
        encoding="utf-8",
    )
```

```python
# src/ai_native_dev/core/exceptions.py
from fastapi import Request
from fastapi.responses import JSONResponse
from typing import Optional

class AppException(Exception):
    def __init__(self, message: str, code: int = 500, status_code: int = 500, details: Optional[dict] = None):
        self.message = message
        self.code = code
        self.status_code = status_code
        self.details = details or {}
        super().__init__(message)

class NotFoundException(AppException):
    def __init__(self, resource: str, resource_id: str):
        super().__init__(f"{resource}不存在: {resource_id}", 404, 404)

class ValidationException(AppException):
    def __init__(self, message: str, details: Optional[dict] = None):
        super().__init__(message, 422, 422, details)

class AIServiceException(AppException):
    def __init__(self, message: str, provider: str = "openai"):
        super().__init__(f"AI服务异常({provider}): {message}", 503, 503, {"provider": provider})

async def app_exception_handler(request: Request, exc: AppException) -> JSONResponse:
    from loguru import logger
    logger.error(f"异常: {exc.message} | 路径: {request.url.path}")
    return JSONResponse(status_code=exc.status_code, content={"code": exc.code, "message": exc.message, "details": exc.details})
```

```python
# src/ai_native_dev/models/__init__.py
```
```python
# src/ai_native_dev/models/base.py
from pydantic import BaseModel, Field, ConfigDict
from datetime import datetime
from typing import Optional, Any

class BaseSchema(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)

class ResponseBase(BaseModel):
    code: int = 200
    message: str = "success"
    data: Optional[Any] = None
    timestamp: datetime = Field(default_factory=datetime.now)
```

```python
# src/ai_native_dev/models/chat.py
from pydantic import BaseModel, Field, field_validator
from typing import Optional
from enum import Enum

class MessageRole(str, Enum):
    USER = "user"
    ASSISTANT = "assistant"
    SYSTEM = "system"

class ChatMessage(BaseModel):
    role: MessageRole
    content: str = Field(..., min_length=1, max_length=10000)

class ChatRequest(BaseModel):
    message: str = Field(..., min_length=1, max_length=10000, description="用户消息")
    session_id: str = Field(..., min_length=1, max_length=100, description="会话ID")
    history: list[ChatMessage] = Field(default_factory=list, description="历史消息")
    temperature: float = Field(default=0.7, ge=0.0, le=2.0)
    stream: bool = False
    use_rag: bool = True

    @field_validator("message")
    @classmethod
    def validate_message(cls, v: str) -> str:
        if not v.strip():
            raise ValueError("消息不能为空")
        return v.strip()

class ChatResponse(BaseModel):
    answer: str
    session_id: str
    sources: list[dict] = Field(default_factory=list)
    usage: Optional[dict] = None
```

```python
# src/ai_native_dev/models/document.py
from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime

class DocumentInfo(BaseModel):
    doc_id: str
    title: str
    description: Optional[str] = None
    filename: str
    created_at: datetime = Field(default_factory=datetime.now)
    chunk_count: int = 0
```

```python
# src/ai_native_dev/api/__init__.py
```
```python
# src/ai_native_dev/api/deps.py
from typing import Annotated
from fastapi import Depends
from ..services.rag_service import RagService
from ..services.agent_service import AgentService
from ..services.document_service import DocumentService

_rag_service: RagService | None = None
_agent_service: AgentService | None = None
_document_service: DocumentService | None = None

async def get_rag_service() -> RagService:
    global _rag_service
    if _rag_service is None:
        _rag_service = RagService()
    return _rag_service

async def get_agent_service() -> AgentService:
    global _agent_service
    if _agent_service is None:
        _agent_service = AgentService()
    return _agent_service

async def get_document_service() -> DocumentService:
    global _document_service
    if _document_service is None:
        _document_service = DocumentService()
    return _document_service

RagServiceDep = Annotated[RagService, Depends(get_rag_service)]
AgentServiceDep = Annotated[AgentService, Depends(get_agent_service)]
DocumentServiceDep = Annotated[DocumentService, Depends(get_document_service)]
```

```python
# src/ai_native_dev/api/middleware.py
import time
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response
from loguru import logger
from collections import defaultdict

class RequestLoggingMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        start = time.perf_counter()
        logger.info(f"→ {request.method} {request.url.path}")
        response = await call_next(request)
        elapsed = (time.perf_counter() - start) * 1000
        logger.info(f"← {request.method} {request.url.path} [{response.status_code}] {elapsed:.2f}ms")
        response.headers["X-Process-Time"] = f"{elapsed:.2f}ms"
        return response

class RateLimitMiddleware(BaseHTTPMiddleware):
    def __init__(self, app, max_requests: int = 100, window_seconds: int = 60):
        super().__init__(app)
        self.max_requests = max_requests
        self.window_seconds = window_seconds
        self._requests: dict[str, list[float]] = defaultdict(list)

    async def dispatch(self, request: Request, call_next):
        ip = request.client.host if request.client else "unknown"
        now = time.time()
        self._requests[ip] = [t for t in self._requests[ip] if now - t < self.window_seconds]
        if len(self._requests[ip]) >= self.max_requests:
            return Response(content='{"detail":"请求过于频繁"}', status_code=429, media_type="application/json")
        self._requests[ip].append(now)
        return await call_next(request)
```

```python
# src/ai_native_dev/api/routes/__init__.py
```
```python
# src/ai_native_dev/api/routes/health.py
from fastapi import APIRouter
router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "healthy", "version": "0.1.0"}
```

```python
# src/ai_native_dev/api/routes/chat.py
from fastapi import APIRouter
from fastapi.responses import StreamingResponse
from ...models.chat import ChatRequest, ChatResponse
from ..deps import AgentServiceDep, RagServiceDep
from ...core.exceptions import AIServiceException

router = APIRouter()

@router.post("", response_model=ChatResponse)
async def chat(request: ChatRequest, agent: AgentServiceDep, rag: RagServiceDep):
    try:
        context = ""
        sources = []
        if request.use_rag:
            rag_result = await rag.query(request.message)
            context = rag_result.context
            sources = rag_result.sources

        result = await agent.chat(
            message=request.message,
            session_id=request.session_id,
            history=request.history,
            context=context,
            temperature=request.temperature,
        )
        return ChatResponse(answer=result["answer"], session_id=request.session_id, sources=sources, usage=result.get("usage"))
    except Exception as e:
        raise AIServiceException(str(e))

@router.post("/stream")
async def chat_stream(request: ChatRequest, agent: AgentServiceDep):
    async def gen():
        async for chunk in agent.stream_chat(request.message, request.session_id, request.history):
            yield f"data: {chunk}\n\n"
        yield "data: [DONE]\n\n"
    return StreamingResponse(gen(), media_type="text/event-stream")
```

```python
# src/ai_native_dev/api/routes/documents.py
import os, uuid
from fastapi import APIRouter, UploadFile, File, Form
from ..deps import DocumentServiceDep
from ...core.settings import settings
from ...core.exceptions import ValidationException, NotFoundException
from ...models.base import ResponseBase

router = APIRouter()

@router.post("/upload", response_model=ResponseBase)
async def upload_document(doc_service: DocumentServiceDep, file: UploadFile = File(...), title: str = Form(...), description: str = Form(default="")):
    allowed = ["text/plain", "text/markdown", "application/pdf"]
    if file.content_type not in allowed:
        raise ValidationException(f"不支持的文件类型: {file.content_type}", {"allowed": allowed})
    content = await file.read()
    if len(content) > settings.max_upload_size:
        raise ValidationException(f"文件过大: {len(content)} bytes")
    file_id = str(uuid.uuid4())
    os.makedirs(settings.upload_dir, exist_ok=True)
    file_path = os.path.join(settings.upload_dir, f"{file_id}_{file.filename}")
    with open(file_path, "wb") as f:
        f.write(content)
    doc_id = await doc_service.index_document(file_path=file_path, title=title, description=description, metadata={"file_id": file_id, "filename": file.filename})
    return ResponseBase(message="上传成功", data={"doc_id": doc_id, "file_id": file_id, "filename": file.filename})

@router.delete("/{doc_id}", response_model=ResponseBase)
async def delete_document(doc_id: str, doc_service: DocumentServiceDep):
    if not await doc_service.delete_document(doc_id):
        raise NotFoundException("文档", doc_id)
    return ResponseBase(message="删除成功", data={"doc_id": doc_id})

@router.get("/list", response_model=ResponseBase)
async def list_documents(doc_service: DocumentServiceDep, limit: int = 20, offset: int = 0):
    docs = await doc_service.list_documents(limit=limit, offset=offset)
    return ResponseBase(data=docs)
```

```python
# src/ai_native_dev/services/__init__.py
```
```python
# src/ai_native_dev/services/rag_service.py
"""RAG服务 —— 封装LlamaIndex检索"""
from dataclasses import dataclass
from typing import Optional
from loguru import logger

@dataclass
class RagResult:
    context: str
    sources: list[dict]

class RagService:
    def __init__(self):
        self._initialized = False
        self._index = None

    async def _ensure_init(self):
        if not self._initialized:
            from ..ai.indexes.vector_index import init_vector_store, get_index
            await init_vector_store()
            self._index = get_index()
            self._initialized = True

    async def query(self, question: str) -> RagResult:
        """检索相关文档"""
        await self._ensure_init()
        if not self._index:
            return RagResult(context="", sources=[])

        try:
            query_engine = self._index.as_query_engine(similarity_top_k=3)
            response = query_engine.query(question)
            sources = [
                {"text": node.text[:200], "score": node.score, "metadata": node.metadata}
                for node in response.source_nodes
            ]
            return RagResult(context=str(response), sources=sources)
        except Exception as e:
            logger.error(f"RAG检索失败: {e}")
            return RagResult(context="", sources=[])
```

```python
# src/ai_native_dev/services/agent_service.py
"""Agent服务 —— 封装LangGraph编排"""
from typing import Optional
from langchain_core.messages import HumanMessage, AIMessage
from loguru import logger

class AgentService:
    def __init__(self):
        self._graph = None
        self._memory = None

    def _ensure_init(self):
        if self._graph is None:
            from ..ai.graphs.agent_graph import build_agent_graph
            self._graph, self._memory = build_agent_graph()

    async def chat(self, message: str, session_id: str, history: list, context: str = "", temperature: float = 0.7) -> dict:
        """多轮对话"""
        self._ensure_init()
        config = {"configurable": {"thread_id": session_id}}

        from langchain_core.messages import HumanMessage
        input_msg = HumanMessage(content=message)
        if context:
            input_msg = HumanMessage(content=f"参考上下文: {context}\n\n问题: {message}")

        result = self._graph.invoke(
            {"messages": [input_msg], "context": context},
            config=config,
        )
        last = result["messages"][-1]
        answer = last.content if hasattr(last, "content") else str(last)
        return {"answer": answer, "usage": None}

    async def stream_chat(self, message: str, session_id: str, history: list):
        """流式对话"""
        self._ensure_init()
        config = {"configurable": {"thread_id": session_id}}
        input_msg = HumanMessage(content=message)
        for chunk in self._graph.stream(
            {"messages": [input_msg], "context": ""},
            config=config,
            stream_mode="messages",
        ):
            if isinstance(chunk, tuple) and len(chunk) >= 2:
                msg = chunk[1]
                if hasattr(msg, "content") and msg.content:
                    yield msg.content
```

```python
# src/ai_native_dev/services/document_service.py
"""文档服务 —— 文档管理"""
import os
from typing import Optional
from loguru import logger
from ..core.settings import settings

class DocumentService:
    def __init__(self):
        self._documents: dict[str, dict] = {}  # 实际项目用数据库

    async def index_document(self, file_path: str, title: str, description: str = "", metadata: Optional[dict] = None) -> str:
        """索引文档到向量库"""
        import uuid
        doc_id = str(uuid.uuid4())

        # 读取文件内容
        with open(file_path, encoding="utf-8", errors="ignore") as f:
            text = f.read()

        # 用LlamaIndex索引
        try:
            from llama_index.core import Document, VectorStoreIndex
            from ..ai.indexes.vector_index import get_index, init_vector_store

            await init_vector_store()
            index = get_index()

            doc = Document(
                text=text,
                metadata={"doc_id": doc_id, "title": title, **(metadata or {})},
            )
            index.insert(doc)
            logger.info(f"文档已索引: {doc_id} ({title})")
        except Exception as e:
            logger.error(f"索引文档失败: {e}")
            # 即使索引失败也返回doc_id，允许重试

        self._documents[doc_id] = {
            "doc_id": doc_id,
            "title": title,
            "description": description,
            "file_path": file_path,
            "metadata": metadata or {},
        }
        return doc_id

    async def delete_document(self, doc_id: str) -> bool:
        """删除文档"""
        if doc_id not in self._documents:
            return False
        info = self._documents.pop(doc_id)
        # 删除文件
        if os.path.exists(info["file_path"]):
            os.remove(info["file_path"])
        # 实际项目还需从向量库删除
        logger.info(f"文档已删除: {doc_id}")
        return True

    async def list_documents(self, limit: int = 20, offset: int = 0) -> list[dict]:
        """文档列表"""
        docs = list(self._documents.values())
        return docs[offset:offset + limit]
```

```python
# src/ai_native_dev/ai/__init__.py
```
```python
# src/ai_native_dev/ai/indexes/__init__.py
```
```python
# src/ai_native_dev/ai/indexes/vector_index.py
"""LlamaIndex向量索引管理"""
import os
from loguru import logger
from ..core.settings import settings

_index = None

async def init_vector_store():
    """初始化向量存储和索引"""
    global _index
    if _index is not None:
        return

    from llama_index.core import Settings as LlamaSettings, VectorStoreIndex, StorageContext, Document
    from llama_index.embeddings.openai import OpenAIEmbedding
    from llama_index.vector_stores.chroma import ChromaVectorStore
    from langchain_openai import ChatOpenAI
    import chromadb

    # 配置
    LlamaSettings.llm = ChatOpenAI(
        model=settings.openai_model,
        temperature=0,
        api_key=settings.openai_api_key,
        base_url=settings.openai_base_url,
    )
    LlamaSettings.embed_model = OpenAIEmbedding(
        model=settings.embedding_model,
        api_key=settings.openai_api_key,
        base_url=settings.openai_base_url,
    )

    # ChromaDB
    os.makedirs(settings.chroma_persist_dir, exist_ok=True)
    db = chromadb.PersistentClient(path=settings.chroma_persist_dir)
    collection = db.get_or_create_collection("ai_native_dev")
    vector_store = ChromaVectorStore(chroma_collection=collection)
    storage_context = StorageContext.from_defaults(vector_store=vector_store)

    # 创建空索引或加载已有索引
    _index = VectorStoreIndex.from_vector_store(
        vector_store,
        storage_context=storage_context,
    )
    logger.info("向量索引初始化完成")

def get_index():
    return _index
```

```python
# src/ai_native_dev/ai/chains/__init__.py
```
```python
# src/ai_native_dev/ai/chains/rag_chain.py
"""LangChain RAG链 —— 用于生成回答"""
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough, RunnableLambda
from ..core.settings import settings

def build_rag_chain():
    """构建RAG生成链"""
    llm = ChatOpenAI(
        model=settings.openai_model,
        temperature=0.3,
        api_key=settings.openai_api_key,
        base_url=settings.openai_base_url,
    )

    prompt = ChatPromptTemplate.from_messages([
        ("system", """你是一个知识助手。根据以下参考信息回答用户问题。
如果参考信息不足以回答，请说明并尝试基于你的知识回答。

参考信息:
{context}"""),
        ("user", "{question}"),
    ])

    chain = (
        {"context": RunnablePassthrough(), "question": RunnablePassthrough()}
        | prompt
        | llm
        | StrOutputParser()
    )
    return chain

def build_simple_chain():
    """构建简单对话链（无RAG）"""
    llm = ChatOpenAI(
        model=settings.openai_model,
        temperature=0.7,
        api_key=settings.openai_api_key,
        base_url=settings.openai_base_url,
    )
    prompt = ChatPromptTemplate.from_messages([
        ("system", "你是Python编程助手，回答准确简洁。"),
        ("user", "{question}"),
    ])
    return prompt | llm | StrOutputParser()
```

```python
# src/ai_native_dev/ai/graphs/__init__.py
```
```python
# src/ai_native_dev/ai/graphs/agent_graph.py
"""LangGraph Agent编排图"""
from typing import TypedDict, Annotated, Literal
import operator
from langgraph.graph import StateGraph, END, START
from langgraph.checkpoint.memory import MemorySaver
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from ..core.settings import settings
from ..chains.rag_chain import build_rag_chain, build_simple_chain
from ..tools.custom_tools import search_knowledge_base, summarize_document
from loguru import logger

class AgentState(TypedDict):
    messages: Annotated[list, operator.add]
    context: str
    answer: str

def build_agent_graph():
    """构建Agent编排图"""
    llm = ChatOpenAI(
        model=settings.openai_model,
        temperature=0.3,
        api_key=settings.openai_api_key,
        base_url=settings.openai_base_url,
    )

    rag_chain = build_rag_chain()
    simple_chain = build_simple_chain()

    def route_input(state: AgentState) -> Literal["rag", "simple"]:
        """路由：有上下文走RAG，否则走简单对话"""
        if state.get("context"):
            return "rag"
        return "simple"

    def rag_node(state: AgentState) -> dict:
        """RAG生成节点"""
        last_msg = state["messages"][-1]
        question = last_msg.content if hasattr(last_msg, "content") else str(last_msg)
        context = state.get("context", "")

        answer = rag_chain.invoke({"context": context, "question": question})
        return {
            "answer": answer,
            "messages": [AIMessage(content=answer)],
        }

    def simple_node(state: AgentState) -> dict:
        """简单对话节点"""
        last_msg = state["messages"][-1]
        question = last_msg.content if hasattr(last_msg, "content") else str(last_msg)

        answer = simple_chain.invoke({"question": question})
        return {
            "answer": answer,
            "messages": [AIMessage(content=answer)],
        }

    # 构建图
    workflow = StateGraph(AgentState)
    workflow.add_node("rag", rag_node)
    workflow.add_node("simple", simple_node)

    workflow.add_conditional_edges(START, route_input, {"rag": "rag", "simple": "simple"})
    workflow.add_edge("rag", END)
    workflow.add_edge("simple", END)

    memory = MemorySaver()
    app = workflow.compile(checkpointer=memory)
    return app, memory
```

```python
# src/ai_native_dev/ai/tools/__init__.py
```
```python
# src/ai_native_dev/ai/tools/custom_tools.py
"""自定义LangChain工具"""
from langchain_core.tools import tool
from loguru import logger

@tool
def search_knowledge_base(query: str) -> str:
    """搜索知识库中的相关文档"""
    logger.info(f"工具调用: search_knowledge_base({query})")
    # 实际项目中调用RAG服务
    return f"知识库检索结果: 关于'{query}'的相关内容..."

@tool
def summarize_document(doc_id: str) -> str:
    """总结指定文档"""
    logger.info(f"工具调用: summarize_document({doc_id})")
    return f"文档{doc_id}的摘要: 这是一份关于Python编程的技术文档..."
```

```python
# src/ai_native_dev/utils/__init__.py
```
```python
# src/ai_native_dev/utils/http_client.py
"""HTTP客户端封装（替代OpenFeign）"""
import httpx
from typing import Optional, Any
from loguru import logger
from tenacity import retry, stop_after_attempt, wait_exponential

class HttpClient:
    def __init__(self, base_url: str = "", timeout: float = 30.0, headers: Optional[dict] = None):
        self._client = httpx.AsyncClient(
            base_url=base_url,
            timeout=httpx.Timeout(timeout, connect=5.0),
            limits=httpx.Limits(max_connections=100, max_keepalive_connections=20),
            headers=headers or {},
        )

    @retry(stop=stop_after_attempt(3), wait=wait_exponential(min=1, max=10))
    async def get(self, path: str, params: Optional[dict] = None, **kwargs) -> dict:
        return await self._request("GET", path, params=params, **kwargs)

    @retry(stop=stop_after_attempt(3), wait=wait_exponential(min=1, max=10))
    async def post(self, path: str, json: Optional[Any] = None, **kwargs) -> dict:
        return await self._request("POST", path, json=json, **kwargs)

    async def _request(self, method: str, path: str, **kwargs) -> dict:
        logger.debug(f"HTTP {method} {path}")
        response = await self._client.request(method, path, **kwargs)
        response.raise_for_status()
        return response.json()

    async def close(self):
        await self._client.aclose()

    async def __aenter__(self):
        return self

    async def __aexit__(self, *args):
        await self.close()
```

```python
# src/ai_native_dev/main.py
"""FastAPI应用入口"""
import os
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from .core.settings import settings
from .core.logging import setup_logging
from .core.exceptions import AppException, app_exception_handler
from .api.routes import health, chat, documents
from .api.middleware import RequestLoggingMiddleware, RateLimitMiddleware

@asynccontextmanager
async def lifespan(app: FastAPI):
    setup_logging()
    from loguru import logger
    logger.info(f"启动 {settings.app_name} (env={settings.app_env})")

    # 确保数据目录存在
    os.makedirs(settings.upload_dir, exist_ok=True)
    os.makedirs(settings.chroma_persist_dir, exist_ok=True)
    os.makedirs("logs", exist_ok=True)

    yield
    logger.info("应用关闭")

app = FastAPI(
    title=settings.app_name,
    description="文档上传 + RAG问答 + 多轮Agent对话的AI助手API",
    version="0.1.0",
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc",
)

app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_credentials=True, allow_methods=["*"], allow_headers=["*"])
app.add_middleware(RequestLoggingMiddleware)
app.add_middleware(RateLimitMiddleware, max_requests=100, window_seconds=60)

app.add_exception_handler(AppException, app_exception_handler)

app.include_router(health.router, tags=["健康检查"])
app.include_router(chat.router, prefix="/api/v1/chat", tags=["对话"])
app.include_router(documents.router, prefix="/api/v1/documents", tags=["文档"])

@app.get("/")
async def root():
    return {"message": f"Welcome to {settings.app_name}", "docs": "/docs"}
```

### 6.2.5 测试文件

```python
# tests/__init__.py
```
```python
# tests/conftest.py
import pytest
from httpx import AsyncClient

@pytest.fixture
async def client():
    from ai_native_dev.main import app
    async with AsyncClient(app=app, base_url="http://test") as ac:
        yield ac
```

```python
# tests/unit/__init__.py
```
```python
# tests/unit/test_models.py
import pytest
from pydantic import ValidationError
from ai_native_dev.models.chat import ChatRequest, ChatMessage, MessageRole

class TestChatRequest:
    def test_valid_request(self):
        req = ChatRequest(message="你好", session_id="s1")
        assert req.message == "你好"
        assert req.temperature == 0.7
        assert req.use_rag is True

    def test_empty_message_raises(self):
        with pytest.raises(ValidationError):
            ChatRequest(message="", session_id="s1")

    def test_temperature_out_of_range(self):
        with pytest.raises(ValidationError):
            ChatRequest(message="test", session_id="s1", temperature=3.0)

    def test_history(self):
        req = ChatRequest(
            message="test",
            session_id="s1",
            history=[ChatMessage(role=MessageRole.USER, content="hi")],
        )
        assert len(req.history) == 1
```

```python
# tests/integration/__init__.py
```
```python
# tests/integration/test_api.py
import pytest

class TestHealth:
    async def test_health(self, client):
        resp = await client.get("/health")
        assert resp.status_code == 200
        assert resp.json()["status"] == "healthy"

class TestRoot:
    async def test_root(self, client):
        resp = await client.get("/")
        assert resp.status_code == 200
        assert "docs" in resp.json()
```

### 6.2.6 Dockerfile

```dockerfile
# Dockerfile
FROM python:3.12-slim

WORKDIR /app

# 安装uv
COPY --from=ghcr.io/astral-sh/uv:latest /uv /usr/local/bin/uv

# 复制依赖文件
COPY pyproject.toml uv.lock ./

# 安装依赖（不安装项目本身）
RUN uv sync --frozen --no-install-project

# 复制源码
COPY src/ ./src/
COPY .env.example ./

# 安装项目
RUN uv sync --frozen

# 创建数据目录
RUN mkdir -p data/uploads data/chroma logs

EXPOSE 8000

CMD ["uv", "run", "uvicorn", "ai_native_dev.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

```yaml
# docker-compose.yml
version: "3.9"

services:
  app:
    build: .
    ports:
      - "8000:8000"
    env_file:
      - .env
    volumes:
      - ./data:/app/data
      - ./logs:/app/logs
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8000/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

### 6.2.7 启动脚本

```bash
#!/bin/bash
# scripts/start.sh
set -e

echo "🚀 启动 AI Native Dev..."

# 检查.env
if [ ! -f .env ]; then
    echo "⚠️  未找到.env文件，从模板创建..."
    cp .env.example .env
    echo "📝 请编辑 .env 填入 OPENAI_API_KEY"
fi

# 安装依赖
uv sync --extra dev

# 初始化数据目录
mkdir -p data/uploads data/chroma logs

# 启动
echo "✅ 启动服务: http://localhost:8000"
echo "📚 API文档: http://localhost:8000/docs"
uv run uvicorn ai_native_dev.main:app --reload --host 0.0.0.0 --port 8000
```

```powershell
# scripts/start.ps1
Write-Host "🚀 启动 AI Native Dev..." -ForegroundColor Green

# 检查.env
if (-not (Test-Path .env)) {
    Write-Host "⚠️  未找到.env文件，从模板创建..." -ForegroundColor Yellow
    Copy-Item .env.example .env
    Write-Host "📝 请编辑 .env 填入 OPENAI_API_KEY" -ForegroundColor Yellow
}

# 安装依赖
uv sync --extra dev

# 初始化目录
New-Item -ItemType Directory -Path data\uploads, data\chroma, logs -Force | Out-Null

# 启动
Write-Host "✅ 启动服务: http://localhost:8000" -ForegroundColor Green
Write-Host "📚 API文档: http://localhost:8000/docs" -ForegroundColor Cyan
uv run uvicorn ai_native_dev.main:app --reload --host 0.0.0.0 --port 8000
```

### 6.2.8 Makefile

```makefile
.PHONY: install dev run test lint format type-check clean

install:
	uv sync

dev:
	uv sync --extra dev

run:
	uv run uvicorn ai_native_dev.main:app --reload --host 0.0.0.0 --port 8000

test:
	uv run pytest -v

test-cov:
	uv run pytest --cov=src --cov-report=html

lint:
	uv run ruff check .

format:
	uv run ruff format .

type-check:
	uv run mypy src/

docker-build:
	docker build -t ai-native-dev .

docker-run:
	docker-compose up --build

clean:
	rm -rf .pytest_cache .ruff_cache .mypy_cache htmlcov logs/*.log
	find . -type d -name __pycache__ -exec rm -rf {} +
```

---

## 6.3 五分钟快速启动指南

```bash
# === 第1步: 克隆/创建项目 ===
mkdir ai-native-dev && cd ai-native-dev
# 将上述文件按目录结构创建

# === 第2步: 安装uv（如未安装） ===
# Windows PowerShell:
powershell -ExecutionPolicy ByPass -c "irm https://astral.sh/uv/install.ps1 | iex"
# Linux/macOS:
curl -LsSf https://astral.sh/uv/install.sh | sh

# === 第3步: 配置环境变量 ===
cp .env.example .env
# 编辑 .env，填入你的 OPENAI_API_KEY

# === 第4步: 安装依赖 ===
uv sync --extra dev

# === 第5步: 启动服务 ===
uv run uvicorn ai_native_dev.main:app --reload --host 0.0.0.0 --port 8000
# 或: make run
# 或: bash scripts/start.sh

# === 验证 ===
# 浏览器打开 http://localhost:8000/docs
# 测试健康检查:
curl http://localhost:8000/health
# 预期: {"status":"healthy","version":"0.1.0"}

# === 上传文档 ===
curl -X POST http://localhost:8000/api/v1/documents/upload \
  -F "file=@README.md" \
  -F "title=项目说明" \
  -F "description=AI助手项目文档"

# === 对话（RAG问答） ===
curl -X POST http://localhost:8000/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"这个项目是做什么的？","session_id":"demo-001","use_rag":true}'

# === 多轮对话 ===
curl -X POST http://localhost:8000/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"能详细说说吗？","session_id":"demo-001","use_rag":false}'

# === 运行测试 ===
uv run pytest -v

# === Docker启动 ===
docker-compose up --build
```

---

## 6.4 附录：Java程序员Python常见困惑FAQ

### FAQ 1: Python没有接口(interface)，如何实现多态？

**答：** Python使用鸭子类型(Duck Typing)和抽象基类(ABC)替代接口。

```python
# 方式1: 鸭子类型（推荐，Pythonic）
class FileLogger:
    def log(self, msg: str): print(f"[FILE] {msg}")

class ConsoleLogger:
    def log(self, msg: str): print(f"[CONSOLE] {msg}")

def do_something(logger):  # 不声明类型，只要求有log方法
    logger.log("doing work")

# 方式2: ABC（需要强制约束时）
from abc import ABC, abstractmethod
class Logger(ABC):
    @abstractmethod
    def log(self, msg: str): pass

class FileLogger(Logger):
    def log(self, msg: str): print(f"[FILE] {msg}")

# 方式3: Protocol（类型提示层面，运行时不强制）
from typing import Protocol
class LoggerProto(Protocol):
    def log(self, msg: str) -> None: ...
```

> **Java类比**：鸭子类型 ≈ 隐式接口（Groovy风格），ABC ≈ abstract class + implements

### FAQ 2: Python的 `__init__.py` 是什么？为什么需要它？

**答：** `__init__.py` 标识一个目录是Python包(package)。Python 3.3+ 支持命名空间包(无 `__init__.py`)，但显式创建 `__init__.py` 仍是最佳实践：
- 标识包边界
- 控制导出内容 (`__all__`)
- 执行包级初始化代码

> **Java类比**：Python包 ≈ Java包(package)，但Python用文件系统目录而非package声明

### FAQ 3: Python的 `self` 显式传入很奇怪，为什么不自动处理？

**答：** Python的设计哲学是"Explicit is better than implicit"(显式优于隐式)。`self` 显式传入的好处：
- 方法与函数统一（方法就是第一个参数为实例的函数）
- 可在方法内明确区分实例变量和局部变量
- 支持类装饰器和元类编程

```python
class MyClass:
    def method(self, x):
        self.x = x  # 明确知道是实例变量

# 实际上 Python 在调用时自动传入self
obj = MyClass()
obj.method(42)  # 等价于 MyClass.method(obj, 42)
```

> **Java类比**：`self` ≈ `this`，但Python要求在参数列表中显式声明

### FAQ 4: Python没有重载(overloading)，同名方法只能定义一个怎么办？

**答：** Python不支持方法重载（后定义覆盖先定义），但有多种替代方案：

```python
# 方式1: 默认参数 + 可选参数
def process(self, data: str, mode: str = "default", timeout: int | None = None):
    ...

# 方式2: *args + **kwargs（灵活但牺牲类型安全）
def process(self, *args, **kwargs):
    if len(args) == 1: ...
    elif len(args) == 2: ...

# 方式3: functools.singledispatch（按参数类型分发）
from functools import singledispatch

@singledispatch
def process(data):
    raise TypeError(f"不支持的类型: {type(data)}")

@process.register
def _(data: str): return data.upper()
@process.register
def _(data: list): return [x * 2 for x in data]
@process.register
def _(data: dict): return {k: str(v) for k, v in data.items()}

# 方式4: 多个不同名称的方法（最简单直接）
def process_text(self, text: str): ...
def process_file(self, file_path: str): ...
```

> **Java类比**：singledispatch ≈ 重载的运行时版，但Python用函数而非方法签名区分

### FAQ 5: Python的GIL是什么？多线程真的没用吗？

**答：** GIL (Global Interpreter Lock) 是CPython的全局解释器锁，同一时刻只有一个线程执行Python字节码。

**关键区别：**
- **CPU密集型任务**：多线程受GIL限制，应使用 `multiprocessing` 或C扩展
- **IO密集型任务**：多线程/async有效，因为IO等待时释放GIL

```python
# CPU密集型 → 用多进程
from multiprocessing import Pool
def cpu_task(n): return sum(i*i for i in range(n))
with Pool(4) as p:
    results = p.map(cpu_task, [10**7, 10**7, 10**7, 10**7])

# IO密集型 → 用async/await（最佳）或多线程
import asyncio, aiohttp
async def fetch(url):
    async with aiohttp.ClientSession() as s:
        async with s.get(url) as r:
            return await r.text()
# 并发100个请求完全没问题

# 混合型 → 进程池 + 协程
```

> **Java类比**：GIL使得Python多线程不如Java多线程强大，但async/await在IO场景下性能更优。Java没有GIL，但有线程安全开销。

---

## 全教程总结

| 章节 | 核心内容 | 关键工具/框架 |
|------|---------|-------------|
| 第一章 | Python语法精要 | 类型提示、推导式、装饰器、上下文管理器、生成器、async/await |
| 第二章 | 工程化环境 | pyenv、venv、uv、pyproject.toml |
| 第三章 | 项目结构与规范 | PEP 8目录、loguru、pytest、ruff、mypy |
| 第四章 | FastAPI Web框架 | 分层架构、Pydantic、DI、中间件、异常处理、httpx |
| 第五章 | AI三大框架 | LangChain（链/工具/Agent）、LangGraph（状态编排）、LlamaIndex（RAG） |
| 第六章 | 完整项目 | 三框架协同 + Docker + 测试 + 启动脚本 |

**学习路径建议：**
1. 先跑通第一章的语法示例，建立Python肌肉记忆
2. 按第二章搭建环境，用第三章规范组织代码
3. 第四章FastAPI是Web层基础，务必熟练
4. 第五章三框架是AI开发核心，建议每个框架独立练习后再做协同
5. 第六章完整项目是最终交付物，可作为后续项目的脚手架
