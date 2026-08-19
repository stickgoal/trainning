import importlib, traceback

mods = [
    ("openai", "openai"),
    ("langchain", "langchain"),
    ("langchain_core", "langchain-core"),
    ("langchain_community", "langchain-community"),
    ("langchain_openai", "langchain-openai"),
    ("langgraph", "langgraph"),
    ("chromadb", "chromadb"),
    ("faiss", "faiss-cpu"),
    ("sentence_transformers", "sentence-transformers"),
    ("llama_index", "llama-index"),
    ("llama_index_core", "llama-index-core"),
    ("ragas", "ragas"),
    ("deepeval", "deepeval"),
    ("langfuse", "langfuse"),
    ("langsmith", "langsmith"),
    ("mcp", "mcp"),
    ("langchain_mcp_adapters", "langchain-mcp-adapters"),
    ("pydantic", "pydantic"),
    ("fastapi", "fastapi"),
    ("pypdf", "pypdf"),
    ("unstructured", "unstructured"),
    ("rank_bm25", "rank-bm25"),
]

ok, fail = [], []
for mod, label in mods:
    try:
        m = importlib.import_module(mod)
        ver = getattr(m, "__version__", "?")
        ok.append(f"{label}=={ver}")
    except Exception as e:
        fail.append(f"{label}: {type(e).__name__}: {str(e)[:150]}")

print("=== OK ({}) ===".format(len(ok)))
for x in ok:
    print("  ", x)
print("=== FAIL ({}) ===".format(len(fail)))
for x in fail:
    print("  ", x)

# 额外验证 llama-index 关键子模块（课程第6章用到）
print("=== llama-index submodules ===")
for sub in ["llama_index.core", "llama_index.llms.openai", "llama_index.embeddings.huggingface", "llama_index.retrievers.bm25"]:
    try:
        importlib.import_module(sub)
        print("  OK", sub)
    except Exception as e:
        print("  FAIL", sub, "->", type(e).__name__, str(e)[:120])

print("RESULT:", "ALL_OK" if not fail else "HAS_FAILURES")
