"""tools 模块单元测试：build_tools 组装 + 各工具返回格式。

离线；search_policy 用假 retriever（不加载向量库），业务工具直连 mock_data。
"""

from langchain_core.documents import Document

from src.tools import mock_data
from src.tools.langchain_tools import build_tools

_TOOL_NAMES = {"search_policy", "query_employee_db", "query_ticket_status"}


class _FakeRetriever:
    """返回固定片段的假检索器。"""

    def invoke(self, query: str) -> list[Document]:
        return [
            Document(
                page_content="年假：入职满 3 年不满 10 年，年假 10 天/年。",
                metadata={"source": "knowledge_docs/hr/leave_policy.txt", "domain": "hr"},
            ),
            Document(
                page_content="住宿：一线城市每晚上限 500 元。",
                metadata={"source": "knowledge_docs/finance/travel_policy.txt", "domain": "finance"},
            ),
        ]


def test_build_tools_returns_three_named_tools():
    tools = build_tools(_FakeRetriever())
    assert len(tools) == 3
    assert {t.name for t in tools} == _TOOL_NAMES


def test_build_tools_all_return_str():
    tools = build_tools(_FakeRetriever())
    assert all(isinstance(t.description, str) for t in tools)


def test_search_policy_returns_inline_source():
    """search_policy 返回片段 + [来源: ...] 行（供 API 解析 sources）。"""
    retriever = _FakeRetriever()
    tools = {t.name: t for t in build_tools(retriever)}
    out = tools["search_policy"].invoke({"query": "年假有几天"})
    assert isinstance(out, str)
    assert "年假" in out
    assert "[来源: knowledge_docs/hr/leave_policy.txt]" in out
    assert "[来源: knowledge_docs/finance/travel_policy.txt]" in out


def test_search_policy_retriever_error_friendly(monkeypatch):
    """检索器异常时返回友好文本而非抛异常（实施注意 4.2）。"""

    class _BoomRetriever:
        def invoke(self, query):
            raise RuntimeError("vector store down")

    tools = {t.name: t for t in build_tools(_BoomRetriever())}
    out = tools["search_policy"].invoke({"query": "x"})
    assert "检索失败" in out


def test_query_employee_db_via_tool():
    tools = {t.name: t for t in build_tools(_FakeRetriever())}
    out = tools["query_employee_db"].invoke({"emp_id": "E001"})
    assert "张三" in out and "研发部" in out and "入职日期" in out


def test_query_employee_db_invalid_id_friendly():
    out = mock_data.query_employee("E999")
    assert "不存在" in out


def test_query_ticket_status_via_tool():
    tools = {t.name: t for t in build_tools(_FakeRetriever())}
    out = tools["query_ticket_status"].invoke({"ticket_id": "RB2024001"})
    assert "审批中" in out


def test_query_ticket_invalid_id_friendly():
    out = mock_data.query_ticket("RB9999999")
    assert "不存在" in out


def test_tool_docstrings_describe_triggers():
    """docstring 是 LLM 选工具的唯一依据，必须含触发条件措辞。"""
    tools = {t.name: t for t in build_tools(_FakeRetriever())}
    assert "当用户询问公司制度" in tools["search_policy"].description
    assert "当用户提到具体工号" in tools["query_employee_db"].description
    assert "当用户询问报销单或工单" in tools["query_ticket_status"].description
