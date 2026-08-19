# AI Native 开发 · 笔试问答题（10 题）

> 每题 10 分，重点：RAG 与 LLM 基础（Q1–Q8），工程化能力（Q9–Q10）。

---

**Q1.** LangChain 中 RAG 文本分块有哪几种常用策略？为什么切分要设置 chunk_overlap？实践中 chunk_size 与 overlap 一般怎么配？

**Q2.** 基础 RAG 用单一向量检索常"召回不全"。工业界如何用"多路召回 + Rerank"解决"召回不全"和"召回不准"？RRF 是什么？一句话概括召回与重排的职责。

**Q3.** 写出 RAG 的六步管线。Embedding 模型和 LLM 在 RAG 中各自负责什么？为什么建库和查询必须用同一个 Embedding 模型？

**Q4.** 为什么 RAG 的 Prompt 要加"上下文没有就说未找到，不要编造"？生产 RAG 还应加哪两个增强来保证检索准、回答不编造？

**Q5.** 什么是双塔（Bi-encoder）和交叉编码器（Cross-encoder）？它们在 RAG 中分别负责什么环节？为什么不能只用交叉编码器做海量召回？

**Q6.** "聊天机器人"和"LLM 应用"本质区别是什么？结合 8 个基础术语说明 LLM 应用的数据流。LLM 为什么"无状态"，应用层如何做"记忆"？

**Q7.** Tool Calling 的四步闭环是什么？最容易漏哪一步，后果是什么？

**Q8.** Token 是什么，为什么是计费和上下文窗口的基本单位？中文与英文的 token 换算大致如何？

**Q9.** MCP 出现前的"N×M 适配地狱"指什么？MCP 三件套（Server/Client/Tool）各负责什么？相比 LangChain @tool，MCP Tool 的根本优势是什么？

**Q10.** Harness 是什么？列出其 7 个子能力。Skill 的内部结构包含哪四部分，与 Tool/Prompt 的粒度区别是什么？
