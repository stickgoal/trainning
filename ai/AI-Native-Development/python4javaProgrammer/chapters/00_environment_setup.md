# 第零章：环境搭建（安装与国内配置）

> 目标：从一台全新电脑开始，把本课程所有代码跑起来。
> 覆盖：Python 安装、pip/uv 国内镜像、课程依赖安装、HuggingFace 镜像、国内可直接调用的 LLM API 配置。
>
> Java 类比：本章 ≈ JDK 安装 + Maven settings.xml 配置阿里云镜像。
>
> **阅读顺序提示**：本章是"装好能跑"的操作手册；第二章《工程化环境》是"为什么这样设计"的原理讲解。先跑通本章，再读第二章效果最佳。

---

## 0.1 你需要准备什么

| 事项 | 要求 |
|------|------|
| 操作系统 | Windows 10/11、macOS 或主流 Linux 均可 |
| Python 版本 | **3.11 或 3.12**（课程要求 `>=3.11`，3.12 已验证） |
| 磁盘空间 | 至少 10 GB（torch + 向量库等依赖较大） |
| 网络 | 能访问国内镜像源（清华/阿里）；LLM 调用需要一个大模型 API Key |

---

## 0.2 安装 Python

### 方式一：直接安装（最简单，推荐新手）

官网下载慢，用国内镜像：

- 淘宝镜像：https://registry.npmmirror.com/binary.html?path=python/3.12.7/
- 华为镜像：https://mirrors.huaweicloud.com/python/3.12.7/

**Windows**：下载 `python-3.12.7-amd64.exe`，安装时**务必勾选 "Add python.exe to PATH"**。

**macOS**：下载 `.pkg` 安装包，或用 Homebrew：

```bash
brew install python@3.12
```

**Linux（Ubuntu/Debian）**：

```bash
sudo apt update
sudo apt install python3.12 python3.12-venv python3-pip
```

> 注意：Ubuntu 22.04 以前的系统源里没有 3.12，可用 deadsnakes PPA 或改用下面的 pyenv 方式。

### 方式二：pyenv 多版本管理（推荐长期使用）

参考第二章 2.1 节。Windows 用 pyenv-win：

```powershell
# PowerShell 安装 pyenv-win
Invoke-WebRequest -UseBasicParsing -Uri "https://raw.githubusercontent.com/pyenv-win/pyenv-win/master/pyenv-win/install-pyenv-win.ps1" -OutFile "./install-pyenv-win.ps1"; &"./install-pyenv-win.ps1"

# 重开终端后
pyenv install 3.12.7
pyenv global 3.12.7
```

> GitHub 下载慢的话，改用方式一。

### 验证安装

```bash
python --version   # 输出 Python 3.12.x
pip --version
```

Windows 上如果提示找不到命令，检查环境变量 PATH 是否包含 Python 安装目录和 `Scripts` 子目录。

---

## 0.3 pip 国内镜像配置（必做）

默认源在国外，速度极慢甚至超时。配置清华 TUNA 镜像：

### 一次性配置（永久生效，推荐）

**Windows PowerShell**：

```powershell
pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple
```

**Linux/macOS**：

```bash
pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple
```

配置写入 `~/.pip/pip.conf`（Linux/macOS）或 `%APPDATA%\pip\pip.ini`（Windows）。

### 临时使用（不改全局配置）

```bash
pip install fastapi -i https://pypi.tuna.tsinghua.edu.cn/simple
```

### 常用国内镜像备选

| 镜像 | 地址 |
|------|------|
| 清华 TUNA | `https://pypi.tuna.tsinghua.edu.cn/simple` |
| 阿里云 | `https://mirrors.aliyun.com/pypi/simple/` |
| 腾讯云 | `https://mirrors.cloud.tencent.com/pypi/simple` |
| 中科大 | `https://mirrors.ustc.edu.cn/pypi/simple` |

> Java 类比：等价于在 Maven 的 `settings.xml` 里配 `<mirror>aliyunmaven</mirror>`。

---

## 0.4 安装 uv 并配置镜像（推荐）

uv 是 Rust 编写的包管理器，装大依赖（torch、chromadb）时快一个数量级，强烈推荐。

### 安装

```bash
# Windows PowerShell：
powershell -ExecutionPolicy ByPass -c "irm https://astral.sh/uv/install.ps1 | iex"

# Linux/macOS：
curl -LsSf https://astral.sh/uv/install.sh | sh
```

> astral.sh 访问慢可用 pip 方式安装：`pip install uv`

### 配置国内镜像

uv 不读 pip 的配置，需要单独设置（环境变量方式，写入你的 shell 配置文件）：

**Windows PowerShell**（永久）：

```powershell
[Environment]::SetEnvironmentVariable("UV_DEFAULT_INDEX", "https://pypi.tuna.tsinghua.edu.cn/simple", "User")
```

**Linux/macOS**（追加到 `~/.bashrc` 或 `~/.zshrc`）：

```bash
export UV_DEFAULT_INDEX=https://pypi.tuna.tsinghua.edu.cn/simple
```

---

## 0.5 创建虚拟环境并安装课程依赖

在仓库根目录（`AI-Native-Development/`）执行：

```bash
# 1. 创建虚拟环境（任选其一）
python -m venv .venv
# 或 uv venv --python 3.12

# 2. 激活虚拟环境
# Windows PowerShell:
.venv\Scripts\Activate.ps1
# Windows Git Bash:
source .venv/Scripts/activate
# Linux/macOS:
source .venv/bin/activate

# 3. 安装课程依赖（任选其一）
pip install -r requirements.txt
# 或（更快，推荐）：
uv pip install -r requirements.txt
```

> **Windows PowerShell 报错"禁止运行脚本"？** 执行一次：
> ```powershell
> Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
> ```

### 大依赖安装说明

| 包 | 大小 | 备注 |
|------|------|------|
| `sentence-transformers` | 会连带安装 torch（约 2.5 GB） | 安装最慢的一个，耐心等待；uv 安装明显更快 |
| `chromadb` | ~100 MB | 需要 SQLite >= 3.35，Python 3.11+ 自带的版本满足，无需额外处理 |
| `faiss-cpu` | ~30 MB | Windows 有预编译 wheel，直接装即可 |
| `unstructured` | 较多系统依赖 | 仅文档加载示例用，装不上可暂时跳过 |

torch 想走国内源更快的话（CPU 版即可，本课程不要求 GPU）：

```bash
uv pip install torch --index-url https://download.pytorch.org/whl/cpu
```

---

## 0.6 HuggingFace 国内镜像（sentence-transformers 模型下载必配）

`sentence-transformers` 首次运行时会从 HuggingFace 下载 embedding 模型（如 `BAAI/bge-small-zh-v1.5`），国内直连基本失败。配置镜像：

**Windows PowerShell**（永久）：

```powershell
[Environment]::SetEnvironmentVariable("HF_ENDPOINT", "https://hf-mirror.com", "User")
```

**Linux/macOS**（追加到 `~/.bashrc` 或 `~/.zshrc`）：

```bash
export HF_ENDPOINT=https://hf-mirror.com
```

配置后**重开终端**（或重新激活 venv），再运行课程代码即可正常下载模型。

也可以在代码里最前面加（不推荐，仅临时救急）：

```python
import os
os.environ["HF_ENDPOINT"] = "https://hf-mirror.com"  # 必须在 import sentence_transformers 之前
```

> 或者彻底离线：在 hf-mirror.com 手动下载模型到本地目录，加载时传 `model_name="./models/bge-small-zh-v1.5"`。

---

## 0.7 LLM API 国内配置（跑通 AI 代码的关键）

课程代码统一通过 `OPENAI_BASE_URL` + `OPENAI_API_KEY`（OpenAI 兼容协议）访问大模型。**不需要 OpenAI 官方账号**，以下国内服务都是 OpenAI 兼容接口，任选一家注册充值即可：

| 服务商 | Base URL | 推荐模型 | 说明 |
|--------|----------|----------|------|
| DeepSeek | `https://api.deepseek.com/v1` | `deepseek-chat` | 便宜、注册即用，课程首选 |
| 阿里云百炼（通义） | `https://dashscope.aliyuncs.com/compatible-mode/v1` | `qwen-plus` / `qwen-turbo` | 阿里生态，新用户有免费额度 |
| 智谱 AI | `https://open.bigmodel.cn/api/paas/v4` | `glm-4-flash` | flash 模型免费 |
| 硅基流动 SiliconFlow | `https://api.siliconflow.cn/v1` | `Qwen/Qwen2.5-7B-Instruct` | 模型多，聚合平台 |

### 配置 .env 文件

在项目根目录创建 `.env`（已被 .gitignore 忽略，不会误提交）：

```ini
# 以 DeepSeek 为例
OPENAI_API_KEY=sk-你的key
OPENAI_BASE_URL=https://api.deepseek.com/v1
OPENAI_MODEL=deepseek-chat
```

用通义的话改成：

```ini
OPENAI_API_KEY=sk-你的百炼key
OPENAI_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
OPENAI_MODEL=qwen-plus
```

课程代码中的 `settings.openai_base_url`、`ChatOpenAI(base_url=..., api_key=...)` 都会从这两个变量读取，切换服务商只改 `.env`，代码零改动。

### 获取 API Key

- **DeepSeek**：https://platform.deepseek.com → 注册 → 左侧"API Keys" → 创建
- **通义百炼**：https://bailian.console.aliyun.com → 开通 → API-KEY 管理
- **智谱**：https://open.bigmodel.cn → 注册 → API Keys

---

## 0.8 验证环境：最小冒烟测试

### 测试 1：依赖导入

```bash
python -c "import fastapi, chromadb, langchain, openai; print('基础依赖 OK')"
```

### 测试 2：LLM 连通性

新建 `test_llm.py`：

```python
import os
from dotenv import load_dotenv
from openai import OpenAI

load_dotenv()

client = OpenAI(
    api_key=os.getenv("OPENAI_API_KEY"),
    base_url=os.getenv("OPENAI_BASE_URL"),
)

resp = client.chat.completions.create(
    model=os.getenv("OPENAI_MODEL", "deepseek-chat"),
    messages=[{"role": "user", "content": "你好，请回复：环境OK"}],
)
print(resp.choices[0].message.content)
```

```bash
pip install python-dotenv   # 若尚未安装
python test_llm.py          # 输出"环境OK"即成功
```

### 测试 3：embedding 模型下载（RAG 章节）

```python
from sentence_transformers import SentenceTransformer

model = SentenceTransformer("BAAI/bge-small-zh-v1.5")  # 首次运行走 hf-mirror 下载
vec = model.encode(["测试文本"])
print(f"向量维度: {vec.shape}")   # 输出 (1, 512) 即成功
```

三个测试都通过，全课程代码即可运行。

---

## 0.9 常见问题排查

| 现象 | 原因与解法 |
|------|-----------|
| `pip install` 卡住/超时 | 未配镜像（0.3 节）或公司网络代理，先 `pip config list` 检查 |
| torch 安装极慢 | 改用 uv + 镜像（0.4/0.5 节），或单独指定 CPU 版源 |
| `huggingface.co` 连接超时 | 未配 `HF_ENDPOINT`（0.6 节），配完重开终端 |
| `openai.AuthenticationError` | `.env` 里 key 写错 / 没加引号外的空格 / 没在项目根目录运行（`load_dotenv()` 找不到文件） |
| `openai.NotFoundError` (404) | `OPENAI_MODEL` 名称与服务商不匹配，对照 0.7 节表格 |
| PowerShell 无法激活 venv | 执行策略限制，见 0.5 节提示 |
| chromadb 报 sqlite 版本错误 | Python 版本 < 3.11，升级 Python |
| 换了服务商代码报错 | 确认 `.env` 的三个变量都改了（KEY、BASE_URL、MODEL），且重启了运行中的服务 |

---

## 本章小结

```
环境搭建五步走：
1. Python 3.12        —— 官网/国内镜像直接装
2. pip/uv 镜像        —— 清华源，一次配置永久生效
3. venv + 依赖        —— 激活后 pip/uv install -r requirements.txt
4. HF 镜像            —— HF_ENDPOINT=https://hf-mirror.com
5. .env 配 LLM        —— DeepSeek/通义/智谱，OpenAI 兼容协议，改配置不改代码
```

环境就绪后，进入第一章《Python 语法（精要速览 + 完整语法体系）》开始学习。
