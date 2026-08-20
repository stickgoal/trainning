# ai-cs · 企业智能客服系统（RAG）

> 基于企业内部规章制度的智能客服：问制度走 RAG 检索，问数据走工具查询，一个 Agent 两种能力。
> 技术栈（课程第 13 章综合案例 1）：**Python + LangChain + RAG + Chroma + MCP**

---

## 项目状态

**已实施（M0~M6 完成并提交，M7 可选未做）。** 当前实现：

- ✅ M0 环境与骨架：config / logging / 目录结构
- ✅ M1 语料与模拟数据：6 份制度文档 + mock_data + 测试集 12 条
- ✅ M2 RAG 基础管线：Chroma 建库 + LCEL 问答链（离线检索命中率 1.00）
- ✅ M3 多路召回 + Rerank：BM25 + Ensemble + Rerank（完整精排链路实测命中率 1.00，默认启用）
- ✅ M4 工具 + Agent 组装：三个工具 + create_agent，S1~S5 场景测试全过
- ✅ M5 FastAPI 服务 + Web 聊天页
- ✅ M6 评估基线：faithfulness/tool_accuracy 1.00，防幻觉一票否决通过
- ⏭ M7（可选）MCP Server 独立进程形态：未实施

实施进度记录：`docs/progress.md`。

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

## 快速开始

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
