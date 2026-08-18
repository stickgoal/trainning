# -*- coding: utf-8 -*-
"""
示例 02: LangChain 核心架构
============================
展示 LangChain 核心组件：Chat Model、Prompt Template、Chain、Tool、Agent

适合 Java 工程师阅读：LCEL 语法和 Python 特殊语法均有中文注释
"""

# ============================================
# 1. Chat Model — LLM 调用封装
# ============================================

from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser, JsonOutputParser
from langchain_core.tools import tool
from langchain_core.runnables import RunnablePassthrough
from pydantic import BaseModel, Field

# Python 装饰器（decorator）：@tool 表示下面的函数被注册为 LangChain 工具
# 装饰器本质是高阶函数：tool(get_weather) 返回一个增强版的函数
# Java 类比：类似注解 @Tool（如果 Java 有工具框架的话），或 AOP 切面增强

# --- 初始化 Chat Model ---
# 类似 Java 的：ChatModel model = new ChatOpenAI("gpt-4o");
model: ChatOpenAI = ChatOpenAI(
    model="gpt-4o",
    temperature=0.7,
    # api_key 和 base_url 从环境变量自动读取
)


# ============================================
# 2. Prompt Template — 提示词模板
# ============================================

# ChatPromptTemplate 是参数化的提示词模板
# Java 类比：类似 Thymeleaf 模板引擎，用变量替换生成最终文本

prompt = ChatPromptTemplate.from_messages([
    # 系统消息：设定 AI 角色
    ("system", "你是一个 {role}，请用简洁中文回答。"),
    # 用户消息：包含变量 {question}
    ("human", "{question}"),
])

# 变量替换后生成最终 Prompt
formatted = prompt.invoke({"role": "Python 技术顾问", "question": "什么是列表推导式？"})
print("=== Prompt Template ===")
print(formatted)


# ============================================
# 3. Chain (LCEL) — 可组合管道
# ============================================

# LCEL (LangChain Expression Language) 使用 | 运算符组合组件
# | 是 Python 的位或运算符，LangChain 重载了它来表示管道
# Java 类比：类似 Stream API 的链式调用
#   stream.filter(...).map(...).collect(...)
# Python LCEL:
#   prompt | model | parser

# StrOutputParser：提取 LLM 响应中的纯文本
parser = StrOutputParser()

# 组合成 Chain
chain = prompt | model | parser

# 调用 Chain
# invoke 方法会依次执行：prompt.invoke() → model.invoke() → parser.invoke()
result = chain.invoke({"role": "技术架构师", "question": "为什么 Python 适合 AI 开发？"})
print("\n=== Chain (LCEL) ===")
print(result)


# ============================================
# 4. Tool — 工具定义
# ============================================

# @tool 装饰器：将普通 Python 函数注册为 LangChain 工具
# LangChain 自动从函数签名和 docstring 提取工具描述
# docstring 会作为工具的 description 传给 LLM
@tool
def search_database(query: str, limit: int = 5) -> str:
    """
    搜索企业数据库，返回匹配的记录。

    Args:
        query: 搜索关键词
        limit: 返回结果数量上限，默认5条

    Returns:
        搜索结果字符串
    """
    # 模拟数据库查询
    return f"找到 {limit} 条关于 '{query}' 的记录：[记录1, 记录2, ...]"


@tool
def calculate(expression: str) -> str:
    """
    计算数学表达式并返回结果。

    Args:
        expression: 数学表达式，如 "2 + 3 * 4"

    Returns:
        计算结果
    """
    try:
        # eval() 是 Python 内置函数，执行字符串表达式
        # 注意：生产环境中 eval() 有安全风险，此处仅作演示
        result = eval(expression)
        return f"结果: {result}"
    except Exception as e:
        return f"计算错误: {e}"


tools = [search_database, calculate]

print("\n=== Tool 定义 ===")
for t in tools:
    # t.name: 工具名称（函数名）
    # t.description: 工具描述（docstring）
    # t.args_schema: 参数 Schema（自动从类型提示生成）
    print(f"工具: {t.name}")
    print(f"  描述: {t.description[:50]}...")
    print(f"  参数: {t.args_schema.model_json_schema()['properties']}")


# ============================================
# 5. Agent — LLM + Tool + 推理循环
# ============================================

from langchain.agents import create_tool_calling_agent, AgentExecutor

# Agent 系统 Prompt
agent_prompt = ChatPromptTemplate.from_messages([
    ("system", """你是一个智能助手，可以使用以下工具回答问题。
    可用工具：{tool_names}
    请根据问题选择合适的工具。如果不需要工具，直接回答。"""),
    ("human", "{input}"),
    # agent_scratchpad: Agent 的思考过程记录区
    # LangChain 自动填充，包含 Agent 的推理步骤和工具调用历史
    ("placeholder", "{agent_scratchpad}"),
])

# 创建 Agent
# create_tool_calling_agent 类似创建一个策略对象：
# 它知道如何用 LLM + Prompt + Tools 组成一个推理循环
agent = create_tool_calling_agent(
    llm=model,
    tools=tools,
    prompt=agent_prompt,
)

# AgentExecutor：Agent 的执行器，管理推理循环
# 类似 Java 的 ExecutorService，负责调度执行
# 它会循环执行：LLM 思考 → 调用工具 → 观察结果 → 继续思考 → ... → 最终回答
agent_executor = AgentExecutor(
    agent=agent,
    tools=tools,
    verbose=True,  # 打印思考过程
    max_iterations=5,  # 最大循环次数（防止无限循环）
)

print("\n=== Agent 执行 ===")
result = agent_executor.invoke({
    "input": "帮我搜索数据库中关于用户注册的记录，然后计算 15 * 23 的结果。"
})
print(f"\n最终结果: {result['output']}")


# ============================================
# 6. Pydantic Model — 结构化输出
# ============================================

# Pydantic 是 Python 的数据校验库
# BaseModel 类似 Java 的 POJO/DTO，但自带校验逻辑
# Java 类比：Bean Validation (JSR-303) + Lombok 的结合体

class CodeReview(BaseModel):
    """代码审查结果的结构化模型"""
    # Field(..., description=...) 定义字段描述，帮助 LLM 理解输出格式
    score: int = Field(description="代码质量评分，0-100")
    issues: list[str] = Field(description="发现的问题列表")
    suggestion: str = Field(description="改进建议")

    # list[str] 是 Python 类型提示，表示字符串列表
    # Java 类比：List<String>


# 使用结构化输出
review_prompt = ChatPromptTemplate.from_messages([
    ("system", "你是一个代码审查专家。请审查以下代码并返回结构化结果。"),
    ("human", "{code}"),
])

# with_structured_output：让 LLM 返回符合 Pydantic Model 的结构化数据
# LLM 的输出会被自动解析为 CodeReview 对象
structured_model = model.with_structured_output(CodeReview)

review_chain = review_prompt | structured_model

print("\n=== 结构化输出 ===")
review_result = review_chain.invoke({
    "code": "def add(a, b): return a + b"
})
# review_result 是 CodeReview 对象，可以像访问对象属性一样访问
print(f"评分: {review_result.score}")
print(f"问题: {review_result.issues}")
print(f"建议: {review_result.suggestion}")


# ============================================
# 7. Runnable 组合 — 高级管道
# ============================================

# RunnablePassthrough：透传输入，不做任何处理
# 常用于在 Chain 中传递额外上下文
# Java 类比：Function.identity() 或 NoOp

# 并行执行多个 Runnable，然后合并结果
parallel_chain = RunnablePassthrough.assign(
    # assign 方法：在输入字典中添加新字段
    # 每个字段的值可以是一个 Runnable
    summary=lambda x: f"问题: {x['question'][:20]}...",
)

full_chain = parallel_chain | prompt | model | parser

print("\n=== Runnable 组合 ===")
result = full_chain.invoke({
    "role": "架构师",
    "question": "如何设计一个高并发的 API 网关？"
})
print(result[:200])
