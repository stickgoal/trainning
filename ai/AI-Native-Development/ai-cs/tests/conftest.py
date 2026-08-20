"""pytest 全局配置：确保 ai-cs 根目录在 sys.path（任意 cwd 均可运行）。"""

import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))
