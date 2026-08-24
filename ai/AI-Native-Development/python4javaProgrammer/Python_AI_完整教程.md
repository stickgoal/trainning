# Python + AI 寮€鍙戝疄鎴樻暀绋?
> **闈㈠悜瀵硅薄锛?* 鏈?骞翠互涓奐ava缁忛獙鐨勭▼搴忓憳
> **鏍稿績瀹氫綅锛?* 浠ython鎶€鏈綋绯讳负涓讳綋锛孞ava浠呬綔涓虹被姣斿弬鑰冿紙鍏ㄦ枃鍗犳瘮<5%锛屽彧鍦ㄥ叧閿蹇靛鐢ㄤ竴鍙ヨ瘽鐐瑰嚭锛?> **鏈€缁堜氦浠橈細** 涓€涓畬鏁村彲杩愯鐨凙I鍔╂墜API椤圭洰

---

## 鐩綍

- [绗竴绔狅細Python璇硶绮捐](#绗竴绔爌ython璇硶绮捐)
- [绗簩绔狅細宸ョ▼鍖栫幆澧僝(#绗簩绔犲伐绋嬪寲鐜)
- [绗笁绔狅細椤圭洰缁撴瀯涓庡紑鍙戣鑼僝(#绗笁绔犻」鐩粨鏋勪笌寮€鍙戣鑼?
- [绗洓绔狅細FastAPI Web妗嗘灦](#绗洓绔爁astapi-web妗嗘灦)
- [绗簲绔狅細AI涓夊ぇ妗嗘灦瀹炴垬](#绗簲绔燼i涓夊ぇ妗嗘灦瀹炴垬)
- [绗叚绔狅細瀹屾暣椤圭洰 鈥斺€?AI鍔╂墜API](#绗叚绔犲畬鏁撮」鐩?-ai鍔╂墜api)
- [浜斿垎閽熷揩閫熷惎鍔ㄦ寚鍗梋(#浜斿垎閽熷揩閫熷惎鍔ㄦ寚鍗?
- [闄勫綍锛欽ava绋嬪簭鍛楶ython甯歌鍥版儜FAQ](#闄勫綍java绋嬪簭鍛榩ython甯歌鍥版儜faq)

---


# 绗竴绔狅細Python璇硶绮捐

> 闈㈠悜鏈?骞翠互涓奐ava缁忛獙鐨勭▼搴忓憳锛屽揩閫熸帉鎻ython鏍稿績璇硶鐗规€с€?> Java绫绘瘮浠呯敤浜庤緟鍔╃悊瑙ｏ紝涓嶅睍寮€銆?
---

## 1.1 鍔ㄦ€佺被鍨嬩笌绫诲瀷绯荤粺

Python鏄姩鎬佺被鍨嬭瑷€锛屽彉閲忓湪杩愯鏃剁粦瀹氱被鍨嬶紝鏃犻渶澹版槑绫诲瀷銆?
```python
# python4javaProgrammer/syntax/dynamic_typing.py

# Python: 鍙橀噺闅忔椂鍙噸鏂扮粦瀹氫笉鍚岀被鍨?x = 42          # int
x = "hello"     # 鐜板湪 x 鏄?str
x = [1, 2, 3]   # 鐜板湪 x 鏄?list

# Java绫绘瘮: Object绫诲瀷鍙互瀹炵幇绫讳技鏁堟灉锛屼絾Python杩欐槸鍘熺敓琛屼负

# type() 鏌ョ湅绫诲瀷
print(type(x))  # <class 'list'>

# isinstance() 妫€鏌ョ被鍨?print(isinstance(x, list))  # True

# 楦瓙绫诲瀷 (Duck Typing) 鈥斺€?Python鐨勬牳蹇冨摬瀛?class Duck:
    def quack(self):
        return "Quack!"

class Person:
    def quack(self):
        return "I'm pretending to be a duck!"

def make_sound(thing):
    # 涓嶆鏌ョ被鍨嬶紝鍙鏌ヨ涓?    return thing.quack()

print(make_sound(Duck()))    # Quack!
print(make_sound(Person()))  # I'm pretending to be a duck!
```

### 1.1.1 绫诲瀷鎻愮ず (Type Hints)

Python 3.5+ 鏀寔绫诲瀷鎻愮ず锛岃繍琛屾椂涓嶅己鍒讹紝浣?IDE 鍜?mypy/ruff 鍙仛闈欐€佹鏌ャ€?
```python
# python4javaProgrammer/syntax/type_hints.py
from typing import Optional, Union, List, Dict, Callable
from pydantic import BaseModel

# 鍩烘湰绫诲瀷鎻愮ず
def greet(name: str, times: int = 1) -> str:
    return (f"Hello, {name}! " * times).strip()

# 瀹瑰櫒绫诲瀷鎻愮ず
def process_items(items: List[str]) -> Dict[str, int]:
    return {item: len(item) for item in items}

# Optional 琛ㄧず鍙兘涓?None
def find_user(user_id: int) -> Optional[dict]:
    users = {1: {"name": "Alice"}, 2: {"name": "Bob"}}
    return users.get(user_id)

# Union 琛ㄧず澶氱鍙兘绫诲瀷
def parse_value(value: Union[int, str]) -> float:
    return float(value)

# Python 3.10+ 鍙敤 | 璇硶
def parse_value_new(value: int | str) -> float:
    return float(value)

# Callable 绫诲瀷鎻愮ず
def apply_func(func: Callable[[int, int], int], a: int, b: int) -> int:
    return func(a, b)

# Pydantic 妯″瀷鍋氳繍琛屾椂绫诲瀷鏍￠獙锛堝悗缁璅astAPI绔犺妭璇﹁锛?class User(BaseModel):
    id: int
    name: str
    email: str | None = None  # 鍙€夊瓧娈碉紝榛樿None

user = User(id=1, name="Alice", email="alice@example.com")
print(user.model_dump())  # {'id': 1, 'name': 'Alice', 'email': 'alice@example.com'}

if __name__ == "__main__":
    print(greet("World", 3))
    print(process_items(["apple", "banana"]))
    print(find_user(1))
    print(parse_value("3.14"))
    print(apply_func(lambda a, b: a + b, 3, 5))
```

> **Java绫绘瘮**锛氱被鍨嬫彁绀虹被浼糐ava鐨勬硾鍨嬪弬鏁帮紝浣哖ython鍙湪闈欐€佸垎鏋愬眰妫€鏌ワ紝杩愯鏃朵笉寮哄埗銆?
---

## 1.2 鎺ㄥ寮?(Comprehensions)

鎺ㄥ寮忔槸Python鏈€浼橀泤鐨勭壒鎬т箣涓€锛岀敤绠€娲佽娉曞垱寤洪泦鍚堛€?
```python
# python4javaProgrammer/syntax/comprehensions.py

# 鍒楄〃鎺ㄥ寮?numbers = range(1, 11)
squares = [x ** 2 for x in numbers]
print(squares)  # [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]

# 甯︽潯浠剁殑鍒楄〃鎺ㄥ寮?even_squares = [x ** 2 for x in numbers if x % 2 == 0]
print(even_squares)  # [4, 16, 36, 64, 100]

# 瀛楀吀鎺ㄥ寮?word_lengths = {word: len(word) for word in ["hello", "world", "python"]}
print(word_lengths)  # {'hello': 5, 'world': 5, 'python': 6}

# 闆嗗悎鎺ㄥ寮?unique_lengths = {len(word) for word in ["hello", "world", "python"]}
print(unique_lengths)  # {5, 6}

# 宓屽鎺ㄥ寮?鈥斺€?鐭╅樀杞疆
matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
transposed = [[row[i] for row in matrix] for i in range(len(matrix[0]))]
print(transposed)  # [[1, 4, 7], [2, 5, 8], [3, 6, 9]]

# 鐢熸垚鍣ㄨ〃杈惧紡 鈥斺€?鎯版€ф眰鍊硷紝鑺傜渷鍐呭瓨
total = sum(x ** 2 for x in range(1, 1_000_001))
print(f"Sum of squares 1..1000000 = {total}")

# Java绫绘瘮: 鎺ㄥ寮?鈮?Stream.collect(Collectors.toList())锛屼絾璇硶鏇寸畝娲?```

---

## 1.3 瑁呴グ鍣?(Decorators)

瑁呴グ鍣ㄦ槸Python鐨勯珮绾х壒鎬э紝鐢ㄤ簬鍦ㄤ笉淇敼鍑芥暟鐨勫墠鎻愪笅澧炲己鍑芥暟琛屼负銆?
```python
# python4javaProgrammer/syntax/decorators.py
import time
import functools
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# --- 鍩虹瑁呴グ鍣細璁℃椂 ---
def timer(func):
    """娴嬮噺鍑芥暟鎵ц鏃堕棿"""
    @functools.wraps(func)  # 淇濈暀鍘熷嚱鏁扮殑鍏冧俊鎭?    def wrapper(*args, **kwargs):
        start = time.perf_counter()
        result = func(*args, **kwargs)
        elapsed = time.perf_counter() - start
        logger.info(f"{func.__name__} 鑰楁椂 {elapsed:.4f}s")
        return result
    return wrapper

# --- 甯﹀弬鏁扮殑瑁呴グ鍣?---
def retry(max_attempts=3, delay=1.0):
    """澶辫触鑷姩閲嶈瘯"""
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            for attempt in range(1, max_attempts + 1):
                try:
                    return func(*args, **kwargs)
                except Exception as e:
                    if attempt == max_attempts:
                        raise
                    logger.warning(f"绗瑊attempt}娆″け璐? {e}, {delay}s鍚庨噸璇?..")
                    time.sleep(delay)
        return wrapper
    return decorator

# --- 绫昏楗板櫒 ---
class CountCalls:
    """缁熻鍑芥暟璋冪敤娆℃暟"""
    def __init__(self, func):
        self.func = func
        self.count = 0
        functools.update_wrapper(self, func)

    def __call__(self, *args, **kwargs):
        self.count += 1
        logger.info(f"{self.func.__name__} 宸茶皟鐢?{self.count} 娆?)
        return self.func(*args, **kwargs)

# --- 浣跨敤瑁呴グ鍣?---
@timer
@retry(max_attempts=3, delay=0.5)
@CountCalls
def fetch_data(url: str) -> str:
    import random
    if random.random() < 0.5:
        raise ConnectionError(f"杩炴帴 {url} 澶辫触")
    return f"Data from {url}"

# --- 瑁呴グ鍣ㄥ爢鍙犳墽琛岄『搴?---
# CountCalls(fetch_data) 鈫?retry(...) 鈫?timer(...)
# 璋冪敤鏃? timer 鈫?retry 鈫?CountCalls 鈫?瀹為檯鍑芥暟

if __name__ == "__main__":
    try:
        result = fetch_data("https://api.example.com/data")
        print(result)
    except Exception as e:
        print(f"鏈€缁堝け璐? {e}")

# Java绫绘瘮: 瑁呴グ鍣?鈮?Spring AOP @Around娉ㄨВ锛屼絾Python鐢ㄥ嚱鏁拌€岄潪娉ㄨВ瀹炵幇
```

---

## 1.4 涓婁笅鏂囩鐞嗗櫒 (Context Managers)

涓婁笅鏂囩鐞嗗櫒鐢ㄤ簬璧勬簮绠＄悊锛堟枃浠躲€佹暟鎹簱杩炴帴銆侀攣绛夛級锛岀‘淇濊祫婧愭纭噴鏀俱€?
```python
# python4javaProgrammer/syntax/context_manager.py
import contextlib
import threading

# --- with璇彞锛氳嚜鍔ㄨ祫婧愮鐞?---
# 鏂囦欢鎿嶄綔
with open("example.txt", "w", encoding="utf-8") as f:
    f.write("Hello, Python!")
# 鏂囦欢鑷姩鍏抽棴锛屽嵆浣垮彂鐢熷紓甯?
# --- 鑷畾涔変笂涓嬫枃绠＄悊鍣細绫绘柟寮?---
class DatabaseConnection:
    """妯℃嫙鏁版嵁搴撹繛鎺ョ殑涓婁笅鏂囩鐞嗗櫒"""
    def __init__(self, dsn: str):
        self.dsn = dsn
        self.conn = None

    def __enter__(self):
        print(f"  鈫?杩炴帴鏁版嵁搴? {self.dsn}")
        self.conn = {"dsn": self.dsn, "connected": True}
        return self.conn

    def __exit__(self, exc_type, exc_val, exc_tb):
        print(f"  鈫?鍏抽棴鏁版嵁搴撹繛鎺? {self.dsn}")
        if self.conn:
            self.conn["connected"] = False
        # 杩斿洖False锛堥粯璁わ級琛ㄧず涓嶆姂鍒跺紓甯?        # 杩斿洖True琛ㄧず鎶戝埗寮傚父
        return False

# 浣跨敤
with DatabaseConnection("postgresql://localhost:5432/mydb") as db:
    print(f"  鏌ヨ鏁版嵁: {db}")
    # 鍗充娇杩欓噷鎶涘紓甯革紝__exit__ 涔熶細琚皟鐢?
# --- 鑷畾涔変笂涓嬫枃绠＄悊鍣細contextlib鏂瑰紡锛堟帹鑽愮畝鍗曞満鏅級 ---
@contextlib.contextmanager
def timer_context(label: str):
    """璁℃椂涓婁笅鏂囩鐞嗗櫒"""
    import time
    start = time.perf_counter()
    try:
        yield  # yield涔嬪墠鐨勪唬鐮佺浉褰撲簬__enter__
        # yield涔嬪悗鐨勪唬鐮佺浉褰撲簬__exit__
    finally:
        elapsed = time.perf_counter() - start
        print(f"  鈴?{label} 鑰楁椂 {elapsed:.4f}s")

# 浣跨敤
with timer_context("鏁版嵁澶勭悊"):
    total = sum(x ** 2 for x in range(100_000))
    print(f"  缁撴灉: {total}")

# --- 澶氫笂涓嬫枃绠＄悊鍣?---
# 鍚屾椂绠＄悊澶氫釜璧勬簮
lock = threading.Lock()

@contextlib.contextmanager
def acquire_lock():
    acquired = lock.acquire(timeout=5)
    if not acquired:
        raise TimeoutError("鑾峰彇閿佽秴鏃?)
    try:
        yield
    finally:
        lock.release()

# 宓屽浣跨敤
with DatabaseConnection("mysql://localhost/db") as db, timer_context("浜嬪姟"):
    print(f"  鍦ㄤ簨鍔′腑鎿嶄綔: {db}")

# Java绫绘瘮: with璇彞 鈮?try-with-resources锛宊_enter__/__exit__ 鈮?AutoCloseable.close()
```

---

## 1.5 鐢熸垚鍣?(Generators)

鐢熸垚鍣ㄦ槸鎯版€ц绠楃殑鍒╁櫒锛岀敤 `yield` 閫愭浜у嚭鍊硷紝鍐呭瓨鍗犵敤鏋佷綆銆?
```python
# python4javaProgrammer/syntax/generators.py
import sys
from typing import Generator, Iterator

# --- 鍩虹鐢熸垚鍣?---
def fibonacci(n: int) -> Generator[int, None, None]:
    """鐢熸垚鍓峮涓枑娉㈤偅濂戞暟"""
    a, b = 0, 1
    for _ in range(n):
        yield a
        a, b = b, a + b

# 閫愪釜浜у嚭锛屼笉鍗犵敤澶ч噺鍐呭瓨
for num in fibonacci(10):
    print(num, end=" ")  # 0 1 1 2 3 5 8 13 21 34
print()

# --- 鐢熸垚鍣?vs 鍒楄〃锛氬唴瀛樺姣?---
# 鍒楄〃锛氫竴娆℃€х敓鎴愭墍鏈夊厓绱?big_list = [x ** 2 for x in range(1_000_000)]
print(f"鍒楄〃鍐呭瓨: {sys.getsizeof(big_list) / 1024 / 1024:.1f} MB")  # ~8 MB

# 鐢熸垚鍣細鎯版€ф眰鍊?big_gen = (x ** 2 for x in range(1_000_000))
print(f"鐢熸垚鍣ㄥ唴瀛? {sys.getsizeof(big_gen)} bytes")  # ~200 bytes

# --- 鐢熸垚鍣ㄦ柟娉曪細send / throw / close ---
def accumulator() -> Generator[int, int, None]:
    """鍙帴鏀跺閮ㄥ€肩殑绱姞鍣?""
    total = 0
    while True:
        value = yield total
        if value is None:
            break
        total += value

acc = accumulator()
next(acc)          # 鍚姩鐢熸垚鍣紝杩斿洖0
print(acc.send(10))  # 10
print(acc.send(20))  # 30
print(acc.send(30))  # 60
acc.close()         # 鍏抽棴鐢熸垚鍣?
# --- yield from锛氬鎵樼敓鎴愬櫒 ---
def chain_generators(*iterables) -> Generator:
    """灏嗗涓彲杩唬瀵硅薄涓茶仈"""
    for iterable in iterables:
        yield from iterable  # 绛変环浜?for x in iterable: yield x

result = list(chain_generators([1, 2], [3, 4], [5, 6]))
print(result)  # [1, 2, 3, 4, 5, 6]

# --- 瀹炴垬锛氶€愯璇诲彇澶ф枃浠?---
def read_large_file(filepath: str, encoding: str = "utf-8") -> Generator[str, None, None]:
    """閫愯璇诲彇澶ф枃浠讹紝涓嶄竴娆℃€у姞杞藉埌鍐呭瓨"""
    with open(filepath, encoding=encoding) as f:
        for line in f:
            yield line.rstrip("\n")

# 浣跨敤锛堝嵆浣挎枃浠?0GB涔熶笉浼氱垎鍐呭瓨锛?# for line in read_large_file("huge_data.csv"):
#     process(line)

# --- 鏃犻檺鐢熸垚鍣?---
def natural_numbers(start: int = 1) -> Generator[int, None, None]:
    """鏃犻檺鑷劧鏁板簭鍒?""
    n = start
    while True:
        yield n
        n += 1

# 鐢?itertools.islice 鍙栧墠N涓?from itertools import islice
first_10 = list(islice(natural_numbers(), 10))
print(first_10)  # [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# Java绫绘瘮: 鐢熸垚鍣?鈮?Stream.generate()锛屼絾yield鏈哄埗鏇寸洿瑙傦紝鍐呭瓨鏁堢巼鏇撮珮
```

---

## 1.6 寮傛缂栫▼ async/await

Python鐨刟sync/await鐢ㄤ簬IO瀵嗛泦鍨嬩换鍔★紙缃戠粶璇锋眰銆佹暟鎹簱鏌ヨ绛夛級锛屽崟绾跨▼骞跺彂銆?
```python
# python4javaProgrammer/syntax/async_await.py
import asyncio
import aiohttp  # pip install aiohttp
import time
from typing import List

# --- 鍩虹鍗忕▼ ---
async def hello() -> str:
    """寮傛鍑芥暟锛堝崗绋嬶級"""
    await asyncio.sleep(0.5)  # 闈為樆濉炵瓑寰?    return "Hello, Async!"

# --- 骞跺彂鎵ц澶氫釜鍗忕▼ ---
async def fetch_url(session: aiohttp.ClientSession, url: str) -> str:
    """寮傛HTTP璇锋眰"""
    async with session.get(url) as response:
        return await response.text()

async def fetch_all(urls: List[str]) -> List[str]:
    """骞跺彂鑾峰彇澶氫釜URL"""
    async with aiohttp.ClientSession() as session:
        tasks = [fetch_url(session, url) for url in urls]
        # asyncio.gather: 骞跺彂鎵ц鎵€鏈変换鍔?        results = await asyncio.gather(*tasks)
        return results

# --- asyncio.create_task: 鍚庡彴浠诲姟 ---
async def background_job() -> None:
    """鍚庡彴浠诲姟绀轰緥"""
    for i in range(5):
        print(f"  鍚庡彴浠诲姟杩涘害: {i+1}/5")
        await asyncio.sleep(0.3)

async def main_task() -> None:
    """涓讳换鍔?""
    task = asyncio.create_task(background_job())
    # 鍚屾椂鍋氬叾浠栧伐浣?    await asyncio.sleep(1)
    print("  涓讳换鍔″畬鎴?)
    await task  # 绛夊緟鍚庡彴浠诲姟瀹屾垚

# --- 瓒呮椂鎺у埗 ---
async def slow_operation() -> str:
    await asyncio.sleep(10)
    return "缁堜簬瀹屾垚浜?

async def with_timeout() -> str:
    try:
        # Python 3.11+ 鎺ㄨ崘鐢?asyncio.timeout
        async with asyncio.timeout(2.0):
            return await slow_operation()
    except TimeoutError:
        return "鎿嶄綔瓒呮椂!"

# --- 淇″彿閲忥細闄愬埗骞跺彂鏁?---
async def rate_limited_fetch(urls: List[str], max_concurrent: int = 3) -> List[str]:
    """闄愬埗骞跺彂鏁扮殑鎵归噺璇锋眰"""
    semaphore = asyncio.Semaphore(max_concurrent)

    async def fetch_with_limit(session, url):
        async with semaphore:  # 鑾峰彇淇″彿閲?            return await fetch_url(session, url)

    async with aiohttp.ClientSession() as session:
        tasks = [fetch_with_limit(session, url) for url in urls]
        return await asyncio.gather(*tasks)

# --- asyncio.Queue锛氬崗绋嬮棿閫氫俊 ---
async def producer(queue: asyncio.Queue, items: list) -> None:
    for item in items:
        await asyncio.sleep(0.1)  # 妯℃嫙鐢熶骇鑰楁椂
        await queue.put(item)
        print(f"  馃摛 鐢熶骇: {item}")
    await queue.put(None)  # 鍝ㄥ叺鍊?
async def consumer(queue: asyncio.Queue) -> None:
    while True:
        item = await queue.get()
        if item is None:
            queue.task_done()
            break
        await asyncio.sleep(0.2)  # 妯℃嫙娑堣垂鑰楁椂
        print(f"  馃摜 娑堣垂: {item}")
        queue.task_done()

async def producer_consumer_demo() -> None:
    queue: asyncio.Queue = asyncio.Queue(maxsize=5)
    await asyncio.gather(
        producer(queue, ["A", "B", "C", "D"]),
        consumer(queue)
    )

# --- 杩愯鍏ュ彛 ---
async def main() -> None:
    print("=== 鍩虹鍗忕▼ ===")
    result = await hello()
    print(f"  {result}")

    print("\n=== 鍚庡彴浠诲姟 ===")
    await main_task()

    print("\n=== 瓒呮椂鎺у埗 ===")
    print(f"  {await with_timeout()}")

    print("\n=== 鐢熶骇鑰?娑堣垂鑰?===")
    await producer_consumer_demo()

if __name__ == "__main__":
    # asyncio.run() 鏄帹鑽愮殑鍚姩鏂瑰紡
    asyncio.run(main())

# Java绫绘瘮: async/await 鈮?CompletableFuture锛屼絾Python璇硶鏇寸畝娲?# Python鏄崟绾跨▼鍗忕▼锛堜簨浠跺惊鐜級锛孞ava鐨凜ompletableFuture鍙厤绾跨▼姹?```

---

## 1.7 鍑芥暟寮忕紪绋嬬壒鎬?
```python
# python4javaProgrammer/syntax/functional.py
from functools import reduce, partial, lru_cache
from operator import add

# --- 楂橀樁鍑芥暟 ---
# map / filter / reduce
numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# map: 鏄犲皠
doubled = list(map(lambda x: x * 2, numbers))
print(doubled)  # [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

# filter: 杩囨护
evens = list(filter(lambda x: x % 2 == 0, numbers))
print(evens)  # [2, 4, 6, 8, 10]

# reduce: 褰掔害
total = reduce(add, numbers)
print(total)  # 55

# 鎺ㄥ寮忔洿Pythonic锛堟帹鑽愶級
doubled = [x * 2 for x in numbers]
evens = [x for x in numbers if x % 2 == 0]

# --- partial: 鍋忓嚱鏁?---
def power(base: int, exponent: int) -> int:
    return base ** exponent

square = partial(power, exponent=2)
cube = partial(power, exponent=3)
print(square(5))  # 25
print(cube(3))    # 27

# --- lru_cache: 鑷姩缂撳瓨 ---
@lru_cache(maxsize=128)
def expensive_computation(n: int) -> int:
    """甯︾紦瀛樼殑閫掑綊鏂愭尝閭ｅ"""
    if n < 2:
        return n
    return expensive_computation(n - 1) + expensive_computation(n - 2)

print(expensive_computation(100))  # 鐬棿瀹屾垚锛堟湁缂撳瓨锛?
# --- 瑙ｅ寘涓庡杩斿洖鍊?---
def min_max(numbers: list) -> tuple:
    return min(numbers), max(numbers)

lo, hi = min_max([3, 1, 4, 1, 5, 9, 2, 6])
print(f"min={lo}, max={hi}")

# 鏄熷彿瑙ｅ寘
first, *middle, last = [1, 2, 3, 4, 5]
print(f"first={first}, middle={middle}, last={last}")

# 瀛楀吀瑙ｅ寘
defaults = {"host": "localhost", "port": 5432}
overrides = {"port": 8080, "debug": True}
config = {**defaults, **overrides}
print(config)  # {'host': 'localhost', 'port': 8080, 'debug': True}
```

---

## 1.8 鏁版嵁绫讳笌妯″紡鍖归厤

```python
# python4javaProgrammer/syntax/dataclass_match.py
from dataclasses import dataclass, field
from enum import Enum
from typing import Any

# --- dataclass: 鑷姩鐢熸垚 __init__ / __repr__ / __eq__ ---
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
print(p1 == p2)      # True (鑷姩鐢熸垚 __eq__)
print(p1.apply_discount(0.2))  # 47.92

# --- frozen dataclass: 涓嶅彲鍙?---
@dataclass(frozen=True)
class Point:
    x: float
    y: float

    def distance_to(self, other: "Point") -> float:
        return ((self.x - other.x) ** 2 + (self.y - other.y) ** 2) ** 0.5

origin = Point(0, 0)
p = Point(3, 4)
print(origin.distance_to(p))  # 5.0

# --- Python 3.10+ 缁撴瀯鍖栨ā寮忓尮閰?(match/case) ---
class MessageType(Enum):
    TEXT = "text"
    IMAGE = "image"
    FILE = "file"

def handle_message(msg: dict) -> str:
    match msg:
        case {"type": MessageType.TEXT, "content": text}:
            return f"鏂囨湰娑堟伅: {text}"
        case {"type": MessageType.IMAGE, "url": url, "width": w, "height": h}:
            return f"鍥剧墖: {url} ({w}x{h})"
        case {"type": MessageType.FILE, "filename": name, "size": size} if size > 10_000_000:
            return f"澶ф枃浠? {name} ({size} bytes)"
        case {"type": MessageType.FILE, "filename": name}:
            return f"灏忔枃浠? {name}"
        case _:
            return "鏈煡娑堟伅绫诲瀷"

print(handle_message({"type": MessageType.TEXT, "content": "Hello"}))
print(handle_message({"type": MessageType.FILE, "filename": "data.csv", "size": 500}))
print(handle_message({"type": MessageType.IMAGE, "url": "http://img.jpg", "width": 800, "height": 600}))

# Java绫绘瘮: dataclass 鈮?Java Record锛圝DK 14+锛夛紝match/case 鈮?switch琛ㄨ揪寮?+ 妯″紡鍖归厤
```

---

## 鏈珷灏忕粨

| 鐗规€?| Python | Java绫绘瘮 | 鏍稿績宸紓 |
|------|--------|---------|---------|
| 鍔ㄦ€佺被鍨?| 杩愯鏃剁粦瀹?| 缂栬瘧鏃跺０鏄?| Python鏇寸伒娲讳絾闇€鑷緥 |
| 鎺ㄥ寮?| `[x for x in ...]` | Stream API | Python璇硶鏇寸畝娲?|
| 瑁呴グ鍣?| `@decorator` | `@Around` (AOP) | Python鐢ㄥ嚱鏁板疄鐜?|
| 涓婁笅鏂囩鐞嗗櫒 | `with` | try-with-resources | 姒傚康绫讳技 |
| 鐢熸垚鍣?| `yield` | `Stream.generate()` | Python鏇村師鐢?|
| async/await | 鍗曠嚎绋嬩簨浠跺惊鐜?| CompletableFuture | 骞跺彂妯″瀷涓嶅悓 |
| 绫诲瀷鎻愮ず | 鍙€夛紝杩愯鏃朵笉寮哄埗 | 寮哄埗 | 娓愯繘寮忕被鍨嬬郴缁?|
| dataclass | `@dataclass` | `record` | 鑷姩鐢熸垚鏍锋澘浠ｇ爜 |
| 妯″紡鍖归厤 | `match/case` | `switch` (JDK 21+) | Python鏀寔缁撴瀯瑙ｆ瀯 |

涓嬩竴绔犳垜浠皢鎼缓瀹屾暣鐨凱ython宸ョ▼鍖栧紑鍙戠幆澧冦€?

# 绗簩绔狅細宸ョ▼鍖栫幆澧?
> 浠庨浂鎼缓Python宸ョ▼鍖栧紑鍙戠幆澧冿細pyenv + venv + pip + uv + pyproject.toml

---

## 2.1 Python鐗堟湰绠＄悊锛歱yenv

pyenv 鐢ㄤ簬鍦ㄥ悓涓€鍙版満鍣ㄤ笂绠＄悊澶氫釜Python鐗堟湰銆?
```bash
# 瀹夎pyenv锛圠inux/macOS锛?curl https://pyenv.run | bash

# Windows鎺ㄨ崘鐢?pyenv-win
# PowerShell瀹夎
Invoke-WebRequest -UseBasicParsing -Uri "https://raw.githubusercontent.com/pyenv-win/pyenv-win/master/pyenv-win/install-pyenv-win.ps1" -OutFile "./install-pyenv-win.ps1"; &"./install-pyenv-win.ps1"

# 甯哥敤鍛戒护
pyenv install 3.12.7          # 瀹夎鎸囧畾鐗堟湰
pyenv versions                # 鏌ョ湅宸插畨瑁呯増鏈?pyenv global 3.12.7           # 璁剧疆鍏ㄥ眬鐗堟湰
pyenv local 3.12.7            # 涓哄綋鍓嶉」鐩缃増鏈紙鐢熸垚.python-version鏂囦欢锛?
# Java绫绘瘮: pyenv 鈮?SDKMAN! (绠＄悊JDK鐗堟湰)
```

---

## 2.2 铏氭嫙鐜锛歷env

姣忎釜椤圭洰浣跨敤鐙珛鐨勪緷璧栫幆澧冿紝閬垮厤鍏ㄥ眬姹℃煋銆?
```bash
# 鍒涘缓铏氭嫙鐜
python -m venv .venv

# 婵€娲?# Linux/macOS:
source .venv/bin/activate
# Windows PowerShell:
.venv\Scripts\Activate.ps1

# 閫€鍑?deactivate

# 鏌ョ湅宸插畨瑁呭寘
pip list
pip freeze > requirements.txt  # 瀵煎嚭渚濊禆

# Java绫绘瘮: venv 鈮?Maven灞€閮ㄤ粨搴撻殧绂伙紝浣嗘洿杞婚噺
```

---

## 2.3 鍖呯鐞嗭細pip 涓?uv

### 2.3.1 pip 鍩虹

```bash
pip install fastapi            # 瀹夎鍖?pip install fastapi==0.115.0   # 鎸囧畾鐗堟湰
pip install "fastapi>=0.115.0" # 鐗堟湰鑼冨洿
pip install -r requirements.txt # 鎵归噺瀹夎
pip uninstall fastapi          # 鍗歌浇
pip show fastapi               # 鏌ョ湅鍖呬俊鎭?pip list --outdated            # 鏌ョ湅杩囨椂鐨勫寘
```

### 2.3.2 uv锛氭柊涓€浠ｅ寘绠＄悊鍣紙鎺ㄨ崘锛?
uv 鏄?Rust 缂栧啓鐨勬瀬閫熷寘绠＄悊鍣紝鍏煎 pip 鍛戒护锛岄€熷害蹇?10-100 鍊嶃€?
```bash
# 瀹夎uv
# Linux/macOS:
curl -LsSf https://astral.sh/uv/install.sh | sh
# Windows PowerShell:
powershell -ExecutionPolicy ByPass -c "irm https://astral.sh/uv/install.ps1 | iex"

# 甯哥敤鍛戒护
uv venv                        # 鍒涘缓铏氭嫙鐜锛堥粯璁?.venv锛?uv venv --python 3.12          # 鎸囧畾Python鐗堟湰
uv pip install fastapi         # 瀹夎鍖咃紙鍏煎pip璇硶锛?uv pip install -r requirements.txt
uv pip list
uv pip freeze

# uv 鐙湁鍔熻兘
uv sync                        # 鏍规嵁 pyproject.toml 鍚屾渚濊禆锛堢被浼?mvn install锛?uv add fastapi                 # 娣诲姞渚濊禆鍒?pyproject.toml
uv add --dev pytest ruff       # 娣诲姞寮€鍙戜緷璧?uv remove fastapi              # 绉婚櫎渚濊禆
uv lock                        # 鐢熸垚 lock 鏂囦欢锛坲v.lock锛?uv run python main.py          # 鍦ㄩ」鐩幆澧冧腑杩愯

# Java绫绘瘮: uv 鈮?Maven/Gradle锛屼絾閫熷害鏋佸揩锛宲yproject.toml 鈮?pom.xml/build.gradle
```

---

## 2.4 椤圭洰閰嶇疆锛歱yproject.toml

`pyproject.toml` 鏄?Python 椤圭洰鐨勬爣鍑嗛厤缃枃浠讹紙PEP 518/621锛夛紝鍙栦唬 `setup.py`銆?
```toml
# python4javaProgrammer/pyproject.toml

[project]
name = "ai-native-dev"
version = "0.1.0"
description = "Python+AI寮€鍙戝疄鎴樻暀绋嬮」鐩?
readme = "README.md"
requires-python = ">=3.11"
authors = [
    { name = "Your Name", email = "you@example.com" }
]
license = { text = "MIT" }

# 杩愯鏃朵緷璧?dependencies = [
    # Web妗嗘灦
    "fastapi>=0.115.0",
    "uvicorn[standard]>=0.30.0",
    # HTTP瀹㈡埛绔?    "httpx>=0.27.0",
    # 鏁版嵁鏍￠獙
    "pydantic>=2.0",
    "pydantic-settings>=2.0",
    # AI妗嗘灦
    "langchain>=0.3.0",
    "langchain-openai>=0.2.0",
    "langchain-community>=0.3.0",
    "langgraph>=0.2.0",
    "llama-index>=0.12.0",
    "chromadb>=0.5.0",
    # 宸ュ叿搴?    "python-dotenv>=1.0",
    "loguru>=0.7.0",
]

# 鍙€変緷璧栵紙鎸夊姛鑳藉垎缁勶級
[project.optional-dependencies]
dev = [
    "pytest>=8.0",
    "pytest-asyncio>=0.24",
    "pytest-cov>=5.0",
    "httpx>=0.27.0",  # 娴嬭瘯瀹㈡埛绔?]
lint = [
    "ruff>=0.6.0",
    "mypy>=1.11",
]
docs = [
    "mkdocs-material>=9.0",
]

# 鏋勫缓鍚庣锛堢幇浠ython鎺ㄨ崘鐢╤atchling锛?[build-system]
requires = ["hatchling"]
build-backend = "hatchling.build"

# Ruff閰嶇疆
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
ignore = ["E501"]  # 琛岄暱搴︾敱formatter澶勭悊

[tool.ruff.format]
quote-style = "double"
indent-style = "space"

# pytest閰嶇疆
[tool.pytest.ini_options]
testpaths = ["tests"]
python_files = "test_*.py"
python_functions = "test_*"
asyncio_mode = "auto"
addopts = "-v --tb=short --strict-markers"

# mypy閰嶇疆
[tool.mypy]
python_version = "3.11"
warn_return_any = true
warn_unused_configs = true
disallow_untyped_defs = false  # 娓愯繘寮忕被鍨嬫鏌?ignore_missing_imports = true

# hatchling鏋勫缓閰嶇疆
[tool.hatch.build.targets.wheel]
packages = ["src/ai_native_dev"]
```

### 2.4.1 瀹屾暣椤圭洰鍒濆鍖栨祦绋?
```bash
# 1. 鍒涘缓椤圭洰鐩綍
mkdir ai-native-dev && cd ai-native-dev

# 2. 鐢╱v鍒濆鍖?uv init --python 3.12

# 3. 娣诲姞渚濊禆
uv add fastapi uvicorn[standard] httpx pydantic pydantic-settings
uv add langchain langchain-openai langchain-community langgraph
uv add llama-index chromadb python-dotenv loguru
uv add --dev pytest pytest-asyncio pytest-cov ruff mypy

# 4. 鐢熸垚lock鏂囦欢
uv lock

# 5. 鍚屾瀹夎
uv sync

# Java绫绘瘮: 鏁翠釜娴佺▼ 鈮?mvn archetype:generate + 淇敼pom.xml + mvn install
```

---

## 2.5 鐜鍙橀噺绠＄悊锛?env

```python
# python4javaProgrammer/env_config_example.py

# --- 鏂瑰紡1: python-dotenv锛堢畝鍗曠洿鎺ワ級 ---
# .env 鏂囦欢
"""
OPENAI_API_KEY=sk-xxxx
DATABASE_URL=postgresql://localhost:5432/mydb
DEBUG=true
APP_ENV=development
"""
from dotenv import load_dotenv
import os

load_dotenv()  # 鍔犺浇 .env 鏂囦欢
api_key = os.getenv("OPENAI_API_KEY")
debug = os.getenv("DEBUG", "false").lower() == "true"

# --- 鏂瑰紡2: pydantic-settings锛堟帹鑽愶紝绫诲瀷瀹夊叏锛?---
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    """搴旂敤閰嶇疆锛岃嚜鍔ㄤ粠鐜鍙橀噺/.env鏂囦欢鍔犺浇"""
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
    )

    # 搴旂敤閰嶇疆
    app_name: str = "AI Native Dev"
    debug: bool = False
    app_env: str = "development"

    # OpenAI
    openai_api_key: str
    openai_model: str = "gpt-4o-mini"

    # 鏁版嵁搴?    database_url: str = "postgresql://localhost:5432/mydb"

    # Redis
    redis_url: str = "redis://localhost:6379/0"

    # ChromaDB
    chroma_persist_dir: str = "./data/chroma"

settings = Settings()
print(f"App: {settings.app_name}")
print(f"Model: {settings.openai_model}")
print(f"Debug: {settings.debug}")

# 鏂瑰紡2浼樺娍: 绫诲瀷鏍￠獙 + 榛樿鍊?+ IDE鑷姩琛ュ叏 + 鐜闅旂
# Java绫绘瘮: pydantic-settings 鈮?@ConfigurationProperties (Spring Boot)
```

---

## 2.6 澶氱増鏈琍ython涓嶤I鐜

```yaml
# .github/workflows/ci.yml 绀轰緥锛堜粎渚涘弬鑰冿級
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

## 鏈珷灏忕粨

| 宸ュ叿 | 鐢ㄩ€?| Java绫绘瘮 |
|------|------|---------|
| pyenv | Python鐗堟湰绠＄悊 | SDKMAN! |
| venv | 铏氭嫙鐜 | Maven灞€閮ㄤ粨搴?|
| pip | 鍖呭畨瑁?| 鈥?|
| uv | 鏋侀€熷寘绠＄悊 | Maven/Gradle |
| pyproject.toml | 椤圭洰閰嶇疆 | pom.xml |
| pydantic-settings | 閰嶇疆绠＄悊 | @ConfigurationProperties |
| .env | 鐜鍙橀噺 | application.yml |

涓嬩竴绔犳垜浠皢璁捐鏍囧噯鍖栫殑椤圭洰缁撴瀯涓庡紑鍙戣鑼冦€?

# 绗笁绔狅細椤圭洰缁撴瀯涓庡紑鍙戣鑼?
> 鏍囧噯鍖栫洰褰曠粨鏋?+ 鏃ュ織 + 閰嶇疆绠＄悊 + 娴嬭瘯 + 浠ｇ爜璐ㄩ噺宸ュ叿

---

## 3.1 PEP 8 鏍囧噯鐩綍缁撴瀯

```
ai-native-dev/
鈹溾攢鈹€ pyproject.toml          # 椤圭洰閰嶇疆锛堜緷璧栥€佸伐鍏烽厤缃級
鈹溾攢鈹€ uv.lock                 # 渚濊禆閿佸畾鏂囦欢
鈹溾攢鈹€ .env                    # 鐜鍙橀噺锛堜笉鎻愪氦鍒癵it锛?鈹溾攢鈹€ .env.example            # 鐜鍙橀噺妯℃澘
鈹溾攢鈹€ .gitignore
鈹溾攢鈹€ README.md
鈹溾攢鈹€ Dockerfile
鈹溾攢鈹€ docker-compose.yml
鈹溾攢鈹€ Makefile                # 甯哥敤鍛戒护蹇嵎鏂瑰紡
鈹?鈹溾攢鈹€ src/                    # 婧愪唬鐮佹牴鐩綍
鈹?  鈹斺攢鈹€ ai_native_dev/      # Python鍖?鈹?      鈹溾攢鈹€ __init__.py
鈹?      鈹溾攢鈹€ main.py         # 搴旂敤鍏ュ彛
鈹?      鈹溾攢鈹€ config.py       # 閰嶇疆绠＄悊
鈹?      鈹?鈹?      鈹溾攢鈹€ api/            # API灞傦紙璺敱銆佷腑闂翠欢锛?鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ deps.py     # 渚濊禆娉ㄥ叆
鈹?      鈹?  鈹溾攢鈹€ middleware.py
鈹?      鈹?  鈹斺攢鈹€ routes/
鈹?      鈹?      鈹溾攢鈹€ __init__.py
鈹?      鈹?      鈹溾攢鈹€ chat.py
鈹?      鈹?      鈹溾攢鈹€ documents.py
鈹?      鈹?      鈹斺攢鈹€ health.py
鈹?      鈹?鈹?      鈹溾攢鈹€ core/           # 鏍稿績鍩虹璁炬柦
鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ exceptions.py
鈹?      鈹?  鈹溾攢鈹€ logging.py
鈹?      鈹?  鈹斺攢鈹€ settings.py
鈹?      鈹?鈹?      鈹溾攢鈹€ models/         # 鏁版嵁妯″瀷锛圥ydantic / SQLAlchemy锛?鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ base.py
鈹?      鈹?  鈹溾攢鈹€ chat.py
鈹?      鈹?  鈹斺攢鈹€ document.py
鈹?      鈹?鈹?      鈹溾攢鈹€ services/       # 涓氬姟閫昏緫灞?鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ rag_service.py
鈹?      鈹?  鈹溾攢鈹€ agent_service.py
鈹?      鈹?  鈹斺攢鈹€ document_service.py
鈹?      鈹?鈹?      鈹溾攢鈹€ ai/             # AI缁勪欢
鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ chains/     # LangChain閾?鈹?      鈹?  鈹溾攢鈹€ graphs/     # LangGraph鍥?鈹?      鈹?  鈹溾攢鈹€ indexes/    # LlamaIndex绱㈠紩
鈹?      鈹?  鈹斺攢鈹€ tools/      # 鑷畾涔夊伐鍏?鈹?      鈹?鈹?      鈹斺攢鈹€ utils/          # 宸ュ叿鍑芥暟
鈹?          鈹溾攢鈹€ __init__.py
鈹?          鈹斺攢鈹€ http_client.py
鈹?鈹溾攢鈹€ tests/                  # 娴嬭瘯鐩綍
鈹?  鈹溾攢鈹€ __init__.py
鈹?  鈹溾攢鈹€ conftest.py         # pytest fixtures
鈹?  鈹溾攢鈹€ unit/
鈹?  鈹?  鈹溾攢鈹€ test_models.py
鈹?  鈹?  鈹斺攢鈹€ test_services.py
鈹?  鈹溾攢鈹€ integration/
鈹?  鈹?  鈹溾攢鈹€ test_api.py
鈹?  鈹?  鈹斺攢鈹€ test_ai.py
鈹?  鈹斺攢鈹€ fixtures/           # 娴嬭瘯鏁版嵁
鈹?      鈹斺攢鈹€ sample.pdf
鈹?鈹溾攢鈹€ data/                   # 鏁版嵁鐩綍
鈹?  鈹溾攢鈹€ uploads/            # 涓婁紶鏂囦欢
鈹?  鈹斺攢鈹€ chroma/             # ChromaDB鎸佷箙鍖?鈹?鈹溾攢鈹€ scripts/                # 鑴氭湰
鈹?  鈹溾攢鈹€ start.sh
鈹?  鈹斺攢鈹€ start.ps1
鈹?鈹斺攢鈹€ docs/                   # 鏂囨。
    鈹斺攢鈹€ architecture.md
```

> **Java绫绘瘮**锛歚src/ai_native_dev/` 鈮?`src/main/java/com/company/project/`锛宍tests/` 鈮?`src/test/java/`

---

## 3.2 鏃ュ織绠＄悊锛歭oguru

```python
# src/ai_native_dev/core/logging.py
import sys
from loguru import logger
from ..core.settings import settings

def setup_logging() -> None:
    """閰嶇疆鏃ュ織绯荤粺"""
    # 绉婚櫎榛樿handler
    logger.remove()

    # 鎺у埗鍙拌緭鍑猴紙寮€鍙戠幆澧冨僵鑹诧級
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

    # 鏂囦欢杈撳嚭锛堟寜澶╄疆杞紝淇濈暀7澶╋級
    logger.add(
        "logs/app_{time:YYYY-MM-DD}.log",
        level="INFO",
        rotation="00:00",       # 姣忓ぉ杞浆
        retention="7 days",     # 淇濈暀7澶?        compression="zip",      # 鍘嬬缉鏃ф棩蹇?        format="{time:YYYY-MM-DD HH:mm:ss.SSS} | {level: <8} | {name}:{function}:{line} | {message}",
        encoding="utf-8",
    )

    # 閿欒鏃ュ織鍗曠嫭鏂囦欢
    logger.add(
        "logs/error_{time:YYYY-MM-DD}.log",
        level="ERROR",
        rotation="00:00",
        retention="30 days",
        compression="zip",
        encoding="utf-8",
    )

# 浣跨敤绀轰緥
# from loguru import logger
# logger.info("鐢ㄦ埛鐧诲綍: user_id={}", user_id)
# logger.error("鏁版嵁搴撹繛鎺ュけ璐?, exc_info=True)
# logger.debug("璇锋眰鍙傛暟: {}", payload)
```

```python
# src/ai_native_dev/core/settings.py
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    # 搴旂敤
    app_name: str = "AI Native Dev"
    debug: bool = False
    app_env: str = "development"

    # OpenAI
    openai_api_key: str = ""
    openai_base_url: str = "https://api.openai.com/v1"
    openai_model: str = "gpt-4o-mini"
    embedding_model: str = "text-embedding-3-small"

    # 鏁版嵁搴?    database_url: str = "postgresql://localhost:5432/mydb"

    # ChromaDB
    chroma_persist_dir: str = "./data/chroma"

    # 涓婁紶
    upload_dir: str = "./data/uploads"
    max_upload_size: int = 10_485_760  # 10MB

settings = Settings()
```

---

## 3.3 閰嶇疆绠＄悊

```python
# src/ai_native_dev/config.py
"""缁熶竴閰嶇疆鍏ュ彛"""
from .core.settings import settings

# 瀵煎嚭閰嶇疆鍗曚緥
__all__ = ["settings"]

# 浣跨敤鏂瑰紡锛堜换浣曟ā鍧椾腑锛?
# from ai_native_dev.config import settings
# print(settings.openai_api_key)
```

```ini
# .env.example
# 澶嶅埗姝ゆ枃浠朵负.env骞跺～鍏ュ疄闄呭€?
# 搴旂敤閰嶇疆
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

# 涓婁紶閰嶇疆
UPLOAD_DIR=./data/uploads
MAX_UPLOAD_SIZE=10485760
```

---

## 3.4 pytest 鍗曞厓娴嬭瘯

```python
# tests/conftest.py
"""pytest鍏ㄥ眬fixtures"""
import pytest
from httpx import AsyncClient
from ai_native_dev.main import app

@pytest.fixture
async def client():
    """寮傛娴嬭瘯瀹㈡埛绔?""
    async with AsyncClient(app=app, base_url="http://test") as ac:
        yield ac

@pytest.fixture
def sample_document():
    """娴嬭瘯鏂囨。"""
    return {
        "title": "Python鏁欑▼",
        "content": "Python鏄竴绉嶈В閲婂瀷銆侀珮绾х紪绋嬭瑷€銆?,
        "metadata": {"source": "test", "author": "tester"},
    }

@pytest.fixture
def mock_openai_response():
    """妯℃嫙OpenAI鍝嶅簲"""
    return {
        "id": "chatcmpl-test123",
        "choices": [{
            "message": {
                "role": "assistant",
                "content": "杩欐槸涓€涓祴璇曞洖澶嶃€?,
            },
            "finish_reason": "stop",
        }],
    }
```

```python
# tests/unit/test_models.py
"""鏁版嵁妯″瀷娴嬭瘯"""
import pytest
from pydantic import ValidationError
from ai_native_dev.models.chat import ChatRequest, ChatResponse

class TestChatRequest:
    def test_valid_request(self):
        req = ChatRequest(
            message="浣犲ソ",
            session_id="test-001",
        )
        assert req.message == "浣犲ソ"
        assert req.session_id == "test-001"
        assert req.temperature == 0.7  # 榛樿鍊?
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
        assert req.history == []  # 榛樿绌哄垪琛?        assert req.stream is False
```

```python
# tests/unit/test_services.py
"""涓氬姟閫昏緫娴嬭瘯"""
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
        mock_llm.ainvoke.return_value = MagicMock(content="娴嬭瘯绛旀")
        mock_get_llm.return_value = mock_llm

        result = await rag_service.query("浠€涔堟槸Python?")
        assert "娴嬭瘯绛旀" in result.answer
        assert result.sources is not None

    async def test_query_empty_raises(self, rag_service):
        with pytest.raises(ValueError, match="鏌ヨ涓嶈兘涓虹┖"):
            await rag_service.query("")
```

```python
# tests/integration/test_api.py
"""API闆嗘垚娴嬭瘯"""
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
            json={"message": "浣犲ソ", "session_id": "test-001"},
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
        assert response.status_code == 422  # Pydantic鏍￠獙澶辫触
```

杩愯娴嬭瘯锛?
```bash
# 杩愯鎵€鏈夋祴璇?uv run pytest

# 杩愯鎸囧畾鐩綍
uv run pytest tests/unit/

# 甯﹁鐩栫巼
uv run pytest --cov=src --cov-report=html

# 鍙繍琛屽け璐ョ殑娴嬭瘯
uv run pytest --lf

# 骞惰杩愯锛堝畨瑁卲ytest-xdist锛?uv run pytest -n auto

# 鏄剧ず鎵撳嵃杈撳嚭
uv run pytest -s

# 璇︾粏妯″紡
uv run pytest -vv
```

---

## 3.5 浠ｇ爜璐ㄩ噺锛歳uff + mypy

### 3.5.1 ruff锛氭瀬閫烲inter + Formatter

```bash
# 妫€鏌ヤ唬鐮?uv run ruff check .

# 鑷姩淇
uv run ruff check . --fix

# 鏍煎紡鍖栦唬鐮?uv run ruff format .

# 妫€鏌ョ壒瀹氳鍒?uv run ruff check . --select E,W,F,I,B

# 鎺掗櫎鐩綍
# 鍦?pyproject.toml 涓厤缃?
# [tool.ruff]
# exclude = ["data", "scripts", ".venv"]
```

### 3.5.2 mypy锛氶潤鎬佺被鍨嬫鏌?
```bash
# 绫诲瀷妫€鏌?uv run mypy src/

# 涓ユ牸妯″紡
uv run mypy --strict src/

# 蹇界暐缂哄け瀵煎叆
uv run mypy --ignore-missing-imports src/
```

### 3.5.3 pre-commit 閽╁瓙

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
# 瀹夎pre-commit
uv run pre-commit install

# 鎵嬪姩杩愯
uv run pre-commit run --all-files
```

---

## 3.6 Makefile锛氬父鐢ㄥ懡浠?
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
make install    # 瀹夎渚濊禆
make dev        # 瀹夎寮€鍙戜緷璧?make run        # 鍚姩鏈嶅姟
make test       # 杩愯娴嬭瘯
make lint       # 浠ｇ爜妫€鏌?make format     # 鏍煎紡鍖?make type-check # 绫诲瀷妫€鏌?make clean      # 娓呯悊缂撳瓨
```

---

## 鏈珷灏忕粨

| 宸ュ叿 | 鐢ㄩ€?| Java绫绘瘮 |
|------|------|---------|
| PEP 8鐩綍缁撴瀯 | 椤圭洰缁勭粐 | Maven鏍囧噯鐩綍 |
| loguru | 鏃ュ織 | Logback/SLF4J |
| pydantic-settings | 閰嶇疆绠＄悊 | @ConfigurationProperties |
| pytest | 鍗曞厓娴嬭瘯 | JUnit 5 |
| ruff | Linter + Formatter | Checkstyle + Spotless |
| mypy | 绫诲瀷妫€鏌?| 缂栬瘧鍣ㄦ湰韬?|
| pre-commit | Git閽╁瓙 | Git hooks |
| Makefile | 鍛戒护蹇嵎鏂瑰紡 | Maven goals |

涓嬩竴绔犳垜浠皢娣卞叆FastAPI Web妗嗘灦銆?

# 绗洓绔狅細FastAPI Web妗嗘灦

> 鍒嗗眰鏋舵瀯 + Pydantic妯″瀷 + 渚濊禆娉ㄥ叆 + 寮傛璺敱 + 涓棿浠?+ 鍏ㄥ眬寮傚父澶勭悊 + httpx瀹㈡埛绔?
---

## 4.1 鍒嗗眰鏋舵瀯鎬昏

```
璇锋眰 鈫?涓棿浠?鈫?璺敱(API灞? 鈫?渚濊禆娉ㄥ叆 鈫?涓氬姟閫昏緫(Service灞? 鈫?鏁版嵁妯″瀷(Model灞? 鈫?鍝嶅簲
                                    鈫?                              寮傚父澶勭悊鍣紙鍏ㄥ眬鎹曡幏锛?```

> **Java绫绘瘮**锛欶astAPI 鈮?Spring Boot + Spring Web锛屽垎灞傛灦鏋勭浉鍚岋紙Controller 鈫?Service 鈫?Repository锛?
---

## 4.2 Pydantic 鏁版嵁妯″瀷

```python
# src/ai_native_dev/models/base.py
"""鍩虹妯″瀷瀹氫箟"""
from datetime import datetime
from pydantic import BaseModel, Field, ConfigDict
from typing import Optional, Any


class BaseSchema(BaseModel):
    """鎵€鏈塖chema鐨勫熀绫?""
    model_config = ConfigDict(
        from_attributes=True,   # 鏀寔浠嶰RM瀵硅薄鍒涘缓
        populate_by_name=True,  # 鍏佽瀛楁鍒悕
    )


class ResponseBase(BaseModel):
    """缁熶竴鍝嶅簲鏍煎紡"""
    code: int = Field(default=200, description="涓氬姟鐘舵€佺爜")
    message: str = Field(default="success", description="鎻愮ず淇℃伅")
    data: Optional[Any] = Field(default=None, description="鍝嶅簲鏁版嵁")
    timestamp: datetime = Field(default_factory=datetime.now, description="鍝嶅簲鏃堕棿")


class PaginatedResponse(BaseModel):
    """鍒嗛〉鍝嶅簲"""
    items: list[Any]
    total: int
    page: int
    page_size: int
    total_pages: int
```

```python
# src/ai_native_dev/models/chat.py
"""鑱婂ぉ鐩稿叧妯″瀷"""
from pydantic import BaseModel, Field, field_validator
from typing import Optional
from enum import Enum


class MessageRole(str, Enum):
    """娑堟伅瑙掕壊"""
    USER = "user"
    ASSISTANT = "assistant"
    SYSTEM = "system"


class ChatMessage(BaseModel):
    """鍗曟潯娑堟伅"""
    role: MessageRole
    content: str = Field(..., min_length=1, max_length=10000)
    metadata: Optional[dict] = None


class ChatRequest(BaseModel):
    """鑱婂ぉ璇锋眰"""
    message: str = Field(
        ...,
        min_length=1,
        max_length=10000,
        description="鐢ㄦ埛娑堟伅",
        examples=["浣犲ソ锛岃浠嬬粛涓€涓婸ython"],
    )
    session_id: str = Field(
        ...,
        min_length=1,
        max_length=100,
        description="浼氳瘽ID",
        examples=["session-001"],
    )
    history: list[ChatMessage] = Field(
        default_factory=list,
        description="鍘嗗彶娑堟伅",
    )
    temperature: float = Field(
        default=0.7,
        ge=0.0,
        le=2.0,
        description="娓╁害鍙傛暟",
    )
    stream: bool = Field(default=False, description="鏄惁娴佸紡杩斿洖")
    use_rag: bool = Field(default=True, description="鏄惁浣跨敤RAG妫€绱?)

    @field_validator("message")
    @classmethod
    def validate_message(cls, v: str) -> str:
        """娑堟伅涓嶈兘鍙湁绌虹櫧"""
        if not v.strip():
            raise ValueError("娑堟伅涓嶈兘涓虹┖")
        return v.strip()


class ChatResponse(BaseModel):
    """鑱婂ぉ鍝嶅簲"""
    answer: str = Field(..., description="AI鍥炵瓟")
    session_id: str = Field(..., description="浼氳瘽ID")
    sources: list[dict] = Field(
        default_factory=list,
        description="RAG妫€绱㈡潵婧?,
    )
    usage: Optional[dict] = Field(default=None, description="Token浣跨敤閲?)


class DocumentUploadRequest(BaseModel):
    """鏂囨。涓婁紶璇锋眰"""
    title: str = Field(..., min_length=1, max_length=200, description="鏂囨。鏍囬")
    description: Optional[str] = Field(default=None, max_length=500)
```

---

## 4.3 搴旂敤鍏ュ彛涓庝腑闂翠欢

```python
# src/ai_native_dev/main.py
"""FastAPI搴旂敤鍏ュ彛"""
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
    """搴旂敤鐢熷懡鍛ㄦ湡绠＄悊"""
    # 鍚姩鏃?    setup_logging()
    from loguru import logger
    logger.info(f"鍚姩 {settings.app_name} (env={settings.app_env})")

    # 鍒濆鍖朅I缁勪欢
    from .ai.indexes.vector_index import init_vector_store
    await init_vector_store()
    logger.info("鍚戦噺绱㈠紩鍒濆鍖栧畬鎴?)

    yield  # 搴旂敤杩愯涓?
    # 鍏抽棴鏃?    logger.info("姝ｅ湪鍏抽棴搴旂敤...")


app = FastAPI(
    title=settings.app_name,
    description="鏂囨。涓婁紶 + RAG闂瓟 + 澶氳疆Agent瀵硅瘽鐨凙I鍔╂墜API",
    version="0.1.0",
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc",
)

# === 涓棿浠讹紙鎵ц椤哄簭锛氫粠涓嬪線涓婃坊鍔狅紝浠庝笂寰€涓嬫墽琛岋級 ===

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 鐢熶骇鐜鏇挎崲涓哄叿浣撳煙鍚?    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 璇锋眰鏃ュ織
app.add_middleware(RequestLoggingMiddleware)

# 闄愭祦
app.add_middleware(RateLimitMiddleware, max_requests=100, window_seconds=60)

# === 鍏ㄥ眬寮傚父澶勭悊 ===
app.add_exception_handler(AppException, app_exception_handler)

# === 娉ㄥ唽璺敱 ===
app.include_router(health.router, tags=["鍋ュ悍妫€鏌?])
app.include_router(chat.router, prefix="/api/v1/chat", tags=["瀵硅瘽"])
app.include_router(documents.router, prefix="/api/v1/documents", tags=["鏂囨。"])


@app.get("/")
async def root():
    return {"message": f"Welcome to {settings.app_name}", "docs": "/docs"}
```

```python
# src/ai_native_dev/api/middleware.py
"""涓棿浠跺畾涔?""
import time
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response
from loguru import logger
from collections import defaultdict


class RequestLoggingMiddleware(BaseHTTPMiddleware):
    """璇锋眰鏃ュ織涓棿浠?""

    async def dispatch(self, request: Request, call_next):
        start_time = time.perf_counter()

        # 璇锋眰淇℃伅
        logger.info(
            f"鈫?{request.method} {request.url.path} "
            f"from {request.client.host if request.client else 'unknown'}"
        )

        response = await call_next(request)

        # 鍝嶅簲淇℃伅
        elapsed = (time.perf_counter() - start_time) * 1000
        logger.info(
            f"鈫?{request.method} {request.url.path} "
            f"[{response.status_code}] {elapsed:.2f}ms"
        )

        # 娣诲姞鍝嶅簲澶?        response.headers["X-Process-Time"] = f"{elapsed:.2f}ms"
        return response


class RateLimitMiddleware(BaseHTTPMiddleware):
    """绠€鍗曢檺娴佷腑闂翠欢锛堝唴瀛樼増锛岀敓浜х幆澧冪敤Redis锛?""

    def __init__(self, app, max_requests: int = 100, window_seconds: int = 60):
        super().__init__(app)
        self.max_requests = max_requests
        self.window_seconds = window_seconds
        self._requests: dict[str, list[float]] = defaultdict(list)

    async def dispatch(self, request: Request, call_next):
        client_ip = request.client.host if request.client else "unknown"
        now = time.time()

        # 娓呯悊杩囨湡璁板綍
        self._requests[client_ip] = [
            t for t in self._requests[client_ip]
            if now - t < self.window_seconds
        ]

        if len(self._requests[client_ip]) >= self.max_requests:
            return Response(
                content='{"detail":"璇锋眰杩囦簬棰戠箒"}',
                status_code=429,
                media_type="application/json",
            )

        self._requests[client_ip].append(now)
        return await call_next(request)
```

---

## 4.4 鍏ㄥ眬寮傚父澶勭悊

```python
# src/ai_native_dev/core/exceptions.py
"""缁熶竴寮傚父浣撶郴"""
from fastapi import Request
from fastapi.responses import JSONResponse
from typing import Optional


class AppException(Exception):
    """搴旂敤鍩虹寮傚父"""
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
    """璧勬簮涓嶅瓨鍦?""
    def __init__(self, resource: str, resource_id: str):
        super().__init__(
            message=f"{resource}涓嶅瓨鍦? {resource_id}",
            code=404,
            status_code=404,
        )


class ValidationException(AppException):
    """涓氬姟鏍￠獙寮傚父"""
    def __init__(self, message: str, details: Optional[dict] = None):
        super().__init__(message=message, code=422, status_code=422, details=details)


class AIServiceException(AppException):
    """AI鏈嶅姟寮傚父"""
    def __init__(self, message: str, provider: str = "openai"):
        super().__init__(
            message=f"AI鏈嶅姟寮傚父({provider}): {message}",
            code=503,
            status_code=503,
            details={"provider": provider},
        )


async def app_exception_handler(request: Request, exc: AppException) -> JSONResponse:
    """鍏ㄥ眬寮傚父澶勭悊鍣?""
    from loguru import logger
    logger.error(
        f"寮傚父: {exc.message} | 璺緞: {request.url.path} | 璇︽儏: {exc.details}"
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

## 4.5 璺敱涓庝緷璧栨敞鍏?
```python
# src/ai_native_dev/api/deps.py
"""渚濊禆娉ㄥ叆"""
from typing import Annotated
from fastapi import Depends, Header, HTTPException
from ai_native_dev.core.settings import settings
from ai_native_dev.services.rag_service import RagService
from ai_native_dev.services.agent_service import AgentService
from ai_native_dev.services.document_service import DocumentService

# 鏈嶅姟鍗曚緥缂撳瓨
_rag_service: RagService | None = None
_agent_service: AgentService | None = None
_document_service: DocumentService | None = None


async def get_rag_service() -> RagService:
    """鑾峰彇RAG鏈嶅姟锛堝崟渚嬶級"""
    global _rag_service
    if _rag_service is None:
        _rag_service = RagService()
    return _rag_service


async def get_agent_service() -> AgentService:
    """鑾峰彇Agent鏈嶅姟锛堝崟渚嬶級"""
    global _agent_service
    if _agent_service is None:
        _agent_service = AgentService()
    return _agent_service


async def get_document_service() -> DocumentService:
    """鑾峰彇鏂囨。鏈嶅姟锛堝崟渚嬶級"""
    global _document_service
    if _document_service is None:
        _document_service = DocumentService()
    return _document_service


async def verify_api_key(x_api_key: Annotated[str | None, Header()] = None) -> str:
    """API Key楠岃瘉"""
    if settings.debug:
        return "debug-mode"  # 璋冭瘯妯″紡璺宠繃楠岃瘉
    if not x_api_key or x_api_key != settings.openai_api_key:
        raise HTTPException(status_code=401, detail="鏃犳晥鐨凙PI Key")
    return x_api_key


# 绫诲瀷鍒悕锛堢畝鍖栬矾鐢辩鍚嶏級
RagServiceDep = Annotated[RagService, Depends(get_rag_service)]
AgentServiceDep = Annotated[AgentService, Depends(get_agent_service)]
DocumentServiceDep = Annotated[DocumentService, Depends(get_document_service)]
```

```python
# src/ai_native_dev/api/routes/health.py
"""鍋ュ悍妫€鏌ヨ矾鐢?""
from fastapi import APIRouter, Depends
from ai_native_dev.api.deps import RagServiceDep

router = APIRouter()


@router.get("/health")
async def health_check():
    """鍋ュ悍妫€鏌?""
    return {"status": "healthy", "version": "0.1.0"}


@router.get("/health/ready")
async def readiness_check(rag_service: RagServiceDep):
    """灏辩华妫€鏌?""
    return {
        "status": "ready",
        "rag_service": "available" if rag_service else "unavailable",
    }
```

```python
# src/ai_native_dev/api/routes/chat.py
"""瀵硅瘽璺敱"""
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
    AI瀵硅瘽鎺ュ彛

    - 鏀寔澶氳疆瀵硅瘽锛堥€氳繃session_id鍏宠仈锛?    - 鍙€塕AG妫€绱㈠寮?    - 鍙€夋祦寮忚繑鍥?    """
    try:
        if request.use_rag:
            # 鍏堝仛RAG妫€绱?            rag_result = await rag_service.query(request.message)
            # 灏嗘绱㈢粨鏋滄敞鍏ヤ笂涓嬫枃
            context = rag_result.context
            sources = rag_result.sources
        else:
            context = ""
            sources = []

        # Agent澶勭悊
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
    """娴佸紡瀵硅瘽鎺ュ彛锛圫SE锛?""
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
"""鏂囨。绠＄悊璺敱"""
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
    """涓婁紶鏂囨。锛堟敮鎸?txt, .md, .pdf锛?""
    # 鏍￠獙鏂囦欢绫诲瀷
    allowed_types = ["text/plain", "text/markdown", "application/pdf"]
    if file.content_type not in allowed_types:
        raise ValidationException(
            f"涓嶆敮鎸佺殑鏂囦欢绫诲瀷: {file.content_type}",
            {"allowed": allowed_types},
        )

    # 鏍￠獙鏂囦欢澶у皬
    content = await file.read()
    if len(content) > settings.max_upload_size:
        raise ValidationException(
            f"鏂囦欢杩囧ぇ: {len(content)} bytes",
            {"max_size": settings.max_upload_size},
        )

    # 淇濆瓨鏂囦欢
    file_id = str(uuid.uuid4())
    file_path = os.path.join(settings.upload_dir, f"{file_id}_{file.filename}")
    os.makedirs(os.path.dirname(file_path), exist_ok=True)
    with open(file_path, "wb") as f:
        f.write(content)

    # 绱㈠紩鏂囨。
    doc_id = await document_service.index_document(
        file_path=file_path,
        title=title,
        description=description,
        metadata={"file_id": file_id, "filename": file.filename},
    )

    return ResponseBase(
        message="鏂囨。涓婁紶骞剁储寮曟垚鍔?,
        data={"doc_id": doc_id, "file_id": file_id, "filename": file.filename},
    )


@router.delete("/{doc_id}", response_model=ResponseBase)
async def delete_document(
    doc_id: str,
    document_service: DocumentServiceDep,
):
    """鍒犻櫎鏂囨。"""
    deleted = await document_service.delete_document(doc_id)
    if not deleted:
        raise NotFoundException("鏂囨。", doc_id)
    return ResponseBase(message="鏂囨。鍒犻櫎鎴愬姛", data={"doc_id": doc_id})


@router.get("/list", response_model=ResponseBase)
async def list_documents(
    document_service: DocumentServiceDep,
    limit: int = 20,
    offset: int = 0,
):
    """鏂囨。鍒楄〃"""
    docs = await document_service.list_documents(limit=limit, offset=offset)
    return ResponseBase(data=docs)
```

---

## 4.6 灏佽 httpx 瀹㈡埛绔紙鏇夸唬 OpenFeign锛?
```python
# src/ai_native_dev/utils/http_client.py
"""
HTTP瀹㈡埛绔皝瑁咃紙鏇夸唬Java涓殑OpenFeign/RestTemplate锛?
鐗规€?
- 杩炴帴姹犲鐢?- 鑷姩閲嶈瘯
- 璇锋眰/鍝嶅簲鏃ュ織
- 瓒呮椂鎺у埗
- 缁熶竴閿欒澶勭悊
"""
import httpx
from typing import Optional, Any
from loguru import logger
from tenacity import retry, stop_after_attempt, wait_exponential


class HttpClient:
    """寮傛HTTP瀹㈡埛绔?""

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
        """缁熶竴璇锋眰鏂规硶"""
        logger.debug(f"HTTP {method} {path} kwargs={kwargs}")

        response = await self._client.request(method, path, **kwargs)

        if response.status_code >= 400:
            logger.error(
                f"HTTP {method} {path} 澶辫触: "
                f"status={response.status_code} body={response.text}"
            )
            response.raise_for_status()

        logger.debug(f"HTTP {method} {path} 鈫?{response.status_code}")
        return response.json()

    async def stream_get(self, path: str, params: Optional[dict] = None):
        """娴佸紡GET璇锋眰"""
        async with self._client.stream("GET", path, params=params) as response:
            response.raise_for_status()
            async for chunk in response.aiter_bytes():
                yield chunk

    async def close(self):
        """鍏抽棴瀹㈡埛绔?""
        await self._client.aclose()

    async def __aenter__(self):
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        await self.close()


# 浣跨敤绀轰緥
"""
# 浣滀负渚濊禆娉ㄥ叆
async def get_external_api_client() -> HttpClient:
    client = HttpClient(
        base_url="https://api.example.com",
        headers={"Authorization": f"Bearer {settings.api_key}"},
    )
    return client

# 鍦⊿ervice涓娇鐢?class SomeService:
    def __init__(self, http: HttpClient):
        self.http = http

    async def call_external(self):
        result = await self.http.get("/users/1")
        return result

# 鐩存帴浣跨敤
async with HttpClient("https://api.example.com") as client:
    data = await client.get("/users/1")
    print(data)
"""
# Java绫绘瘮: HttpClient 鈮?OpenFeign + Resilience4j锛堥噸璇曘€佽秴鏃躲€佽繛鎺ユ睜锛?```

---

## 4.7 鏈€灏忓彲杩愯绀轰緥

```python
# python4javaProgrammer/fastapi_demo.py
"""FastAPI鏈€灏忕ず渚?鈥斺€?鍙洿鎺ヨ繍琛?""
from fastapi import FastAPI, Depends
from pydantic import BaseModel, Field
from typing import Annotated

app = FastAPI(title="Demo API")

# 妯℃嫙鏁版嵁搴?fake_db = {"users": {1: {"name": "Alice"}, 2: {"name": "Bob"}}}

# Pydantic妯″瀷
class UserCreate(BaseModel):
    name: str = Field(..., min_length=1, max_length=50)

class UserResponse(BaseModel):
    id: int
    name: str

# 渚濊禆
def get_db():
    return fake_db

DbDep = Annotated[dict, Depends(get_db)]

# 璺敱
@app.get("/users/{user_id}", response_model=UserResponse)
async def get_user(user_id: int, db: DbDep):
    user = db["users"].get(user_id)
    if not user:
        from fastapi import HTTPException
        raise HTTPException(status_code=404, detail="鐢ㄦ埛涓嶅瓨鍦?)
    return UserResponse(id=user_id, name=user["name"])

@app.post("/users", response_model=UserResponse, status_code=201)
async def create_user(user: UserCreate, db: DbDep):
    new_id = max(db["users"].keys()) + 1
    db["users"][new_id] = {"name": user.name}
    return UserResponse(id=new_id, name=user.name)

# 杩愯: uvicorn fastapi_demo:app --reload
# 鏂囨。: http://localhost:8000/docs
```

---

## 鏈珷灏忕粨

| 鐗规€?| FastAPI | Java绫绘瘮 |
|------|---------|---------|
| 璺敱 | `@router.get/post` | `@GetMapping/@PostMapping` |
| 鏁版嵁鏍￠獙 | Pydantic妯″瀷 | @Valid + Bean Validation |
| 渚濊禆娉ㄥ叆 | `Depends()` | @Autowired |
| 涓棿浠?| `add_middleware()` | Filter/Interceptor |
| 寮傚父澶勭悊 | `add_exception_handler()` | @ControllerAdvice |
| HTTP瀹㈡埛绔?| httpx灏佽 | OpenFeign/RestTemplate |
| 寮傛璺敱 | `async def` | CompletableFuture/WebFlux |
| 鐢熷懡鍛ㄦ湡 | `lifespan` | @PostConstruct/@PreDestroy |

涓嬩竴绔犳垜浠皢杩涘叆AI涓夊ぇ妗嗘灦瀹炴垬銆?

# 绗簲绔狅細AI涓夊ぇ妗嗘灦瀹炴垬

> LangChain + LangGraph + LlamaIndex 涓夋鏋跺崗鍚屽疄鎴?
---

## 5.1 LangChain锛氬伐鍏蜂笌閾?
### 5.1.1 LCEL閾?(LangChain Expression Language)

LCEL鏄疞angChain鐨勫０鏄庡紡绠￠亾璇硶锛岀敤 `|` 杩愮畻绗︿覆鑱旂粍浠躲€?
```python
# python4javaProgrammer/ai_demo/langchain_lcel.py
"""LangChain LCEL閾炬渶灏忕ず渚?""
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from dotenv import load_dotenv

load_dotenv()

# --- 鍩虹閾? prompt | llm | parser ---
prompt = ChatPromptTemplate.from_messages([
    ("system", "浣犳槸涓€涓狿ython缂栫▼鍔╂墜锛屽洖绛旂畝娲佸噯纭€?),
    ("user", "{question}"),
])

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.7)

# StrOutputParser: 鎻愬彇AI鍥炲鐨勫瓧绗︿覆鍐呭
chain = prompt | llm | StrOutputParser()

# 璋冪敤
result = chain.invoke({"question": "Python鐨凣IL鏄粈涔堬紵涓€鍙ヨ瘽瑙ｉ噴銆?})
print(result)  # GIL鏄叏灞€瑙ｉ噴鍣ㄩ攣...

# --- 甯︿笂涓嬫枃鐨凴AG閾?---
from langchain_core.runnables import RunnableLambda

def retrieve_context(query: str) -> str:
    """妯℃嫙妫€绱紙瀹為檯鐢↙lamaIndex鎴栧悜閲忓簱锛?""
    fake_docs = {
        "Python": "Python鏄В閲婂瀷璇█锛屾敮鎸佸绉嶇紪绋嬭寖寮忋€?,
        "GIL": "GIL纭繚鍚屼竴鏃跺埢鍙湁涓€涓嚎绋嬫墽琛孭ython瀛楄妭鐮併€?,
    }
    for key in fake_docs:
        if key.lower() in query.lower():
            return fake_docs[key]
    return ""

rag_chain = (
    {"context": RunnableLambda(retrieve_context), "question": RunnablePassthrough()}
    | ChatPromptTemplate.from_messages([
        ("system", "鏍规嵁浠ヤ笅涓婁笅鏂囧洖绛旈棶棰樸€俓n涓婁笅鏂? {context}"),
        ("user", "{question}"),
    ])
    | llm
    | StrOutputParser()
)

result = rag_chain.invoke("浠€涔堟槸GIL锛?)
print(result)

# --- 閾剧殑缁勪欢鍖栫粍鍚?---
# 姣忎釜缁勪欢閮芥槸Runnable锛屽彲浠ュ崟鐙祴璇曞拰鏇挎崲
prompt_component = ChatPromptTemplate.from_messages([
    ("system", "浣犳槸{role}銆?),
    ("user", "{input}"),
])

# 浣跨敤RunnableParallel骞惰鎵ц
from langchain_core.runnables import RunnableParallel

parallel_chain = RunnableParallel(
    original=RunnablePassthrough(),
    uppercased=RunnableLambda(lambda x: x["input"].upper()),
    length=RunnableLambda(lambda x: len(x["input"])),
)

result = parallel_chain.invoke({"role": "缈昏瘧", "input": "hello world"})
print(result)
# {'original': {'role': '缈昏瘧', 'input': 'hello world'},
#  'uppercased': 'HELLO WORLD', 'length': 11}

# --- 娴佸紡杈撳嚭 ---
print("\n=== 娴佸紡杈撳嚭 ===")
for chunk in chain.stream({"question": "鐢ㄤ笁鍙ヨ瘽浠嬬粛Python銆?}):
    print(chunk, end="", flush=True)
print()

# --- 鎵归噺澶勭悊 ---
results = chain.batch([
    {"question": "浠€涔堟槸鍒楄〃鎺ㄥ寮忥紵"},
    {"question": "浠€涔堟槸瑁呴グ鍣紵"},
    {"question": "浠€涔堟槸鐢熸垚鍣紵"},
])
for i, r in enumerate(results):
    print(f"\n--- 闂{i+1} ---\n{r}")
```

### 5.1.2 Prompt妯℃澘

```python
# python4javaProgrammer/ai_demo/langchain_prompt.py
"""LangChain Prompt妯℃澘"""
from langchain_core.prompts import (
    ChatPromptTemplate,
    FewShotChatMessagePromptTemplate,
    PromptTemplate,
)
from langchain_openai import ChatOpenAI
from langchain_core.output_parsers import StrOutputParser
from dotenv import load_dotenv

load_dotenv()

# --- 鍩虹Prompt妯℃澘 ---
template = PromptTemplate.from_template(
    "璇风敤{language}璇█缂栧啓涓€涓獅functionality}鐨勫嚱鏁帮紝骞舵坊鍔犳敞閲娿€?
)
prompt = template.format(language="Python", functionality="蹇€熸帓搴?)
print("=== 鍩虹妯℃澘 ===")
print(prompt)

# --- ChatPrompt锛堝杞璇濓級 ---
chat_prompt = ChatPromptTemplate.from_messages([
    ("system", "浣犳槸涓€涓祫娣眥domain}宸ョ▼甯堛€?),
    ("human", "闂: {question}"),
    ("ai", "鍥炵瓟: "),
])

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.3)
chain = chat_prompt | llm | StrOutputParser()

result = chain.invoke({
    "domain": "Python鍚庣",
    "question": "FastAPI鍜孌jango濡備綍閫夋嫨锛?,
})
print("\n=== ChatPrompt ===")
print(result)

# --- Few-shot Prompt锛堝皯鏍锋湰瀛︿範锛?---
examples = [
    {"input": "Python鍒楄〃鎬庝箞鍙嶈浆锛?, "output": "list[::-1] 鎴?list.reverse()"},
    {"input": "Python瀛楀吀鎬庝箞鎸夊€兼帓搴忥紵", "output": "sorted(d.items(), key=lambda x: x[1])"},
    {"input": "Python瀛楃涓叉€庝箞鍘荤┖鏍硷紵", "output": "s.strip() 鍘讳袱绔? s.replace(' ', '') 鍘绘墍鏈?},
]

example_prompt = ChatPromptTemplate.from_messages([
    ("human", "{input}"),
    ("ai", "{output}"),
])

few_shot_prompt = FewShotChatMessagePromptTemplate(
    example_prompt=example_prompt,
    examples=examples,
)

final_prompt = ChatPromptTemplate.from_messages([
    ("system", "浣犳槸涓€涓狿ython缂栫▼鍔╂墜锛岃鍙傝€冪ず渚嬮鏍煎洖绛斻€?),
    few_shot_prompt,
    ("human", "{input}"),
])

chain = final_prompt | llm | StrOutputParser()
result = chain.invoke({"input": "Python鍒楄〃鎬庝箞鍘婚噸锛?})
print("\n=== Few-shot ===")
print(result)

# --- 鍔ㄦ€丳rompt锛堟牴鎹潯浠堕€夋嫨妯℃澘锛?---
from langchain_core.runnables import RunnableLambda

def choose_prompt(input_dict):
    """鏍规嵁闂绫诲瀷閫夋嫨涓嶅悓妯℃澘"""
    question = input_dict["question"]
    if "浠ｇ爜" in question or "缂栫▼" in question:
        return ChatPromptTemplate.from_messages([
            ("system", "浣犳槸缂栫▼鍔╂墜銆傚彧杩斿洖浠ｇ爜锛屼笉瑙ｉ噴銆?),
            ("user", "{question}"),
        ])
    else:
        return ChatPromptTemplate.from_messages([
            ("system", "浣犳槸閫氱敤鍔╂墜銆傚洖绛旂畝娲併€?),
            ("user", "{question}"),
        ])

dynamic_chain = (
    RunnableLambda(choose_prompt)
    | llm
    | StrOutputParser()
)

print("\n=== 鍔ㄦ€丳rompt ===")
print(dynamic_chain.invoke({"question": "浠ｇ爜锛氬啋娉℃帓搴?}))
```

### 5.1.3 杈撳嚭瑙ｆ瀽鍣?
```python
# python4javaProgrammer/ai_demo/langchain_parser.py
"""LangChain 杈撳嚭瑙ｆ瀽鍣?""
from langchain_core.output_parsers import (
    StrOutputParser,
    JsonOutputParser,
    PydanticOutputParser,
    CommaSeparatedListOutputParser,
)
from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import ChatOpenAI
from pydantic import BaseModel, Field
from typing import List
from dotenv import load_dotenv

load_dotenv()

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)

# --- 1. 瀛楃涓茶В鏋愬櫒 ---
str_chain = (
    ChatPromptTemplate.from_messages([("user", "{question}")])
    | llm
    | StrOutputParser()
)

# --- 2. JSON瑙ｆ瀽鍣?---
json_parser = JsonOutputParser()
json_chain = (
    ChatPromptTemplate.from_messages([
        ("system", "杩斿洖JSON鏍煎紡: {format_instructions}"),
        ("user", "{question}"),
    ]).partial(format_instructions=json_parser.get_format_instructions())
    | llm
    | json_parser
)
result = json_chain.invoke({"question": "浠嬬粛Python鐨勪笁涓壒鐐?})
print("=== JSON瑙ｆ瀽鍣?===")
print(result)

# --- 3. Pydantic瑙ｆ瀽鍣紙鎺ㄨ崘锛氱被鍨嬪畨鍏級 ---
class CodeReview(BaseModel):
    """浠ｇ爜瀹℃煡缁撴灉"""
    issues: List[str] = Field(description="鍙戠幇鐨勯棶棰樺垪琛?)
    severity: str = Field(description="涓ラ噸绋嬪害: low/medium/high")
    suggestion: str = Field(description="鏀硅繘寤鸿")

pydantic_parser = PydanticOutputParser(pydantic_object=CodeReview)

review_chain = (
    ChatPromptTemplate.from_messages([
        ("system", "浣犳槸涓€涓唬鐮佸鏌ヤ笓瀹躲€俓n{format_instructions}"),
        ("user", "瀹℃煡浠ヤ笅浠ｇ爜:\n{code}"),
    ]).partial(format_instructions=pydantic_parser.get_format_instructions())
    | llm
    | pydantic_parser
)

code = """
def add(a, b):
    return a + b
result = add("1", 2)
"""
review = review_chain.invoke({"code": code})
print("\n=== Pydantic瑙ｆ瀽鍣?===")
print(f"闂: {review.issues}")
print(f"涓ラ噸搴? {review.severity}")
print(f"寤鸿: {review.suggestion}")

# --- 4. 鍒楄〃瑙ｆ瀽鍣?---
list_parser = CommaSeparatedListOutputParser()
list_chain = (
    ChatPromptTemplate.from_messages([
        ("user", "鍒楀嚭{count}涓狿ython Web妗嗘灦銆俓n{format_instructions}"),
    ]).partial(format_instructions=list_parser.get_format_instructions())
    | llm
    | list_parser
)
frameworks = list_chain.invoke({"count": 5})
print("\n=== 鍒楄〃瑙ｆ瀽鍣?===")
print(frameworks)  # ['FastAPI', 'Django', 'Flask', 'Tornado', 'Sanic']
```

### 5.1.4 Tool涓嶢gent

```python
# python4javaProgrammer/ai_demo/langchain_tools_agent.py
"""LangChain 宸ュ叿涓嶢gent"""
from langchain_openai import ChatOpenAI
from langchain_core.tools import tool, Tool
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain.agents import create_tool_calling_agent, AgentExecutor
from langchain_core.messages import HumanMessage, AIMessage
from dotenv import load_dotenv
import datetime
import requests

load_dotenv()

# --- 鑷畾涔夊伐鍏?---
@tool
def get_current_time() -> str:
    """鑾峰彇褰撳墠鏃堕棿"""
    return datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")

@tool
def calculate(expression: str) -> str:
    """璁＄畻鏁板琛ㄨ揪寮忋€傝緭鍏? 鏁板琛ㄨ揪寮忓瓧绗︿覆锛屽 '2+3*4'"""
    try:
        # 瀹夊叏鐨別val锛堝疄闄呴」鐩敤ast.literal_eval鎴栦笓闂ㄥ簱锛?        allowed = set("0123456789+-*/.() ")
        if not all(c in allowed for c in expression):
            return "鍙敮鎸佹暟瀛楀拰鍩烘湰杩愮畻绗?
        result = eval(expression)
        return str(result)
    except Exception as e:
        return f"璁＄畻澶辫触: {e}"

@tool
def search_web(query: str) -> str:
    """鎼滅储缃戠粶淇℃伅锛堟ā鎷燂級"""
    # 瀹為檯椤圭洰涓皟鐢ㄦ悳绱PI
    return f"鍏充簬 '{query}' 鐨勬悳绱㈢粨鏋? Python 3.12浜?023骞村彂甯冿紝鎬ц兘鎻愬崌鏄捐憲..."

# --- 浣跨敤 @tool 瑁呴グ鍣ㄧ殑宸ュ叿 ---
tools = [get_current_time, calculate, search_web]

# --- 鍒涘缓Agent ---
llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)

prompt = ChatPromptTemplate.from_messages([
    ("system", """浣犳槸涓€涓櫤鑳藉姪鎵嬶紝鍙互浣跨敤浠ヤ笅宸ュ叿:
{tools}

璇锋牴鎹敤鎴烽棶棰樺喅瀹氭槸鍚︿娇鐢ㄥ伐鍏枫€傚鏋滈渶瑕佷娇鐢ㄥ伐鍏凤紝璇存槑鐞嗙敱銆?""),
    MessagesPlaceholder(variable_name="chat_history", optional=True),
    ("user", "{input}"),
    ("assistant", "宸ュ叿浣跨敤璁板綍: {agent_scratchpad}"),
])

agent = create_tool_calling_agent(llm, tools, prompt)
agent_executor = AgentExecutor(agent=agent, tools=tools, verbose=True)

# --- 鎵ц ---
print("=== 娴嬭瘯1: 鏃堕棿鏌ヨ ===")
result = agent_executor.invoke({"input": "鐜板湪鍑犵偣浜嗭紵"})
print(result["output"])

print("\n=== 娴嬭瘯2: 鏁板璁＄畻 ===")
result = agent_executor.invoke({"input": "璁＄畻 (15 + 27) * 3 - 10"})
print(result["output"])

print("\n=== 娴嬭瘯3: 缁勫悎浠诲姟 ===")
result = agent_executor.invoke({
    "input": "鍏堝憡璇夋垜鐜板湪鏃堕棿锛岀劧鍚庢悳绱ython 3.12鐨勬柊鐗规€э紝鏈€鍚庤绠?365 * 24"
})
print(result["output"])

# --- 甯﹀巻鍙插璇濈殑Agent ---
print("\n=== 娴嬭瘯4: 甯﹀巻鍙插璇?===")
chat_history = []
question = "鎴戝彨寮犱笁锛岃浣忔垜鐨勫悕瀛?
result = agent_executor.invoke({"input": question, "chat_history": chat_history})
chat_history.append(HumanMessage(content=question))
chat_history.append(AIMessage(content=result["output"]))
print(result["output"])

# 杩介棶
question2 = "鎴戝彨浠€涔堝悕瀛楋紵"
result2 = agent_executor.invoke({"input": question2, "chat_history": chat_history})
print(result2["output"])
```

---

## 5.2 LangGraph锛氱姸鎬佺紪鎺?
### 5.2.1 StateGraph 鍩虹

```python
# python4javaProgrammer/ai_demo/langgraph_basic.py
"""LangGraph StateGraph 鐘舵€佸浘缂栨帓"""
from langgraph.graph import StateGraph, END, START
from typing import TypedDict, Annotated, Literal
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, AIMessage
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from dotenv import load_dotenv
import operator

load_dotenv()

# --- 瀹氫箟鐘舵€?---
class AgentState(TypedDict):
    """Agent鐘舵€佸畾涔?""
    messages: Annotated[list, operator.add]  # 娑堟伅鍒楄〃锛堝彲绱姞锛?    question: str
    context: str
    answer: str
    needs_search: bool

# --- 瀹氫箟鑺傜偣鍑芥暟 ---
llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)

def analyze_question(state: AgentState) -> dict:
    """鍒嗘瀽闂锛氬垽鏂槸鍚﹂渶瑕佹绱?""
    question = state["question"]
    prompt = ChatPromptTemplate.from_messages([
        ("system", """鍒嗘瀽鐢ㄦ埛闂锛屽垽鏂槸鍚﹂渶瑕佹悳绱㈠閮ㄧ煡璇嗐€?鍥炵瓟JSON鏍煎紡: {{"needs_search": true/false, "reason": "..."}}
- 濡傛灉鏄父璇嗛棶棰樻垨缂栫▼鍩虹锛屼笉闇€瑕佹悳绱?- 濡傛灉娑夊強鏈€鏂颁俊鎭垨鐗瑰畾棰嗗煙鐭ヨ瘑锛岄渶瑕佹悳绱?""),
        ("user", "{question}"),
    ])
    chain = prompt | llm | StrOutputParser()
    result = chain.invoke({"question": question})

    import json
    try:
        analysis = json.loads(result)
        needs_search = analysis.get("needs_search", False)
    except:
        needs_search = "鎼滅储" in question or "鏈€鏂? in question

    return {
        "needs_search": needs_search,
        "messages": [AIMessage(content=f"鍒嗘瀽缁撴灉: needs_search={needs_search}")],
    }

def search_context(state: AgentState) -> dict:
    """妫€绱笂涓嬫枃锛堟ā鎷烺AG妫€绱級"""
    question = state["question"]
    # 瀹為檯椤圭洰涓皟鐢↙lamaIndex鎴栧悜閲忓簱
    context = f"妫€绱㈠埌鐨勭浉鍏崇煡璇? 鍏充簬'{question}'鐨勬妧鏈枃妗ｇ墖娈?.."
    return {
        "context": context,
        "messages": [AIMessage(content=f"妫€绱㈠畬鎴? {context}")],
    }

def generate_answer(state: AgentState) -> dict:
    """鐢熸垚鍥炵瓟"""
    question = state["question"]
    context = state.get("context", "")

    if context:
        prompt = ChatPromptTemplate.from_messages([
            ("system", "鏍规嵁浠ヤ笅涓婁笅鏂囧洖绛旈棶棰樸€俓n涓婁笅鏂? {context}"),
            ("user", "{question}"),
        ])
    else:
        prompt = ChatPromptTemplate.from_messages([
            ("system", "浣犳槸Python缂栫▼鍔╂墜銆?),
            ("user", "{question}"),
        ])

    chain = prompt | llm | StrOutputParser()
    answer = chain.invoke({"question": question, "context": context})

    return {
        "answer": answer,
        "messages": [AIMessage(content=answer)],
    }

# --- 鏋勫缓鍥?---
workflow = StateGraph(AgentState)

# 娣诲姞鑺傜偣
workflow.add_node("analyze", analyze_question)
workflow.add_node("search", search_context)
workflow.add_node("generate", generate_answer)

# 娣诲姞杈?workflow.add_edge(START, "analyze")

# 鏉′欢杈癸細鏍规嵁鍒嗘瀽缁撴灉鍐冲畾璺緞
def route_after_analysis(state: AgentState) -> Literal["search", "generate"]:
    """璺敱鍑芥暟"""
    if state["needs_search"]:
        return "search"
    return "generate"

workflow.add_conditional_edges(
    "analyze",
    route_after_analysis,
    {"search": "search", "generate": "generate"},
)

workflow.add_edge("search", "generate")
workflow.add_edge("generate", END)

# 缂栬瘧
app = workflow.compile()

# --- 鎵ц ---
print("=== 娴嬭瘯1: 涓嶉渶瑕佹悳绱?===")
result = app.invoke({
    "question": "Python鐨勫垪琛ㄦ帹瀵煎紡鎬庝箞鍐欙紵",
    "messages": [],
    "context": "",
    "answer": "",
    "needs_search": False,
})
print(f"鍥炵瓟: {result['answer']}")
print(f"娑堟伅鏁? {len(result['messages'])}")

print("\n=== 娴嬭瘯2: 闇€瑕佹悳绱?===")
result = app.invoke({
    "question": "鎼滅储Python 3.12鐨勬渶鏂扮壒鎬?,
    "messages": [],
    "context": "",
    "answer": "",
    "needs_search": False,
})
print(f"鍥炵瓟: {result['answer']}")
print(f"涓婁笅鏂? {result['context']}")
```

### 5.2.2 鏉′欢杈逛笌澶氳疆瀵硅瘽Agent

```python
# python4javaProgrammer/ai_demo/langgraph_multi_turn.py
"""LangGraph 澶氳疆瀵硅瘽Agent + Human-in-loop"""
from langgraph.graph import StateGraph, END, START
from langgraph.checkpoint.memory import MemorySaver
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from langchain_core.tools import tool
from langgraph.prebuilt import ToolNode
from typing import TypedDict, Annotated, Literal
import operator
from dotenv import load_dotenv

load_dotenv()

# --- 鐘舵€佸畾涔?---
class MultiTurnState(TypedDict):
    messages: Annotated[list, operator.add]
    user_input: str
    awaiting_human: bool

# --- 宸ュ叿 ---
@tool
def search_document(query: str) -> str:
    """鎼滅储涓婁紶鐨勬枃妗?""
    return f"鎵惧埌涓?{query}'鐩稿叧鐨勬枃妗ｅ唴瀹? [妯℃嫙鏂囨。鐗囨]"

@tool
def ask_human(question: str) -> str:
    """褰撻渶瑕佷汉绫荤‘璁ゆ椂璋冪敤姝ゅ伐鍏?""
    return f"绛夊緟浜虹被纭: {question}"

tools = [search_document, ask_human]
tool_node = ToolNode(tools)

# --- LLM ---
llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
llm_with_tools = llm.bind_tools(tools)

# --- 鑺傜偣 ---
def agent_node(state: MultiTurnState) -> dict:
    """Agent涓昏妭鐐?""
    messages = state["messages"]
    if not any(isinstance(m, SystemMessage) for m in messages):
        messages = [SystemMessage(content="浣犳槸鏂囨。闂瓟鍔╂墜銆傚彲浠ユ悳绱㈡枃妗ｆ垨鍦ㄩ渶瑕佺‘璁ゆ椂璇㈤棶浜虹被銆?)] + messages

    response = llm_with_tools.invoke(messages)
    return {"messages": [response]}

def should_continue(state: MultiTurnState) -> Literal["tools", "__end__"]:
    """鍒ゆ柇鏄惁缁х画鎵ц宸ュ叿"""
    last_message = state["messages"][-1]
    if hasattr(last_message, "tool_calls") and last_message.tool_calls:
        # 妫€鏌ユ槸鍚﹂渶瑕佷汉绫讳粙鍏?        for tc in last_message.tool_calls:
            if tc["name"] == "ask_human":
                return "tools"  # 鍏堣蛋宸ュ叿鑺傜偣锛堝疄闄呬腑鍙彃鍏ヤ腑鏂級
        return "tools"
    return END

# --- 鏋勫缓鍥?---
workflow = StateGraph(MultiTurnState)

workflow.add_node("agent", agent_node)
workflow.add_node("tools", tool_node)

workflow.add_edge(START, "agent")
workflow.add_conditional_edges("agent", should_continue, ["tools", END])
workflow.add_edge("tools", "agent")  # 宸ュ叿鎵ц鍚庡洖鍒癮gent

# 缂栬瘧锛堝甫璁板繂锛?memory = MemorySaver()
app = workflow.compile(checkpointer=memory)

# --- 澶氳疆瀵硅瘽娴嬭瘯 ---
config = {"configurable": {"thread_id": "conversation-001"}}

print("=== 绗竴杞?===")
result = app.invoke(
    {"messages": [HumanMessage(content="甯垜鎼滅储Python寮傛缂栫▼鐨勬枃妗?)], "user_input": "", "awaiting_human": False},
    config=config,
)
last = result["messages"][-1]
print(f"AI: {last.content if hasattr(last, 'content') else last}")

print("\n=== 绗簩杞紙杩介棶锛?===")
result = app.invoke(
    {"messages": [HumanMessage(content="鑳借缁嗚璇碼sync/await鍚楋紵")], "user_input": "", "awaiting_human": False},
    config=config,
)
last = result["messages"][-1]
print(f"AI: {last.content if hasattr(last, 'content') else last}")

print("\n=== 绗笁杞紙闇€瑕佺‘璁わ級 ===")
result = app.invoke(
    {"messages": [HumanMessage(content="鍒犻櫎鎵€鏈夋枃妗ｅ墠闇€瑕佹垜纭鍚楋紵")], "user_input": "", "awaiting_human": False},
    config=config,
)
last = result["messages"][-1]
print(f"AI: {last.content if hasattr(last, 'content') else last}")
```

### 5.2.3 Human-in-loop

```python
# python4javaProgrammer/ai_demo/langgraph_human_in_loop.py
"""LangGraph Human-in-loop 浜哄伐瀹℃牳"""
from langgraph.graph import StateGraph, END, START
from langgraph.checkpoint.memory import MemorySaver
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, AIMessage
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from typing import TypedDict, Annotated, Literal
import operator
from dotenv import load_dotenv

load_dotenv()

class ReviewState(TypedDict):
    messages: Annotated[list, operator.add]
    draft: str
    approved: bool
    feedback: str

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.3)

def generate_draft(state: ReviewState) -> dict:
    """鐢熸垚鑽夌"""
    prompt = ChatPromptTemplate.from_messages([
        ("system", "鏍规嵁鐢ㄦ埛瑕佹眰鐢熸垚鍥炲鑽夌銆?),
        ("user", "{input}"),
    ])
    chain = prompt | llm | StrOutputParser()
    draft = chain.invoke({"input": state["messages"][-1].content})
    return {
        "draft": draft,
        "messages": [AIMessage(content=f"鑽夌: {draft}")],
    }

def human_review(state: ReviewState) -> dict:
    """浜哄伐瀹℃牳鑺傜偣锛堝疄闄呬腑閫氳繃UI涓柇锛?""
    # 鍦ㄥ疄闄呭簲鐢ㄤ腑锛岃繖閲屼細鏆傚仠绛夊緟浜哄伐杈撳叆
    # LangGraph 鏀寔 interrupt_before 閰嶇疆
    return {"approved": True, "feedback": ""}

def revise_draft(state: ReviewState) -> dict:
    """鏍规嵁鍙嶉淇敼"""
    prompt = ChatPromptTemplate.from_messages([
        ("system", "鏍规嵁鍙嶉淇敼鍥炲銆傚師鑽夌: {draft}\n鍙嶉: {feedback}"),
        ("user", "璇蜂慨鏀?),
    ])
    chain = prompt | llm | StrOutputParser()
    revised = chain.invoke({"draft": state["draft"], "feedback": state["feedback"]})
    return {
        "draft": revised,
        "messages": [AIMessage(content=f"淇鐗? {revised}")],
    }

def route_review(state: ReviewState) -> Literal["revise", "__end__"]:
    """鏍规嵁瀹℃牳缁撴灉璺敱"""
    if state["approved"]:
        return END
    return "revise"

# 鏋勫缓鍥?workflow = StateGraph(ReviewState)
workflow.add_node("generate", generate_draft)
workflow.add_node("review", human_review)
workflow.add_node("revise", revise_draft)

workflow.add_edge(START, "generate")
workflow.add_edge("generate", "review")
workflow.add_conditional_edges("review", route_review, ["revise", END])
workflow.add_edge("revise", "review")  # 淇敼鍚庡啀娆″鏍?
# 缂栬瘧鏃舵寚瀹氫腑鏂偣
memory = MemorySaver()
app = workflow.compile(
    checkpointer=memory,
    interrupt_before=["review"],  # 鍦ㄥ鏍歌妭鐐瑰墠鏆傚仠
)

# 浣跨敤:
# 1. 杩愯鍒皉eview鍓嶄細鏆傚仠
# 2. 浜哄伐鏌ョ湅state["draft"]
# 3. 鐢?app.invoke({"approved": False, "feedback": "鏇寸畝娲佷簺"}, config) 缁х画
```

---

## 5.3 LlamaIndex锛歊AG瀹屾暣娴佺▼

### 5.3.1 鏂囨。鍔犺浇涓庡垎鍧?
```python
# python4javaProgrammer/ai_demo/llamaindex_index.py
"""LlamaIndex 鏂囨。鍔犺浇涓庡垎鍧?""
from llama_index.core import (
    SimpleDirectoryReader,
    VectorStoreIndex,
    StorageContext,
    Settings,
)
from llama_index.core.node_parser import SentenceSplitter
from llama_index.core.schema import Document, TextNode
from llama_index.vector_stores.chroma import ChromaVectorStore
from llama_index.embeddings.openai import OpenAIEmbedding
from langchain_openai import ChatOpenAI
import chromadb
from dotenv import load_dotenv
import os

load_dotenv()

# --- 閰嶇疆鍏ㄥ眬Settings ---
Settings.llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")
Settings.chunk_size = 512
Settings.chunk_overlap = 50

# --- 1. 鏂囨。鍔犺浇 ---

# 鏂瑰紡A: 浠庣洰褰曞姞杞?# data/docs/ 涓嬫斁 .txt, .md, .pdf 鏂囦欢
if os.path.exists("data/docs"):
    reader = SimpleDirectoryReader(
        input_dir="data/docs",
        required_exts=[".txt", ".md", ".pdf"],
        recursive=True,
    )
    documents = reader.load_data()
    print(f"鍔犺浇浜?{len(documents)} 涓枃妗?)
else:
    # 鏂瑰紡B: 鎵嬪姩鍒涘缓鏂囨。
    documents = [
        Document(
            text="""
            Python鏄竴绉嶉珮绾х紪绋嬭瑷€锛岀敱Guido van Rossum浜?991骞村垱寤恒€?            Python鏀寔澶氱缂栫▼鑼冨紡锛氶潰鍚戝璞°€佸懡浠ゅ紡銆佸嚱鏁板紡鍜岃繃绋嬪紡銆?            Python鐨勫摬瀛﹀己璋冧唬鐮佺殑鍙鎬у拰绠€娲佹€с€?            Python骞挎硾搴旂敤浜嶹eb寮€鍙戙€佹暟鎹瀛︺€佷汉宸ユ櫤鑳姐€佽嚜鍔ㄥ寲绛夐鍩熴€?            Python鐨勫寘绠＄悊宸ュ叿pip浣垮緱瀹夎绗笁鏂瑰簱闈炲父鏂逛究銆?            Python鐨勮櫄鎷熺幆澧僾env鍙互鍒涘缓闅旂鐨勪緷璧栫幆澧冦€?            """,
            metadata={"source": "python_intro.md", "author": "tutorial"},
        ),
        Document(
            text="""
            FastAPI鏄竴涓幇浠ｇ殑銆佸揩閫熺殑Web妗嗘灦锛岀敤浜庢瀯寤篜ython API銆?            FastAPI鍩轰簬鏍囧噯Python绫诲瀷鎻愮ず锛岃嚜鍔ㄧ敓鎴愪氦浜掑紡鏂囨。銆?            FastAPI鍘熺敓鏀寔寮傛缂栫▼锛屾€ц兘鎺ヨ繎Node.js鍜孏o銆?            FastAPI浣跨敤Pydantic杩涜鏁版嵁鏍￠獙鍜屽簭鍒楀寲銆?            FastAPI鏀寔WebSocket銆丟raphQL绛夐珮绾х壒鎬с€?            """,
            metadata={"source": "fastapi_intro.md", "author": "tutorial"},
        ),
    ]
    print(f"鍒涘缓浜?{len(documents)} 涓ず渚嬫枃妗?)

# --- 2. 鏂囨湰鍒嗗潡 ---
splitter = SentenceSplitter(
    chunk_size=512,
    chunk_overlap=50,
    separator="銆俓n",  # 涓枃鍒嗛殧绗?)

nodes = splitter.get_nodes_from_documents(documents)
print(f"\n鍒嗗潡鍚庡緱鍒?{len(nodes)} 涓妭鐐?)
for i, node in enumerate(nodes[:3]):
    print(f"\n--- 鑺傜偣 {i} ---")
    print(f"鏂囨湰: {node.text[:100]}...")
    print(f"鍏冩暟鎹? {node.metadata}")

# --- 3. 鍚戦噺绱㈠紩锛圕hromaDB锛?---

# 鍒濆鍖朇hromaDB
db = chromadb.PersistentClient(path="./data/chroma")
chroma_collection = db.get_or_create_collection("ai_native_dev")

# 鍒涘缓鍚戦噺瀛樺偍
vector_store = ChromaVectorStore(chroma_collection=chroma_collection)
storage_context = StorageContext.from_defaults(vector_store=vector_store)

# 鏋勫缓绱㈠紩
index = VectorStoreIndex(
    nodes=nodes,
    storage_context=storage_context,
    show_progress=True,
)

print("\n绱㈠紩鏋勫缓瀹屾垚锛?)

# --- 4. 鎸佷箙鍖?---
# ChromaDB鑷姩鎸佷箙鍖栧埌鎸囧畾璺緞
# 涓嬫鍙洿鎺ュ姞杞?
# vector_store = ChromaVectorStore(chroma_collection=chroma_collection)
# index = VectorStoreIndex.from_vector_store(vector_store)
```

### 5.3.2 鏌ヨ寮曟搸涓嶳AG

```python
# python4javaProgrammer/ai_demo/llamaindex_query.py
"""LlamaIndex 鏌ヨ寮曟搸涓嶳AG娴佺▼"""
from llama_index.core import (
    VectorStoreIndex,
    StorageContext,
    Settings,
    Response,
)
from llama_index.core.query_engine import (
    RetrieverQueryEngine,
    SubQuestionQueryEngine,
)
from llama_index.core.retrievers import VectorIndexRetriever
from llama_index.core.postprocessor import SimilarityPostprocessor
from llama_index.core.response_synthesizers import ResponseMode
from llama_index.vector_stores.chroma import ChromaVectorStore
from llama_index.embeddings.openai import OpenAIEmbedding
from langchain_openai import ChatOpenAI
import chromadb
from dotenv import load_dotenv

load_dotenv()

# --- 閰嶇疆 ---
Settings.llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# --- 鍔犺浇宸叉湁绱㈠紩 ---
db = chromadb.PersistentClient(path="./data/chroma")
chroma_collection = db.get_or_create_collection("ai_native_dev")
vector_store = ChromaVectorStore(chroma_collection=chroma_collection)
index = VectorStoreIndex.from_vector_store(vector_store)

# --- 1. 鍩虹鏌ヨ寮曟搸 ---
query_engine = index.as_query_engine(
    similarity_top_k=3,                    # 妫€绱op-3鐩稿叧鏂囨。
    response_mode=ResponseMode.COMPACT,   # 鍝嶅簲妯″紡
)

print("=== 鍩虹鏌ヨ ===")
response = query_engine.query("Python鏈夊摢浜涘簲鐢ㄩ鍩燂紵")
print(f"鍥炵瓟: {response}")
print(f"鏉ユ簮: {[n.metadata for n in response.source_nodes]}")

# --- 2. 鑷畾涔夋绱㈠櫒 ---
retriever = VectorIndexRetriever(
    index=index,
    similarity_top_k=5,
)

# 鐩镐技搴﹁繃婊ゅ櫒锛堝彧淇濈暀鐩稿叧搴?0.7鐨勶級
postprocessor = SimilarityPostprocessor(similarity_cutoff=0.5)

# 鍝嶅簲鍚堟垚鍣?from llama_index.core.response_synthesizers import CompactAndRefine

custom_engine = RetrieverQueryEngine(
    retriever=retriever,
    node_postprocessors=[postprocessor],
)

print("\n=== 鑷畾涔夋绱?===")
response = custom_engine.query("FastAPI鐨勭壒鎬ф槸浠€涔堬紵")
print(f"鍥炵瓟: {response}")
print(f"妫€绱㈠埌 {len(response.source_nodes)} 涓浉鍏虫枃妗?)

# --- 3. 妫€绱?+ 鐢熸垚 鍒嗘鎵ц ---
print("\n=== 鍒嗘鎵ц ===")

# 姝ラA: 鍙绱?retrieved_nodes = retriever.retrieve("Python鐨勫寘绠＄悊宸ュ叿")
print(f"妫€绱㈠埌 {len(retrieved_nodes)} 涓妭鐐?")
for i, node in enumerate(retrieved_nodes):
    print(f"  [{i}] score={node.score:.4f} text={node.text[:50]}...")

# 姝ラB: 鍙敓鎴愶紙缁欏畾涓婁笅鏂囷級
from llama_index.core.response_synthesizers import Generation

synthesizer = CompactAndRefine()
response = synthesizer.synthesize(
    query="Python鐨勫寘绠＄悊宸ュ叿鏄粈涔堬紵",
    nodes=retrieved_nodes,
)
print(f"鍥炵瓟: {response}")

# --- 4. 娴佸紡鏌ヨ ---
print("\n=== 娴佸紡鏌ヨ ===")
streaming_engine = index.as_query_engine(
    similarity_top_k=3,
    streaming=True,
)
response_stream = streaming_engine.query("浠嬬粛Python鐨勭紪绋嬭寖寮?)
for text in response_stream.response_gen:
    print(text, end="", flush=True)
print()

# --- 5. 鑱婂ぉ寮曟搸锛堝杞璇濓級 ---
print("\n=== 鑱婂ぉ寮曟搸 ===")
chat_engine = index.as_chat_engine(
    chat_mode="context",  # context妯″紡: 淇濇寔妫€绱?+ 鍘嗗彶
    similarity_top_k=3,
)

# 绗竴杞?response = chat_engine.chat("Python鏄皝鍒涘缓鐨勶紵")
print(f"Q: Python鏄皝鍒涘缓鐨勶紵")
print(f"A: {response}")

# 绗簩杞紙鏈変笂涓嬫枃锛?response = chat_engine.chat("浠栨槸鍝竴骞村垱寤虹殑锛?)
print(f"Q: 浠栨槸鍝竴骞村垱寤虹殑锛?)
print(f"A: {response}")

# 绗笁杞?response = chat_engine.chat("杩欎釜璇█鏈変粈涔堢壒鐐癸紵")
print(f"Q: 杩欎釜璇█鏈変粈涔堢壒鐐癸紵")
print(f"A: {response}")

# 閲嶇疆鑱婂ぉ
chat_engine.reset()
```

---

## 鏈珷灏忕粨

| 妗嗘灦 | 鏍稿績瀹氫綅 | 鍏抽敭姒傚康 | 浣跨敤鍦烘櫙 |
|------|---------|---------|---------|
| LangChain | 宸ュ叿涓庨摼 | LCEL閾俱€丳rompt妯℃澘銆佽緭鍑鸿В鏋愬櫒銆丄gent | 鏋勫缓宸ュ叿璋冪敤銆佽嚜瀹氫箟娴佺▼ |
| LangGraph | 鐘舵€佺紪鎺?| StateGraph銆佹潯浠惰竟銆佸杞璇濄€丠uman-in-loop | 澶嶆潅宸ヤ綔娴併€佺姸鎬佹満 |
| LlamaIndex | RAG绱㈠紩 | 鏂囨。鍔犺浇銆佸垎鍧椼€佸悜閲忕储寮曘€佹煡璇㈠紩鎿?| 鏂囨。闂瓟銆佺煡璇嗗簱妫€绱?|

涓夋鏋跺崗鍚屾ā寮忥細
- **LlamaIndex** 璐熻矗鏂囨。鍔犺浇銆佸垎鍧椼€佸悜閲忓寲銆佹绱?- **LangChain** 鎻愪緵Prompt妯℃澘銆佽緭鍑鸿В鏋愬櫒銆佽嚜瀹氫箟宸ュ叿
- **LangGraph** 缂栨帓鏁翠綋娴佺▼锛堟绱⑩啋鐢熸垚鈫掑鏍糕啋杈撳嚭锛?

# 绗叚绔狅細瀹屾暣椤圭洰 鈥斺€?AI鍔╂墜API

> 涓夋鏋跺崗鍚岀殑鍙繍琛岄」鐩細鏂囨。涓婁紶 + RAG闂瓟 + 澶氳疆Agent瀵硅瘽

---

## 6.1 瀹屾暣鐩綍缁撴瀯

```
ai-native-dev/
鈹溾攢鈹€ pyproject.toml
鈹溾攢鈹€ .env.example
鈹溾攢鈹€ .gitignore
鈹溾攢鈹€ Dockerfile
鈹溾攢鈹€ docker-compose.yml
鈹溾攢鈹€ Makefile
鈹溾攢鈹€ README.md
鈹?鈹溾攢鈹€ src/
鈹?  鈹斺攢鈹€ ai_native_dev/
鈹?      鈹溾攢鈹€ __init__.py
鈹?      鈹溾攢鈹€ main.py                 # 搴旂敤鍏ュ彛
鈹?      鈹溾攢鈹€ config.py               # 閰嶇疆鍏ュ彛
鈹?      鈹?鈹?      鈹溾攢鈹€ core/
鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ settings.py         # Pydantic Settings
鈹?      鈹?  鈹溾攢鈹€ logging.py          # loguru閰嶇疆
鈹?      鈹?  鈹斺攢鈹€ exceptions.py       # 寮傚父浣撶郴
鈹?      鈹?鈹?      鈹溾攢鈹€ api/
鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ deps.py             # 渚濊禆娉ㄥ叆
鈹?      鈹?  鈹溾攢鈹€ middleware.py       # 涓棿浠?鈹?      鈹?  鈹斺攢鈹€ routes/
鈹?      鈹?      鈹溾攢鈹€ __init__.py
鈹?      鈹?      鈹溾攢鈹€ health.py
鈹?      鈹?      鈹溾攢鈹€ chat.py
鈹?      鈹?      鈹斺攢鈹€ documents.py
鈹?      鈹?鈹?      鈹溾攢鈹€ models/
鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ base.py
鈹?      鈹?  鈹溾攢鈹€ chat.py
鈹?      鈹?  鈹斺攢鈹€ document.py
鈹?      鈹?鈹?      鈹溾攢鈹€ services/
鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ rag_service.py      # RAG鏈嶅姟锛圠lamaIndex锛?鈹?      鈹?  鈹溾攢鈹€ agent_service.py    # Agent鏈嶅姟锛圠angGraph锛?鈹?      鈹?  鈹斺攢鈹€ document_service.py # 鏂囨。鏈嶅姟
鈹?      鈹?鈹?      鈹溾攢鈹€ ai/
鈹?      鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹溾攢鈹€ chains/
鈹?      鈹?  鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹?  鈹斺攢鈹€ rag_chain.py    # LangChain RAG閾?鈹?      鈹?  鈹溾攢鈹€ graphs/
鈹?      鈹?  鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹?  鈹斺攢鈹€ agent_graph.py  # LangGraph缂栨帓
鈹?      鈹?  鈹溾攢鈹€ indexes/
鈹?      鈹?  鈹?  鈹溾攢鈹€ __init__.py
鈹?      鈹?  鈹?  鈹斺攢鈹€ vector_index.py # LlamaIndex鍚戦噺绱㈠紩
鈹?      鈹?  鈹斺攢鈹€ tools/
鈹?      鈹?      鈹溾攢鈹€ __init__.py
鈹?      鈹?      鈹斺攢鈹€ custom_tools.py # 鑷畾涔夊伐鍏?鈹?      鈹?鈹?      鈹斺攢鈹€ utils/
鈹?          鈹溾攢鈹€ __init__.py
鈹?          鈹斺攢鈹€ http_client.py      # httpx灏佽
鈹?鈹溾攢鈹€ tests/
鈹?  鈹溾攢鈹€ __init__.py
鈹?  鈹溾攢鈹€ conftest.py
鈹?  鈹溾攢鈹€ unit/
鈹?  鈹?  鈹溾攢鈹€ __init__.py
鈹?  鈹?  鈹溾攢鈹€ test_models.py
鈹?  鈹?  鈹斺攢鈹€ test_services.py
鈹?  鈹斺攢鈹€ integration/
鈹?      鈹溾攢鈹€ __init__.py
鈹?      鈹斺攢鈹€ test_api.py
鈹?鈹溾攢鈹€ data/
鈹?  鈹溾攢鈹€ uploads/
鈹?  鈹斺攢鈹€ chroma/
鈹?鈹斺攢鈹€ scripts/
    鈹溾攢鈹€ start.sh
    鈹斺攢鈹€ start.ps1
```

---

## 6.2 鏍稿績婧愮爜

### 6.2.1 pyproject.toml

```toml
# pyproject.toml
[project]
name = "ai-native-dev"
version = "0.1.0"
description = "鏂囨。涓婁紶 + RAG闂瓟 + 澶氳疆Agent瀵硅瘽鐨凙I鍔╂墜API"
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
# .env.example 鈥斺€?澶嶅埗涓?env骞跺～鍏ュ疄闄呭€?APP_NAME=AI Native Dev
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

### 6.2.4 鏍稿績妯″潡婧愮爜

```python
# src/ai_native_dev/__init__.py
"""AI Native Dev - Python+AI寮€鍙戝疄鎴樻暀绋嬮」鐩?""
__version__ = "0.1.0"
```

```python
# src/ai_native_dev/config.py
"""缁熶竴閰嶇疆鍏ュ彛"""
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
        super().__init__(f"{resource}涓嶅瓨鍦? {resource_id}", 404, 404)

class ValidationException(AppException):
    def __init__(self, message: str, details: Optional[dict] = None):
        super().__init__(message, 422, 422, details)

class AIServiceException(AppException):
    def __init__(self, message: str, provider: str = "openai"):
        super().__init__(f"AI鏈嶅姟寮傚父({provider}): {message}", 503, 503, {"provider": provider})

async def app_exception_handler(request: Request, exc: AppException) -> JSONResponse:
    from loguru import logger
    logger.error(f"寮傚父: {exc.message} | 璺緞: {request.url.path}")
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
    message: str = Field(..., min_length=1, max_length=10000, description="鐢ㄦ埛娑堟伅")
    session_id: str = Field(..., min_length=1, max_length=100, description="浼氳瘽ID")
    history: list[ChatMessage] = Field(default_factory=list, description="鍘嗗彶娑堟伅")
    temperature: float = Field(default=0.7, ge=0.0, le=2.0)
    stream: bool = False
    use_rag: bool = True

    @field_validator("message")
    @classmethod
    def validate_message(cls, v: str) -> str:
        if not v.strip():
            raise ValueError("娑堟伅涓嶈兘涓虹┖")
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
        logger.info(f"鈫?{request.method} {request.url.path}")
        response = await call_next(request)
        elapsed = (time.perf_counter() - start) * 1000
        logger.info(f"鈫?{request.method} {request.url.path} [{response.status_code}] {elapsed:.2f}ms")
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
            return Response(content='{"detail":"璇锋眰杩囦簬棰戠箒"}', status_code=429, media_type="application/json")
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
        raise ValidationException(f"涓嶆敮鎸佺殑鏂囦欢绫诲瀷: {file.content_type}", {"allowed": allowed})
    content = await file.read()
    if len(content) > settings.max_upload_size:
        raise ValidationException(f"鏂囦欢杩囧ぇ: {len(content)} bytes")
    file_id = str(uuid.uuid4())
    os.makedirs(settings.upload_dir, exist_ok=True)
    file_path = os.path.join(settings.upload_dir, f"{file_id}_{file.filename}")
    with open(file_path, "wb") as f:
        f.write(content)
    doc_id = await doc_service.index_document(file_path=file_path, title=title, description=description, metadata={"file_id": file_id, "filename": file.filename})
    return ResponseBase(message="涓婁紶鎴愬姛", data={"doc_id": doc_id, "file_id": file_id, "filename": file.filename})

@router.delete("/{doc_id}", response_model=ResponseBase)
async def delete_document(doc_id: str, doc_service: DocumentServiceDep):
    if not await doc_service.delete_document(doc_id):
        raise NotFoundException("鏂囨。", doc_id)
    return ResponseBase(message="鍒犻櫎鎴愬姛", data={"doc_id": doc_id})

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
"""RAG鏈嶅姟 鈥斺€?灏佽LlamaIndex妫€绱?""
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
        """妫€绱㈢浉鍏虫枃妗?""
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
            logger.error(f"RAG妫€绱㈠け璐? {e}")
            return RagResult(context="", sources=[])
```

```python
# src/ai_native_dev/services/agent_service.py
"""Agent鏈嶅姟 鈥斺€?灏佽LangGraph缂栨帓"""
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
        """澶氳疆瀵硅瘽"""
        self._ensure_init()
        config = {"configurable": {"thread_id": session_id}}

        from langchain_core.messages import HumanMessage
        input_msg = HumanMessage(content=message)
        if context:
            input_msg = HumanMessage(content=f"鍙傝€冧笂涓嬫枃: {context}\n\n闂: {message}")

        result = self._graph.invoke(
            {"messages": [input_msg], "context": context},
            config=config,
        )
        last = result["messages"][-1]
        answer = last.content if hasattr(last, "content") else str(last)
        return {"answer": answer, "usage": None}

    async def stream_chat(self, message: str, session_id: str, history: list):
        """娴佸紡瀵硅瘽"""
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
"""鏂囨。鏈嶅姟 鈥斺€?鏂囨。绠＄悊"""
import os
from typing import Optional
from loguru import logger
from ..core.settings import settings

class DocumentService:
    def __init__(self):
        self._documents: dict[str, dict] = {}  # 瀹為檯椤圭洰鐢ㄦ暟鎹簱

    async def index_document(self, file_path: str, title: str, description: str = "", metadata: Optional[dict] = None) -> str:
        """绱㈠紩鏂囨。鍒板悜閲忓簱"""
        import uuid
        doc_id = str(uuid.uuid4())

        # 璇诲彇鏂囦欢鍐呭
        with open(file_path, encoding="utf-8", errors="ignore") as f:
            text = f.read()

        # 鐢↙lamaIndex绱㈠紩
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
            logger.info(f"鏂囨。宸茬储寮? {doc_id} ({title})")
        except Exception as e:
            logger.error(f"绱㈠紩鏂囨。澶辫触: {e}")
            # 鍗充娇绱㈠紩澶辫触涔熻繑鍥瀌oc_id锛屽厑璁搁噸璇?
        self._documents[doc_id] = {
            "doc_id": doc_id,
            "title": title,
            "description": description,
            "file_path": file_path,
            "metadata": metadata or {},
        }
        return doc_id

    async def delete_document(self, doc_id: str) -> bool:
        """鍒犻櫎鏂囨。"""
        if doc_id not in self._documents:
            return False
        info = self._documents.pop(doc_id)
        # 鍒犻櫎鏂囦欢
        if os.path.exists(info["file_path"]):
            os.remove(info["file_path"])
        # 瀹為檯椤圭洰杩橀渶浠庡悜閲忓簱鍒犻櫎
        logger.info(f"鏂囨。宸插垹闄? {doc_id}")
        return True

    async def list_documents(self, limit: int = 20, offset: int = 0) -> list[dict]:
        """鏂囨。鍒楄〃"""
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
"""LlamaIndex鍚戦噺绱㈠紩绠＄悊"""
import os
from loguru import logger
from ..core.settings import settings

_index = None

async def init_vector_store():
    """鍒濆鍖栧悜閲忓瓨鍌ㄥ拰绱㈠紩"""
    global _index
    if _index is not None:
        return

    from llama_index.core import Settings as LlamaSettings, VectorStoreIndex, StorageContext, Document
    from llama_index.embeddings.openai import OpenAIEmbedding
    from llama_index.vector_stores.chroma import ChromaVectorStore
    from langchain_openai import ChatOpenAI
    import chromadb

    # 閰嶇疆
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

    # 鍒涘缓绌虹储寮曟垨鍔犺浇宸叉湁绱㈠紩
    _index = VectorStoreIndex.from_vector_store(
        vector_store,
        storage_context=storage_context,
    )
    logger.info("鍚戦噺绱㈠紩鍒濆鍖栧畬鎴?)

def get_index():
    return _index
```

```python
# src/ai_native_dev/ai/chains/__init__.py
```
```python
# src/ai_native_dev/ai/chains/rag_chain.py
"""LangChain RAG閾?鈥斺€?鐢ㄤ簬鐢熸垚鍥炵瓟"""
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough, RunnableLambda
from ..core.settings import settings

def build_rag_chain():
    """鏋勫缓RAG鐢熸垚閾?""
    llm = ChatOpenAI(
        model=settings.openai_model,
        temperature=0.3,
        api_key=settings.openai_api_key,
        base_url=settings.openai_base_url,
    )

    prompt = ChatPromptTemplate.from_messages([
        ("system", """浣犳槸涓€涓煡璇嗗姪鎵嬨€傛牴鎹互涓嬪弬鑰冧俊鎭洖绛旂敤鎴烽棶棰樸€?濡傛灉鍙傝€冧俊鎭笉瓒充互鍥炵瓟锛岃璇存槑骞跺皾璇曞熀浜庝綘鐨勭煡璇嗗洖绛斻€?
鍙傝€冧俊鎭?
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
    """鏋勫缓绠€鍗曞璇濋摼锛堟棤RAG锛?""
    llm = ChatOpenAI(
        model=settings.openai_model,
        temperature=0.7,
        api_key=settings.openai_api_key,
        base_url=settings.openai_base_url,
    )
    prompt = ChatPromptTemplate.from_messages([
        ("system", "浣犳槸Python缂栫▼鍔╂墜锛屽洖绛斿噯纭畝娲併€?),
        ("user", "{question}"),
    ])
    return prompt | llm | StrOutputParser()
```

```python
# src/ai_native_dev/ai/graphs/__init__.py
```
```python
# src/ai_native_dev/ai/graphs/agent_graph.py
"""LangGraph Agent缂栨帓鍥?""
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
    """鏋勫缓Agent缂栨帓鍥?""
    llm = ChatOpenAI(
        model=settings.openai_model,
        temperature=0.3,
        api_key=settings.openai_api_key,
        base_url=settings.openai_base_url,
    )

    rag_chain = build_rag_chain()
    simple_chain = build_simple_chain()

    def route_input(state: AgentState) -> Literal["rag", "simple"]:
        """璺敱锛氭湁涓婁笅鏂囪蛋RAG锛屽惁鍒欒蛋绠€鍗曞璇?""
        if state.get("context"):
            return "rag"
        return "simple"

    def rag_node(state: AgentState) -> dict:
        """RAG鐢熸垚鑺傜偣"""
        last_msg = state["messages"][-1]
        question = last_msg.content if hasattr(last_msg, "content") else str(last_msg)
        context = state.get("context", "")

        answer = rag_chain.invoke({"context": context, "question": question})
        return {
            "answer": answer,
            "messages": [AIMessage(content=answer)],
        }

    def simple_node(state: AgentState) -> dict:
        """绠€鍗曞璇濊妭鐐?""
        last_msg = state["messages"][-1]
        question = last_msg.content if hasattr(last_msg, "content") else str(last_msg)

        answer = simple_chain.invoke({"question": question})
        return {
            "answer": answer,
            "messages": [AIMessage(content=answer)],
        }

    # 鏋勫缓鍥?    workflow = StateGraph(AgentState)
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
"""鑷畾涔塋angChain宸ュ叿"""
from langchain_core.tools import tool
from loguru import logger

@tool
def search_knowledge_base(query: str) -> str:
    """鎼滅储鐭ヨ瘑搴撲腑鐨勭浉鍏虫枃妗?""
    logger.info(f"宸ュ叿璋冪敤: search_knowledge_base({query})")
    # 瀹為檯椤圭洰涓皟鐢≧AG鏈嶅姟
    return f"鐭ヨ瘑搴撴绱㈢粨鏋? 鍏充簬'{query}'鐨勭浉鍏冲唴瀹?.."

@tool
def summarize_document(doc_id: str) -> str:
    """鎬荤粨鎸囧畾鏂囨。"""
    logger.info(f"宸ュ叿璋冪敤: summarize_document({doc_id})")
    return f"鏂囨。{doc_id}鐨勬憳瑕? 杩欐槸涓€浠藉叧浜嶱ython缂栫▼鐨勬妧鏈枃妗?.."
```

```python
# src/ai_native_dev/utils/__init__.py
```
```python
# src/ai_native_dev/utils/http_client.py
"""HTTP瀹㈡埛绔皝瑁咃紙鏇夸唬OpenFeign锛?""
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
"""FastAPI搴旂敤鍏ュ彛"""
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
    logger.info(f"鍚姩 {settings.app_name} (env={settings.app_env})")

    # 纭繚鏁版嵁鐩綍瀛樺湪
    os.makedirs(settings.upload_dir, exist_ok=True)
    os.makedirs(settings.chroma_persist_dir, exist_ok=True)
    os.makedirs("logs", exist_ok=True)

    yield
    logger.info("搴旂敤鍏抽棴")

app = FastAPI(
    title=settings.app_name,
    description="鏂囨。涓婁紶 + RAG闂瓟 + 澶氳疆Agent瀵硅瘽鐨凙I鍔╂墜API",
    version="0.1.0",
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc",
)

app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_credentials=True, allow_methods=["*"], allow_headers=["*"])
app.add_middleware(RequestLoggingMiddleware)
app.add_middleware(RateLimitMiddleware, max_requests=100, window_seconds=60)

app.add_exception_handler(AppException, app_exception_handler)

app.include_router(health.router, tags=["鍋ュ悍妫€鏌?])
app.include_router(chat.router, prefix="/api/v1/chat", tags=["瀵硅瘽"])
app.include_router(documents.router, prefix="/api/v1/documents", tags=["鏂囨。"])

@app.get("/")
async def root():
    return {"message": f"Welcome to {settings.app_name}", "docs": "/docs"}
```

### 6.2.5 娴嬭瘯鏂囦欢

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
        req = ChatRequest(message="浣犲ソ", session_id="s1")
        assert req.message == "浣犲ソ"
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

# 瀹夎uv
COPY --from=ghcr.io/astral-sh/uv:latest /uv /usr/local/bin/uv

# 澶嶅埗渚濊禆鏂囦欢
COPY pyproject.toml uv.lock ./

# 瀹夎渚濊禆锛堜笉瀹夎椤圭洰鏈韩锛?RUN uv sync --frozen --no-install-project

# 澶嶅埗婧愮爜
COPY src/ ./src/
COPY .env.example ./

# 瀹夎椤圭洰
RUN uv sync --frozen

# 鍒涘缓鏁版嵁鐩綍
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

### 6.2.7 鍚姩鑴氭湰

```bash
#!/bin/bash
# scripts/start.sh
set -e

echo "馃殌 鍚姩 AI Native Dev..."

# 妫€鏌?env
if [ ! -f .env ]; then
    echo "鈿狅笍  鏈壘鍒?env鏂囦欢锛屼粠妯℃澘鍒涘缓..."
    cp .env.example .env
    echo "馃摑 璇风紪杈?.env 濉叆 OPENAI_API_KEY"
fi

# 瀹夎渚濊禆
uv sync --extra dev

# 鍒濆鍖栨暟鎹洰褰?mkdir -p data/uploads data/chroma logs

# 鍚姩
echo "鉁?鍚姩鏈嶅姟: http://localhost:8000"
echo "馃摎 API鏂囨。: http://localhost:8000/docs"
uv run uvicorn ai_native_dev.main:app --reload --host 0.0.0.0 --port 8000
```

```powershell
# scripts/start.ps1
Write-Host "馃殌 鍚姩 AI Native Dev..." -ForegroundColor Green

# 妫€鏌?env
if (-not (Test-Path .env)) {
    Write-Host "鈿狅笍  鏈壘鍒?env鏂囦欢锛屼粠妯℃澘鍒涘缓..." -ForegroundColor Yellow
    Copy-Item .env.example .env
    Write-Host "馃摑 璇风紪杈?.env 濉叆 OPENAI_API_KEY" -ForegroundColor Yellow
}

# 瀹夎渚濊禆
uv sync --extra dev

# 鍒濆鍖栫洰褰?New-Item -ItemType Directory -Path data\uploads, data\chroma, logs -Force | Out-Null

# 鍚姩
Write-Host "鉁?鍚姩鏈嶅姟: http://localhost:8000" -ForegroundColor Green
Write-Host "馃摎 API鏂囨。: http://localhost:8000/docs" -ForegroundColor Cyan
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

## 6.3 浜斿垎閽熷揩閫熷惎鍔ㄦ寚鍗?
```bash
# === 绗?姝? 鍏嬮殕/鍒涘缓椤圭洰 ===
mkdir ai-native-dev && cd ai-native-dev
# 灏嗕笂杩版枃浠舵寜鐩綍缁撴瀯鍒涘缓

# === 绗?姝? 瀹夎uv锛堝鏈畨瑁咃級 ===
# Windows PowerShell:
powershell -ExecutionPolicy ByPass -c "irm https://astral.sh/uv/install.ps1 | iex"
# Linux/macOS:
curl -LsSf https://astral.sh/uv/install.sh | sh

# === 绗?姝? 閰嶇疆鐜鍙橀噺 ===
cp .env.example .env
# 缂栬緫 .env锛屽～鍏ヤ綘鐨?OPENAI_API_KEY

# === 绗?姝? 瀹夎渚濊禆 ===
uv sync --extra dev

# === 绗?姝? 鍚姩鏈嶅姟 ===
uv run uvicorn ai_native_dev.main:app --reload --host 0.0.0.0 --port 8000
# 鎴? make run
# 鎴? bash scripts/start.sh

# === 楠岃瘉 ===
# 娴忚鍣ㄦ墦寮€ http://localhost:8000/docs
# 娴嬭瘯鍋ュ悍妫€鏌?
curl http://localhost:8000/health
# 棰勬湡: {"status":"healthy","version":"0.1.0"}

# === 涓婁紶鏂囨。 ===
curl -X POST http://localhost:8000/api/v1/documents/upload \
  -F "file=@README.md" \
  -F "title=椤圭洰璇存槑" \
  -F "description=AI鍔╂墜椤圭洰鏂囨。"

# === 瀵硅瘽锛圧AG闂瓟锛?===
curl -X POST http://localhost:8000/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"杩欎釜椤圭洰鏄仛浠€涔堢殑锛?,"session_id":"demo-001","use_rag":true}'

# === 澶氳疆瀵硅瘽 ===
curl -X POST http://localhost:8000/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"鑳借缁嗚璇村悧锛?,"session_id":"demo-001","use_rag":false}'

# === 杩愯娴嬭瘯 ===
uv run pytest -v

# === Docker鍚姩 ===
docker-compose up --build
```

---

## 6.4 闄勫綍锛欽ava绋嬪簭鍛楶ython甯歌鍥版儜FAQ

### FAQ 1: Python娌℃湁鎺ュ彛(interface)锛屽浣曞疄鐜板鎬侊紵

**绛旓細** Python浣跨敤楦瓙绫诲瀷(Duck Typing)鍜屾娊璞″熀绫?ABC)鏇夸唬鎺ュ彛銆?
```python
# 鏂瑰紡1: 楦瓙绫诲瀷锛堟帹鑽愶紝Pythonic锛?class FileLogger:
    def log(self, msg: str): print(f"[FILE] {msg}")

class ConsoleLogger:
    def log(self, msg: str): print(f"[CONSOLE] {msg}")

def do_something(logger):  # 涓嶅０鏄庣被鍨嬶紝鍙姹傛湁log鏂规硶
    logger.log("doing work")

# 鏂瑰紡2: ABC锛堥渶瑕佸己鍒剁害鏉熸椂锛?from abc import ABC, abstractmethod
class Logger(ABC):
    @abstractmethod
    def log(self, msg: str): pass

class FileLogger(Logger):
    def log(self, msg: str): print(f"[FILE] {msg}")

# 鏂瑰紡3: Protocol锛堢被鍨嬫彁绀哄眰闈紝杩愯鏃朵笉寮哄埗锛?from typing import Protocol
class LoggerProto(Protocol):
    def log(self, msg: str) -> None: ...
```

> **Java绫绘瘮**锛氶腑瀛愮被鍨?鈮?闅愬紡鎺ュ彛锛圙roovy椋庢牸锛夛紝ABC 鈮?abstract class + implements

### FAQ 2: Python鐨?`__init__.py` 鏄粈涔堬紵涓轰粈涔堥渶瑕佸畠锛?
**绛旓細** `__init__.py` 鏍囪瘑涓€涓洰褰曟槸Python鍖?package)銆侾ython 3.3+ 鏀寔鍛藉悕绌洪棿鍖?鏃?`__init__.py`)锛屼絾鏄惧紡鍒涘缓 `__init__.py` 浠嶆槸鏈€浣冲疄璺碉細
- 鏍囪瘑鍖呰竟鐣?- 鎺у埗瀵煎嚭鍐呭 (`__all__`)
- 鎵ц鍖呯骇鍒濆鍖栦唬鐮?
> **Java绫绘瘮**锛歅ython鍖?鈮?Java鍖?package)锛屼絾Python鐢ㄦ枃浠剁郴缁熺洰褰曡€岄潪package澹版槑

### FAQ 3: Python鐨?`self` 鏄惧紡浼犲叆寰堝鎬紝涓轰粈涔堜笉鑷姩澶勭悊锛?
**绛旓細** Python鐨勮璁″摬瀛︽槸"Explicit is better than implicit"(鏄惧紡浼樹簬闅愬紡)銆俙self` 鏄惧紡浼犲叆鐨勫ソ澶勶細
- 鏂规硶涓庡嚱鏁扮粺涓€锛堟柟娉曞氨鏄涓€涓弬鏁颁负瀹炰緥鐨勫嚱鏁帮級
- 鍙湪鏂规硶鍐呮槑纭尯鍒嗗疄渚嬪彉閲忓拰灞€閮ㄥ彉閲?- 鏀寔绫昏楗板櫒鍜屽厓绫荤紪绋?
```python
class MyClass:
    def method(self, x):
        self.x = x  # 鏄庣‘鐭ラ亾鏄疄渚嬪彉閲?
# 瀹為檯涓?Python 鍦ㄨ皟鐢ㄦ椂鑷姩浼犲叆self
obj = MyClass()
obj.method(42)  # 绛変环浜?MyClass.method(obj, 42)
```

> **Java绫绘瘮**锛歚self` 鈮?`this`锛屼絾Python瑕佹眰鍦ㄥ弬鏁板垪琛ㄤ腑鏄惧紡澹版槑

### FAQ 4: Python娌℃湁閲嶈浇(overloading)锛屽悓鍚嶆柟娉曞彧鑳藉畾涔変竴涓€庝箞鍔烇紵

**绛旓細** Python涓嶆敮鎸佹柟娉曢噸杞斤紙鍚庡畾涔夎鐩栧厛瀹氫箟锛夛紝浣嗘湁澶氱鏇夸唬鏂规锛?
```python
# 鏂瑰紡1: 榛樿鍙傛暟 + 鍙€夊弬鏁?def process(self, data: str, mode: str = "default", timeout: int | None = None):
    ...

# 鏂瑰紡2: *args + **kwargs锛堢伒娲讳絾鐗虹壊绫诲瀷瀹夊叏锛?def process(self, *args, **kwargs):
    if len(args) == 1: ...
    elif len(args) == 2: ...

# 鏂瑰紡3: functools.singledispatch锛堟寜鍙傛暟绫诲瀷鍒嗗彂锛?from functools import singledispatch

@singledispatch
def process(data):
    raise TypeError(f"涓嶆敮鎸佺殑绫诲瀷: {type(data)}")

@process.register
def _(data: str): return data.upper()
@process.register
def _(data: list): return [x * 2 for x in data]
@process.register
def _(data: dict): return {k: str(v) for k, v in data.items()}

# 鏂瑰紡4: 澶氫釜涓嶅悓鍚嶇О鐨勬柟娉曪紙鏈€绠€鍗曠洿鎺ワ級
def process_text(self, text: str): ...
def process_file(self, file_path: str): ...
```

> **Java绫绘瘮**锛歴ingledispatch 鈮?閲嶈浇鐨勮繍琛屾椂鐗堬紝浣哖ython鐢ㄥ嚱鏁拌€岄潪鏂规硶绛惧悕鍖哄垎

### FAQ 5: Python鐨凣IL鏄粈涔堬紵澶氱嚎绋嬬湡鐨勬病鐢ㄥ悧锛?
**绛旓細** GIL (Global Interpreter Lock) 鏄疌Python鐨勫叏灞€瑙ｉ噴鍣ㄩ攣锛屽悓涓€鏃跺埢鍙湁涓€涓嚎绋嬫墽琛孭ython瀛楄妭鐮併€?
**鍏抽敭鍖哄埆锛?*
- **CPU瀵嗛泦鍨嬩换鍔?*锛氬绾跨▼鍙桮IL闄愬埗锛屽簲浣跨敤 `multiprocessing` 鎴朇鎵╁睍
- **IO瀵嗛泦鍨嬩换鍔?*锛氬绾跨▼/async鏈夋晥锛屽洜涓篒O绛夊緟鏃堕噴鏀綠IL

```python
# CPU瀵嗛泦鍨?鈫?鐢ㄥ杩涚▼
from multiprocessing import Pool
def cpu_task(n): return sum(i*i for i in range(n))
with Pool(4) as p:
    results = p.map(cpu_task, [10**7, 10**7, 10**7, 10**7])

# IO瀵嗛泦鍨?鈫?鐢╝sync/await锛堟渶浣筹級鎴栧绾跨▼
import asyncio, aiohttp
async def fetch(url):
    async with aiohttp.ClientSession() as s:
        async with s.get(url) as r:
            return await r.text()
# 骞跺彂100涓姹傚畬鍏ㄦ病闂

# 娣峰悎鍨?鈫?杩涚▼姹?+ 鍗忕▼
```

> **Java绫绘瘮**锛欸IL浣垮緱Python澶氱嚎绋嬩笉濡侸ava澶氱嚎绋嬪己澶э紝浣哸sync/await鍦↖O鍦烘櫙涓嬫€ц兘鏇翠紭銆侸ava娌℃湁GIL锛屼絾鏈夌嚎绋嬪畨鍏ㄥ紑閿€銆?
---

## 鍏ㄦ暀绋嬫€荤粨

| 绔犺妭 | 鏍稿績鍐呭 | 鍏抽敭宸ュ叿/妗嗘灦 |
|------|---------|-------------|
| 绗竴绔?| Python璇硶绮捐 | 绫诲瀷鎻愮ず銆佹帹瀵煎紡銆佽楗板櫒銆佷笂涓嬫枃绠＄悊鍣ㄣ€佺敓鎴愬櫒銆乤sync/await |
| 绗簩绔?| 宸ョ▼鍖栫幆澧?| pyenv銆乿env銆乽v銆乸yproject.toml |
| 绗笁绔?| 椤圭洰缁撴瀯涓庤鑼?| PEP 8鐩綍銆乴oguru銆乸ytest銆乺uff銆乵ypy |
| 绗洓绔?| FastAPI Web妗嗘灦 | 鍒嗗眰鏋舵瀯銆丳ydantic銆丏I銆佷腑闂翠欢銆佸紓甯稿鐞嗐€乭ttpx |
| 绗簲绔?| AI涓夊ぇ妗嗘灦 | LangChain锛堥摼/宸ュ叿/Agent锛夈€丩angGraph锛堢姸鎬佺紪鎺掞級銆丩lamaIndex锛圧AG锛?|
| 绗叚绔?| 瀹屾暣椤圭洰 | 涓夋鏋跺崗鍚?+ Docker + 娴嬭瘯 + 鍚姩鑴氭湰 |

**瀛︿範璺緞寤鸿锛?*
1. 鍏堣窇閫氱涓€绔犵殑璇硶绀轰緥锛屽缓绔婸ython鑲岃倝璁板繂
2. 鎸夌浜岀珷鎼缓鐜锛岀敤绗笁绔犺鑼冪粍缁囦唬鐮?3. 绗洓绔燜astAPI鏄疻eb灞傚熀纭€锛屽姟蹇呯啛缁?4. 绗簲绔犱笁妗嗘灦鏄疉I寮€鍙戞牳蹇冿紝寤鸿姣忎釜妗嗘灦鐙珛缁冧範鍚庡啀鍋氬崗鍚?5. 绗叚绔犲畬鏁撮」鐩槸鏈€缁堜氦浠樼墿锛屽彲浣滀负鍚庣画椤圭洰鐨勮剼鎵嬫灦
