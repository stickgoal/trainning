"""
============================================================
 05_llamaindex_demo.py — LlamaIndex 检索增强进阶
 对应章节：第6章 LlamaIndex 与检索增强进阶
============================================================

演示三个能力：
 1. 基础 RAG（VectorStoreIndex + QueryEngine）
 2. Rerank 重排序（自定义交叉编码器 NodePostprocessor）
 3. 多路召回（RouterQueryEngine 路由到不同索引）

【运行流程】
 pip install llama-index llama-index-embeddings-huggingface llama-index-llms-openai-like
 pip install sentence-transformers   # 本地 Embedding + Rerank，无需厂商 API Key
 python examples/05_llamaindex_demo.py

 （Embedding 与 Rerank 都用本地模型：bge-small-zh 做向量，bge-reranker 做精排。
   LLM 用 DeepSeek（OpenAI 兼容端点），需要 DEEPSEEK_API_KEY。）

【Python 特殊语法提示】
 - LlamaIndex 的 Document(text=...) 等价于 LangChain 的 Document(page_content=...)
 - Settings 是全局配置对象，类比 Spring 的全局 Bean，配置一次处处生效
 - NodePostprocessor 类比 Java 的拦截器/后置处理器：检索结果在进 LLM 前统一精排
"""

import os

from llama_index.core import Settings, Document, VectorStoreIndex
from llama_index.core.query_engine import RetrieverQueryEngine, RouterQueryEngine
from llama_index.core.retrievers import VectorIndexRetriever
from llama_index.core.postprocessor import NodePostprocessor
from llama_index.core.schema import QueryBundle
from llama_index.core.tools import QueryEngineTool, ToolMetadata
from llama_index.core.selectors import LLMSingleSelector
from llama_index.llms.openai_like import OpenAILike
from llama_index.embeddings.huggingface import HuggingFaceEmbedding
from sentence_transformers import CrossEncoder


# =================================================================
# 全局配置：LLM 用 DeepSeek，Embedding 用本地 bge 模型
# =================================================================
def setup_settings():
    """配置 LlamaIndex 全局 Settings（类比 Spring 全局 Bean）。"""
    llm = OpenAILike(
        model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        api_base="https://api.deepseek.com",   # DeepSeek 的 OpenAI 兼容端点
        is_chat_model=True,
        temperature=0.1,
    )
    embed_model = HuggingFaceEmbedding(model_name="BAAI/bge-small-zh-v1.5")
    # 全局生效：后续所有 index / query engine 默认用它俩
    Settings.llm = llm
    Settings.embed_model = embed_model
    Settings.chunk_size = 200       # 切分粒度（中文 200 字符左右）
    Settings.chunk_overlap = 30
    print("[配置] LLM=deepseek-chat | Embedding=BAAI/bge-small-zh-v1.5")


# =================================================================
# 模拟文档（真实场景用 SimpleDirectoryReader 读 PDF/Wiki/DB）
# =================================================================
LEAVE_TEXTS = [
    "【请假制度】年假：员工入职满 1 年享有 5 天年假，满 3 年 10 天，满 10 年 15 天。",
    "【请假制度】病假：每年有 10 天带薪病假，需提供三甲医院证明。",
    "【请假制度】婚假：法定 3 天，晚婚额外 7 天。产假：女员工 158 天，男员工陪产假 15 天。",
]
REIMBURSE_TEXTS = [
    "【报销制度】差旅费：交通费实报实销，住宿一线城市每晚不超过 500 元，二线不超过 350 元。",
    "【报销制度】餐费：差旅期间每天 80 元补贴，无需发票。",
    "【报销制度】审批：500 元内直属领导，500-3000 部门经理，3000 以上总监。",
]


# =================================================================
# 能力 2 需要：自定义 Rerank 后处理器（交叉编码器精排）
# =================================================================
class CrossEncoderRerank(NodePostprocessor):
    """
    用 sentence-transformers 的交叉编码器对召回结果精排。
    原理：把 (query, 文档) 拼成一对喂进模型，模型输出相关分，按分排序取 Top-N。
    LlamaIndex 也内置 SentenceTransformerRerank，这里手写一版便于理解原理。
    """

    def __init__(self, model_name: str = "BAAI/bge-reranker-base", top_n: int = 3):
        super().__init__()
        self._model = CrossEncoder(model_name)
        self._top_n = top_n

    def _postprocess_nodes(self, nodes, query_bundle=None):
        if query_bundle is None or not nodes:
            return nodes
        query = query_bundle.query_str
        # 1) 构造 (query, doc) 样本对
        pairs = [[query, n.get_content()] for n in nodes]
        # 2) 交叉编码器打分（越高越相关）
        scores = self._model.predict(pairs)
        for score, node in zip(scores, nodes):
            node.score = float(score)
        # 3) 按分降序，取 Top-N
        nodes = sorted(nodes, key=lambda n: n.score, reverse=True)
        return nodes[: self._top_n]


# =================================================================
# 能力 1：基础 RAG（一个索引 + 一个 QueryEngine）
# =================================================================
def demo_basic_rag():
    print("\n" + "=" * 55)
    print("能力 1：基础 RAG（VectorStoreIndex + QueryEngine）")
    print("=" * 55)
    docs = [Document(text=t) for t in LEAVE_TEXTS + REIMBURSE_TEXTS]
    index = VectorStoreIndex.from_documents(docs)
    engine = index.as_query_engine(similarity_top_k=3)
    resp = engine.query("入职 3 年能休几天年假？")
    print(f"❓ 入职 3 年能休几天年假？\n✅ {str(resp)}")


# =================================================================
# 能力 2：Rerank 重排序（粗召回 Top6 → 精排 Top3）
# =================================================================
def demo_rerank():
    print("\n" + "=" * 55)
    print("能力 2：Rerank 重排序（交叉编码器精排）")
    print("=" * 55)
    docs = [Document(text=t) for t in LEAVE_TEXTS + REIMBURSE_TEXTS]
    index = VectorStoreIndex.from_documents(docs)

    # 粗召回：先取 Top 6，给精排留空间
    retriever = VectorIndexRetriever(index=index, similarity_top_k=6)
    # 精排：交叉编码器把 6 条重排到 Top 3
    rerank = CrossEncoderRerank(model_name="BAAI/bge-reranker-base", top_n=3)

    engine = RetrieverQueryEngine(retriever=retriever, node_postprocessors=[rerank])
    resp = engine.query("出差住宿费每晚最多报多少？")
    print(f"❓ 出差住宿费每晚最多报多少？\n✅ {str(resp)}")
    print("（内部流程：粗召回 6 条 → Rerank 精排 → 取 Top 3 拼接上下文）")


# =================================================================
# 能力 3：多路召回（RouterQueryEngine 路由到不同索引）
# =================================================================
def demo_multi_path():
    print("\n" + "=" * 55)
    print("能力 3：多路召回（RouterQueryEngine 路由）")
    print("=" * 55)
    # 两条"路"：各自独立索引
    leave_index = VectorStoreIndex.from_documents([Document(text=t) for t in LEAVE_TEXTS])
    reimburse_index = VectorStoreIndex.from_documents([Document(text=t) for t in REIMBURSE_TEXTS])

    leave_tool = QueryEngineTool.from_defaults(
        query_engine=leave_index.as_query_engine(similarity_top_k=3),
        metadata=ToolMetadata(
            name="leave_policy",
            description="查询公司请假制度（年假、病假、婚假、产假、审批流程）",
        ),
    )
    reimburse_tool = QueryEngineTool.from_defaults(
        query_engine=reimburse_index.as_query_engine(similarity_top_k=3),
        metadata=ToolMetadata(
            name="reimburse_policy",
            description="查询公司报销制度（差旅、住宿、餐补、审批额度）",
        ),
    )

    # Router 用 LLM 判断问题属于哪条路，再路由到对应索引
    router = RouterQueryEngine(
        selector=LLMSingleSelector.from_defaults(),
        query_engine_tools=[leave_tool, reimburse_tool],
    )

    for q in ["请病假需要什么证明？", "出差住宿费每晚最多报多少？"]:
        resp = router.query(q)
        print(f"❓ {q}\n✅ {str(resp)}")


def main() -> None:
    if not os.getenv("DEEPSEEK_API_KEY"):
        print("⚠️  需要 DEEPSEEK_API_KEY 来调用 LLM。")
        print("   配置 export DEEPSEEK_API_KEY=sk-... 后重跑。")
        return

    print("=" * 55)
    print("LlamaIndex 检索增强进阶 演示")
    print("=" * 55)
    setup_settings()
    demo_basic_rag()
    demo_rerank()
    demo_multi_path()


if __name__ == "__main__":
    main()
