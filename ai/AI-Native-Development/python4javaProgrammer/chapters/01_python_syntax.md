# 第一章：Python语法精要

> 面向有3年以上Java经验的程序员，快速掌握Python核心语法特性。
> Java类比仅用于辅助理解，不展开。

---

## 1.1 动态类型与类型系统

Python是动态类型语言，变量在运行时绑定类型，无需声明类型。

```python
# python4javaProgrammer/syntax/dynamic_typing.py

# Python: 变量随时可重新绑定不同类型
x = 42          # int
x = "hello"     # 现在 x 是 str
x = [1, 2, 3]   # 现在 x 是 list

# Java类比: Object类型可以实现类似效果，但Python这是原生行为

# type() 查看类型
print(type(x))  # <class 'list'>

# isinstance() 检查类型
print(isinstance(x, list))  # True

# 鸭子类型 (Duck Typing) —— Python的核心哲学
class Duck:
    def quack(self):
        return "Quack!"

class Person:
    def quack(self):
        return "I'm pretending to be a duck!"

def make_sound(thing):
    # 不检查类型，只检查行为
    return thing.quack()

print(make_sound(Duck()))    # Quack!
print(make_sound(Person()))  # I'm pretending to be a duck!
```

### 1.1.1 类型提示 (Type Hints)

Python 3.5+ 支持类型提示，运行时不强制，但 IDE 和 mypy/ruff 可做静态检查。

```python
# python4javaProgrammer/syntax/type_hints.py
from typing import Optional, Union, List, Dict, Callable
from pydantic import BaseModel

# 基本类型提示
def greet(name: str, times: int = 1) -> str:
    return (f"Hello, {name}! " * times).strip()

# 容器类型提示
def process_items(items: List[str]) -> Dict[str, int]:
    return {item: len(item) for item in items}

# Optional 表示可能为 None
def find_user(user_id: int) -> Optional[dict]:
    users = {1: {"name": "Alice"}, 2: {"name": "Bob"}}
    return users.get(user_id)

# Union 表示多种可能类型
def parse_value(value: Union[int, str]) -> float:
    return float(value)

# Python 3.10+ 可用 | 语法
def parse_value_new(value: int | str) -> float:
    return float(value)

# Callable 类型提示
def apply_func(func: Callable[[int, int], int], a: int, b: int) -> int:
    return func(a, b)

# Pydantic 模型做运行时类型校验（后续FastAPI章节详讲）
class User(BaseModel):
    id: int
    name: str
    email: str | None = None  # 可选字段，默认None

user = User(id=1, name="Alice", email="alice@example.com")
print(user.model_dump())  # {'id': 1, 'name': 'Alice', 'email': 'alice@example.com'}

if __name__ == "__main__":
    print(greet("World", 3))
    print(process_items(["apple", "banana"]))
    print(find_user(1))
    print(parse_value("3.14"))
    print(apply_func(lambda a, b: a + b, 3, 5))
```

> **Java类比**：类型提示类似Java的泛型参数，但Python只在静态分析层检查，运行时不强制。

---

## 1.2 推导式 (Comprehensions)

推导式是Python最优雅的特性之一，用简洁语法创建集合。

```python
# python4javaProgrammer/syntax/comprehensions.py

# 列表推导式
numbers = range(1, 11)
squares = [x ** 2 for x in numbers]
print(squares)  # [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]

# 带条件的列表推导式
even_squares = [x ** 2 for x in numbers if x % 2 == 0]
print(even_squares)  # [4, 16, 36, 64, 100]

# 字典推导式
word_lengths = {word: len(word) for word in ["hello", "world", "python"]}
print(word_lengths)  # {'hello': 5, 'world': 5, 'python': 6}

# 集合推导式
unique_lengths = {len(word) for word in ["hello", "world", "python"]}
print(unique_lengths)  # {5, 6}

# 嵌套推导式 —— 矩阵转置
matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
transposed = [[row[i] for row in matrix] for i in range(len(matrix[0]))]
print(transposed)  # [[1, 4, 7], [2, 5, 8], [3, 6, 9]]

# 生成器表达式 —— 惰性求值，节省内存
total = sum(x ** 2 for x in range(1, 1_000_001))
print(f"Sum of squares 1..1000000 = {total}")

# Java类比: 推导式 ≈ Stream.collect(Collectors.toList())，但语法更简洁
```

---

## 1.3 装饰器 (Decorators)

装饰器是Python的高级特性，用于在不修改函数的前提下增强函数行为。

```python
# python4javaProgrammer/syntax/decorators.py
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

## 1.4 上下文管理器 (Context Managers)

上下文管理器用于资源管理（文件、数据库连接、锁等），确保资源正确释放。

```python
# python4javaProgrammer/syntax/context_manager.py
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

## 1.5 生成器 (Generators)

生成器是惰性计算的利器，用 `yield` 逐步产出值，内存占用极低。

```python
# python4javaProgrammer/syntax/generators.py
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

## 1.6 异步编程 async/await

Python的async/await用于IO密集型任务（网络请求、数据库查询等），单线程并发。

```python
# python4javaProgrammer/syntax/async_await.py
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

## 1.7 函数式编程特性

```python
# python4javaProgrammer/syntax/functional.py
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

## 1.8 数据类与模式匹配

```python
# python4javaProgrammer/syntax/dataclass_match.py
from dataclasses import dataclass, field
from enum import Enum
from typing import Any

# --- dataclass: 自动生成 __init__ / __repr__ / __eq__ ---
@dataclass
class Product:
    id: int
    name: str
    price: float
    tags: list[str] = field(default_factory=list)
    in_stock: bool = True

    def apply_discount(self, rate: float) -> float:
        return self.price * (1 - rate)

p1 = Product(id=1, name="Python Book", price=59.9, tags=["programming", "python"])
p2 = Product(id=1, name="Python Book", price=59.9, tags=["programming", "python"])
print(p1)            # Product(id=1, name='Python Book', price=59.9, ...)
print(p1 == p2)      # True (自动生成 __eq__)
print(p1.apply_discount(0.2))  # 47.92

# --- frozen dataclass: 不可变 ---
@dataclass(frozen=True)
class Point:
    x: float
    y: float

    def distance_to(self, other: "Point") -> float:
        return ((self.x - other.x) ** 2 + (self.y - other.y) ** 2) ** 0.5

origin = Point(0, 0)
p = Point(3, 4)
print(origin.distance_to(p))  # 5.0

# --- Python 3.10+ 结构化模式匹配 (match/case) ---
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

# Java类比: dataclass ≈ Java Record（JDK 14+），match/case ≈ switch表达式 + 模式匹配
```

---

## 本章小结

| 特性 | Python | Java类比 | 核心差异 |
|------|--------|---------|---------|
| 动态类型 | 运行时绑定 | 编译时声明 | Python更灵活但需自律 |
| 推导式 | `[x for x in ...]` | Stream API | Python语法更简洁 |
| 装饰器 | `@decorator` | `@Around` (AOP) | Python用函数实现 |
| 上下文管理器 | `with` | try-with-resources | 概念类似 |
| 生成器 | `yield` | `Stream.generate()` | Python更原生 |
| async/await | 单线程事件循环 | CompletableFuture | 并发模型不同 |
| 类型提示 | 可选，运行时不强制 | 强制 | 渐进式类型系统 |
| dataclass | `@dataclass` | `record` | 自动生成样板代码 |
| 模式匹配 | `match/case` | `switch` (JDK 21+) | Python支持结构解构 |

下一章我们将搭建完整的Python工程化开发环境。
