# 第二章：工程化环境

> 从零搭建Python工程化开发环境：pyenv + venv + pip + uv + pyproject.toml

---

## 2.1 Python版本管理：pyenv

pyenv 用于在同一台机器上管理多个Python版本。

```bash
# 安装pyenv（Linux/macOS）
curl https://pyenv.run | bash

# Windows推荐用 pyenv-win
# PowerShell安装
Invoke-WebRequest -UseBasicParsing -Uri "https://raw.githubusercontent.com/pyenv-win/pyenv-win/master/pyenv-win/install-pyenv-win.ps1" -OutFile "./install-pyenv-win.ps1"; &"./install-pyenv-win.ps1"

# 常用命令
pyenv install 3.12.7          # 安装指定版本
pyenv versions                # 查看已安装版本
pyenv global 3.12.7           # 设置全局版本
pyenv local 3.12.7            # 为当前项目设置版本（生成.python-version文件）

# Java类比: pyenv ≈ SDKMAN! (管理JDK版本)
```

---

## 2.2 虚拟环境：venv

每个项目使用独立的依赖环境，避免全局污染。

```bash
# 创建虚拟环境
python -m venv .venv

# 激活
# Linux/macOS:
source .venv/bin/activate
# Windows PowerShell:
.venv\Scripts\Activate.ps1

# 退出
deactivate

# 查看已安装包
pip list
pip freeze > requirements.txt  # 导出依赖

# Java类比: venv ≈ Maven局部仓库隔离，但更轻量
```

---

## 2.3 包管理：pip 与 uv

### 2.3.1 pip 基础

```bash
pip install fastapi            # 安装包
pip install fastapi==0.115.0   # 指定版本
pip install "fastapi>=0.115.0" # 版本范围
pip install -r requirements.txt # 批量安装
pip uninstall fastapi          # 卸载
pip show fastapi               # 查看包信息
pip list --outdated            # 查看过时的包
```

### 2.3.2 uv：新一代包管理器（推荐）

uv 是 Rust 编写的极速包管理器，兼容 pip 命令，速度快 10-100 倍。

```bash
# 安装uv
# Linux/macOS:
curl -LsSf https://astral.sh/uv/install.sh | sh
# Windows PowerShell:
powershell -ExecutionPolicy ByPass -c "irm https://astral.sh/uv/install.ps1 | iex"

# 常用命令
uv venv                        # 创建虚拟环境（默认 .venv）
uv venv --python 3.12          # 指定Python版本
uv pip install fastapi         # 安装包（兼容pip语法）
uv pip install -r requirements.txt
uv pip list
uv pip freeze

# uv 独有功能
uv sync                        # 根据 pyproject.toml 同步依赖（类似 mvn install）
uv add fastapi                 # 添加依赖到 pyproject.toml
uv add --dev pytest ruff       # 添加开发依赖
uv remove fastapi              # 移除依赖
uv lock                        # 生成 lock 文件（uv.lock）
uv run python main.py          # 在项目环境中运行

# Java类比: uv ≈ Maven/Gradle，但速度极快，pyproject.toml ≈ pom.xml/build.gradle
```

---

## 2.4 项目配置：pyproject.toml

`pyproject.toml` 是 Python 项目的标准配置文件（PEP 518/621），取代 `setup.py`。

```toml
# python4javaProgrammer/pyproject.toml

[project]
name = "ai-native-dev"
version = "0.1.0"
description = "Python+AI开发实战教程项目"
readme = "README.md"
requires-python = ">=3.11"
authors = [
    { name = "Your Name", email = "you@example.com" }
]
license = { text = "MIT" }

# 运行时依赖
dependencies = [
    # Web框架
    "fastapi>=0.115.0",
    "uvicorn[standard]>=0.30.0",
    # HTTP客户端
    "httpx>=0.27.0",
    # 数据校验
    "pydantic>=2.0",
    "pydantic-settings>=2.0",
    # AI框架
    "langchain>=0.3.0",
    "langchain-openai>=0.2.0",
    "langchain-community>=0.3.0",
    "langgraph>=0.2.0",
    "llama-index>=0.12.0",
    "chromadb>=0.5.0",
    # 工具库
    "python-dotenv>=1.0",
    "loguru>=0.7.0",
]

# 可选依赖（按功能分组）
[project.optional-dependencies]
dev = [
    "pytest>=8.0",
    "pytest-asyncio>=0.24",
    "pytest-cov>=5.0",
    "httpx>=0.27.0",  # 测试客户端
]
lint = [
    "ruff>=0.6.0",
    "mypy>=1.11",
]
docs = [
    "mkdocs-material>=9.0",
]

# 构建后端（现代Python推荐用hatchling）
[build-system]
requires = ["hatchling"]
build-backend = "hatchling.build"

# Ruff配置
[tool.ruff]
target-version = "py311"
line-length = 100

[tool.ruff.lint]
select = [
    "E",    # pycodestyle errors
    "W",    # pycodestyle warnings
    "F",    # pyflakes
    "I",    # isort
    "B",    # flake8-bugbear
    "C4",   # flake8-comprehensions
    "UP",   # pyupgrade
    "N",    # pep8-naming
    "SIM",  # flake8-simplify
]
ignore = ["E501"]  # 行长度由formatter处理

[tool.ruff.format]
quote-style = "double"
indent-style = "space"

# pytest配置
[tool.pytest.ini_options]
testpaths = ["tests"]
python_files = "test_*.py"
python_functions = "test_*"
asyncio_mode = "auto"
addopts = "-v --tb=short --strict-markers"

# mypy配置
[tool.mypy]
python_version = "3.11"
warn_return_any = true
warn_unused_configs = true
disallow_untyped_defs = false  # 渐进式类型检查
ignore_missing_imports = true

# hatchling构建配置
[tool.hatch.build.targets.wheel]
packages = ["src/ai_native_dev"]
```

### 2.4.1 完整项目初始化流程

```bash
# 1. 创建项目目录
mkdir ai-native-dev && cd ai-native-dev

# 2. 用uv初始化
uv init --python 3.12

# 3. 添加依赖
uv add fastapi uvicorn[standard] httpx pydantic pydantic-settings
uv add langchain langchain-openai langchain-community langgraph
uv add llama-index chromadb python-dotenv loguru
uv add --dev pytest pytest-asyncio pytest-cov ruff mypy

# 4. 生成lock文件
uv lock

# 5. 同步安装
uv sync

# Java类比: 整个流程 ≈ mvn archetype:generate + 修改pom.xml + mvn install
```

---

## 2.5 环境变量管理：.env

```python
# python4javaProgrammer/env_config_example.py

# --- 方式1: python-dotenv（简单直接） ---
# .env 文件
"""
OPENAI_API_KEY=sk-xxxx
DATABASE_URL=postgresql://localhost:5432/mydb
DEBUG=true
APP_ENV=development
"""
from dotenv import load_dotenv
import os

load_dotenv()  # 加载 .env 文件
api_key = os.getenv("OPENAI_API_KEY")
debug = os.getenv("DEBUG", "false").lower() == "true"

# --- 方式2: pydantic-settings（推荐，类型安全） ---
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    """应用配置，自动从环境变量/.env文件加载"""
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
    )

    # 应用配置
    app_name: str = "AI Native Dev"
    debug: bool = False
    app_env: str = "development"

    # OpenAI
    openai_api_key: str
    openai_model: str = "gpt-4o-mini"

    # 数据库
    database_url: str = "postgresql://localhost:5432/mydb"

    # Redis
    redis_url: str = "redis://localhost:6379/0"

    # ChromaDB
    chroma_persist_dir: str = "./data/chroma"

settings = Settings()
print(f"App: {settings.app_name}")
print(f"Model: {settings.openai_model}")
print(f"Debug: {settings.debug}")

# 方式2优势: 类型校验 + 默认值 + IDE自动补全 + 环境隔离
# Java类比: pydantic-settings ≈ @ConfigurationProperties (Spring Boot)
```

---

## 2.6 多版本Python与CI环境

```yaml
# .github/workflows/ci.yml 示例（仅供参考）
name: CI

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        python-version: ["3.11", "3.12"]
    steps:
      - uses: actions/checkout@v4
      - name: Install uv
        uses: astral-sh/setup-uv@v3
      - name: Set up Python
        run: uv python install ${{ matrix.python-version }}
      - name: Install dependencies
        run: uv sync --extra dev
      - name: Lint
        run: uv run ruff check .
      - name: Type check
        run: uv run mypy src/
      - name: Test
        run: uv run pytest --cov=src
```

---

## 本章小结

| 工具 | 用途 | Java类比 |
|------|------|---------|
| pyenv | Python版本管理 | SDKMAN! |
| venv | 虚拟环境 | Maven局部仓库 |
| pip | 包安装 | — |
| uv | 极速包管理 | Maven/Gradle |
| pyproject.toml | 项目配置 | pom.xml |
| pydantic-settings | 配置管理 | @ConfigurationProperties |
| .env | 环境变量 | application.yml |

下一章我们将设计标准化的项目结构与开发规范。
