# 第五章：AI 三大框架实战

> 这一章不堆代码。我们用**一个贯穿全章的案例**「技术文档助手」串起 LangChain → Agent → LangGraph → LlamaIndex 四个框架，每讲一个框架都回答三个问题：**它解决什么/代码为什么这样写/Java 里对应什么**。
>
> 配套项目在 `python4javaProgrammer/ai_demo/`，四个文件对应四节：
>
> | 文件 | 对应节 | 讲什么 |
> |------|--------|--------|
> | `llm_factory.py` | 5.1 | 模型工厂：一处切换真模型/假模型 |
> | `01_langchain_basics.py` | 5.2 | LangChain：从一次调用到一条链 |
> | `02_langchain_agent.py` | 5.3 | Tool 与 Agent：让模型「动手」 |
> | `03_langgraph_flow.py` | 5.4 | LangGraph：流程必须可控时用状态机 |
> | `04_llamaindex_rag.py` | 5.5 | LlamaIndex / RAG：让回答有出处 |
>
> **所有示例零 API Key 也能跑**——靠 `llm_factory.py` 切换离线假模型。配好 Key 后业务代码一行不改即可切真模型。

---

## 5.1 环境准备：让所有示例零 Key 也能跑

### 5.1.1 为什么需要一个「模型工厂」

AI 框架的示例，最大的学习门槛是：**它要联网、要 API Key、输出还不固定**。这三点会直接劝退初学者：

- 没 Key → 代码跑不起来 → 只能"读"，读不懂；
- 有 Key → 输出每次都不一样 → 你没法验证"这一节讲的解析到底生效没有"；
- 网络受限 → 半路报 SSL 错误 → 注意力全被环境问题吸走。

**解法**：把所有"拿模型"的动作收口到一个函数里。`ai_demo/llm_factory.py` 就是干这个的：

```python
def has_real_key() -> bool:
    """判断是否配置了真实 Key。"""
    return bool(os.getenv("DEEPSEEK_API_KEY") or os.getenv("OPENAI_API_KEY"))


def build_chat_model(
    *,
    model: str = "deepseek-chat",
    temperature: float = 0.0,
    streaming: bool = False,
    scripted: list[str] | None = None,
) -> BaseChatModel:
    """构建聊天模型：有 Key 用真的，没 Key 用假的。"""
    if has_real_key():
        from langchain_openai import ChatOpenAI
        base_url = os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com")
        api_key = os.getenv("DEEPSEEK_API_KEY") or os.getenv("OPENAI_API_KEY")
        return ChatOpenAI(
            model=model, temperature=temperature, streaming=streaming,
            api_key=api_key, base_url=base_url, timeout=30,
        )

    print("  [离线模式] 未检测到 DEEPSEEK_API_KEY，使用脚本化假模型")

    class _StreamingFake(FakeListChatModel):
        """把整段回复切成小块吐出来，模拟真模型的逐 token 流式。"""
        def stream(self, *args, **kwargs) -> Iterator[AIMessageChunk]:
            text = self._call(args[0] if args else [])
            for i in range(0, len(text), 4):      # 每 4 个字一块
                yield AIMessageChunk(content=text[i : i + 4])

    return _StreamingFake(responses=list(scripted or SCRIPTED_REPLIES))
```

**要点：**

- 离线时返回的是 `FakeListChatModel` 的子类——它**按调用顺序**依次吐出预设台词。这不是作弊，是让"数据怎么流动""解析是否生效""状态机怎么跳转"这些**真正要学的部分**能在没有网络的情况下被验证。
- 真实模型走 `ChatOpenAI`：DeepSeek、OpenAI、通义、智谱的 **OpenAI 兼容接口**底层都是同一个类，区别只在 `base_url` 和 Key。这一点务必记住——国内大模型几乎都提供 OpenAI 兼容模式，接入成本极低。
- **Java 类比**：这就是 Spring 的 `@Bean` + `@Profile`——开发环境注入 Mock 实现，生产注入真实实现，业务代码只依赖接口 `BaseChatModel`。

### 5.1.2 为什么还需要第二个假模型（`build_scripted_model`）

`FakeListChatModel` 只能返回**字符串**。但 Agent 的"思考"是"我要调用 `get_task` 工具，参数是 `T-1024`"——这是一条带 `tool_calls` 字段的 `AIMessage`，字符串装不下。

所以工厂里还有一个能返回完整消息序列的假模型：

```python
class ScriptedChatModel(BaseChatModel):
    """按脚本返回完整消息（不只是字符串）的假模型。

    能返回带 tool_calls 的 AIMessage，因此可演示
    Agent 的工具调用循环、LangGraph 的条件路由。
    """
    responses: list[AIMessage] = []
    _cursor: int = 0

    @property
    def _llm_type(self) -> str:
        return "scripted-chat-model"

    def _generate(self, messages, stop=None, run_manager=None, **kwargs):
        reply = self.responses[self._cursor] if self._cursor < len(self.responses) \
                else AIMessage(content="（剧本用尽，兜底回复）")
        self._cursor += 1
        return ChatResult(generations=[ChatGeneration(message=reply)])

    def bind_tools(self, tools, **kwargs):
        """Agent 调用它把工具 schema 绑到模型上。假模型不必真绑定，
        但必须实现这个方法，否则 create_agent 会报
        'model does not support tool calling'。"""
        return self


def build_scripted_model(messages: list[AIMessage]) -> ScriptedChatModel:
    return ScriptedChatModel(responses=list(messages))
```

**要点：** 你给一段 `AIMessage` 剧本，模型就照着演。比如想演示"模型决定调工具"，就传一条带 `tool_calls` 的消息：

```python
llm = build_scripted_model([
    AIMessage(content="", tool_calls=[
        {"name": "get_task", "args": {"task_id": "T-1024"}, "id": "call_1"}
    ]),
    AIMessage(content="T-1024 由 alice 负责，截止 2026-09-10。"),
])
```

**Java 类比**：`ScriptedChatModel` 就是给 Service 写的 Stub——按预设剧本返回，让流程逻辑可测试。

### 5.1.3 依赖与运行方式

`ai_demo/` 不需要单独的虚拟环境，复用本教程根目录的 `.venv` 即可。涉及的核心依赖（已装）：

```toml
# 在根目录 pyproject.toml 的 dev 依赖里
dependencies = [
    "langchain>=0.3",
    "langchain-openai",        # ChatOpenAI 在这里
    "langgraph>=0.3",
    "llama-index>=0.12",
    "pydantic>=2",
]
```

运行示例（以第一章为例，其余把文件名换掉即可）：

```bash
cd python4javaProgrammer/ai_demo
../../.venv/Scripts/python.exe 01_langchain_basics.py
```

> 提示：每个示例文件顶部的 `"""docstring"""` 都写了本节要回答的问题，建议先读 docstring 再读代码。

---

## 5.2 LangChain：从一次调用到一条链

`01_langchain_basics.py` 回答一个问题：**为什么不直接 `llm.invoke(prompt)`，而要用框架？**

案例：输入一段技术文本，产出摘要、标签、评分。

### 5.2.1 最小调用：模型的返回不是字符串

很多人以为 `llm.invoke("...")` 返回字符串。跑一下看真实返回：

```python
llm = build_chat_model(scripted=["FastAPI 是基于类型注解的 Python Web 框架。"])
result = llm.invoke("用一句话介绍 FastAPI")

print("返回类型：", type(result).__name__)
print("  .content  →", result.content[:60])
print("  .type     →", result.type)
```

真实输出：

```text
[离线模式] 未检测到 DEEPSEEK_API_KEY，使用脚本化假模型
模型： _StreamingFake（离线假模型）
返回类型： AIMessage
  .content  → FastAPI 是基于类型注解的 Python Web 框架。
  .type     → ai
  完整对象  → AIMessage(content='FastAPI 是基于类型注解的 Python Web 框架。', ...)
```

**要点：**

- 返回的不是 `str`，而是 **`AIMessage`**。这是 LangChain 的核心抽象——对话是「消息序列」，不是「字符串拼接」。多轮对话、工具调用、流式全都建立在这个抽象上。
- **Java 类比**：`AIMessage` 像一个带 `role` 字段的 DTO，而不是返回裸 `String`。

### 5.2.2 Prompt 模板：把「拼字符串」变成「填参数」

```python
prompt = ChatPromptTemplate.from_messages([
    ("system", "你是技术文档分析助手。回答要简洁，不超过 {max_words} 个字。"),
    ("human", "请分析这段文档：\n\n{document}"),
])

messages = prompt.invoke({"max_words": 30, "document": SAMPLE_DOC})
for m in messages.to_messages():
    print(f"  [{m.type:6}] {m.content[:70]}...")

llm = build_chat_model(scripted=["FastAPI：类型注解驱动的现代 Web 框架，自带 OpenAPI 文档。"])
result = (prompt | llm | StrOutputParser()).invoke({"max_words": 30, "document": SAMPLE_DOC})
print("\n链的输出（已经是 str）：", result)
```

真实输出：

```text
[system] 你是技术文档分析助手。回答要简洁，不超过 30 个字。...
[human ] 请分析这段文档：
FastAPI 是一个现代 Python Web 框架。它基于标准类型注解，用 Pydantic 做数据校验，
自动生成...
[离线模式] 未检测到 DEEPSEEK_API_KEY，使用脚本化假模型

链的输出（已经是 str）： FastAPI：类型注解驱动的现代 Web 框架，自带 OpenAPI 文档。
```

**要点：**

- **System 消息定「人格和约束」，Human 消息定「这一轮任务」**。混在一起写，模型对约束的遵从度会明显下降。
- 模板把变量外置，同一个 Prompt 可复用给不同输入——这是可维护性的分水岭。
- **Java 类比**：`ChatPromptTemplate` ≈ `String.format` 的模板 + 角色枚举。

### 5.2.3 LCEL：用 `|` 把组件串成管道

`|` 是这一章最重要的符号。它把"提示 → 模型 → 解析"串成一条链：

```python
chain = prompt | llm | parser

print("链的结构：")
print("  ", chain)
print("各环节入参/出参：")
print("   dict        →  ChatPromptTemplate  →  PromptValue(消息列表)")
print("   PromptValue →  ChatModel           →  AIMessage")
print("   AIMessage   →  StrOutputParser     →  str")
```

真实输出（节选）：

```text
链的结构：
   first=ChatPromptTemplate(...) middle=[_StreamingFake(...)] last=StrOutputParser()

invoke 结果： FastAPI 通过类型注解自动生成接口文档。
类型的 __name__： TextAccessor
isinstance(result, str)： True
result == 那个字符串： True
```

⚠️ **一个容易被吓到的细节**：类型名是 `TextAccessor`，不是 `str`。LangChain 1.x 的 `StrOutputParser` 返回 `str` 的子类（惰性取值器）。别急着查文档——先用两行代码验证它到底能不能当字符串用：

```python
isinstance(result, str)   # → True
result == "..."           # → True
```

结论：**完全兼容 str**，类型名只是内部实现细节。这就是排查第三方库行为的标准动作——**先验证，再查文档**。

**要点（理解 LangChain 的关键）：**

- `` `|` `` 不是语法糖，它调用的是 `Runnable.__or__`，返回一个 `RunnableSequence`。
- 所有组件都实现同一个接口 `Runnable`，都有 `invoke / stream / batch / ainvoke`。→ 链本身也是 `Runnable`，可被继续 `|`，也可被单独测试。
- **Java 类比**：`Runnable` ≈ Java 的 `Function<T,R>`；`a | b` ≈ `a.andThen(b)`。不同在于 LangChain 的每个环节都自带异步、流式、批量的默认实现。

### 5.2.4 结构化输出：别再正则抠字符串了

生产里最危险的写法：让模型"以 JSON 格式回答"，然后自己 `json.loads`——模型会在 JSON 外包 ` ```json ` 标记、加解释，一 loads 就崩。正确做法：

```python
class DocAnalysis(BaseModel):
    """文档分析结果（Pydantic 模型即契约）。"""
    summary: str = Field(description="一句话摘要，不超过 40 字")
    tags: list[str] = Field(description="3 个以内的标签")
    score: int = Field(description="文档信息密度评分，1-10", ge=1, le=10)
    category: Literal["tutorial", "reference", "news"] = Field(description="文档类型")


parser = JsonOutputParser(pydantic_object=DocAnalysis)
prompt = ChatPromptTemplate.from_messages([
    ("system", "你是文档分析助手，严格按格式要求输出。"),
    ("human", "{format_instructions}\n\n文档内容：\n{document}"),
]).partial(format_instructions=parser.get_format_instructions())

chain = prompt | llm | parser
result = chain.invoke({"document": SAMPLE_DOC})

analysis = DocAnalysis.model_validate(result)   # 转成真 Pydantic 对象
```

真实输出：

```text
解析结果类型： dict
解析结果： {'summary': 'FastAPI 是基于类型注解的现代 Web 框架', 'tags': ['Python', 'FastAPI', 'Web'], 'score': 8, 'category': 'tutorial'}

转为 Pydantic 后：
  summary  : FastAPI 是基于类型注解的现代 Web 框架
  tags     : ['Python', 'FastAPI', 'Web']
  score    : 8 （类型 int）
  category : tutorial
```

**要点：**

- `JsonOutputParser` 负责**容错解析**（处理模型不老实的包装），`Pydantic` 负责**类型校验与约束**（`ge/le/Literal`）。两者分工明确。
- `get_format_instructions()` 把 schema 自动写进 Prompt——手写既啰嗦又容易和模型定义不同步。
- **Java 类比**：这就是 Jackson 的 `ObjectMapper.readValue`，只不过输入是不可靠的自然语言。

### 5.2.5 并行分支与自定义步骤

一条链里可以有"不需要模型"的纯计算步骤，也可以用并行省时间：

```python
summary_chain = (ChatPromptTemplate.from_messages([("human", "用一句话概括：{document}")])
                 | build_chat_model(scripted=["FastAPI 是现代 Python Web 框架。"]) | StrOutputParser())
keyword_chain = (ChatPromptTemplate.from_messages([("human", "提取 3 个关键词：{document}")])
                 | build_chat_model(scripted=["Python, FastAPI, OpenAPI"]) | StrOutputParser())

def count_chars(text: str) -> int:
    return len(text)

combined = RunnableParallel(
    summary=summary_chain,
    keywords=keyword_chain,
    raw_length=RunnableLambda(lambda x: count_chars(x["document"])),   # 纯函数进管道
)
result = combined.invoke({"document": SAMPLE_DOC})
```

真实输出：

```text
并行结果（dict）：
  summary      = FastAPI 是现代 Python Web 框架。
  keywords     = Python, FastAPI, OpenAPI
  raw_length   = 99

原文长度：99 字（由 RunnableLambda 算出，没调模型）
```

**要点：**

- `RunnableParallel` 的多个分支**并发**执行（内部线程池/asyncio）。两次独立调用模型 → 总耗时接近一次，不是两次。
- `RunnableLambda` 让"不需要模型的加工步骤"也能进管道，保持整条链调用方式统一。
- **Java 类比**：`RunnableParallel` ≈ `CompletableFuture.allOf`，但是**声明式**的——你描述"要什么"，不是"怎么并发"。

### 5.2.6 流式与批量

```python
chain = ChatPromptTemplate.from_messages([("human", "{q}")]) | llm | StrOutputParser()
print("流式：")
for chunk in chain.stream({"q": "介绍 FastAPI"}):
    print(chunk, end="|", flush=True)
print("\n  → 共收到", len(collected), "个 chunk；拼接后：", "".join(collected))

batch_result = chain.batch([{"q": "问题1"}, {"q": "问题2"}, {"q": "问题3"}])
```

真实输出：

```text
流式（每个 chunk 立刻可用）：
Fast|API |用类型注|解驱动校|验与文档|生成，性|能好、上|手快。|
  → 共收到 8 个 chunk；拼接后： FastAPI 用类型注解驱动校验与文档生成，性能好、上手快。

批量（一次调用处理多个输入）：
  1. 回复A
  2. 回复B
  3. 回复C
```

**要点：**

- 流式不是为了炫技，是为了**首字延迟**。用户等 3 秒看到第一个字 vs 等 3 秒看到全部，体验天差地别。
- `batch()` 内部并发，比 `for` 循环 `invoke` 快得多（尤其模型有并发额度时）。
- 这三个方法（`invoke / stream / batch`）**每条链都自带**，你不用写任何并发代码——这就是统一 `Runnable` 接口的价值。

> **小结（5.2）**：LangChain 解决的是「单次调用的工程化」。
> ✅ 擅长：固定流程、可复用、结构化输出、并发/流式
> ❌ 不擅长：需要根据中间结果**动态改变流程**——那是 LangGraph 的活（5.4）

---

## 5.3 Tool 与 Agent：让模型能「动手」

`02_langchain_agent.py` 回答：当答案**不在模型脑子里，在公司系统里**（"帮我查 T-1024 谁负责"），怎么办？

### 5.3.1 工具是什么：docstring + 类型注解 = 给模型看的说明书

```python
@tool
def get_task(task_id: str) -> str:
    """查询任务详情，包括负责人、状态、截止日期。

    Args:
        task_id: 任务编号，形如 T-1024
    """
    tasks = {
        "T-1024": {"assignee": "alice", "status": "进行中", "due": "2026-09-10"},
        "T-2048": {"assignee": "bob", "status": "已完成", "due": "2026-08-20"},
    }
    t = tasks.get(task_id)
    if not t:
        return f"未找到任务 {task_id}"
    return f"任务 {task_id}：负责人 {t['assignee']}，状态 {t['status']}，截止 {t['due']}"


TOOLS = [search_docs, get_task, days_until]
print("工具清单：")
for t in TOOLS:
    print(f"  · {t.name:12} 参数={list(t.args.keys())}")
    print(f"    说明：{t.description.splitlines()[0]}")
```

**要点：**

- `@tool` 把普通函数包装成「带元数据的工具」。元数据来自三处：
  - 函数名 → 工具名
  - 类型注解 → 参数 schema（模型照此填参数）
  - docstring → 功能说明（**模型靠它决定要不要调这个工具**）
- **所以 docstring 不是写给同事看的，是写给模型看的**。写「查询任务」不如写「查询任务详情，包括负责人、状态、截止日期」——后者模型调用准确率明显更高。
- **Java 类比**：`@tool` ≈ 给方法加 `@Bean` + 自动生成 OpenAPI schema。区别是"调用方"是模型，不是前端。

### 5.3.2 Agent：模型与工具之间的循环

LangChain 1.x 创建 Agent 的 API 是 `create_agent`（旧版的 `AgentExecutor`、`initialize_agent` 已废弃）：

```python
script = [
    AIMessage(content="", tool_calls=[
        {"name": "search_docs", "args": {"query": "限流配置"}, "id": "call_1"}]),
    AIMessage(content="", tool_calls=[
        {"name": "get_task", "args": {"task_id": "T-1024"}, "id": "call_2"}]),
    AIMessage(content="限流由两个配置项控制（100 次/60 秒）；T-1024 由 alice 负责，截止 2026-09-10。"),
]
llm = build_scripted_model(script)
agent = create_agent(
    model=llm,
    tools=TOOLS,
    system_prompt="你是技术文档助手。需要外部信息时先调用工具，不要凭空猜测。",
)
print("Agent 对象类型：", type(agent).__name__)

result = agent.invoke({"messages": [HumanMessage(content="限流怎么配？顺便查下 T-1024 谁负责")]})
for i, m in enumerate(result["messages"], 1):
    label = type(m).__name__
    if isinstance(m, ToolMessage):
        print(f"  {i}. [{label}] ← 工具返回：{str(m.content)[:50]}")
    elif isinstance(m, AIMessage) and getattr(m, "tool_calls", None):
        calls = ", ".join(f"{c['name']}({c['args']})" for c in m.tool_calls)
        print(f"  {i}. [{label}] → 决定调用：{calls}")
    elif isinstance(m, AIMessage):
        print(f"  {i}. [{label}] 最终回答：{str(m.content)[:60]}")
```

真实输出（节选）：

```text
Agent 对象类型： CompiledStateGraph
（注意：它不是黑盒对象，而是一个 CompiledStateGraph —— 编译好的 LangGraph 图。）

 1. [HumanMessage] 限流怎么配？顺便查下 T-1024 谁负责
 2. [AIMessage] → 决定调用：search_docs(query='限流配置')
 3. [ToolMessage] ← 工具返回：限流通过 RATE_LIMIT_MAX_REQUESTS 与 RATE_LIMIT_WINDOW_SECONDS...
 4. [AIMessage] → 决定调用：get_task(task_id='T-1024')
 5. [ToolMessage] ← 工具返回：任务 T-1024：负责人 alice，状态 进行中，截止 2026-09-10
 6. [AIMessage] 最终回答：限流由两个配置项控制（100 次/60 秒）；T-1024 由 alice 负责...

直接调用工具（不经过模型）：
  get_task.invoke({'task_id': 'T-1024'}) → 任务 T-1024：负责人 alice，状态 进行中，截止 2026-09-10
```

**循环机制（Agent 的核心，务必理解）：**

```
  模型收到：系统提示 + 历史消息 + 工具清单
                    │
       模型输出里有没有 tool_calls？
          ╱                    ╲
       有                      没有
      ╱                          ╲
  执行工具，结果包装成        输出就是最终答案
  ToolMessage 追加历史          循环结束
      ╲                          ╱
       └──────→ 回到顶部 ──────┘
```

- 关键：**"要不要调工具"是模型自己决定的**，框架只负责执行和回填。工具描述写得含糊，模型就会该调不调、不该调乱调。
- 每轮模型输入都含完整历史——模型"记得"上一步查到了什么。
- **Java 类比**：这是一个 `while` 循环，退出条件是"模型不再请求工具"。LangChain 帮你写了这个循环、工具分发、消息回填。

### 5.3.3 结构化输出：把「搜集」和「格式化」拆成两步

`create_agent` 支持 `response_format` 让 Agent 直接返回 Pydantic 对象。但生产里更可控的做法是**拆两步**——Agent 只负责搜集信息，格式化交给独立一条链：

```python
agent = create_agent(model=llm_agent, tools=[get_task], system_prompt="你是任务简报助手。")
result = agent.invoke({"messages": [HumanMessage(content="整理 T-2048 的简报")]})
conversation = result["messages"][-1].content

parser = JsonOutputParser(pydantic_object=TaskBrief)
format_prompt = ChatPromptTemplate.from_messages([
    ("system", "把下面的任务信息整理成 JSON。"),
    ("human", "{format_instructions}\n\n原始信息：\n{info}"),
]).partial(format_instructions=parser.get_format_instructions())

brief: TaskBrief = TaskBrief.model_validate(
    (format_prompt | llm_format | parser).invoke({"info": conversation})
)
print("  task_id   :", brief.task_id)
print("  assignee  :", brief.assignee)
```

**为什么要拆开（而不是 `response_format` 一把梭）：**

- `response_format` 让**同一个模型**既搜集又格式化，任何一环出错都难定位。
- 拆开后：Agent 的输出可人工抽查，格式化链可单独写单测，中间结果能落库、能重放。
- 格式化这一步还能换更小更便宜的模型——它不需要工具能力。
- **Java 类比**：Agent ≈ Service 层，格式化链 ≈ 独立的 Converter。这就是"关注点分离"在 Agent 工程里的具体形态。

### 5.3.4 Agent 的边界：什么时候它不够用

Agent 把"下一步做什么"的决策权**完全交给模型**。这在探索性任务里很灵活，但下面这些场景会失控：

① **流程有硬性规定**——"必须先检索、再生成、最后经合规检查才能发布" → Agent 可能跳步，你拦不住（只能靠 Prompt 恳求它）。
② **需要人工介入**——"生成后暂停，等人审批，批准才继续" → Agent 的循环里没有"暂停等人"这个动作。
③ **需要循环重试且带计数**——"质量不达标就重写，最多 3 次，第 4 次转人工" → 重试次数散落在消息历史里，无法可靠控制。
④ **需要断点续跑 / 长时挂起**——"审批可能要等两天" → Agent 进程不能挂两天。

**共同点**：这些需求都是"流程结构"层面的，而 Agent 的流程结构是不可控的。

**解法**：把流程从"模型自己决定"改成"你用状态机显式定义"——这就是 LangGraph（5.4）。而且注意：LangChain 1.x 的 `create_agent` 返回的**就是一个 `CompiledStateGraph`**——Agent 本身就是用 LangGraph 搭的。所以从 Agent 迁移到 LangGraph，不是换框架，是"把预置图换成自己画的图"。

> **小结（5.3）**：
> ✅ Agent 擅长：开放式任务、工具数量不多、无需严格流程约束
> ❌ Agent 不擅长：硬性流程、人工审批、可控重试、长时挂起
> → 后者交给 LangGraph（5.4）

---

## 5.4 LangGraph：当流程必须可控

`03_langgraph_flow.py` 把 5.3 第四节的需求落成代码：技术文档发布流程。

```
收到草稿需求 → 判断文档类型 → 生成内容 → 人工审批
                                  ↑          │
                                  └── 驳回 ───┤（最多重写 2 次）
                                             ↓ 通过
                                           发布 → 结束
```

四个硬约束，Agent 一个都做不到（见 5.3.4）：① 先分类再生成 ② 生成后暂停等人 ③ 驳回回写且最多 2 次 ④ 审批可能等两天。

### 5.4.1 State：所有节点共享的「白板」

```python
class DocState(TypedDict):
    """文档发布流程的状态。每个节点读取它、往里写、返回增量。"""
    topic: str          # 原始需求
    doc_type: str       # 分类结果：tutorial / reference / news
    draft: str          # 当前草稿
    feedback: str       # 审批意见
    approved: bool      # 是否通过
    rewrite_count: int  # 已重写次数（防死循环的关键）
    published: bool     # 是否已发布
```

真实输出：

```text
状态字段（DocState）：
  · topic          : str
  · doc_type       : str
  · draft          : str
  · feedback       : str
  · approved       : bool
  · rewrite_count  : int
  · published      : bool

要点：
  · 节点函数**不互相调用**。它们只读写这块共享白板。
    这是 LangGraph 和"函数调用链"最本质的区别。
  · 节点返回的是**增量字典**（要改哪些字段），不是完整状态。
```

**要点：**

- 节点**不互相调用**，只读写共享状态。好处：任何时刻都能把整块状态打印/存库/回放——线上排查时你能精确知道"卡在哪一步、当时状态是什么"。
- **Java 类比**：`State` ≈ 一个不可变的 `RequestContext`，每个 Filter 往里塞东西再传给下一个。

### 5.4.2 节点：普通函数，签名 `(state) -> 增量字典`

```python
def classify(state: DocState) -> dict[str, Any]:
    topic = state["topic"]
    llm = build_scripted_model([AIMessage(content="tutorial")])
    doc_type = str(llm.invoke(f"判断类型：{topic}").content).strip()
    return {"doc_type": doc_type}            # 只返回要改的字段


def generate(state: DocState) -> dict[str, Any]:
    feedback = state.get("feedback") or "（首次生成，无反馈）"
    llm = build_scripted_model([AIMessage(content=f"【{state.get('doc_type')}草稿】关于「{state['topic']}」...")])
    # 生成一次，计数加一——这个数字是循环能否退出的唯一依据
    return {"draft": "...", "feedback": "", "rewrite_count": state.get("rewrite_count", 0) + 1}


def human_review(state: DocState) -> dict[str, Any]:
    decision = interrupt({                       # ← 图会在这里暂停
        "question": "请审批以下草稿，输入 approved 或 feedback:你的意见",
        "draft": state["draft"], "attempt": state.get("rewrite_count", 0),
    })
    text = str(decision)
    if text.strip().lower() == "approved":
        return {"approved": True, "feedback": ""}
    return {"approved": False, "feedback": text}
```

**要点：**

- 节点就是函数：输入整个 state，返回要修改的字段。**没有基类要继承，没有装饰器必须加**——所以极易单测（5.4.2 演示里直接 `classify(state)` 脱离图调用）。
- 唯一约定：返回 key 必须在 State 里声明过，否则框架报错——这个"编译期检查"挡掉大量拼写错误。

### 5.4.3 条件边与循环：把规则从模型手里拿回来

```python
def route_after_review(state: DocState) -> Literal["publish", "generate", "end_manual"]:
    """审批之后往哪走——整个流程的策略中枢。注意：这里没有任何模型参与。"""
    if state.get("approved"):
        return "publish"
    if state.get("rewrite_count", 0) >= MAX_ATTEMPTS:
        return "end_manual"          # 次数用尽，转人工兜底
    return "generate"

graph = StateGraph(DocState)
graph.add_node("classify", classify)
graph.add_node("generate", generate)
graph.add_node("human_review", human_review)
graph.add_node("publish", publish)
graph.add_edge(START, "classify")
graph.add_edge("classify", "generate")
graph.add_edge("generate", "human_review")
graph.add_edge("publish", END)
graph.add_conditional_edges("human_review", route_after_review,
    {"publish": "publish", "generate": "generate", "end_manual": END})
```

真实输出：

```text
节点：
  ● __start__   ● classify   ● generate   ● human_review   ● publish   ● __end__

边：
  __start__ ──▶ classify
  classify ──▶ generate
  generate ──▶ human_review
  human_review ──▶ __end__
  human_review ──▶ generate  ← 循环回边
  human_review ──▶ publish
  publish ──▶ __end__

关键观察：
  · generate → human_review → generate 这条回边就是**循环**。在 LCEL 链里你画不出这条边（链是单向 DAG）。
  · 循环的退出条件写在 route_after_review 里：通过→publish；未通过且重写<2次→generate；用尽→end_manual。
  · **这个退出条件是纯代码，不是 Prompt**。模型再怎么"想再试一次"，也突破不了 rewrite_count>=2 这道闸。
```

**要点：**

- `generate → human_review → generate` 这条回边就是可控循环。LCEL 链画不出（链是单向 DAG）。
- 退出条件是**纯代码**，不是 Prompt——这就是"可控"的确切含义。
- **Java 类比**：整张图 ≈ `Spring StateMachine`，条件边 ≈ 状态转移表里的 guard 表达式。

### 5.4.4 interrupt：让流程暂停，等人（可能等两天）

```python
app = build_graph().compile(checkpointer=InMemorySaver())
config = {"configurable": {"thread_id": "doc-2026-0903"}}

result = app.invoke(initial, config=config)          # 跑到 human_review 暂停
interrupts = result.get("__interrupt__") or []
payload = interrupts[0].value
print(f"  第 {payload['attempt']}/{payload['max_attempts']} 稿：", payload["draft"])

result = app.invoke(Command(resume="feedback:需要补充一个中间件顺序的实际示例"), config=config)  # 驳回
result = app.invoke(Command(resume="approved"), config=config)   # 通过
print("  approved  =", result.get("approved"))
print("  published =", result.get("published"))
```

真实输出：

```text
▶ 第 1 次运行（跑到审批节点会自动暂停）：
  第 1/2 稿： 【tutorial草稿】关于「FastAPI 中间件执行顺序」的内容，已参考反馈：（首次生成，无反馈）
  等待人工决策： 请审批以下草稿，输入 approved（通过）或 feedback:你的意见

  注意：invoke 正常返回了，没有抛异常。中断信息在 result["__interrupt__"] 里。

▶ 人工决策（驳回，要求补充示例）
  → 已根据反馈重新生成，第 2/2 稿：...

▶ 人工决策（第 2 次：通过）
  approved  = True
  published = True

机制说明：
  · interrupt(payload) 抛出 GraphInterrupt，框架把**当前完整状态**写入 checkpointer，然后正常返回。
  · 恢复时用 Command(resume=答案) 再次 invoke，框架从 checkpointer 读出状态，从 interrupt 那一行继续往下执行。
  · 因为状态落在 checkpointer 里，两次 invoke 之间可以隔两天、可以在另一进程、甚至另一台机器。
  · thread_id 是关键：它是这条流程实例的身份证，不同用户的审批靠它隔离。
```

**要点：**

- `interrupt(payload)` 让流程在指定节点**暂停并把完整状态落库**，不抛异常。
- 用 `Command(resume=...)` 恢复时，框架从 `interrupt` 那一行继续往下执行，中间可隔任意久。
- **Java 类比**：这是把方法栈帧**序列化存库**再恢复——Java 里做不到（栈帧无法序列化），通常得自己写"状态表 + 步骤枚举"。LangGraph 把这个能力内置了，这是它**最不可替代的价值**。

### 5.4.5 循环上限：怎么保证不死循环

```python
result = app.invoke(initial, config=config)
for i in range(1, MAX_ATTEMPTS + 2):
    print(f"  第 {i} 次审批：驳回")
    result = app.invoke(Command(resume=f"feedback:第 {i} 次修改意见"), config=config)
    count = result.get("rewrite_count", 0)
    interrupted = "__interrupt__" in result
    print(f"     → 已生成 {count} 稿；仍在等待审批={interrupted}")
    if not interrupted:
        print(f"     → 达到上限 {MAX_ATTEMPTS} 稿，走 end_manual 转人工，不再循环")
        break
```

真实输出：

```text
连续驳回 3 次，看 rewrite_count 如何封顶：
  第 1 次审批：驳回
     → 已生成 2 稿；仍在等待审批=True
  第 2 次审批：驳回
     → 已生成 2 稿；仍在等待审批=False
     → 达到上限 2 稿，走 end_manual 转人工，不再循环
  最终：published=False, approved=False
```

**要点：**

- 图里的循环必须有**明确的、能用数字表达的退出条件**。
- 只靠"模型觉得可以了"当退出条件，等于把系统稳定性交给概率。
- 兜底路径（`end_manual`）比死循环或超时好得多：它保证"最坏情况下有人接手"，而不是"最坏情况下卡死"。

> **小结（5.4）**：
> ✅ LangGraph 擅长：固定顺序、条件路由、可控循环、人工中断、断点续跑
> ❌ 不适合：完全没有固定结构的开放式探索（那种用 Agent 更省事）
> 📌 记住：LangChain 1.x 的 `create_agent` 返回的也是 `CompiledStateGraph`——Agent 就是框架预置的一张图，两者不是替代关系，是抽象层级关系

---

## 5.5 LlamaIndex / RAG：让回答有出处

`04_llamaindex_rag.py` 回答：模型不知道公司内部的"限流怎么配"——这些信息在文档里，不在训练数据里。

三种办法：① 全塞进 Prompt（装不下、按 token 计费）② 微调（贵、更新慢、无法溯源）③ **RAG：先检索相关片段，再让模型基于片段回答** ← 本文件讲这个。

RAG 五步流水线：`文档 → ① 加载 → ② 分块 → ③ 向量化 → ④ 存入索引 → ⑤ 检索 + 生成`

> **零依赖说明**：本文件自带一个可解释的哈希 Embedding，不下载任何模型、不联网、不需要 GPU。真实语义模型（bge-m3 等）要下载几百 MB，离线跑不了，且是黑盒——教学时我们用朴素实现把原理讲透。

### 5.5.1 Embedding：朴素哈希实现 + 余弦相似度

```python
class HashingEmbedding(BaseEmbedding):
    """教学用 Embedding：基于字符 n-gram 的哈希投影（hashing trick）。
    原理（真实语义模型也是这个思想，只是把"字面重合"换成"语义重合"）：
      1. 把文本切成 n-gram（这里用 2-gram，即相邻两字）
      2. 每个 n-gram 哈希到向量某维度，该维度 +1
      3. L2 归一化，得到单位向量
      4. 相似度 = 向量点积（余弦相似度）
    """
    embed_dim: int = 128
    ngram: int = 2

    def _embed(self, text: str) -> list[float]:
        cleaned = re.sub(r"[^\w\u4e00-\u9fff]+", "", text.lower())
        vec = [0.0] * self.embed_dim
        grams = [cleaned[i:i+self.ngram] for i in range(len(cleaned)-self.ngram+1)] \
                if len(cleaned) >= self.ngram else [cleaned]
        for g in grams:
            vec[hash(g) % self.embed_dim] += 1.0
        norm = math.sqrt(sum(v*v for v in vec)) or 1.0
        return [v / norm for v in vec]

    def _get_text_embedding(self, text: str) -> list[float]:
        return self._embed(text)
```

真实输出：

```text
向量维度：128（真实模型通常 768 或 1536 维）
非零元素个数：3  ← 稀疏：大部分维度是 0

余弦相似度：
  「限流配置」      vs「限流是怎么配置的」 = 0.4364  ← 语义相近，分数高
  「限流配置」      vs「生产部署规范」      = 0.0000  ← 无关，分数低
  「限流配置」      vs「限流配置」          = 1.0000  ← 与自身必为 1.0
```

**要点：**

- Embedding 本质：把"不可比较的文本"映射成"可计算的向量"，让"相似"变成一个**数值**。有了数值才能排序、设阈值、做混合检索——这是整个 RAG 的地基。
- 打分是**相对的**：0.32 本身没意义，有意义的是"它比其他候选高"。所以别写死阈值，看 Top-K 的相对差距。
- 真实模型能识别"限流"和"速率限制"同义，本实现只能识别字面重合——这是朴素实现与语义模型的差距。

### 5.5.2 分块：为什么必须切，切多大

LlamaIndex 的 `SentenceSplitter` 依赖 NLTK 语料，离线会报 `LookupError`。本文件自己写分块（逻辑就几行，写出来才真正理解 `chunk_size/overlap`）：

```python
def split_text(text: str, chunk_size: int = 120, overlap: int = 20) -> list[str]:
    sentences = re.split(r"(?<=[。！？；])", text)
    sentences = [s for s in sentences if s.strip()]
    chunks: list[str] = []
    current = ""
    for sent in sentences:
        if len(current) + len(sent) <= chunk_size:
            current += sent
        else:
            if current:
                chunks.append(current)
            current = (current[-overlap:] if overlap else "") + sent   # 保留重叠，避免切断语义
    if current:
        chunks.append(current)
    return chunks
```

真实输出：

```text
原文长度：190 字

  chunk_size= 40 → 切成 4 块：
      [1]（ 65字）限流通过 RATE_LIMIT_MAX_REQUESTS 和 RATE_...
      [2]（ 40字）默认值分别是 100 和 60，表示每个客户端 60 秒内最多 100 ...
  chunk_size=120 → 切成 2 块：
      [1]（105字）限流通过 RATE_LIMIT_MAX_REQUESTS 和 RATE_...
      [2]（ 85字）限流逻辑实现在 api/middleware.py 的 RateLimi...
  chunk_size=512 → 切成 1 块：
      [1]（190字）限流通过 RATE_LIMIT_MAX_REQUESTS 和 RATE_...

  overlap 的作用（chunk_size=60）：
    overlap= 0 → 4 块；第2块开头：默认值分别是 100 和 60...
    overlap=20 → 4 块；第2块开头：DOW_SECONDS 两个配...   ← 衔接更自然
```

**要点：**

- 为什么必须分块？① 上下文窗口有限；② 长文本中间会被模型"忽略"（lost in the middle）；③ 按块计费更省。
- 块太小 → 语义不完整；块太大 → 噪声多、相似度被稀释。经验起点：中文 300–500 字，配 10–20% 重叠。**没有万能值，必须用自己的数据测**。

### 5.5.3 建索引与检索：看到「检索到了什么、分数多少」

```python
Settings.embed_model = HashingEmbedding()
index = build_index()                       # 文档→分块→TextNode→索引
print(f"索引中的节点数：{len(index.docstore.docs)}")

retriever = index.as_retriever(similarity_top_k=3)
for query in ["限流是怎么配置的？", "生产环境怎么部署？"]:
    print(f"▶ 查询：{query}")
    for i, n in enumerate(retriever.retrieve(query), 1):
        score = n.score or 0.0
        bar = "█" * int(score * 30)
        print(f"   {i}. [score={score:.4f}] {bar}")
        print(f"      来源：{n.node.metadata.get('title')}")
        print(f"      内容：{n.node.text[:60]}...")
```

真实输出（节选）：

```text
索引中的节点数：8

▶ 查询：限流是怎么配置的？
   1. [score=0.3108] █████████
      来源：限流配置说明
      内容：客户端 60 秒内最多 100 次请求。限流逻辑实现在 api/middleware.py 的 RateLimitMid...
   2. [score=0.2288] ██████
      来源：限流配置说明
      内容：限流通过 RATE_LIMIT_MAX_REQUESTS 和 RATE_LIMIT_WINDOW_SECONDS 两个配...
   3. [score=0.2097] ██████
      来源：错误处理约定
      内容：带 code 与 message 字段。异常处理器统一把业务异常映射为 HTTP 状态码：NotFoundExcepti...

▶ 查询：生产环境怎么部署？
   1. [score=0.2730] ████████
      来源：数据库访问规范
      内容：数据访问通过 Repository 层隔离，业务层只依赖 TaskRepository 接口...
   ...
```

**要点（RAG 调优的起点）：**

- **一定要把检索结果打印出来看**。RAG 答错的绝大多数情况不是"模型不行"，而是"根本没检索到相关块"。
- 排第 1 的块不含答案，后面调 Prompt 都是白费力气。
- 分数看**分布**：0.9/0.2/0.1 说明检索确定；0.35/0.33/0.30 说明基本在随机猜，该换 Embedding 或改分块。
- 生产建议：把 Top-K 和分数记进日志，才能事后定位问题。

### 5.5.4 检索 + 生成：为什么要能拆开

```python
# ① 只检索，不生成
nodes = index.as_retriever(similarity_top_k=2).retrieve(query)
context = "\n\n".join(n.node.text for n in nodes)

# ② 只生成（基于给定上下文）
from llama_index.core import get_response_synthesizer
synthesizer = get_response_synthesizer(response_mode="compact")
response = synthesizer.synthesize(query, nodes)

# ③ 一步到位
qe = index.as_query_engine(similarity_top_k=2)
r = qe.query(query)
```

真实输出（节选）：

```text
【步骤 A】只检索（不调模型，毫秒级、零成本）：
   1. [0.3108] 客户端 60 秒内最多 100 次请求...
   2. [0.2288] 限流通过 RATE_LIMIT_MAX_REQUESTS...

【步骤 B】只生成（把检索到的上下文 + 问题 交给模型）：
   上下文长度：212 字
   模型回答（离线 MockLLM 的返回是固定的）：
    Context information is below.
---------------------
title: 限流配置说明
客户端 60 秒内最多 100 次请求...

【步骤 C】query_engine 一步到位（内部就是 A + B）：
    Context information is below.
...
```

**要点：**

- 检索可缓存、可用 BM25 兜底、可加规则过滤；生成可换模型、加引用标注、加拒答逻辑。拆开写才能分别优化、分别定位问题。
- 关键设计：让模型**只基于给定上下文回答**，上下文缺失时明确说"资料中没有"——这一步 Prompt 决定 RAG 是"减少幻觉"还是"编得更像真的"。

### 5.5.5 RAG 的失败模式（比成功路径更值得记）

```text
① 检索不到 —— 最常见
   症状：答案在文档里，模型却说"资料中没有"。
   改进：调 chunk_size/overlap；换领域适配 Embedding；查询改写；混合检索（向量+BM25）。

② 检索到了但答错
   症状：Top-1 块有答案，模型答成别的（如新旧两版配置矛盾）。
   改进：文档加版本/时间元数据，Prompt 要求"冲突时以较新者为准"。

③ 检索到但没引用 —— 溯源失效
   症状：答案对，但说不出哪份文档。原因：query_engine 一把梭丢掉 source_nodes。
   改进：拆开执行，从 NodeWithScore 取 metadata 做引用。企业场景硬需求：答案必须有出处。

④ 上下文塞满，关键信息被忽略
   症状：Top-K=20，答案在第 15 块，模型仍答错（lost in the middle）。
   改进：K 别贪多（3-5）；检索后做重排（reranker）把最相关的放首尾。

⑤ 把 RAG 当数据库用
   症状：问"一共有多少个任务"，RAG 答非所问。
   改进：精确统计/聚合走工具调用（见 5.3 的 @tool），让模型自己决定检索还是查库。

📌 一句话：RAG 效果上限由检索质量决定，不要一上来换更大模型——先把 Top-K 打印出来看清楚。
```

> **小结（5.5）**：
> RAG 五步：加载 → 分块 → 向量化 → 建索引 → 检索+生成
> ✅ 适合：私域知识问答、需要溯源、知识频繁更新
> ❌ 不适合：统计聚合、精确匹配（那些用工具调用）

---

## 5.6 框架怎么选：一张决策表

四个框架不是"谁替代谁"，而是**抽象层级递进**——LangChain 1.x 的 `create_agent` 本身就是一张 LangGraph 预置图。选哪个，看你的流程有多少"必须由你掌控"：

| 你的需求 | 用什么 | 原因 |
|----------|--------|------|
| 固定步骤的管线（Prompt→模型→解析） | **LangChain LCEL** | `\|` 串起来最快，链可复用可测 |
| 答案在公司系统里，需调 API/查库 | **+ Tool / Agent** | 让模型决定调哪个工具 |
| 流程有硬性顺序、需人工审批、可控重试 | **LangGraph** | 状态机显式定义流程，循环/中断可控 |
| 回答要基于私有文档、需溯源 | **LlamaIndex / RAG** | 检索+生成分离，答案有出处 |
| 开放式探索、工具多、无严格流程 | **Agent 优先** | 把决策权交给模型更灵活 |

**一张图记忆它们的定位：**

```
       你掌控得越少 ←────────────────────────→ 你掌控得越多
  ┌──────────┐   ┌──────────┐   ┌──────────────┐   ┌──────────────┐
  │  Agent    │──▶│ LangChain│──▶│  LangGraph    │   │  LlamaIndex   │
  │(模型自决) │   │  LCEL    │   │(你画状态机)   │   │  (RAG 管线)   │
  └──────────┘   └──────────┘   └──────────────┘   └──────────────┘
   探索性任务      固定管线          可控流程           知识问答
```

> 实战里它们经常**组合**：LangGraph 画的图里，某个节点就是一个 LangChain 链；那个链里又用了 `@tool`；工具内部再调 LlamaIndex 做检索。**先分别学透，再组合**。

---

## 5.7 生产级拓展提示

教程为了"零 Key 可跑"，做了大量简化。落到生产必须补齐以下几块：

### 5.7.1 接真实模型（一行切换）

```bash
# 设置环境变量即可，业务代码不动
export DEEPSEEK_API_KEY="sk-xxxxx"
# 如需指定网关（如代理、私有化部署）
export DEEPSEEK_BASE_URL="https://api.deepseek.com"
```

`build_chat_model()` 检测到 Key 会自动走 `ChatOpenAI`。**注意**：真实模型输出不固定，测试里要断言"结构合法"而非"内容相等"——这正是 `llm_factory` 提供假模型的意义。

### 5.7.2 持久化

| 组件 | 教程 | 生产 |
|------|------|------|
| Agent/LangGraph 状态 | `InMemorySaver` | `AsyncPostgresSaver`（断点续跑落库，支持"审批等两天"） |
| RAG 向量 | 内存索引 | `Chroma` / `pgvector` / `Milvus`（持久化、可增量更新） |
| 配置 | 脚本常量 | `pydantic-settings`（见第四章 4.9）+ 密钥管理（Vault / 云 KMS） |

### 5.7.3 可观测性

- **LangSmith / Langfuse / Opik**：把每次调用、每个 token、每条工具轨迹录下来，线上排查靠它。
- RAG 必记：检索到的 Top-K 与分数（5.5.3 强调过）。
- Agent 必记：完整的 `messages` 轨迹（5.3.2 打印过），便于复盘"模型为什么调了那个工具"。

### 5.7.4 流式与前端

- `chain.stream()` / `agent.astream_events()` 把首字延迟压到最低（5.2.6）。
- 前端用 SSE 或 WebSocket 接流式输出——第四章 4.6 已讲过 FastAPI 的 `StreamingResponse`。

### 5.7.5 安全护栏

- **输入校验**：所有进模型的用户文本先过一遍安全过滤（见第四章 4.7 异常体系 + Pydantic 校验）。
- **工具权限**：Agent 能调的工具 = 它的"能力边界"。生产里工具要带鉴权，不能让模型随意删库。
- **输出审查**：金融/医疗等场景，模型最终回答要过合规检查再返回。

### 5.7.6 避坑清单

1. **别用旧 API**：`AgentExecutor`、`initialize_agent`、`ConversationChain` 在 LangChain 1.x 已废弃，新代码用 `create_agent`。
2. **Embedding 选定后别换**：向量索引和 Embedding 模型强绑定，换模型等于旧索引全部失效，必须重建。
3. **chunk_size 必须实测**：别抄别人的值，用自己语料测检索命中率。
4. **RAG 先看检索再看模型**：80% 的"答得差"是检索问题，不是模型问题。

---

## 本章小结

- **LangChain**：把"一次模型调用"工程化成可复用、可测试、结构化的链（`|` 是核心）。
- **Tool/Agent**：让模型能调外部能力，循环由框架托管，"调不调工具"由模型决定。
- **LangGraph**：当流程有硬性约束（顺序/审批/重试/挂起）时，用状态机把控制权拿回你手里；`interrupt` + checkpointer 实现"暂停两天再继续"。
- **LlamaIndex/RAG**：让回答基于私有文档且有出处；五步流水线里**检索质量决定上限**。
- 四者是抽象层级关系，不是替代关系——真实系统里常常组合使用。

**自检清单（学完应能回答）：**

- [ ] 为什么 `llm.invoke()` 返回的是 `AIMessage` 而不是 `str`？
- [ ] `prompt | llm | parser` 里的 `|` 到底调用了什么方法？
- [ ] Agent 的"思考→调工具→回填"循环，退出条件是什么？
- [ ] LangGraph 里循环怎么避免死循环？（提示：`rewrite_count`）
- [ ] `interrupt` 之后，状态存在哪、靠什么恢复？
- [ ] RAG 里"答非所问"，第一步该看什么？（提示：打印 Top-K）
