"""纯 RAG 问答链（M2，T2.5）——不走 Agent，供评估/对比/教学用。

⚠️ LCEL 陷阱：链起点是 dict，retriever 必须用 itemgetter("question") 抽出字符串，
否则会把整个 dict 喂给 retriever 报 `AttributeError: 'dict' object has no attribute 'replace'`。
"""

import os
from operator import itemgetter

from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_deepseek import ChatDeepSeek

from src.core.config import Config

# 防幻觉 Prompt（措辞对齐课程 04_rag_demo，含来源要求）
RAG_PROMPT = ChatPromptTemplate.from_template(
    """你是企业制度客服助手，根据以下检索到的制度片段回答问题。
要求：
1. 只基于片段内容回答，禁止编造。
2. 如果片段中没有答案，明确回答"知识库中未找到相关内容"。
3. 回答末尾注明来源文档。

【制度片段】
{context}

【问题】
{question}
""".strip()
)


def _model() -> ChatDeepSeek:
    return ChatDeepSeek(
        model=Config.LLM_MODEL,
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        temperature=Config.LLM_TEMPERATURE,
    )


def format_docs_with_sources(docs) -> str:
    """把检索片段拼成带来源行的文本（工具返回与 chain context 共用）。"""
    blocks = []
    for d in docs:
        src = d.metadata.get("source", "未知来源")
        blocks.append(f"[来源: {src}]\n{d.page_content}")
    return "\n\n".join(blocks)


def build_rag_chain(retriever):
    """组装 LCEL 链，输入 {"question": str}，输出 str。

    结构（对齐架构文档 3.2）：
        {"context": itemgetter("question") | retriever | format,
         "question": itemgetter("question")}
        | RAG_PROMPT | model | StrOutputParser()
    """
    return (
        {
            "context": itemgetter("question")
            | retriever
            | format_docs_with_sources,
            "question": itemgetter("question"),
        }
        | RAG_PROMPT
        | _model()
        | StrOutputParser()
    )
