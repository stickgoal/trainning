"""M2 离线检索测试（不调 LLM、不耗 API 额度，最高频回归）。

验收标准（03_开发计划.md M2）：
- 对测试集制度类问题，gold 关键词出现在召回片段中，检索命中率 ≥ 0.8。

判定规则：
- 取制度类且非知识库外用例（expect_tool == "search_policy" 且 gold_answer 非"未找到"）；
- 对每问 invoke retriever，拼接召回片段文本，任一 gold keyword 命中即算该题命中；
- 整体命中率 = 命中数 / 总数 ≥ 0.8。

若向量库不存在会自动建库（同 scripts/ingest.py 逻辑），便于直接跑测试。
"""

import pytest

from src.rag.loader import load_and_split
from src.rag.retriever import build_retriever
from src.rag.vectorstore import load_vectorstore
from tests.testdata import TESTCASES

# 召回条数（basic k=3；hybrid 由 reranker 收口为 top4，取更大值覆盖）
TOPK = 4


@pytest.fixture(scope="module")
def retriever():
    """保证向量库存在并返回检索器（module 级复用，只建一次）。"""
    if load_vectorstore() is None:
        # 库不存在：自动建库（对齐 ingest 逻辑）
        from src.rag.vectorstore import build_vectorstore

        build_vectorstore(load_and_split())
    return build_retriever()


def _policy_cases():
    """制度类且非知识库外用例（用于检索命中判定）。"""
    return [
        t
        for t in TESTCASES
        if t["expect_tool"] == "search_policy" and "未找到" not in t["gold_answer"]
    ]


def _hit(case, retriever) -> tuple[bool, str]:
    """返回 (是否命中, 召回片段拼接文本)。任一 gold keyword 出现在片段中即命中。"""
    docs = retriever.invoke(case["question"])[:TOPK]
    text = "\n".join(d.page_content for d in docs)
    return any(kw in text for kw in case["keywords"]), text


def test_retrieval_hit_rate_ge_08(retriever):
    """核心验收：检索命中率 ≥ 0.8。"""
    cases = _policy_cases()
    assert len(cases) >= 5, "制度类用例不足，无法评估命中率"

    hits = 0
    details = []
    for c in cases:
        hit, text = _hit(c, retriever)
        hits += int(hit)
        details.append(f"  {'✓' if hit else '✗'} {c['id']} {c['question']} (kw={c['keywords']})")

    rate = hits / len(cases)
    print(f"\n检索命中率: {hits}/{len(cases)} = {rate:.2f}")
    print("\n".join(details))
    assert rate >= 0.8, f"检索命中率 {rate:.2f} < 0.8"


def test_retriever_returns_docs_with_source_metadata(retriever):
    """召回片段应带 source metadata（供来源引用）。"""
    docs = retriever.invoke(_policy_cases()[0]["question"])
    assert docs, "检索结果为空"
    for d in docs:
        assert d.metadata.get("source", "").startswith("knowledge_docs/"), (
            f"片段缺 source metadata: {d.metadata}"
        )


if __name__ == "__main__":
    pytest.main([__file__, "-v", "-s"])
