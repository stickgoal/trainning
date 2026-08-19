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
