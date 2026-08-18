# -*- coding: utf-8 -*-
"""
示例 01: LLM 应用开发基础
=========================
展示 LLM 调用的核心概念：Prompt、Token、Context、Tool Calling、Memory

适合 Java 工程师阅读：所有 Python 特殊语法均有中文注释
"""

# ============================================
# 1. 基础 LLM 调用
# ============================================

import os
from openai import OpenAI

# Python 类型提示 (Type Hints)：类似 Java 的泛型声明
# client: OpenAI 表示变量 client 的类型是 OpenAI
# Java 类比：OpenAI client = new OpenAI();
client: OpenAI = OpenAI(
    api_key=os.getenv("OPENAI_API_KEY"),  # 从环境变量读取 API Key
    base_url=os.getenv("OPENAI_BASE_URL", "https://api.openai.com/v1"),
)

# --- 最基础的 LLM 调用 ---
response = client.chat.completions.create(
    model="gpt-4o",  # 指定模型
    messages=[
        # messages 是一个列表，每个元素是对话中的一条消息
        # role 可选：system（系统设定）、user（用户）、assistant（AI 回复）
        {"role": "system", "content": "你是一个 Python 技术顾问。"},
        {"role": "user", "content": "什么是装饰器（decorator）？一句话解释。"},
    ],
    temperature=0.7,  # 温度参数：0=确定性输出，1=高随机性，类比 Java 的随机种子控制
    max_tokens=200,   # 最大生成 Token 数（Token ≈ 半个中文词 / 1个英文单词）
)

print("=== LLM 基础调用 ===")
print(response.choices[0].message.content)
# Token 使用量：理解成本的关键指标
print(f"Token 使用: {response.usage}")  # prompt_tokens + completion_tokens = total_tokens


# ============================================
# 2. 多轮对话与 Memory 概念
# ============================================

# 在 LLM 应用中，"记忆"本质上是把历史消息一起发送给 LLM
# LLM 本身是无状态的（每次调用独立），Memory 由应用层维护
# Java 类比：HttpSession 是服务端维护的，HTTP 本身是无状态的

conversation_history: list[dict] = [
    {"role": "system", "content": "你是一个技术面试官，正在面试 Java 工程师。"},
]

# list[dict] 是 Python 类型提示，表示元素为字典的列表
# Java 类比：List<Map<String, String>>


def chat(user_input: str, history: list[dict]) -> str:
    """
    带记忆的对话函数。

    Python 三引号字符串作为函数文档（docstring），
    类似 Java 的 Javadoc 注释。

    Args:
        user_input: 用户输入文本
        history: 对话历史列表

    Returns:
        LLM 的回复文本
    """
    # 将用户消息添加到历史
    history.append({"role": "user", "content": user_input})

    # 把完整历史发送给 LLM（这就是"记忆"的本质）
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=history,  # 发送完整对话历史
        temperature=0.7,
    )

    reply = response.choices[0].message.content
    # 将 AI 回复也添加到历史，以便下一轮对话使用
    history.append({"role": "assistant", "content": reply})

    return reply


print("\n=== 多轮对话 ===")
print(chat("你好，我有5年 Java 经验。", conversation_history))
print(chat("我想转 Python AI 开发，需要学什么？", conversation_history))
# 此时 conversation_history 包含了所有对话，LLM 能"记住"上下文


# ============================================
# 3. Tool Calling（工具调用）概念
# ============================================

# Tool Calling 让 LLM 能调用外部函数
# LLM 本身不执行代码，而是返回"我想调用哪个工具，参数是什么"
# 应用层负责实际执行，并把结果返回给 LLM
# Java 类比：类似 RPC 远程过程调用，LLM 是调用方，应用是服务方

import json

# 定义一个工具函数（LLM 可以调用的外部能力）
def get_weather(city: str) -> str:
    """模拟天气查询工具"""
    # 实际应用中这里会调用天气 API
    return f"{city}今天晴，25°C"


# 定义工具的 Schema（告诉 LLM 有哪些工具可用）
# 这是 JSON Schema 格式，LLM 通过它理解工具的参数和用途
tools_schema = [
    {
        "type": "function",
        "function": {
            "name": "get_weather",
            "description": "查询指定城市的天气",
            "parameters": {
                "type": "object",
                "properties": {
                    "city": {
                        "type": "string",
                        "description": "城市名称，如：北京、上海",
                    }
                },
                "required": ["city"],
            },
        },
    }
]

print("\n=== Tool Calling ===")

# 第一步：发送请求，LLM 决定是否需要调用工具
response = client.chat.completions.create(
    model="gpt-4o",
    messages=[{"role": "user", "content": "北京天气怎么样？"}],
    tools=tools_schema,  # 把工具列表传给 LLM
    tool_choice="auto",  # auto = LLM 自主决定是否调用工具
)

message = response.choices[0].message

# 检查 LLM 是否决定调用工具
if message.tool_calls:
    # LLM 决定调用工具
    tool_call = message.tool_calls[0]
    function_name = tool_call.function.name  # "get_weather"
    function_args = json.loads(tool_call.function.arguments)  # {"city": "北京"}

    print(f"LLM 决定调用: {function_name}({function_args})")

    # 应用层执行工具（LLM 不执行，只决策）
    # getattr(obj, name) 是 Python 内置函数，类似 Java 反射
    # 等价于：if function_name == "get_weather": result = get_weather(**function_args)
    available_functions = {"get_weather": get_weather}
    function_to_call = available_functions[function_name]
    function_result = function_to_call(**function_args)
    # **function_args 是 Python 的字典解包语法
    # 等价于 function_to_call(city="北京")
    # Java 没有直接等价物，类似反射调用 method.invoke(obj, args)

    print(f"工具执行结果: {function_result}")

    # 第二步：把工具结果返回给 LLM，让它生成自然语言回答
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[
            {"role": "user", "content": "北京天气怎么样？"},
            message,  # LLM 的工具调用请求
            {
                "role": "tool",
                "tool_call_id": tool_call.id,
                "content": function_result,
            },
        ],
    )
    print(f"最终回答: {response.choices[0].message.content}")
else:
    print(f"LLM 直接回答: {message.content}")


# ============================================
# 4. Embedding 概念演示
# ============================================

# Embedding：将文本转换为向量，用于语义相似度计算
# Java 类比：将对象序列化为 byte[]，但这里序列化的是语义信息

print("\n=== Embedding 概念 ===")

embedding_response = client.embeddings.create(
    model="text-embedding-3-small",
    input="Python 是 AI 开发的首选语言",
)
# embedding_response.data[0].embedding 是一个浮点数列表（如 1536 维向量）
# 这个向量捕捉了文本的语义信息
embedding_vector = embedding_response.data[0].embedding
print(f"向量维度: {len(embedding_vector)}")
print(f"前5个值: {embedding_vector[:5]}")
# 语义相近的文本，向量距离（余弦相似度）也相近
# 这就是向量检索（Vector Search）的基础
