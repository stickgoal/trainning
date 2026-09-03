"""第四章（5.4）：LlamaIndex —— 让回答「有出处」：RAG 完整流程。

案例背景：技术文档助手现在要回答公司内部问题，比如
"我们的限流是怎么配置的？"

模型不知道答案——这些信息在公司文档里，不在训练数据里。
三个办法：

  ① 把全部文档塞进 Prompt  → 上下文窗口装不下，且按 token 计费
  ② 微调模型                 → 成本高、知识更新慢、无法溯源
  ③ **RAG：先检索相关片段，再让模型基于片段回答**  ← 本文件讲这个

RAG 的五步流水线：
    文档 → ① 加载 → ② 分块 → ③ 向量化 → ④ 存入索引 → ⑤ 检索 + 生成

本文件回答六个问题：
1. 为什么要分块？块的大小怎么定？
2. Embedding 到底做了什么？（含一个能跑的朴素实现）
3. "相似"是怎么算出来的？
4. 怎么看到检索到了什么、相似度多少？（可观测性是调优的前提）
5. 检索和生成为什么要能分开执行？
6. RAG 常见的失败模式有哪些？

运行：
    python ai_demo/04_llamaindex_rag.py

零依赖、零下载、零 API Key：本文件自带一个可解释的哈希 Embedding。
"""
from __future__ import annotations

import math
import re
from collections.abc import Sequence

from llama_index.core import Settings, VectorStoreIndex
from llama_index.core.base.embeddings.base import BaseEmbedding
from llama_index.core.embeddings.mock_embed_model import MockEmbedding  # noqa: F401  (对比用)
from llama_index.core.llms import MockLLM
from llama_index.core.node_parser import SentenceSplitter  # noqa: F401  (仅作对比说明，本文件未使用)
from llama_index.core.schema import NodeWithScore, TextNode

# ===========================================================================
# 0. 语料：模拟公司技术文档库
# ===========================================================================
CORPUS: list[tuple[str, str]] = [
    (
        "限流配置说明",
        "限流通过 RATE_LIMIT_MAX_REQUESTS 和 RATE_LIMIT_WINDOW_SECONDS 两个配置项控制。"
        "默认值分别是 100 和 60，表示每个客户端 60 秒内最多 100 次请求。"
        "限流逻辑实现在 api/middleware.py 的 RateLimitMiddleware 中。"
        "超出阈值时返回 HTTP 429，响应头携带 Retry-After。",
    ),
    (
        "部署规范",
        "生产环境使用 Gunicorn 管理多个 uvicorn worker 进程。"
        "worker 数量建议设置为 CPU 核数，命令为 gunicorn -w 4 -k uvicorn.workers.UvicornWorker。"
        "容器镜像不要以 root 用户运行，需创建非特权用户。",
    ),
    (
        "错误处理约定",
        "所有业务异常继承 AppException，携带 code 与 message 字段。"
        "异常处理器统一把业务异常映射为 HTTP 状态码：NotFoundException 映射 404，"
        "ConflictException 映射 409，ValidationException 映射 422。未捕获异常统一返回 500。",
    ),
    (
        "数据库访问规范",
        "数据访问通过 Repository 层隔离，业务层只依赖 TaskRepository 接口。"
        "当前提供 InMemoryTaskRepository 实现，生产环境可替换为 SQLAlchemy 实现，"
        "Service 层代码无需改动。",
    ),
]


# ===========================================================================
# 1. 一个能讲清楚原理的 Embedding 实现
# ===========================================================================
class HashingEmbedding(BaseEmbedding):
    """教学用 Embedding：基于字符 n-gram 的哈希投影（hashing trick）。

    为什么不用现成的语义模型（bge / text-embedding-3）？
      · 那些要下载几百 MB 模型文件，离线跑不了；
      · 它们是黑盒，你无法解释"为什么这两段相似"。

    为什么不用 llama_index 自带的 MockEmbedding？
      · 它对所有文本都返回同一个常量向量 [0.5, 0.5, ...]，
        余弦相似度恒为 1.0，检索结果没有区分度——用它演示 RAG 会得到完全误导的结论。
      · 本实现保证：**字面重合越多，向量越接近**，能真实体现检索行为。

    原理（真实语义模型的核心思想也是这个，只是把"字面重合"换成了"语义重合"）：
      1. 把文本切成 n-gram（这里用 2-gram，即相邻两字）
      2. 每个 n-gram 哈希到向量的某个维度，该维度 +1
      3. L2 归一化，得到单位向量
      4. 两段文本的相似度 = 向量点积（余弦相似度）
    """

    embed_dim: int = 128
    ngram: int = 2

    @classmethod
    def class_name(cls) -> str:
        return "HashingEmbedding"

    def _embed(self, text: str) -> list[float]:
        # 去空白与标点，只留中英文与数字
        cleaned = re.sub(r"[^\w\u4e00-\u9fff]+", "", text.lower())
        if not cleaned:
            return [0.0] * self.embed_dim

        vec = [0.0] * self.embed_dim
        grams = (
            [cleaned[i : i + self.ngram] for i in range(len(cleaned) - self.ngram + 1)]
            if len(cleaned) >= self.ngram
            else [cleaned]
        )
        for g in grams:
            vec[hash(g) % self.embed_dim] += 1.0

        # L2 归一化：让余弦相似度 == 点积，且消除文本长度的影响
        norm = math.sqrt(sum(v * v for v in vec)) or 1.0
        return [v / norm for v in vec]

    def _get_text_embedding(self, text: str) -> list[float]:
        return self._embed(text)

    def _get_query_embedding(self, query: str) -> list[float]:
        return self._embed(query)

    async def _aget_query_embedding(self, query: str) -> list[float]:
        return self._embed(query)


def cosine(a: Sequence[float], b: Sequence[float]) -> float:
    return sum(x * y for x, y in zip(a, b))


# ===========================================================================
# 2. 演示：Embedding 与相似度
# ===========================================================================
def demo_1_embedding() -> None:
    print("\n" + "=" * 68)
    print("1. Embedding：把文本变成可比较的向量")
    print("=" * 68)

    emb = HashingEmbedding()
    v1 = emb.get_text_embedding("限流配置")
    v2 = emb.get_text_embedding("限流是怎么配置的")
    v3 = emb.get_text_embedding("生产部署规范")

    print(f"向量维度：{len(v1)}（真实模型通常 768 或 1536 维）")
    print(f"非零元素个数：{sum(1 for x in v1 if x)}  ← 稀疏：大部分维度是 0")
    print("\n余弦相似度：")
    print(f"  「限流配置」      vs「限流是怎么配置的」 = {cosine(v1, v2):.4f}  ← 语义相近，分数高")
    print(f"  「限流配置」      vs「生产部署规范」      = {cosine(v1, v3):.4f}  ← 无关，分数低")
    print(f"  「限流配置」      vs「限流配置」          = {cosine(v1, v1):.4f}  ← 与自身必为 1.0")

    print("""
要点：
  · Embedding 的本质：把"不可比较的文本"映射成"可计算的向量"，
    让"相似"这件事变成一个**数值**。
  · 有了数值，才能排序、设阈值、做混合检索——这是整个 RAG 的地基。
  · 打分是**相对的**：0.32 本身没有意义，有意义的是"它比其他候选高"。
    所以不要写死阈值，要看 Top-K 的相对差距。
  · 真实模型（bge-m3、text-embedding-3）能识别"限流"和"速率限制"同义，
    本实现只能识别字面重合——这是朴素实现与语义模型的差距所在。""")


# ===========================================================================
# 3. 分块：为什么必须切，切多大
# ===========================================================================
def split_text(text: str, chunk_size: int = 120, overlap: int = 20) -> list[str]:
    """分块：先按句末标点切句，再贪心合并到指定长度，块间保留重叠。

    为什么要自己写？
      · LlamaIndex 的 SentenceSplitter 依赖 NLTK 语料（punkt_tab / stopwords），
        离线环境下一跑就报 LookupError，还得联网下载几十 MB。
      · 分块的逻辑本身就这几行，写出来你才真正理解 chunk_size 和 overlap 在干什么。

    LlamaIndex 内置分块器（需要时能换）：
      · SentenceSplitter        按句子切（需 NLTK）
      · TokenTextSplitter       按 token 切（需 tiktoken）
      · SemanticSplitterNodeParser  按语义相似度切（需 Embedding，效果最好、最慢）
    """
    sentences = re.split(r"(?<=[。！？；])", text)
    sentences = [s for s in sentences if s.strip()]

    chunks: list[str] = []
    current = ""
    for sent in sentences:
        if len(current) + len(sent) <= chunk_size:
            current += sent
        else:
            if current:
                chunks.append(current)
            # 保留上一块末尾的 overlap 个字，避免把完整语义切断
            current = (current[-overlap:] if overlap else "") + sent
    if current:
        chunks.append(current)
    return chunks


def demo_2_chunking() -> None:
    print("\n" + "=" * 68)
    print("2. 分块：把长文档切成可检索的单元")
    print("=" * 68)

    full_text = CORPUS[0][1]
    print(f"原文长度：{len(full_text)} 字")

    for size in (40, 120, 512):
        chunks = split_text(full_text, chunk_size=size, overlap=0)
        print(f"\n  chunk_size={size:3} → 切成 {len(chunks)} 块：")
        for i, c in enumerate(chunks[:3], 1):
            print(f"      [{i}]（{len(c):3}字）{c[:36]}...")

    print("\n  overlap 的作用（chunk_size=60）：")
    for ov in (0, 20):
        chunks = split_text(full_text, chunk_size=60, overlap=ov)
        print(f"    overlap={ov:2} → {len(chunks)} 块；第1块结尾：...{chunks[0][-15:]}")
        print(f"                        第2块开头：{chunks[1][:15]}...")

    print("""
要点：
  · 为什么必须分块？
      ① 上下文窗口有限，塞不下整篇文档；
      ② 即使塞得下，长文本中间部分会被模型"忽略"（lost in the middle）；
      ③ 按块计费，只送相关块能显著省钱。
  · 块太小 → 语义不完整（"它默认 100" 这种块，脱离上下文就看不懂）。
  · 块太大 → 噪声多，相似度被稀释，检索不准，还费钱。
  · 经验起点：中文 300-500 字，配 10%-20% 的重叠（overlap）避免把句子切断。
    **没有万能值，必须用自己的数据测**。""")


# ===========================================================================
# 4. 建索引 + 检索：看到"检索到了什么"
# ===========================================================================
def build_index(chunk_size: int = 120, overlap: int = 20) -> VectorStoreIndex:
    """构建向量索引：文档 → 分块 → TextNode → 索引。"""
    nodes: list[TextNode] = []
    for title, text in CORPUS:
        for chunk in split_text(text, chunk_size=chunk_size, overlap=overlap):
            nodes.append(TextNode(text=chunk, metadata={"title": title}))
    # from_nodes 会对每个节点调用 embed_model 生成向量并存入向量库
    return VectorStoreIndex.from_documents([]) if not nodes else VectorStoreIndex(nodes)


def demo_3_retrieve() -> None:
    print("\n" + "=" * 68)
    print("3. 检索：把「检索到什么、分数多少」看清楚")
    print("=" * 68)

    Settings.embed_model = HashingEmbedding()
    index = build_index()

    print(f"索引中的节点数：{len(index.docstore.docs)}")
    print("（4 篇文档被切成更多个块——每块是独立的检索单元）\n")

    retriever = index.as_retriever(similarity_top_k=3)

    for query in ["限流是怎么配置的？", "生产环境怎么部署？"]:
        print(f"▶ 查询：{query}")
        nodes: list[NodeWithScore] = retriever.retrieve(query)
        for i, n in enumerate(nodes, 1):
            title = n.node.metadata.get("title", "?")
            score = n.score if n.score is not None else 0.0
            bar = "█" * int(score * 30)
            print(f"   {i}. [score={score:.4f}] {bar}")
            print(f"      来源：{title}")
            print(f"      内容：{n.node.text[:60]}...")
        print()

    print("""
要点（这是 RAG 调优的起点）：
  · **一定要把检索结果打印出来看**。RAG 答错的绝大多数情况，
    不是"模型不行"，而是"根本没检索到相关块"。
  · 排在第 1 的块如果不包含答案，后面调 Prompt 都是白费力气。
  · 分数要看**分布**：0.9 / 0.2 / 0.1 说明检索很确定；
    0.35 / 0.33 / 0.30 说明检索基本是随机猜，这时该换 Embedding 或改分块。
  · 生产建议：把 Top-K 和分数一起记进日志，才能事后定位问题。""")


# ===========================================================================
# 5. 检索 + 生成：完整 RAG
# ===========================================================================
def demo_4_full_rag() -> None:
    print("\n" + "=" * 68)
    print("4. 完整 RAG：检索 + 生成，以及为什么要能拆开")
    print("=" * 68)

    Settings.embed_model = HashingEmbedding()
    Settings.llm = MockLLM()  # 离线：返回固定文本，不联网
    index = build_index()

    query = "限流是怎么配置的？"

    # ① 只检索，不生成
    retriever = index.as_retriever(similarity_top_k=2)
    nodes = retriever.retrieve(query)
    print("【步骤 A】只检索（不调模型，毫秒级、零成本）：")
    context = "\n\n".join(n.node.text for n in nodes)
    for i, n in enumerate(nodes, 1):
        print(f"   {i}. [{(n.score or 0):.4f}] {n.node.text[:50]}...")

    # ② 只生成（基于给定上下文）
    from llama_index.core import get_response_synthesizer

    synthesizer = get_response_synthesizer(response_mode="compact")
    print("\n【步骤 B】只生成（把检索到的上下文 + 问题 交给模型）：")
    print(f"   上下文长度：{len(context)} 字")
    response = synthesizer.synthesize(query, nodes)
    print("   模型回答（离线 MockLLM 的返回是固定的）：")
    print("   ", str(response)[:100])

    # ③ 一步到位
    print("\n【步骤 C】query_engine 一步到位（内部就是 A + B）：")
    qe = index.as_query_engine(similarity_top_k=2)
    r = qe.query(query)
    print("   ", str(r)[:100])

    print("""
为什么要能拆开：
  · 检索可以缓存、可以用传统检索（BM25）兜底、可以加规则过滤；
  · 生成可以换模型、加引用标注、加拒答逻辑；
  · 出问题时能定位：是"没检索到"（查步骤 A）还是"检索到了但答错"（查步骤 B）。
  · 生产里我几乎总是拆开写，而不是直接用 query_engine 一把梭。

关键设计：让模型只基于给定上下文回答，并在上下文缺失时明确说"资料中没有"。
这一步 Prompt 决定了 RAG 是"减少幻觉"还是"编得更像真的"。""")


# ===========================================================================
# 6. 失败模式
# ===========================================================================
def demo_5_failure_modes() -> None:
    print("\n" + "=" * 68)
    print("5. RAG 的失败模式（比成功路径更值得记住）")
    print("=" * 68)
    print("""
① 检索不到 —— 最常见
   症状：答案明显在文档里，模型却说"资料中没有"。
   排查：打印 Top-K，看答案所在块排第几、分数多少。
   常见原因：分块把答案切断了；Embedding 不认识领域术语（缩写、行话）；
             查询用的是口语，文档用的是书面语。
   改进：调整 chunk_size/overlap；换领域适配的 Embedding；
         查询改写（让模型把问题改写成文档用语）；混合检索（向量 + BM25）。

② 检索到了但答错
   症状：Top-1 块里明明有正确答案，模型答成了别的。
   原因：上下文里有多个相似但矛盾的段落（比如新旧两版配置说明）。
   改进：给文档加版本号/更新时间元数据，检索时过滤；
         Prompt 里要求"冲突时以更新时间较新的为准"。

③ 检索到但没引用 —— 溯源失效
   症状：答案正确，但说不出来自哪份文档。
   原因：用 query_engine 一把梭，丢掉了 source_nodes。
   改进：拆开执行，从 NodeWithScore 里取 metadata 做引用标注。
   这是企业场景的**硬需求**：答案必须有出处，否则没人敢信。

④ 上下文塞满，关键信息被忽略
   症状：Top-K 设成 20，答案在第 15 块，模型还是答错。
   原因：lost in the middle —— 模型对长上下文首尾敏感、中间迟钝。
   改进：K 别贪多（3-5 足够）；检索后做重排（reranker），把最相关的放首尾。

⑤ 把 RAG 当数据库用
   症状：问"一共有多少个任务"，RAG 答非所问。
   原因：RAG 擅长"找相关片段"，不擅长"统计、聚合、精确匹配"。
   改进：需要精确查询的场景走工具调用（见 02 的 @tool），
         让模型自己决定是检索文档还是查数据库。

📌 一句话总结：RAG 的效果上限由**检索质量**决定，
   不要一上来就换更大的模型——先把 Top-K 打印出来看清楚。""")


def main() -> None:
    print("=" * 68)
    print("LlamaIndex / RAG：让模型的回答有出处")
    print("=" * 68)
    demo_1_embedding()
    demo_2_chunking()
    demo_3_retrieve()
    demo_4_full_rag()
    demo_5_failure_modes()
    print("\n" + "=" * 68)
    print("小结")
    print("  RAG 五步：加载 → 分块 → 向量化 → 建索引 → 检索+生成")
    print("  ✅ 适合：私域知识问答、需要溯源、知识频繁更新")
    print("  ❌ 不适合：统计聚合、精确匹配（那些用工具调用）")
    print("  📌 调优第一动作：把检索到的 Top-K 和分数打印出来")
    print("=" * 68)


if __name__ == "__main__":
    main()
