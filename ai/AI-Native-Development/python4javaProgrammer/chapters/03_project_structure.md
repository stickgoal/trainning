# 第三章：项目结构与开发规范

> 标准化目录结构 + 日志 + 配置管理 + 测试 + 代码质量工具

---

## 3.1 PEP 8 标准目录结构

```
ai-native-dev/
├── pyproject.toml          # 项目配置（依赖、工具配置）
├── uv.lock                 # 依赖锁定文件
├── .env                    # 环境变量（不提交到git）
├── .env.example            # 环境变量模板
├── .gitignore
├── README.md
├── Dockerfile
├── docker-compose.yml
├── Makefile                # 常用命令快捷方式
│
├── src/                    # 源代码根目录
│   └── ai_native_dev/      # Python包
│       ├── __init__.py
│       ├── main.py         # 应用入口
│       ├── config.py       # 配置管理
│       │
│       ├── api/            # API层（路由、中间件）
│       │   ├── __init__.py
│       │   ├── deps.py     # 依赖注入
│       │   ├── middleware.py
│       │   └── routes/
│       │       ├── __init__.py
│       │       ├── chat.py
│       │       ├── documents.py
│       │       └── health.py
│       │
│       ├── core/           # 核心基础设施
│       │   ├── __init__.py
│       │   ├── exceptions.py
│       │   ├── logging.py
│       │   └── settings.py
│       │
│       ├── models/         # 数据模型（Pydantic / SQLAlchemy）
│       │   ├── __init__.py
│       │   ├── base.py
│       │   ├── chat.py
│       │   └── document.py
│       │
│       ├── services/       # 业务逻辑层
│       │   ├── __init__.py
│       │   ├── rag_service.py
│       │   ├── agent_service.py
│       │   └── document_service.py
│       │
│       ├── ai/             # AI组件
│       │   ├── __init__.py
│       │   ├── chains/     # LangChain链
│       │   ├── graphs/     # LangGraph图
│       │   ├── indexes/    # LlamaIndex索引
│       │   └── tools/      # 自定义工具
│       │
│       └── utils/          # 工具函数
│           ├── __init__.py
│           └── http_client.py
│
├── tests/                  # 测试目录
│   ├── __init__.py
│   ├── conftest.py         # pytest fixtures
│   ├── unit/
│   │   ├── test_models.py
│   │   └── test_services.py
│   ├── integration/
│   │   ├── test_api.py
│   │   └── test_ai.py
│   └── fixtures/           # 测试数据
│       └── sample.pdf
│
├── data/                   # 数据目录
│   ├── uploads/            # 上传文件
│   └── chroma/             # ChromaDB持久化
│
├── scripts/                # 脚本
│   ├── start.sh
│   └── start.ps1
│
└── docs/                   # 文档
    └── architecture.md
```

> **Java类比**：`src/ai_native_dev/` ≈ `src/main/java/com/company/project/`，`tests/` ≈ `src/test/java/`

---

## 3.2 日志管理：loguru

```python
# src/ai_native_dev/core/logging.py
import sys
from loguru import logger
from ..core.settings import settings

def setup_logging() -> None:
    """配置日志系统"""
    # 移除默认handler
    logger.remove()

    # 控制台输出（开发环境彩色）
    logger.add(
        sys.stdout,
        level="DEBUG" if settings.debug else "INFO",
        format=(
            "<green>{time:YYYY-MM-DD HH:mm:ss.SSS}</green> | "
            "<level>{level: <8}</level> | "
            "<cyan>{name}</cyan>:<cyan>{function}</cyan>:<cyan>{line}</cyan> | "
            "<level>{message}</level>"
        ),
        colorize=True,
    )

    # 文件输出（按天轮转，保留7天）
    logger.add(
        "logs/app_{time:YYYY-MM-DD}.log",
        level="INFO",
        rotation="00:00",       # 每天轮转
        retention="7 days",     # 保留7天
        compression="zip",      # 压缩旧日志
        format="{time:YYYY-MM-DD HH:mm:ss.SSS} | {level: <8} | {name}:{function}:{line} | {message}",
        encoding="utf-8",
    )

    # 错误日志单独文件
    logger.add(
        "logs/error_{time:YYYY-MM-DD}.log",
        level="ERROR",
        rotation="00:00",
        retention="30 days",
        compression="zip",
        encoding="utf-8",
    )

# 使用示例
# from loguru import logger
# logger.info("用户登录: user_id={}", user_id)
# logger.error("数据库连接失败", exc_info=True)
# logger.debug("请求参数: {}", payload)
```

```python
# src/ai_native_dev/core/settings.py
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    # 应用
    app_name: str = "AI Native Dev"
    debug: bool = False
    app_env: str = "development"

    # OpenAI
    openai_api_key: str = ""
    openai_base_url: str = "https://api.openai.com/v1"
    openai_model: str = "gpt-4o-mini"
    embedding_model: str = "text-embedding-3-small"

    # 数据库
    database_url: str = "postgresql://localhost:5432/mydb"

    # ChromaDB
    chroma_persist_dir: str = "./data/chroma"

    # 上传
    upload_dir: str = "./data/uploads"
    max_upload_size: int = 10_485_760  # 10MB

settings = Settings()
```

---

## 3.3 配置管理

```python
# src/ai_native_dev/config.py
"""统一配置入口"""
from .core.settings import settings

# 导出配置单例
__all__ = ["settings"]

# 使用方式（任何模块中）:
# from ai_native_dev.config import settings
# print(settings.openai_api_key)
```

```ini
# .env.example
# 复制此文件为.env并填入实际值

# 应用配置
APP_NAME=AI Native Dev
DEBUG=true
APP_ENV=development

# OpenAI
OPENAI_API_KEY=sk-your-api-key-here
OPENAI_BASE_URL=https://api.openai.com/v1
OPENAI_MODEL=gpt-4o-mini
EMBEDDING_MODEL=text-embedding-3-small

# ChromaDB
CHROMA_PERSIST_DIR=./data/chroma

# 上传配置
UPLOAD_DIR=./data/uploads
MAX_UPLOAD_SIZE=10485760
```

---

## 3.4 pytest 单元测试

```python
# tests/conftest.py
"""pytest全局fixtures"""
import pytest
from httpx import AsyncClient
from ai_native_dev.main import app

@pytest.fixture
async def client():
    """异步测试客户端"""
    async with AsyncClient(app=app, base_url="http://test") as ac:
        yield ac

@pytest.fixture
def sample_document():
    """测试文档"""
    return {
        "title": "Python教程",
        "content": "Python是一种解释型、高级编程语言。",
        "metadata": {"source": "test", "author": "tester"},
    }

@pytest.fixture
def mock_openai_response():
    """模拟OpenAI响应"""
    return {
        "id": "chatcmpl-test123",
        "choices": [{
            "message": {
                "role": "assistant",
                "content": "这是一个测试回复。",
            },
            "finish_reason": "stop",
        }],
    }
```

```python
# tests/unit/test_models.py
"""数据模型测试"""
import pytest
from pydantic import ValidationError
from ai_native_dev.models.chat import ChatRequest, ChatResponse

class TestChatRequest:
    def test_valid_request(self):
        req = ChatRequest(
            message="你好",
            session_id="test-001",
        )
        assert req.message == "你好"
        assert req.session_id == "test-001"
        assert req.temperature == 0.7  # 默认值

    def test_empty_message_raises(self):
        with pytest.raises(ValidationError) as exc_info:
            ChatRequest(message="", session_id="test-001")
        assert "message" in str(exc_info.value)

    def test_temperature_range(self):
        with pytest.raises(ValidationError):
            ChatRequest(message="test", session_id="s1", temperature=2.5)
        with pytest.raises(ValidationError):
            ChatRequest(message="test", session_id="s1", temperature=-0.5)

    def test_optional_fields(self):
        req = ChatRequest(message="test", session_id="s1")
        assert req.history == []  # 默认空列表
        assert req.stream is False
```

```python
# tests/unit/test_services.py
"""业务逻辑测试"""
import pytest
from unittest.mock import AsyncMock, MagicMock, patch
from ai_native_dev.services.rag_service import RagService

class TestRagService:
    @pytest.fixture
    def rag_service(self):
        return RagService()

    @patch("ai_native_dev.services.rag_service.get_llm")
    async def test_query_returns_response(self, mock_get_llm, rag_service):
        mock_llm = AsyncMock()
        mock_llm.ainvoke.return_value = MagicMock(content="测试答案")
        mock_get_llm.return_value = mock_llm

        result = await rag_service.query("什么是Python?")
        assert "测试答案" in result.answer
        assert result.sources is not None

    async def test_query_empty_raises(self, rag_service):
        with pytest.raises(ValueError, match="查询不能为空"):
            await rag_service.query("")
```

```python
# tests/integration/test_api.py
"""API集成测试"""
import pytest

class TestHealthEndpoint:
    async def test_health_check(self, client):
        response = await client.get("/health")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "healthy"

class TestChatEndpoint:
    async def test_chat_success(self, client):
        response = await client.post(
            "/api/v1/chat",
            json={"message": "你好", "session_id": "test-001"},
        )
        assert response.status_code == 200
        data = response.json()
        assert "answer" in data
        assert "session_id" in data

    async def test_chat_empty_message(self, client):
        response = await client.post(
            "/api/v1/chat",
            json={"message": "", "session_id": "test-001"},
        )
        assert response.status_code == 422  # Pydantic校验失败
```

运行测试：

```bash
# 运行所有测试
uv run pytest

# 运行指定目录
uv run pytest tests/unit/

# 带覆盖率
uv run pytest --cov=src --cov-report=html

# 只运行失败的测试
uv run pytest --lf

# 并行运行（安装pytest-xdist）
uv run pytest -n auto

# 显示打印输出
uv run pytest -s

# 详细模式
uv run pytest -vv
```

---

## 3.5 代码质量：ruff + mypy

### 3.5.1 ruff：极速Linter + Formatter

```bash
# 检查代码
uv run ruff check .

# 自动修复
uv run ruff check . --fix

# 格式化代码
uv run ruff format .

# 检查特定规则
uv run ruff check . --select E,W,F,I,B

# 排除目录
# 在 pyproject.toml 中配置:
# [tool.ruff]
# exclude = ["data", "scripts", ".venv"]
```

### 3.5.2 mypy：静态类型检查

```bash
# 类型检查
uv run mypy src/

# 严格模式
uv run mypy --strict src/

# 忽略缺失导入
uv run mypy --ignore-missing-imports src/
```

### 3.5.3 pre-commit 钩子

```yaml
# .pre-commit-config.yaml
repos:
  - repo: https://github.com/astral-sh/ruff-pre-commit
    rev: v0.6.0
    hooks:
      - id: ruff
        args: [--fix]
      - id: ruff-format
  - repo: https://github.com/pre-commit/mirrors-mypy
    rev: v1.11.0
    hooks:
      - id: mypy
        additional_dependencies: [pydantic, pydantic-settings]
```

```bash
# 安装pre-commit
uv run pre-commit install

# 手动运行
uv run pre-commit run --all-files
```

---

## 3.6 Makefile：常用命令

```makefile
# Makefile
.PHONY: install dev test lint format type-check clean run

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

clean:
	rm -rf .pytest_cache .ruff_cache .mypy_cache htmlcov logs/*.log
	find . -type d -name __pycache__ -exec rm -rf {} +
```

```bash
make install    # 安装依赖
make dev        # 安装开发依赖
make run        # 启动服务
make test       # 运行测试
make lint       # 代码检查
make format     # 格式化
make type-check # 类型检查
make clean      # 清理缓存
```

---

## 本章小结

| 工具 | 用途 | Java类比 |
|------|------|---------|
| PEP 8目录结构 | 项目组织 | Maven标准目录 |
| loguru | 日志 | Logback/SLF4J |
| pydantic-settings | 配置管理 | @ConfigurationProperties |
| pytest | 单元测试 | JUnit 5 |
| ruff | Linter + Formatter | Checkstyle + Spotless |
| mypy | 类型检查 | 编译器本身 |
| pre-commit | Git钩子 | Git hooks |
| Makefile | 命令快捷方式 | Maven goals |

下一章我们将深入FastAPI Web框架。
