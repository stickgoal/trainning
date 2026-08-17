"""
============================================================
 02_langchain_demo.py — LangChain 4 段核心演示
 对应章节：第3章 LangChain
============================================================

演示：
 1. 调 LLM（Chat Model 统一接口）
 2. Prompt Template（变量插值复用）
 3. LCEL Chain（用 | 串联 Runnable，LangChain 的精髓）
 4. Agent + Tool（LLM 自主选工具）

【运行流程】
 set DEEPSEEK_API_KEY=sk-xxx
 pip install langchain langchain-deepseek
 python examples/02_langchain_demo.py

【应用场景】
 80% 的 "LLM + 提示词 + 检索" 场景的默认起点。

【Python 特殊语法提示】
 - | 管道符     : LangChain 重载了 | 运算符，把 Runnable 串联（Java 无等价，类比 Unix 管道）
 - @tool 装饰器 : 把普通函数自动转成 Tool 对象（Java 注解 @Tool）
 - Runnable<T>  : 统一抽象，invoke/ainvoke/batch/stream 都同一接口（Java 的 Function 接口家族）
 - async/await : 异步，第 7 章详解
"""

import os


def demo_1_chat_model() -> None:
    """段1：用 LangChain 的 ChatModel 统一接口调 LLM。"""
    from langchain_deepseek import ChatDeepSeek

    # 统一接口：换模型只改这里（OpenAI / Anthropic / 通义 / DeepSeek 都实现同一抽象）
    model = ChatDeepSeek(
        model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        temperature=0.3,
    )

    # invoke 是 Runnable 的统一入口（同步），还有 ainvoke（异步）、batch、stream
    response = model.invoke("用一句话解释什么是 LLM。")
    print(f"[段1] ChatModel.invoke → {response.content}\n")


def demo_2_prompt_template() -> None:
    """段2：PromptTemplate 把变量插进提示词，复用。"""
    from langchain_core.prompts import ChatPromptTemplate

    # 模板里有 {role} {topic} 占位符
    template = ChatPromptTemplate.from_template(
        "你是一位{role}。请用一句话解释{topic}。"
    )

    # 同一个模板，填不同变量 → 复用
    r1 = template.invoke({"role": "Java 架构师", "topic": "Pydantic"})
    r2 = template.invoke({"role": "AI 研究员", "topic": "Embedding"})

    print(f"[段2] 模板1 → {r1.to_messages()[0].content}")
    print(f"     模板2 → {r2.to_messages()[0].content}\n")


def demo_3_lcel_chain() -> None:
    """
    段3：LCEL（LangChain Expression Language）链。
    核心思想：所有组件都是 Runnable，用 | 串成流水线。

    类比 Java：就像 Stream.map().filter().collect()，
    但这里每一步都是 LLM 调用或数据变换。
    """
    from langchain_deepseek import ChatDeepSeek
    from langchain_core.prompts import ChatPromptTemplate
    from langchain_core.output_parsers import StrOutputParser

    model = ChatDeepSeek(
        model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
        api_key=os.getenv("DEEPSEEK_API_KEY"),
    )

    prompt = ChatPromptTemplate.from_template("用一句话解释 {concept}。")

    # ★ LCEL 精髓：用 | 把 prompt | model | parser 串成一条链
    # 数据流：{concept} → 填模板 → 发给 LLM → 解析成纯字符串
    chain = prompt | model | StrOutputParser()

    # 一行调用，得到最终纯文本
    result = chain.invoke({"concept": "RAG"})
    print(f"[段3] LCEL Chain → {result}\n")


def demo_4_agent_with_tool() -> None:
    """
    段4：Agent = LLM + Tool + 决策循环。
    LLM 自己决定何时调哪个工具。

    注意：LangChain 1.x 移除了旧版 create_tool_calling_agent / AgentExecutor，
    LangGraph 的 create_react_agent 也已废弃；当前官方推荐入口是
    langchain.agents.create_agent（内部同样实现 bind_tools + 决策循环）。
    """
    from langchain_deepseek import ChatDeepSeek
    from langchain_core.tools import tool
    from langchain.agents import create_agent

    # @tool 装饰器：把普通 Python 函数自动转成 Tool 对象
    # docstring 会作为工具说明发给 LLM，让它知道何时用这个工具
    @tool
    def add(a: int, b: int) -> int:
        """两个整数相加。当用户要做加法时调用。"""
        return a + b

    @tool
    def multiply(a: int, b: int) -> int:
        """两个整数相乘。当用户要做乘法时调用。"""
        return a * b

    model = ChatDeepSeek(
        model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
        api_key=os.getenv("DEEPSEEK_API_KEY"),
    )
    tools = [add, multiply]

    # create_agent：组装一个会自主选工具的 Agent（等价于旧版 AgentExecutor）
    agent = create_agent(model, tools)

    # 输入是 messages 列表（注意：不再是 {"input": ...} 这种旧格式）
    # Agent 自己决定先 multiply(3,4)=12，再 add(12,5)=17
    result = agent.invoke(
        {"messages": [{"role": "user", "content": "计算 3 乘以 4，再加 5。"}]}
    )
    # 最终回复在最后一条 AI 消息里
    print(f"[段4] Agent → {result['messages'][-1].content}\n")


def main() -> None:
    if not os.getenv("DEEPSEEK_API_KEY"):
        print("⚠️  请先 set DEEPSEEK_API_KEY=sk-xxx")
        return
    demo_1_chat_model()
    demo_2_prompt_template()
    demo_3_lcel_chain()
    demo_4_agent_with_tool()


if __name__ == "__main__":
    main()
