"""
============================================================
 04_rag_demo.py — RAG 检索增强生成完整流程
 对应章节：第5章 RAG 技术体系
============================================================

演示 6 步管线：
 Document → Chunk → Embedding → VectorDB → Retriever → LLM

【运行流程】
 pip install langchain langchain-community chromadb
 pip install sentence-transformers   # 本地 Embedding，无需 API Key
 python examples/04_rag_demo.py

 （Embedding 统一用本地 sentence-transformers 模型，无需任何厂商 API Key。
   DeepSeek 等国产 LLM 不提供 Embedding 接口，故 RAG 用本地向量模型。）

【应用场景】
 企业知识库、文档问答、合规检索。

【Python 特殊语法提示】
 - with ... as ...   : Context Manager，自动管理资源（Java try-with-resources）
 - 列表推导式 [x for x in ...] : 紧凑写法，类比 Java Stream.collect(toList())
 - Chroma.from_documents(...) : 类工厂方法
"""

import os

# =================================================================
# 国内网络环境适配（HuggingFace 官方域名常被墙）
# - 若本机已缓存该 Embedding 模型 → 强制离线模式，不再联网探测，避免卡死超时
# - 若未缓存（首次运行）→ 走国内镜像 hf-mirror.com 下载权重
# 说明：SentenceTransformerEmbeddings 在加载时会联网探测 adapter_config.json，
#       即使模型已缓存、国内无法访问 huggingface.co 也会一直重试直到超时。
# =================================================================
_HF_CACHE = os.path.join(
    os.getenv("HF_HOME", os.path.expanduser("~/.cache/huggingface")), "hub"
)
if os.path.isdir(os.path.join(_HF_CACHE, "models--BAAI--bge-small-zh-v1.5")):
    os.environ["HF_HUB_OFFLINE"] = "1"          # 已有缓存：纯离线，零网络
else:
    os.environ.setdefault("HF_ENDPOINT", "https://hf-mirror.com")  # 首次：国内镜像下载

from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.embeddings import SentenceTransformerEmbeddings
from langchain_community.vectorstores import Chroma
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_deepseek import ChatDeepSeek
from operator import itemgetter


# =================================================================
# 准备一份模拟文档（真实场景从 PDF / Wiki / DB 读）
# =================================================================
SAMPLE_DOC_PATH = "sample_knowledge.txt"


def prepare_sample_doc() -> None:
    """造一份假的企业知识文档，供 RAG 检索。"""
    content = """\
【公司请假制度 v3.2】
1. 年假：员工入职满 1 年享有 5 天年假，满 3 年 10 天，满 10 年 15 天。
2. 病假：每年有 10 天带薪病假，需提供三甲医院证明。
3. 事假：按无薪处理，每月不超过 3 天，全年不超过 15 天。
4. 婚假：法定 3 天，晚婚额外 7 天。
5. 产假：女员工 158 天，男员工陪产假 15 天。
6. 审批流程：3 天内直属领导审批，3 天以上需部门经理 + HR 双审。

【报销制度 v2.1】
1. 差旅费：交通费实报实销，住宿一线城市 ≤ 500/晚，二线 ≤ 350/晚。
2. 餐费：差旅期间 80 元/天补贴，无需发票。
3. 报销时限：费用发生 30 天内提交，逾期不予报销。
4. 审批：500 元内直属领导，500-3000 部门经理，3000 以上总监。
"""
    with open(SAMPLE_DOC_PATH, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"[准备] 已生成模拟文档: {SAMPLE_DOC_PATH}")


# =================================================================
# RAG 完整流程
# =================================================================
def build_rag_pipeline():
    """
    构建一次 RAG 流程，返回可问答的 chain。
    6 步：Document → Chunk → Embedding → VectorDB → Retriever → LLM
    """
    # —— 步骤1：Document 加载 ——
    # TextLoader 读文本，真实场景用 PyPDFLoader / WebBaseLoader / NotionLoader
    loader = TextLoader(SAMPLE_DOC_PATH, encoding="utf-8")
    docs = loader.load()
    print(f"[步骤1 Document] 加载 {len(docs)} 个文档")

    # —— 步骤2：Chunk 切分 ——
    # RecursiveCharacterTextSplitter：按 ["\n\n", "\n", " ", ""] 层级切，
    # 优先在段落边界断开，避免把一句话切成两半
    splitter = RecursiveCharacterTextSplitter(
        chunk_size=200,      # 每块约 200 字符
        chunk_overlap=30,    # 相邻块重叠 30 字符，保证语义连续
    )
    chunks = splitter.split_documents(docs)
    print(f"[步骤2 Chunk] 切成 {len(chunks)} 块")

    # —— 步骤3+4：Embedding + 入 VectorDB ——
    # SentenceTransformerEmbeddings：本地模型，首次运行会下载权重（中文推荐 bge 系列）
    # DeepSeek 等国产 LLM 不提供 Embedding 接口，故用本地向量模型，无需厂商 Key
    embeddings = SentenceTransformerEmbeddings(model_name="BAAI/bge-small-zh-v1.5")
    vector_db = Chroma.from_documents(chunks, embeddings)
    print(f"[步骤3+4 Embedding+VectorDB] 入库完成")

    # —— 步骤5：Retriever 检索 ——
    # as_retriever：把 VectorStore 包装成可调用的检索器
    retriever = vector_db.as_retriever(search_kwargs={"k": 3})  # 取 Top3
    print(f"[步骤5 Retriever] 就绪，Top-K=3")

    # —— 步骤6：组装 RAG Chain ——
    model = ChatDeepSeek(
        model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        temperature=0.1,  # 低温度，问答要稳定
    )

    prompt = ChatPromptTemplate.from_template("""
你是企业知识库助手，根据以下检索到的上下文回答问题。
如果上下文里没有答案，就说 "知识库中未找到"，不要编造。

【上下文】
{context}

【问题】
{question}
""".strip())

    # LCEL 链：
    # 1) 用 itemgetter 从输入字典里抽出 question 字符串，交给 retriever 检索
    #    （关键：retriever 必须接收「字符串」，否则会拿到整个 dict 导致 embed 报错）
    # 2) 把检索到的片段拼成 context 字符串
    # 3) 填进 prompt
    # 4) 发给 LLM
    # 5) 解析成纯文本
    chain = (
        {"context": itemgetter("question")
                   | retriever
                   | (lambda docs: "\n\n".join(d.page_content for d in docs)),
         "question": itemgetter("question")}
        | prompt
        | model
        | StrOutputParser()
    )
    print(f"[步骤6 LLM Chain] 组装完成\n")
    return chain, retriever


def main() -> None:
    if not os.getenv("DEEPSEEK_API_KEY"):
        print("⚠️  需要 DEEPSEEK_API_KEY 来调用 LLM。")
        print("   或者把 model 换成本地模型，去掉这行检查。")
        return

    print("=" * 55)
    print("RAG 检索增强生成 演示")
    print("=" * 55)

    prepare_sample_doc()
    chain, retriever = build_rag_pipeline()

    questions = [
        "我入职 5 年了，能休几天年假？",
        "我去北京出差，住宿费每晚最多能报多少？",
        "请病假需要什么证明？",
        "公司食堂几点开饭？",  # 知识库里没有，应该回答"未找到"
    ]

    for q in questions:
        print(f"\n❓ 问题: {q}")
        # 先看检索到了哪些片段
        retrieved = retriever.invoke(q)
        print(f"   检索到 {len(retrieved)} 个片段:")
        for i, d in enumerate(retrieved, 1):
            print(f"     [{i}] {d.page_content[:50]}...")
        # 再让 LLM 基于检索结果回答
        answer = chain.invoke({"question": q})
        print(f"   ✅ 回答: {answer}")


if __name__ == "__main__":
    main()
