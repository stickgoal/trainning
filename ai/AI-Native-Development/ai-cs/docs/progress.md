# 实施进度记录（ai-cs）

> 实施 Agent 每完成一个里程碑在此追加：做了什么 / 验收结果 / 遗留问题。

## 总览（2026-08-19 收尾）

| 里程碑 | 状态 | 验收 |
|---|---|---|
| M0 环境与骨架 | ✅ | import 链路通 |
| M1 语料与模拟数据 | ✅ | 6 文档可读 + 测试 6/6 |
| M2 RAG 基础管线 | ✅ | 命中率 7/7=1.00 |
| M4 工具 + Agent | ✅ | S1~S5 全过（S4 一票否决 ✓） |
| M3 多路召回+Rerank | ✅ | 完整精排链路实测命中 1.00（08-20 补测） |
| M5 FastAPI + Web | ✅ | /api/chat 打通 |
| M6 评估基线 | ✅ | 三指标 1.00 + 门禁通过 |
| M7 MCP Server（可选） | ⏭ 未做 | 进程内工具已覆盖，独立进程形态留待后续 |

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

---

## M3 · 多路召回 + Rerank ✅ 完整验收（2026-08-19 部分 + 08-20 补测）

**做了什么**
- `src/rag/retriever.py` 补齐 `_hybrid_retriever`：BM25Retriever(k=20) + EnsembleRetriever(weights 0.5/0.5) + CrossEncoderReranker(top_n=4)；
- `RETRIEVER_MODE=basic|hybrid` 开关（config + 分派逻辑，M2 已预置）；
- 加降级保护：reranker 加载失败（异常可捕获时）自动降级为 Ensemble 无精排；
- `src/__init__.py` 钳制 OMP_NUM_THREADS/TOKENIZERS_PARALLELISM（规避 Windows torch 加载大模型崩溃的诱因之一）。

**验收结果（2026-08-20 补测，C 盘清理后环境允许）**
```bash
RETRIEVER_MODE=hybrid python -m pytest tests/test_rag_offline.py -s
# 完整精排链路（ContextualCompressionRetriever，非降级）确认生效 ✓
# 检索命中率: 7/7 = 1.00 ≥ 0.9 ✓
RETRIEVER_MODE=hybrid 全量测试：64 passed（含 rerank 加载）
# 评估基线（hybrid）: context_precision/faithfulness/tool_accuracy 均 1.000，防幻觉通过
```
- 默认 `RETRIEVER_MODE` 已切回 **hybrid**（完整精排为设计目标）；受限环境仍可 `RETRIEVER_MODE=basic` 回退。

**遗留问题**
- 无（原 C 盘空间限制已解除）。

---

## M5 · FastAPI 服务 + Web 页 ✅ 2026-08-19

**做了什么**
- `src/api/schemas.py`：ChatRequest（question 长度 1~500）/ ChatResponse（answer/sources/tool_calls/elapsed_ms）；
- `src/api/app.py`：POST /api/chat（agent 单例 lru_cache + 解析 messages 提取 sources/tool_calls）、POST /api/rebuild（串行加锁 + 失效缓存）、GET /health；503 当 Key 缺失；
- `web/index.html`：极简单页（输入框 + 对话气泡 + 来源展示，fetch 调 /api/chat）；
- 聊天日志落盘 logs/chat.log（question/tools/sources/耗时）。

**验收结果**
```bash
uvicorn src.api.app:app --port 8100 &
curl -s localhost:8100/health                       # {"status":"ok"} ✓
curl -X POST localhost:8100/api/chat -d '{"question":"年假满3年有几天？"}'
# → {"answer":"...年假 10 天/年...", "sources":["knowledge_docs/hr/leave_policy.txt",...],
#    "tool_calls":["search_policy"], "elapsed_ms":27415} ✓
# Web 页 GET / → HTTP 200 ✓
```

**遗留问题**
- 无。

---

## 测试完善 ✅ 2026-08-20

**做了什么**
- 新增 `tests/conftest.py`（统一 sys.path，任意 cwd 可跑）；
- 新增 6 个离线单元测试文件，覆盖之前未测的纯逻辑层：
  - `test_core.py`（8 项）：Config 路径/默认值/中文分隔符/**环境变量覆盖（subprocess 验证，避免 reload 污染 pytest 进程）**/日志落盘×2；
  - `test_rag_loader.py`（6 项）：文档计数/metadata 注入/domain 推断/切分边界/域覆盖/向量库缺失；
  - `test_rag_retriever.py`（9 项）：format_docs 来源格式/未知来源兜底/防幻觉 Prompt/retriever 分派（basic/hybrid/**rerank 降级**/非法模式）/向量库 roundtrip；
  - `test_tools.py`（9 项）：工具组装/返回类型/search_policy 内联来源/检索异常友好/员工/工单/非法输入/触发词 docstring；
  - `test_api.py`（9 项）：schemas 校验（空/超长）/**`_parse_result` 解析去重**/`/health`/无 Key 503/假 agent 成功路径；
  - `test_evaluate_utils.py`（8 项）：`_aggregate` 指标聚合/排除 out_of_scope/防幻觉判定/测试集口径一致性。

**验收结果**
```bash
python -m pytest tests/ -v
# 64 passed in 106.80s ✓（此前 14 项 → 64 项）
# 其中 test_scenarios 6 项为需 Key 的真实场景测试（无 Key 自动 skip）
```

**遗留问题**
- 无。hybrid rerank 的端到端验证仍受 C 盘空间限制（见 M3），降级路径已用假模型类覆盖。

---

## M6 · 测试集 + 评估 + 回归基线 ✅ 2026-08-19

**做了什么**
- 测试集 testdata.py 定稿（12 条，覆盖 6 域 + 2 知识库外 + 1 档位推理组合 stretch）；
- `scripts/evaluate.py`：keyless 迷你评估（课程 5.4.3），输出 context_precision / faithfulness / tool_accuracy / 防幻觉通过；支持 `--verbose` 明细与基线对比；阈值门禁（faithfulness ≥0.8、tool_accuracy ≥0.9、S4 一票否决）失败退出码 2；
- 首次基线落盘 `tests/baseline.json`（gitignore，不入库）。

**验收结果**
```bash
python scripts/evaluate.py --verbose
# context_precision : 1.000
# faithfulness      : 1.000   (阈值 ≥ 0.8) ✓
# tool_accuracy     : 1.000   (阈值 ≥ 0.9) ✓
# 防幻觉(S4/T11)    : 通过 ✓
# ✅ 全部门禁通过。
# 亮点：T12 stretch 组合题（E001 年假几天）正确链式调用
#       query_employee_db + search_policy，Agent 多步决策达标。
```

**遗留问题**
- 无。

---

## 功能分支 kb-management · 知识库管理 + 业务库直连 ✅ 2026-08-21

> 需求：新起分支，支持知识库管理（文件在线上传与解析）；`query_employee_db` /
> `query_ticket_status` 直接走数据库（替代 M1 的内存 `mock_data.py`）。

### 总览

| 阶段 | 提交 | 内容 | 验收 |
|---|---|---|---|
| A 业务库 | `1a5ce64` | 新增 `src/db/engine.py`（employees/tickets 两表 + 种子 + 懒初始化守卫），工具改读 SQLite | test_db 9/9；全量 100 passed |
| B 解析器 | `e5a0c5b` | `src/kb/parsers.py` 多格式解析（txt/md/pdf/docx/csv/xlsx） | test_parsers 10/10 |
| C 注册+重建 | `ae1750e` | `src/kb/db.py`（注册表）+ `src/kb/service.py`（入库/reindex，静态+上传合并） | test_kb 6/6 |
| D API+Web | `26aacd0` | `kb_routes.py`（上传/列表/删除）+ `/api/db/*` + 知识库管理 Web Tab | test_kb_api 11/11；全量 100 passed |
| E 清理 | `efdaeaf` | 删除 `mock_data.py`，测试改直连 `src.db.engine` | test_db/m1/tools 24 passed；全量 100 passed |
| F 修正 | `36bbcf7` | `build_vectorstore` 先清空集合再写入（修复重复累积） | 全量 100 passed |

### 关键设计

- **业务库直连**：`query_employee_db`/`query_ticket_status` 改调 `src.db.engine`；种子数据与旧 `mock_data.py` 口径一致（E001~E005 / RB2024001 / RB2024002 / IT20240011），保证 `test_scenarios` 的 T9/T10/T12 契约不变。`engine.query_*` 内置 `_ensure()` 懒初始化守卫，工具未显式启动也可安全查询。
- **上传即入库**：`kb.add_file` 解析字节 → 落盘 `data/uploads/` → 注册 `kb.db.documents` → 触发 `reindex()`，与静态 `knowledge_docs` 共用一个 Chroma 索引（合并切分）。
- **错误码**：不支持的扩展名 → `UnsupportedFormatError` → 400；解析损坏 → `ParseError` → 422。
- **业务库查询接口**：`GET /api/db/employees`、`GET /api/db/tickets` 复用 `DBEmployee`/`DBTicket` schema，管理视角直读 SQLite。
- **Web**：`web/index.html` 新增「知识库管理」Tab——文件选择/领域/上传、文档列表（类型·大小·分块·状态徽标）、删除、重建索引；原有「智能问答」Tab 保留。

### 验证

```bash
python -m pytest            # 100 passed（含 11 个 KB API 离线用例）
```

### 遗留问题

- `mock_data.py` 已删除（见 E），工具与测试均改直连 `src.db.engine`。
- 上传文档的 `reindex()` 每次全量重建（数据量小，可接受）；超大库可改为**增量 upsert**（见 F 备注）。
- 本功能分支在本地 git 重建后命名为扁平 `kb-management`（该环境 git 不支持含 `/` 的分支名）；另存 `refs/heads/feat/kb-management` 仅作语义标注。

---

## 功能分支 kb-management · 收尾 E/F ✅ 2026-08-22

### E · 删除 mock_data，测试改直连 engine（提交 `efdaeaf`）

- 删除 `src/tools/mock_data.py`（已被 `src.db.engine` 取代，工具层在 A 阶段已切到 engine）。
- `tests/test_m1_data.py`、`tests/test_tools.py` 的 `mock_data` 引用改到 `src.db.engine`；
  业务库口径不变（E001 张三/研发部/高级工程师、RB2024001 审批中等契约保留）。
- 验收：`tests/test_db.py + test_m1_data.py + test_tools.py` → 24 passed；全量 100 passed。

### F · 修复 reindex 重复累积（提交 `36bbcf7`）

- **问题**：`Chroma.from_documents` 对「已存在的集合」是**追加**而非替换，导致每次
  `/api/rebuild`、每次上传文档都累积重复 chunk（检索命中仍对，但索引膨胀、embedding 重复开销）。
- **修复**：`build_vectorstore` 改为先 `chromadb.PersistentClient.delete_collection("langchain")`
  再 `from_documents`，保证幂等、无重复；默认集合名锁定为 `"langchain"`，与 `load_vectorstore` 一致。
- 备注：当前 `reindex()` 仍是「全量重建」（数据量小可接受）。若要做到「增量 upsert」
  （仅重嵌变动的 KB 文档、静态语料常驻），需给 chunk 赋稳定 id 并在 add_file/delete_document
  中按 id upsert/delete，同时上传/删除后清掉 agent 缓存（避免缓存检索器读到旧索引）。属后续优化。

### 验收

```bash
python -m pytest            # 100 passed（3m45s，后台运行）
```

### 环境注意（重要）

- 本工作区出现**临时文件莫名消失**的异常：曾两次发现 `ai-cs/src/**` 整批从工作树被删
  （git 仍保留，可 `git restore ai-cs/src/` 找回）。pytest 产生的 `*.log` 也常被环境清理。
  对策：**改动及时 commit**，长测试务必后台运行并把日志写到 `ai-cs/` 之外（如工作区根目录），
  避免依赖临时日志文件。
