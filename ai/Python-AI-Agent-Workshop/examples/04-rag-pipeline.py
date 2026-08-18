# -*- coding: utf-8 -*-
"""
示例 04: RAG 管线完整实现
=========================
展示 RAG 全流程：文档加载 → 切分 → 向量化 → 存储 → 检索 → 问答

适合 Java 工程师阅读：每个 RAG 阶段均有详细注释
"""

from langchain_community.document_loaders import TextLoader, PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
from langchain_community.vectorstores import Chroma
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough, RunnableLambda
import os


# ============================================
# 阶段 1: Document Loading — 文档加载
# ============================================

print("=== 阶段 1: 文档加载 ===")

# 创建示例文档（实际应用中加载 PDF/Word/HTML 等文件）
sample_docs_dir = "./sample_docs"
os.makedirs(sample_docs_dir, exist_ok=True)

with open(f"{sample_docs_dir}/company_policy.txt", "w", encoding="utf-8") as f:
    f.write("""公司差旅报销政策

1. 交通费用
员工出差可报销火车票（高铁二等座）、飞机票（经济舱）。
市内交通按实际费用报销，每日上限100元。
自驾出差按每公里1.5元补贴油费。

2. 住宿费用
一线城市住宿标准：每晚不超过500元。
二线城市住宿标准：每晚不超过400元。
三线及以下城市：每晚不超过300元。

3. 餐饮补贴
出差期间餐饮补贴：每人每日100元，无需发票。
如已报销商务宴请费用，当日餐饮补贴取消。

4. 报销流程
出差结束后5个工作日内提交报销申请。
需附：出差审批单、交通票据、住宿发票。
审批流程：直属经理 → 财务部门 → 打款。
""")

# Document Loader 加载文档
# LangChain 提供多种 Loader：TextLoader, PyPDFLoader, WebBaseLoader 等
# Java 类比：类似 Apache Tika 的文档解析
loader = TextLoader(f"{sample_docs_dir}/company_policy.txt", encoding="utf-8")
documents = loader.load()

print(f"加载文档数量: {len(documents)}")
print(f"文档来源: {documents[0].metadata['source']}")
print(f"文档长度: {len(documents[0].page_content)} 字符")


# ============================================
# 阶段 2: Text Splitting — 文本切分
# ============================================

print("\n=== 阶段 2: 文本切分 ===")

# RecursiveCharacterTextSplitter：递归字符切分器
# 按 ["\n\n", "\n", " ", ""] 的优先级递归切分
# 尽量在段落边界切分，保持语义完整性
# chunk_overlap：块之间的重叠区域，避免在切分处丢失上下文
# Java 类比：类似分页处理，但更智能

text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=200,        # 每个块的最大字符数
    chunk_overlap=30,      # 块之间的重叠字符数（保持上下文连贯）
    separators=["\n\n", "\n", "。", "，", " ", ""],  # 中文友好的分隔符
)

# 切分文档
chunks = text_splitter.split_documents(documents)

print(f"切分后的块数量: {len(chunks)}")
for i, chunk in enumerate(chunks):
    print(f"  块 {i+1}: {len(chunk.page_content)} 字符, 内容: {chunk.page_content[:50]}...")


# ============================================
# 阶段 3: Embedding — 向量化
# ============================================

print("\n=== 阶段 3: 向量化 ===")

# OpenAIEmbeddings：将文本转换为向量
# 向量是文本的语义数学表示，维度通常为 1536（text-embedding-3-small）
# 语义相近的文本，向量距离也近
# Java 类比：将对象序列化为 byte[]，但这里序列化的是语义信息

embeddings = OpenAIEmbeddings(model="text-embedding-3-small")

# 对单个文本进行向量化（演示）
test_embedding = embeddings.embed_query("差旅报销标准是多少？")
print(f"向量维度: {len(test_embedding)}")
print(f"前5个值: {test_embedding[:5]}")


# ============================================
# 阶段 4: Vector Store — 向量存储
# ============================================

print("\n=== 阶段 4: 向量存储 ===")

# Chroma：轻量级向量数据库，适合开发原型
# 生产环境推荐 Milvus（分布式向量数据库）
# Java 类比：
#   Chroma → H2 Database（轻量、嵌入式）
#   Milvus → Elasticsearch Cluster（生产级）

# from_documents：自动完成 Embedding + 存储
# 将每个 chunk 向量化并存入 Chroma
vector_store = Chroma.from_documents(
    documents=chunks,
    embedding=embeddings,
    collection_name="company_policy",  # 集合名称，类似数据库表名
    persist_directory="./chroma_db",   # 持久化目录
)

print(f"向量数据库已存储 {len(chunks)} 个文档块")


# ============================================
# 阶段 5: Retrieval — 向量检索
# ============================================

print("\n=== 阶段 5: 向量检索 ===")

# Retriever：检索器，从向量数据库中查找最相关的文档块
# similarity_search：基于余弦相似度的语义搜索
# k=2：返回最相关的2个结果
# Java 类比：类似 Elasticsearch 的查询接口

retriever = vector_store.as_retriever(
    search_type="similarity",  # 检索类型：相似度搜索
    search_kwargs={"k": 2},    # 返回 Top-2 结果
)

# 测试检索
query = "住宿报销标准是多少？"
retrieved_docs = retriever.invoke(query)

print(f"查询: {query}")
print(f"检索到 {len(retrieved_docs)} 个相关文档块:")
for i, doc in enumerate(retrieved_docs):
    print(f"  结果 {i+1}: {doc.page_content[:80]}...")


# ============================================
# 阶段 6: RAG 问答 — 检索增强生成
# ============================================

print("\n=== 阶段 6: RAG 问答 ===")

# RAG Prompt 模板：将检索到的上下文和用户问题组合成 Prompt
rag_prompt = ChatPromptTemplate.from_messages([
    ("system", """你是一个企业知识库助手。请根据以下检索到的上下文回答问题。
    如果上下文中没有答案，请说明"根据现有资料无法回答"。
    回答时请注明信息来源。

    上下文信息：
    {context}
    """),
    ("human", "{question}"),
])

# LLM
llm = ChatOpenAI(model="gpt-4o", temperature=0)
parser = StrOutputParser()


# 格式化检索结果的函数
def format_docs(docs: list) -> str:
    """
    将检索到的文档块列表格式化为纯文本。
    
    Python 的列表推导式 (List Comprehension)：
    [x for x in items] 类似 Java Stream 的 map + collect
    " ".join(list) 类似 Java 的 String.join(" ", list)
    """
    return "\n\n".join(f"[文档{i+1}] {doc.page_content}" for i, doc in enumerate(docs))


# 构建 RAG Chain
# 流程：用户问题 → 检索相关文档 → 格式化上下文 → 构建 Prompt → LLM 生成 → 解析输出
# Java 类比：类似 Service 层编排多个组件完成业务逻辑
rag_chain = (
    # RunnablePassthrough.assign：在输入字典中添加 context 字段
    # retriever 根据问题检索文档，format_docs 格式化为文本
    {
        "context": retriever | RunnableLambda(format_docs),
        "question": RunnablePassthrough(),
    }
    | rag_prompt
    | llm
    | parser
)

# 执行 RAG 问答
questions = [
    "一线城市出差住宿标准是多少？",
    "餐饮补贴怎么算？需要发票吗？",
    "报销审批流程是什么？",
]

for q in questions:
    print(f"\n问题: {q}")
    answer = rag_chain.invoke(q)
    print(f"回答: {answer}")


# ============================================
# 阶段 7: 高级 RAG — Hybrid Search
# ============================================

print("\n=== 阶段 7: 高级 RAG（概念演示）===")

# Hybrid Search：关键词检索 + 向量检索 混合
# 向量检索擅长语义匹配（"住宿标准" → "酒店费用上限"）
# 关键词检索擅长精确匹配（"500元" → 包含 "500" 的文档）
# 融合两者结果，取长补短

# 概念代码（实际实现需要 BM25 Retriever）：
print("""
Hybrid Search 流程:
  用户提问
    ├── 向量检索 (语义匹配) → Top-K 结果
    ├── 关键词检索 (BM25)   → Top-K 结果
    └── 分数融合 (Reciprocal Rank Fusion)
        → 重排序 → 最终 Top-K 结果

Rerank 流程:
  检索结果 → Cross-Encoder 模型 → 重新打分排序 → 更精确的 Top-K
""")


# ============================================
# 阶段 8: 清理
# ============================================

# 清理临时文件
import shutil
shutil.rmtree(sample_docs_dir, ignore_errors=True)
shutil.rmtree("./chroma_db", ignore_errors=True)
print("\n=== 清理完成 ===")
