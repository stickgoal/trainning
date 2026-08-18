# Python 入门教程（面向 Java 程序员）

> 写给能读懂 Java、但还没正经写过 Python 的工程师。
> 目标：让你能无障碍阅读并改写本仓库 `examples/` 下那 15 个 AI 示例，并具备独立编写 Python 脚本的能力。

---

## 导读：这份教程解决什么问题

这不是另一本"从 print 开始"的 Python 书，也不是机器学习课。

它是**桥梁**：在你（Java 后端）和本课程的 AI 示例之间架一座语法桥。你大概率已经能猜出 Python 代码大概在干什么，但会被一些"长得不像 Java"的写法卡住——`@dataclass`、`f-string`、`async/await`、`list[dict]`、`if __name__ == "__main__"`、`**args`……

本教程按"**先换脑子 → 补语法 → 读真实代码 → 看生态地图**"的顺序组织。每章都是学员可直接阅读的完整正文，配可运行代码和 Java 类比。

### 配套文件

- `python-primer/python_basics_demo.py`：**纯标准库、零依赖、可直接跑**的语法演示，覆盖本文全部语法点。
  ```bash
  python python-primer/python_basics_demo.py
  ```
- 本文末尾的 **附录 A · 语法点覆盖对照表**：逐条列出"某个语法点 → 在本文哪章讲 → 出现在哪个 `examples/` 文件"，用于自查是否全覆盖。

### 三个贯穿全教程的心智问题

1. **Python 和 Java 最反直觉的差别是什么？** → 第 1 章
2. **示例里那些怪语法各自是干什么的？** → 第 2–7 章 + 第 8 章串讲
3. **学完语法，下一步该对接哪条 Python 技术栈？** → 第 9 章（主流技术科普）

---

## 第 0 章 · 环境与工具链搭建

> 对应需求：包含解释器安装、包管理工具及具体操作步骤。
> 对 Java 工程师：Python 的"环境"≈ JDK + Maven/Gradle + 本地仓库，但更轻、更散。

### 0.1 为什么先讲环境

Java 工程师习惯了：装 JDK → 配 `JAVA_HOME` → 用 Maven 管依赖。Python 不完全一样，最大的坑是**一台机器可能有多个 Python**，且项目依赖默认是全局的。所以第一课是：**装对解释器 + 给每个项目一个隔离环境**。

### 0.2 安装 Python 解释器

**版本要求**：课程示例需要 **Python 3.11+**，推荐 **3.12**。

**方式一：官网安装（最直观）**
1. 打开 https://www.python.org/downloads/
2. 下载 3.12 的安装包（Windows 选 `Windows installer (64-bit)`，macOS 选 `macOS 64-bit universal2`）
3. **关键**：Windows 安装时务必勾选 **`Add python.exe to PATH`**，否则命令行找不到 `python`
4. 一路 Next 完成

**方式二：pyenv（多版本共存，推荐老手）**
```bash
# macOS / Linux
brew install pyenv
pyenv install 3.12.0
pyenv global 3.12.0

# Windows 用 pyenv-win
# 详见 https://github.com/pyenv-win/pyenv-win
```
> Java 类比：pyenv ≈ 你机器上同时装 JDK 8/11/17 并用 `JAVA_HOME` 切换。

### 0.3 确认安装 + 进入 REPL

装完后打开终端：
```bash
python --version        # 期望输出 Python 3.12.x
# 若输出是 3.8 之类的老版本，说明 PATH 里另有旧 Python，用 pyenv 或绝对路径解决

python                  # 直接进入 REPL（交互式解释器）
>>> print("hello")
hello
>>> exit()              # 退出
```
> **REPL** 是 Python 的"即时执行窗口"，类比 Java 的 `jshell`（JDK 9+）。写小逻辑先在这里试，很省时间。

### 0.4 虚拟环境（最重要的一步）

Python 依赖默认装到**全局**，多个项目容易互相覆盖。解决办法是**虚拟环境**，把每个项目的依赖隔离在自己的目录里。

```bash
# 进入项目目录
cd AI-Native-Development

# 创建虚拟环境（生成一个 .venv 目录，里面是独立的 Python 副本）
python -m venv .venv

# 激活（激活后，python/pip 都指向这个环境）
# Windows（Git Bash / PowerShell 用对应写法）
.venv/Scripts/activate
# macOS / Linux
source .venv/bin/activate

# 激活后命令行前面会出现 (.venv) 前缀，说明生效了
(.venv) python --version
```
> **Java 类比**：虚拟环境 ≈ 给每个项目单独隔离 classpath / 本地 Maven 仓库，互不污染。
> 激活后所有 `pip install` 只装进当前 `.venv`，不影响系统。

退出虚拟环境：
```bash
deactivate
```

### 0.5 包管理工具：pip 与 requirements.txt

`pip` 是 Python 的"Maven 中央仓库客户端"。

```bash
# 安装单个包
pip install requests

# 安装指定版本
pip install "fastapi>=0.110.0"

# 从依赖清单一键安装（本项目就是这么干的）
pip install -r requirements.txt

# 国内加速（清华镜像，强烈建议）
pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple

# 看装了什么、导出清单
pip list
pip freeze > requirements.txt
```
`requirements.txt` 就是一份"依赖清单"，类比 Maven 的 `pom.xml` 里的 `<dependencies>`。本仓库已有现成的 `requirements.txt`，直接 `pip install -r` 即可。

> 注意：课程示例需要 API Key（DeepSeek）和本地 Embedding 模型，完整环境步骤见仓库根 `README.md` 的"七、环境准备"。本文只负责让你**能写能跑 Python 本身**。

### 0.6 现代包管理器科普（对比 Java 构建工具）

`pip` 够用，但生产项目常换更现代的工具。对应关系：

| 工具 | 类比 Java | 说明 |
|---|---|---|
| `pip` + `requirements.txt` | Maven 基础依赖 | 最通用，零学习成本 |
| `pipenv` | — | pip + 虚拟环境 + 锁文件一体化 |
| **`poetry`** | Gradle（带发布） | 依赖管理 + 打包 + 发布到 PyPI，体验最接近 Gradle |
| **`uv`** | Gradle（极速） | 2024 年爆火的 Rust 写的包管理器，速度是 pip 的 10–100 倍，推荐新项目直接用 |
| `conda` / `micromamba` | — | 不只管 Python，还管 C/C++ 科学计算库（数据/AI 场景常用） |

**新项目快速起步（用 uv，推荐）**：
```bash
uv venv                 # 建虚拟环境
uv pip install fastapi  # 装包（比 pip 快很多）
```

### 0.7 编辑器 / IDE

| 工具 | 说明 |
|---|---|
| **VS Code** + Python 插件 | 免费、轻量、生态最强，推荐 |
| **PyCharm** | JetBrains 出品，类比 IntelliJ IDEA，对 Python 最懂行（社区版免费） |
| Jupyter Notebook / Lab | 交互式笔记本，写数据/AI 代码神器（见第 9 章） |

### 0.8 运行脚本与验证

写个文件 `hello.py`：
```python
def main() -> None:
    print("Hello from Python")

if __name__ == "__main__":
    main()
```
运行：
```bash
python hello.py
```

**验证你环境 OK**：直接跑本教程配套演示：
```bash
python python-primer/python_basics_demo.py
```
看到 "全部语法点演示完毕" 即成功。这个文件不需要任何第三方库。

---

## 第 1 章 · 从 Java 到 Python：先换脑子

> 对应需求：帮助有 Java 基础的读者快速上手。先改心智模型，再学语法，事半功倍。

### 1.1 五个最反直觉的差别

| # | 差别 | Java 习惯 | Python 现实 |
|---|---|---|---|
| 1 | **缩进即语法** | 大括号 `{}` 管作用域 | 缩进（4 个空格）管作用域，少/多一个空格就报错 |
| 2 | **动态类型 + 可选类型提示** | `String s = "x";` 必须声明 | `s = "x"` 也能跑；但建议写 `s: str = "x"` |
| 3 | **一切皆对象** | 基本类型 `int` 不是对象 | 连 `int`、`function` 都是对象，可以当变量传来传去 |
| 4 | **没有访问修饰符** | `private/public/protected` | 靠约定：`_name` 表示"私有"（单下划线），并不强制 |
| 5 | **鸭子类型** | 靠 `implements` 接口 | "长得像就行"（Protocol，见第 5 章），不强制继承 |

**最重要的第一条**：Python 用缩进划分代码块，不要画蛇添足加分号，也不要用大括号。

```python
# Java: if (x > 0) { System.out.println("正"); }
if x > 0:
    print("正")          # 缩进 4 空格，属于 if 块
print("这行不在 if 里")   # 退回原缩进，if 结束
```

### 1.2 第一个程序与模块入口

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

### 1.3 import 体系

Java 的 `import com.foo.Bar;` 对应 Python 的两种写法：

```python
import os                          # 整模块导入，用 os.getenv(...)
from os import getenv             # 只导入某个名字，直接用 getenv(...)
from dataclasses import dataclass, field   # 一次导入多个
import os as operating_system      # 起别名（长模块名常用）
```
> 关键认知：**模块 = 一个 .py 文件；包 = 一个目录**（目录里要有 `__init__.py`，现在常可省略但概念在）。`from langchain_core.tools import tool` 就是"从 langchain_core 包的 tools 模块导入 tool"。

### 1.4 注释与 docstring

```python
# 这是单行注释（类比 Java 的 //）

"""这是文档字符串（docstring），写在文件/函数/类开头，
用三引号。它不是普通注释，会被工具读取（IDE 悬浮提示、help() 都靠它）。
示例里 @tool / @mcp.tool() 就是把 docstring 当"工具说明"发给 LLM。"""
```

---

## 第 2 章 · 变量、类型与基础数据结构

### 2.1 变量与动态类型（强烈建议写类型提示）

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
> Java 类比：`public int add(int a, int b)`。`x: int = 5` ≈ `int x = 5;`。Python 的类型提示**运行时不做强制检查**（除非用 mypy，见第 9 章），但 IDE 和人是受益的。

### 2.2 基础类型

| Python | ≈ Java | 说明 |
|---|---|---|
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

### 2.3 字符串：f-string、原始字符串、多行

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

### 2.4 容器：list / tuple / dict / set

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

### 2.5 类型注解进阶（typing 模块）

示例里频繁出现的几个：

```python
from typing import Optional, Any, Union, Callable, Literal

maybe: Optional[str] = None          # ≈ Java Optional<String>
anything: Any = 123                   # 任意类型（尽量少用）
num: Union[int, str] = "5"           # 多类型（Python 3.10+ 也可写 int | str）
cb: Callable[[int, int], int] = add  # 函数类型（类比函数式接口）
mode: Literal["read", "write"] = "read"  # 只能是这几个字符串之一
```

### 2.6 可变默认参数的坑（为什么示例用 `field(default_factory=list)`）

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
这正是 `@dataclass` 里用 `field(default_factory=list)` 的原因（见第 5 章）。

---

## 第 3 章 · 控制流与集合变换

### 3.1 if / elif / else

```python
n = 7
if n > 10:
    desc = "大"
elif n > 5:        # 注意是 elif，不是 else if
    desc = "中"
else:
    desc = "小"
```

### 3.2 for 循环、range、enumerate、zip

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

### 3.3 推导式（Python 的灵魂语法，类比 Java Stream）

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

### 3.4 异常处理 try / except / raise

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

### 3.5 上下文管理器 with（类比 try-with-resources）

```python
# 离开 with 块自动 close 文件，不会漏关（类比 Java try-with-resources）
with open("data.txt", "w", encoding="utf-8") as f:
    f.write("内容")
# 到这里 f 已自动关闭
```
示例 `04_rag_demo.py` 用 `with open(SAMPLE_DOC_PATH, "w", encoding="utf-8") as f:` 生成模拟文档；`06_mcp_server.py` 用 `async with stdio_client(...) as (read, write):` 是异步版（见第 6 章）。

---

## 第 4 章 · 函数

### 4.1 定义、默认参数、关键字参数

```python
def greet(name: str, greeting: str = "你好") -> str:   # greeting 有默认值
    return f"{greeting}，{name}"

greet("李四")                 # 用默认 greeting
greet("王五", "Hi")           # 位置传参
greet(name="赵六", greeting="Yo")  # 关键字传参（顺序可乱）
```

### 4.2 * 与 ** 解包

```python
def query(city: str, unit: str = "celsius") -> str:
    return f"{city}({unit})"

args = {"city": "北京", "unit": "celsius"}
query(**args)        # **dict 把键值对展开成关键字参数，等价于 query(city="北京", unit="celsius")
```
> 示例 `01_llm_basics.py` 里 `get_weather(**args)` 就是 LLM 返回的参数 dict 直接展开传给工具函数——这是 Agent 调工具的经典写法。
> `*list` 则把列表展开成位置参数（示例 `08_harness_demo.py` 用 `[system, summary, *recent]` 摊开列表）。

### 4.3 lambda 匿名函数

```python
square = lambda x: x * x          # 单表达式，无 return 关键字
square(5)                          # 25
```
示例大量用作 `key`：`sorted(nodes, key=lambda n: n.score)`、`lambda docs: "\n\n".join(d.page_content for d in docs)`。

### 4.4 函数是一等公民

Python 函数可以像普通变量一样传参、返回、存进容器（Java 要用函数式接口/方法引用）：

```python
from typing import Callable

def apply(data: list[int], fn: Callable[[int], int]) -> list[int]:
    return [fn(x) for x in data]

apply([1, 2, 3], lambda x: x + 1)    # [2, 3, 4]
```
这是装饰器（4.5、第 5.9 章）和"把工具函数注册给 LLM"的基础。

### 4.5 类型提示进阶

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

## 第 5 章 · 面向对象与抽象

### 5.1 类、__init__、self

```python
class Animal:
    def __init__(self, name: str) -> None:   # 构造器，self 必须显式写（≈ Java 的 this）
        self.name = name
    def speak(self) -> str:
        return f"{self.name} 叫"

Animal("狗").speak()
```
> `self` 不是关键字但约定俗成，相当于 Java 的 `this`，且**必须**作为第一个参数写。

### 5.2 继承与多态

```python
class Dog(Animal):
    def speak(self) -> str:        # 重写
        return f"{self.name} 汪汪"

def let_it_speak(a: Animal) -> None:   # 多态：接收父类，实际跑子类
    print(a.speak())
```

### 5.3 抽象基类 ABC + @abstractmethod（≈ abstract class）

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

### 5.4 @property（≈ getter）

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

### 5.5 @staticmethod / @classmethod

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

### 5.6 Protocol 与鸭子类型（≈ interface，但结构化）

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

### 5.7 @dataclass（示例代码绝对主力，必掌握）

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

### 5.8 TypedDict（带类型的 dict，≈ record）

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

### 5.9 装饰器基础（@tool / @mcp.tool() 的底层）

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

## 第 6 章 · 异步编程 async / await

### 6.1 心智模型

Java 工程师已经懂并发。对应关系：

| Java | Python |
|---|---|
| `Thread` / 线程池 | 不常用，GIL 下单线程更划算 |
| `CompletableFuture` | `async`/`await`（协程） |
| Spring WebFlux / Reactor | `asyncio` 事件循环 |
| `synchronized` / 锁 | `asyncio` 下多用队列/锁原语 |

核心区别：**Python 的 `async` 是单线程内的"协作式多任务"（协程）**，靠 `await` 主动让出控制权，特别适合 IO 密集（网络请求、读写文件、调 LLM）。它**不是**多线程并行。

### 6.2 async def / await / asyncio.run

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

### 6.3 async with / async for（简述）

```python
# 异步上下文管理器：进入/退出也要 await
async with some_async_resource() as r:
    ...
# 异步迭代
async for item in some_async_iterable():
    ...
```
示例 `06_mcp_server.py` 的 `async with stdio_client(server_params) as (read, write):` 和 `await session.list_tools()` 就是 MCP 客户端连接——因为跨进程通信是 IO，天然异步。

### 6.4 何时用

- **用**：调 LLM API、访问数据库、HTTP 请求、MCP/A2A 通信（全是等网络）。
- **不用**：纯 CPU 计算（如大循环算数），那用同步或多进程更合适。

> 给 Java 工程师的定心丸：你**不必先精通 asyncio 才能读示例**。看到 `async/await` 知道"这是异步 IO、用 `asyncio.run` 驱动"即可；真正要并发调 LLM 时再深学。

---

## 第 7 章 · 模块、包与工程组织

### 7.1 模块 = 文件，包 = 目录

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

### 7.2 绝对导入 vs 相对导入

```python
from mylib.core import Foo            # 绝对导入（推荐，清晰）
from .utils import helper             # 相对导入：. 表示当前包
from ..core import Foo                # .. 表示上一级包
```

### 7.3 常用标准库速览

| 模块 | 用途 | Java 类比 |
|---|---|---|
| `os` | 环境变量、路径、进程 | `System.getenv` / `java.io.File` |
| `sys` | 解释器参数、退出 | — |
| `json` | JSON 序列化 | Jackson / Gson |
| `datetime` | 时间 | `java.time` |
| `pathlib` | 面向对象路径操作 | `java.nio.file.Path` |
| `argparse` | 命令行参数解析 | picocli / JCommander |
| `re` | 正则 | `java.util.regex` |

示例用到的：`os.getenv("DEEPSEEK_API_KEY")`、`sys.argv`、`json.loads/dumps(..., ensure_ascii=False)`、`datetime.now().isoformat()`、`hasattr(msg, "tool_calls")`、`operator.add`。

### 7.4 项目结构建议

- 小脚本：单 `.py` 文件即可。
- 库/服务：用 `src/` 布局 + `pyproject.toml`（poetry 或 uv 管理）。
- 应用：FastAPI 服务见第 9 章。

---

## 第 8 章 · 串起来读一段真实示例

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
逐条对应：① TypedDict（5.8）→ ② Annotated/operator.add（4.5）→ ③ @dataclass（5.7）→ ④ field（5.7）→ ⑤ def+类型提示（2.1/4.1）→ ⑥ 容器取值（2.4）→ ⑦ hasattr（标准库）→ ⑧ 字符串返回（控制流）→ ⑨ f-string（2.3）→ ⑩ 推导式（3.3）。

> 建议练习：打开 `examples/01_llm_basics.py`，只看 `@dataclass ChatSession` 和 `demo_tool_calling` 两段，确认你能说出每一行的语法点。卡住就回看对应章节。

---

## 第 9 章 · 当前主流技术科普

> 对应需求：介绍 Python 生态中主流技术方向，便于读者后续对接主流技术栈。
> 定位：生态地图，不是教学。每条说清"是什么 / 解决什么 / Java 类比 / 代表库 / 何时学"。

Python 生态比 Java 更"碎片化"——没有一家独大的 Spring，而是按领域各分天下。下面按"你会接到哪类需求"来科普。

### 9.1 Web 开发（写后端服务）

| 项 | 内容 |
|---|---|
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

### 9.2 数据科学（分析、清洗、可视化）

| 项 | 内容 |
|---|---|
| 是什么 | 表格数据处理、统计、画图 |
| 代表库 | **NumPy**（多维数组/向量运算）、**Pandas**（DataFrame，≈ Java 没有对标，最接近 Excel/SQL 表）、Matplotlib/Seaborn（画图）、scikit-learn（传统机器学习） |
| Java 类比 | Pandas DataFrame ≈ 一张数据库表 + 类 SQL 操作；NumPy ≈ 高性能数组 |
| 何时学 | 做数据分析、特征工程、报表时 |

### 9.3 深度学习（训练/调用神经网络）

| 项 | 内容 |
|---|---|
| 是什么 | 构建和训练神经网络 |
| 代表库 | **PyTorch**（研究/生产首选，动态图）、TensorFlow（工业部署老牌）、JAX（Google 高性能） |
| Java 类比 | 无直接对标；类比深度学习版的"应用服务器" |
| 何时学 | 你要自己训练模型、做微调（LoRA/PEFT）时。本课程只"调用"模型，不训练 |

### 9.4 LLM 应用框架（本课程核心）

| 项 | 内容 |
|---|---|
| 是什么 | 把 LLM、检索、记忆、工具拼成应用 |
| 代表库 | **LangChain**（组件化编排）、**LangGraph**（有状态/循环的 Agent 工作流）、**LlamaIndex**（RAG 专用） |
| Java 类比 | 编排层 ≈ Spring Integration / Camunda；RAG ≈ 带向量的 Elasticsearch 管道 |
| 何时学 | 立即。本课程 13 章全在这层 |

### 9.5 Agent 协议与多 Agent 协作

| 项 | 内容 |
|---|---|
| 是什么 | 标准化"Agent ↔ 工具"、"Agent ↔ Agent"的通信 |
| 代表库/规范 | **MCP**（Model Context Protocol，工具协议，≈ gRPC）、**A2A**（Agent To Agent，多 Agent 协作） |
| Java 类比 | MCP ≈ RPC/gRPC；A2A ≈ 微服务间通信（多 Agent 是"微服务化在 Agent 层的重演"） |
| 何时学 | 你要让 Agent 接公司内部系统、或多 Agent 分工时（第 7、8 章） |

### 9.6 向量数据库与 RAG 工具链

| 项 | 内容 |
|---|---|
| 是什么 | 存向量、做语义检索，是 RAG 的"记忆底座" |
| 代表库 | **Chroma**（轻量本地）、**Milvus**（大规模）、**FAISS**（Meta 向量引擎）、**pgvector**（PostgreSQL 插件）、Qdrant |
| Java 类比 | 带向量索引的 Elasticsearch |
| 配套 | sentence-transformers（本地 Embedding）、Reranker（交叉编码器精排） |
| 何时学 | 做知识库问答、企业文档检索时（第 5、6 章） |

### 9.7 异步与并发

| 项 | 内容 |
|---|---|
| 是什么 | IO 密集型并发（调 LLM/DB/HTTP） |
| 代表库 | **asyncio**（标准库事件循环）、httpx（异步 HTTP 客户端，≈ 异步版 OkHttp） |
| Java 类比 | WebFlux / Reactor / CompletableFuture |
| 何时学 | 并发调多个 LLM 或工具、写高吞吐服务时（第 6 章） |

### 9.8 类型与工程化（写出靠谱的 Python）

| 项 | 内容 |
|---|---|
| 是什么 | 静态检查、数据校验、代码格式化 |
| 代表库 | **mypy**（类型检查，≈ 编译期类型校验）、**ruff**（超快 lint+格式化，≈ Checkstyle+Spotless）、**pydantic**（运行时校验） |
| Java 类比 | mypy ≈ `javac` 类型检查；ruff ≈ Spotless/Checkstyle；pydantic ≈ Bean Validation |
| 何时学 | 项目上了规模、要多人协作时 |

### 9.9 测试

| 项 | 内容 |
|---|---|
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

### 9.10 打包与部署

| 项 | 内容 |
|---|---|
| 是什么 | 把 Python 项目打包、容器化、发到生产 |
| 代表工具 | **uv / poetry**（依赖与打包）、**Docker**（容器化，Python 官方有 slim 镜像）、**conda**（科学计算环境） |
| Java 类比 | poetry/uv ≈ Gradle；Docker 用法完全一致 |
| 何时学 | 要上线时。Python 服务进 Docker 和 Java 没区别 |

### 9.11 大数据 / 分布式

| 项 | 内容 |
|---|---|
| 是什么 | 海量数据批处理 |
| 代表库 | **PySpark**（Spark 的 Python API）、Dask（轻量并行） |
| Java 类比 | PySpark ≈ 用 Python 写 Spark（底层还是 JVM） |
| 何时学 | 数据规模超出单机 Pandas 时 |

### 9.12 科学计算 / Notebook

| 项 | 内容 |
|---|---|
| 是什么 | 交互式、可逐步执行的文档 |
| 代表工具 | **Jupyter Notebook / JupyterLab**、JupyterLab |
| Java 类比 | 无直接对标；≈ 带 Markdown 的可执行实验笔记 |
| 何时学 | 做探索性实验、写技术报告、教学演示时（AI 研究几乎人人用） |

### 9.13 一张选型速查表

| 你的需求 | 首选技术 | 类比 |
|---|---|---|
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

## 附录 A · 语法点覆盖对照表

下表逐条核对：每个语法点 → 在本文哪章讲 → 出现在哪些 `examples/` 示例文件。用于自查"示例里出现的语法点本文是否都覆盖"。

| 语法点 | 本文章节 | 出现于示例（代表性） |
|---|---|---|
| `if __name__ == "__main__"` | 1.2 | 全部 15 个 |
| `import` / `from ... import` | 1.3 | 全部 |
| 函数定义 `def` + 类型提示 `->` | 2.1 / 4.1 | 全部 |
| `f-string`（含条件表达式） | 2.3 | 00,01,02,03,04,06,07,08… |
| 原始字符串 `r"..."` | 2.3 | 09 |
| 多行字符串 `"""..."""` | 1.4 | 全部 |
| 基础类型 / `None is None` | 2.2 | 全部 |
| `list` / `dict` / `tuple` / `set`（含元组解包/比较） | 2.4 | 00,03,13（全部均用容器） |
| `list[str]` / `list[dict]` / `dict[str,X]` | 2.4 / 2.5 | 07,08,09,11,13 |
| `Optional` / `Any` / `Callable` | 2.5 / 4.5 | 08,09 |
| `Literal[...]` | 4.5 | 03,13 |
| `Annotated[..., operator.add]` | 4.5 | 03,13 |
| `TypedDict` | 5.8 | 03,13 |
| `Protocol`（鸭子类型） | 5.6 | 11 |
| `if/elif/else` | 3.1 | 00,01,03,04,08… |
| `for` / `range` / `enumerate` / `zip` | 3.2 | 04,04b,05,07,08 |
| 推导式（列表/字典/生成器） | 3.3 | 04b,05,08 |
| `* 解包` / `** 解包` | 4.2 | 01,08 |
| `lambda` | 4.3 | 04,04b,05,08,12 |
| `try/except/raise` | 3.4 | 01,08 |
| `with open(...) as f` | 3.5 | 04,04b,08,12 |
| `async with` | 6.3 | 06 |
| `async def` / `await` / `asyncio.run` | 6.2 | 06 |
| `class` / 继承 / `self` | 5.1 / 5.2 | 07,08,09,11,13 |
| `ABC` + `@abstractmethod` | 5.3 | 07,09,11 |
| `@property` | 5.4 | 09 |
| `@staticmethod` | 5.5 | 13 |
| `@dataclass` + `field(default_factory=...)` | 5.7 | 01,07,08,09,10,11,13 |
| `@tool` / `@mcp.tool()`（装饰器应用） | 5.9 | 02,03,06,12 |
| `@contextmanager` + `yield` | 5.9（装饰器原理） | 08 |
| `os.getenv` / `os.getcwd` | 7.3 | 00,01,02,03,04,12,13 |
| `sys.argv` / `__file__` / `sys.executable` | 7.3 | 06 |
| `json.loads/dumps` | 7.3 | 01,08 |
| `datetime.now().isoformat()` | 7.3 | 08 |
| `hasattr` | 7.3 | 03 |
| `operator.add` / `itemgetter` | 4.5 / 标准库 | 03,04,13 |

> 结论：示例中出现的所有 Python 语法点，本文均有对应章节讲解，并在 `python_basics_demo.py` 中可运行验证。

---

## 附录 B · Java ↔ Python 速查表

| Java | Python |
|---|---|
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

## 收尾：三个核心问题回顾

1. **Python 和 Java 最反直觉的差别？** → 缩进即语法、动态类型+可选提示、鸭子类型、靠约定而非 `private`（第 1 章）。
2. **示例里那些怪语法各自干啥？** → `@dataclass`/`TypedDict`/`f-string`/`async`/`**解包`/`lambda` 等，第 2–8 章逐条讲，附录 A 可自查全覆盖。
3. **下一步对接哪条技术栈？** → 写接口用 FastAPI，拼 LLM 用 LangChain/LangGraph，做检索用 LlamaIndex+向量库，接系统用 MCP，协作用 A2A（第 9 章 + 本课程 13 章）。

学完本文，回去重读 `examples/` 下任意示例，应该能顺畅读下去。卡住就回查附录 A/B。
