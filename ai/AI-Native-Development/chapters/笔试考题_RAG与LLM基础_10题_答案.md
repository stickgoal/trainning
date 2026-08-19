# AI Native 开发 · 笔试问答题 · 标准答案（10 题）

> 对应题面：`笔试考题_RAG与LLM基础_10题_题面.md`

---

**Q1. 文本分块策略与配置**
- 常用策略：`RecursiveCharacterTextSplitter`（递归字符，通用首选）、`TokenTextSplitter`（按 token 精确控预算）、`MarkdownHeaderTextSplitter`/`HTMLHeaderTextSplitter`（按结构）、`CodeTextSplitter`（按代码语法）、`SemanticChunker`（按语义，慢且费钱）、`ParentDocumentSplitter`（大块检索+小块嵌入）。
- `chunk_overlap`：相邻块重叠一部分，避免一句话被切断、保证语义连续。
- 配置：中文 `chunk_size` 300–800 字符，`overlap` 取 10%–20%；有明确结构时先按结构切再二次递归切。

**Q2. 多路召回 + Rerank**
- 两阶段：多路粗召回（向量 + BM25 + 多字段）融合去重 → Top 50~100（管"别漏"）；Rerank 精排（交叉编码器）→ Top 3~5（管"别乱"）。
- 必须多路：向量擅长语义改写，BM25 擅长精确 token/专有名词/编号，互补才全。
- RRF：`score = Σ 1/(k+rank)`（k 常取 60），只看排名、不看分数绝对值，对不同量纲鲁棒，故默认用它融合。
- 职责：**召回管"别漏"，重排管"别乱"**。

**Q3. 六步管线与角色**
- 六步：Document → Chunk → Embedding → Vector DB → Retriever → LLM。
- 角色：Embedding 负责"文本→向量"、算相似度（检索）；LLM 负责推理生成。两者不同模型，RAG 中都要用。
- 同模型：保证向量空间一致，否则检索全错。

**Q4. 防幻觉与增强**
- 加该约束是防幻觉：否则 LLM 会用训练知识瞎编，不基于检索上下文。
- 两个增强：① 生产加 Rerank（交叉编码器/ColBERT）提升检索准确率；② 加评估闭环（RAGAS/DeepEval 测 `context_precision` 与 `faithfulness`），上线定基线、每次改动做回归。

**Q5. 双塔 vs 交叉编码器**
- 双塔（Bi-encoder）= Embedding：query/doc 各自独立编码再算余弦，可预计算、快，适合海量召回，但相关性表达弱。
- 交叉编码器（Cross-encoder）= Rerank：query+doc 拼接一起算相关分，准但每对现算、慢，只适合少量精排。
- 不能只用交叉编码器：候选多时"query × 每条候选"联合编码计算量爆炸。

**Q6. LLM 应用本质 / 数据流 / 无状态**
- 区别：聊天机器人只问答（无工具/知识/记忆/多步）；LLM 应用 = 推理 + 工具 + 知识(RAG) + 记忆 + 多步决策(Agent)。
- 数据流：User → Application(构造 Prompt) → LLM(推理/决定调 Tool/Retrieval) → Tools/Retrieval(从 Vector DB 召回) → 回灌 LLM → Memory(存会话) → 回答。
- 无状态：LLM 每次调用独立、无持久记忆；Memory 靠应用层每次把完整历史重新发给 LLM 来"模拟记忆"。

**Q7. Tool Calling 四步闭环**
- 四步：① 把工具 schema + 问题发给 LLM；② LLM 返回要调的函数及参数；③ 应用执行函数；④ 把结果回灌 LLM 生成自然语言。
- 最易漏第 ④ 步回灌，后果：用户看到原始 JSON 而非自然语言。

**Q8. Token**
- Token 是 LLM 处理文本的最小单元（非字符非单词），是计费和上下文窗口的基本单位；窗口是硬上限，超了要截断/压缩/分块。
- 换算：英文约 1 词 ≈ 1.3 token；中文约 1 汉字 ≈ 2 token。

**Q9. MCP**
- N×M 适配地狱：N 个 Agent × M 个系统 = N×M 套适配代码，新增任一方都要大面积改。
- 三件套：Server 暴露工具（RPC Server）、Client 发现并调用（RPC Client）、Tool 一个可被调用的函数（RPC 方法）；基于 JSON-RPC 2.0，传输 stdio（本地）/ SSE·HTTP（远程）。
- 根本优势：MCP Tool 是独立服务、可用任意语言写、被多 Agent/产品复用；@tool 必须同进程同语言。

**Q10. Harness 与 Skill**
- Harness = Agent 稳定运行的工程基础设施（类比 Java 的 Spring+AOP+监控+日志+事务）。7 子能力：Context Management（超窗口压缩）、Memory（跨会话持久）、Tool Runtime（沙箱/超时/重试）、Permission（危险操作审批）、Evaluation（自动测质量）、Monitoring（决策可追溯）、Feedback（反馈回灌）。
- Skill 内部结构四部分：Prompt 模板 + Tool 集 + 使用说明 + 触发条件。粒度区别：Tool=函数级（一个方法），Prompt=文本级（一段话），Skill=能力级（带文档的能力包，类比"带 @Component 的 Spring Bean"）。
