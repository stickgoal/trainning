"""
============================================================
 11_case_knowledge_agent.py — 综合案例1：企业知识库 Agent
 对应章节：第13章 案例1
============================================================

技术栈：Python + LangChain + RAG + Vector Database (Chroma) + MCP

流程：
 1. 用 MCP 暴露企业数据库查询工具
 2. 用 RAG 把企业知识文档入库到 Chroma
 3. 用 LangChain 组装 Agent：LLM 同时能调 RAG 检索 + MCP 工具
 4. 用户提问 → Agent 自主选择检索文档 / 查数据库 / 直接回答

【运行流程】
 pip install langchain langchain-community langchain-deepseek
 pip install chromadb sentence-transformers mcp
 set DEEPSEEK_API_KEY=sk-xxx
 python examples/12_case_knowledge_agent.py

【应用场景】
 企业内部 "问知识 + 查数据" 的统一助手。
 一个 Agent 同时覆盖文档知识库（RAG）和企业业务系统（MCP 工具）。

【Python 特殊语法提示】
 - @tool decorator        : 函数转 Tool
 - async def / await     : 异步（MCP 调用是异步的）
 - lambda                : 匿名函数
 - LCEL chain 组合         : prompt | retriever | model | parser
"""

import os
from dataclasses import dataclass


# =================================================================
# 第 1 部分：MCP 工具 —— 暴露企业 DB 查询
# =================================================================
def build_mcp_tool():
    """
    模拟一个 MCP 工具：查询员工信息。
    真实实现：用 FastMCP 起 Server，再用 MCP Client 接进来。
    此处直接包成 LangChain Tool 给 Agent 用（演示逻辑等价）。
    """
    from langchain_core.tools import tool

    @tool
    def query_employee_db(emp_id: str) -> str:
        """
        查询企业 HR 系统中的员工信息。
        参数 emp_id: 员工工号（如 E001）
        当用户问到具体员工时调用。
        """
        db = {
            "E001": "张三 / 研发部 / 高级工程师",
            "E002": "李四 / 产品部 / 产品经理",
            "E003": "王五 / 设计部 / UI设计师",
        }
        return db.get(emp_id, f"工号 {emp_id} 不存在")

    return query_employee_db


# =================================================================
# 第 2 部分：RAG —— 企业知识文档入库
# =================================================================
def build_knowledge_retriever():
    """把企业制度文档加载、切分、入 Chroma 向量库，返回 Retriever。"""
    from langchain_community.document_loaders import TextLoader
    from langchain_text_splitters import RecursiveCharacterTextSplitter
    from langchain_community.embeddings import SentenceTransformerEmbeddings
    from langchain_community.vectorstores import Chroma

    # 模拟一份企业制度文档
    doc_path = "company_policy.txt"
    with open(doc_path, "w", encoding="utf-8") as f:
        f.write("""\
【出差报销制度】
- 交通：实报实销，机票经济舱
- 住宿：一线城市 500/晚，二线 350/晚
- 餐补：80 元/天

【年假制度】
- 满 1 年 5 天
- 满 3 年 10 天
- 满 10 年 15 天
""")

    docs = TextLoader(doc_path, encoding="utf-8").load()
    chunks = RecursiveCharacterTextSplitter(chunk_size=100, chunk_overlap=20).split_documents(docs)
    embeddings = SentenceTransformerEmbeddings(model_name="BAAI/bge-small-zh-v1.5")
    db = Chroma.from_documents(chunks, embeddings)
    return db.as_retriever(search_kwargs={"k": 2})


# =================================================================
# 第 3 部分：组装 Agent —— 同时能用 RAG + MCP 工具
# =================================================================
def build_knowledge_agent():
    """
    组装一个企业知识库 Agent：
    - 用户问制度 → 自动检索文档
    - 用户问员工 → 自动调 MCP 工具查 DB
    """
    from langchain_deepseek import ChatDeepSeek
    from langchain.agents import create_agent

    retriever = build_knowledge_retriever()
    mcp_tool = build_mcp_tool()

    # 把 RAG 也包成 Tool，让 Agent 能调
    from langchain_core.tools import tool

    @tool
    def search_policy(query: str) -> str:
        """搜索企业制度文档。当用户问报销/年假/出差制度时调用。"""
        docs = retriever.invoke(query)
        return "\n".join(d.page_content for d in docs)

    tools = [search_policy, mcp_tool]  # RAG Tool + MCP Tool

    model = ChatDeepSeek(
        model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        temperature=0.1,
    )

    # create_agent：组装一个能同时用 RAG + MCP 工具的 Agent
    # （旧版 create_tool_calling_agent / AgentExecutor 在 LangChain 1.x 已移除）
    agent = create_agent(
        model,
        tools,
        system_prompt="你是企业知识库助手。可以用 search_policy 查制度，用 query_employee_db 查员工。",
    )
    return agent


# =================================================================
# 主流程
# =================================================================
def main() -> None:
    if not os.getenv("DEEPSEEK_API_KEY"):
        print("⚠️  需要 DEEPSEEK_API_KEY。本案例演示用 LLM 做工具选择。")
        print("   可参考代码理解流程：RAG 检索 + MCP 工具 + LangChain Agent 编排。")
        return

    print("=" * 55)
    print("综合案例1：企业知识库 Agent")
    print("=" * 55)

    agent = build_knowledge_agent()

    questions = [
        "出差住宿每晚最多能报多少？",     # → 应触发 search_policy
        "查一下工号 E001 的员工信息",      # → 应触发 query_employee_db
        "年假满 3 年有几天？",             # → 应触发 search_policy
    ]

    for q in questions:
        print(f"\n❓ 用户: {q}")
        result = agent.invoke({"messages": [{"role": "user", "content": q}]})
        print(f"✅ Agent: {result['messages'][-1].content}")


if __name__ == "__main__":
    main()
