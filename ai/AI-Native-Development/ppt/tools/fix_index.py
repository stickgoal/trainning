#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re, os
ROOT = r'D:\workspace\处理\trainning\ai\AI-Native-Development'
IDX = os.path.join(ROOT, 'ppt', 'frontend', 'index.html')
with open(IDX, 'r', encoding='utf-8') as f:
    idx = f.read()

# Ordered slide numbers (base 1..28 + diagram keys 101..113 interleaved)
base = list(range(1, 29))
DIAG_AFTER = {5:101,7:102,10:103,12:104,14:105,16:106,18:107,20:108,21:109,22:110,23:111,25:112,26:113}
order = []
for n in base:
    order.append(n)
    if n in DIAG_AFTER:
        order.append(DIAG_AFTER[n])

new_scripts = '\n'.join(f'    <script type="module" src="/src/slides/slide-{n}.js"></script>' for n in order)

# Locate the start marker, then drop every stale slide script tag that follows
start_marker = '<!--  开始加载slide-N.js区域 -->'
si = idx.index(start_marker)
# Everything from start marker to end of file; remove all old slide script tags
tail = idx[si:]
tail = re.sub(r'\s*<script[^>]*src=["\']/src/slides/slide-\d+\.js["\'][^>]*>\s*</script>', '', tail)
# Keep the end marker if present, else reconstruct minimal
if '结束加载slide-N.js区域' in tail:
    # keep end marker line
    em = tail.index('结束加载slide-N.js区域')
    em_end = tail.index('-->', em) + 3
    head_tail = tail[:em_end]
    rest = tail[em_end:]
    # remove any leftover slide scripts in rest (shouldn't be)
    rebuilt = start_marker + '\n' + new_scripts + '\n    ' + head_tail.strip() + '\n' + rest
else:
    # no end marker: cut before </body>
    bi = tail.index('</body>')
    rebuilt = start_marker + '\n' + new_scripts + '\n  ' + tail[bi:]

idx = idx[:si] + rebuilt
with open(IDX, 'w', encoding='utf-8') as f:
    f.write(idx)

# verify
cnt = len(re.findall(r'src=["\']/src/slides/slide-\d+\.js["\']', idx))
print('slide script tags now:', cnt, 'expected', len(order))
