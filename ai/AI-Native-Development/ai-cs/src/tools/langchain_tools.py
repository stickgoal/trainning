"""LangChain 工具定义（M4，T4.1）。

三个工具，docstring 是 LLM 选工具的唯一依据，措辞按架构文档 3.3：
- search_policy：制度/政策/标准 → RAG 检索（返回片段 + 内联来源行）
- query_employee_db：员工信息（工号）→ 模拟 HR DB
- query_ticket_status：工单/报销单状态（单号）→ 模拟工单 DB

工具内 try/except 返回友好错误文本，不抛异常打断 Agent 循环（实施注意 4.2）。
"""

from langchain_core.tools import tool

from src.rag.chain import format_docs_with_sources
from src.tools import mock_data


def build_tools(retriever) -> list:
    """组装三个工具，返回 list[BaseTool]。

    retriever 由 rag/retriever.build_retriever() 提供（basic 或 hybrid）。
    """

    @tool
    def search_policy(query: str) -> str:
        """搜索企业规章制度文档（年假/病假/报销/差旅/IT支持等制度条款）。
当用户询问公司制度、政策、标准、流程时调用。
参数 query: 用户的问题或检索关键词。"""
        try:
            docs = retriever.invoke(query)
            return format_docs_with_sources(docs)
        except Exception as e:  # 检索失败不打断 Agent，给友好提示
            return f"制度检索失败：{e}"

    @tool
    def query_employee_db(emp_id: str) -> str:
        """查询员工信息（姓名/部门/职位/入职日期）。参数 emp_id: 员工工号，如 E001。
当用户提到具体工号或查某位员工时调用。"""
        return mock_data.query_employee(emp_id)

    @tool
    def query_ticket_status(ticket_id: str) -> str:
        """查询报销单/工单的处理状态。参数 ticket_id: 单号，如 RB2024001。
当用户询问报销单或工单当前状态时调用。"""
        return mock_data.query_ticket(ticket_id)

    return [search_policy, query_employee_db, query_ticket_status]
