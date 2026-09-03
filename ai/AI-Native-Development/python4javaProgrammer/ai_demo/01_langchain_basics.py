"""第一章（5.1）：LangChain 基础 —— 把「一次调用」组装成「一条可复用的链」。

案例背景：我们要做一个**技术文档助手**，输入一段技术文本，产出摘要、标签、评分。

本文件回答五个问题：
1. 直接调模型，和用 LangChain 调，差别在哪？
2. Prompt 模板解决什么问题？
3. LCEL 的 `|` 到底是什么？
4. 怎么让模型输出结构化数据（而不是一堆要正则去抠的文本）？
5. 流式、批量这些工程能力怎么用？

运行：
    python ai_demo/01_langchain_basics.py

无需 API Key（会用离线假模型）；配了 DEEPSEEK_API_KEY 则自动切真实模型。
"""
from __future__ import annotations

from typing import Literal

from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.runnables import RunnableLambda, RunnableParallel
from pydantic import BaseModel, Field

from llm_factory import describe_model

# 一段待分析的技术文本（贯穿全章的示例素材）
SAMPLE_DOC = """
FastAPI 是一个现代 Python Web 框架。它基于标准类型注解，用 Pydantic 做数据校验，
自动生成 OpenAPI 文档。性能接近 NodeJS 和 Go，开发效率则显著提升。
""".strip()


# ===========================================================================
# 1. 最小调用：看看模型到底返回了个什么东西
# ===========================================================================
def demo_1_minimal_call() -> None:
    from llm_factory import build_chat_model

    print("\n" + "=" * 68)
    print("1. 最小调用：模型的返回不是字符串，是消息对象")
    print("=" * 68)

    llm = build_chat_model(scripted=["FastAPI 是基于类型注解的 Python Web 框架。"])
    print("模型：", describe_model(llm))

    result = llm.invoke("用一句话介绍 FastAPI")

    print("\n返回类型：", type(result).__name__)
    print("  .content  →", result.content[:60])
    print("  .type     →", result.type)
    print("  完整对象  →", repr(result)[:120], "...")

    print("""
要点：
  · 返回的不是 str，而是 AIMessage。这是 LangChain 的核心设计——
    对话是「消息序列」，不是「字符串拼接」。多轮对话、工具调用、流式
    全都建立在这个抽象上。
  · Java 类比：AIMessage 就像一个带 role 字段的 DTO，
    而不是返回裸 String。""")


# ===========================================================================
# 2. Prompt 模板：把「拼字符串」变成「填参数」
# ===========================================================================
def demo_2_prompt_template() -> None:
    from llm_factory import build_chat_model

    print("\n" + "=" * 68)
    print("2. Prompt 模板：System / Human 角色分离")
    print("=" * 68)

    prompt = ChatPromptTemplate.from_messages(
        [
            ("system", "你是技术文档分析助手。回答要简洁，不超过 {max_words} 个字。"),
            ("human", "请分析这段文档：\n\n{document}"),
        ]
    )

    # 模板本身是个 Runnable：填参数 → 得到消息列表
    messages = prompt.invoke({"max_words": 30, "document": SAMPLE_DOC})

    print("模板渲染结果（PromptValue → 消息列表）：")
    for m in messages.to_messages():
        print(f"  [{m.type:6}] {m.content[:70]}...")

    llm = build_chat_model(scripted=["FastAPI：类型注解驱动的现代 Web 框架，自带 OpenAPI 文档。"])
    result = (prompt | llm | StrOutputParser()).invoke(
        {"max_words": 30, "document": SAMPLE_DOC}
    )
    print("\n链的输出（已经是 str）：", result)

    print("""
要点：
  · System 消息定「人格和约束」，Human 消息定「这一轮任务」。
    混在一起写，模型对约束的遵从度会明显下降。
  · 模板把变量外置，同一个 Prompt 可以复用给不同输入——
    这是可维护性的分水岭。
  · Java 类比：ChatPromptTemplate ≈ String.format 的模板 + 角色枚举。""")


# ===========================================================================
# 3. LCEL：| 到底是什么
# ===========================================================================
def demo_3_lcel_chain() -> None:
    from llm_factory import build_chat_model

    print("\n" + "=" * 68)
    print("3. LCEL：用 | 把组件串成管道")
    print("=" * 68)

    prompt = ChatPromptTemplate.from_messages(
        [("system", "你是技术文档助手。"), ("human", "{document}")]
    )
    llm = build_chat_model(scripted=["FastAPI 通过类型注解自动生成接口文档。"])
    parser = StrOutputParser()

    chain = prompt | llm | parser

    print("链的结构：")
    print("  ", chain)
    print("\n各环节的入参/出参类型：")
    print("   dict        →  ChatPromptTemplate  →  PromptValue(消息列表)")
    print("   PromptValue →  ChatModel           →  AIMessage")
    print("   AIMessage   →  StrOutputParser     →  str")

    result = chain.invoke({"document": SAMPLE_DOC})
    print("\ninvoke 结果：", result)
    print("类型的 __name__：", type(result).__name__)
    print("isinstance(result, str)：", isinstance(result, str))
    print("result == 那个字符串：", result == "FastAPI 通过类型注解自动生成接口文档。")

    print("""
  ⚠ 一个容易被吓到的细节：类型名是 TextAccessor，不是 str。
    LangChain 1.x 的 StrOutputParser 返回一个 str 的子类（惰性取值器）。
    别急着去查文档——先用两行代码验证它到底能不能当字符串用：

        isinstance(result, str)   →  True
        result == "..."           →  True

    结论：完全兼容 str，类型名只是内部实现细节。
    这就是排查第三方库行为的标准动作：**先验证，再查文档**。""")

    print("""
要点（这是理解 LangChain 的关键）：
  · `|` 不是语法糖，它调用的是 Runnable.__or__，返回一个 RunnableSequence。
  · 所有组件都实现同一个接口 Runnable，都有 invoke / stream / batch / ainvoke。
    → 所以链本身也是 Runnable，可以被继续 `|`，也可以被单独测试。
  · Java 类比：Runnable ≈ Java 的 Function<T,R>。
    `a | b` ≈ `a.andThen(b)`；不同的是 LangChain 的每个环节
    都自带异步、流式、批量的默认实现。""")


# ===========================================================================
# 4. 结构化输出：别再正则抠字符串了
# ===========================================================================
class DocAnalysis(BaseModel):
    """文档分析结果（Pydantic 模型即契约）。"""

    summary: str = Field(description="一句话摘要，不超过 40 字")
    tags: list[str] = Field(description="3 个以内的标签")
    score: int = Field(description="文档信息密度评分，1-10", ge=1, le=10)
    category: Literal["tutorial", "reference", "news"] = Field(description="文档类型")


def demo_4_structured_output() -> None:
    from langchain_core.output_parsers import JsonOutputParser

    from llm_factory import build_chat_model

    print("\n" + "=" * 68)
    print("4. 结构化输出：让模型返回 Pydantic 对象")
    print("=" * 68)

    parser = JsonOutputParser(pydantic_object=DocAnalysis)

    prompt = ChatPromptTemplate.from_messages(
        [
            ("system", "你是文档分析助手，严格按格式要求输出。"),
            ("human", "{format_instructions}\n\n文档内容：\n{document}"),
        ]
    ).partial(format_instructions=parser.get_format_instructions())

    # 离线剧本：严格按 DocAnalysis 的 schema 给出 JSON
    scripted = [
        '```json\n{"summary": "FastAPI 是基于类型注解的现代 Web 框架",'
        ' "tags": ["Python", "FastAPI", "Web"], "score": 8, "category": "tutorial"}\n```'
    ]
    llm = build_chat_model(scripted=scripted)

    chain = prompt | llm | parser
    result = chain.invoke({"document": SAMPLE_DOC})

    print("解析结果类型：", type(result).__name__)
    print("解析结果：", result)

    # 转成真正的 Pydantic 对象，才能享受校验
    analysis = DocAnalysis.model_validate(result)
    print("\n转为 Pydantic 后：")
    print("  summary  :", analysis.summary)
    print("  tags     :", analysis.tags)
    print("  score    :", analysis.score, f"（类型 {type(analysis.score).__name__}）")
    print("  category :", analysis.category)

    print("""
要点：
  · 生产中最常见的错误：让模型"以 JSON 格式回答"，然后自己 json.loads。
    模型会在 JSON 外面包 ```json 标记、加解释文字，一 loads 就崩。
  · JsonOutputParser 负责容错解析，Pydantic 负责类型校验与约束（ge/le/Literal）。
    两者分工：前者处理"模型不老实"，后者处理"数据不合法"。
  · get_format_instructions() 把 schema 自动写进 Prompt——
    手写这段说明既啰嗦又容易和模型定义不同步。
  · Java 类比：这就是 Jackson 的 ObjectMapper.readValue，
    只不过输入是不可靠的自然语言。""")


# ===========================================================================
# 5. 并行分支与自定义步骤
# ===========================================================================
def demo_5_parallel() -> None:
    from llm_factory import build_chat_model

    print("\n" + "=" * 68)
    print("5. RunnableParallel / RunnableLambda：分支与自定义加工")
    print("=" * 68)

    # 分支一：摘要
    summary_chain = (
        ChatPromptTemplate.from_messages([("human", "用一句话概括：{document}")])
        | build_chat_model(scripted=["FastAPI 是现代 Python Web 框架。"])
        | StrOutputParser()
    )
    # 分支二：关键词
    keyword_chain = (
        ChatPromptTemplate.from_messages([("human", "提取 3 个关键词，逗号分隔：{document}")])
        | build_chat_model(scripted=["Python, FastAPI, OpenAPI"])
        | StrOutputParser()
    )
    # 自定义步骤：纯 Python 函数也能进管道
    def count_chars(text: str) -> int:
        return len(text)

    combined = RunnableParallel(
        summary=summary_chain,
        keywords=keyword_chain,
        # RunnableLambda 把普通函数包成 Runnable
        raw_length=RunnableLambda(lambda x: count_chars(x["document"])),
    )

    result = combined.invoke({"document": SAMPLE_DOC})

    print("并行结果（dict）：")
    for key, value in result.items():
        print(f"  {key:12} = {value}")

    print(f"\n原文长度：{len(SAMPLE_DOC)} 字（由 RunnableLambda 算出，没调模型）")

    print("""
要点：
  · RunnableParallel 的多个分支**并发**执行（内部用线程池/asyncio）。
    两次独立调用模型 → 总耗时接近一次，而不是两次。
  · RunnableLambda 让"不需要模型的加工步骤"也能进管道，
    保持整条链的统一调用方式。
  · Java 类比：RunnableParallel ≈ CompletableFuture.allOf，
    但声明式的——你描述"要什么"，而不是"怎么并发"。""")


# ===========================================================================
# 6. 流式与批量
# ===========================================================================
def demo_6_stream_and_batch() -> None:
    from llm_factory import build_chat_model

    print("\n" + "=" * 68)
    print("6. 流式输出与批量处理")
    print("=" * 68)

    llm = build_chat_model(scripted=["FastAPI 用类型注解驱动校验与文档生成，性能好、上手快。"])
    chain = ChatPromptTemplate.from_messages([("human", "{q}")]) | llm | StrOutputParser()

    print("流式（每个 chunk 立刻可用）：")
    collected = []
    for chunk in chain.stream({"q": "介绍 FastAPI"}):
        collected.append(chunk)
        print(chunk, end="|", flush=True)
    print("\n  → 共收到", len(collected), "个 chunk；拼接后：", "".join(collected))

    llm2 = build_chat_model(scripted=["回复A", "回复B", "回复C"])
    chain2 = ChatPromptTemplate.from_messages([("human", "{q}")]) | llm2 | StrOutputParser()
    batch_result = chain2.batch([{"q": "问题1"}, {"q": "问题2"}, {"q": "问题3"}])
    print("\n批量（一次调用处理多个输入）：")
    for i, r in enumerate(batch_result, 1):
        print(f"  {i}. {r}")

    print("""
要点：
  · 流式不是为了炫技，是为了**首字延迟**。用户等 3 秒看到第一个字，
    和等 3 秒看到全部，体验天差地别。
  · batch() 内部并发，比 for 循环 invoke 快得多（尤其模型有并发额度时）。
  · 这三个方法（invoke / stream / batch）**每条链都自带**，
    不需要你写任何并发代码——这就是统一 Runnable 接口的价值。""")


def main() -> None:
    print("=" * 68)
    print("LangChain 基础：从一次调用到一条链")
    print("=" * 68)
    demo_1_minimal_call()
    demo_2_prompt_template()
    demo_3_lcel_chain()
    demo_4_structured_output()
    demo_5_parallel()
    demo_6_stream_and_batch()
    print("\n" + "=" * 68)
    print("小结：LangChain 解决的是「单次调用的工程化」")
    print("  ✅ 擅长：固定流程、可复用、结构化输出、并发/流式")
    print("  ❌ 不擅长：需要根据中间结果动态改变流程 —— 那是 LangGraph 的活（见 03）")
    print("=" * 68)


if __name__ == "__main__":
    main()
