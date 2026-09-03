# 第四章：FastAPI —— 从一个空目录到一个可测试的任务 API

> 本章不讲"FastAPI 有这些功能"，而是**陪你从零把一个项目建起来**。
> 每一步都会回答三个问题：**为什么要有这一层** / **代码为什么这样写** / **换成 Java 你熟悉的东西是什么**。

---

## 本章导读

### 我们要做什么

构建一个叫 **TaskFlow** 的任务管理 API。它不复杂，但五脏俱全：

| 能力 | 涉及的 FastAPI 知识点 |
|------|----------------------|
| 创建 / 查询 / 更新 / 删除任务 | 路由、路径参数、请求体、状态码 |
| 分页 + 多条件筛选 + 排序 | `Query` 参数校验、查询对象 |
| 状态流转（待办 → 进行中 → 已完成） | 状态机、409 冲突 |
| CSV 批量导入 | 文件上传、`BackgroundTasks` |
| 流式导出 | `StreamingResponse`（SSE） |
| 统一错误格式、请求 ID、限流 | 异常处理器、中间件 |
| 85 个自动化测试 | `TestClient`、依赖覆写、夹具 |

### 为什么选"任务管理"而不是 todo 增删改查

因为纯 CRUD 暴露不出分层架构的价值——Service 层会退化成"直接调 Repository"的转发代码，你会觉得"多这一层干嘛"。加了**状态机**和**跨服务校验**之后，业务规则有了真正的家，分层才有意义。

### 路线图（本章的推进顺序）

```
4.1  搭项目      ：最小应用跑起来 → 工程化目录 → pyproject.toml
4.2  模型层      ：Pydantic —— FastAPI 靠类型注解干活，模型就是契约
4.3  仓储层      ：Protocol 接口 + 内存实现 —— 把"数据从哪来"隔离出去
4.4  服务层      ：业务规则的家 —— 不依赖 FastAPI，可单测
4.5  依赖注入    ：把上面三层接起来的"接线方式"
4.6  路由层      ：HTTP 世界 ↔ 业务世界的翻译器
4.7  错误处理    ：业务异常 → 统一 JSON
4.8  中间件/日志 ：横切关注点
4.9  配置管理    ：12-factor，环境变量优先
4.10 装配        ：create_app() 工厂 + lifespan 生命周期
4.11 运行验证    ：uvicorn 启动，curl 打真实请求（有真实输出）
4.12 测试        ：单元测试 + 集成测试（有真实覆盖率）
4.13 生产拓展    ：本章没做但上线必须做的事
```

### 最终目录结构

```
taskflow/
├── pyproject.toml              # 项目元数据 + 依赖 + pytest/ruff/mypy 配置
├── README.md                   # 必须有：pyproject 里 readme 字段指向它
├── .env.example                # 配置模板（进版本库）
├── .env                        # 本地环境变量（不进版本库，需自己从模板复制）
├── .gitignore
├── src/taskflow/               # ← src 布局：包在 src/ 下，防止"没安装也能 import"的假象
    ├── __init__.py
    ├── main.py                 # 应用工厂 + lifespan（Spring Boot 启动类）
    ├── core/                   # 基础设施
    │   ├── settings.py         # 配置（@ConfigurationProperties）
    │   ├── logging.py          # 日志（logback 配置）
    │   └── exceptions.py       # 异常体系 + handler（@ControllerAdvice）
    ├── models/                 # 数据模型（DTO / Entity / VO）
    │   ├── base.py             # ApiResponse<T>、Page<T>
    │   └── task.py             # TaskCreate / TaskRecord / TaskRead ...
    ├── repositories/           # 数据访问（@Repository）
    │   └── task_repo.py        # TaskRepository 接口 + InMemoryTaskRepository
    ├── services/               # 业务逻辑（@Service）
    │   ├── task_service.py
    │   └── user_service.py     # 调用外部服务
    ├── api/                    # HTTP 层（@RestController）
    │   ├── deps.py             # 依赖注入声明（@Bean）
    │   ├── middleware.py       # 中间件（Filter / Interceptor）
    │   └── routes/
    │       ├── health.py
    │       └── tasks.py
    └── utils/
        └── http_client.py      # httpx 封装（OpenFeign + Resilience4j）
└── tests/
    ├── conftest.py             # 公共夹具
    ├── unit/                   # 不启 HTTP，直接调 Service
    └── integration/            # 走完整 HTTP 链路
```

**完整代码在 `python4javaProgrammer/taskflow/` 目录下**，本章所有代码块都是从中摘出来的，可以直接跑。

---

## 4.1 第一步：把项目建起来

### 4.1.1 先看最小应用长什么样

在讲目录规范之前，先建立一个直观印象。**一个能用的 FastAPI 应用只需要 8 行**：

```python
# main.py
from fastapi import FastAPI

app = FastAPI()

@app.get("/")
async def root() -> dict[str, str]:
    return {"message": "Hello TaskFlow"}
```

启动它：

```bash
python -m uvicorn main:app --reload
```

打开 <http://127.0.0.1:8000/docs>，一个可交互的 Swagger 页面已经躺在那里了。

这段代码里有四个必须理解的点：

**① `app = FastAPI()` 创建的是 ASGI 应用**

`FastAPI` 类本身不监听端口、不处理 TCP。它只是实现了一个约定：

```python
async def app(scope, receive, send): ...
```

这就是 **ASGI**（Asynchronous Server Gateway Interface）协议。`uvicorn` 才是真正监听端口、解析 HTTP、调用这个函数的服务器。

> **Java 类比**：`FastAPI()` ≈ `Servlet 容器里的 DispatcherServlet`；`uvicorn` ≈ `Tomcat`。
> 你写的业务代码从不直接接触 Socket，容器负责把 HTTP 报文翻译成对象再交给你。
>
> 区别在于：Java 的 Servlet 规范是**同步阻塞**的（一个请求一个线程），ASGI 是**异步**的（一个线程用事件循环处理成千上万连接）。这就是 Python Web 框架用 `async def` 的原因——IO 等待期间线程可以去干别的。

**② `@app.get("/")` 是路由注册**

装饰器把函数登记到路由表：`GET /` → `root()`。函数返回值自动序列化成 JSON。

> **Java 类比**：`@GetMapping("/")` + `@ResponseBody`。

**③ 返回类型注解 `-> dict[str, str]` 不是摆设**

FastAPI 的核心机制就建立在这上面：它**读取函数签名**（参数的类型注解 + 返回类型注解），然后自动完成三件事：

1. 从 HTTP 请求里**提取**参数并**校验**类型；
2. 把返回值**序列化**成 JSON；
3. 生成 **OpenAPI 文档**（就是 `/docs` 那个页面）。

> **Java 类比**：Spring MVC 用 `@RequestParam`、`@Valid`、`@ResponseBody` 这些**注解**表达同样的信息。
> FastAPI 用的是 **Python 原生的类型提示**，没有额外发明一套注解体系。
> 这是 Python 生态的典型风格：能复用语言特性就不造轮子。
>
> 代价是：类型注解在 Python 里默认是"提示"，运行时不强制。但 FastAPI 会在运行时**真的按注解去校验**——错了就返回 422。

**④ `async def` 和 `def` 的区别（新手最容易踩）**

| 写法 | FastAPI 的行为 | 适用场景 |
|------|---------------|---------|
| `async def` | 在**事件循环**里直接执行 | 内部有 `await`（数据库查询、HTTP 调用） |
| `def` | 丢进**线程池**执行 | 内部是阻塞的同步代码（老库、CPU 密集） |

关键结论：**`async def` 里不能有阻塞调用**。比如你写了 `async def` 却在里面用 `requests.get()`，整个事件循环会被卡住——所有请求一起变慢。

> **Java 类比**：这就像在 Netty 的 EventLoop 线程里执行 `Thread.sleep()`。犯的是同一类错误，只是 Python 这边更隐蔽。

### 4.1.2 用 uv 初始化项目

第二章讲过 `uv`，这里直接用：

```bash
# 创建项目骨架（uv 会生成 pyproject.toml / .gitignore / .python-version）
uv init taskflow --package
cd taskflow

# 加依赖
uv add fastapi uvicorn[standard] pydantic pydantic-settings httpx loguru python-multipart

# 加开发依赖（测试与静态检查，生产镜像不装）
uv add --dev pytest pytest-cov ruff mypy
```

没有 `uv` 也完全可以：

```bash
python -m venv .venv
.venv/Scripts/activate          # Windows
# source .venv/bin/activate     # macOS / Linux
pip install "fastapi>=0.115" "uvicorn[standard]>=0.30" "pydantic>=2.7" \
            "pydantic-settings>=2.3" "httpx>=0.27" "loguru>=0.7" "python-multipart>=0.0.9"
pip install pytest pytest-cov ruff mypy
```

> **uv 做了什么**：`uv add` 会解析依赖树、生成/更新 `uv.lock`（锁定每个包的精确版本，类似 `package-lock.json` 或 Maven 的 dependencyManagement），并把依赖写进 `pyproject.toml`。
> `uv.lock` **必须提交到版本库**——它是"在我机器上能跑"的唯一保证。

### 4.1.3 pyproject.toml 逐段解释

这是本项目的 `pyproject.toml`（`taskflow/pyproject.toml`），每一行都有存在的理由：

```toml
[project]
name = "taskflow"
version = "0.1.0"
description = "FastAPI 教学项目：团队协作任务 API"
readme = "README.md"
requires-python = ">=3.11"

# 运行时依赖：只列"直接用到的"，传递依赖交给 uv.lock / pip 解析
dependencies = [
    "fastapi>=0.115",
    "uvicorn[standard]>=0.30",
    "pydantic>=2.7",
    "pydantic-settings>=2.3",
    "httpx>=0.27",
    "loguru>=0.7",
    "python-multipart>=0.0.9",   # 文件上传（multipart/form-data）需要
]

# 开发依赖：测试与代码质量（生产镜像不装）
[project.optional-dependencies]
dev = [
    "pytest>=8.0",
    "pytest-cov>=5.0",
    "ruff>=0.6",
    "mypy>=1.11",
]
```

**为什么每个包都在：**

| 包 | 作用 | 没有它会怎样 |
|----|------|------------|
| `fastapi` | 框架本体 | — |
| `uvicorn[standard]` | ASGI 服务器；`[standard]` 额外装 uvloop、httptools 等加速组件 | 只有 `uvicorn` 也能跑，性能低一些 |
| `pydantic` | 数据校验与序列化 | FastAPI 强依赖，装 fastapi 会自动带上；显式声明是好习惯 |
| `pydantic-settings` | 从环境变量 / `.env` 读配置 | 得手写 `os.getenv()` + 类型转换 |
| `httpx` | 异步 HTTP 客户端 | 调用外部 API 要用；`requests` 是同步的，不能在 `async def` 里用 |
| `loguru` | 日志 | 用标准库 `logging` 也行，但要写二十行配置代码 |
| `python-multipart` | 解析 `multipart/form-data` | 文件上传接口直接报 `Form data requires python-multipart` |

继续看构建与工具配置：

```toml
[build-system]
requires = ["hatchling"]
build-backend = "hatchling.build"

# src 布局：告诉构建后端包在 src/ 下
[tool.hatch.build.targets.wheel]
packages = ["src/taskflow"]

[tool.pytest.ini_options]
# 关键：让 pytest 能直接 import taskflow，无需先 pip install -e .
pythonpath = ["src"]
testpaths = ["tests"]
python_files = "test_*.py"
python_functions = "test_*"
asyncio_mode = "auto"
asyncio_default_fixture_loop_scope = "function"
addopts = "-v --tb=short"
```

三个关键点：

**① `packages = ["src/taskflow"]`** —— 因为用了 src 布局，构建后端不知道包在哪，必须显式告诉它。忘了这行，打包出来的 wheel 是空的（能装，但 `import taskflow` 报错）。

**② `pythonpath = ["src"]`** —— pytest 的搜索路径。有了它，测试直接跑源码，**不用先 `pip install -e .`**。
> 不写这行就得先装：源码改一行，要么重装，要么靠 editable install 的软链。教学项目里 `pythonpath` 更省事。

**③ `asyncio_mode = "auto"`** —— 让 pytest 自动识别 `async def test_xxx()`。否则你得给每个异步测试打 `@pytest.mark.asyncio` 标记。

### 4.1.4 为什么用 src 布局

```
taskflow/
├── src/taskflow/    ← 包在这里
└── tests/
```

而不是：

```
taskflow/
├── taskflow/        ← 包在项目根
└── tests/
```

原因很实在：**根布局下，你在项目根目录跑 `python -c "import taskflow"` 会成功**——因为 Python 会把"当前目录"加进搜索路径。于是你的测试"在自己机器上通过"，但别人 `pip install` 之后跑不通（缺文件没打进包里）。

src 布局下，包不在根目录，**不安装就 import 不到**。问题会在开发阶段立刻暴露，而不是上线后。

> **Java 类比**：Maven 标准目录 `src/main/java` + `src/test/java`。把源码藏在 `src/` 下，正是同一个思路。

### 4.1.5 目录规划：为什么要分这么多层

这是本章最值得先想清楚的事。**分层的唯一目的，是让"变化"被隔离在某一层里。**

| 层 | 职责 | 不许做什么 | 典型变化来源 |
|----|------|-----------|------------|
| `api/` 路由 | 接参数、调 Service、组装响应 | 写业务 `if` | 接口风格改（加字段、改路径） |
| `services/` | 业务规则、状态机、校验 | 碰 HTTP 对象 | 需求改 |
| `repositories/` | 存取数据 | 做业务判断 | 换数据库 |
| `models/` | 数据契约 | — | 字段调整 |
| `core/` | 配置、日志、异常 | 写业务 | 基础设施升级 |

验证分层是否有效的**唯一标准**：换掉某一层的实现，其他层要不要改？

- 内存仓储 → PostgreSQL：只改 `repositories/`，Service 一行不动 ✅
- 加一个新接口：只改 `api/` + Service 加方法，仓储不动 ✅

> **Java 类比**：Controller / Service / Repository 三层，一模一样。
> 差别在 Python 里没有 `@Service` 注解和 IoC 容器——**依赖靠构造函数传进来**（见 4.5）。
> 这反而是好事：依赖关系写在代码里，一眼可见，不用猜容器给你注入了什么。

---

## 4.2 第二步：模型层 —— Pydantic 就是契约

### 4.2.1 先理解 Pydantic 到底在干什么

Pydantic 做一件事：**你用类型注解声明"数据应该长什么样"，它负责校验和转换。**

```python
from pydantic import BaseModel

class TaskCreate(BaseModel):
    title: str
    priority: str = "medium"

TaskCreate(title="修复登录 500")                 # ✅ priority 用默认值
TaskCreate(title="x", priority="urgent")         # ✅
TaskCreate()                                     # ❌ ValidationError: title 缺失
TaskCreate(title="x", priority=123)              # ❌ 类型不对
```

看最后一行——**赋值即校验**。这是 Pydantic 和"普通 class"的根本区别：

```python
class PlainTask:            # 普通类
    title: str

t = PlainTask()
t.title = 123               # 随便赋，没人管

class PydanticTask(BaseModel):
    title: str

p = PydanticTask(title="ok")
p.title = 123               # 默认也不校验！除非开 validate_assignment
```

第二个反直觉的点：**Pydantic 默认只在"构造时"校验，赋值时不校验**。要开启需要配置（本项目 `TaskRecord` 就开了，后面会讲为什么）。

> **Java 类比**：Pydantic 的 `BaseModel` ≈ Java 的 `record` + Bean Validation（`@NotNull @Size`）+ Jackson 反序列化，三合一。
>
> 差别：Java 的注解校验要显式调用 `validator.validate(obj)`；Pydantic 校验发生在**对象构造的那一行**。

### 4.2.2 三个模型：入参 / 实体 / 出参

这是 Pydantic 项目里**最重要的约定**。看 `src/taskflow/models/task.py`：

```python
class TaskBase(BaseModel):
    """任务公共字段。"""
    title: str = Field(
        ...,                                    # ... 表示必填
        min_length=1,
        max_length=200,
        description="任务标题",
        examples=["修复登录页 500 错误"],
    )
    description: str | None = Field(default=None, max_length=2000, description="任务描述")
    priority: TaskPriority = Field(default=TaskPriority.MEDIUM, description="优先级")
    assignee: str | None = Field(default=None, max_length=50, description="负责人用户名")
    tags: list[str] = Field(default_factory=list, max_length=10, description="标签")
    due_date: date | None = Field(default=None, description="截止日期 YYYY-MM-DD")


class TaskCreate(TaskBase):
    """创建任务的入参模型。"""


class TaskRecord(TaskBase):
    """存储层实体：内部使用，不直接返回给客户端。"""
    model_config = ConfigDict(validate_assignment=True)  # 赋值时也做校验

    id: str = Field(description="任务ID")
    status: TaskStatus = Field(default=TaskStatus.TODO, description="状态")
    created_at: datetime = Field(description="创建时间")
    updated_at: datetime = Field(description="更新时间")
    completed_at: datetime | None = Field(default=None, description="完成时间")


class TaskRead(TaskBase):
    """出参模型：只暴露客户端需要的字段，并补充派生字段。"""
    id: str
    status: TaskStatus
    created_at: datetime
    updated_at: datetime
    completed_at: datetime | None = None
    is_overdue: bool = Field(default=False, description="是否已逾期")
```

为什么是三个，不是一个？

| 模型 | 谁用它 | 关键差异 |
|------|--------|---------|
| `TaskCreate` | 请求体 | **没有 `id` 和 `status`** —— 这些由服务端生成 |
| `TaskRecord` | 内部存储 | 有 `id`、时间戳、`status` |
| `TaskRead` | 响应体 | 比 `TaskRecord` 多一个**派生字段** `is_overdue` |

**如果只用一个模型会怎样？**

```python
# ❌ 反例：一个模型打天下
class Task(BaseModel):
    id: str | None = None          # 创建时没有 → 只能 Optional
    status: TaskStatus | None = None
    title: str
    ...

# 后果：
# 1. 客户端可以在创建时传 id 和 status —— 越权字段全开
# 2. OpenAPI 文档里所有字段都是"可选"，前端不知道哪些必填
# 3. 内部字段（created_at）会被当成入参，Swagger 上显示得填
```

> **Java 类比**：`TaskCreate` = DTO（入参）/ `TaskRecord` = Entity（实体）/ `TaskRead` = VO（出参）。
> Java 项目里这三者也是分开的，只是很多人嫌麻烦合并了——然后就开始踩坑。

**`Field(...)` 里的 `...` 是什么？**

Python 的 `Ellipsis` 字面量，在这里表示"必填，无默认值"。等价于 `Field(required=True)`，但更短。`Field()` 的其他常用参数：

| 参数 | 作用 |
|------|------|
| `default` / `default_factory` | 默认值；**可变对象（list/dict）必须用 `default_factory`** |
| `min_length` / `max_length` | 字符串长度、列表长度 |
| `ge` / `le` / `gt` / `lt` | 数值范围（≥ ≤ > <） |
| `description` | 显示在 OpenAPI 文档 |
| `examples` | Swagger 里的示例值 |

> ⚠️ **最常见的坑**：`tags: list[str] = []`。
> Python 的可变默认参数是**共享的**——所有实例共用同一个 list，改一个影响全部。必须用 `default_factory=list`。
> 这个坑和 Pydantic 无关，是 Python 本身的特性（第一章讲过）。

### 4.2.3 枚举：用 `str, Enum`

```python
class TaskStatus(str, Enum):
    """任务状态。str 枚举：既能当枚举用，又能直接 JSON 序列化。"""
    TODO = "todo"
    IN_PROGRESS = "in_progress"
    DONE = "done"
    CANCELLED = "cancelled"
```

继承 `str, Enum`（而不是只继承 `Enum`）的效果：

```python
TaskStatus.TODO == "todo"      # True
TaskStatus.TODO.value          # "todo"
json.dumps({"s": TaskStatus.TODO})   # 正常工作（纯 Enum 会报错）
```

`priority` 字段声明成 `TaskPriority` 类型后，客户端传 `"super"` 会自动返回 422：

```bash
$ curl -X POST http://127.0.0.1:8123/api/v1/tasks -d '{"title":"x","priority":"super"}'
{"code":422,"message":"参数校验失败","details":[{"field":"priority","message":"Input should be 'low', 'medium', 'high' or 'urgent'","type":"enum"}]}
```

> **Java 类比**：`@EnumXXX` 校验 + Jackson 的 `@JsonValue`。Python 一个 `str, Enum` 搞定。

### 4.2.4 `TaskRecord` 为什么开 `validate_assignment`

```python
model_config = ConfigDict(validate_assignment=True)
```

因为 Service 层的更新逻辑是这样写的：

```python
for field_name, value in changes.items():
    setattr(record, field_name, value)   # ← 赋值触发校验
```

开了这个配置，**每次 `setattr` 都会跑校验**。客户端传 `priority: null` 时，`setattr` 会抛 `ValidationError`，Service 捕获后转成 422：

```python
try:
    for field_name, value in changes.items():
        setattr(record, field_name, value)
except ValidationError as exc:
    # 这是"入参非法"，应该报 422 而不是 500
    raise ValidationException("字段值非法", {"errors": exc.errors()}) from exc
```

不开的话，`null` 会被静默写进去，等后面 `TaskRead.from_record()` 才炸——那时已经分不清是"客户端传错"还是"服务 bug"了。

### 4.2.5 派生字段：出参模型的价值所在

`is_overdue`（是否逾期）**不在数据库里**，是算出来的：

```python
@classmethod
def from_record(cls, record: TaskRecord, *, today: date | None = None) -> "TaskRead":
    """实体 → 出参模型的转换。"""
    today = today or datetime.now().date()
    overdue = (
        record.due_date is not None
        and record.due_date < today
        and record.status not in TERMINAL_STATUSES   # 已完成/已取消不算逾期
    )
    return cls(
        **{
            **record.model_dump(exclude={"completed_at"}),
            "completed_at": record.completed_at,
            "is_overdue": overdue,
        }
    )
```

三处值得学：

**① `model_dump()` 而不是手写字段拷贝**
给 `TaskRecord` 加字段时，只要 `TaskRead` 有同名字段就自动带上，不用改转换代码。

**② `today` 作为参数传入，而不是内部调 `datetime.now()`**
这是**可测试性设计**。测试里传固定日期：

```python
# 测试：2026-01-15 这天，截止 2026-01-10 且未完成 → 逾期
task = TaskRead.from_record(record, today=date(2026, 1, 15))
assert task.is_overdue is True
```

如果写在内部调 `now()`，这个测试**明年就会挂**——典型的"测试随时间失效"。

**③ 用 `@classmethod` 而不是普通函数**
`TaskRead.from_record(record)` 读起来就是"从 record 构造 TaskRead"，语义清楚。

> **Java 类比**：静态工厂方法 `TaskRead.from(record)`。Java 里叫"值对象转换"。

### 4.2.6 统一响应包装：`ApiResponse<T>` 和 `Page<T>`

看 `src/taskflow/models/base.py`：

```python
from typing import Generic, TypeVar

# TypeVar 让 ApiResponse 变成"泛型模型"：ApiResponse[TaskRead]、ApiResponse[Page[TaskRead]]
T = TypeVar("T")


class ApiResponse(BaseModel, Generic[T]):
    """统一响应包装。code=0 表示成功，非 0 表示业务失败。"""

    code: int = Field(default=0, description="业务码，0 表示成功")
    message: str = Field(default="ok", description="提示信息")
    data: T | None = Field(default=None, description="业务数据")

    @classmethod
    def ok(cls, data: T | None = None, message: str = "ok") -> "ApiResponse[T]":
        return cls(code=0, message=message, data=data)


class Page(BaseModel, Generic[T]):
    """分页结果。"""
    items: list[T] = Field(default_factory=list)
    total: int = Field(default=0)
    page: int = Field(default=1, ge=1)
    page_size: int = Field(default=20, ge=1, le=100)
    total_pages: int = Field(default=0)

    @classmethod
    def of(cls, items: list[T], total: int, page: int, page_size: int) -> "Page[T]":
        total_pages = (total + page_size - 1) // page_size if total > 0 else 0
        return cls(items=items, total=total, page=page, page_size=page_size,
                   total_pages=total_pages)
```

**Pydantic 支持泛型**，写法是 `BaseModel` + `Generic[T]`。路由里这样用：

```python
@router.get("", response_model=ApiResponse[Page[TaskRead]])
async def list_tasks(...) -> ApiResponse[Page[TaskRead]]: ...
```

于是 Swagger 文档里能看到完整的嵌套结构，前端生成代码时类型也是准的。

> **Java 类比**：`class ApiResponse<T> { ... }` / `class Page<T> { ... }`——几乎逐字对应。

**为什么要有 `ApiResponse` 这层包装？**

坦白说，**这是国内后端的普遍习惯，不是 REST 的强制要求**。两种流派：

| 流派 | 响应 | 优点 | 缺点 |
|------|------|------|------|
| 包装派（本项目） | `{"code":0,"message":"ok","data":{...}}` | 错误信息有统一位置；业务码和 HTTP 码分离 | HTTP 状态码的语义被弱化 |
| 裸数据派（GitHub API 等） | 直接返回资源 JSON，错误靠 HTTP 状态码 | 符合 HTTP 语义，缓存/代理友好 | 错误信息格式不统一 |

本项目的**折中做法**：

- 成功：HTTP 200/201/204 + `{"code":0,"data":...}`
- 失败：HTTP 4xx/5xx + `{"code":<业务码>,"message":...,"details":...}`

**HTTP 状态码照常使用**（不一律 200），这样网关、监控、CDN 都能正常工作——这一点很重要，见过太多"所有请求都返回 200，错误藏在 body 里"的 API，导致 Nginx 日志完全没法用于可用性统计。

### 4.2.7 PATCH 的坑：`exclude_unset=True`

`TaskUpdate` 所有字段都是 `Optional`：

```python
class TaskUpdate(BaseModel):
    """更新任务的入参模型（PATCH 语义：给什么改什么）。"""
    title: str | None = Field(default=None, min_length=1, max_length=200)
    priority: TaskPriority | None = None
    tags: list[str] | None = Field(default=None, max_length=10)
    ...
```

问题来了：**"没传这个字段"和"传了 null"怎么区分？**

```python
payload.model_dump()                    # {"title": None, "priority": None, ...}  ← 全都有！
payload.model_dump(exclude_unset=True)  # {"priority": "high"}  ← 只有客户端真正传的
```

**这就是 `exclude_unset=True` 的意义。** 用错了会怎样？

```python
# ❌ 错误写法
changes = payload.model_dump()          # 用户只想改优先级，但 title 也变成 None 了
for k, v in changes.items():
    setattr(record, k, v)               # 标题被清空 → 数据丢失

# ✅ 正确写法（本项目 services/task_service.py）
changes = payload.model_dump(exclude_unset=True)
if not changes:
    raise ValidationException("请求体中没有可更新的字段")
```

这是 Pydantic + PATCH 组合时**排名第一的踩坑点**。

配套的还有一条业务约定（写在代码注释里）：

```python
# 约定：tags 传 null 表示"清空标签"，而不是"赋值为 null"
if "tags" in changes and changes["tags"] is None:
    changes["tags"] = []
```

**"传 null 表示清空"是一个产品决策**，必须在文档里写清楚，否则客户端调 `PATCH {"tags": null}` 期待清空，服务端却返回 422（因为 `list[str] | None` 允许 null，但你没定义语义）。

### 4.2.8 本节小结：模型层的三条铁律

1. **入参、实体、出参分开**——安全边界和文档准确性都靠它；
2. **可变默认值用 `default_factory`**——`[]` 和 `{}` 是共享的；
3. **涉及"现在/今天"的计算，把时间作为参数传入**——否则测试会随时间失效。

---
## 4.3 第三步：仓储层 —— 把"数据从哪来"隔离出去

### 4.3.1 用 Protocol 定义接口

`src/taskflow/repositories/task_repo.py` 开头：

```python
from typing import Protocol


class TaskRepository(Protocol):
    """仓储接口。

    用 Protocol 而不是 ABC：
    实现类不需要显式继承它，只要"长得像"就算实现了（结构化子类型）。
    对应 Java 的 interface —— 但鸭子类型，耦合更低。
    """

    async def add(self, task: TaskRecord) -> TaskRecord:
        """新增任务；id 已存在时抛出 ValueError。"""
        ...

    async def get(self, task_id: str) -> TaskRecord | None:
        """按 id 查询，不存在返回 None（不要抛异常，让 Service 决定语义）。"""
        ...

    async def update(self, task: TaskRecord) -> TaskRecord: ...
    async def delete(self, task_id: str) -> bool: ...
    async def search(self, query: TaskQuery) -> tuple[list[TaskRecord], int]: ...
    async def list_all(self) -> list[TaskRecord]: ...
```

**`Protocol` 是什么？**

Python 3.8+ 引入的**结构化子类型**（Structural Subtyping），通俗叫"静态鸭子类型"。

```python
class Flyable(Protocol):
    def fly(self) -> None: ...

class Bird:
    def fly(self) -> None:      # 没有写 class Bird(Flyable)
        print("扇翅膀")

def take_off(thing: Flyable) -> None:
    thing.fly()

take_off(Bird())     # ✅ 类型检查器认可：Bird 有 fly()，就"是" Flyable
```

对比两种方式：

| 方式 | 写法 | 特点 |
|------|------|------|
| `Protocol`（本项目） | `class InMemoryTaskRepository:` | 不用继承，实现类和接口**零耦合** |
| `ABC`（抽象基类） | `class InMemoryTaskRepository(TaskRepository):` | 必须显式继承，不实现抽象方法就报错 |

> **Java 类比**：`Protocol` ≈ Java 的 `interface`，但**不需要 `implements` 关键字**。
> Java 是"名义子类型"（必须声明实现关系），Python Protocol 是"结构子类型"（有这个方法就算）。
>
> 实用价值：给第三方类做适配时，你不用改它的源码去继承你的接口。

**`...` 是什么？** 方法体里的 `...` 是 `Ellipsis`，表示"这里没有实现"。在 Protocol 里这就是标准写法——只声明签名。

### 4.3.2 内存实现

```python
class InMemoryTaskRepository:
    """内存实现：用一个 dict 存数据。"""

    def __init__(self) -> None:
        self._tasks: dict[str, TaskRecord] = {}
        self._lock = threading.Lock()

    async def add(self, task: TaskRecord) -> TaskRecord:
        with self._lock:
            if task.id in self._tasks:
                raise ValueError(f"任务已存在: {task.id}")
            self._tasks[task.id] = task
            return task

    async def get(self, task_id: str) -> TaskRecord | None:
        return self._tasks.get(task_id)

    async def delete(self, task_id: str) -> bool:
        with self._lock:
            return self._tasks.pop(task_id, None) is not None
```

**问题一：都是 `async`，但里面一个 `await` 都没有，为什么要这样写？**

因为**接口形状必须提前对齐**。假如现在写成同步的 `def add()`，将来换成 PostgreSQL 实现时：

```python
# 换成数据库后
async def add(self, task):        # 必须是 async（数据库 IO 要 await）
    await self._session.execute(...)
```

调用方 Service 里所有 `await repo.add(...)` 全部要改成 `repo.add(...)`——**改的是几十处业务代码**。

提前统一成 `async`，换存储时只改仓储实现，Service 一行不动。这就是**面向接口编程**的实际收益。

**问题二：既然是 async（单线程事件循环），为什么还要加锁？**

因为 FastAPI 的线程池：`def`（非 async）路由会被丢进线程池并发执行。即便你全用 `async def`，也可能有同步代码路径。更现实的原因是——**这把锁是为将来准备的**：换成真数据库时，并发由数据库事务保证；内存实现下，`threading.Lock` 是最低成本的正确性保障。

> 一句话：内存仓储是"教学用的替身"，但它的接口和并发语义都按真实实现来设计。

**问题三：`get()` 为什么返回 `None` 而不抛异常？**

这是**分层职责**问题。"查不到"在 HTTP 语义里是 404，但**仓储层不该知道 HTTP**。它的职责是"返回数据或没有数据"，由 Service 决定"没有数据"意味着 404、400 还是别的。

```python
# ❌ 仓储里抛 NotFoundException（这是 Service 的活）
async def get(self, task_id: str) -> TaskRecord:
    task = self._tasks.get(task_id)
    if task is None:
        raise NotFoundException("任务", task_id)   # 仓储层不该 import HTTP 异常
    return task

# ✅ 仓储返回 None，Service 决定语义
async def get(self, task_id: str) -> TaskRecord | None:
    return self._tasks.get(task_id)
```

### 4.3.3 过滤 → 排序 → 分页

```python
async def search(self, query: TaskQuery) -> tuple[list[TaskRecord], int]:
    """过滤 → 排序 → 分页。

    真实项目里这三步应该下推到数据库（WHERE / ORDER BY / LIMIT OFFSET）；
    内存实现只是把逻辑照抄一遍，方便本地跑通。
    """
    records = [r for r in self._tasks.values() if self._matches(r, query)]

    records.sort(key=lambda r: self._sort_key(r, query.sort_by), reverse=query.desc)

    total = len(records)
    start = (query.page - 1) * query.page_size
    end = start + query.page_size
    return records[start:end], total
```

**为什么要返回 `(当前页数据, 总数)` 这个元组？**

因为前端分页组件需要"总共多少条"才能算出有几页。这两步必须是一次查询的两个产出——**先算 total 再切片**。

> ⚠️ **生产提示**：内存里 `len()` 是 O(1)，数据库里 `COUNT(*)` 在大表上可能很慢（几秒）。
> 数据量过百万后，常见做法是：
> - 不返回精确 total，只返回"是否还有下一页"（游标分页）；
> - 或缓存/近似计数（如 PostgreSQL 的 `reltuples` 估算）。

**排序的坑：None 和具体值不能直接比较**

```python
@staticmethod
def _sort_key(record: TaskRecord, sort_by: str) -> tuple[int, object]:
    """返回 (是否为None, 值) 的元组：None 永远排在有值后面。"""
    if sort_by == "priority":
        return (0, _PRIORITY_ORDER[record.priority])
    raw = getattr(record, sort_by, None)
    if raw is None:
        return (1, "")
    return (0, raw)
```

按 `due_date` 排序时，有些任务没有截止日期（`None`）。直接 `sorted(..., key=lambda r: r.due_date)` 会抛：

```
TypeError: '<' not supported between instances of 'NoneType' and 'datetime.date'
```

解法：**把排序键改成元组 `(是否为空, 值)`**。比较元组时先比第一个元素——有值的（0）永远排在没有值的（1）前面，且不会拿 `None` 和 `date` 比较。

这是 Python 排序的常用技巧。

**优先级排序为什么要用映射表？**

因为 `TaskPriority` 是字符串枚举，`"urgent" < "low"` 按字母序比较，结果毫无业务意义。所以定义权重：

```python
_PRIORITY_ORDER = {
    TaskPriority.LOW: 0,
    TaskPriority.MEDIUM: 1,
    TaskPriority.HIGH: 2,
    TaskPriority.URGENT: 3,
}
```

配合默认的 `desc=True`（倒序），URGENT 排最前——符合"先处理最紧急的"直觉。

### 4.3.4 查询对象 `TaskQuery`：为什么用 dataclass

```python
@dataclass(slots=True)
class TaskQuery:
    """查询条件对象（不是 HTTP 入参模型，而是传给仓储层的查询对象）。

    用 dataclass 而不是 Pydantic 模型：它只在内部流转，不需要校验和序列化。
    """
    status: TaskStatus | None = None
    priority: TaskPriority | None = None
    assignee: str | None = None
    tag: str | None = None
    keyword: str | None = None
    page: int = 1
    page_size: int = 20
    sort_by: str = "created_at"
    desc: bool = True
    only_overdue: bool = False
    # "逾期"是相对概念，必须由调用方给定"今天"是哪天，
    # 仓储层自己调 datetime.now() 会让测试不可复现。
    overdue_before: date | None = None
    extra: dict[str, object] = field(default_factory=dict)
```

**为什么不直接把 10 个参数传给 `search()`？**

```python
# ❌ 参数地狱
async def search(status, priority, assignee, tag, keyword,
                 page, page_size, sort_by, desc, only_overdue): ...

# ✅ 一个对象
async def search(query: TaskQuery): ...
```

对象方式的好处：加筛选条件时**不用改方法签名**，所有调用方不受影响。

**为什么是 `dataclass` 不是 `BaseModel`？**

| | Pydantic `BaseModel` | `dataclass` |
|---|---|---|
| 校验 | 有（构造即校验） | 无 |
| 序列化 | 有 | 简单支持 |
| 性能 | 慢一些（校验开销） | 快约 10 倍 |
| 用途 | 系统边界（HTTP 入参/出参、配置） | 内部数据结构 |

`TaskQuery` 由 Service 从**已校验过**的路由参数构造，不需要二次校验——用 dataclass 更快更轻。

`slots=True` 是 Python 3.10+ 特性：禁止动态加属性，**省内存 + 防拼写错误**（写错字段名会报错而不是默默创建）。

---

## 4.4 第四步：服务层 —— 业务规则的家

服务层是整个项目**最值钱的部分**：它不 import 任何 FastAPI 的东西，只依赖仓储接口和外部服务接口。所以它可以被单独测试，也能在 CLI、定时任务里复用。

### 4.4.1 构造函数注入：把"不确定性"全部外置

```python
class TaskService:
    def __init__(
        self,
        repository: TaskRepository,
        user_service: UserServiceLike | None = None,
        *,
        now: Callable[[], datetime] = datetime.now,
        id_factory: Callable[[], str] = lambda: uuid.uuid4().hex[:12],
    ) -> None:
        self._repo = repository
        self._user_service = user_service
        self._now = now
        self._id_factory = id_factory
```

四个参数，各有教学意义：

| 参数 | 为什么是构造参数 | 不这样做会怎样 |
|------|----------------|---------------|
| `repository` | 换存储/测试替身 | 内部 `new` 死实现，测试只能打真实数据库 |
| `user_service` | 外部依赖，可传 `None` 表示"不校验" | 单元测试被迫联网 |
| `now` | **固定时间，让测试可复现** | 涉及日期的断言明年就挂 |
| `id_factory` | **让 ID 可预测** | 断言只能写正则匹配 uuid |

> **Java 类比**：这就是 Spring 的**构造器注入**，也是 Spring 官方推荐的方式（优于字段 `@Autowired`）。
> 区别是 Python 没有容器，谁调用谁负责传——依赖图在代码里显式可见。

**"注入时间和 ID 生成器"是本项目最值得借鉴的一点。** 看测试里的效果：

```python
# tests/unit/test_task_service.py
counter = itertools.count(1)
service = TaskService(
    repository,
    user_service,
    now=lambda: FIXED_NOW,                        # 时间永远是 2026-01-15 10:00
    id_factory=lambda: f"task-{next(counter):03d}", # ID 是 task-001, task-002...
)

task = await service.create_task(TaskCreate(title="修复登录 500"))
assert task.id == "task-001"          # 可精确断言
assert task.created_at == FIXED_NOW   # 可精确断言
```

不注入的话，测试得写成：

```python
assert re.match(r"[0-9a-f]{12}", task.id)      # 只能验格式
assert (datetime.now() - task.created_at).seconds < 5   # 只能验"差不多"
```

### 4.4.2 业务方法：读起来应该像需求文档

```python
async def get_task(self, task_id: str) -> TaskRead:
    """查单个任务，不存在抛 404（由全局 handler 转成 JSON 响应）。"""
    record = await self._repo.get(task_id)
    if record is None:
        raise NotFoundException("任务", task_id)
    return TaskRead.from_record(record, today=self._now().date())
```

注意 `raise NotFoundException` —— **Service 不返回错误 dict，只抛异常**。由 4.7 节的全局 handler 统一转成 JSON。好处：

- 业务代码里没有 HTTP 概念；
- 错误响应格式**全局统一**；
- 任何一层抛异常都能被捕获处理。

### 4.4.3 状态机：业务约束集中表达

```python
# models/task.py
TERMINAL_STATUSES = frozenset({TaskStatus.DONE, TaskStatus.CANCELLED})

# 状态机：key 是当前状态，value 是允许流转到的状态
ALLOWED_TRANSITIONS: dict[TaskStatus, frozenset[TaskStatus]] = {
    TaskStatus.TODO:         frozenset({TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED}),
    TaskStatus.IN_PROGRESS:  frozenset({TaskStatus.TODO, TaskStatus.DONE, TaskStatus.CANCELLED}),
    TaskStatus.DONE:         frozenset(),          # 已完成：终态
    TaskStatus.CANCELLED:    frozenset(),          # 已取消：终态
}
```

Service 里的流转逻辑：

```python
async def transition_status(self, task_id: str, target: TaskStatus) -> TaskRead:
    """状态流转：按状态机校验，非法流转返回 409。"""
    record = await self._require_task(task_id)
    allowed = ALLOWED_TRANSITIONS.get(record.status, frozenset())

    if target not in allowed:
        raise ConflictException(
            f"不允许的状态流转: {record.status.value} → {target.value}",
            {
                "current": record.status.value,
                "target": target.value,
                "allowed": sorted(s.value for s in allowed),   # 告诉客户端"你能改成什么"
            },
        )

    now = self._now()
    record.status = target
    record.updated_at = now
    record.completed_at = now if target == TaskStatus.DONE else None
    await self._repo.update(record)
    return TaskRead.from_record(record, today=now.date())
```

**用字典表达状态机的三个好处：**

1. **规则一目了然**——产品评审时可以把这张表贴出来逐行确认；
2. **错误信息能自解释**——`allowed` 字段告诉客户端当前能流转到哪些状态；
3. **加状态只改一处**——不用在代码里到处找 `if status == ...`。

实际调用效果（真实输出）：

```bash
$ curl -X POST http://127.0.0.1:8123/api/v1/tasks/demo-004/transition \
       -H "Content-Type: application/json" -d '{"target":"in_progress"}'

{"code":409,"message":"不允许的状态流转: done → in_progress",
 "details":{"current":"done","target":"in_progress","allowed":[]}}
```

**为什么状态变更要单独开一个接口？** 代码注释里写了原因：

```python
class TaskTransitionRequest(BaseModel):
    """为什么状态变更要单独开一个接口，而不是塞进 PATCH /{task_id}？
    因为状态是"受控字段"：它有状态机约束、要留痕、可能触发通知。
    把"改标题"和"标记完成"混在一个 PATCH 里，业务规则会越来越难维护。
    """
```

这是 REST 设计里的常见选择：**行为（action）独立成子资源**，而不是伪装成字段更新。

### 4.4.4 外部服务调用与降级

`services/user_service.py` 演示"调用别人的 HTTP API"：

```python
class UserService:
    def __init__(self, http: HttpClient, *, enabled: bool = True, fail_open: bool = True):
        self._http = http
        self._enabled = enabled
        self._fail_open = fail_open  # 外部服务挂了是"放行"还是"拒绝"

    async def exists(self, username: str) -> bool:
        """降级策略（fail_open=True）：查不动就当用户存在，保证主流程可用。
        金融/风控类场景应设成 False —— 宁可拒绝，也不能放过脏数据。
        """
        if not self._enabled:
            return True

        try:
            data = await self._http.get(f"/users/{username}")
        except HttpClientError as exc:
            logger.warning(f"用户服务不可用（{exc.status_code}）: {exc}")
            if self._fail_open:
                return True
            raise ExternalServiceException("user-service", str(exc)) from exc

        if not isinstance(data, dict):
            return True
        return bool(data.get("exists", True))
```

**`fail_open`（失败放行）是一个架构决策，不是技术细节。** 选型依据：

| 场景 | `fail_open` | 理由 |
|------|------------|------|
| 用户名校验（本项目） | `True` | 校验挂了不该阻断用户创建任务 |
| 权限/风控/支付校验 | **`False`** | 校验挂了必须拒绝，否则越权 |

> **Java 类比**：Resilience4j 的 `CircuitBreaker` + fallback 方法。这里手写了一个简化版。

Service 里的调用点：

```python
async def _ensure_assignee_valid(self, assignee: str | None) -> None:
    """校验负责人。"""
    if not assignee or self._user_service is None:
        return
    if not await self._user_service.exists(assignee):
        raise ValidationException(f"负责人不存在: {assignee}", {"assignee": assignee})
```

`user_service` 可以是 `None`——**"不校验"是一个合法的运行时状态**（离线环境、测试环境）。真实输出：

```bash
$ curl -X POST http://127.0.0.1:8123/api/v1/tasks \
       -H "Content-Type: application/json" -d '{"title":"x","assignee":"nobody"}'

{"code":422,"message":"负责人不存在: nobody","details":{"assignee":"nobody"}}
```

### 4.4.5 httpx 客户端：连接池与重试

`utils/http_client.py` 的核心：

```python
RETRYABLE_STATUS = frozenset({429, 500, 502, 503, 504})


class HttpClient:
    """异步 HTTP 客户端：连接池 + 超时 + 指数退避重试 + 结构化日志。

    用法：
        client = HttpClient("https://api.example.com")
        await client.get("/users/1")
        await client.aclose()          # 应用关闭时必须调用
    """

    def __init__(self, base_url: str = "", *, timeout: float = 10.0,
                 connect_timeout: float = 5.0, max_retries: int = 3,
                 backoff_base: float = 0.2,
                 transport: httpx.AsyncBaseTransport | None = None) -> None:
        # transport 参数专门为测试留的后门：可以注入 MockTransport，不真发网络请求
        self._client = httpx.AsyncClient(
            base_url=base_url,
            timeout=httpx.Timeout(timeout, connect=connect_timeout),
            limits=httpx.Limits(max_connections=100, max_keepalive_connections=20),
            headers=headers or {},
            transport=transport,
        )
```

**四个必须讲清的设计点：**

**① 连接池必须全局共享**

```python
# ❌ 错误：每个请求新建客户端
async def get_user():
    async with httpx.AsyncClient() as client:     # 每次 TCP 握手 + TLS 握手
        return await client.get("...")

# ✅ 正确：一个实例活在整个应用生命周期（本项目放在 app.state，见 4.10）
```

> **Java 类比**：Apache HttpClient 的 `PoolingHttpClientConnectionManager`。同一个道理。

**② 超时必须显式设置**

`httpx.Timeout(timeout, connect=connect_timeout)` —— 总超时 10s，连接超时 5s。

不设超时的后果：外部服务挂了 → 你的所有请求线程全部卡住 → 整个服务不可用（**级联故障**）。超时是熔断降级的前提。

**③ 只重试"值得重试"的错误**

```python
if response.status_code in RETRYABLE_STATUS:      # 429/5xx：可能是临时的，重试
    last_error = HttpClientError(...)
elif response.status_code >= 400:                 # 4xx：我请求有问题，重试也没用
    raise HttpClientError(...)
```

**重试 4xx 是常见错误**——客户端传错参数，重试 3 次还是错，白白放大流量（还可能把对方打挂）。

**④ 指数退避而不是固定间隔**

```python
wait = self._backoff_base * (2 ** (attempt - 1))   # 0.2s → 0.4s → 0.8s
```

固定间隔重试会造成**重试风暴**：服务恢复瞬间，所有客户端同时重试，再次打挂。指数退避 + 随机抖动（jitter）是标准做法。

> 本项目没加 jitter，生产环境建议加：`wait * (0.5 + random.random())`。

**⑤ `transport` 参数是为测试留的后门**

```python
# 测试里注入 MockTransport，不发真实网络请求
transport = httpx.MockTransport(handler)
client = HttpClient("http://fake", transport=transport)
```

"为测试留一个注入点"是低成本高回报的设计——不需要 mock 框架打补丁。

---

## 4.5 第五步：依赖注入 —— 把各层接起来

### 4.5.1 FastAPI 的 DI 与 Spring 的本质区别

`src/taskflow/api/deps.py` 的完整结构：

```python
from typing import Annotated
from fastapi import Depends, Request


def get_repository(request: Request) -> TaskRepository:
    """取仓储实例。测试时通过 app.dependency_overrides 换成假实现。"""
    return request.app.state.repository


def get_user_service(request: Request) -> UserService:
    return request.app.state.user_service


def get_task_service(
    repository: Annotated[TaskRepository, Depends(get_repository)],
    user_service: Annotated[UserService, Depends(get_user_service)],
) -> TaskService:
    """构造业务服务。"""
    return TaskService(repository, user_service)
```

**FastAPI 的 DI 有三个特点，Java 程序员需要适应：**

| 维度 | Spring | FastAPI |
|------|--------|---------|
| 实现方式 | IoC 容器 + 注解扫描 | **函数参数声明** |
| 配置文件 | XML / `@Configuration` 类 | 无——`Depends(get_xxx)` 就是配置 |
| 作用域 | singleton / prototype / request / session | 默认**请求级缓存**（同一请求内只解析一次） |

**核心机制**：路由函数**声明一个带 `Depends` 的参数**，框架在调用路由前先解析依赖：

```python
@router.get("/tasks")
async def list_tasks(service: TaskServiceDep):   # ← 框架先执行 get_task_service()
    ...                                          #   它又依赖 get_repository()
                                                 #   递归解析完整依赖图，再调用本函数
```

**依赖可以层层嵌套**，框架按依赖图自动解析。`get_task_service` 依赖 `get_repository` 和 `get_user_service`，框架会先解析后者。

### 4.5.2 `Annotated[X, Depends(fn)]` 写法

```python
# 老写法（不推荐）
def list_tasks(service: TaskService = Depends(get_task_service)): ...

# 新写法（FastAPI 0.95+ 推荐）
TaskServiceDep = Annotated[TaskService, Depends(get_task_service)]

def list_tasks(service: TaskServiceDep): ...
```

**为什么推荐 `Annotated`？**

`Annotated[T, metadata]` 是 Python 3.9+ 的标准语法，意思是"类型 T，附带元数据"。好处：

1. **类型信息保留完整**——IDE 和类型检查器知道 `service` 是 `TaskService`，有完整补全；
2. **依赖声明可复用**——定义一次 `TaskServiceDep`，所有路由直接用，不用重复写 `Depends(...)`；
3. **不污染默认值**——老写法把依赖塞进参数默认值，容易和真正的默认值冲突。

本项目的类型别名集中定义：

```python
SettingsDep     = Annotated[Settings, Depends(get_settings)]
RepositoryDep   = Annotated[TaskRepository, Depends(get_repository)]
TaskServiceDep  = Annotated[TaskService, Depends(get_task_service)]
UserServiceDep  = Annotated[UserService, Depends(get_user_service)]
AuditDep        = Annotated[RequestAudit, Depends(get_audit)]
```

### 4.5.3 yield 型依赖：带"释放逻辑"的依赖

```python
@dataclass(slots=True)
class RequestAudit:
    """一次请求的审计上下文。"""
    path: str
    started_at: datetime


async def get_audit(request: Request) -> AsyncIterator[RequestAudit]:
    """yield 型依赖：yield 之前 = 获取资源，yield 之后 = 释放资源。

    对应 Java：try-with-resources，或 Spring 的 @Transactional 环绕通知。
    """
    audit = RequestAudit(path=request.url.path, started_at=datetime.now())
    logger.debug(f"审计开始: {audit.path}")
    try:
        yield audit                    # ← 路由函数在这里执行
    finally:
        elapsed_ms = (datetime.now() - audit.started_at).total_seconds() * 1000
        logger.debug(f"审计结束: {audit.path} 耗时 {elapsed_ms:.1f}ms")
```

**执行顺序**（这是理解异步依赖的关键）：

```
1. 执行 yield 之前的代码（准备资源）
2. yield 值给路由函数
3. 路由函数执行完毕（哪怕抛异常）
4. 回到 yield 之后，执行清理代码
```

**最常见的实战用法是数据库会话**（代码注释里有范例）：

```python
async def get_db():
    session = SessionLocal()
    try:
        yield session                 # 路由函数执行
        await session.commit()        # 正常结束 → 提交
    except Exception:
        await session.rollback()      # 异常 → 回滚
        raise
    finally:
        await session.close()         # 一定会执行
```

> **Java 类比**：`try-with-resources`（自动关闭）+ `@Transactional`（自动提交/回滚）的组合。
> Python 用 `yield` + `try/finally` 实现，更底层但更灵活。

### 4.5.4 `dependency_overrides`：测试替换的官方机制

```python
app = create_app()
app.dependency_overrides[get_repository] = lambda: fake_repository
```

**不改一行业务代码，就能把真实依赖换成测试替身。**

> **Java 类比**：`@MockBean` / `@TestConfiguration` 替换 Bean。

本项目的 `tests/conftest.py` 里这样用：

```python
def build_test_client(repository, user_service):
    counter = itertools.count(1)

    def fake_task_service() -> TaskService:
        return TaskService(
            repository,
            user_service,
            now=lambda: FIXED_NOW,                       # 固定时间
            id_factory=lambda: f"gen-{next(counter):03d}",  # 可预测 ID
        )

    app = create_app()
    app.dependency_overrides[get_repository] = lambda: repository
    app.dependency_overrides[get_user_service] = lambda: user_service
    app.dependency_overrides[get_task_service] = fake_task_service   # ← 连时间都固定了

    with TestClient(app) as test_client:
        yield test_client

    app.dependency_overrides.clear()      # 用完清理，避免污染其他用例
```

**为什么还要覆写 `get_task_service`（而不只是仓储）？**

因为 `get_task_service` 里 `new TaskService(...)` 时用的是**默认的时间函数和 ID 生成器**。测试需要"确定性"，所以整个 Service 的构造过程都要接管。

这一点说明：**"时间和 ID 做成构造参数"这个决定，在测试阶段得到了回报。**

> ⚠️ **注意 `dependency_overrides.clear()`**
> 如果不清理，覆写会残留在 app 实例上。由于 `create_app()` 每个用例新建 app，本项目影响不大；但如果你用模块级单例 app，漏掉这行会导致测试互相污染。

### 4.5.5 本节小结：DI 的三条规律

1. **路由声明参数 = 声明依赖**，没有容器，依赖图在代码里显式可见；
2. **`Annotated[X, Depends(fn)]` 是官方推荐写法**，类型信息完整、可复用；
3. **需要清理资源就用 `yield` 依赖**，`finally` 一定执行。

---
## 4.6 第六步：路由层 —— HTTP 世界与业务世界的翻译器

### 4.6.1 APIRouter 与模块化注册

```python
# api/routes/tasks.py
from fastapi import APIRouter

router = APIRouter()
```

然后在装配时统一注册（`main.py`）：

```python
app.include_router(health.router, tags=["健康检查"])
app.include_router(tasks.router, prefix="/api/v1/tasks", tags=["任务"])
```

三个参数的作用：

| 参数 | 效果 |
|------|------|
| `prefix="/api/v1/tasks"` | 路由里写 `""` 和 `"/{task_id}"`，实际是 `/api/v1/tasks` 和 `/api/v1/tasks/{task_id}` |
| `tags=["任务"]` | Swagger 文档里的**分组标题**，几十个接口时才不会是一锅粥 |
| 多个 `include_router` | 按业务域拆分文件，避免一个 3000 行的 `main.py` |

> **Java 类比**：`@RequestMapping("/api/v1/tasks")` + `@Tag(name="任务")`（Swagger 注解）。
> 区别：FastAPI 的路由表是**显式注册**的，没有组件扫描——你 `include_router` 了才生效。
> 这避免了 Spring 项目里"扫包路径配错导致 Controller 静默失效"的经典问题。

### 4.6.2 查询参数：`Query()` 声明校验规则

```python
@router.get("", response_model=ApiResponse[Page[TaskRead]], summary="任务列表（分页 + 多条件筛选）")
async def list_tasks(
    service: TaskServiceDep,
    status: TaskStatus | None = Query(default=None, description="按状态筛选"),
    assignee: str | None = Query(default=None, max_length=50, description="按负责人筛选"),
    keyword: str | None = Query(default=None, description="标题/描述模糊匹配"),
    only_overdue: bool = Query(default=False, description="只看逾期"),
    page: int = Query(default=1, ge=1, description="页码，从 1 开始"),
    page_size: int = Query(default=20, ge=1, le=100, description="每页条数"),
    sort_by: Literal["created_at", "updated_at", "priority", "due_date"] = Query(
        default="created_at", description="排序字段"
    ),
    desc: bool = Query(default=True, description="是否倒序"),
) -> ApiResponse[Page[TaskRead]]:
    query = TaskQuery(status=status, assignee=assignee, keyword=keyword,
                      only_overdue=only_overdue, page=page, page_size=page_size,
                      sort_by=sort_by, desc=desc)
    return ApiResponse.ok(await service.list_tasks(query))
```

**FastAPI 怎么区分"依赖"和"查询参数"？**

看参数的类型：

- 类型是 `Annotated[X, Depends(fn)]` → 依赖注入；
- 类型是标量（`int`/`str`/`bool`/Enum/`Literal`）→ **查询参数**；
- 类型是 Pydantic 模型 → **请求体**；
- 函数名里没有的路径参数 → 路径参数。

所以 `service: TaskServiceDep`（带 Depends）是依赖，`page: int = Query(...)` 是查询参数。

**`Query()` 的必要性**：直接写 `page: int = 1` 也能工作，但加上 `Query(ge=1, le=100)` 才能：

```bash
$ curl "http://127.0.0.1:8123/api/v1/tasks?page=0"
{"code":422,"message":"参数校验失败",
 "details":[{"field":"page","message":"Input should be greater than or equal to 1","type":"greater_than_equal"}]}
```

**为什么必须限制 `page_size` 上限？** 不限制的话，客户端传 `page_size=1000000` 就是一次 DoS——一次查询把内存打爆。

**`Literal` 限制可选值**——`sort_by` 只能是这四个字符串之一，传别的直接 422。比用 `str` + 运行时 `if` 检查好得多，而且文档里会显示成下拉框。

> **Java 类比**：`@RequestParam @Min(1) @Max(100)`。FastAPI 用 `Query(ge=1, le=100)` 表达。

### 4.6.3 路径参数与路由顺序（重要坑）

```python
@router.get("/stats", ...)                       # ① 固定路径
async def get_stats(service: TaskServiceDep): ...

@router.get("/export/stream", ...)               # ② 固定路径
async def export_tasks_stream(service: TaskServiceDep): ...

@router.get("/{task_id}", ...)                   # ③ 动态路径
async def get_task(task_id: str, service: TaskServiceDep): ...
```

**⚠️ 顺序不能反。** FastAPI **按注册顺序匹配**，如果把 `/{task_id}` 写在最前面：

```python
@router.get("/{task_id}")          # ← 写在前面
async def get_task(task_id: str): ...

@router.get("/stats")              # ← 永远不会被匹配到
async def get_stats(): ...
```

请求 `GET /api/v1/tasks/stats` 会被当成 `task_id="stats"` 命中详情接口，然后返回"任务 stats 不存在"。

> **Java 类比**：Spring MVC 也有同样问题，但它有"更具体的路径优先"的匹配策略（AntPathMatcher 排序），相对宽容。
> FastAPI/Starlette 是**严格按注册顺序**，所以必须自己排好。
>
> **规则**：先固定路径，后动态路径。

### 4.6.4 状态码：别一律返回 200

```python
@router.post("", response_model=ApiResponse[TaskRead], status_code=201, summary="创建任务")
async def create_task(...) -> ApiResponse[TaskRead]: ...

@router.delete("/{task_id}", status_code=204, summary="删除任务")
async def delete_task(task_id: str, service: TaskServiceDep) -> Response:
    """204 No Content：删除成功但没有响应体。

    注意：返回 Response 实例时 FastAPI 不再做 response_model 校验，
    这正是我们想要的 —— 204 本来就规定不能有 body。
    """
    await service.delete_task(task_id)
    return Response(status_code=204)
```

| 场景 | 状态码 | 理由 |
|------|-------|------|
| 创建成功 | **201** | 资源已创建，REST 惯例 |
| 删除成功 | **204** | No Content，无响应体 |
| 查询成功 | 200 | — |

**204 的特殊处理**：直接返回 `Response(status_code=204)` 而不用 `response_model`，因为 204 响应**不允许有 body**。如果路由声明了返回类型，FastAPI 会尝试序列化，导致协议错误。

### 4.6.5 `response_model` 的双重作用

```python
@router.get("/{task_id}", response_model=ApiResponse[TaskRead], summary="任务详情")
async def get_task(task_id: str, service: TaskServiceDep) -> ApiResponse[TaskRead]:
    return ApiResponse.ok(await service.get_task(task_id))
```

`response_model` 做两件事：

1. **过滤字段**——返回值里多余的字段会被剔除。比如 Service 不小心返回了含 `internal_flag` 的对象，只要 `TaskRead` 里没有这个字段，就不会出现在响应里。**这是一道安全防线**（防止内部字段泄露）。
2. **生成文档**——Swagger 里显示准确的响应结构。

> **建议**：始终显式写 `response_model`，不要依赖"自动推断返回类型注解"。显式声明是文档准确性的保证。

### 4.6.6 文件上传：CSV 批量导入

```python
@router.post("/import", response_model=ApiResponse[TaskImportResult], summary="CSV 批量导入")
async def import_tasks(
    service: TaskServiceDep,
    settings: SettingsDep,
    file: UploadFile = File(..., description="CSV 文件，列：title,description,priority,assignee,tags,due_date"),
) -> ApiResponse[TaskImportResult]:
    filename = (file.filename or "").lower()
    if not filename.endswith((".csv", ".txt")):
        raise ValidationException("只支持 .csv / .txt 文件", {"filename": file.filename})

    # utf-8-sig：Excel 导出的 CSV 常带 BOM，直接用 utf-8 解码会在首列出现 \ufeff
    content = (await file.read()).decode("utf-8-sig")

    result = await service.import_from_csv(content, max_rows=settings.max_import_rows)
    return ApiResponse.ok(result, message=f"导入完成，成功 {result.created} 条")
```

三个实战要点：

**① 需要 `python-multipart`**
没装会报 `Form data requires python-multipart to be installed`。这是 FastAPI 新手最常遇到的报错之一。

**② `utf-8-sig` 而不是 `utf-8`**
Excel 导出的 CSV 带 BOM（字节序标记 `\xef\xbb\xbf`）。用 `utf-8` 解码，第一列名会变成 `\ufefftitle`，然后"标题字段找不到"。`utf-8-sig` 会自动吃掉 BOM。

**③ `max_rows` 从配置读，不硬编码**
`settings.max_import_rows` —— 上传大小限制是**运维策略**，应该能不重启改不了就改配置。

真实调用（tasks.csv 里有 2 条合法、1 条空标题、1 条非法优先级）：

```bash
$ curl -X POST http://127.0.0.1:8123/api/v1/tasks/import -F "file=@tasks.csv"

{"code":0,"message":"导入完成，成功 2 条",
 "data":{"created":2,"skipped":2,
         "errors":["第4行: 标题为空","第5行: 非法优先级 'super'"],
         "task_ids":["8ec5dfd2e39e","4687219515ab"]}}
```

注意设计：**部分失败不整体回滚**，而是逐行记录错误。批量导入场景下这比"一行错全部失败"友好得多。

Service 里的实现要点：

```python
for line_no, row in enumerate(reader, start=2):     # 第 1 行是表头
    if line_no - 1 > max_rows:
        result.errors.append(f"第{line_no}行: 超出单次导入上限 {max_rows}")
        break

    title = (row.get("title") or "").strip()
    if not title:
        result.skipped += 1
        result.errors.append(f"第{line_no}行: 标题为空")
        continue        # 跳过这行，继续处理下一行
```

> ⚠️ **生产注意**：`await file.read()` 会把整个文件读进内存。真实项目应该用 `file.file` 流式读取，或者先传对象存储（S3/OSS）拿到 key 再异步处理。直接读大文件会 OOM。

### 4.6.7 流式响应：SSE 导出

```python
@router.get("/export/stream", summary="流式导出（SSE）")
async def export_tasks_stream(service: TaskServiceDep) -> StreamingResponse:
    """以 Server-Sent Events 逐条推送任务。

    为什么不用一次性返回 JSON？数据量大时，流式可以：
    - 边查边推，首字节时间（TTFB）极短；
    - 服务端内存占用恒定。
    """
    async def event_stream() -> AsyncIterator[bytes]:
        count = 0
        yield b'event: start\ndata: {"status":"start"}\n\n'
        async for record in service.iter_all():
            payload = TaskRead.from_record(record).model_dump(mode="json")
            line = f"event: task\ndata: {json.dumps(payload, ensure_ascii=False)}\n\n"
            yield line.encode("utf-8")
            count += 1
            await asyncio.sleep(0.05)   # 故意慢一点，方便肉眼看到"流"的效果
        yield f'event: done\ndata: {{"count":{count}}}\n\n'.encode("utf-8")

    return StreamingResponse(
        event_stream(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no",   # 告诉 Nginx 不要缓冲
        },
    )
```

**SSE 格式**：每条消息是 `event: <类型>\ndata: <内容>\n\n`（**两个换行结尾**）。前端用 `EventSource` 接收：

```javascript
const es = new EventSource("/api/v1/tasks/export/stream");
es.addEventListener("task", (e) => console.log(JSON.parse(e.data)));
es.addEventListener("done", (e) => es.close());
```

真实输出（`curl -N` 禁用缓冲）：

```
event: start
data: {"status":"start"}

event: task
data: {"title": "修复登录页 500 错误", "description": null, "priority": "urgent", "id": "demo-001", ...}

event: task
data: {"title": "补充单元测试覆盖率到 80%", "priority": "high", "id": "demo-002", ...}
```

**三个坑：**

1. **`X-Accel-Buffering: no`** —— Nginx 默认缓冲响应，不设这个 header，SSE 会被攒到最后一次性发出，"流"就变"批"了。
2. **中间件不能缓冲响应体** —— 见 4.8 节，`BaseHTTPMiddleware` 会破坏流式。
3. **客户端也要禁用缓冲** —— curl 加 `-N`，浏览器 `EventSource` 自动处理。

> **对比 WebSocket**：SSE 是**单向**（服务端→客户端），基于 HTTP，自动重连；WebSocket 是双向，协议更重。
> 只需推送（通知、进度、AI 流式输出）用 SSE 更简单。

### 4.6.8 BackgroundTasks：轻量后台任务

```python
@router.post("", response_model=ApiResponse[TaskRead], status_code=201)
async def create_task(
    payload: TaskCreate,
    service: TaskServiceDep,
    audit: AuditDep,
    background_tasks: BackgroundTasks,
) -> ApiResponse[TaskRead]:
    task = await service.create_task(payload)
    # 后台任务：响应先返回，之后再执行
    background_tasks.add_task(_write_audit_log, "create_task", task.id, audit.path)
    return ApiResponse.ok(task, message="创建成功")
```

`BackgroundTasks` 是 FastAPI 提供的参数（框架自动注入），`add_task` 注册的函数会在**响应发送之后**执行。

**适用边界**（代码注释里强调了）：

```python
def _write_audit_log(action: str, task_id: str, path: str) -> None:
    """BackgroundTasks 只适合"轻量、允许失败"的操作：
    它没有重试、没有持久化，进程一挂任务就没了。
    关键业务请用 Celery / ARQ 这类真正的任务队列。
    """
```

| 场景 | 用 `BackgroundTasks`？ |
|------|----------------------|
| 写审计日志、发站内通知 | ✅ 可以 |
| 发邮件、生成报表、调第三方 | ❌ 用 Celery / ARQ / Dramatiq |

> **Java 类比**：Spring 的 `@Async`，同样是"在 Spring 里够用，上生产要考虑线程池隔离和持久化"。

---

## 4.7 第七步：错误处理 —— 业务异常如何变成 HTTP 响应

### 4.7.1 异常体系设计

`src/taskflow/core/exceptions.py`：

```python
class AppException(Exception):
    """应用异常基类。"""
    def __init__(self, message: str, *, code: int = 500, status_code: int = 500,
                 details: dict[str, Any] | None = None) -> None:
        self.message = message
        self.code = code                  # 业务码（响应体里的 code）
        self.status_code = status_code    # HTTP 状态码
        self.details: dict[str, Any] = details or {}
        super().__init__(message)


class NotFoundException(AppException):
    """资源不存在 → 404。"""
    def __init__(self, resource: str, resource_id: str) -> None:
        super().__init__(f"{resource}不存在: {resource_id}", code=404, status_code=404,
                         details={"resource": resource, "id": resource_id})


class ValidationException(AppException):
    """业务校验失败 → 422。"""
    def __init__(self, message: str, details: dict[str, Any] | None = None) -> None:
        super().__init__(message, code=422, status_code=422, details=details)


class ConflictException(AppException):
    """状态冲突 → 409。"""
    def __init__(self, message: str, details: dict[str, Any] | None = None) -> None:
        super().__init__(message, code=409, status_code=409, details=details)


class ExternalServiceException(AppException):
    """调用外部服务失败 → 503。"""
    def __init__(self, service: str, message: str) -> None:
        super().__init__(f"外部服务异常({service}): {message}", code=503, status_code=503,
                         details={"service": service})
```

**为什么要区分 `code`（业务码）和 `status_code`（HTTP 码）？**

- HTTP 状态码**种类有限**（40 多个常用），表达不了细分业务错误；
- 业务码是给**前端程序**看的（用于分支处理），HTTP 码是给**基础设施**看的（网关、监控、CDN）。

比如"任务不存在"和"用户不存在"都是 404，但业务码可以不同，前端据此显示不同提示。

> **Java 类比**：自定义 `RuntimeException` 体系 + `@ControllerAdvice` / `@ExceptionHandler`。

### 4.7.2 四个异常处理器

```python
async def app_exception_handler(request: Request, exc: AppException) -> JSONResponse:
    """处理所有 AppException 子类。"""
    logger.warning(f"业务异常 [{exc.code}] {exc.message} | path={request.url.path}")
    return JSONResponse(
        status_code=exc.status_code,
        content={"code": exc.code, "message": exc.message, "details": exc.details or None},
    )


async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    """把 FastAPI 默认的 422 错误改成和 AppException 一致的结构。"""
    errors = [
        {
            "field": ".".join(str(part) for part in err["loc"][1:]) or "body",
            "message": err["msg"],
            "type": err["type"],
        }
        for err in exc.errors()
    ]
    return JSONResponse(status_code=422,
                        content={"code": 422, "message": "参数校验失败", "details": errors})


async def http_exception_handler(request: Request, exc: StarletteHTTPException) -> JSONResponse:
    """统一 HTTPException（404/405 等框架抛出的）的响应体。"""
    return JSONResponse(status_code=exc.status_code,
                        content={"code": exc.status_code, "message": exc.detail, "details": None})


async def unhandled_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    """兜底处理器：避免把堆栈抛给客户端。"""
    logger.exception(f"未捕获异常 | path={request.url.path} | {exc}")
    return JSONResponse(status_code=500,
                        content={"code": 500, "message": "服务器内部错误", "details": None})
```

**为什么要改 FastAPI 默认的 422 格式？**

默认长这样（对前端极不友好）：

```json
{"detail":[{"loc":["body","title"],"msg":"field required","type":"value_error.missing"}]}
```

统一后：

```json
{"code":422,"message":"参数校验失败",
 "details":[{"field":"title","message":"Field required","type":"missing"}]}
```

前端可以统一从 `details[].field` 取字段做表单高亮，不用解析 `loc` 数组的嵌套结构。

**`err["loc"][1:]` 是什么？**

`loc` 形如 `("body", "title")` 或 `("query", "page")`——第一个元素是**参数来源**，后面是字段路径。去掉第一段只留字段路径，正好是前端表单需要的字段名。

**兜底处理器为什么必须有？**

不注册的话，未捕获异常会：

1. 把**完整 traceback 返回给客户端**（泄露文件路径、代码片段——安全事故）；
2. 响应格式和你的 `ApiResponse` 完全不一致（前端解析崩溃）。

`logger.exception()` 会把堆栈写进日志（排查用），但响应里只有一句人话。

### 4.7.3 注册顺序与匹配规则

```python
app.add_exception_handler(AppException, app_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)
app.add_exception_handler(StarletteHTTPException, http_exception_handler)
app.add_exception_handler(Exception, unhandled_exception_handler)
```

**匹配规则**：先找**精确匹配**（异常类本身），找不到就沿 MRO 找**父类中最近的注册项**，最后才落到 `Exception`。

所以 `NotFoundException`（继承自 `AppException`）会走 `app_exception_handler` —— **子类自动命中父类的 handler**，不用给每个异常类都注册一遍。

> ⚠️ **注意 `RequestValidationError` 的位置**：它继承自 `Exception`，但因为先精确匹配到了自己的 handler，不会被兜底拦截。
> 注册顺序在 Starlette 里不影响匹配优先级（按类型查找，不是按注册顺序遍历），**但为了可读性建议按"具体到宽泛"排列**。

---

## 4.8 第八步：中间件与日志

### 4.8.1 中间件的执行顺序（最容易搞反）

```python
app.add_middleware(RateLimitMiddleware, ...)
app.add_middleware(RequestContextMiddleware)
app.add_middleware(CORSMiddleware, ...)
```

**后添加的更靠外层**。上面这段的实际执行顺序：

```
请求 → CORS → RequestContext → RateLimit → 路由 → RateLimit → RequestContext → CORS → 响应
```

**洋葱模型**：请求从外往里穿，响应从里往外穿。

> 记忆方法：`add_middleware` 相当于"往已有中间件外面再包一层"。

### 4.8.2 纯 ASGI 中间件 vs `BaseHTTPMiddleware`

本项目同时用了两种写法，**这是有意为之的对比教学**：

```python
# ① 纯 ASGI 中间件（直接实现 __call__）
class RequestContextMiddleware:
    def __init__(self, app: ASGIApp) -> None:
        self.app = app

    async def __call__(self, scope: Scope, receive: Receive, send: Send) -> None:
        if scope["type"] != "http":
            await self.app(scope, receive, send)     # websocket / lifespan 直接放行
            return

        request_id = uuid4().hex[:12]
        started = time.perf_counter()
        status_holder: dict[str, int] = {"status": 500}

        async def send_wrapper(message: MutableMapping[str, Any]) -> None:
            # 抓住 http.response.start 这一帧，就能拿到状态码并往响应头里塞东西
            if message["type"] == "http.response.start":
                status_holder["status"] = message["status"]
                headers = MutableHeaders(scope=message)
                headers.append("X-Request-ID", request_id)
            await send(message)

        try:
            await self.app(scope, receive, send_wrapper)
        finally:
            elapsed_ms = (time.perf_counter() - started) * 1000
            logger.info(f"{scope['method']} {scope['path']} → {status_holder['status']} "
                        f"({elapsed_ms:.1f}ms) rid={request_id}")


# ② BaseHTTPMiddleware（继承 + 实现 dispatch）
class RateLimitMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next) -> Response:
        ...
        return await call_next(request)
```

**为什么 `RequestContextMiddleware` 必须写成纯 ASGI？**

因为 `BaseHTTPMiddleware` 会把响应体**全部收进内存**再转发。对普通 JSON 响应无所谓，但会让 `StreamingResponse` / SSE **流不起来**——客户端会等到响应结束才收到数据，4.6.7 节的 SSE 导出就废了。

| 中间件类型 | 写法 | 优点 | 缺点 |
|-----------|------|------|------|
| 纯 ASGI | 实现 `__call__(scope, receive, send)` | 性能最好，**支持流式** | 要理解 ASGI 协议细节 |
| `BaseHTTPMiddleware` | 继承 + `dispatch(request, call_next)` | 简单，拿到 `Request`/`Response` 对象 | **破坏流式响应**，有额外开销 |

**选择规则**：中间件要碰响应体 → 必须用纯 ASGI；只做请求侧处理（限流、鉴权）→ `BaseHTTPMiddleware` 够用。

**`send_wrapper` 的技巧**：ASGI 的响应是通过 `send()` 发送的一串消息。抓住 `http.response.start` 这一帧，就能拿到状态码并**往响应头里插入内容**：

```python
if message["type"] == "http.response.start":
    headers = MutableHeaders(scope=message)
    headers.append("X-Request-ID", request_id)   # 加响应头
```

效果（真实输出）：

```bash
$ curl -i http://127.0.0.1:8123/health
HTTP/1.1 200 OK
x-request-id: 5451cb13e99e          # ← 中间件注入
```

**`X-Request-ID` 的价值**：一次请求经过网关、应用、数据库，日志散落各处。有了统一的 request_id，就能把所有相关日志串起来——**分布式追踪的最小实现**。生产环境会升级成 OpenTelemetry TraceId（见 4.13）。

### 4.8.3 限流中间件

```python
class RateLimitMiddleware(BaseHTTPMiddleware):
    """滑动窗口限流（内存版）。"""
    def __init__(self, app: ASGIApp, max_requests: int = 100, window_seconds: int = 60):
        super().__init__(app)
        self.max_requests = max_requests
        self.window_seconds = window_seconds
        self._hits: dict[str, list[float]] = defaultdict(list)

    async def dispatch(self, request: Request, call_next) -> Response:
        client_ip = request.client.host if request.client else "unknown"
        now = time.time()

        # 只保留时间窗内的记录
        self._hits[client_ip] = [t for t in self._hits[client_ip] if now - t < self.window_seconds]

        if len(self._hits[client_ip]) >= self.max_requests:
            return Response(
                content='{"code":429,"message":"请求过于频繁，请稍后再试","details":null}',
                status_code=429,
                media_type="application/json",
                headers={"Retry-After": str(self.window_seconds)},
            )

        self._hits[client_ip].append(now)
        return await call_next(request)
```

**局限（代码注释里已标明）**：内存字典**只对本进程有效**。多副本部署时，每个副本独立计数——实际限流阈值变成 `N × max_requests`。生产环境必须用 Redis + Lua 脚本（原子操作）。

> 另外：`request.client.host` 在有反向代理时拿到的是**代理的 IP**。要用真实客户端 IP 得解析 `X-Forwarded-For`（并配置 `--forwarded-allow-ips`），否则所有请求都显示同一个 IP，限流直接把所有人挡在外面。

### 4.8.4 日志：loguru

```python
def setup_logging(level: str = "DEBUG", json_logs: bool = False) -> None:
    """配置全局日志。

    loguru 默认就有一个输出到 stderr 的 sink，
    所以第一步必须先 remove()，否则日志会打印两遍。
    """
    logger.remove()

    if json_logs:
        # 生产环境：输出 JSON，方便 ELK / Loki 采集
        logger.add(sys.stdout, level=level, serialize=True)
    else:
        # 开发环境：彩色、单行、带时间
        logger.add(
            sys.stdout,
            level=level,
            colorize=True,
            format=("<green>{time:HH:mm:ss.SSS}</green> | "
                    "<level>{level: <8}</level> | "
                    "<cyan>{name}</cyan>:<cyan>{function}</cyan> - "
                    "<level>{message}</level>"),
        )
```

**为什么用 loguru 而不是标准库 `logging`？**

标准库 `logging` 要配 Handler、Formatter、Filter、Logger 层级，二十行起步，且 `getLogger(__name__)` 到处传。loguru 的核心优势：

- **零配置可用**：`from loguru import logger; logger.info("x")`
- **`logger.remove()` 一键重置**（避免重复输出）
- **`logger.exception()` 自动带堆栈**
- **`serialize=True` 一行切换到 JSON 输出**

> **Java 类比**：loguru ≈ SLF4J + Logback，但省掉了 logback.xml。
>
> **标准库日志的第一大坑**：`logging` 的 root logger 会传播，配置不当就出现"每条日志打两遍"。loguru 的 `remove()` 从设计上解决了这个。

**使用要点**：

```python
logger.debug("详细调试信息")            # 开发用
logger.info("创建任务 {}: {}", id, title)   # 关键业务动作
logger.warning("限流触发: {}", ip)         # 可恢复的异常
logger.error("调用失败")                   # 需要处理
logger.exception("未捕获异常")             # 自动附加完整堆栈（只在 except 块里用）
```

**用 `{}` 占位而不是 f-string**：

```python
logger.info(f"创建任务 {task_id}")     # ❌ 不管什么日志级别，字符串都会先拼接
logger.info("创建任务 {}", task_id)     # ✅ 级别不够时跳过格式化，省性能
```

---

## 4.9 第九步：配置管理

### 4.9.1 pydantic-settings

```python
from functools import lru_cache
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """应用配置。字段默认值 = 开发环境默认值，可被环境变量/.env 覆盖。"""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        env_prefix="TASKFLOW_",      # 避免和系统里其它项目的同名变量冲突
        case_sensitive=False,        # TASKFLOW_DEBUG 和 taskflow_debug 都认
        extra="ignore",              # .env 里有陌生变量时不要报错
    )

    # ---- 应用 ----
    app_name: str = "TaskFlow API"
    app_env: str = "development"     # development | testing | production
    debug: bool = False
    host: str = "127.0.0.1"
    port: int = 8000

    # ---- 外部「用户服务」----
    user_service_url: str = "http://127.0.0.1:9999"
    user_service_timeout: float = 2.0
    user_service_enabled: bool = False   # 关闭时跳过校验（离线也能跑）

    # ---- 限流 ----
    rate_limit_max_requests: int = 100
    rate_limit_window_seconds: int = 60

    # ---- 批量导入 ----
    max_import_rows: int = 1000

    @property
    def is_production(self) -> bool:
        return self.app_env == "production"


@lru_cache
def get_settings() -> Settings:
    """配置单例。lru_cache 保证全进程只解析一次 .env。"""
    return Settings()
```

**配置优先级**（从高到低）：

```
环境变量  >  .env 文件  >  字段默认值
```

也就是说，`TASKFLOW_DEBUG=true python -m uvicorn ...` 会覆盖 `.env` 里的 `TASKFLOW_DEBUG=false`。

**四个关键配置：**

| 配置 | 为什么需要 |
|------|-----------|
| `env_prefix="TASKFLOW_"` | 系统里可能有其它项目也用 `DEBUG` 变量。加前缀避免"我改了 A 项目的配置，B 项目跟着变" |
| `case_sensitive=False` | Windows 环境变量不区分大小写，Linux 区分。关掉能避免跨平台差异 |
| `extra="ignore"` | `.env` 里有拼写错的变量时**静默忽略**，而不是启动失败。生产环境可改成 `"forbid"` 严格模式 |
| `@lru_cache` | 每次 `get_settings()` 都重新读文件很浪费；缓存后全进程只解析一次 |

> **Java 类比**：`@ConfigurationProperties(prefix = "taskflow")` + `@Bean` 单例。
> 差别：Spring Boot 有 `application-{profile}.yml` 多套配置；Python 生态更倾向**环境变量优先**（12-factor），`.env` 只用于本地开发。

**类型自动转换**是 pydantic-settings 的核心价值：

```python
# .env
TASKFLOW_PORT=9000
TASKFLOW_DEBUG=true
TASKFLOW_RATE_LIMIT_WINDOW_SECONDS=120

# Python 里拿到的是
settings.port                        # 9000 (int，不是 "9000")
settings.debug                       # True (bool，不是 "true")
settings.rate_limit_window_seconds   # 120 (int)
```

手写 `os.getenv()` 你得自己 `int()` / 处理 `"true"` / `"1"` / `"yes"` 各种写法，还有转换失败的异常处理。

### 4.9.2 12-factor 与密钥管理

**铁律：`.env` 绝不进版本库。**

```gitignore
.env
.env.*
!.env.example
```

提交一个 `.env.example`（只含键名和假值）作为模板：

```bash
# .env.example
TASKFLOW_APP_ENV=development
TASKFLOW_DEBUG=true
TASKFLOW_USER_SERVICE_URL=http://127.0.0.1:9999
TASKFLOW_USER_SERVICE_ENABLED=false
```

**生产环境的密钥**（数据库密码、API Key）**不要放在 `.env` 文件里**——用：

- Kubernetes Secret / ConfigMap
- 云厂商的密钥管理服务（阿里云 KMS、AWS Secrets Manager）
- Vault

原因：`.env` 文件会被打进镜像层、写进日志、被 `docker inspect` 看到。

---

## 4.10 第十步：装配 —— `create_app()` 工厂与生命周期

### 4.10.1 为什么用工厂函数

```python
def create_app() -> FastAPI:
    """应用工厂。"""
    settings = get_settings()
    setup_logging(...)

    @asynccontextmanager
    async def lifespan(app: FastAPI) -> AsyncIterator[None]:
        ...

    app = FastAPI(title=settings.app_name, ..., lifespan=lifespan)
    # 注册中间件、异常处理器、路由
    return app


app = create_app()      # uvicorn 入口：uvicorn taskflow.main:app --reload
```

**对比"模块级 `app = FastAPI()`"的写法：**

| | 工厂函数 | 模块级变量 |
|---|---|---|
| 测试时创建不同配置实例 | ✅ `create_app()` 每次全新 | ❌ 全局单例互相污染 |
| 多次 import 的副作用 | ✅ 无 | ❌ 顶层代码执行多次 |
| 延迟初始化 | ✅ 调用时才初始化 | ❌ import 即初始化 |

特别是**模块级代码在 import 时就执行**——测试里 import 多次就会初始化多次，而且没法在关闭时优雅释放资源。

> **Java 类比**：工厂函数 ≈ `new SpringApplicationBuilder().profiles("test").run()`。
> Spring Boot 测试里 `@SpringBootTest` 会缓存 ApplicationContext，本质上也是为了复用/隔离应用实例。

### 4.10.2 lifespan：启动与关闭

```python
@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncIterator[None]:
    """应用生命周期：yield 之前是启动，yield 之后是关闭。

    对应 Java：@PostConstruct / @PreDestroy。
    """
    # ===== 启动 =====
    app.state.repository = InMemoryTaskRepository()
    await seed_demo_data(app.state.repository)

    app.state.http_client = HttpClient(
        base_url=settings.user_service_url,
        timeout=settings.user_service_timeout,
    )
    app.state.user_service = UserService(
        app.state.http_client, enabled=settings.user_service_enabled
    )
    logger.info(f"{settings.app_name} 启动完成 (env={settings.app_env}, debug={settings.debug})")

    yield   # ← 应用在这里对外提供服务

    # ===== 关闭 =====
    await app.state.http_client.aclose()
    logger.info("应用已关闭，资源已释放")
```

**`yield` 之后的代码会在应用关闭时执行**——这是释放资源的正确位置：

- 关闭数据库连接池
- 关闭 HTTP 客户端（`aclose()`）
- 等待后台任务完成
- 刷盘、上报指标

**为什么必须放在 lifespan 而不是模块顶层？**

模块顶层代码在 `import` 时执行。如果测试 import 了 50 次，就会创建 50 个连接池——而且没有任何地方能关闭它们。

**`app.state` 是什么？**

FastAPI 提供的**应用级共享存储**，类型是任意对象。本项目把"活在整个进程生命周期"的单例放在这里：

```python
app.state.repository     # 仓储（数据在内存里，必须跨请求共享）
app.state.http_client    # HTTP 连接池（复用连接）
app.state.user_service   # 外部服务客户端
```

然后依赖函数从这里取（4.5 节）：

```python
def get_repository(request: Request) -> TaskRepository:
    return request.app.state.repository
```

**为什么不用全局变量？**

因为全局变量和测试冲突：测试 A 往全局仓储塞了数据，测试 B 读到了——用例互相污染。用 `app.state` + `dependency_overrides`，每个测试用新的 app 实例，数据天然隔离。

> **Java 类比**：`app.state` ≈ ApplicationContext 里的单例 Bean。

### 4.10.3 完整的装配顺序

```python
app = FastAPI(
    title=settings.app_name,
    description="第四章贯穿案例：团队协作任务 API",
    version=__version__,
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc",
    # openapi_url=None  # 生产环境可以关掉文档
)

# ===== 中间件 =====（后添加的更外层）
app.add_middleware(RateLimitMiddleware, max_requests=..., window_seconds=...)
app.add_middleware(RequestContextMiddleware)
app.add_middleware(CORSMiddleware,
                   allow_origins=["*"] if settings.debug else [],  # 生产必须写具体域名
                   allow_credentials=True, allow_methods=["*"], allow_headers=["*"])

# ===== 异常处理器 =====
app.add_exception_handler(AppException, app_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)
app.add_exception_handler(StarletteHTTPException, http_exception_handler)
app.add_exception_handler(Exception, unhandled_exception_handler)

# ===== 路由 =====
app.include_router(health.router, tags=["健康检查"])
app.include_router(tasks.router, prefix="/api/v1/tasks", tags=["任务"])

@app.get("/", tags=["根"])
async def root() -> dict[str, str]:
    return {"name": settings.app_name, "docs": "/docs", "health": "/health"}
```

**CORS 的生产配置**：

```python
allow_origins=["*"] if settings.debug else []   # ← 开发环境放开，生产环境收紧
```

`allow_origins=["*"]` + `allow_credentials=True` 是**危险组合**（浏览器会拒绝，且语义上等于对所有站点开放凭证）。生产环境必须写具体域名：

```python
allow_origins=["https://app.example.com", "https://admin.example.com"]
```

### 4.10.4 健康检查：两个探针为什么要分开

```python
@router.get("/health", response_model=ApiResponse[dict], summary="存活探针")
async def health(settings: SettingsDep) -> ApiResponse[dict]:
    """只返回进程本身的状态。

    千万别在这里查数据库 —— 数据库抖一下就返回 500，
    K8s 会以为容器挂了并反复重启，把小故障放大成雪崩。
    """
    return ApiResponse.ok({"status": "ok", "env": settings.app_env})


@router.get("/health/ready", response_model=ApiResponse[dict], summary="就绪探针")
async def readiness(repository: RepositoryDep) -> ApiResponse[dict]:
    """检查依赖是否可用。这里用仓储读一次数据来验证。"""
    tasks = await repository.list_all()
    return ApiResponse.ok({"status": "ready", "tasks": len(tasks)})
```

| 探针 | K8s 用途 | 失败后果 | 应该检查什么 |
|------|---------|---------|------------|
| `/health`（liveness） | 容器是否活着 | **重启容器** | 只查进程本身 |
| `/health/ready`（readiness） | 能否接收流量 | **摘除出 Service** | 检查依赖（DB、缓存） |

**这是 Kubernetes 部署的经典踩坑点**：把依赖检查写进 liveness，数据库一抖动 → 所有 Pod 同时重启 → 重启后数据库压力更大 → 雪崩。

---
## 4.11 第十一步：跑起来，用真实请求验证

### 4.11.1 启动

```bash
cd taskflow

# 方式一：直接跑（PYTHONPATH 指向 src，因为用了 src 布局）
PYTHONPATH=src python -m uvicorn taskflow.main:app --reload --port 8123

# 方式二：如果先装了包（uv sync / pip install -e .），不需要 PYTHONPATH
uvicorn taskflow.main:app --reload --port 8123
```

参数说明：

| 参数 | 作用 |
|------|------|
| `taskflow.main:app` | 模块路径:变量名（`main.py` 里的 `app = create_app()`） |
| `--reload` | 代码改动自动重启（**仅开发用**，生产千万别开） |
| `--port 8123` | 端口，默认 8000 |
| `--host 0.0.0.0` | 监听所有网卡（容器部署必须；默认只监听 127.0.0.1） |
| `--workers 4` | 多进程（**生产用**，数量通常 = CPU 核数） |

启动后你可以立刻打开三个页面：

- <http://127.0.0.1:8123/docs> — Swagger UI，可直接点按钮发请求
- <http://127.0.0.1:8123/redoc> — ReDoc，更适合阅读
- <http://127.0.0.1:8123/openapi.json> — OpenAPI 规范原文（前端可据此生成 TS 类型）

**这些文档是免费的**——因为你写了类型注解和 `response_model`，FastAPI 自动生成。Java 里要达到同样效果需要 `springdoc-openapi` + 一堆 `@Operation` 注解。

### 4.11.2 逐个接口验证（真实输出）

以下是实际运行本项目后的结果，可以直接对照学习。

**① 根路径与健康检查**

```bash
$ curl -s http://127.0.0.1:8123/
{"name":"TaskFlow API","docs":"/docs","health":"/health"}

$ curl -i http://127.0.0.1:8123/health
HTTP/1.1 200 OK
content-type: application/json
x-request-id: 5451cb13e99e          # ← 中间件注入的 request_id

{"code":0,"message":"ok","data":{"status":"ok","env":"development"}}
```

**② 创建任务（201）**

```bash
$ curl -s -X POST http://127.0.0.1:8123/api/v1/tasks \
    -H "Content-Type: application/json" \
    -d '{"title":"修复登录页 500","priority":"urgent","assignee":"alice","tags":["backend","bug"]}'

{"code":0,"message":"创建成功",
 "data":{"title":"修复登录页 500","description":null,"priority":"urgent","assignee":"alice",
         "tags":["backend","bug"],"due_date":null,"id":"bcab92d2677e","status":"todo",
         "created_at":"2026-09-02T17:24:27.553802","updated_at":"2026-09-02T17:24:27.553802",
         "completed_at":null,"is_overdue":false}}
```

注意：`id`、`status`、`created_at` **都是服务端生成的**——因为入参模型 `TaskCreate` 里没有这些字段，客户端传了也会被忽略。

**③ 分页查询 + 排序**

```bash
$ curl -s "http://127.0.0.1:8123/api/v1/tasks?page=1&page_size=2&sort_by=priority"

{"code":0,"message":"ok",
 "data":{"items":[ {...urgent...}, {...urgent...} ],
         "total":6,"page":1,"page_size":2,"total_pages":3}}
```

`total=6`（符合条件的总数），`total_pages=3` —— 前端据此渲染分页器。

**④ 统计（注意路由顺序）**

```bash
$ curl -s http://127.0.0.1:8123/api/v1/tasks/stats

{"code":0,"message":"ok",
 "data":{"total":6,"by_status":{"todo":4,"in_progress":1,"done":1},
         "by_priority":{"urgent":2,"high":1,"medium":2,"low":1},
         "overdue":1,"completion_rate":0.1667}}
```

如果 `/{task_id}` 注册在 `/stats` 之前，这个请求会返回"任务 stats 不存在"。

**⑤ 参数校验失败（422）**

```bash
$ curl -s -X POST http://127.0.0.1:8123/api/v1/tasks \
    -H "Content-Type: application/json" -d '{"priority":"high"}'

{"code":422,"message":"参数校验失败",
 "details":[{"field":"title","message":"Field required","type":"missing"}]}
```

注意 `message` 是"参数校验失败" —— 这是**框架层面**的校验（字段缺失）。

**⑥ 业务校验失败（也是 422，但原因不同）**

```bash
$ curl -s -X POST http://127.0.0.1:8123/api/v1/tasks \
    -H "Content-Type: application/json" -d '{"title":"x","assignee":"nobody"}'

{"code":422,"message":"负责人不存在: nobody","details":{"assignee":"nobody"}}
```

同样是 422，但 `message` 说的是**业务原因**。区分方式：`message == "参数校验失败"` 是框架校验（看 `details[].field`），否则是业务校验（看 `message`）。

**⑦ 资源不存在（404）**

```bash
$ curl -s http://127.0.0.1:8123/api/v1/tasks/nope

{"code":404,"message":"任务不存在: nope","details":{"resource":"任务","id":"nope"}}
```

**⑧ 状态机拒绝（409）**

```bash
$ curl -s -X POST http://127.0.0.1:8123/api/v1/tasks/demo-004/transition \
    -H "Content-Type: application/json" -d '{"target":"in_progress"}'

{"code":409,"message":"不允许的状态流转: done → in_progress",
 "details":{"current":"done","target":"in_progress","allowed":[]}}
```

`allowed: []` 明确告诉客户端：已完成是终态，没有任何可流转的目标。

**⑨ CSV 批量导入（部分成功）**

tasks.csv（4 行数据，2 合法 2 非法）：

```csv
title,description,priority,assignee,tags,due_date
接入支付回调,,high,alice,backend;payment,2026-10-01
修复导出乱码,Excel 打开乱码,medium,bob,bug,2026-09-10
,空标题,low,carol,,
第六行,优先级非法,super,dave,,2026-13-45
```

```bash
$ curl -s -X POST http://127.0.0.1:8123/api/v1/tasks/import -F "file=@tasks.csv"

{"code":0,"message":"导入完成，成功 2 条",
 "data":{"created":2,"skipped":2,
         "errors":["第4行: 标题为空","第5行: 非法优先级 'super'"],
         "task_ids":["8ec5dfd2e39e","4687219515ab"]}}
```

行号从 2 开始计（表头是第 1 行），和 Excel 里看到的行号一致——**这个细节决定用户能不能快速定位问题**。

**⑩ 流式导出（SSE）**

```bash
$ curl -s -N http://127.0.0.1:8123/api/v1/tasks/export/stream

event: start
data: {"status":"start"}

event: task
data: {"title": "修复登录页 500 错误", "id": "demo-001", "priority": "urgent", ...}

event: task
data: {"title": "补充单元测试覆盖率到 80%", "id": "demo-002", "priority": "high", ...}
```

`curl -N` 是禁用缓冲的意思。不加的话 curl 会攒着，你看不到"一条条蹦出来"的效果。

### 4.11.3 用 `/docs` 调试

打开 Swagger 页面，你会看到：

- 每个接口的**请求体示例**（来自 `Field(examples=...)`）
- **每个字段的校验规则**（来自 `Field(min_length=..., ge=...)`）
- **枚举字段的下拉框**（来自 `Literal` 和 `str, Enum`）
- **完整的响应结构**（来自 `response_model`）

这些全部是从类型注解推导出来的，没有一行额外文档代码。

---

## 4.12 第十二步：测试 —— 让代码敢改

### 4.12.1 测试策略

本项目 85 个测试分两层，实际分布如下：

```
tests/
├── unit/
│   ├── test_models.py        23 个  → 纯 Pydantic 校验（含参数化）
│   ├── test_task_service.py  22 个  → 直接调 Service，不启 HTTP（毫秒级）
│   └── test_http_client.py   12 个  → httpx MockTransport，不发真实网络请求
└── integration/
    └── test_api.py           28 个  → 走完整 HTTP 链路（含中间件、DI、异常处理）
──────────────────────────────────────
单元 57 + 集成 28 = 85
```

跑一遍看分层耗时：

```bash
$ pytest tests/unit -q
57 passed in 0.37s          # 毫秒级：没有 HTTP、没有 IO

$ pytest tests/integration -q
28 passed in 17.40s         # 慢 40 倍：每个用例都要走完整 ASGI 栈
```

> **注意这个 40 倍差距**。它决定了测试策略：能下沉到单元测试的规则，绝不放进集成测试。
> `test_api.py` 之所以还要 28 个，是因为状态码映射、中间件、`dependency_overrides`
> 这些"装配层"的东西，只有走 HTTP 才测得出来。

**为什么这样分？** 经典测试金字塔：

| 层级 | 速度 | 覆盖范围 | 数量占比 |
|------|------|---------|---------|
| 单元测试（Service/Model） | 毫秒 | 单个类 | 最多 |
| 集成测试（HTTP 链路） | 几十毫秒 | 完整请求 | 中等 |
| 端到端（真实部署） | 秒级 | 全系统 | 最少 |

**核心原则：业务规则尽量在单元测试覆盖。** Service 层的状态机有 20 多种流转组合，用 HTTP 测试要发 20 个请求；直接调 Service 方法，几毫秒一个。

### 4.12.2 conftest.py：夹具设计

```python
# 固定"当前时间"，让涉及日期的断言可复现（例如逾期判断）
FIXED_NOW = datetime(2026, 1, 15, 10, 0, 0)


class FakeUserService:
    """用户服务的测试替身：不联网，只认一份固定名单。

    对应 Java：@MockBean UserService。
    """
    def __init__(self, known: set[str] | None = None, *, enabled: bool = True) -> None:
        self.known = known or {"alice", "bob", "carol"}
        self.enabled = enabled
        self.calls: list[str] = []

    async def exists(self, username: str) -> bool:
        self.calls.append(username)          # 记录调用，可断言"是否真的校验了"
        if not self.enabled:
            return True
        return username in self.known


SEED_ROWS = [
    ("task-001", "修复登录 500", TaskPriority.URGENT, TaskStatus.TODO,
     "alice", ["backend"], FIXED_NOW.date() - timedelta(days=5), 5),   # 逾期
    ("task-002", "补充单元测试", TaskPriority.HIGH, TaskStatus.IN_PROGRESS,
     "bob", ["test"], FIXED_NOW.date() + timedelta(days=10), 4),
    ("task-003", "升级依赖", TaskPriority.MEDIUM, TaskStatus.DONE,
     "alice", ["ops"], FIXED_NOW.date() + timedelta(days=10), 3),
    ("task-004", "下线旧接口", TaskPriority.LOW, TaskStatus.CANCELLED,
     None, [], None, 2),
    ("task-005", "写技术周报", TaskPriority.MEDIUM, TaskStatus.TODO,
     "carol", ["docs"], None, 1),
]


def seed_tasks(repository: InMemoryTaskRepository) -> InMemoryTaskRepository:
    """往仓储里塞 5 条固定数据。

    仓储的 add 是 async 方法，这里在同步上下文里用 asyncio.run 跑一次。
    """
    async def _fill() -> None:
        for task_id, title, priority, status, assignee, tags, due_date, hours_ago in SEED_ROWS:
            created = FIXED_NOW - timedelta(hours=hours_ago)
            await repository.add(TaskRecord(...))

    asyncio.run(_fill())
    return repository
```

**为什么用普通函数 `seed_tasks()` 而不是 fixture 套 fixture？**

这是本项目踩过的一个坑，注释里写了：

```python
"""注意：填充数据用的是普通函数 seed_tasks()，不是 fixture 套 fixture。
fixture 之间互相依赖层级一多，很容易出现"拿到的是 fixture 定义对象而不是值"的坑。
"""
```

具体来说，如果写成：

```python
@pytest.fixture
def seeded_client(seeded_repository, user_service):     # ← seeded_repository 也是 fixture
    return build_test_client(seeded_repository, user_service)
```

在某些 pytest 版本下，`seeded_repository` 参数拿到的**不是仓储实例，而是 fixture 定义对象**。普通函数则完全没有这个问题——拿到什么就是什么。

**教训**：fixture 继承链超过两层就考虑用普通函数。

**夹具一览：**

| 夹具 | 内容 | 用途 |
|------|------|------|
| `repository` | 空仓储 | 每个用例全新，零耦合 |
| `seeded_repository` | 5 条固定数据 | Service 单测 |
| `client` | 空数据 TestClient | 创建/删除类测试 |
| `seeded_client` | 5 条数据 TestClient | 查询/统计类测试 |
| `fixed_now` | 固定的"现在" | 时间相关断言 |

### 4.12.3 单元测试：不启 HTTP

```python
def build_service(repository, user_service=None) -> TaskService:
    """构造一个"确定性"的 TaskService：时间固定、ID 可预测。"""
    counter = itertools.count(1)
    return TaskService(
        repository,
        user_service,
        now=lambda: FIXED_NOW,
        id_factory=lambda: f"task-{next(counter):03d}",
    )


async def test_create_task_sets_defaults():
    service = build_service(InMemoryTaskRepository())
    task = await service.create_task(TaskCreate(title="修复登录 500"))

    assert task.id == "task-001"              # ID 可预测
    assert task.status == TaskStatus.TODO
    assert task.priority == TaskPriority.MEDIUM
    assert task.created_at == FIXED_NOW       # 时间固定
    assert task.is_overdue is False


async def test_create_task_validates_assignee():
    service = build_service(InMemoryTaskRepository(), FakeUserService({"alice"}))

    ok = await service.create_task(TaskCreate(title="x", assignee="alice"))
    assert ok.assignee == "alice"

    with pytest.raises(ValidationException) as exc:      # 断言异常
        await service.create_task(TaskCreate(title="x", assignee="nobody"))
    assert "负责人不存在" in str(exc.value)
```

**注意 `async def test_xxx()`** —— 因为 `pyproject.toml` 里配了 `asyncio_mode = "auto"`，pytest 自动处理异步测试，不用加 `@pytest.mark.asyncio`。

**`pytest.raises` 的用法**：

```python
with pytest.raises(ValidationException) as exc:
    await service.create_task(...)

assert exc.value.code == 422                 # 可以检查异常对象的属性
```

### 4.12.4 集成测试：完整 HTTP 链路

```python
def test_create_task_returns_201(client: TestClient):
    response = client.post(
        "/api/v1/tasks",
        json={"title": "修复登录 500", "priority": "urgent", "assignee": "alice",
              "tags": ["backend", "bug"]},
    )
    assert response.status_code == 201, response.text    # ← 失败时打印响应体，好排查
    body = response.json()
    assert body["code"] == 0
    assert body["data"]["status"] == "todo"


def test_create_task_rejects_missing_title(client: TestClient):
    """参数校验失败：统一的 422 结构。"""
    response = client.post("/api/v1/tasks", json={"priority": "high"})
    assert response.status_code == 422
    body = response.json()
    assert body["message"] == "参数校验失败"
    assert body["details"][0]["field"] == "title"


def test_request_id_header_is_injected(client: TestClient):
    """中间件给每个响应都加上 X-Request-ID。"""
    response = client.get("/health")
    request_id = response.headers.get("x-request-id")
    assert request_id is not None
    assert len(request_id) == 12
```

**`TestClient` 是什么？**

它在进程内**直接调用 ASGI 应用**，不监听端口、不启 uvicorn。但中间件、依赖注入、异常处理器、路由**全部真实执行**。

> **Java 类比**：`@SpringBootTest(webEnvironment = MOCK)` + `MockMvc`。

**为什么断言里加 `response.text`？**

```python
assert response.status_code == 201, response.text
```

断言失败时，pytest 会把 `response.text`（实际响应体）打印出来。否则你只能看到"期望 201 实际 422"，还得手动复现才能知道为什么。**这是个小习惯，能省很多时间。**

### 4.12.5 运行结果

```bash
$ pytest -q --cov=taskflow --cov-report=term-missing

Name                                     Stmts   Miss  Cover   Missing
----------------------------------------------------------------------
src\taskflow\api\deps.py                    35      4    89%   33, 38, 43, 60
src\taskflow\api\middleware.py              45      2    96%   95-96
src\taskflow\api\routes\health.py           11      0   100%
src\taskflow\api\routes\tasks.py            62      0   100%
src\taskflow\core\exceptions.py             37      2    95%   137-138
src\taskflow\core\logging.py                 7      1    86%   21
src\taskflow\core\settings.py               21      0   100%
src\taskflow\main.py                        52      0   100%
src\taskflow\models\base.py                 20      0   100%
src\taskflow\models\task.py                 86      0   100%
src\taskflow\repositories\task_repo.py      70      4    94%   76, 83, 146, 149
src\taskflow\services\task_service.py      123      1    99%   132
src\taskflow\services\user_service.py       21      4    81%   33, 43-45
src\taskflow\utils\http_client.py           53      1    98%   79
----------------------------------------------------------------------
TOTAL                                      644     19    97%

======================= 85 passed, 1 warning in 22.37s ========================
```

**怎么看覆盖率报告：**

- **`Missing` 列**告诉你哪些行没测到。比如 `middleware.py` 的 95-96 行是限流触发的分支——说明"限流"这个场景没被测。
- **不要追求 100%**。有意义的目标是：**业务层（models/services）接近 100%，基础设施层（logging/deps）80%+**。
  `logging.py` 的 86% 是因为生产 JSON 日志分支没测——这类代码测了也没多大价值。

- **覆盖率不等于测试质量**。一个只调函数不断言的测试也能刷满覆盖率。覆盖率的作用是**找出"完全没被碰过"的代码**，而不是证明测试有效。

**关于那 1 个 warning：一次真实的排查**

先看它是什么：

```bash
$ pytest -W error::DeprecationWarning        # 把警告升级成错误，暴露完整来源

============================== warnings summary ===============================
..\.venv\Lib\site-packages\fastapi\testclient.py:1
  StarletteDeprecationWarning: Using `httpx` with `starlette.testclient`
  is deprecated; install `httpx2` instead.
    from starlette.testclient import TestClient as TestClient  # noqa
```

来源链条是：`你的测试` → `from fastapi.testclient import TestClient` → 转发到
`starlette.testclient` → 该模块内部 `import httpx2` 失败、回退到 `httpx` 并发出弃用预告。

**所以它不是你代码的问题**，而是上游正在把 `TestClient` 的底层从 `httpx` 迁移到 `httpx2` 的过渡期产物。

接下来是重点——**我想把它忽略掉，但没有成功**。实测了三种方式：

| 方式 | 命令 / 配置 | 结果 |
|------|------------|------|
| pytest ini 配置 | `filterwarnings = ["ignore::DeprecationWarning:starlette.testclient"]` | 无效，仍 1 warning |
| 命令行 `-W` | `pytest -W "ignore::DeprecationWarning"` | 无效，仍 1 warning |
| 环境变量 | `PYTHONWARNINGS="ignore::DeprecationWarning" pytest` | 无效，仍 1 warning |

**原因**：这条警告发生在 **import 阶段**（collection 时导入 `fastapi.testclient`），
而 pytest 在收集阶段对警告强制使用 `simplefilter("always")` 记录，用户配置的过滤器在这个阶段不生效。
过滤器只作用于**测试用例执行期间**产生的警告。

> 这正是排查类问题的真实形态：**不是每个警告都能被优雅地消掉。**
> 处理这类警告的正确姿势是：
>
> 1. **先确认它来自上游**（读 traceback 最后几帧，看文件路径是否在你的项目目录里）；
> 2. **不要为了消掉它而降版本或改写法**——那是用新问题掩盖旧问题；
> 3. **在 CI 里把它列入白名单**，或接受它存在，等上游完成迁移后自然消失。
>
> 反过来，**来自你自己代码的警告必须立刻修**。区分这两者的能力，比会写 `filterwarnings` 重要得多。

### 4.12.6 测试常见坑

| 坑 | 症状 | 解法 |
|----|------|------|
| 测试互相污染 | 单独跑通过，一起跑失败 | 每个用例用全新 fixture；`dependency_overrides.clear()` |
| 时间相关测试随时间失效 | 某天突然挂 | 注入 `now`，用固定时间 |
| 断言 uuid | 只能写正则 | 注入 `id_factory` |
| SSE 测试卡住 | 一直等响应结束 | 用 `client.stream()` 上下文，读几行就 `break` |
| 异步测试不执行 | 测试"通过"但没跑 | 配 `asyncio_mode = "auto"` 或加 `@pytest.mark.asyncio` |

---

## 4.13 生产级拓展：本章没做但上线必须做的事

> 本节是**提示清单**，不展开实现。每项给出"为什么需要"和"怎么选"。
> 顺序大致按重要性排列。

### 4.13.1 数据持久化（最高优先级）

本项目的 `InMemoryTaskRepository` 重启即丢数据。换成数据库：

```python
# 换实现，Service 一行不用改 —— 这就是 4.3 节分层的价值
class SqlAlchemyTaskRepository:
    def __init__(self, session_factory) -> None:
        self._session_factory = session_factory

    async def get(self, task_id: str) -> TaskRecord | None:
        async with self._session_factory() as session:
            row = await session.get(TaskRow, task_id)
            return TaskRecord.model_validate(row) if row else None
```

| 方案 | 适用场景 | 关键库 |
|------|---------|--------|
| **SQLAlchemy 2.0**（async） | 关系型数据，需要复杂查询 | `sqlalchemy[asyncio]` + `asyncpg` |
| **SQLModel** | 简单 CRUD，想让 Model 和 Schema 合一 | FastAPI 作者的作品 |
| **Tortoise ORM** | 喜欢 Django ORM 风格 | `tortoise-orm` |
| 直接用 `asyncpg` | 性能敏感，愿意写 SQL | `asyncpg` |

**必须配套做的事：**

1. **连接池配置** —— `pool_size`、`max_overflow`、`pool_recycle`（MySQL 8 小时断连问题）
2. **会话管理** —— 用 `yield` 依赖（4.5.3 节），每个请求一个会话
3. **迁移工具** —— Alembic，**不要手工改表结构**

```bash
alembic revision --autogenerate -m "add tasks table"
alembic upgrade head
```

4. **防止 N+1 查询** —— SQLAlchemy 用 `selectinload()` 预加载关联

### 4.13.2 认证与授权

本项目没有任何鉴权——所有接口裸奔。生产必须加：

```python
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials

security = HTTPBearer()

async def get_current_user(
    credentials: Annotated[HTTPAuthorizationCredentials, Depends(security)],
) -> User:
    """解析 JWT，拿不到就 401。"""
    try:
        payload = jwt.decode(credentials.credentials, SECRET_KEY, algorithms=["HS256"])
    except JWTError:
        raise HTTPException(status_code=401, detail="token 无效")
    return User(**payload)

CurrentUser = Annotated[User, Depends(get_current_user)]

@router.delete("/{task_id}")
async def delete_task(task_id: str, user: CurrentUser, ...): ...
```

| 需求 | 方案 |
|------|------|
| 密码存储 | `passlib` + bcrypt / argon2（**绝不能 MD5**） |
| Token | `python-jose` 或 `pyjwt`，JWT + 刷新令牌 |
| OAuth2 密码流 | `fastapi.security.OAuth2PasswordBearer` |
| 权限模型 | RBAC：`casbin`；简单场景自己写依赖 |
| API Key | 自定义 Header 依赖 |

**注意**：鉴权必须做成**依赖**，而不是在每个路由里手写 if——漏一个就是漏洞。

### 4.13.3 可观测性

三件套：日志、指标、链路追踪。

**链路追踪**（把 4.8.2 节的 `X-Request-ID` 升级成标准实现）：

```python
from opentelemetry.instrumentation.fastapi import FastAPIInstrumentor

FastAPIInstrumentor.instrument_app(app)     # 一行接入，自动产生 trace
```

配套：Jaeger / Zipkin 展示，或直接用阿里云 ARMS、Datadog。

**指标**（Prometheus）：

```python
from prometheus_fastapi_instrumentator import Instrumentator

Instrumentator().instrument(app).expose(app)    # 自动暴露 /metrics
```

**关键**：日志里要带上 `trace_id`，否则日志和链路对不上，排查时仍然要靠猜。

### 4.13.4 部署与运行

**多进程与进程管理：**

```bash
# 方式一：uvicorn 自己管多进程（简单）
uvicorn taskflow.main:app --workers 4 --host 0.0.0.0

# 方式二：Gunicorn 管进程 + uvicorn worker（生产推荐，支持优雅重启）
gunicorn taskflow.main:app \
    -w 4 -k uvicorn.workers.UvicornWorker \
    --bind 0.0.0.0:8000 \
    --timeout 60 --graceful-timeout 30
```

**Docker 镜像要点：**

```dockerfile
FROM python:3.12-slim

# 关键 1：先装依赖再拷代码，利用 Docker 层缓存
COPY pyproject.toml uv.lock ./
RUN pip install --no-cache-dir -r pyproject.toml

# 关键 2：不装 dev 依赖
# 关键 3：不要用 root 运行
RUN useradd -m appuser
USER appuser

COPY src/ ./src/
CMD ["gunicorn", "taskflow.main:app", "-k", "uvicorn.workers.UvicornWorker", "-w", "4"]
```

**必须的部署配置：**

| 项目 | 做法 |
|------|------|
| 优雅关闭 | `--graceful-timeout`，让 lifespan 的清理代码跑完 |
| 健康检查 | K8s 配 liveness/readiness（4.10.4 节） |
| 反向代理 | Nginx 要配 `proxy_buffering off`（SSE 才能流） |
| 静态资源 | 交给 CDN 或 Nginx，不要让 Python 处理 |

### 4.13.5 安全加固

| 风险 | 措施 |
|------|------|
| CORS 全开 | 生产写具体域名（4.10.3 节） |
| 文档泄露 | 生产 `openapi_url=None` 或加 Basic Auth |
| SQL 注入 | 用 ORM 参数化查询，**绝不字符串拼 SQL** |
| 请求体过大 | Nginx 配 `client_max_body_size`，应用层也要限制 |
| 依赖漏洞 | `pip-audit` / `safety check` 进 CI |
| 敏感信息泄露 | 兜底异常处理器（4.7.2 节）必须有 |
| 频率攻击 | 限流换 Redis 实现（4.8.3 节） |
| HTTPS | 强制 HSTS，代理层终止 TLS |

### 4.13.6 任务队列

`BackgroundTasks` 不够用时的选择：

| 方案 | 依赖 | 适用 |
|------|------|------|
| **Celery** | Redis / RabbitMQ | 功能最全，生态成熟，但配置复杂 |
| **ARQ** | Redis only | 纯 asyncio，轻量，和 FastAPI 气质相符 |
| **Dramatiq** | Redis / RabbitMQ | 简洁，性能好 |
| **Taskiq** | Redis / NATS | 原生 async，类型友好 |

```python
# ARQ 示例
async def send_email(ctx, user_id: str, subject: str): ...

await redis.enqueue_job("send_email", user_id, "任务已分配")
```

**选型关键**：任务要不要**持久化**和**重试**？要 → 用队列；不要 → `BackgroundTasks`。

### 4.13.7 其他生产关注点

**API 版本管理** —— 本项目用 `/api/v1/` 前缀，是最简单的做法。演进策略：

- URL 前缀（`/v1`、`/v2`）—— 最直观，推荐
- Header 版本（`Accept: application/vnd.api.v2+json`）—— RESTful 但难调试
- 破坏性变更必须有**废弃期公告**和双版本并存期

**分页改进** —— 深分页（`page=10000`）性能差。游标分页：

```python
@router.get("")
async def list_tasks(cursor: str | None = None, limit: int = 20): ...
# 用 created_at + id 做游标，避免 OFFSET 大偏移扫描
```

**缓存** —— 用 `fastapi-cache2`，或 Redis 手动缓存：

```python
@cache(expire=60)
async def get_stats(): ...
```

**并发安全** —— 状态流转这类操作有竞态（两个人同时改状态）：

```sql
UPDATE tasks SET status='done'
WHERE id = $1 AND status = 'in_progress'   -- 乐观锁：条件不满足则 0 行受影响
```

**配置校验** —— 生产环境启动前校验必填配置：

```python
class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="forbid")

    database_url: str          # 没有默认值 → 缺失时启动即失败
    secret_key: str

    @model_validator(mode="after")
    def check_production(self):
        if self.is_production and self.secret_key == "changeme":
            raise ValueError("生产环境必须修改 secret_key")
        return self
```

**早失败原则**：配置错误应该在**启动时**暴露，而不是运行到某个分支才发现。

---

## 4.14 小结

### 本章做了什么

从一个空目录开始，搭出了一个 **644 行源码、97% 覆盖率、85 个测试全部通过**的 API 项目，涵盖：

```
项目搭建   → pyproject.toml / src 布局 / uv
模型层     → Pydantic 三模型分离 / 泛型响应包装 / PATCH 陷阱
仓储层     → Protocol 接口 / 过滤排序分页
服务层     → 构造器注入 / 状态机 / 外部服务降级
依赖注入   → Depends / Annotated / yield 依赖 / 测试覆写
路由层     → APIRouter / Query 校验 / 状态码 / 文件上传 / SSE
错误处理   → 异常体系 / 4 个 handler / 统一 422 格式
中间件日志 → 洋葱模型 / 纯 ASGI vs BaseHTTPMiddleware / request_id
配置管理   → pydantic-settings / 环境变量优先 / 密钥不入代码
装配       → create_app 工厂 / lifespan / app.state
测试       → 单元测试 + TestClient 集成测试 / 固定时间与 ID
```

### 十四个关键决策（值得记住的"为什么"）

| # | 决策 | 原因 |
|---|------|------|
| 1 | 入参/实体/出参三个模型 | 安全边界 + 文档准确性 |
| 2 | 仓储返回 `None` 而非抛异常 | 404 是 HTTP 语义，不该污染数据层 |
| 3 | 时间和 ID 做成构造参数 | 让测试可复现、可精确断言 |
| 4 | `Protocol` 而非 `ABC` | 实现类零耦合 |
| 5 | 内存仓储也用 `async` | 接口形状提前对齐，换存储不改调用方 |
| 6 | 派生字段在出参模型算 | 实体保持纯粹 |
| 7 | `exclude_unset=True` | PATCH 语义的正确实现 |
| 8 | 状态机用字典表达 | 规则集中、错误自解释 |
| 9 | 固定路径路由注册在动态路径之前 | FastAPI 严格按注册顺序匹配 |
| 10 | 兜底异常处理器 | 防止 traceback 泄露 |
| 11 | 流式接口必须用纯 ASGI 中间件 | `BaseHTTPMiddleware` 会缓冲响应体 |
| 12 | `create_app()` 工厂而非模块级 app | 测试隔离、延迟初始化 |
| 13 | `app.state` 存单例而非全局变量 | 避免测试互相污染 |
| 14 | liveness 不查依赖 | 防止小故障放大成雪崩 |

### 自检清单：你的 FastAPI 项目合格了吗

- [ ] 有没有**入参模型和出参模型分离**？（防止客户端伪造 `id`/`status`）
- [ ] 涉及**日期/时间**的逻辑，时间是不是作为参数传入的？
- [ ] 所有外部调用（DB、HTTP）有没有**超时设置**？
- [ ] 有没有**兜底异常处理器**？（别把 traceback 返回给客户端）
- [ ] `page_size` 有没有**上限**？
- [ ] 配置里的**密钥**是不是在环境变量里，而不是代码/`.env` 里？
- [ ] 健康检查的 **liveness 探针**是不是没有查数据库？
- [ ] 路由注册顺序：**固定路径在动态路径之前**？
- [ ] 中间件如果要碰流式响应，是不是**纯 ASGI 实现**？
- [ ] 有没有**至少一个**不启 HTTP 的 Service 单元测试？

### 下一步

第四章讲的是"怎么写一个能用的 Web 服务"。第五章进入 AI 部分——你会发现本章建立的分层结构（Service 不依赖框架、依赖靠注入、时间可固定）在接 LLM 时**价值翻倍**：LLM 调用慢、贵、结果不确定，这些特点让"可测试性"从加分项变成必需品。

---

> **配套代码**：`python4javaProgrammer/taskflow/`（完整可运行）
>
> ```bash
> cd python4javaProgrammer/taskflow
> pip install -e ".[dev]"
> pytest                                          # 85 passed
> PYTHONPATH=src uvicorn taskflow.main:app --reload
> ```

