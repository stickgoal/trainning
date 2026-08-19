"""
============================================================
 01_llm_basics.py — LLM 应用开发基础（8 个核心概念）
 对应章节：第2章 LLM Application 开发基础
============================================================

演示 3 段：
 1. 直接调 LLM（Prompt / Token / Context）
 2. Tool Calling（LLM 决定调用哪个函数）
 3. Memory（带历史对话再问一句）

【运行流程】
 set DEEPSEEK_API_KEY=sk-xxx
 python examples/01_llm_basics.py

【应用场景】
 任何 "用 LLM 做 X" 的需求，都从这 8 个概念开始拆。

【Python 特殊语法提示】
 - def foo(x: int) -> str   : 类型提示（Java 泛型等价物）
 - list[dict]               : 泛型容器写法（Python 3.9+，Java 的 List<Map>）
 - @dataclass                : 装饰器，自动生成构造器/equals（Java 的 Lombok @Data）
 - try/except                : 异常处理（Java 的 try/catch）
"""

import os
import json
from dataclasses import dataclass, field


# =================================================================
# 段 1：直接调 LLM —— Prompt / Token / Context
# =================================================================
def demo_basic_call() -> None:
    """最简单的 LLM 调用，演示 Prompt / Token / Context 三个概念。"""
    from openai import OpenAI

    client = OpenAI(
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        base_url=os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com"),
    )
    model = os.getenv("DEEPSEEK_MODEL", "deepseek-chat")

    prompt = "解释什么是 Token？用一句话回答。"   # ← Prompt：给 LLM 的输入
    response = client.chat.completions.create(
        model=model,
        messages=[{"role": "user", "content": prompt}],
    )

    # Token：LLM 处理的文本单元（不是字符也不是单词，介于两者之间）
    # Context：单次请求的有效上下文窗口，token 数有上限
    print(f"[段1] Prompt: {prompt}")
    print(f"      Token 用量: {response.usage}")  # prompt_tokens + completion_tokens
    print(f"      回答: {response.choices[0].message.content}\n")


# =================================================================
# 段 2：Tool Calling —— LLM 决定调用哪个函数
# =================================================================
def get_weather(city: str, unit: str = "celsius") -> str:
    """一个模拟工具函数。LLM 不会自己执行它，只会 '决定' 调用它。"""
    # 真实场景这里会调天气 API
    return f"{city} 今天 25°C，晴（模拟数据）"


# 用 JSON Schema 描述工具，让 LLM 知道这个工具的存在和参数
WEATHER_TOOL_SCHEMA = {
    "type": "function",
    "function": {
        "name": "get_weather",
        "description": "查询某城市天气",
        "parameters": {
            "type": "object",
            "properties": {
                "city": {"type": "string", "description": "城市名"},
                "unit": {"type": "string", "enum": ["celsius", "fahrenheit"]},
            },
            "required": ["city"],
        },
    },
}


def demo_tool_calling() -> None:
    """演示 LLM 主动决定调用 get_weather 工具的完整闭环。"""
    from openai import OpenAI

    client = OpenAI(
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        base_url=os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com"),
    )
    model = os.getenv("DEEPSEEK_MODEL", "deepseek-chat")

    print("[段2] Tool Calling 演示：")
    # 第 1 步：把用户问题 + 工具 schema 一起发给 LLM
    response = client.chat.completions.create(
        model=model,
        messages=[{"role": "user", "content": "北京今天天气怎么样？"}],
        tools=[WEATHER_TOOL_SCHEMA],
    )
    msg = response.choices[0].message

    # 第 2 步：LLM 返回的不是答案，而是一个 tool_call 指令
    if msg.tool_calls:
        call = msg.tool_calls[0]
        args = json.loads(call.function.arguments)  # LLM 给的参数
        print(f"      LLM 决定调用工具: {call.function.name}({args})")

        # 第 3 步：我们（应用层）真正执行工具
        result = get_weather(**args)  # **args 把 dict 展开成关键字参数
        print(f"      工具执行结果: {result}")

        # 第 4 步：把工具结果回灌给 LLM，让它生成最终自然语言回答
        final = client.chat.completions.create(
            model=model,
            messages=[
                {"role": "user", "content": "北京今天天气怎么样？"},
                msg,  # LLM 上一轮说要调工具
                {"role": "tool", "tool_call_id": call.id, "content": result},
            ],
        )
        print(f"      LLM 最终回答: {final.choices[0].message.content}\n")
    else:
        print("      LLM 没有触发工具调用\n")


# =================================================================
# 段 3：Memory —— 跨轮对话状态
# =================================================================
@dataclass
class ChatSession:
    """
    简易会话记忆：用 list 存历史消息。
    @dataclass 装饰器自动生成 __init__ / __repr__，类似 Java Lombok @Data。
    field(default_factory=list) 给可变默认值，避免所有实例共享同一个 list。
    """
    messages: list = field(default_factory=list)

    def add(self, role: str, content: str) -> None:
        self.messages.append({"role": role, "content": content})

    def ask(self, question: str) -> str:
        from openai import OpenAI
        client = OpenAI(
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        base_url=os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com"),
    )
        self.add("user", question)
        # 把全部历史带上 → 这就是 "Memory" 的本质
        response = client.chat.completions.create(
            model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
            messages=self.messages,
        )
        answer = response.choices[0].message.content
        self.add("assistant", answer)
        return answer


def demo_memory() -> None:
    print("[段3] Memory 演示（带历史对话）：")
    session = ChatSession()
    session.add("system", "你是一个简洁的助手。")
    r1 = session.ask("我叫张三，记住这个名字。")
    print(f"      第1轮: {r1}")
    r2 = session.ask("我叫什么名字？")  # 没有记忆就答不出来
    print(f"      第2轮: {r2}\n")


def main() -> None:
    if not os.getenv("DEEPSEEK_API_KEY"):
        print("⚠️  请先 set DEEPSEEK_API_KEY=sk-xxx")
        return
    # demo_basic_call()
    # demo_tool_calling()
    demo_memory()


if __name__ == "__main__":
    main()
