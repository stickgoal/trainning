# 实施进度记录（ai-cs）

> 实施 Agent 每完成一个里程碑在此追加：做了什么 / 验收结果 / 遗留问题。

---

## M0 · 环境与项目骨架 ✅ 2026-08-19

**做了什么**
- 确认课程根 venv（`.venv/Scripts/python.exe`）可用，`langchain/chromadb/sentence_transformers/langchain_deepseek` 均可 import；
- 创建 `src/core/config.py`：路径（ROOT/KNOWLEDGE_DIR/CHROMA_DIR/LOG_DIR）、模型（LLM/EMBED/RERANK，环境变量可覆盖）、检索参数（BASIC_K/RECALL_K/RERANK_TOP_N）、切分参数（400/60 中文分隔符）、运行开关（TOOLS_MODE/RETRIEVER_MODE）；
- 创建 `src/core/logging.py`：`get_logger`（控制台 + logs/ai-cs.log 滚动）与 `get_chat_logger`（logs/chat.log 专用聊天日志）；
- requirements.txt、.gitignore、目录骨架（此前已就绪）。

**验收结果**
```bash
python -c "from src.core.config import Config; print(Config.KNOWLEDGE_DIR)"
# → D:\...\ai-cs\knowledge_docs ✓
python -c "import langchain, chromadb, sentence_transformers; print('ok')"
# → imports ok ✓
```
日志模块冒烟通过（控制台输出 + chat.log 落盘）。

**遗留问题**
- 无。M1 语料与测试集初稿此前已就绪，待补 `src/tools/mock_data.py`。

---

## M1 · 知识语料与模拟数据 ✅ 2026-08-19

**做了什么**
- 6 份语料此前已就绪（hr×2 / finance×2 / admin×2，UTF-8 + 【章节】分节，覆盖请假/差旅/报销/手册/IT/行政，含数字条款，无食堂/班车留白）；
- 创建 `src/tools/mock_data.py`：EMPLOYEES（5 人含 hire_date，与测试集口径对齐：E001 张三/研发部/高级工程师）、TICKETS（3 单覆盖不同状态，RB2024001=审批中）、`query_employee`/`query_ticket` 返回纯文本且对非法工号返回友好错误；
- 测试集 testdata.py 初稿 12 条此前已就绪（3 制度 + 2 工具 + 2 知识库外 + 档位推理 stretch）；
- 新增 `tests/test_m1_data.py`（6 项：语料可读性/分节结构/与测试集一致性/数据覆盖度/非法输入友好）。

**验收结果**
```bash
python -m pytest tests/test_m1_data.py -v   # 6 passed in 0.07s ✓
```
语料可被 utf-8 读出；gold_answer 与 mock_data 口径人工核对一致。

**遗留问题**
- 无。

---

## M2 · RAG 基础管线 ✅ 2026-08-19

**做了什么**
- `src/rag/loader.py`：load_documents/split_documents/load_and_split，遍历 knowledge_docs 注入 metadata（source/domain/doc/seq），中文分隔符切分 400/60；
- `src/rag/vectorstore.py`：build_vectorstore（Chroma 持久化到 chroma_db/）+ load_vectorstore（已存在时直连，共用同一 embedding 实例）；
- `src/rag/retriever.py`：按 RETRIEVER_MODE 分派，basic=纯向量 k=3；hybrid 分支预置（M3 填充）；config 默认 basic；
- `src/rag/chain.py`：LCEL 问答链（itemgetter 抽字符串防 dict 坑）+ format_docs_with_sources（内联来源行）+ 防幻觉 Prompt；
- `scripts/ingest.py`：一键建库，输出文档/chunk/各域统计；
- `tests/test_rag_offline.py`：离线检索测试（不调 LLM）。

**验收结果**
```bash
python scripts/ingest.py
# 文档数 6 / chunk 数 13；admin 5 / finance 4 / hr 4 ✓
python -m pytest tests/test_rag_offline.py -s
# 检索命中率 7/7 = 1.00 ≥ 0.8 ✓（2 passed）
# 冒烟（需 Key）：
#   问"入职5年有几天年假" → 10 天/年（来源 leave_policy.txt）✓
#   问"食堂几点开饭"     → 知识库中未找到相关内容 ✓（防幻觉）
```

**遗留问题**
- 无。P0 纯向量命中率已达 1.00；M3 hybrid 预期维持/提升（尤其单号/专有名词类 query）。

---

## M4 · 工具 + Agent 组装 ✅ 2026-08-19

**做了什么**
- `src/tools/langchain_tools.py`：三个 @tool（search_policy 包 retriever 返回内联来源行 / query_employee_db / query_ticket_status），docstring 精确描述触发条件，工具内 try/except 返回友好错误；
- `src/agents/customer_service.py`：create_agent（LangChain 1.x 入口）+ ChatDeepSeek(temperature=0.1) + SYSTEM_PROMPT 工具边界声明（架构文档 3.4）；
- `tests/test_scenarios.py`：S1~S5 场景测试，断言 answer 含 gold 关键词 + tool_calls 与 expect_tool 一致；S4 防幻觉单独一票否决用例；无 Key 自动 skip。

**验收结果**
```bash
python -m pytest tests/test_scenarios.py -s
# 6 passed in 44.76s ✓
# S1 出差住宿 → search_policy ✓
# S2 年假档位 → search_policy ✓（满5年=10天，档位推理正确）
# S3 查 E001 → query_employee_db ✓（张三/研发部/高级工程师）
# S4 食堂 → "知识库中未找到" ✓（防幻觉一票否决通过，无需调优）
# S5 查 RB2024001 → query_ticket_status ✓（审批中）
```

**遗留问题**
- 无。工具选择首次即全部正确（T4.4 调优未触发）。
