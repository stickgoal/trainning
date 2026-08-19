# ai-cs · 企业智能客服系统（RAG）

> 基于企业内部规章制度的智能客服：问制度走 RAG 检索，问数据走工具查询，一个 Agent 两种能力。
> 技术栈（课程第 13 章综合案例 1）：**Python + LangChain + RAG + Chroma + MCP**

---

## 项目状态

**当前阶段：规划完成，待实施。**

后续实施 Agent 请按顺序阅读：

| # | 文档 | 内容 |
|---|---|---|
| 1 | [docs/01_系统分析.md](docs/01_系统分析.md) | 业务背景、场景（S1~S6）、功能清单（FR/NFR）、技术选型依据 |
| 2 | [docs/02_架构设计.md](docs/02_架构设计.md) | 架构图、目录蓝图、模块接口契约、数据流 |
| 3 | [docs/03_开发计划.md](docs/03_开发计划.md) | 里程碑 M0~M7、任务分解、验收标准、执行纪律 |
| 4 | [docs/04_实施注意事项.md](docs/04_实施注意事项.md) | **动手前必读**：API 陷阱、环境坑、报错速查、红线 |

实施进度记录：`docs/progress.md`（实施过程中自建）。

---

## 一句话架构

```
员工提问
   ↓
FastAPI /api/chat
   ↓
LangChain Agent（create_agent，LLM 自主选工具）
   ├─ 制度问题 → search_policy
   │     → 多路召回（向量 + BM25）→ Rerank 精排 → Top4 片段
   │     → Chroma（企业制度文档 knowledge_docs/）
   ├─ 查员工 → query_employee_db（模拟 HR DB）
   └─ 查工单 → query_ticket_status（模拟工单 DB）
   ↓
基于工具返回内容生成回答（带来源引用，知识库没有则明确"未找到"）
```

## 目录结构

```
ai-cs/
├── docs/              # 规划文档（4 份，实施依据）
├── knowledge_docs/    # 企业制度语料（hr/finance/admin 三域）
├── src/
│   ├── core/          # 配置、日志
│   ├── rag/           # 加载/切分/向量库/检索/问答链
│   ├── tools/         # 模拟 DB + @tool + (P1) MCP Server
│   ├── agents/        # create_agent 组装
│   └── api/           # FastAPI
├── scripts/           # ingest.py 建库 / evaluate.py 评估
├── tests/             # 测试集 + 离线检索测试 + 场景测试
└── requirements.txt
```

## 快速开始（实施完成后）

```bash
# 0. 环境：复用课程根 venv，设 Key 与 HF 镜像
export DEEPSEEK_API_KEY=sk-xxx
export HF_ENDPOINT=https://hf-mirror.com

# 1. 建库
python scripts/ingest.py

# 2. 起服务
uvicorn src.api.app:app --port 8100

# 3. 提问
curl -X POST localhost:8100/api/chat \
     -H "Content-Type: application/json" \
     -d '{"question": "年假满3年有几天？"}'
```

## 验收基线

- 场景用例 S1~S5 全过；**S4 防幻觉（知识库外问题必须答"未找到"）为一票否决项**；
- 评估指标：faithfulness ≥ 0.8，tool_accuracy ≥ 0.9；
- 详见 [docs/03_开发计划.md](docs/03_开发计划.md) 速查表。

## 与课程的关系

本项目是课程第 13 章案例 1（企业知识库 Agent）的工程化扩展：语料从 1 份扩到 6 份分域文档、检索升级为多路召回+Rerank、增加 FastAPI 服务层与评估回归。课程代码位置见 `examples/`（母版映射表：[docs/02_架构设计.md](docs/02_架构设计.md) 第 7 节）。
