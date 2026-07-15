#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""生成 HumanInTheLoop 改造前后并排(difflib)可视化对比 HTML。"""
import difflib
import html
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BEFORE = os.path.join(ROOT, "compare", "humanintheloop", "before")
AFTER = os.path.join(ROOT, "src", "main", "java", "com", "example", "agentic", "humanintheloop")

# (展示名, before 文件名, after 相对 humanintheloop 包的路径 或 None 表示已删除)
PAIRS = [
    ("HumanInTheLoopService.java", "HumanInTheLoopService.java",
     os.path.join("service", "HumanInTheLoopService.java")),
    ("HumanInTheLoopController.java", "HumanInTheLoopController.java",
     os.path.join("controller", "HumanInTheLoopController.java")),
    ("HumanApprovalWorkflow.java（改造后已删除）", "HumanApprovalWorkflow.java", None),
]


def read(path):
    try:
        with open(path, "r", encoding="utf-8") as f:
            return f.read().splitlines()
    except FileNotFoundError:
        return None


def esc(s):
    return html.escape(s)


sections = []
nav_items = []
for idx, (title, before_name, after_rel) in enumerate(PAIRS):
    b = read(os.path.join(BEFORE, before_name))
    a = read(os.path.join(AFTER, after_rel)) if after_rel else None
    if a is None:
        a = ["// 该文件在改造后被删除，不再存在于代码库中。"]
    b = b or ["// (改造前无此文件)"]
    differ = difflib.HtmlDiff(wrapcolumn=120)
    table = differ.make_table(b, a, fromdesc="改造前 (before)", todesc="改造后 (after)",
                              context=False, numlines=0)
    anchor = f"sec{idx}"
    nav_items.append(f'<li><a href="#{anchor}">{esc(title)}</a></li>')
    sections.append(
        f'<section id="{anchor}"><h2>{esc(title)}</h2>'
        f'<p class="meta">改造前：<code>compare/humanintheloop/before/{esc(before_name)}</code> '
        f'&nbsp;|&nbsp; 改造后：<code>src/main/java/com/example/agentic/humanintheloop/'
        f'{esc(after_rel or "(已删除)")}</code></p>{table}</section>'
    )

nav = "<ul>" + "".join(nav_items) + "</ul>"
body = "\n".join(sections)

doc = f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>HumanInTheLoop 改造前后对比</title>
<style>
  body {{ font-family: -apple-system, "Segoe UI", "Microsoft YaHei", sans-serif; margin: 0; color: #222; }}
  header {{ background:#0f172a; color:#fff; padding:18px 24px; }}
  header h1 {{ margin:0; font-size:20px; }}
  header p {{ margin:6px 0 0; font-size:13px; color:#cbd5e1; }}
  nav {{ background:#f1f5f9; padding:12px 24px; border-bottom:1px solid #e2e8f0; }}
  nav ul {{ margin:0; padding-left:18px; font-size:14px; }}
  nav a {{ color:#0f172a; text-decoration:none; }}
  nav a:hover {{ text-decoration:underline; }}
  main {{ padding:16px 24px; }}
  section {{ margin-bottom:36px; }}
  section h2 {{ font-size:17px; border-left:4px solid #2563eb; padding-left:10px; }}
  .meta {{ font-size:12px; color:#64748b; margin:4px 0 10px; }}
  .meta code {{ background:#f1f5f9; padding:1px 5px; border-radius:4px; }}
  table.diff {{ width:100%; border-collapse:collapse; font-family:"SFMono-Regular",Consolas,Menlo,monospace; font-size:12px; }}
  table.diff th, table.diff td {{ border:1px solid #e2e8f0; padding:2px 6px; vertical-align:top; white-space:pre; }}
  table.diff thead th {{ background:#0f172a; color:#fff; }}
  td.diff_header {{ background:#f8fafc; text-align:right; color:#94a3b8; width:36px; }}
  .diff_add {{ background:#dcfce7; }}
  .diff_chg {{ background:#fef9c3; }}
  .diff_sub {{ background:#fee2e2; }}
</style>
</head>
<body>
<header>
  <h1>HumanInTheLoop 改造前后对比</h1>
  <p>左侧 = 改造前（tag <code>hitl-before-refactor</code> / commit e748830）；右侧 = 改造后（HEAD，commit 6d1094f）。
     红色=删除，绿色=新增，黄色=修改。</p>
</header>
<nav><b>目录</b>{nav}</nav>
<main>{body}</main>
</body>
</html>"""

out = os.path.join(ROOT, "compare", "humanintheloop", "humanintheloop-diff.html")
with open(out, "w", encoding="utf-8") as f:
    f.write(doc)
print("written:", out, len(doc), "bytes")
