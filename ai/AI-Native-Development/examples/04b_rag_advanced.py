"""
============================================================
 04b_rag_advanced.py — RAG 进阶：多路召回 + Rerank 重排序
 对应章节：第5章 RAG 技术体系（5.4 多路召回与 Rerank）
============================================================

演示两阶段检索：
 阶段一 多路召回：向量检索（语义）+ BM25（字面）→ RRF 融合
 阶段二 重排序：交叉编码器精排到 Top-N

【运行流程】
 pip install langchain langchain-community chromadb rank-bm25
 pip install sentence-transformers   # 本地 Embedding + Rerank，无需厂商 API Key
 python examples/04b_rag_advanced.py

 （本示例的检索/融合/精排全程本地，无需 API Key；
   仅最后一步"生成回答"需要 DEEPSEEK_API_KEY。）

【Python 特殊语法提示】
 - EnsembleRetriever : 把多个检索器融合，类比 Java 的多个数据源聚合
 - ContextualCompressionRetriever : 检索后再压缩，类比"先查询后过滤"的装饰器
"""

import os

from langchain_community.retrievers import BM25Retriever
from langchain.retrievers import EnsembleRetriever, ContextualCompressionRetriever
from langchain.retrievers.document_compressors import CrossEncoderReranker
from langchain_community.cross_encoders import HuggingFaceCrossEncoder
from langchain_community.embeddings import SentenceTransformerEmbeddings
from langchain_community.vectorstores import Chroma
from langchain_core.documents import Document


# =================================================================
# 模拟文档（含一个专有名词文档，用来展示 BM25 对精确 token 的优势）
# =================================================================
RAW_TEXTS = [
    "【请假制度】年假：员工入职满 1 年享有 5 天年假，满 3 年 10 天，满 10 年 15 天。",
    "【请假制度】病假：每年有 10 天带薪病假，需提供三甲医院证明。",
    "【请假制度】婚假：法定 3 天，晚婚额外 7 天。产假：女员工 158 天，男员工陪产假 15 天。",
    "【报销制度】差旅费：交通费实报实销，住宿一线城市每晚不超过 500 元，二线不超过 350 元。",
    "【报销制度】餐费：差旅期间每天 80 元补贴，无需发票。",
    "【报销制度】审批：500 元内直属领导，500-3000 部门经理，3000 以上总监。",
    # 专有名词文档：OrderService 是纯 token，向量语义弱，BM25 字面匹配强
    "【技术文档】OrderService 订单服务部署：使用 K8s 集群，helm 部署，命名空间 order-prod，副本数 3。",
]


def build_retrievers():
    """构建 向量检索器 与 BM25 检索器。"""
    docs = [Document(page_content=t) for t in RAW_TEXTS]

    # —— 路 1：向量检索（语义）——
    embeddings = SentenceTransformerEmbeddings(model_name="BAAI/bge-small-zh-v1.5")
    vector_db = Chroma.from_documents(docs, embeddings)
    vector_retriever = vector_db.as_retriever(search_kwargs={"k": 5})

    # —— 路 2：BM25 关键词检索（字面）——
    bm25_retriever = BM25Retriever.from_documents(docs, k=5)

    return vector_retriever, bm25_retriever


def build_hybrid(vector_retriever, bm25_retriever):
    """融合两条路（RRF）。"""
    return EnsembleRetriever(
        retrievers=[vector_retriever, bm25_retriever],
        weights=[0.5, 0.5],   # 0.5/0.5 = RRF 融合
    )


def build_rerank_retriever(hybrid, top_n=3):
    """在融合检索器外面包一层 Rerank 精排。"""
    model = HuggingFaceCrossEncoder(model_name="BAAI/bge-reranker-base")
    compressor = CrossEncoderReranker(model=model, top_n=top_n)
    return ContextualCompressionRetriever(
        base_compressor=compressor,
        base_retriever=hybrid,
    )


def print_docs(title, docs):
    print(f"  ── {title} ──")
    for i, d in enumerate(docs, 1):
        print(f"     [{i}] {d.page_content[:45]}...")


def main() -> None:
    print("=" * 55)
    print("RAG 进阶：多路召回 + Rerank 重排序 演示")
    print("=" * 55)

    vector_retriever, bm25_retriever = build_retrievers()
    hybrid = build_hybrid(vector_retriever, bm25_retriever)
    rerank_retriever = build_rerank_retriever(hybrid, top_n=3)

    # 两个对比问题：一个语义型，一个精确 token 型
    for q in ["公司有几天带薪病假？", "OrderService 怎么部署？"]:
        print(f"\n❓ 问题: {q}")

        vec = vector_retriever.invoke(q)
        print_docs("纯向量召回 Top5", vec)

        bm25 = bm25_retriever.invoke(q)
        print_docs("纯 BM25 召回 Top5", bm25)

        fused = hybrid.invoke(q)
        print_docs("多路融合（RRF）Top5", fused)

        ranked = rerank_retriever.invoke(q)
        print_docs("Rerank 精排 Top3", ranked)

    # 可选：接 LLM 生成回答（需 API Key）
    if os.getenv("DEEPSEEK_API_KEY"):
        print("\n" + "=" * 55)
        print("（可选）接入 LLM 生成回答")
        print("=" * 55)
        from langchain_core.prompts import ChatPromptTemplate
        from langchain_core.output_parsers import StrOutputParser
        from langchain_deepseek import ChatDeepSeek

        model = ChatDeepSeek(model="deepseek-chat", api_key=os.getenv("DEEPSEEK_API_KEY"), temperature=0.1)
        prompt = ChatPromptTemplate.from_template(
            "根据以下上下文回答问题，上下文没有就说\"未找到\"。\n上下文：{context}\n问题：{question}"
        )
        chain = (
            {"context": rerank_retriever | (lambda d: "\n\n".join(x.page_content for x in d)),
             "question": lambda x: x["question"]}
            | prompt | model | StrOutputParser()
        )
        print("❓ OrderService 怎么部署？")
        print("✅", chain.invoke({"question": "OrderService 怎么部署？"}))
    else:
        print("\n⚠️  未设置 DEEPSEEK_API_KEY，跳过 LLM 生成（检索/融合/精排已演示完毕）。")


if __name__ == "__main__":
    main()
