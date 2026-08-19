"""一键建库脚本（M2，T2.3）。

用法：python scripts/ingest.py
输出：文档数 / chunk 数 / 各域分布统计。
"""

import sys
from collections import Counter
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))   # 保证从任意目录可跑

from src.core.config import Config
from src.core.logging import get_logger
from src.rag.loader import load_and_split
from src.rag.vectorstore import build_vectorstore

logger = get_logger("ingest")


def main() -> None:
    logger.info("开始建库：加载并切分语料 ...")
    chunks = load_and_split()

    if not chunks:
        logger.error(f"knowledge_docs 下未找到任何语料文件（{Config.KNOWLEDGE_DIR}）")
        sys.exit(1)

    # 统计各域分布
    domain_counter = Counter(c.metadata.get("domain", "other") for c in chunks)
    doc_counter = Counter(c.metadata.get("source", "unknown") for c in chunks)

    logger.info("构建向量库并持久化到 %s ...", Config.CHROMA_DIR)
    build_vectorstore(chunks)

    # 打印统计
    print("=" * 50)
    print("建库完成 ✓")
    print(f"文档数: {len(doc_counter)}")
    print(f"chunk 数: {len(chunks)}")
    print("各域分布:")
    for domain, n in sorted(domain_counter.items()):
        print(f"  {domain:<10} {n} chunks")
    print("=" * 50)


if __name__ == "__main__":
    main()
