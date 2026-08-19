"""文档加载与切分（M2，T2.1）。

遍历 knowledge_docs/**/*.txt，为每个 chunk 注入 metadata：
- source: 相对 ai-cs 根的路径（如 knowledge_docs/finance/travel_policy.txt）
- domain: hr / finance / admin（按一级子目录推断）
- doc:    文件名
"""

from pathlib import Path

from langchain_community.document_loaders import TextLoader
from langchain_core.documents import Document
from langchain_text_splitters import RecursiveCharacterTextSplitter

from src.core.config import Config

# 域 → 子目录名的映射（用于 metadata 校验与统计）
DOMAIN_DIRS = ["hr", "finance", "admin"]


def _list_docs() -> list[Path]:
    """返回所有语料文件（*.txt，按路径排序保证可复现）。"""
    return sorted(Config.KNOWLEDGE_DIR.rglob("*.txt"))


def _domain_of(path: Path) -> str:
    """从相对路径推断域：knowledge_docs/<domain>/<file>。"""
    rel = path.relative_to(Config.KNOWLEDGE_DIR)
    parts = rel.parts
    return parts[0] if parts and parts[0] in DOMAIN_DIRS else "other"


def load_documents() -> list[Document]:
    """加载全部语料为 Document（未切分），保留原始元数据。"""
    docs: list[Document] = []
    for path in _list_docs():
        loader = TextLoader(str(path), encoding="utf-8")
        loaded = loader.load()
        for d in loaded:
            rel = path.relative_to(Config.KNOWLEDGE_DIR).as_posix()
            d.metadata.update(
                {
                    "source": f"knowledge_docs/{rel}",
                    "domain": _domain_of(path),
                    "doc": path.name,
                }
            )
        docs.extend(loaded)
    return docs


def split_documents(docs: list[Document]) -> list[Document]:
    """中文友好切分：chunk_size=400 / overlap=60，分隔符含中文标点。"""
    splitter = RecursiveCharacterTextSplitter(
        chunk_size=Config.CHUNK_SIZE,
        chunk_overlap=Config.CHUNK_OVERLAP,
        separators=Config.SEPARATORS,
    )
    chunks = splitter.split_documents(docs)
    # 给每个 chunk 补充 seq 序号，便于溯源
    for i, c in enumerate(chunks):
        c.metadata["seq"] = i
    return chunks


def load_and_split() -> list[Document]:
    """加载 + 切分，一步到位（ingest 与 hybrid 检索共用的数据源）。"""
    return split_documents(load_documents())
