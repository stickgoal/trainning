"""api 层单元测试：schemas 校验 + 健康检查 + 响应解析（不调 LLM）。

- /health 与 503（无 Key）不依赖 agent，离线可测；
- _parse_result 是纯函数，用构造的假消息验证；
- chat 成功路径用 monkeypatch 假 agent 验证响应结构。
"""

import pytest
from fastapi.testclient import TestClient
from langchain_core.messages import AIMessage, ToolMessage

from src.api.app import _parse_result, app
from src.api.schemas import ChatRequest, ChatResponse


@pytest.fixture(scope="module")
def client():
    return TestClient(app)


# ---------- schemas ----------

def test_chat_request_valid():
    req = ChatRequest(question="年假有几天")
    assert req.question == "年假有几天"


def test_chat_request_empty_invalid():
    with pytest.raises(Exception):
        ChatRequest(question="")


def test_chat_request_too_long_invalid():
    with pytest.raises(Exception):
        ChatRequest(question="长" * 501)


def test_chat_response_model():
    resp = ChatResponse(answer="10 天", sources=["a"], tool_calls=["search_policy"], elapsed_ms=12)
    assert resp.answer == "10 天"
    assert resp.elapsed_ms == 12


# ---------- _parse_result（纯函数） ----------

def test_parse_result_extracts_answer_sources_tools():
    """从 agent 消息中提取 answer / sources（[来源:]行）/ tool_calls。"""
    result = {
        "messages": [
            AIMessage(
                content="",
                tool_calls=[
                    {"name": "search_policy", "args": {"query": "x"}, "id": "1"}
                ],
            ),
            ToolMessage(
                name="search_policy",
                content=(
                    "[来源: knowledge_docs/hr/leave_policy.txt]\n年假规则...\n\n"
                    "[来源: knowledge_docs/hr/leave_policy.txt]\n重复来源"
                ),
                tool_call_id="1",
            ),
            AIMessage(content="最终回答"),
        ]
    }
    answer, sources, tool_calls = _parse_result(result)
    assert answer == "最终回答"
    assert sources == ["knowledge_docs/hr/leave_policy.txt"]   # 去重保序
    assert tool_calls == ["search_policy"]


def test_parse_result_no_sources():
    """无工具调用/无来源时返回空列表。"""
    result = {"messages": [AIMessage(content="直接回答")]}
    answer, sources, tool_calls = _parse_result(result)
    assert answer == "直接回答"
    assert sources == []
    assert tool_calls == []


# ---------- /health ----------

def test_health_ok(client):
    resp = client.get("/health")
    assert resp.status_code == 200
    assert resp.json() == {"status": "ok"}


# ---------- /api/chat 无 Key → 503 ----------

def test_chat_503_without_api_key(client, monkeypatch):
    monkeypatch.delenv("DEEPSEEK_API_KEY", raising=False)
    resp = client.post("/api/chat", json={"question": "年假有几天"})
    assert resp.status_code == 503


def test_chat_422_empty_question(client):
    resp = client.post("/api/chat", json={"question": ""})
    assert resp.status_code == 422


# ---------- /api/chat 成功路径（假 agent，不耗 Key/模型） ----------

def test_chat_success_with_mocked_agent(client, monkeypatch):
    """monkeypatch 假 agent：验证响应结构含 answer/sources/tool_calls/elapsed_ms。"""

    class _FakeAgent:
        def invoke(self, inputs):
            return {
                "messages": [
                    AIMessage(
                        content="",
                        tool_calls=[
                            {"name": "search_policy", "args": {}, "id": "1"}
                        ],
                    ),
                    ToolMessage(
                        name="search_policy",
                        content="[来源: knowledge_docs/hr/leave_policy.txt]\n规则",
                        tool_call_id="1",
                    ),
                    AIMessage(content="年假 10 天"),
                ]
            }

    monkeypatch.setenv("DEEPSEEK_API_KEY", "sk-test")
    from src.api import app as app_module

    monkeypatch.setattr(app_module, "_get_agent", lambda: _FakeAgent())
    resp = client.post("/api/chat", json={"question": "年假有几天"})
    assert resp.status_code == 200
    body = resp.json()
    assert body["answer"] == "年假 10 天"
    assert body["tool_calls"] == ["search_policy"]
    assert body["sources"] == ["knowledge_docs/hr/leave_policy.txt"]
    assert isinstance(body["elapsed_ms"], int)
