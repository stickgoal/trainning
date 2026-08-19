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
