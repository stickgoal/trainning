# 第一章：Python 语法（精要速览 + 完整语法体系）

> 写给能读懂 Java、但还没正经写过 Python 的工程师。 目标：让你能无障碍阅读并改写本仓库 `examples/` 下那 15 个 AI 示例，并具备独立编写 Python 脚本的能力。

---

## 导读：本章解决什么问题

本章由原《第一章 · Python 语法精要》与《语法桥梁》合并而成：既保留精要速览，也保留完整的语法体系（含 Java 类比、examples 对照、生态科普）。目标是让你能无障碍阅读并改写本仓库 `examples/` 下那 15 个 AI 示例，并具备独立编写 Python 脚本的能力。

本章按"**先换脑子 → 补语法 → 读真实代码 → 看生态地图**"的顺序组织。每一节都是可直接阅读的完整正文，配可运行代码和 Java 类比。

> 阅读建议：想快读概览，扫 1.1–1.5 节即可；想系统学透（含装饰器进阶、生成器、函数式编程、match/case），按顺序通读全章。

### 配套文件

- `python4javaProgrammer/python_basics_demo.py`：**纯标准库、零依赖、可直接跑**的语法演示，覆盖本章全部语法点。

  ```bash
  python python4javaProgrammer/python_basics_demo.py
  ```
- 本章末尾的 **附录 A · 语法点覆盖对照表**：逐条列出"某个语法点 → 在本章哪节讲 → 出现在哪个 `examples/` 文件"，用于自查是否全覆盖。

### 三个贯穿本章的心智问题

1. **Python 和 Java 最反直觉的差别是什么？** → 1.1 节
2. **示例里那些怪语法各自是干什么的？** → 1.2–1.7 节 + 1.8 节串讲
3. **学完语法，下一步该对接哪条 Python 技术栈？** → 1.9 节（主流技术科普）

---

## 1.0 环境与工具链搭建（第零章已覆盖）

> 环境搭建（Python 解释器安装、venv 虚拟环境、pip/uv 国内镜像、LLM API Key 配置）见**第零章**， 本章直接从语法开始。若你已能 `python xxx.py` 跑脚本、`pip install` 装包，即可跳过本节。
>
> 一句话回忆：Python 的"环境" ≈ JDK + Maven/Gradle + 本地仓库，但更轻、更散；核心动作是 **装对解释器 + 每个项目一个虚拟环境**（`python -m venv .venv` + 激活）。

## 1.1 从 Java 到 Python：先换脑子

> 对应需求：帮助有 Java 基础的读者快速上手。先改心智模型，再学语法，事半功倍。

### 1.1.1 五个最反直觉的差别

| \# | 差别 | Java 习惯 | Python 现实 |
| --- | --- | --- | --- |
| 1 | **缩进即语法** | 大括号 `{}` 管作用域 | 缩进（4 个空格）管作用域，少/多一个空格就报错 |
| 2 | **动态类型 + 可选类型提示** | `String s = "x";` 必须声明 | `s = "x"` 也能跑；但建议写 `s: str = "x"` |
| 3 | **一切皆对象** | 基本类型 `int` 不是对象 | 连 `int`、`function` 都是对象，可以当变量传来传去 |
| 4 | **没有访问修饰符** | `private/public/protected` | 靠约定：`_name` 表示"私有"（单下划线），并不强制 |
| 5 | **鸭子类型** | 靠 `implements` 接口 | "长得像就行"（Protocol，见 1.5 节），不强制继承 |

**最重要的第一条**：Python 用缩进划分代码块，不要画蛇添足加分号，也不要用大括号。

```python
# Java: if (x > 0) { System.out.println("正"); }
if x > 0:
    print("正")          # 缩进 4 空格，属于 if 块
print("这行不在 if 里")   # 退回原缩进，if 结束
```

### 1.1.2 第一个程序与模块入口

```python
def main() -> None:
    print("Hello, Python")

# 这行是"模块入口"：只有直接运行本文件时才执行 main()
# 被别的文件 import 时不执行（类比 Java 的 public static void main 只作为程序入口）
if __name__ == "__main__":
    main()
```

`if __name__ == "__main__":` 是 Python 的固定 idiom：

- 直接 `python xxx.py` 运行时，`__name__` 等于 `"__main__"`，里面的代码执行；
- 被 `import` 进别的文件时，`__name__` 是模块名，里面的代码**不执行**（避免 import 就自动跑副作用）。

### 1.1.3 import 体系

Java 的 `import com.foo.Bar;` 对应 Python 的两种写法：

```python
import os                          # 整模块导入，用 os.getenv(...)
from os import getenv             # 只导入某个名字，直接用 getenv(...)
from dataclasses import dataclass, field   # 一次导入多个
import os as operating_system      # 起别名（长模块名常用）
```

> 关键认知：**模块 = 一个 .py 文件；包 = 一个目录**（目录里要有 `__init__.py`，现在常可省略但概念在）。`from langchain_core.tools import tool` 就是"从 langchain_core 包的 tools 模块导入 tool"。

### 1.1.4 注释与 docstring

```python
# 这是单行注释（类比 Java 的 //）

"""这是文档字符串（docstring），写在文件/函数/类开头，
用三引号。它不是普通注释，会被工具读取（IDE 悬浮提示、help() 都靠它）。
示例里 @tool / @mcp.tool() 就是把 docstring 当"工具说明"发给 LLM。"""
```

---

**练习【1.1】import 与模块入口**

场景：把一个工具拆成"可被导入的模块 + 直接运行入口"。写 `greet.py`，含 `greet(name)` 函数；用 `__main__` 守卫让 `python greet.py Alice` 打印问候，而 `import greet` 时不自动打印。

```python
# greet.py
import sys

def greet(name: str) -> str:
    return f"Hello, {name}!"

# TODO: 加 if __name__ == "__main__": 守卫，从 sys.argv[1] 取名字打印 greet(name)
```

考察点：`import` 体系、`__name__ == "__main__"` 区分"被导入 vs 直接运行"、`sys.argv` 取命令行参数。

## 1.2 变量、类型与基础数据结构

### 1.2.1 变量与动态类型（强烈建议写类型提示）

```python
x = 10          # 不声明类型也能用（动态类型）
x = "hi"        # 甚至可以换成字符串——Java 不允许，Python 允许

# 但请养成写类型提示的习惯，读起来和 Java 一样清楚：
count: int = 5
name: str = "张三"
price: float = 9.9
active: bool = True
```

函数返回类型用 `->`：

```python
def add(a: int, b: int) -> int:
    return a + b
```

> Java 类比：`public int add(int a, int b)`。`x: int = 5` ≈ `int x = 5;`。Python 的类型提示**运行时不做强制检查**（除非用 mypy，见 1.9 节），但 IDE 和人是受益的。

### 1.2.2 基础类型

| Python | ≈ Java | 说明 |
| --- | --- | --- |
| `int` | `long` | 任意精度，不会溢出 |
| `float` | `double` | 浮点 |
| `str` | `String` | 不可变 |
| `bool` | `boolean` | `True`/`False`（首字母大写） |
| `None` | `null` | 但它是**单例**，判断用 `is None` 而非 `== None` |

```python
n: None = None
if n is None:          # 正确写法
    print("是空")
```

### 1.2.3 字符串：f-string、原始字符串、多行

```python
name = "张三"
# f-string：把变量塞进 {}，类比 String.format
print(f"{name} 今年 {2026 - 1990} 岁")

# f-string 里能放表达式，包括三元运算符
ok = True
print(f"结果: {'成功' if ok else '失败'}")

# 原始字符串 r"..."：反斜杠不转义，写正则/SQL/Windows 路径必备
import re
pattern = r"\d{3}-\d{4}"      # 如果是普通字符串要写成 "\\d"

# 多行字符串：用三引号，常用来写 prompt
prompt = """
你是一位助手。
请回答用户的问题。
"""
```

### 1.2.4 容器：list / tuple / dict / set

```python
# list ≈ Java List/ArrayList（可变、可混合类型）
items: list = [1, "二", 3.0]
items.append("四")                 # 类比 List.add

# 泛型容器写法（Python 3.9+，PEP 585），强烈推荐
names: list[str] = ["Alice", "Bob"]          # ≈ List<String>
messages: list[dict] = [{"role": "user", "content": "你好"}]  # ≈ List<Map>

# tuple ≈ 不可变数组，也常当"轻量 record"
point: tuple = (10, 20)

# 元组解包：一次给多个变量赋值（Java 没有这种写法）
major, minor = (3, 12)
print(f"{major}.{minor}")

# 元组可逐个元素比较，常用来做版本判断（示例 00_setup.py 用 (major, minor) < (3, 10)）
if (major, minor) >= (3, 10):
    print("满足 3.10+")

# dict ≈ Java Map
scores: dict[str, int] = {"Alice": 90, "Bob": 80}
scores["Cindy"] = 85                          # map.put
print(scores.get("David", 0))                 # 带默认值取值，map.get(k, default)

# set ≈ Java HashSet，去重
tags: set = {"java", "python", "java"}        # 结果只有两个元素
```

**练习【1.2-容器】标签云**

场景：给定一组带重复的标签，用 `list`/`set`/`sorted`/切片/手写 `dict` 完成去重、排序、取前 N、上色映射。只用内建函数 + 字面量，不用循环。

```python
tags = ["ai", "llm", "rag", "ai", "mcp", "llm", "agent"]

unique = ...                      # TODO: set 去重
ordered = ...                     # TODO: sorted() 字母序
top3 = ...                        # TODO: 切片取前 3
colors = {"ai": "red", "llm": "blue"}   # 手写映射

print(unique, ordered, top3, colors["ai"])
```

考察点：list/tuple/dict/set 字面量、`set` 去重、`sorted` 内建、切片、dict 索引。

### 1.2.5 类型注解进阶（typing 模块）

Python 3.5 起支持**类型注解（type hints）**，3.9/3.10 之后越来越好用。先说清一件事：

> **类型注解是给"人和工具"看的，运行时默认不强制。**
>
> - `def f(x: int) -> str:` 里的 `int` / `str` **不会**在运行时拦截传错类型（Python 仍是动态语言）。
> - 它的价值在于：**IDE 自动补全更准、静态检查器（mypy / pyright）能在运行前抓出类型错误、代码可读性更强**。
> - 类比 Java：Java 的类型是"编译期强制"的；Python 的注解更像"加强版注释 + 可插拔的编译期检查"。

下面按"常见度 + 实战价值"逐类讲解。

#### 1. 最常用的五个：Optional / Union / Any / Literal / Callable

```python
from typing import Optional, Any, Union, Callable, Literal

maybe: Optional[str] = None        # 要么 str 要么 None。≈ Java Optional<String>
anything: Any = 123                 # 任意类型，等于"放弃检查"，尽量少用
num: Union[int, str] = "5"          # 多类型：int 或 str
cb: Callable[[int, int], int] = add # 函数类型：接收两个 int 返回 int（类比函数式接口）
mode: Literal["read", "write"] = "read"  # 只能是这几个字面量之一（类比枚举/常量）
```

- `Optional[X]` 就是 `Union[X, None]` 的简写，表示"可能没有值"。
- `Any` 会"传染"——一旦用 `Any`，相关表达式的检查就全关了，所以**只在接第三方动态数据时用**。
- `Literal[...]` 适合配置项、模式开关等"取值有限"的场景，比裸 `str` 更精确。
- `Callable[[参数类型...], 返回值类型]` 描述"一个可调用对象"，写回调 / 依赖注入时很有用。

> **现代写法（Python 3.10+）**：`Union[int, str]` 可直接写 `int | str`，`Optional[str]` 可写 `str | None`。本书示例统一用 `X | Y` 风格（基于 3.10+ 环境），若要兼容 3.8/3.9 则仍需从 `typing` 导入老写法。

**练习 1：日志器配置（Literal + Optional/Callable）**

场景：写一个 `setup_logger`，名字是字符串，日志级别只能是 `DEBUG`/`INFO`/`WARN`/`ERROR` 四个值之一，回调函数可选（接收一条消息 str、返回 None）。请只补全类型注解，函数体照抄即可——重点是签名。

```python
from typing import Literal, Callable

# TODO: 补全参数与返回值类型注解
def setup_logger(name, level="INFO", callback=None) -> None:
    print(f"[{level}] {name}")

setup_logger("app", "DEBUG")          # ✓
# setup_logger("app", "TRACE")        # 取消注释后跑 mypy，会报 TRACE 不在 Literal 里
```

考察点：用 `Literal` 收窄枚举值、用 `Callable[..., None] | None` 描述"可选回调"。

#### 2. 容器类型：具体类型 vs 抽象类型

```python
from typing import Sequence, Mapping, Collection

# 抽象类型（描述"能力"，更宽松、更灵活）
def total(xs: Sequence[int]) -> int:        # 接收任何"可索引、有长度"的序列，list/tuple 都行
    return sum(xs)

def keys(d: Mapping[str, int]) -> Collection[str]:
    return d.keys()

print(total([1, 2, 3]))     # ✓ list 可以
print(total((10, 20)))      # ✓ tuple 也可以
```

- `Sequence` / `Mapping` / `Collection` 描述**接口能力**（类比 Java 的 `List`/`Map` 接口，而非 `ArrayList`/`HashMap`）。**函数参数优先用抽象类型**，这样 list、tuple、自定义序列都能传，复用性更高。
- `Tuple[float, float]` 还能标记**定长异构**元组：`point: tuple[float, float]`。
- Python 3.9+ 推荐直接用内置泛型 `list[int]` / `dict[str, int]` / `tuple[int, str]`，无需从 `typing` 导入 `List`/`Dict`/`Tuple`（老写法仍可用，但新代码建议用内置形式）。

**练习 2：成绩求和与取前两个（抽象容器类型）**

场景：`sum_scores` 接收"姓名→分数"的映射（dict 或别的 Mapping 都能传），用内建 `sum()` 求总分；`first_two` 接收任意序列，返回前两个元素组成的 list。只用内建函数 + 索引，不用循环。

```python
from typing import Mapping, Sequence

# TODO: 补全类型注解（参数用抽象类型，返回值用 list[...]）
def sum_scores(scores):
    return sum(scores.values())

def first_two(items):
    return [items[0], items[1]]

print(sum_scores({"a": 90, "b": 85}))   # 175
print(first_two((10, 20, 30)))          # [10, 20]
```

考察点：参数用 `Mapping`/`Sequence` 抽象类型（而非 `dict`/`list`），调用更灵活；返回值用具体 `list[...]`。

#### 3. 泛型：TypeVar 与 Generic

写"对多种类型都适用、但内部保持类型一致"的函数 / 类时，用泛型：

```python
from typing import TypeVar, Generic

T = TypeVar("T")                    # 一个类型变量
U = TypeVar("U", int, str)          # 约束上界：只能是 int 或 str

def first(xs: list[T]) -> T:        # 输入 list[T]，返回 T（类型自动跟随）
    return xs[0]

# 泛型类
class Stack(Generic[T]):
    def __init__(self) -> None:
        self._items: list[T] = []
    def push(self, x: T) -> None:
        self._items.append(x)
    def pop(self) -> T:
        return self._items.pop()

s: Stack[int] = Stack()
s.push(1)                 # ✓
# s.push("x")            # mypy 会报错：期望 int
```

- `TypeVar("T")` 类比 Java 的 `<T>`；`TypeVar("U", int, str)` 类比带约束的泛型（这里约束为"只能是 int 或 str"）。
- `Generic[T]` 类比 Java 的 `class Stack<T>`。

**练习 3：泛型盒子 Box[T]（TypeVar + Generic）**

场景：实现一个能装任意类型值的 `Box`，存什么类型、取出来还是什么类型（类型跟随）。再写 `wrap(x)` 把任意值装进 Box 返回。仿照本模块的 `Stack` 写法。

```python
from typing import TypeVar, Generic

T = TypeVar("T")

# TODO: 完成 Box 类
class Box(Generic[T]):
    # 提示：__init__ 存 self._value: T；value() -> T 取出来
    ...

def wrap(x: T) -> "Box[T]":     # 返回类型用字符串前向引用（Box 在下面才定义）
    return Box(x)

b: Box[int] = wrap(42)
print(b.value())                 # 42
```

考察点：`TypeVar` 声明、`Generic[T]` 继承、方法签名里用 `T`、返回 `Box[T]` 的前向引用。

#### 4. Protocol：结构化子类型（鸭子类型 + 静态检查）

Java 靠 `implements` 声明接口；Python 更宽松——**只要"长得像"就行**（鸭子类型）。`Protocol` 让这种"长得像"能被静态检查器识别：

```python
from typing import Protocol

class SizedAndCloseable(Protocol):
    def __len__(self) -> int: ...
    def close(self) -> None: ...

def cleanup(resource: SizedAndCloseable) -> None:
    print(f"size={len(resource)}")
    resource.close()

# 任意满足"有 __len__ 和 close"的对象都能传，无需显式继承
class MyFile:
    def __len__(self) -> int: return 10
    def close(self) -> None: print("closed")

cleanup(MyFile())   # ✓ 静态检查通过
```

- 类比：比 Java `interface` 更松——**不看是否声明实现，只看是否具备对应方法签名**（structural typing）。
- 实战价值：写工具函数时不强制对方继承你的基类，库之间解耦更自然。

**练习 4：可绘制图形面积（Protocol）**

场景：定义 `Drawable` Protocol（有 `area(self) -> float` 方法），写 `total_area(shapes)` 求总面积。再定义 `Circle`、`Square` 两个普通类（**不继承** Drawable），各自实现 `area`，验证它们都能传给 `total_area`。

```python
from typing import Protocol

class Drawable(Protocol):
    def area(self) -> float: ...

# TODO: 补全参数与返回类型
def total_area(shapes):
    return sum(s.area() for s in shapes)   # 生成器表达式，1.5.11 正式讲；这里照抄

class Circle:
    def __init__(self, r: float) -> None: self.r = r
    def area(self) -> float: return 3.14 * self.r * self.r

class Square:
    def __init__(self, a: float) -> None: self.a = a
    def area(self) -> float: return self.a * self.a

print(total_area([Circle(1), Square(2)]))   # ≈ 11.14
```

考察点：`Protocol` 定义；`Circle`/`Square` 不显式继承 `Drawable` 也能通过检查（结构化子类型）。

#### 5. TypedDict：给 dict 上"结构"

当函数接收结构固定的 dict（比如 JSON 载荷），`TypedDict` 让检查器知道里面有什么键：

```python
from typing import TypedDict

class UserRecord(TypedDict):
    id: int
    name: str
    email: str | None     # 可选键

def greet(u: UserRecord) -> str:
    return f"hi {u['name']}"

# 检查器能验证：u['id'] 是 int，u['email'] 可能为 None，拼错键名会报错
```

- 类比：TypeScript 的 `interface`，或 Java 里"字段已知的 Map"。
- 注意：`TypedDict` 是**检查期**结构，运行时它仍是普通 dict，不做校验（那是 Pydantic 的活，见下文）。

**练习 5：用户记录格式化（TypedDict）**

场景：一段 JSON 解析后是固定结构的 dict（id、name、email 可能为空）。用 `TypedDict` 描述这个结构，写 `label(u)` 返回 `"id:name"` 字符串，再构造一个 dict 传进去。

```python
from typing import TypedDict

# TODO: 定义 UserRecord（id:int, name:str, email:str | None）
class UserRecord(TypedDict):
    ...

# TODO: 补全参数类型
def label(u) -> str:
    return f"{u['id']}:{u['name']}"

rec = {"id": 1, "name": "Alice", "email": None}
print(label(rec))   # 1:Alice
```

考察点：`TypedDict` 类定义、可选键 `str | None`、参数标注为该 TypedDict（检查器会校验键名拼写）。

#### 6. 常量与类变量：Final / ClassVar

```python
from typing import Final, ClassVar

MAX_RETRIES: Final = 3          # 约定为常量，mypy 会阻止重新赋值
# MAX_RETRIES = 4               # mypy 报错：不能改 Final 变量

class Config:
    version: ClassVar[str] = "1.0"  # 类级别共享，不属于实例
    timeout: int = 30                # 实例字段
```

- `Final` ≈ Java 的 `final` 变量（语义上靠检查器强制）。
- `ClassVar` 显式区分"类变量"和"实例字段"，避免把类属性误当实例属性。

**练习 6：HTTP 状态码常量（Final + ClassVar）**

场景：把一组 HTTP 状态码定义为不可变常量，把默认超时设为类共享变量。打印三者，并（用注释）体会 `OK = 500` 会被 mypy 拒绝。

```python
from typing import Final, ClassVar

# TODO: 用 Final 标记下面三个常量
OK = 200
NOT_FOUND = 404
SERVER_ERROR = 500

class HttpConfig:
    # TODO: 用 ClassVar 标记 DEFAULT_TIMEOUT
    DEFAULT_TIMEOUT = 30
    retry: int = 3        # 实例字段，对比一下

print(OK, NOT_FOUND, SERVER_ERROR, HttpConfig.DEFAULT_TIMEOUT)
# OK = 500               # 取消注释后跑 mypy，会报"不能给 Final 变量赋值"
```

考察点：`Final` 标记常量、`ClassVar` 区分"类变量 vs 实例字段"。

#### 7. 重载：@overload

Python 运行时没有真正的重载（后定义覆盖前定义），但可以用 `@overload` 给**静态检查器**描述多种调用形态：

```python
from typing import overload

@overload
def parse(s: str) -> int: ...              # 形态1：传 str 返回 int
@overload
def parse(s: str, base: int) -> str: ...   # 形态2：传两个参数返回 str

def parse(s: str, base: int = 10) -> int | str:   # 真正实现（只需一个）
    if base == 10:
        return int(s)
    return format(int(s), f"0{base}b")

r1: int = parse("42")          # ✓ 检查器知道返回 int
r2: str = parse("42", 2)       # ✓ 检查器知道返回 str
```

- `@overload` 只给检查器看，运行时只用最后一个真正实现。类比 Java 的方法重载签名声明。

**练习 7：灵活的 to_list（@overload）**

场景：`to_list(x)` 有两种用法——传 str 返回单元素 `list[str]`；传 `list[int]` 返回它的副本 `list[int]`。用 `@overload` 声明两种形态，再写一个真正实现（`if`/`isinstance` 1.3 才正式讲，照抄即可）。

```python
from typing import overload

# TODO: 两个 @overload 签名
@overload
def to_list(x: str) -> list[str]: ...
@overload
def to_list(x: list[int]) -> list[int]: ...

# TODO: 真正实现（一个 def 即可，覆盖所有形态）
def to_list(x):
    if isinstance(x, str):
        return [x]
    return list(x)

a: list[str] = to_list("hi")        # ✓ 检查器知道返回 list[str]
b: list[int] = to_list([1, 2, 3])   # ✓ 知道返回 list[int]
```

考察点：多个 `@overload` 签名 + 一个真正实现；检查器能按调用形态推断返回类型。

#### 8. Annotated：把"元数据"挂到类型上

`Annotated[T, meta...]` 把额外信息（校验规则、描述、依赖）和类型绑在一起，**运行时可通过 `get_type_hints` 取到**。这是 FastAPI / Pydantic v2 的核心机制：

```python
from typing import Annotated
from pydantic import BaseModel, Field

class Account(BaseModel):
    # Annotated[int, Field(...)]：类型是 int，附带"大于0"的校验元数据
    balance: Annotated[int, Field(gt=0, description="账户余额")] = 100

# FastAPI 也用它声明"查询参数 + 描述"：
#   def search(q: Annotated[str, Query(min_length=1)]) -> list[Item]: ...
```

- 注解本身"还是 int"，但多挂了一层说明；运行时 `typing.get_type_hints(Account, include_extras=True)` 能拿到 `Field(...)` 这个元数据。

**练习 8：商品模型带校验（Annotated + Pydantic）**

场景：用 Pydantic 定义商品，价格必须 &gt; 0、库存必须 ≥ 0。用 `Annotated` 把校验规则挂到类型上。构造一个合法实例打印；再尝试 `price=-1`，用 `try/except` 捕获并打印错误类型名（`try/except` 1.3.4 正式讲，照抄）。

```python
from typing import Annotated
from pydantic import BaseModel, Field, ValidationError

class Product(BaseModel):
    name: str
    # TODO: 用 Annotated + Field 约束 price>0、stock>=0
    price: float
    stock: int

p = Product(name="Book", price=29.9, stock=10)
print(p.model_dump())

try:
    Product(name="Bad", price=-1, stock=0)   # 应触发校验错误
except ValidationError as e:
    print(type(e).__name__)
```

考察点：`Annotated[T, Field(...)]` 把校验元数据挂到类型上；Pydantic 在构造时自动校验。

#### 9. 工程技巧：TYPE_CHECKING 与 cast

```python
from typing import TYPE_CHECKING, cast

# 避免循环导入：只在类型检查时导入，运行时不会真正 import
if TYPE_CHECKING:
    from models import User

def handle(u: "User") -> None:   # 用字符串形式引用，或上面的 TYPE_CHECKING 导入
    ...

# cast：告诉检查器"我比它更清楚这个值的类型"（不做运行时转换！）
x = cast("list[int]", some_obj)   # 运行时 x 仍是 some_obj，只是检查器相信它是 list[int]
```

- `TYPE_CHECKING`：值为 `False` 的常量，专门用在不希望运行时执行的导入，解决"模块 A 导入 B、B 又导入 A"的循环依赖。
- `cast` 是"断言式提示"，**不转换数据**，滥用会掩盖真实 bug，仅在确实知道类型但检查器推断不出时用。

**练习 9：跨模块类型引用（TYPE_CHECKING + cast）**

场景：`handle` 函数要接收另一个模块 `models.py` 里的 `Config` 对象，但运行时不想真的导入 `models`（避免循环依赖）。另有一段从 JSON 解析回来的数据（类型是 `Any`），你确知它是 `dict[str, int]`，用 `cast` 告诉检查器。补全两处 TODO。

```python
from typing import TYPE_CHECKING, Any, cast

# TODO: 用 TYPE_CHECKING 守卫，只在类型检查时导入 Config
# if TYPE_CHECKING:
#     from models import Config

# TODO: 参数类型用字符串前向引用 "Config"
def handle(cfg) -> None:
    print(cfg)

raw: Any = {"a": 1, "b": 2}     # 比方说是 json.loads 的返回，类型是 Any
# TODO: 用 cast 把 raw 声明为 dict[str, int]（不改运行时，只说服检查器）
typed = raw
print(typed["a"])
```

考察点：`TYPE_CHECKING` 守卫解决循环导入、字符串前向引用 `"Config"`、`cast(typ, val)` 只影响检查器不改运行时。

#### 10. 工具链：让注解真正"生效"

注解写出来不跑检查器就等于注释。推荐：

| 工具 | 用途 |
| --- | --- |
| `mypy` | 老牌静态类型检查器（`pip install mypy`，然后 `mypy your_file.py`） |
| `pyright` | 微软出品，VS Code 的 Pylance 底层，速度更快 |
| IDE 内置 | VS Code / PyCharm 默认基于类型推断做补全和红线提示 |

在 `examples/` 的 AI 代码里，类型注解主要服务于**可读性 + IDE 补全 + 接 FastAPI 时的自动校验**，不强制跑 mypy。若要写"给别人当库"的代码，建议把 mypy 加进 CI。

**练习 10：用 mypy 抓一个类型 bug（动手题）**

场景：类型注解写了不跑检查器就等于注释。请把下面这段故意带类型错误的代码存为 `practice_typing.py`，安装并运行 mypy，观察报错，再改正直到 mypy 输出 `Success`。

```python
# practice_typing.py
def add(a: int, b: int) -> int:
    return a + b

print(add("1", 2))     # 故意传错类型：运行时 Python 会拼成 "12"，但类型是错的
```

操作步骤：

1. `pip install mypy`
2. `mypy practice_typing.py` → 应看到类似 `error: Argument 1 to "add" has incompatible type "str"; expected "int"`
3. 改正调用（如 `add(1, 2)`）后再跑 mypy，直到 `Success: no issues found`

考察点：真实跑一次静态检查器，理解"注解 + 检查器"组合如何替代 Java 的编译期类型强制。

> 补充：**Pydantic 模型**——把类型注解变成"运行时也会校验"的数据类（FastAPI 章节大量使用，需 `pip install pydantic`）：

```python
from pydantic import BaseModel, ValidationError

class User(BaseModel):
    id: int
    name: str
    email: str | None = None   # 可选字段，默认 None

user = User(id=1, name="Alice", email="alice@example.com")
print(user.model_dump())
# -> {'id': 1, 'name': 'Alice', 'email': 'alice@example.com'}

# 校验发生在构造时：传错类型会直接抛错
try:
    User(id="not-an-int", name="Bob")   # ValidationError: id 必须是 int
except ValidationError as e:
    print(type(e).__name__)   # ValidationError
```

- `TypedDict`（第 5 点）只管"检查期结构"，`Pydantic` 管"运行时校验 + 自动转换 + 序列化"。两者互补：内部传递用 TypedDict 轻量，对外接口 / 配置用 Pydantic 严格。

### 1.2.6 可变默认参数的坑（为什么示例用 `field(default_factory=list)`）

```python
# 危险写法：默认参数在函数定义时只创建一次，所有调用共享同一个 list
def bad(x=[]):
    x.append(1)
    return x
bad()   # [1]
bad()   # [1, 1]  ← 陷阱！第二次调用还在用同一个 list

# 正确写法：用 None 占位，函数内新建
def good(x=None):
    x = x or []
    x.append(1)
    return x
```

这正是 `@dataclass` 里用 `field(default_factory=list)` 的原因（见 1.5 节）。

---

**练习【1.2.6】可变默认参数的坑**

场景：写 `add_tag(tag, tags=None)`，对比错误写法 `tags=[]` 的"上次调用残留"。用 None 哨兵 + 每次新建 list 规避。

```python
# ❌ 错误写法（体会坑）：默认 list 在函数定义时只创建一次，跨调用共享
def add_tag_bad(tag, tags=[]):
    tags.append(tag)
    return tags

# TODO: 正确写法，用 None 哨兵 + 每次新建 list
def add_tag(tag, tags=None):
    ...
    return tags

print(add_tag_bad("a"))   # ['a']
print(add_tag_bad("b"))   # ⚠️ ['a', 'b'] —— 上次残留
print(add_tag("a"))       # ['a']
print(add_tag("b"))       # ['b'] —— 互不影响
```

考察点：可变默认参数坑、None 哨兵 + `if tags is None:` + `[]` 新建（`if` 1.3.1 正式讲，照抄）。

## 1.3 控制流与集合变换

### 1.3.1 if / elif / else

```python
n = 7
if n > 10:
    desc = "大"
elif n > 5:        # 注意是 elif，不是 else if
    desc = "中"
else:
    desc = "小"
```

### 1.3.2 for 循环、range、enumerate、zip

```python
# for ... in 直接遍历（类比增强 for）
for fruit in ["苹果", "香蕉"]:
    print(fruit)

# range 类似 IntStream.range，左闭右开
for i in range(1, 4):      # 1, 2, 3
    print(i)

# enumerate：同时拿索引和值（Java 要自己维护下标）
for idx, fruit in enumerate(["苹果", "香蕉"], 1):   # 从 1 开始编号
    print(idx, fruit)

# zip：并行遍历多个序列
for k, v in zip(["a", "b"], [1, 2]):
    print(k, v)

# in：成员判断（类比 contains）
if "python" in tags:
    print("包含")
```

### 1.3.3 推导式（Python 的灵魂语法，类比 Java Stream）

```python
nums = [1, 2, 3, 4, 5]

# 列表推导：变换 + 收集，类比 stream().map().collect(toList())
squares = [x * x for x in nums]                 # [1, 4, 9, 16, 25]

# 带过滤
evens = [x for x in nums if x % 2 == 0]         # [2, 4]

# 字典推导
upper_map = {k: k.upper() for k in ["a", "b"]}  # {'a':'A', 'b':'B'}

# 集合推导
unique_lens = {len(w) for w in ["hi", "hello", "hi"]}

# 生成器表达式：(...) 不立即算，按需产出，省内存
gen = (x * x for x in nums)
```

示例里 `04b_rag_advanced.py` 的 `[Document(page_content=t) for t in RAW_TEXTS]`、`08_harness_demo.py` 的 `sorted(nodes, key=lambda n: n.score, reverse=True)` 都是这套写法。

**练习【1.3.3】推导式三连**

场景：一组摄氏温度转华氏（F = C\*9/5+32），只保留 ≥25°C 的；再用字典推导式做 word→长度 映射。

```python
celsius = [18, 25, 30, 22, 35]
words = ["ai", "agent", "mcp"]

fahrenheit = [ ... ]              # TODO: 列表推导式 C*9/5+32
hot = [ ... ]                     # TODO: 带 if 过滤，保留 C>=25
word_len = { ... }                # TODO: 字典推导式 word -> len(word)

print(fahrenheit, hot, word_len)
```

考察点：列表推导式、带 `if` 过滤、字典推导式。

### 1.3.4 异常处理 try / except / raise

```python
def divide(a: int, b: int) -> float:
    if b == 0:
        raise ValueError("除数不能为 0")   # raise ≈ throw
    return a / b

try:
    divide(1, 0)
except ValueError as e:                     # except ... as e ≈ catch (ValueError e)
    print(f"出错了: {e}")
finally:
    print("无论成败都执行")                 # ≈ finally
```

示例 `01_llm_basics.py` 的 `json.loads(...)`、`08_harness_demo.py` 的 `raise PermissionError(...)` 都是这套。

**练习【1.3.4】安全除法与转换**

场景：`safe_divide(a, b)` 除零返回 None 不崩；`to_int(s)` 对非数字字符串返回 None。

```python
def safe_divide(a: float, b: float) -> float | None:
    # TODO: try/except ZeroDivisionError，出错返回 None
    ...

def to_int(s: str) -> int | None:
    # TODO: try/except ValueError，出错返回 None
    ...

print(safe_divide(10, 2))   # 5.0
print(safe_divide(1, 0))    # None
print(to_int("42"))         # 42
print(to_int("x"))          # None
```

考察点：`try/except` 捕获**特定**异常、返回兜底值、不要裸 `except:` 吞所有异常。

### 1.3.5 上下文管理器 with（类比 try-with-resources）

```python
# 离开 with 块自动 close 文件，不会漏关（类比 Java try-with-resources）
with open("data.txt", "w", encoding="utf-8") as f:
    f.write("内容")
# 到这里 f 已自动关闭
```

示例 `04_rag_demo.py` 用 `with open(SAMPLE_DOC_PATH, "w", encoding="utf-8") as f:` 生成模拟文档；`06_mcp_server.py` 用 `async with stdio_client(...) as (read, write):` 是异步版（见 1.6 节）。

---

### 1.3.6 上下文管理器进阶（类方式与 contextlib）

上下文管理器用于资源管理（文件、数据库连接、锁等），确保资源正确释放。

```python
import contextlib
import threading

# --- with语句：自动资源管理 ---
# 文件操作
with open("example.txt", "w", encoding="utf-8") as f:
    f.write("Hello, Python!")
# 文件自动关闭，即使发生异常

# --- 自定义上下文管理器：类方式 ---
class DatabaseConnection:
    """模拟数据库连接的上下文管理器"""
    def __init__(self, dsn: str):
        self.dsn = dsn
        self.conn = None

    def __enter__(self):
        print(f"  → 连接数据库: {self.dsn}")
        self.conn = {"dsn": self.dsn, "connected": True}
        return self.conn

    def __exit__(self, exc_type, exc_val, exc_tb):
        print(f"  ← 关闭数据库连接: {self.dsn}")
        if self.conn:
            self.conn["connected"] = False
        # 返回False（默认）表示不抑制异常
        # 返回True表示抑制异常
        return False

# 使用
with DatabaseConnection("postgresql://localhost:5432/mydb") as db:
    print(f"  查询数据: {db}")
    # 即使这里抛异常，__exit__ 也会被调用

# --- 自定义上下文管理器：contextlib方式（推荐简单场景） ---
@contextlib.contextmanager
def timer_context(label: str):
    """计时上下文管理器"""
    import time
    start = time.perf_counter()
    try:
        yield  # yield之前的代码相当于__enter__
        # yield之后的代码相当于__exit__
    finally:
        elapsed = time.perf_counter() - start
        print(f"  ⏱ {label} 耗时 {elapsed:.4f}s")

# 使用
with timer_context("数据处理"):
    total = sum(x ** 2 for x in range(100_000))
    print(f"  结果: {total}")

# --- 多上下文管理器 ---
# 同时管理多个资源
lock = threading.Lock()

@contextlib.contextmanager
def acquire_lock():
    acquired = lock.acquire(timeout=5)
    if not acquired:
        raise TimeoutError("获取锁超时")
    try:
        yield
    finally:
        lock.release()

# 嵌套使用
with DatabaseConnection("mysql://localhost/db") as db, timer_context("事务"):
    print(f"  在事务中操作: {db}")

# Java类比: with语句 ≈ try-with-resources，__enter__/__exit__ ≈ AutoCloseable.close()
```

---

**练习【1.3.6】计时器上下文**

场景：用 `@contextmanager` 写一个 `timer(label)`，进入打印 start、退出打印用时；用它包一段代码。

```python
import time
from contextlib import contextmanager

@contextmanager
def timer(label: str):
    # TODO: 打印 start；记 start=time.perf_counter()；
    #       yield（让 with 块执行）；退出时打印 elapsed
    ...

with timer("sleep-block"):
    time.sleep(0.01)
```

考察点：`@contextmanager` + `yield` + `with`；体会"进入 / 退出"两个钩子，类比 try-with-resources。

## 1.4 函数

### 1.4.1 定义、默认参数、关键字参数

```python
def greet(name: str, greeting: str = "你好") -> str:   # greeting 有默认值
    return f"{greeting}，{name}"

greet("李四")                 # 用默认 greeting
greet("王五", "Hi")           # 位置传参
greet(name="赵六", greeting="Yo")  # 关键字传参（顺序可乱）
```

### 1.4.2 \* 与 \*\* 解包

```python
def query(city: str, unit: str = "celsius") -> str:
    return f"{city}({unit})"

args = {"city": "北京", "unit": "celsius"}
query(**args)        # **dict 把键值对展开成关键字参数，等价于 query(city="北京", unit="celsius")
```

> 示例 `01_llm_basics.py` 里 `get_weather(**args)` 就是 LLM 返回的参数 dict 直接展开传给工具函数——这是 Agent 调工具的经典写法。 `*list` 则把列表展开成位置参数（示例 `08_harness_demo.py` 用 `[system, summary, *recent]` 摊开列表）。

### 1.4.3 lambda 匿名函数

```python
square = lambda x: x * x          # 单表达式，无 return 关键字
square(5)                          # 25
```

示例大量用作 `key`：`sorted(nodes, key=lambda n: n.score)`、`lambda docs: "\n\n".join(d.page_content for d in docs)`。

### 1.4.4 函数是一等公民

Python 函数可以像普通变量一样传参、返回、存进容器（Java 要用函数式接口/方法引用）：

```python
from typing import Callable

def apply(data: list[int], fn: Callable[[int], int]) -> list[int]:
    return [fn(x) for x in data]

apply([1, 2, 3], lambda x: x + 1)    # [2, 3, 4]
```

这是装饰器（1.4.5、1.5.9 节）和"把工具函数注册给 LLM"的基础。

### 1.4.5 类型提示进阶

```python
from typing import Callable, Optional, Any, Literal, Annotated
import operator

cb: Callable[..., Any]                    # 任意参数的函数
mode: Literal["tools", "human", "__end__"]  # 字符串字面量约束（示例条件边返回值）

# Annotated：给类型"附加元信息"，本身不改变类型，被框架读取
msg: Annotated[list, "这是消息列表"]
# LangGraph 用它定义 reducer：operator.add 表示"累加而非覆盖"
```

---

### 1.4.6 函数式编程特性（map / filter / reduce / partial）

```python
from functools import reduce, partial, lru_cache
from operator import add

# --- 高阶函数 ---
# map / filter / reduce
numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# map: 映射
doubled = list(map(lambda x: x * 2, numbers))
print(doubled)  # [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

# filter: 过滤
evens = list(filter(lambda x: x % 2 == 0, numbers))
print(evens)  # [2, 4, 6, 8, 10]

# reduce: 归约
total = reduce(add, numbers)
print(total)  # 55

# 推导式更Pythonic（推荐）
doubled = [x * 2 for x in numbers]
evens = [x for x in numbers if x % 2 == 0]

# --- partial: 偏函数 ---
def power(base: int, exponent: int) -> int:
    return base ** exponent

square = partial(power, exponent=2)
cube = partial(power, exponent=3)
print(square(5))  # 25
print(cube(3))    # 27

# --- lru_cache: 自动缓存 ---
@lru_cache(maxsize=128)
def expensive_computation(n: int) -> int:
    """带缓存的递归斐波那契"""
    if n < 2:
        return n
    return expensive_computation(n - 1) + expensive_computation(n - 2)

print(expensive_computation(100))  # 瞬间完成（有缓存）

# --- 解包与多返回值 ---
def min_max(numbers: list) -> tuple:
    return min(numbers), max(numbers)

lo, hi = min_max([3, 1, 4, 1, 5, 9, 2, 6])
print(f"min={lo}, max={hi}")

# 星号解包
first, *middle, last = [1, 2, 3, 4, 5]
print(f"first={first}, middle={middle}, last={last}")

# 字典解包
defaults = {"host": "localhost", "port": 5432}
overrides = {"port": 8080, "debug": True}
config = {**defaults, **overrides}
print(config)  # {'host': 'localhost', 'port': 8080, 'debug': True}
```

---

**练习【1.4】函数式处理用户**

场景：一组用户 dict，用 `filter` + `lambda` 筛成年人，`sorted` 按 age 排序，`map` 提取名字。

```python
users = [
    {"name": "Alice", "age": 17},
    {"name": "Bob",   "age": 20},
    {"name": "Cara",  "age": 30},
    {"name": "Dan",   "age": 16},
]

adults = ...        # TODO: filter + lambda 筛 age >= 18
ordered = ...       # TODO: sorted(adults, key=...) 按 age
names = ...         # TODO: map + lambda 提取 name，再 list(...)

print(names)        # ['Bob', 'Cara']
```

考察点：`lambda`、`filter/map/sorted` 高阶函数、`key=` 回调、函数是一等公民。

## 1.5 面向对象与抽象

### 1.5.1 类、**init**、self

```python
class Animal:
    def __init__(self, name: str) -> None:   # 构造器，self 必须显式写（≈ Java 的 this）
        self.name = name
    def speak(self) -> str:
        return f"{self.name} 叫"

Animal("狗").speak()
```

> `self` 不是关键字但约定俗成，相当于 Java 的 `this`，且**必须**作为第一个参数写。

### 1.5.2 继承与多态

```python
class Dog(Animal):
    def speak(self) -> str:        # 重写
        return f"{self.name} 汪汪"

def let_it_speak(a: Animal) -> None:   # 多态：接收父类，实际跑子类
    print(a.speak())
```

### 1.5.3 抽象基类 ABC + @abstractmethod（≈ abstract class）

```python
from abc import ABC, abstractmethod

class Shape(ABC):
    @abstractmethod
    def area(self) -> float: ...    # ... 是 Ellipsis，表示"留空"，强制子类实现

class Circle(Shape):
    def area(self) -> float:
        return 3.14
```

示例 `07_a2a_demo.py`、`09_skill_demo.py`、`11_enterprise_arch.py` 都用 `BaseAgent(ABC)` / `Orchestrator(ABC)` 定义契约。

### 1.5.4 @property（≈ getter）

```python
class User:
    def __init__(self, first: str, last: str) -> None:
        self.first = first
        self.last = last
    @property
    def full_name(self) -> str:     # 调用时不用加 ()
        return f"{self.first}{self.last}"

User("张", "三").full_name         # "张三"
```

示例 `09_skill_demo.py` 用 `@property` + `@abstractmethod` 定义 `tools` 属性。

### 1.5.5 @staticmethod / @classmethod

```python
class MathUtil:
    @staticmethod
    def add(x: int, y: int) -> int:     # 不依赖实例，≈ Java static
        return x + y
    @classmethod
    def create(cls):                     # 第一个参数是类本身
        return cls()
```

示例 `13_case_dev_agent.py` 的 `CodingSkill.run` 用 `@staticmethod`。

### 1.5.6 Protocol 与鸭子类型（≈ interface，但结构化）

```python
from typing import Protocol

class Logger(Protocol):          # 只要实现了 log 方法，就算 Logger，无需显式 implements
    def log(self, msg: str) -> None: ...

class ConsoleLogger:
    def log(self, msg: str) -> None:
        print(msg)

def use(l: Logger) -> None:
    l.log("hi")

use(ConsoleLogger())             # ConsoleLogger 没继承 Logger，但"长得像"就能传
```

示例 `11_enterprise_arch.py` 用 `LLMService(Protocol)`、`MemoryService(Protocol)` 等定义能力层接口——这是"面向接口编程"的 Python 写法。

### 1.5.7 @dataclass（示例代码绝对主力，必掌握）

`@dataclass` 自动生成 `__init__`、`__repr__`、`__eq__` 等，约等于 Java 的 Lombok `@Data` / `@Value`。

```python
from dataclasses import dataclass, field

@dataclass
class Point:
    x: int
    y: int

p = Point(1, 2)          # 自动有了构造器，不用手写 def __init__

@dataclass
class ChatSession:
    messages: list = field(default_factory=list)   # 可变默认值用工厂，避免共享
```

> 示例里 `01_llm_basics.py` 的 `ChatSession`、`07_a2a_demo.py` 的 `AgentMessage`、`09_skill_demo.py` 的 `BaseSkill`、`11_enterprise_arch.py` 的 `UserRequest`、`13_case_dev_agent.py` 的 `Harness` 全是 `@dataclass`。**不会这个，等于看不懂示例。**

**练习【1.5·dataclass】商品清单**

场景：用 `@dataclass` 定义 `Product(name, price, quantity=1)`，创建几个实例，用推导式 + `sum` 算总价，`sorted` 按 price 排序。

```python
from dataclasses import dataclass

@dataclass
class Product:
    name: str
    price: float
    quantity: int = 1

items = [Product("A", 10.0, 2), Product("B", 3.5, 5), Product("C", 50.0)]

total = sum([ ... ])              # TODO: 推导式算 price*quantity 之和
cheapest_first = sorted(...)      # TODO: 按 price 升序

print(total, [p.name for p in cheapest_first])
```

考察点：`@dataclass` 定义、字段默认值、把 dataclass 实例当普通对象用 + 推导式/`sorted` 综合。

### 1.5.8 TypedDict（带类型的 dict，≈ record）

```python
from typing import TypedDict

class AgentMessage(TypedDict):
    sender: str
    receiver: str
    content: str

msg: AgentMessage = {"sender": "A", "receiver": "B", "content": "hi"}
print(msg["content"])
```

示例 `03_langgraph_demo.py` 的 `AgentState(TypedDict)`、`13_case_dev_agent.py` 的 `DevState(TypedDict)` 用它定义 LangGraph 在节点间传递的共享状态。

### 1.5.9 装饰器基础（@tool / @mcp.tool() 的底层）

装饰器本质：**接收函数、返回函数的"高阶函数"**。理解这个，示例里的 `@tool`、`@mcp.tool()`、`@contextmanager`、`@abstractmethod` 就都不神秘了。

```python
from typing import Callable, Any

def log_call(func: Callable[..., Any]) -> Callable[..., Any]:
    def wrapper(*args, **kwargs):
        print(f"调用 {func.__name__}")
        return func(*args, **kwargs)
    return wrapper

@log_call                       # 等价于：add = log_call(add)
def add(a: int, b: int) -> int:
    return a + b
```

> 示例里的 `@tool`（LangChain 把函数变 Tool）、`@mcp.tool()`（MCP 把函数变可调用工具）、`@contextmanager`（把生成器变上下文管理器）全是同一套机制，只是装饰器内部做的事不同。

---

### 1.5.10 装饰器进阶（带参数装饰器、类装饰器、堆叠）

装饰器是Python的高级特性，用于在不修改函数的前提下增强函数行为。

```python
import time
import functools
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# --- 基础装饰器：计时 ---
def timer(func):
    """测量函数执行时间"""
    @functools.wraps(func)  # 保留原函数的元信息
    def wrapper(*args, **kwargs):
        start = time.perf_counter()
        result = func(*args, **kwargs)
        elapsed = time.perf_counter() - start
        logger.info(f"{func.__name__} 耗时 {elapsed:.4f}s")
        return result
    return wrapper

# --- 带参数的装饰器 ---
def retry(max_attempts=3, delay=1.0):
    """失败自动重试"""
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            for attempt in range(1, max_attempts + 1):
                try:
                    return func(*args, **kwargs)
                except Exception as e:
                    if attempt == max_attempts:
                        raise
                    logger.warning(f"第{attempt}次失败: {e}, {delay}s后重试...")
                    time.sleep(delay)
        return wrapper
    return decorator

# --- 类装饰器 ---
class CountCalls:
    """统计函数调用次数"""
    def __init__(self, func):
        self.func = func
        self.count = 0
        functools.update_wrapper(self, func)

    def __call__(self, *args, **kwargs):
        self.count += 1
        logger.info(f"{self.func.__name__} 已调用 {self.count} 次")
        return self.func(*args, **kwargs)

# --- 使用装饰器 ---
@timer
@retry(max_attempts=3, delay=0.5)
@CountCalls
def fetch_data(url: str) -> str:
    import random
    if random.random() < 0.5:
        raise ConnectionError(f"连接 {url} 失败")
    return f"Data from {url}"

# --- 装饰器堆叠执行顺序 ---
# CountCalls(fetch_data) → retry(...) → timer(...)
# 调用时: timer → retry → CountCalls → 实际函数

if __name__ == "__main__":
    try:
        result = fetch_data("https://api.example.com/data")
        print(result)
    except Exception as e:
        print(f"最终失败: {e}")

# Java类比: 装饰器 ≈ Spring AOP @Around注解，但Python用函数而非注解实现
```

---

**练习【1.5·装饰器】带参 @retry**

场景：写 `@retry(times=3)`，包装一个"可能随机失败"的函数，失败自动重试到 times 次或成功。

```python
import random
from functools import wraps

def retry(times: int = 3):
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            # TODO: 最多循环 times 次；try 调 func；
            #       成功就 return；全失败就 raise 最后一次的异常
            ...
        return wrapper
    return decorator

@retry(times=4)
def flaky():
    if random.random() < 0.7:
        raise RuntimeError("transient fail")
    return "ok"

print(flaky())
```

考察点：带参数装饰器（三层嵌套）、`functools.wraps`、`*args/**kwargs` 透传、闭包捕获 `times`。

### 1.5.11 生成器（惰性计算与 yield）

生成器是惰性计算的利器，用 `yield` 逐步产出值，内存占用极低。

```python
import sys
from typing import Generator, Iterator

# --- 基础生成器 ---
def fibonacci(n: int) -> Generator[int, None, None]:
    """生成前n个斐波那契数"""
    a, b = 0, 1
    for _ in range(n):
        yield a
        a, b = b, a + b

# 逐个产出，不占用大量内存
for num in fibonacci(10):
    print(num, end=" ")  # 0 1 1 2 3 5 8 13 21 34
print()

# --- 生成器 vs 列表：内存对比 ---
# 列表：一次性生成所有元素
big_list = [x ** 2 for x in range(1_000_000)]
print(f"列表内存: {sys.getsizeof(big_list) / 1024 / 1024:.1f} MB")  # ~8 MB

# 生成器：惰性求值
big_gen = (x ** 2 for x in range(1_000_000))
print(f"生成器内存: {sys.getsizeof(big_gen)} bytes")  # ~200 bytes

# --- 生成器方法：send / throw / close ---
def accumulator() -> Generator[int, int, None]:
    """可接收外部值的累加器"""
    total = 0
    while True:
        value = yield total
        if value is None:
            break
        total += value

acc = accumulator()
next(acc)          # 启动生成器，返回0
print(acc.send(10))  # 10
print(acc.send(20))  # 30
print(acc.send(30))  # 60
acc.close()         # 关闭生成器

# --- yield from：委托生成器 ---
def chain_generators(*iterables) -> Generator:
    """将多个可迭代对象串联"""
    for iterable in iterables:
        yield from iterable  # 等价于 for x in iterable: yield x

result = list(chain_generators([1, 2], [3, 4], [5, 6]))
print(result)  # [1, 2, 3, 4, 5, 6]

# --- 实战：逐行读取大文件 ---
def read_large_file(filepath: str, encoding: str = "utf-8") -> Generator[str, None, None]:
    """逐行读取大文件，不一次性加载到内存"""
    with open(filepath, encoding=encoding) as f:
        for line in f:
            yield line.rstrip("\n")

# 使用（即使文件10GB也不会爆内存）
# for line in read_large_file("huge_data.csv"):
#     process(line)

# --- 无限生成器 ---
def natural_numbers(start: int = 1) -> Generator[int, None, None]:
    """无限自然数序列"""
    n = start
    while True:
        yield n
        n += 1

# 用 itertools.islice 取前N个
from itertools import islice
first_10 = list(islice(natural_numbers(), 10))
print(first_10)  # [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# Java类比: 生成器 ≈ Stream.generate()，但yield机制更直观，内存效率更高
```

---

**练习【1.5·生成器】无限斐波那契**

场景：写一个无限斐波那契生成器 `fib()`，用 `yield` 惰性产出；用 `itertools.islice` 取前 10 项。

```python
from itertools import islice

def fib():
    # TODO: 用 yield 无限产出 1,1,2,3,5,8,13,...
    ...

g = fib()
print(list(islice(g, 10)))   # [1,1,2,3,5,8,13,21,34,55]
```

考察点：`yield`、生成器惰性求值、`next`、与 `islice` 配合从无限序列取有限个。

### 1.5.12 结构化模式匹配 match / case

> 对应 Java 21+ 的 switch 表达式 + 模式匹配（≈ Java 21 的 switch 模式匹配）。

```python
from enum import Enum

class MessageType(Enum):
    TEXT = "text"
    IMAGE = "image"
    FILE = "file"

def handle_message(msg: dict) -> str:
    match msg:
        case {"type": MessageType.TEXT, "content": text}:
            return f"文本消息: {text}"
        case {"type": MessageType.IMAGE, "url": url, "width": w, "height": h}:
            return f"图片: {url} ({w}x{h})"
        case {"type": MessageType.FILE, "filename": name, "size": size} if size > 10_000_000:
            return f"大文件: {name} ({size} bytes)"
        case {"type": MessageType.FILE, "filename": name}:
            return f"小文件: {name}"
        case _:
            return "未知消息类型"

print(handle_message({"type": MessageType.TEXT, "content": "Hello"}))
print(handle_message({"type": MessageType.FILE, "filename": "data.csv", "size": 500}))
print(handle_message({"type": MessageType.IMAGE, "url": "http://img.jpg", "width": 800, "height": 600}))

# Java类比: match/case ≈ switch表达式 + 模式匹配（JDK 21+）
```

**练习【1.5·match】HTTP 状态码分类**

场景：用 `match/case` 给 HTTP 状态码分类，带守卫子句区分 2xx/4xx/5xx。

```python
def classify(code: int) -> str:
    match code:
        # TODO: 用 case + 守卫 if 200 <= code < 300 等返回 "success"/"client"/"server"
        ...
        case _:
            return "unknown"

print(classify(200))   # success
print(classify(404))   # client
print(classify(500))   # server
print(classify(302))   # unknown
```

考察点：`match/case`、守卫子句 `case _ if ...`、默认 `case _`。

## 1.6 异步编程 async / await

### 1.6.1 心智模型

Java 工程师已经懂并发。对应关系：

| Java | Python |
| --- | --- |
| `Thread` / 线程池 | 不常用，GIL 下单线程更划算 |
| `CompletableFuture` | `async`/`await`（协程） |
| Spring WebFlux / Reactor | `asyncio` 事件循环 |
| `synchronized` / 锁 | `asyncio` 下多用队列/锁原语 |

核心区别：**Python 的 `async` 是单线程内的"协作式多任务"（协程）**，靠 `await` 主动让出控制权，特别适合 IO 密集（网络请求、读写文件、调 LLM）。它**不是**多线程并行。

### 1.6.2 async def / await / asyncio.run

```python
import asyncio

async def fetch(name: str) -> str:      # async def 定义协程
    await asyncio.sleep(0.1)            # await 等待一个异步操作（让出控制权）
    return f"数据来自 {name}"

async def main():
    r1 = await fetch("A")               # 顺序等
    r2 = await fetch("B")
    print(r1, r2)

asyncio.run(main())                     # 启动事件循环（≈ 提交到线程池并等完成）
```

**练习【1.6·async 基础】串行异步任务**

场景：写 `async def fetch(name)` 模拟异步 IO（`await asyncio.sleep`），在 `main()` 里依次 await 三个任务，用 `asyncio.run` 跑。

```python
import asyncio

async def fetch(name: str) -> str:
    await asyncio.sleep(0.01)        # 模拟 IO
    return f"data-{name}"

async def main() -> None:
    # TODO: 依次 await fetch("a"/"b"/"c")，把结果收集进 list 打印
    ...

asyncio.run(main())
```

考察点：`async def` / `await` / `asyncio.run`、协程串行调用、`asyncio.run` 是入口。

### 1.6.3 async with / async for（简述）

```python
# 异步上下文管理器：进入/退出也要 await
async with some_async_resource() as r:
    ...
# 异步迭代
async for item in some_async_iterable():
    ...
```

示例 `06_mcp_server.py` 的 `async with stdio_client(server_params) as (read, write):` 和 `await session.list_tools()` 就是 MCP 客户端连接——因为跨进程通信是 IO，天然异步。

### 1.6.4 何时用

- **用**：调 LLM API、访问数据库、HTTP 请求、MCP/A2A 通信（全是等网络）。
- **不用**：纯 CPU 计算（如大循环算数），那用同步或多进程更合适。

> 给 Java 工程师的定心丸：你**不必先精通 asyncio 才能读示例**。看到 `async/await` 知道"这是异步 IO、用 `asyncio.run` 驱动"即可；真正要并发调 LLM 时再深学。

---

### 1.6.5 异步并发进阶（gather / 超时 / 信号量 / 队列）

Python的async/await用于IO密集型任务（网络请求、数据库查询等），单线程并发。

```python
import asyncio
import aiohttp  # pip install aiohttp
import time
from typing import List

# --- 基础协程 ---
async def hello() -> str:
    """异步函数（协程）"""
    await asyncio.sleep(0.5)  # 非阻塞等待
    return "Hello, Async!"

# --- 并发执行多个协程 ---
async def fetch_url(session: aiohttp.ClientSession, url: str) -> str:
    """异步HTTP请求"""
    async with session.get(url) as response:
        return await response.text()

async def fetch_all(urls: List[str]) -> List[str]:
    """并发获取多个URL"""
    async with aiohttp.ClientSession() as session:
        tasks = [fetch_url(session, url) for url in urls]
        # asyncio.gather: 并发执行所有任务
        results = await asyncio.gather(*tasks)
        return results

# --- asyncio.create_task: 后台任务 ---
async def background_job() -> None:
    """后台任务示例"""
    for i in range(5):
        print(f"  后台任务进度: {i+1}/5")
        await asyncio.sleep(0.3)

async def main_task() -> None:
    """主任务"""
    task = asyncio.create_task(background_job())
    # 同时做其他工作
    await asyncio.sleep(1)
    print("  主任务完成")
    await task  # 等待后台任务完成

# --- 超时控制 ---
async def slow_operation() -> str:
    await asyncio.sleep(10)
    return "终于完成了"

async def with_timeout() -> str:
    try:
        # Python 3.11+ 推荐用 asyncio.timeout
        async with asyncio.timeout(2.0):
            return await slow_operation()
    except TimeoutError:
        return "操作超时!"

# --- 信号量：限制并发数 ---
async def rate_limited_fetch(urls: List[str], max_concurrent: int = 3) -> List[str]:
    """限制并发数的批量请求"""
    semaphore = asyncio.Semaphore(max_concurrent)

    async def fetch_with_limit(session, url):
        async with semaphore:  # 获取信号量
            return await fetch_url(session, url)

    async with aiohttp.ClientSession() as session:
        tasks = [fetch_with_limit(session, url) for url in urls]
        return await asyncio.gather(*tasks)

# --- asyncio.Queue：协程间通信 ---
async def producer(queue: asyncio.Queue, items: list) -> None:
    for item in items:
        await asyncio.sleep(0.1)  # 模拟生产耗时
        await queue.put(item)
        print(f"  📤 生产: {item}")
    await queue.put(None)  # 哨兵值

async def consumer(queue: asyncio.Queue) -> None:
    while True:
        item = await queue.get()
        if item is None:
            queue.task_done()
            break
        await asyncio.sleep(0.2)  # 模拟消费耗时
        print(f"  📥 消费: {item}")
        queue.task_done()

async def producer_consumer_demo() -> None:
    queue: asyncio.Queue = asyncio.Queue(maxsize=5)
    await asyncio.gather(
        producer(queue, ["A", "B", "C", "D"]),
        consumer(queue)
    )

# --- 运行入口 ---
async def main() -> None:
    print("=== 基础协程 ===")
    result = await hello()
    print(f"  {result}")

    print("\n=== 后台任务 ===")
    await main_task()

    print("\n=== 超时控制 ===")
    print(f"  {await with_timeout()}")

    print("\n=== 生产者-消费者 ===")
    await producer_consumer_demo()

if __name__ == "__main__":
    # asyncio.run() 是推荐的启动方式
    asyncio.run(main())

# Java类比: async/await ≈ CompletableFuture，但Python语法更简洁
# Python是单线程协程（事件循环），Java的CompletableFuture可配线程池
```

---

**练习【1.6·并发】gather + 超时**

场景：把上一题的"依次 await"改成 `asyncio.gather` 并发；再用 `asyncio.wait_for` 给任务设超时，体会 `TimeoutError`。

```python
import asyncio

async def fetch(name: str) -> str:
    await asyncio.sleep(0.01)
    return f"data-{name}"

async def main() -> None:
    # TODO: 用 asyncio.gather 并发跑 a/b/c
    results = ...
    print(results)

    # TODO: 用 asyncio.wait_for(fetch("slow"), timeout=0.005) 触发 TimeoutError
    ...

asyncio.run(main())
```

考察点：`asyncio.gather` 并发（对比串行）、`asyncio.wait_for` 超时、`TimeoutError`。

## 1.7 模块、包与工程组织

### 1.7.1 模块 = 文件，包 = 目录

```
my_project/
├── pyproject.toml        # 现代项目的"pom.xml"（poetry/uv 用）
├── src/
│   └── mylib/
│       ├── __init__.py   # 标记为包（可为空）
│       ├── core.py       # 模块
│       └── utils.py      # 模块
└── tests/
```

`import mylib.core` 就是从 `mylib` 包的 `core.py` 导入。运行项目推荐 **src layout**（代码放 `src/` 下），避免测试时误 import 到源码目录。

### 1.7.2 绝对导入 vs 相对导入

```python
from mylib.core import Foo            # 绝对导入（推荐，清晰）
from .utils import helper             # 相对导入：. 表示当前包
from ..core import Foo                # .. 表示上一级包
```

### 1.7.3 常用标准库速览

| 模块 | 用途 | Java 类比 |
| --- | --- | --- |
| `os` | 环境变量、路径、进程 | `System.getenv` / `java.io.File` |
| `sys` | 解释器参数、退出 | — |
| `json` | JSON 序列化 | Jackson / Gson |
| `datetime` | 时间 | `java.time` |
| `pathlib` | 面向对象路径操作 | `java.nio.file.Path` |
| `argparse` | 命令行参数解析 | picocli / JCommander |
| `re` | 正则 | `java.util.regex` |

示例用到的：`os.getenv("DEEPSEEK_API_KEY")`、`sys.argv`、`json.loads/dumps(..., ensure_ascii=False)`、`datetime.now().isoformat()`、`hasattr(msg, "tool_calls")`、`operator.add`。

### 1.7.4 项目结构建议

- 小脚本：单 `.py` 文件即可。
- 库/服务：用 `src/` 布局 + `pyproject.toml`（poetry 或 uv 管理）。
- 应用：FastAPI 服务见 1.9 节。

---

**练习【1.7】把扁平脚本重构成包**

场景：把一个扁平脚本重构成包 `mypkg`，要求 `python -m mypkg.main` 能跑、用绝对导入。

```text
# 目标结构
mypkg/
  __init__.py        # 空文件，标记为包
  math_utils.py      # def add(a, b): return a + b
  main.py            # 绝对导入 add 并打印 add(1, 2)
```

```python
# mypkg/math_utils.py
def add(a: int, b: int) -> int:
    return a + b

# mypkg/main.py —— TODO: 用绝对导入 from mypkg.math_utils import add，并打印 add(1, 2)

# mypkg/__init__.py —— 空文件即可
```

考察点：模块=文件、包=目录 + `__init__.py`、绝对导入、`python -m pkg.mod` 运行入口。

## 1.8 串起来读一段真实示例

前面每章都散点讲了语法。现在读一段"合成但真实风格"的代码——它把示例里最常见的几个写法集中在一起，逐行标注。读完后，你再翻 `examples/03_langgraph_demo.py` 这种文件就不会慌了。

```python
from typing import Annotated, TypedDict
from dataclasses import dataclass, field
import operator

# ① TypedDict：定义节点间传递的状态结构（≈ Java record）
class AgentState(TypedDict):
    # ② Annotated[list, operator.add]：框架看到这个元信息，
    #    节点输出时"累加"而非"覆盖" messages 列表
    messages: Annotated[list, operator.add]
    step: int

# ③ @dataclass：自动构造器，少写一堆 __init__
@dataclass
class Tool:
    name: str
    func: object = field(default_factory=lambda: None)   # ④ field(default_factory=...) 安全默认

# ⑤ 普通函数 + 类型提示（≈ Java 方法签名）
def should_continue(state: AgentState) -> str:
    last = state["messages"][-1]                 # ⑥ dict 取值 + list 取最后一个
    if hasattr(last, "tool_calls") and last.tool_calls:   # ⑦ hasattr 判断属性
        return "tools"                            # ⑧ 返回字符串当"下一跳"（条件边）
    return "end"

# ⑨ f-string 插值 + 推导式
def summarize(msgs: list) -> str:
    return "\n".join(f"- {m}" for m in msgs)     # ⑩ 生成器表达式，按需产出
```

逐条对应：① TypedDict（1.5.8 节）→ ② Annotated/operator.add（1.4.5 节）→ ③ @dataclass（1.5.7 节）→ ④ field（1.5.7 节）→ ⑤ def+类型提示（1.2.1/1.4.1 节）→ ⑥ 容器取值（1.2.4 节）→ ⑦ hasattr（标准库）→ ⑧ 字符串返回（控制流）→ ⑨ f-string（1.2.3 节）→ ⑩ 推导式（1.3.3 节）。

> 建议练习：打开 `examples/01_llm_basics.py`，只看 `@dataclass ChatSession` 和 `demo_tool_calling` 两段，确认你能说出每一行的语法点。卡住就回看对应章节。

---

## 1.9 当前主流技术科普

> 对应需求：介绍 Python 生态中主流技术方向，便于读者后续对接主流技术栈。 定位：生态地图，不是教学。每条说清"是什么 / 解决什么 / Java 类比 / 代表库 / 何时学"。

Python 生态比 Java 更"碎片化"——没有一家独大的 Spring，而是按领域各分天下。下面按"你会接到哪类需求"来科普。

### 1.9.1 Web 开发（写后端服务）

| 项 | 内容 |
| --- | --- |
| 是什么 | 用 Python 写 HTTP 服务 / API |
| 代表库 | **FastAPI**（异步、类型提示驱动，最主流）、Flask（轻量微框架）、Django（全家桶 MVC） |
| Java 类比 | FastAPI ≈ Spring Boot（且自带 OpenAPI 文档）；Django ≈ Spring + Hibernate 一体 |
| 数据校验 | **Pydantic**（用类型提示定义请求/响应模型，≈ Java Bean Validation / DTO） |
| 何时学 | 你要写"AI 能力的 HTTP 接口"时（几乎必然）。本课程 `07_a2a_demo.py` 已出现 FastAPI 影子 |

```python
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()
class Req(BaseModel):
    name: str
@app.post("/hello")
def hi(req: Req):
    return {"ok": True, "name": req.name}
```

### 1.9.2 数据科学（分析、清洗、可视化）

| 项 | 内容 |
| --- | --- |
| 是什么 | 表格数据处理、统计、画图 |
| 代表库 | **NumPy**（多维数组/向量运算）、**Pandas**（DataFrame，≈ Java 没有对标，最接近 Excel/SQL 表）、Matplotlib/Seaborn（画图）、scikit-learn（传统机器学习） |
| Java 类比 | Pandas DataFrame ≈ 一张数据库表 + 类 SQL 操作；NumPy ≈ 高性能数组 |
| 何时学 | 做数据分析、特征工程、报表时 |

### 1.9.3 深度学习（训练/调用神经网络）

| 项 | 内容 |
| --- | --- |
| 是什么 | 构建和训练神经网络 |
| 代表库 | **PyTorch**（研究/生产首选，动态图）、TensorFlow（工业部署老牌）、JAX（Google 高性能） |
| Java 类比 | 无直接对标；类比深度学习版的"应用服务器" |
| 何时学 | 你要自己训练模型、做微调（LoRA/PEFT）时。本课程只"调用"模型，不训练 |

### 1.9.4 LLM 应用框架（本课程核心）

| 项 | 内容 |
| --- | --- |
| 是什么 | 把 LLM、检索、记忆、工具拼成应用 |
| 代表库 | **LangChain**（组件化编排）、**LangGraph**（有状态/循环的 Agent 工作流）、**LlamaIndex**（RAG 专用） |
| Java 类比 | 编排层 ≈ Spring Integration / Camunda；RAG ≈ 带向量的 Elasticsearch 管道 |
| 何时学 | 立即。本课程 13 章全在这层 |

### 1.9.5 Agent 协议与多 Agent 协作

| 项 | 内容 |
| --- | --- |
| 是什么 | 标准化"Agent ↔ 工具"、"Agent ↔ Agent"的通信 |
| 代表库/规范 | **MCP**（Model Context Protocol，工具协议，≈ gRPC）、**A2A**（Agent To Agent，多 Agent 协作） |
| Java 类比 | MCP ≈ RPC/gRPC；A2A ≈ 微服务间通信（多 Agent 是"微服务化在 Agent 层的重演"） |
| 何时学 | 你要让 Agent 接公司内部系统、或多 Agent 分工时（主教程第 7、8 章） |

### 1.9.6 向量数据库与 RAG 工具链

| 项 | 内容 |
| --- | --- |
| 是什么 | 存向量、做语义检索，是 RAG 的"记忆底座" |
| 代表库 | **Chroma**（轻量本地）、**Milvus**（大规模）、**FAISS**（Meta 向量引擎）、**pgvector**（PostgreSQL 插件）、Qdrant |
| Java 类比 | 带向量索引的 Elasticsearch |
| 配套 | sentence-transformers（本地 Embedding）、Reranker（交叉编码器精排） |
| 何时学 | 做知识库问答、企业文档检索时（主教程第 5、6 章） |

### 1.9.7 异步与并发

| 项 | 内容 |
| --- | --- |
| 是什么 | IO 密集型并发（调 LLM/DB/HTTP） |
| 代表库 | **asyncio**（标准库事件循环）、httpx（异步 HTTP 客户端，≈ 异步版 OkHttp） |
| Java 类比 | WebFlux / Reactor / CompletableFuture |
| 何时学 | 并发调多个 LLM 或工具、写高吞吐服务时（1.6 节） |

### 1.9.8 类型与工程化（写出靠谱的 Python）

| 项 | 内容 |
| --- | --- |
| 是什么 | 静态检查、数据校验、代码格式化 |
| 代表库 | **mypy**（类型检查，≈ 编译期类型校验）、**ruff**（超快 lint+格式化，≈ Checkstyle+Spotless）、**pydantic**（运行时校验） |
| Java 类比 | mypy ≈ `javac` 类型检查；ruff ≈ Spotless/Checkstyle；pydantic ≈ Bean Validation |
| 何时学 | 项目上了规模、要多人协作时 |

### 1.9.9 测试

| 项 | 内容 |
| --- | --- |
| 是什么 | 单元测试 / 集成测试 |
| 代表库 | **pytest**（事实标准，写法极简）、unittest（标准库，≈ JUnit） |
| Java 类比 | pytest ≈ JUnit 5；`assert x == y` 直接写，不用 `assertEquals` |
| 何时学 | 写任何要长期维护的代码时 |

```python
# test_demo.py
def add(a, b):
    return a + b
def test_add():
    assert add(1, 2) == 3      # pytest 直接跑这个
```

### 1.9.10 打包与部署

| 项 | 内容 |
| --- | --- |
| 是什么 | 把 Python 项目打包、容器化、发到生产 |
| 代表工具 | **uv / poetry**（依赖与打包）、**Docker**（容器化，Python 官方有 slim 镜像）、**conda**（科学计算环境） |
| Java 类比 | poetry/uv ≈ Gradle；Docker 用法完全一致 |
| 何时学 | 要上线时。Python 服务进 Docker 和 Java 没区别 |

### 1.9.11 大数据 / 分布式

| 项 | 内容 |
| --- | --- |
| 是什么 | 海量数据批处理 |
| 代表库 | **PySpark**（Spark 的 Python API）、Dask（轻量并行） |
| Java 类比 | PySpark ≈ 用 Python 写 Spark（底层还是 JVM） |
| 何时学 | 数据规模超出单机 Pandas 时 |

### 1.9.12 科学计算 / Notebook

| 项 | 内容 |
| --- | --- |
| 是什么 | 交互式、可逐步执行的文档 |
| 代表工具 | **Jupyter Notebook / JupyterLab**、JupyterLab |
| Java 类比 | 无直接对标；≈ 带 Markdown 的可执行实验笔记 |
| 何时学 | 做探索性实验、写技术报告、教学演示时（AI 研究几乎人人用） |

### 1.9.13 一张选型速查表

| 你的需求 | 首选技术 | 类比 |
| --- | --- | --- |
| 写 AI 能力的 HTTP 接口 | FastAPI + Pydantic | Spring Boot |
| 拼装 LLM 应用 | LangChain / LangGraph | Spring Integration |
| 知识库问答 | LlamaIndex + Chroma + Rerank | ES + 向量 |
| Agent 接内部系统 | MCP | gRPC |
| 多 Agent 协作 | A2A | 微服务通信 |
| 数据分析 | Pandas + NumPy | — |
| 异步高并发 | asyncio + httpx | WebFlux |
| 工程治理 | mypy + ruff + pytest | Checkstyle + JUnit |
| 训练模型 | PyTorch | — |
| 部署 | Docker + uv/poetry | Docker + Gradle |

---

## 1.10 语法特性速查表（精要版）

| 特性 | Python | Java类比 | 核心差异 |
| --- | --- | --- | --- |
| 动态类型 | 运行时绑定 | 编译时声明 | Python更灵活但需自律 |
| 推导式 | `[x for x in ...]` | Stream API | Python语法更简洁 |
| 装饰器 | `@decorator` | `@Around` (AOP) | Python用函数实现 |
| 上下文管理器 | `with` | try-with-resources | 概念类似 |
| 生成器 | `yield` | `Stream.generate()` | Python更原生 |
| async/await | 单线程事件循环 | CompletableFuture | 并发模型不同 |
| 类型提示 | 可选，运行时不强制 | 强制 | 渐进式类型系统 |
| dataclass | `@dataclass` | `record` | 自动生成样板代码 |
| 模式匹配 | `match/case` | `switch` (JDK 21+) | Python支持结构解构 |

## 附录 A · 语法点覆盖对照表

下表逐条核对：每个语法点 → 在本文哪章讲 → 出现在哪些 `examples/` 示例文件。用于自查"示例里出现的语法点本文是否都覆盖"。

| 语法点 | 本文章节 | 出现于示例（代表性） |
| --- | --- | --- |
| `if __name__ == "__main__"` | 1.1.2 | 全部 15 个 |
| `import` / `from ... import` | 1.1.3 | 全部 |
| 函数定义 `def` + 类型提示 `->` | 1.2.1 / 1.4.1 | 全部 |
| `f-string`（含条件表达式） | 1.2.3 | 00,01,02,03,04,06,07,08… |
| 原始字符串 `r"..."` | 1.2.3 | 09 |
| 多行字符串 `"""..."""` | 1.1.4 | 全部 |
| 基础类型 / `None is None` | 1.2.2 | 全部 |
| `list` / `dict` / `tuple` / `set`（含元组解包/比较） | 1.2.4 | 00,03,13（全部均用容器） |
| `list[str]` / `list[dict]` / `dict[str,X]` | 1.2.4 / 1.2.5 | 07,08,09,11,13 |
| `Optional` / `Any` / `Callable` | 1.2.5 / 1.4.5 | 08,09 |
| `Literal[...]` | 1.4.5 | 03,13 |
| `Annotated[..., operator.add]` | 1.4.5 | 03,13 |
| `TypedDict` | 1.5.8 | 03,13 |
| `Protocol`（鸭子类型） | 1.5.6 | 11 |
| `if/elif/else` | 1.3.1 | 00,01,03,04,08… |
| `for` / `range` / `enumerate` / `zip` | 1.3.2 | 04,04b,05,07,08 |
| 推导式（列表/字典/生成器） | 1.3.3 | 04b,05,08 |
| `* 解包` / `** 解包` | 1.4.2 | 01,08 |
| `lambda` | 1.4.3 | 04,04b,05,08,12 |
| `try/except/raise` | 1.3.4 | 01,08 |
| `with open(...) as f` | 1.3.5 | 04,04b,08,12 |
| `async with` | 1.6.3 | 06 |
| `async def` / `await` / `asyncio.run` | 1.6.2 | 06 |
| `class` / 继承 / `self` | 1.5.1 / 1.5.2 | 07,08,09,11,13 |
| `ABC` + `@abstractmethod` | 1.5.3 | 07,09,11 |
| `@property` | 1.5.4 | 09 |
| `@staticmethod` | 1.5.5 | 13 |
| `@dataclass` + `field(default_factory=...)` | 1.5.7 | 01,07,08,09,10,11,13 |
| `@tool` / `@mcp.tool()`（装饰器应用） | 1.5.9 | 02,03,06,12 |
| `@contextmanager` + `yield` | 1.5.9（装饰器原理） | 08 |
| `os.getenv` / `os.getcwd` | 1.7.3 | 00,01,02,03,04,12,13 |
| `sys.argv` / `__file__` / `sys.executable` | 1.7.3 | 06 |
| `json.loads/dumps` | 1.7.3 | 01,08 |
| `datetime.now().isoformat()` | 1.7.3 | 08 |
| `hasattr` | 1.7.3 | 03 |
| `operator.add` / `itemgetter` | 1.4.5 / 标准库 | 03,04,13 |

> 结论：示例中出现的所有 Python 语法点，本文均有对应章节讲解，并在 `python_basics_demo.py` 中可运行验证。

---

## 附录 B · Java ↔ Python 速查表

| Java | Python |
| --- | --- |
| `public static void main` | `if __name__ == "__main__":` |
| `String s = "x";` | `s: str = "x"` |
| `List<String>` | `list[str]` |
| `Map<String,Integer>` | `dict[str, int]` |
| `null` | `None`（用 `is None` 判断） |
| `obj instanceof X` | `isinstance(obj, X)` / `hasattr(obj, "x")` |
| `for (int i=0; i<n; i++)` | `for i in range(n):` |
| 增强 for `for (T x : list)` | `for x in list:` |
| Stream `.map().filter().collect()` | 列表推导 `[f(x) for x in list if cond]` |
| `try/catch/throw` | `try/except/raise` |
| `try (var r = ...) {}` | `with ... as r:` |
| `abstract class` | `class X(ABC)` + `@abstractmethod` |
| `interface` | `Protocol`（结构化）或 `ABC` |
| Lombok `@Data` | `@dataclass` |
| getter | `@property` |
| 函数式接口 `Function<T,R>` | `Callable[[T], R]` + `lambda` |
| `implements` / `extends` | 继承即可；Protocol 不用声明 |
| Maven `pom.xml` | `requirements.txt` / `pyproject.toml` |
| `mvn install` | `pip install` / `uv pip install` |
| `jshell` | `python`（REPL） |
| `JUnit` | `pytest` |
| Spring Boot | FastAPI |
| Checkstyle/Spotless | `ruff` |
| `javac` 类型检查 | `mypy` |

---

## 本章小结：三个核心问题回顾

1. **Python 和 Java 最反直觉的差别？** → 缩进即语法、动态类型+可选提示、鸭子类型、靠约定而非 `private`（1.1 节）。
2. **示例里那些怪语法各自干啥？** → `@dataclass`/`TypedDict`/`f-string`/`async`/`**解包`/`lambda` 等，1.2–1.8 节逐条讲，附录 A 可自查全覆盖。
3. **下一步对接哪条技术栈？** → 写接口用 FastAPI，拼 LLM 用 LangChain/LangGraph，做检索用 LlamaIndex+向量库，接系统用 MCP，协作用 A2A（1.9 节 + 主教程 13 章）。

学完本章，回到 `examples/` 下任意示例，应该能顺畅读下去。卡住就回查附录 A/B。
