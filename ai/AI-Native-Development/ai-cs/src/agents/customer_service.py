"""Agent 组装（M4，T4.2）。

create_agent（LangChain 1.x 入口，AgentExecutor 已移除）+ ChatDeepSeek(temperature=0.1)。
system_prompt 明确工具边界（架构文档 3.4），是防工具乱选的关键。
"""

import os

from langchain.agents import create_agent
from langchain_deepseek import ChatDeepSeek

from src.core.config import Config

SYSTEM_PROMPT = """你是企业智能客服，负责回答员工关于公司规章制度与业务数据的问题。
规则：
1. 制度类问题（假期/报销/差旅/IT流程/员工手册等）→ 调 search_policy 检索文档作答；
2. 查员工信息（工号/姓名）→ 调 query_employee_db；
3. 查工单/报销单状态（单号）→ 调 query_ticket_status；
4. 只依据工具返回的内容回答，没有相关内容就说"知识库中未找到"，禁止编造；
5. 回答简洁准确，制度类问题注明出处。"""


def build_agent(tools):
    """组装 Agent。tools 来自 tools.langchain_tools.build_tools(retriever)。"""
    model = ChatDeepSeek(
        model=Config.LLM_MODEL,
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        temperature=Config.LLM_TEMPERATURE,   # 问答要稳定
    )
    return create_agent(model, tools, system_prompt=SYSTEM_PROMPT)
