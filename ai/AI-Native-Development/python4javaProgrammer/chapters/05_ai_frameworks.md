# 第五章：AI三大框架实战

> LangChain + LangGraph + LlamaIndex 三框架协同实战

---

## 5.1 LangChain：工具与链

### 5.1.1 LCEL链 (LangChain Expression Language)

LCEL是LangChain的声明式管道语法，用 `|` 运算符串联组件。

```python
# python4javaProgrammer/ai_demo/langchain_lcel.py
"""LangChain LCEL链最小示例"""
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from dotenv import load_dotenv

load_dotenv()

# --- 基础链: prompt | llm | parser ---
prompt = ChatPromptTemplate.from_messages([
    ("system", "你是一个Python编程助手，回答简洁准确。"),
    ("user", "{question}"),
])

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.7)

# StrOutputParser: 提取AI回复的字符串内容
chain = prompt | llm | StrOutputParser()

# 调用
result = chain.invoke({"question": "Python的GIL是什么？一句话解释。"})
print(result)  # GIL是全局解释器锁...

# --- 带上下文的RAG链 ---
from langchain_core.runnables import RunnableLambda

def retrieve_context(query: str) -> str:
    """模拟检索（实际用LlamaIndex或向量库）"""
    fake_docs = {
        "Python": "Python是解释型语言，支持多种编程范式。",
        "GIL": "GIL确保同一时刻只有一个线程执行Python字节码。",
    }
    for key in fake_docs:
        if key.lower() in query.lower():
            return fake_docs[key]
    return ""

rag_chain = (
    {"context": RunnableLambda(retrieve_context), "question": RunnablePassthrough()}
    | ChatPromptTemplate.from_messages([
        ("system", "根据以下上下文回答问题。\n上下文: {context}"),
        ("user", "{question}"),
    ])
    | llm
    | StrOutputParser()
)

result = rag_chain.invoke("什么是GIL？")
print(result)

# --- 链的组件化组合 ---
# 每个组件都是Runnable，可以单独测试和替换
prompt_component = ChatPromptTemplate.from_messages([
    ("system", "你是{role}。"),
    ("user", "{input}"),
])

# 使用RunnableParallel并行执行
from langchain_core.runnables import RunnableParallel

parallel_chain = RunnableParallel(
    original=RunnablePassthrough(),
    uppercased=RunnableLambda(lambda x: x["input"].upper()),
    length=RunnableLambda(lambda x: len(x["input"])),
)

result = parallel_chain.invoke({"role": "翻译", "input": "hello world"})
print(result)
# {'original': {'role': '翻译', 'input': 'hello world'},
#  'uppercased': 'HELLO WORLD', 'length': 11}

# --- 流式输出 ---
print("\n=== 流式输出 ===")
for chunk in chain.stream({"question": "用三句话介绍Python。"}):
    print(chunk, end="", flush=True)
print()

# --- 批量处理 ---
results = chain.batch([
    {"question": "什么是列表推导式？"},
    {"question": "什么是装饰器？"},
    {"question": "什么是生成器？"},
])
for i, r in enumerate(results):
    print(f"\n--- 问题{i+1} ---\n{r}")
```

### 5.1.2 Prompt模板

```python
# python4javaProgrammer/ai_demo/langchain_prompt.py
"""LangChain Prompt模板"""
from langchain_core.prompts import (
    ChatPromptTemplate,
    FewShotChatMessagePromptTemplate,
    PromptTemplate,
)
from langchain_openai import ChatOpenAI
from langchain_core.output_parsers import StrOutputParser
from dotenv import load_dotenv

load_dotenv()

# --- 基础Prompt模板 ---
template = PromptTemplate.from_template(
    "请用{language}语言编写一个{functionality}的函数，并添加注释。"
)
prompt = template.format(language="Python", functionality="快速排序")
print("=== 基础模板 ===")
print(prompt)

# --- ChatPrompt（多轮对话） ---
chat_prompt = ChatPromptTemplate.from_messages([
    ("system", "你是一个资深{domain}工程师。"),
    ("human", "问题: {question}"),
    ("ai", "回答: "),
])

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.3)
chain = chat_prompt | llm | StrOutputParser()

result = chain.invoke({
    "domain": "Python后端",
    "question": "FastAPI和Django如何选择？",
})
print("\n=== ChatPrompt ===")
print(result)

# --- Few-shot Prompt（少样本学习） ---
examples = [
    {"input": "Python列表怎么反转？", "output": "list[::-1] 或 list.reverse()"},
    {"input": "Python字典怎么按值排序？", "output": "sorted(d.items(), key=lambda x: x[1])"},
    {"input": "Python字符串怎么去空格？", "output": "s.strip() 去两端, s.replace(' ', '') 去所有"},
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
    ("system", "你是一个Python编程助手，请参考示例风格回答。"),
    few_shot_prompt,
    ("human", "{input}"),
])

chain = final_prompt | llm | StrOutputParser()
result = chain.invoke({"input": "Python列表怎么去重？"})
print("\n=== Few-shot ===")
print(result)

# --- 动态Prompt（根据条件选择模板） ---
from langchain_core.runnables import RunnableLambda

def choose_prompt(input_dict):
    """根据问题类型选择不同模板"""
    question = input_dict["question"]
    if "代码" in question or "编程" in question:
        return ChatPromptTemplate.from_messages([
            ("system", "你是编程助手。只返回代码，不解释。"),
            ("user", "{question}"),
        ])
    else:
        return ChatPromptTemplate.from_messages([
            ("system", "你是通用助手。回答简洁。"),
            ("user", "{question}"),
        ])

dynamic_chain = (
    RunnableLambda(choose_prompt)
    | llm
    | StrOutputParser()
)

print("\n=== 动态Prompt ===")
print(dynamic_chain.invoke({"question": "代码：冒泡排序"}))
```

### 5.1.3 输出解析器

```python
# python4javaProgrammer/ai_demo/langchain_parser.py
"""LangChain 输出解析器"""
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

# --- 1. 字符串解析器 ---
str_chain = (
    ChatPromptTemplate.from_messages([("user", "{question}")])
    | llm
    | StrOutputParser()
)

# --- 2. JSON解析器 ---
json_parser = JsonOutputParser()
json_chain = (
    ChatPromptTemplate.from_messages([
        ("system", "返回JSON格式: {format_instructions}"),
        ("user", "{question}"),
    ]).partial(format_instructions=json_parser.get_format_instructions())
    | llm
    | json_parser
)
result = json_chain.invoke({"question": "介绍Python的三个特点"})
print("=== JSON解析器 ===")
print(result)

# --- 3. Pydantic解析器（推荐：类型安全） ---
class CodeReview(BaseModel):
    """代码审查结果"""
    issues: List[str] = Field(description="发现的问题列表")
    severity: str = Field(description="严重程度: low/medium/high")
    suggestion: str = Field(description="改进建议")

pydantic_parser = PydanticOutputParser(pydantic_object=CodeReview)

review_chain = (
    ChatPromptTemplate.from_messages([
        ("system", "你是一个代码审查专家。\n{format_instructions}"),
        ("user", "审查以下代码:\n{code}"),
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
print("\n=== Pydantic解析器 ===")
print(f"问题: {review.issues}")
print(f"严重度: {review.severity}")
print(f"建议: {review.suggestion}")

# --- 4. 列表解析器 ---
list_parser = CommaSeparatedListOutputParser()
list_chain = (
    ChatPromptTemplate.from_messages([
        ("user", "列出{count}个Python Web框架。\n{format_instructions}"),
    ]).partial(format_instructions=list_parser.get_format_instructions())
    | llm
    | list_parser
)
frameworks = list_chain.invoke({"count": 5})
print("\n=== 列表解析器 ===")
print(frameworks)  # ['FastAPI', 'Django', 'Flask', 'Tornado', 'Sanic']
```

### 5.1.4 Tool与Agent

```python
# python4javaProgrammer/ai_demo/langchain_tools_agent.py
"""LangChain 工具与Agent"""
from langchain_openai import ChatOpenAI
from langchain_core.tools import tool, Tool
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain.agents import create_tool_calling_agent, AgentExecutor
from langchain_core.messages import HumanMessage, AIMessage
from dotenv import load_dotenv
import datetime
import requests

load_dotenv()

# --- 自定义工具 ---
@tool
def get_current_time() -> str:
    """获取当前时间"""
    return datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")

@tool
def calculate(expression: str) -> str:
    """计算数学表达式。输入: 数学表达式字符串，如 '2+3*4'"""
    try:
        # 安全的eval（实际项目用ast.literal_eval或专门库）
        allowed = set("0123456789+-*/.() ")
        if not all(c in allowed for c in expression):
            return "只支持数字和基本运算符"
        result = eval(expression)
        return str(result)
    except Exception as e:
        return f"计算失败: {e}"

@tool
def search_web(query: str) -> str:
    """搜索网络信息（模拟）"""
    # 实际项目中调用搜索API
    return f"关于 '{query}' 的搜索结果: Python 3.12于2023年发布，性能提升显著..."

# --- 使用 @tool 装饰器的工具 ---
tools = [get_current_time, calculate, search_web]

# --- 创建Agent ---
llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)

prompt = ChatPromptTemplate.from_messages([
    ("system", """你是一个智能助手，可以使用以下工具:
{tools}

请根据用户问题决定是否使用工具。如果需要使用工具，说明理由。"""),
    MessagesPlaceholder(variable_name="chat_history", optional=True),
    ("user", "{input}"),
    ("assistant", "工具使用记录: {agent_scratchpad}"),
])

agent = create_tool_calling_agent(llm, tools, prompt)
agent_executor = AgentExecutor(agent=agent, tools=tools, verbose=True)

# --- 执行 ---
print("=== 测试1: 时间查询 ===")
result = agent_executor.invoke({"input": "现在几点了？"})
print(result["output"])

print("\n=== 测试2: 数学计算 ===")
result = agent_executor.invoke({"input": "计算 (15 + 27) * 3 - 10"})
print(result["output"])

print("\n=== 测试3: 组合任务 ===")
result = agent_executor.invoke({
    "input": "先告诉我现在时间，然后搜索Python 3.12的新特性，最后计算 365 * 24"
})
print(result["output"])

# --- 带历史对话的Agent ---
print("\n=== 测试4: 带历史对话 ===")
chat_history = []
question = "我叫张三，记住我的名字"
result = agent_executor.invoke({"input": question, "chat_history": chat_history})
chat_history.append(HumanMessage(content=question))
chat_history.append(AIMessage(content=result["output"]))
print(result["output"])

# 追问
question2 = "我叫什么名字？"
result2 = agent_executor.invoke({"input": question2, "chat_history": chat_history})
print(result2["output"])
```

---

## 5.2 LangGraph：状态编排

### 5.2.1 StateGraph 基础

```python
# python4javaProgrammer/ai_demo/langgraph_basic.py
"""LangGraph StateGraph 状态图编排"""
from langgraph.graph import StateGraph, END, START
from typing import TypedDict, Annotated, Literal
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, AIMessage
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from dotenv import load_dotenv
import operator

load_dotenv()

# --- 定义状态 ---
class AgentState(TypedDict):
    """Agent状态定义"""
    messages: Annotated[list, operator.add]  # 消息列表（可累加）
    question: str
    context: str
    answer: str
    needs_search: bool

# --- 定义节点函数 ---
llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)

def analyze_question(state: AgentState) -> dict:
    """分析问题：判断是否需要检索"""
    question = state["question"]
    prompt = ChatPromptTemplate.from_messages([
        ("system", """分析用户问题，判断是否需要搜索外部知识。
回答JSON格式: {{"needs_search": true/false, "reason": "..."}}
- 如果是常识问题或编程基础，不需要搜索
- 如果涉及最新信息或特定领域知识，需要搜索"""),
        ("user", "{question}"),
    ])
    chain = prompt | llm | StrOutputParser()
    result = chain.invoke({"question": question})

    import json
    try:
        analysis = json.loads(result)
        needs_search = analysis.get("needs_search", False)
    except:
        needs_search = "搜索" in question or "最新" in question

    return {
        "needs_search": needs_search,
        "messages": [AIMessage(content=f"分析结果: needs_search={needs_search}")],
    }

def search_context(state: AgentState) -> dict:
    """检索上下文（模拟RAG检索）"""
    question = state["question"]
    # 实际项目中调用LlamaIndex或向量库
    context = f"检索到的相关知识: 关于'{question}'的技术文档片段..."
    return {
        "context": context,
        "messages": [AIMessage(content=f"检索完成: {context}")],
    }

def generate_answer(state: AgentState) -> dict:
    """生成回答"""
    question = state["question"]
    context = state.get("context", "")

    if context:
        prompt = ChatPromptTemplate.from_messages([
            ("system", "根据以下上下文回答问题。\n上下文: {context}"),
            ("user", "{question}"),
        ])
    else:
        prompt = ChatPromptTemplate.from_messages([
            ("system", "你是Python编程助手。"),
            ("user", "{question}"),
        ])

    chain = prompt | llm | StrOutputParser()
    answer = chain.invoke({"question": question, "context": context})

    return {
        "answer": answer,
        "messages": [AIMessage(content=answer)],
    }

# --- 构建图 ---
workflow = StateGraph(AgentState)

# 添加节点
workflow.add_node("analyze", analyze_question)
workflow.add_node("search", search_context)
workflow.add_node("generate", generate_answer)

# 添加边
workflow.add_edge(START, "analyze")

# 条件边：根据分析结果决定路径
def route_after_analysis(state: AgentState) -> Literal["search", "generate"]:
    """路由函数"""
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

# 编译
app = workflow.compile()

# --- 执行 ---
print("=== 测试1: 不需要搜索 ===")
result = app.invoke({
    "question": "Python的列表推导式怎么写？",
    "messages": [],
    "context": "",
    "answer": "",
    "needs_search": False,
})
print(f"回答: {result['answer']}")
print(f"消息数: {len(result['messages'])}")

print("\n=== 测试2: 需要搜索 ===")
result = app.invoke({
    "question": "搜索Python 3.12的最新特性",
    "messages": [],
    "context": "",
    "answer": "",
    "needs_search": False,
})
print(f"回答: {result['answer']}")
print(f"上下文: {result['context']}")
```

### 5.2.2 条件边与多轮对话Agent

```python
# python4javaProgrammer/ai_demo/langgraph_multi_turn.py
"""LangGraph 多轮对话Agent + Human-in-loop"""
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

# --- 状态定义 ---
class MultiTurnState(TypedDict):
    messages: Annotated[list, operator.add]
    user_input: str
    awaiting_human: bool

# --- 工具 ---
@tool
def search_document(query: str) -> str:
    """搜索上传的文档"""
    return f"找到与'{query}'相关的文档内容: [模拟文档片段]"

@tool
def ask_human(question: str) -> str:
    """当需要人类确认时调用此工具"""
    return f"等待人类确认: {question}"

tools = [search_document, ask_human]
tool_node = ToolNode(tools)

# --- LLM ---
llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
llm_with_tools = llm.bind_tools(tools)

# --- 节点 ---
def agent_node(state: MultiTurnState) -> dict:
    """Agent主节点"""
    messages = state["messages"]
    if not any(isinstance(m, SystemMessage) for m in messages):
        messages = [SystemMessage(content="你是文档问答助手。可以搜索文档或在需要确认时询问人类。")] + messages

    response = llm_with_tools.invoke(messages)
    return {"messages": [response]}

def should_continue(state: MultiTurnState) -> Literal["tools", "__end__"]:
    """判断是否继续执行工具"""
    last_message = state["messages"][-1]
    if hasattr(last_message, "tool_calls") and last_message.tool_calls:
        # 检查是否需要人类介入
        for tc in last_message.tool_calls:
            if tc["name"] == "ask_human":
                return "tools"  # 先走工具节点（实际中可插入中断）
        return "tools"
    return END

# --- 构建图 ---
workflow = StateGraph(MultiTurnState)

workflow.add_node("agent", agent_node)
workflow.add_node("tools", tool_node)

workflow.add_edge(START, "agent")
workflow.add_conditional_edges("agent", should_continue, ["tools", END])
workflow.add_edge("tools", "agent")  # 工具执行后回到agent

# 编译（带记忆）
memory = MemorySaver()
app = workflow.compile(checkpointer=memory)

# --- 多轮对话测试 ---
config = {"configurable": {"thread_id": "conversation-001"}}

print("=== 第一轮 ===")
result = app.invoke(
    {"messages": [HumanMessage(content="帮我搜索Python异步编程的文档")], "user_input": "", "awaiting_human": False},
    config=config,
)
last = result["messages"][-1]
print(f"AI: {last.content if hasattr(last, 'content') else last}")

print("\n=== 第二轮（追问） ===")
result = app.invoke(
    {"messages": [HumanMessage(content="能详细说说async/await吗？")], "user_input": "", "awaiting_human": False},
    config=config,
)
last = result["messages"][-1]
print(f"AI: {last.content if hasattr(last, 'content') else last}")

print("\n=== 第三轮（需要确认） ===")
result = app.invoke(
    {"messages": [HumanMessage(content="删除所有文档前需要我确认吗？")], "user_input": "", "awaiting_human": False},
    config=config,
)
last = result["messages"][-1]
print(f"AI: {last.content if hasattr(last, 'content') else last}")
```

### 5.2.3 Human-in-loop

```python
# python4javaProgrammer/ai_demo/langgraph_human_in_loop.py
"""LangGraph Human-in-loop 人工审核"""
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
    """生成草稿"""
    prompt = ChatPromptTemplate.from_messages([
        ("system", "根据用户要求生成回复草稿。"),
        ("user", "{input}"),
    ])
    chain = prompt | llm | StrOutputParser()
    draft = chain.invoke({"input": state["messages"][-1].content})
    return {
        "draft": draft,
        "messages": [AIMessage(content=f"草稿: {draft}")],
    }

def human_review(state: ReviewState) -> dict:
    """人工审核节点（实际中通过UI中断）"""
    # 在实际应用中，这里会暂停等待人工输入
    # LangGraph 支持 interrupt_before 配置
    return {"approved": True, "feedback": ""}

def revise_draft(state: ReviewState) -> dict:
    """根据反馈修改"""
    prompt = ChatPromptTemplate.from_messages([
        ("system", "根据反馈修改回复。原草稿: {draft}\n反馈: {feedback}"),
        ("user", "请修改"),
    ])
    chain = prompt | llm | StrOutputParser()
    revised = chain.invoke({"draft": state["draft"], "feedback": state["feedback"]})
    return {
        "draft": revised,
        "messages": [AIMessage(content=f"修订版: {revised}")],
    }

def route_review(state: ReviewState) -> Literal["revise", "__end__"]:
    """根据审核结果路由"""
    if state["approved"]:
        return END
    return "revise"

# 构建图
workflow = StateGraph(ReviewState)
workflow.add_node("generate", generate_draft)
workflow.add_node("review", human_review)
workflow.add_node("revise", revise_draft)

workflow.add_edge(START, "generate")
workflow.add_edge("generate", "review")
workflow.add_conditional_edges("review", route_review, ["revise", END])
workflow.add_edge("revise", "review")  # 修改后再次审核

# 编译时指定中断点
memory = MemorySaver()
app = workflow.compile(
    checkpointer=memory,
    interrupt_before=["review"],  # 在审核节点前暂停
)

# 使用:
# 1. 运行到review前会暂停
# 2. 人工查看state["draft"]
# 3. 用 app.invoke({"approved": False, "feedback": "更简洁些"}, config) 继续
```

---

## 5.3 LlamaIndex：RAG完整流程

### 5.3.1 文档加载与分块

```python
# python4javaProgrammer/ai_demo/llamaindex_index.py
"""LlamaIndex 文档加载与分块"""
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

# --- 配置全局Settings ---
Settings.llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")
Settings.chunk_size = 512
Settings.chunk_overlap = 50

# --- 1. 文档加载 ---

# 方式A: 从目录加载
# data/docs/ 下放 .txt, .md, .pdf 文件
if os.path.exists("data/docs"):
    reader = SimpleDirectoryReader(
        input_dir="data/docs",
        required_exts=[".txt", ".md", ".pdf"],
        recursive=True,
    )
    documents = reader.load_data()
    print(f"加载了 {len(documents)} 个文档")
else:
    # 方式B: 手动创建文档
    documents = [
        Document(
            text="""
            Python是一种高级编程语言，由Guido van Rossum于1991年创建。
            Python支持多种编程范式：面向对象、命令式、函数式和过程式。
            Python的哲学强调代码的可读性和简洁性。
            Python广泛应用于Web开发、数据科学、人工智能、自动化等领域。
            Python的包管理工具pip使得安装第三方库非常方便。
            Python的虚拟环境venv可以创建隔离的依赖环境。
            """,
            metadata={"source": "python_intro.md", "author": "tutorial"},
        ),
        Document(
            text="""
            FastAPI是一个现代的、快速的Web框架，用于构建Python API。
            FastAPI基于标准Python类型提示，自动生成交互式文档。
            FastAPI原生支持异步编程，性能接近Node.js和Go。
            FastAPI使用Pydantic进行数据校验和序列化。
            FastAPI支持WebSocket、GraphQL等高级特性。
            """,
            metadata={"source": "fastapi_intro.md", "author": "tutorial"},
        ),
    ]
    print(f"创建了 {len(documents)} 个示例文档")

# --- 2. 文本分块 ---
splitter = SentenceSplitter(
    chunk_size=512,
    chunk_overlap=50,
    separator="。\n",  # 中文分隔符
)

nodes = splitter.get_nodes_from_documents(documents)
print(f"\n分块后得到 {len(nodes)} 个节点")
for i, node in enumerate(nodes[:3]):
    print(f"\n--- 节点 {i} ---")
    print(f"文本: {node.text[:100]}...")
    print(f"元数据: {node.metadata}")

# --- 3. 向量索引（ChromaDB） ---

# 初始化ChromaDB
db = chromadb.PersistentClient(path="./data/chroma")
chroma_collection = db.get_or_create_collection("ai_native_dev")

# 创建向量存储
vector_store = ChromaVectorStore(chroma_collection=chroma_collection)
storage_context = StorageContext.from_defaults(vector_store=vector_store)

# 构建索引
index = VectorStoreIndex(
    nodes=nodes,
    storage_context=storage_context,
    show_progress=True,
)

print("\n索引构建完成！")

# --- 4. 持久化 ---
# ChromaDB自动持久化到指定路径
# 下次可直接加载:
# vector_store = ChromaVectorStore(chroma_collection=chroma_collection)
# index = VectorStoreIndex.from_vector_store(vector_store)
```

### 5.3.2 查询引擎与RAG

```python
# python4javaProgrammer/ai_demo/llamaindex_query.py
"""LlamaIndex 查询引擎与RAG流程"""
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

# --- 配置 ---
Settings.llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# --- 加载已有索引 ---
db = chromadb.PersistentClient(path="./data/chroma")
chroma_collection = db.get_or_create_collection("ai_native_dev")
vector_store = ChromaVectorStore(chroma_collection=chroma_collection)
index = VectorStoreIndex.from_vector_store(vector_store)

# --- 1. 基础查询引擎 ---
query_engine = index.as_query_engine(
    similarity_top_k=3,                    # 检索top-3相关文档
    response_mode=ResponseMode.COMPACT,   # 响应模式
)

print("=== 基础查询 ===")
response = query_engine.query("Python有哪些应用领域？")
print(f"回答: {response}")
print(f"来源: {[n.metadata for n in response.source_nodes]}")

# --- 2. 自定义检索器 ---
retriever = VectorIndexRetriever(
    index=index,
    similarity_top_k=5,
)

# 相似度过滤器（只保留相关度>0.7的）
postprocessor = SimilarityPostprocessor(similarity_cutoff=0.5)

# 响应合成器
from llama_index.core.response_synthesizers import CompactAndRefine

custom_engine = RetrieverQueryEngine(
    retriever=retriever,
    node_postprocessors=[postprocessor],
)

print("\n=== 自定义检索 ===")
response = custom_engine.query("FastAPI的特性是什么？")
print(f"回答: {response}")
print(f"检索到 {len(response.source_nodes)} 个相关文档")

# --- 3. 检索 + 生成 分步执行 ---
print("\n=== 分步执行 ===")

# 步骤A: 只检索
retrieved_nodes = retriever.retrieve("Python的包管理工具")
print(f"检索到 {len(retrieved_nodes)} 个节点:")
for i, node in enumerate(retrieved_nodes):
    print(f"  [{i}] score={node.score:.4f} text={node.text[:50]}...")

# 步骤B: 只生成（给定上下文）
from llama_index.core.response_synthesizers import Generation

synthesizer = CompactAndRefine()
response = synthesizer.synthesize(
    query="Python的包管理工具是什么？",
    nodes=retrieved_nodes,
)
print(f"回答: {response}")

# --- 4. 流式查询 ---
print("\n=== 流式查询 ===")
streaming_engine = index.as_query_engine(
    similarity_top_k=3,
    streaming=True,
)
response_stream = streaming_engine.query("介绍Python的编程范式")
for text in response_stream.response_gen:
    print(text, end="", flush=True)
print()

# --- 5. 聊天引擎（多轮对话） ---
print("\n=== 聊天引擎 ===")
chat_engine = index.as_chat_engine(
    chat_mode="context",  # context模式: 保持检索 + 历史
    similarity_top_k=3,
)

# 第一轮
response = chat_engine.chat("Python是谁创建的？")
print(f"Q: Python是谁创建的？")
print(f"A: {response}")

# 第二轮（有上下文）
response = chat_engine.chat("他是哪一年创建的？")
print(f"Q: 他是哪一年创建的？")
print(f"A: {response}")

# 第三轮
response = chat_engine.chat("这个语言有什么特点？")
print(f"Q: 这个语言有什么特点？")
print(f"A: {response}")

# 重置聊天
chat_engine.reset()
```

---

## 本章小结

| 框架 | 核心定位 | 关键概念 | 使用场景 |
|------|---------|---------|---------|
| LangChain | 工具与链 | LCEL链、Prompt模板、输出解析器、Agent | 构建工具调用、自定义流程 |
| LangGraph | 状态编排 | StateGraph、条件边、多轮对话、Human-in-loop | 复杂工作流、状态机 |
| LlamaIndex | RAG索引 | 文档加载、分块、向量索引、查询引擎 | 文档问答、知识库检索 |

三框架协同模式：
- **LlamaIndex** 负责文档加载、分块、向量化、检索
- **LangChain** 提供Prompt模板、输出解析器、自定义工具
- **LangGraph** 编排整体流程（检索→生成→审核→输出）
