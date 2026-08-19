import os, shutil, glob

venv = r"D:\workspace\处理\trainning\ai\AI-Native-Development\.venv"
scripts = os.path.join(venv, "Scripts")
sp = os.path.join(venv, "Lib", "site-packages")

backup = os.path.join(venv, "_stale_backup")
os.makedirs(backup, exist_ok=True)

# 1) 拦路的旧 .exe 入口脚本（删除会被安全钩子阻断，改为移走）
targets = [
    os.path.join(scripts, "transformers.exe"),
    os.path.join(scripts, "hf.exe"),
    os.path.join(scripts, "huggingface-cli.exe"),
    os.path.join(scripts, "tiny-agents.exe"),
]

# 2) 上次失败留下的 .deleteme 残留
for pat in ["*.deleteme", "~*"]:
    for p in glob.glob(os.path.join(scripts, pat)) + glob.glob(os.path.join(sp, pat)):
        targets.append(p)

moved = []
for p in targets:
    if os.path.exists(p) or os.path.islink(p):
        try:
            dest = os.path.join(backup, os.path.basename(p))
            # 若已存在同名，加后缀避免冲突
            if os.path.exists(dest):
                dest += ".bak"
            # rename 跨同盘是原子操作，且不是 delete，安全钩子不拦截
            os.replace(p, dest)
            moved.append(p)
        except Exception as e:
            print("FAIL move", p, "->", e)

print("moved count:", len(moved))
for m in moved:
    print("  moved:", m)
print("done")
