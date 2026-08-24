# 第四章：FastAPI Web框架

> 分层架构 + Pydantic模型 + 依赖注入 + 异步路由 + 中间件 + 全局异常处理 + httpx客户端

---

## 4.1 分层架构总览

```
请求 → 中间件 → 路由(API层) → 依赖注入 → 业务逻辑(Service层) → 数据模型(Model层) → 响应
                                    ↓
                              异常处理器（全局捕获）
```

> **Java类比**：FastAPI ≈ Spring Boot + Spring Web，分层架构相同（Controller → Service → Repository）

---

## 4.2 Pydantic 数据模型

```python
# src/ai_native_dev/models/base.py
"""基础模型定义"""
from datetime import datetime
from pydantic import BaseModel, Field, ConfigDict
from typing import Optional, Any


class BaseSchema(BaseModel):
    """所有Schema的基类"""
    model_config = ConfigDict(
        from_attributes=True,   # 支持从ORM对象创建
        populate_by_name=True,  # 允许字段别名
    )


class ResponseBase(BaseModel):
    """统一响应格式"""
    code: int = Field(default=200, description="业务状态码")
    message: str = Field(default="success", description="提示信息")
    data: Optional[Any] = Field(default=None, description="响应数据")
    timestamp: datetime = Field(default_factory=datetime.now, description="响应时间")


class PaginatedResponse(BaseModel):
    """分页响应"""
    items: list[Any]
    total: int
    page: int
    page_size: int
    total_pages: int
```

```python
# src/ai_native_dev/models/chat.py
"""聊天相关模型"""
from pydantic import BaseModel, Field, field_validator
from typing import Optional
from enum import Enum


class MessageRole(str, Enum):
    """消息角色"""
    USER = "user"
    ASSISTANT = "assistant"
    SYSTEM = "system"


class ChatMessage(BaseModel):
    """单条消息"""
    role: MessageRole
    content: str = Field(..., min_length=1, max_length=10000)
    metadata: Optional[dict] = None


class ChatRequest(BaseModel):
    """聊天请求"""
    message: str = Field(
        ...,
        min_length=1,
        max_length=10000,
        description="用户消息",
        examples=["你好，请介绍一下Python"],
    )
    session_id: str = Field(
        ...,
        min_length=1,
        max_length=100,
        description="会话ID",
        examples=["session-001"],
    )
    history: list[ChatMessage] = Field(
        default_factory=list,
        description="历史消息",
    )
    temperature: float = Field(
        default=0.7,
        ge=0.0,
        le=2.0,
        description="温度参数",
    )
    stream: bool = Field(default=False, description="是否流式返回")
    use_rag: bool = Field(default=True, description="是否使用RAG检索")

    @field_validator("message")
    @classmethod
    def validate_message(cls, v: str) -> str:
        """消息不能只有空白"""
        if not v.strip():
            raise ValueError("消息不能为空")
        return v.strip()


class ChatResponse(BaseModel):
    """聊天响应"""
    answer: str = Field(..., description="AI回答")
    session_id: str = Field(..., description="会话ID")
    sources: list[dict] = Field(
        default_factory=list,
        description="RAG检索来源",
    )
    usage: Optional[dict] = Field(default=None, description="Token使用量")


class DocumentUploadRequest(BaseModel):
    """文档上传请求"""
    title: str = Field(..., min_length=1, max_length=200, description="文档标题")
    description: Optional[str] = Field(default=None, max_length=500)
```

---

## 4.3 应用入口与中间件

```python
# src/ai_native_dev/main.py
"""FastAPI应用入口"""
import time
from contextlib import asynccontextmanager
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from .core.settings import settings
from .core.logging import setup_logging
from .core.exceptions import AppException, app_exception_handler
from .api.routes import health, chat, documents
from .api.middleware import RequestLoggingMiddleware, RateLimitMiddleware


@asynccontextmanager
async def lifespan(app: FastAPI):
    """应用生命周期管理"""
    # 启动时
    setup_logging()
    from loguru import logger
    logger.info(f"启动 {settings.app_name} (env={settings.app_env})")

    # 初始化AI组件
    from .ai.indexes.vector_index import init_vector_store
    await init_vector_store()
    logger.info("向量索引初始化完成")

    yield  # 应用运行中

    # 关闭时
    logger.info("正在关闭应用...")


app = FastAPI(
    title=settings.app_name,
    description="文档上传 + RAG问答 + 多轮Agent对话的AI助手API",
    version="0.1.0",
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc",
)

# === 中间件（执行顺序：从下往上添加，从上往下执行） ===

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 生产环境替换为具体域名
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 请求日志
app.add_middleware(RequestLoggingMiddleware)

# 限流
app.add_middleware(RateLimitMiddleware, max_requests=100, window_seconds=60)

# === 全局异常处理 ===
app.add_exception_handler(AppException, app_exception_handler)

# === 注册路由 ===
app.include_router(health.router, tags=["健康检查"])
app.include_router(chat.router, prefix="/api/v1/chat", tags=["对话"])
app.include_router(documents.router, prefix="/api/v1/documents", tags=["文档"])


@app.get("/")
async def root():
    return {"message": f"Welcome to {settings.app_name}", "docs": "/docs"}
```

```python
# src/ai_native_dev/api/middleware.py
"""中间件定义"""
import time
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response
from loguru import logger
from collections import defaultdict


class RequestLoggingMiddleware(BaseHTTPMiddleware):
    """请求日志中间件"""

    async def dispatch(self, request: Request, call_next):
        start_time = time.perf_counter()

        # 请求信息
        logger.info(
            f"→ {request.method} {request.url.path} "
            f"from {request.client.host if request.client else 'unknown'}"
        )

        response = await call_next(request)

        # 响应信息
        elapsed = (time.perf_counter() - start_time) * 1000
        logger.info(
            f"← {request.method} {request.url.path} "
            f"[{response.status_code}] {elapsed:.2f}ms"
        )

        # 添加响应头
        response.headers["X-Process-Time"] = f"{elapsed:.2f}ms"
        return response


class RateLimitMiddleware(BaseHTTPMiddleware):
    """简单限流中间件（内存版，生产环境用Redis）"""

    def __init__(self, app, max_requests: int = 100, window_seconds: int = 60):
        super().__init__(app)
        self.max_requests = max_requests
        self.window_seconds = window_seconds
        self._requests: dict[str, list[float]] = defaultdict(list)

    async def dispatch(self, request: Request, call_next):
        client_ip = request.client.host if request.client else "unknown"
        now = time.time()

        # 清理过期记录
        self._requests[client_ip] = [
            t for t in self._requests[client_ip]
            if now - t < self.window_seconds
        ]

        if len(self._requests[client_ip]) >= self.max_requests:
            return Response(
                content='{"detail":"请求过于频繁"}',
                status_code=429,
                media_type="application/json",
            )

        self._requests[client_ip].append(now)
        return await call_next(request)
```

---

## 4.4 全局异常处理

```python
# src/ai_native_dev/core/exceptions.py
"""统一异常体系"""
from fastapi import Request
from fastapi.responses import JSONResponse
from typing import Optional


class AppException(Exception):
    """应用基础异常"""
    def __init__(
        self,
        message: str,
        code: int = 500,
        status_code: int = 500,
        details: Optional[dict] = None,
    ):
        self.message = message
        self.code = code
        self.status_code = status_code
        self.details = details or {}
        super().__init__(message)


class NotFoundException(AppException):
    """资源不存在"""
    def __init__(self, resource: str, resource_id: str):
        super().__init__(
            message=f"{resource}不存在: {resource_id}",
            code=404,
            status_code=404,
        )


class ValidationException(AppException):
    """业务校验异常"""
    def __init__(self, message: str, details: Optional[dict] = None):
        super().__init__(message=message, code=422, status_code=422, details=details)


class AIServiceException(AppException):
    """AI服务异常"""
    def __init__(self, message: str, provider: str = "openai"):
        super().__init__(
            message=f"AI服务异常({provider}): {message}",
            code=503,
            status_code=503,
            details={"provider": provider},
        )


async def app_exception_handler(request: Request, exc: AppException) -> JSONResponse:
    """全局异常处理器"""
    from loguru import logger
    logger.error(
        f"异常: {exc.message} | 路径: {request.url.path} | 详情: {exc.details}"
    )
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "code": exc.code,
            "message": exc.message,
            "details": exc.details,
        },
    )
```

---

## 4.5 路由与依赖注入

```python
# src/ai_native_dev/api/deps.py
"""依赖注入"""
from typing import Annotated
from fastapi import Depends, Header, HTTPException
from ai_native_dev.core.settings import settings
from ai_native_dev.services.rag_service import RagService
from ai_native_dev.services.agent_service import AgentService
from ai_native_dev.services.document_service import DocumentService

# 服务单例缓存
_rag_service: RagService | None = None
_agent_service: AgentService | None = None
_document_service: DocumentService | None = None


async def get_rag_service() -> RagService:
    """获取RAG服务（单例）"""
    global _rag_service
    if _rag_service is None:
        _rag_service = RagService()
    return _rag_service


async def get_agent_service() -> AgentService:
    """获取Agent服务（单例）"""
    global _agent_service
    if _agent_service is None:
        _agent_service = AgentService()
    return _agent_service


async def get_document_service() -> DocumentService:
    """获取文档服务（单例）"""
    global _document_service
    if _document_service is None:
        _document_service = DocumentService()
    return _document_service


async def verify_api_key(x_api_key: Annotated[str | None, Header()] = None) -> str:
    """API Key验证"""
    if settings.debug:
        return "debug-mode"  # 调试模式跳过验证
    if not x_api_key or x_api_key != settings.openai_api_key:
        raise HTTPException(status_code=401, detail="无效的API Key")
    return x_api_key


# 类型别名（简化路由签名）
RagServiceDep = Annotated[RagService, Depends(get_rag_service)]
AgentServiceDep = Annotated[AgentService, Depends(get_agent_service)]
DocumentServiceDep = Annotated[DocumentService, Depends(get_document_service)]
```

```python
# src/ai_native_dev/api/routes/health.py
"""健康检查路由"""
from fastapi import APIRouter, Depends
from ai_native_dev.api.deps import RagServiceDep

router = APIRouter()


@router.get("/health")
async def health_check():
    """健康检查"""
    return {"status": "healthy", "version": "0.1.0"}


@router.get("/health/ready")
async def readiness_check(rag_service: RagServiceDep):
    """就绪检查"""
    return {
        "status": "ready",
        "rag_service": "available" if rag_service else "unavailable",
    }
```

```python
# src/ai_native_dev/api/routes/chat.py
"""对话路由"""
from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import StreamingResponse
from ai_native_dev.models.chat import ChatRequest, ChatResponse
from ai_native_dev.api.deps import AgentServiceDep, RagServiceDep
from ai_native_dev.core.exceptions import AIServiceException

router = APIRouter()


@router.post("", response_model=ChatResponse)
async def chat(
    request: ChatRequest,
    agent_service: AgentServiceDep,
    rag_service: RagServiceDep,
):
    """
    AI对话接口

    - 支持多轮对话（通过session_id关联）
    - 可选RAG检索增强
    - 可选流式返回
    """
    try:
        if request.use_rag:
            # 先做RAG检索
            rag_result = await rag_service.query(request.message)
            # 将检索结果注入上下文
            context = rag_result.context
            sources = rag_result.sources
        else:
            context = ""
            sources = []

        # Agent处理
        result = await agent_service.chat(
            message=request.message,
            session_id=request.session_id,
            history=request.history,
            context=context,
            temperature=request.temperature,
        )

        return ChatResponse(
            answer=result.answer,
            session_id=request.session_id,
            sources=sources,
            usage=result.usage,
        )

    except Exception as e:
        raise AIServiceException(str(e))


@router.post("/stream")
async def chat_stream(
    request: ChatRequest,
    agent_service: AgentServiceDep,
):
    """流式对话接口（SSE）"""
    async def event_generator():
        async for chunk in agent_service.stream_chat(
            message=request.message,
            session_id=request.session_id,
            history=request.history,
        ):
            yield f"data: {chunk}\n\n"
        yield "data: [DONE]\n\n"

    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
    )
```

```python
# src/ai_native_dev/api/routes/documents.py
"""文档管理路由"""
import os
import uuid
from fastapi import APIRouter, Depends, UploadFile, File, Form, HTTPException
from ai_native_dev.api.deps import DocumentServiceDep
from ai_native_dev.core.settings import settings
from ai_native_dev.core.exceptions import ValidationException, NotFoundException
from ai_native_dev.models.base import ResponseBase

router = APIRouter()


@router.post("/upload", response_model=ResponseBase)
async def upload_document(
    document_service: DocumentServiceDep,
    file: UploadFile = File(...),
    title: str = Form(...),
    description: str = Form(default=""),
):
    """上传文档（支持.txt, .md, .pdf）"""
    # 校验文件类型
    allowed_types = ["text/plain", "text/markdown", "application/pdf"]
    if file.content_type not in allowed_types:
        raise ValidationException(
            f"不支持的文件类型: {file.content_type}",
            {"allowed": allowed_types},
        )

    # 校验文件大小
    content = await file.read()
    if len(content) > settings.max_upload_size:
        raise ValidationException(
            f"文件过大: {len(content)} bytes",
            {"max_size": settings.max_upload_size},
        )

    # 保存文件
    file_id = str(uuid.uuid4())
    file_path = os.path.join(settings.upload_dir, f"{file_id}_{file.filename}")
    os.makedirs(os.path.dirname(file_path), exist_ok=True)
    with open(file_path, "wb") as f:
        f.write(content)

    # 索引文档
    doc_id = await document_service.index_document(
        file_path=file_path,
        title=title,
        description=description,
        metadata={"file_id": file_id, "filename": file.filename},
    )

    return ResponseBase(
        message="文档上传并索引成功",
        data={"doc_id": doc_id, "file_id": file_id, "filename": file.filename},
    )


@router.delete("/{doc_id}", response_model=ResponseBase)
async def delete_document(
    doc_id: str,
    document_service: DocumentServiceDep,
):
    """删除文档"""
    deleted = await document_service.delete_document(doc_id)
    if not deleted:
        raise NotFoundException("文档", doc_id)
    return ResponseBase(message="文档删除成功", data={"doc_id": doc_id})


@router.get("/list", response_model=ResponseBase)
async def list_documents(
    document_service: DocumentServiceDep,
    limit: int = 20,
    offset: int = 0,
):
    """文档列表"""
    docs = await document_service.list_documents(limit=limit, offset=offset)
    return ResponseBase(data=docs)
```

---

## 4.6 封装 httpx 客户端（替代 OpenFeign）

```python
# src/ai_native_dev/utils/http_client.py
"""
HTTP客户端封装（替代Java中的OpenFeign/RestTemplate）

特性:
- 连接池复用
- 自动重试
- 请求/响应日志
- 超时控制
- 统一错误处理
"""
import httpx
from typing import Optional, Any
from loguru import logger
from tenacity import retry, stop_after_attempt, wait_exponential


class HttpClient:
    """异步HTTP客户端"""

    def __init__(
        self,
        base_url: str = "",
        timeout: float = 30.0,
        max_retries: int = 3,
        headers: Optional[dict] = None,
    ):
        self._client = httpx.AsyncClient(
            base_url=base_url,
            timeout=httpx.Timeout(timeout, connect=5.0),
            limits=httpx.Limits(max_connections=100, max_keepalive_connections=20),
            headers=headers or {},
        )
        self._max_retries = max_retries

    @retry(stop=stop_after_attempt(3), wait=wait_exponential(min=1, max=10))
    async def get(self, path: str, params: Optional[dict] = None, **kwargs) -> dict:
        return await self._request("GET", path, params=params, **kwargs)

    @retry(stop=stop_after_attempt(3), wait=wait_exponential(min=1, max=10))
    async def post(self, path: str, json: Optional[Any] = None, **kwargs) -> dict:
        return await self._request("POST", path, json=json, **kwargs)

    @retry(stop=stop_after_attempt(3), wait=wait_exponential(min=1, max=10))
    async def put(self, path: str, json: Optional[Any] = None, **kwargs) -> dict:
        return await self._request("PUT", path, json=json, **kwargs)

    @retry(stop=stop_after_attempt(3), wait=wait_exponential(min=1, max=10))
    async def delete(self, path: str, **kwargs) -> dict:
        return await self._request("DELETE", path, **kwargs)

    async def _request(self, method: str, path: str, **kwargs) -> dict:
        """统一请求方法"""
        logger.debug(f"HTTP {method} {path} kwargs={kwargs}")

        response = await self._client.request(method, path, **kwargs)

        if response.status_code >= 400:
            logger.error(
                f"HTTP {method} {path} 失败: "
                f"status={response.status_code} body={response.text}"
            )
            response.raise_for_status()

        logger.debug(f"HTTP {method} {path} → {response.status_code}")
        return response.json()

    async def stream_get(self, path: str, params: Optional[dict] = None):
        """流式GET请求"""
        async with self._client.stream("GET", path, params=params) as response:
            response.raise_for_status()
            async for chunk in response.aiter_bytes():
                yield chunk

    async def close(self):
        """关闭客户端"""
        await self._client.aclose()

    async def __aenter__(self):
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        await self.close()


# 使用示例
"""
# 作为依赖注入
async def get_external_api_client() -> HttpClient:
    client = HttpClient(
        base_url="https://api.example.com",
        headers={"Authorization": f"Bearer {settings.api_key}"},
    )
    return client

# 在Service中使用
class SomeService:
    def __init__(self, http: HttpClient):
        self.http = http

    async def call_external(self):
        result = await self.http.get("/users/1")
        return result

# 直接使用
async with HttpClient("https://api.example.com") as client:
    data = await client.get("/users/1")
    print(data)
"""
# Java类比: HttpClient ≈ OpenFeign + Resilience4j（重试、超时、连接池）
```

---

## 4.7 最小可运行示例

```python
# python4javaProgrammer/fastapi_demo.py
"""FastAPI最小示例 —— 可直接运行"""
from fastapi import FastAPI, Depends
from pydantic import BaseModel, Field
from typing import Annotated

app = FastAPI(title="Demo API")

# 模拟数据库
fake_db = {"users": {1: {"name": "Alice"}, 2: {"name": "Bob"}}}

# Pydantic模型
class UserCreate(BaseModel):
    name: str = Field(..., min_length=1, max_length=50)

class UserResponse(BaseModel):
    id: int
    name: str

# 依赖
def get_db():
    return fake_db

DbDep = Annotated[dict, Depends(get_db)]

# 路由
@app.get("/users/{user_id}", response_model=UserResponse)
async def get_user(user_id: int, db: DbDep):
    user = db["users"].get(user_id)
    if not user:
        from fastapi import HTTPException
        raise HTTPException(status_code=404, detail="用户不存在")
    return UserResponse(id=user_id, name=user["name"])

@app.post("/users", response_model=UserResponse, status_code=201)
async def create_user(user: UserCreate, db: DbDep):
    new_id = max(db["users"].keys()) + 1
    db["users"][new_id] = {"name": user.name}
    return UserResponse(id=new_id, name=user.name)

# 运行: uvicorn fastapi_demo:app --reload
# 文档: http://localhost:8000/docs
```

---

## 本章小结

| 特性 | FastAPI | Java类比 |
|------|---------|---------|
| 路由 | `@router.get/post` | `@GetMapping/@PostMapping` |
| 数据校验 | Pydantic模型 | @Valid + Bean Validation |
| 依赖注入 | `Depends()` | @Autowired |
| 中间件 | `add_middleware()` | Filter/Interceptor |
| 异常处理 | `add_exception_handler()` | @ControllerAdvice |
| HTTP客户端 | httpx封装 | OpenFeign/RestTemplate |
| 异步路由 | `async def` | CompletableFuture/WebFlux |
| 生命周期 | `lifespan` | @PostConstruct/@PreDestroy |

下一章我们将进入AI三大框架实战。
