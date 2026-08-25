"""
============================================================
python_basics_demo.py — 面向 Java 工程师的 Python 语法演示
============================================================

本文件覆盖《Python + AI 开发实战教程》第一章（Python 语法）里讲到的几乎所有语法点，
并且 **纯标准库、零第三方依赖**，你装好 Python 后直接就能跑：

    python python_basics_demo.py

每一段都对应教程里的一章。看完本文件，你应该能无障碍阅读
examples/ 下那 15 个 AI 示例里的 Python 代码。

【Python 特殊语法提示】（逐条对应教程）
 - 模块 docstring：文件顶部的三引号字符串，是文档也是注释
 - from X import Y / import X：导入（类比 Java import）
 - def / -> 类型提示：函数定义与返回类型
 - if __name__ == "__main__"：模块入口（类比 public static void main）
 - f"..."：f-string 字符串插值（类比 String.format）
 - r"..."：原始字符串，反斜杠不转义（写正则/SQL 常用）
 - list / dict / tuple / set：四种内置容器
 - list[str] / dict[str, X]：PEP 585 泛型容器写法（Java List<String>/Map）
 - Optional / Any / Callable / Literal / Annotated / TypedDict / Protocol：typing 模块
 - if / for / enumerate / zip / 推导式：控制流与集合变换
 - try / except / raise：异常处理（类比 try/catch/throw）
 - with ... as ...：上下文管理器（类比 try-with-resources）
 - * 解包 / ** 解包：实参展开
 - lambda：匿名函数
 - class / ABC / @abstractmethod / @property / @staticmethod：面向对象
 - @dataclass：自动生成构造器与常用方法（类比 Lombok @Data）
 - @decorator：装饰器（高阶函数）
 - async / await / asyncio：异步编程
"""

import os
import sys
import json
import asyncio
from dataclasses import dataclass, field
from abc import ABC, abstractmethod
from contextlib import contextmanager, asynccontextmanager
from datetime import datetime
from typing import Optional, Any, Callable, Literal, TypedDict, Protocol, Annotated
import operator


# =================================================================
# 1. 模块入口 + 基础输出
# =================================================================
def demo_entry_and_print() -> None:
    """演示模块入口、print、字符串乘法、f-string。"""
    # 字符串可以乘以整数重复（Java 没有这种语法糖）
    print("=" * 50)
    print("1. 入口 / 输出 / 字符串")

    name = "张三"
    age = 30
    # f-string：把表达式塞进 {}，类比 Java 的 String.format("%s 今年 %d 岁", name, age)
    print(f"   {name} 今年 {age} 岁")

    # f-string 里还能写条件表达式（Java 的三元运算符 ? : ）
    ok = True
    print(f"   状态: {'成功' if ok else '失败'}")

    # 原始字符串 r"..."：反斜杠不当转义，写正则/Windows 路径/SQL 很有用
    pattern = r"\d{3}-\d{4}"
    print(f"   原始字符串（正则）: {pattern}")

    # 多行字符串用三引号，常用来写长文本或 prompt
    multi = """
    这是一段
    多行文本
    """
    print(f"   多行字符串长度: {len(multi)}")


# =================================================================
# 2. 变量、类型提示与基础类型
# =================================================================
def demo_variables_and_types() -> None:
    """演示动态类型 + 可选类型提示、None、bool。"""
    print("=" * 50)
    print("2. 变量 / 类型提示 / 基础类型")

    # Python 是动态类型：不用声明类型也能跑
    x = 10          # int
    x = "hello"     # 同一变量可以换成 str（Java 不允许）
    print(f"   动态类型: x 现在是字符串 {x!r}")

    # 但强烈建议写类型提示（Java 工程师会很舒服）：变量: 类型 = 值
    count: int = 5
    price: float = 9.9
    label: str = "商品"
    active: bool = True
    nothing: None = None   # None 类似 Java 的 null，但它是单例（用 is 比较）
    print(f"   类型提示: count={count}, price={price}, active={active}, nothing={nothing}")

    # None 用 is / is not 判断，不要用 ==（它是单例）
    if nothing is None:
        print("   nothing 是 None（单例，用 is 判断）")

    # 元组解包：一次给多个变量赋值（Java 没有）
    major, minor = (3, 12)
    print(f"   Python 版本号: {major}.{minor}")
    # 元组可比较：按元素逐个比
    if (major, minor) >= (3, 10):
        print("   满足 3.10+ 要求")


# =================================================================
# 3. 容器：list / tuple / dict / set
# =================================================================
def demo_containers() -> None:
    """演示四种容器及其泛型写法，对照 Java。"""
    print("=" * 50)
    print("3. 容器 list / tuple / dict / set")

    # list ≈ Java List/ArrayList；可放任意类型
    items: list = [1, "二", 3.0]
    items.append("四")              # 可变，类比 List.add
    print(f"   list: {items}, 长度 {len(items)}")

    # 泛型容器写法（Python 3.9+，PEP 585）：list[str] ≈ Java List<String>
    names: list[str] = ["Alice", "Bob"]
    # list[dict] ≈ Java List<Map<String,String>>
    messages: list[dict] = [{"role": "user", "content": "你好"}]
    print(f"   list[str]: {names}")
    print(f"   list[dict]: {messages}")

    # tuple ≈ 不可变数组（也可当 Java 的 record 用）
    point: tuple = (10, 20)
    print(f"   tuple（不可变）: {point}")

    # dict ≈ Java Map；dict[str, int] ≈ Map<String,Integer>
    scores: dict[str, int] = {"Alice": 90, "Bob": 80}
    scores["Cindy"] = 85                     # 类比 map.put
    print(f"   dict: {scores}")
    print(f"   dict.get 带默认值: {scores.get('David', 0)}")
    print(f"   dict 取值: {scores['Alice']}")

    # set ≈ Java HashSet，去重且无序
    tags: set = {"java", "python", "java"}   # 重复的 java 被去掉
    print(f"   set（去重）: {tags}")


# =================================================================
# 4. 控制流 + 推导式
# =================================================================
def demo_control_flow() -> None:
    """演示 if/for/range/enumerate/zip + 列表/字典推导式。"""
    print("=" * 50)
    print("4. 控制流 / 推导式")

    # if / elif / else
    n = 7
    if n > 10:
        desc = "大"
    elif n > 5:
        desc = "中"
    else:
        desc = "小"
    print(f"   {n} 属于: {desc}")

    # for + range（range 类似 Java 的 IntStream.range）
    total = 0
    for i in range(1, 4):       # 1,2,3（左闭右开，类似 Java fori）
        total += i
    print(f"   range(1,4) 求和: {total}")

    # enumerate：同时拿到下标和元素（Java 要自己维护索引）
    fruits = ["苹果", "香蕉"]
    for idx, f in enumerate(fruits, 1):   # 从 1 开始编号
        print(f"     第{idx}个水果: {f}")

    # zip：并行遍历多个序列（类似 Java 两个 list 一起迭代）
    keys = ["a", "b"]
    vals = [1, 2]
    for k, v in zip(keys, vals):
        print(f"     zip: {k}={v}")

    # 列表推导式：紧凑地"变换+收集"，类比 Java Stream.map().collect(toList())
    squares: list[int] = [i * i for i in range(5)]
    print(f"   列表推导 [i*i for i in range(5)]: {squares}")

    # 带过滤的推导式（类比 Stream.filter）
    evens: list[int] = [i for i in range(10) if i % 2 == 0]
    print(f"   过滤推导（偶数）: {evens}")

    # 字典推导式
    upper_map: dict[str, str] = {k: k.upper() for k in ["a", "b", "c"]}
    print(f"   字典推导: {upper_map}")

    # sorted + lambda + reverse（类比 Comparator）
    words = ["banana", "apple", "cherry"]
    by_len = sorted(words, key=lambda w: len(w), reverse=True)
    print(f"   sorted(key=lambda): {by_len}")

    # * 解包：把列表摊开（注意与函数参数 ** 解包区分）
    head = [1, 2]
    tail = [3, 4]
    merged = [*head, *tail]
    print(f"   [*a, *b] 解包合并: {merged}")


# =================================================================
# 5. 异常处理
# =================================================================
def demo_exceptions() -> None:
    """演示 try/except/else/finally/raise。"""
    print("=" * 50)
    print("5. 异常处理（try/except ≈ try/catch）")

    def risky_divide(a: int, b: int) -> float:
        if b == 0:
            raise ValueError("除数不能为 0")   # raise ≈ throw
        return a / b

    try:
        print(f"   10 / 2 = {risky_divide(10, 2)}")
        risky_divide(1, 0)
    except ValueError as e:                    # except ... as e ≈ catch (ValueError e)
        print(f"   捕获到异常: {e}")
    else:
        print("   没抛异常才走这里")
    finally:
        print("   无论是否异常都执行（类比 finally）")

    # 自定义异常也能用（这里是内置 PermissionError 演示）
    try:
        raise PermissionError("危险操作需审批")
    except PermissionError as e:
        print(f"   权限异常: {e}")


# =================================================================
# 6. 上下文管理器 with（类比 try-with-resources）
# =================================================================
def demo_context_manager() -> None:
    """演示 with open(...) 以及 @contextmanager 自定义。"""
    print("=" * 50)
    print("6. 上下文管理器 with（自动关资源）")

    path = "python_primer_tmp.txt"
    # with open(...) as f：离开 with 块自动 close，类比 Java try-with-resources
    with open(path, "w", encoding="utf-8") as f:
        f.write("临时内容")
    with open(path, "r", encoding="utf-8") as f:
        print(f"   读回文件: {f.read()!r}")

    # @contextmanager + yield：把生成器函数变成上下文管理器
    @contextmanager
    def timer(label: str):
        start = datetime.now()
        yield                          # with 块内的代码在这里执行
        print(f"   [{label}] 耗时: {datetime.now() - start}")

    with timer("演示"):
        _ = sum(range(1000))


# =================================================================
# 7. 函数进阶：默认参数 / * / ** / lambda / 一等公民
# =================================================================
def demo_functions() -> None:
    """演示默认参数、** 解包、lambda、函数作为参数。"""
    print("=" * 50)
    print("7. 函数进阶")

    # 默认参数（注意：默认参数只在函数定义时求值一次，别用可变对象做默认值！）
    def greet(name: str, greeting: str = "你好") -> str:
        return f"{greeting}，{name}"
    print(f"   默认参数: {greet('李四')} | {greet('王五', 'Hi')}")

    # ** 解包：把 dict 当成关键字参数传进去（示例里 LLM 工具调用常用）
    def query(city: str, unit: str = "celsius") -> str:
        return f"{city} 天气（{unit}）"
    args = {"city": "北京", "unit": "celsius"}
    print(f"   **dict 解包调用: {query(**args)}")

    # lambda：匿名短函数（类比 Java 的箭头函数 / 单方法匿名类）
    square = lambda x: x * x
    print(f"   lambda: square(5) = {square(5)}")

    # 函数是一等公民：可以当参数传（Callable 类型提示）
    def apply(data: list[int], fn: Callable[[int], int]) -> list[int]:
        return [fn(x) for x in data]
    print(f"   函数作参数: {apply([1, 2, 3], lambda x: x + 1)}")

    # Optional / Any 类型提示
    def maybe(v: Optional[int]) -> Any:
        return v if v is not None else "无"
    print(f"   Optional 返回值: {maybe(None)} / {maybe(3)}")


# =================================================================
# 8. 面向对象：class / ABC / @property / @staticmethod / Protocol
# =================================================================
def demo_oop() -> None:
    """演示类、继承、抽象基类、property、staticmethod、Protocol。"""
    print("=" * 50)
    print("8. 面向对象")

    # 普通类 + __init__ + self
    class Animal:
        def __init__(self, name: str) -> None:
            self.name = name
        def speak(self) -> str:
            return f"{self.name} 叫"
    a = Animal("狗")
    print(f"   类: {a.speak()}")

    # 继承 + 重写
    class Dog(Animal):
        def speak(self) -> str:
            return f"{self.name} 汪汪"
    print(f"   继承: {Dog('旺财').speak()}")

    # 抽象基类 ABC + @abstractmethod（≈ Java abstract class）
    class Shape(ABC):
        @abstractmethod
        def area(self) -> float: ...   # ... 是 Ellipsis，表示"留空实现"
    class Circle(Shape):
        def __init__(self, r: float) -> None:
            self.r = r
        def area(self) -> float:
            return 3.14 * self.r * self.r
    print(f"   抽象基类: 圆面积 = {Circle(2).area()}")

    # @property：把方法当属性访问（≈ Java 的 getter，无需括号）
    class User:
        def __init__(self, first: str, last: str) -> None:
            self.first = first
            self.last = last
        @property
        def full_name(self) -> str:
            return f"{self.first}{self.last}"
    print(f"   @property: {User('张', '三').full_name}")

    # @staticmethod：不依赖实例的方法（≈ Java static 方法）
    class MathUtil:
        @staticmethod
        def add(x: int, y: int) -> int:
            return x + y
    print(f"   @staticmethod: {MathUtil.add(2, 3)}")

    # Protocol：结构化子类型 / 鸭子类型（≈ Java interface，但无需显式 implements）
    class Logger(Protocol):
        def log(self, msg: str) -> None: ...
    class ConsoleLogger:
        def log(self, msg: str) -> None:
            print(f"   [log] {msg}")
    def use_logger(l: Logger) -> None:
        l.log("Protocol 鸭子类型")
    use_logger(ConsoleLogger())


# =================================================================
# 9. @dataclass + TypedDict（示例里大量出现）
# =================================================================
def demo_dataclass_and_typeddict() -> None:
    """演示 dataclass 自动构造器、field(default_factory)、TypedDict。"""
    print("=" * 50)
    print("9. dataclass / TypedDict（示例代码的绝对主力）")

    # @dataclass：自动生成 __init__ / __repr__ / == 等（≈ Lombok @Data）
    @dataclass
    class Point:
        x: int
        y: int
    p = Point(1, 2)
    print(f"   dataclass 自动构造器: {p}")

    # field(default_factory=list)：给"可变默认值"用工厂，避免所有实例共享同一个 list
    @dataclass
    class ChatSession:
        messages: list = field(default_factory=list)   # 每个实例独立的新 list
    s1 = ChatSession()
    s1.messages.append("hi")
    s2 = ChatSession()
    print(f"   default_factory：s1 有 {len(s1.messages)} 条，s2 有 {len(s2.messages)} 条")

    # TypedDict：给 dict 加字段类型约束（≈ Java record / Map 但带结构）
    class AgentMessage(TypedDict):
        sender: str
        receiver: str
        content: str
    msg: AgentMessage = {"sender": "A", "receiver": "B", "content": "hi"}
    print(f"   TypedDict: {msg['content']}")


# =================================================================
# 10. 装饰器基础（@tool / @mcp.tool() / @contextmanager 的底层）
# =================================================================
def demo_decorator() -> None:
    """演示装饰器就是"接收函数、返回函数"的高阶函数。"""
    print("=" * 50)
    print("10. 装饰器 @（高阶函数）")

    # 定义一个装饰器：在原函数前后打印日志
    def log_call(func: Callable[..., Any]) -> Callable[..., Any]:
        def wrapper(*args, **kwargs):               # *args/**kwargs 接收任意参数
            print(f"   [decorator] 调用 {func.__name__}")
            return func(*args, **kwargs)
        return wrapper

    @log_call                  # @log_call 等价于：add = log_call(add)
    def add(a: int, b: int) -> int:
        return a + b
    print(f"   被装饰的函数: add(3,4) = {add(3, 4)}")
    # 你看到的 @tool / @mcp.tool() / @abstractmethod 本质都是这个机制


# =================================================================
# 11. 异步 async / await / asyncio（MCP / 网络调用常用）
# =================================================================
async def demo_async() -> None:
    """演示 async def / await / asyncio.run / async with。"""
    print("=" * 50)
    print("11. 异步 async / await")

    # async def 定义协程；await 等待一个异步操作完成
    async def fetch(name: str) -> str:
        await asyncio.sleep(0.01)        # 模拟网络 IO 等待
        return f"数据来自 {name}"

    # 顺序等两个
    r1 = await fetch("A")
    r2 = await fetch("B")
    print(f"   {r1} | {r2}")

    # async with：异步上下文管理器（真实场景：MCP 的 stdio_client）
    @asynccontextmanager
    async def connect(label: str):
        await asyncio.sleep(0.01)
        yield f"连接[{label}]"
        await asyncio.sleep(0.01)

    async with connect("db") as conn:
        print(f"   {conn} 已就绪")


# =================================================================
# 12. 标准库速览 + 语法点总覆盖检查
# =================================================================
def demo_stdlib() -> None:
    """演示 os / sys / json / datetime / hasattr / itemgetter 风格用法。"""
    print("=" * 50)
    print("12. 常用标准库")

    print(f"   当前目录(os.getcwd): {os.getcwd()}")
    print(f"   环境变量示例(os.getenv): {os.getenv('DEEPSEEK_API_KEY', '未设置')}")
    print(f"   命令行参数(sys.argv): {sys.argv}")
    print(f"   本文件路径(__file__): {__file__}")

    # json：和 Java 的 JSON 互操作最常见
    payload = {"role": "user", "content": "你好"}
    text = json.dumps(payload, ensure_ascii=False)   # ensure_ascii=False 保留中文
    print(f"   json.dumps: {text}")
    back = json.loads(text)
    print(f"   json.loads 回来: {back['content']}")

    # datetime
    print(f"   datetime.now().isoformat: {datetime.now().isoformat()}")

    # hasattr：判断对象有没有某属性（示例里判断 LLM 消息是否有 tool_calls）
    class Msg:
        tool_calls = []
    m = Msg()
    print(f"   hasattr(m, 'tool_calls'): {hasattr(m, 'tool_calls')}")

    # operator.add 作为"归约函数"：示例里 LangGraph 用它做消息累加
    reduced = []
    for chunk in (["a"], ["b"], ["c"]):
        reduced = operator.add(reduced, chunk)   # 列表拼接
    print(f"   operator.add 累加: {reduced}")


# =================================================================
# 入口
# =================================================================
def main() -> None:
    print("\n########## Python 语法演示（面向 Java 工程师）##########\n")
    demo_entry_and_print()
    demo_variables_and_types()
    demo_containers()
    demo_control_flow()
    demo_exceptions()
    demo_context_manager()
    demo_functions()
    demo_oop()
    demo_dataclass_and_typeddict()
    demo_decorator()
    # 异步部分需要 asyncio.run 驱动事件循环
    asyncio.run(demo_async())
    demo_stdlib()
    print("\n########## 全部语法点演示完毕 ##########")
    print("下一步：读 chapters/01_python_syntax.md，把每条和示例对上号。\n")


if __name__ == "__main__":
    main()
