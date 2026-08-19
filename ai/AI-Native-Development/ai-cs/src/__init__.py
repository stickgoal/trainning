"""ai-cs 包入口。

Windows 上 torch + transformers 以 meta 设备/mmap 加载大模型（如 bge-reranker-base）
可能触发 access violation；在包首次导入时钳制线程数可规避（M3 实测）。
setdefault 保留用户显式设置。
"""

import os

os.environ.setdefault("OMP_NUM_THREADS", "1")
os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
