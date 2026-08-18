#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
White-theme remake pipeline for the AI-Native-Development training deck:
1. Theme-swap all 28 base slides from dark (#0B1220) to white, with contrast-safe
   syntax-highlight + accent colors (single-pass hex map, no double-substitution).
2. Generate 13 dedicated diagram slides (mermaid PNGs, white bg) and interleave
   them after each chapter's concept slide.
3. Rewrite index.html <script> order + fix footer page numbers to the new total.
4. Update page-global-config.json + index.html :root background to white.
"""
import os, re, json

ROOT = r'D:\workspace\处理\trainning\ai\AI-Native-Development'
SLIDES = os.path.join(ROOT, 'frontend', 'src', 'slides')
IDX = os.path.join(ROOT, 'frontend', 'index.html')
CFG = os.path.join(ROOT, 'docs', 'page-global-config.json')

# ---- 1. Hex palette map (dark -> white-theme, contrast safe) -------------
HEX_MAP = {
    '#0B1220': '#FFFFFF',  # page bg
    '#0A1120': '#F1F5F9',  # code header bg
    '#0F172A': '#F8FAFC',  # code bg
    '#0F1B2E': '#EFF6FF',  # callout bg (blue-50)
    '#111C33': '#F1F5F9',  # card bg
    '#1E3A5F': '#E2E8F0',  # border
    '#F1F5F9': '#0F172A',  # heading text
    '#E2E8F0': '#1E293B',  # code text
    '#CBD5E1': '#334155',  # body text
    '#94A3B8': '#64748B',  # muted text
    '#64748B': '#64748B',  # keep (works on white)
    '#475569': '#475569',  # keep
    '#3B82F6': '#2563EB',  # primary blue
    '#60A5FA': '#2563EB',  # blue accent
    '#22D3EE': '#0891B2',  # cyan accent
    '#8B5CF6': '#7C3AED',  # violet accent
    '#A78BFA': '#7C3AED',  # violet accent
    '#C084FC': '#7C3AED',  # keyword purple
    '#34D399': '#059669',  # green accent
    '#FBBF24': '#D97706',  # amber accent
    '#86EFAC': '#15803D',  # string green
    '#FDBA74': '#C2410C',  # number orange
    '#F87171': '#F87171',  # red (keep - mac dot)
    '#F472B6': '#DB2777',  # pink accent
}
# single-pass regex: match any old hex, replace by its mapped value (no re-scan)
HEX_RE = re.compile('|'.join(re.escape(k) for k in HEX_MAP), re.IGNORECASE)
def swap_hexes(text):
    return HEX_RE.sub(lambda m: HEX_MAP[m.group(0)], text)

# ---- 2. Order & diagram definitions --------------------------------------
# Concept slide per chapter -> diagram file. Diagram keys 101..113.
DIAG = [
    # (after_base_slide, key, diagram_png, chapter_badge, chapter_name, title, caption)
    (5,  101, 'diagram-01_python_ai_ecosystem.png', '第 1 章', 'Python AI 生态概览',
     'Python AI 生态全景', '五层技术栈：从 Web 框架到工具协议，对应 Java 工程师熟悉的组件'),
    (7,  102, 'diagram-02_llm_app_stack.png', '第 2 章', 'LLM Application 开发基础',
     'LLM 应用数据流', 'User → Application → LLM → Tools / Knowledge / Memory 的完整闭环'),
    (10, 103, 'diagram-03_langchain.png', '第 3 章', 'LangChain',
     'LangChain 组件关系', 'Model / Prompt / Chain / Retriever / … 围绕 Runnable 统一抽象组合'),
    (12, 104, 'diagram-04_langgraph.png', '第 4 章', 'LangGraph',
     'LangGraph 状态机', 'Graph / Node / Edge / State 构成可执行有向图，支持循环与中断'),
    (14, 105, 'diagram-05_rag.png', '第 5 章', 'RAG 技术体系',
     'RAG 检索增强管线', 'Document→Chunk→Embedding→VectorDB→Retriever→LLM，多路召回 + Rerank'),
    (16, 106, 'diagram-06_llamaindex.png', '第 6 章', 'LlamaIndex 与检索增强进阶',
     'LlamaIndex 检索抽象', 'Document→Node→Index→Retriever→Postprocessor→QueryEngine 两阶段'),
    (18, 107, 'diagram-07_mcp.png', '第 7 章', 'MCP',
     'MCP 协议拓扑', 'N 个 Agent + M 个系统 = N+M 份实现，标准化工具协议'),
    (20, 108, 'diagram-08_a2a.png', '第 8 章', 'A2A',
     'A2A 多 Agent 协作', 'Research / Coding / Testing 角色化分工与任务委派'),
    (21, 109, 'diagram-09_harness.png', '第 9 章', 'Harness Engineering',
     'Harness 工程底座', 'Context / Memory / Tool Runtime / Permission / Eval / Monitor / Feedback'),
    (22, 110, 'diagram-10_skill.png', '第 10 章', 'Agent Skill',
     'Agent Skill 结构', 'Prompt + Tool + 说明 + 触发条件 封装为可复用能力'),
    (23, 111, 'diagram-11_framework_selection.png', '第 11 章', '主流框架选型与生态进阶',
     '框架选型地图', '按单/多 Agent、RAG/编排、跨语言需求对照选型'),
    (25, 112, 'diagram-12_enterprise.png', '第 12 章', '企业级 Agent 架构',
     '企业级六层架构', '用户 → Agent App → 能力层 → Harness → 知识/系统，系统工程'),
    (26, 113, 'diagram-13_cases.png', '第 13 章', '综合案例',
     '综合案例架构', '知识库 Agent（检索为主）与研发 Agent（流程为主）'),
]

# Build final ordered list of slide NUMBERS (base + diagram keys interleaved)
base_nums = list(range(1, 29))
order_nums = []
diag_by_after = {d[0]: d for d in DIAG}
for n in base_nums:
    order_nums.append(n)
    if n in diag_by_after:
        order_nums.append(diag_by_after[n][1])
TOTAL = len(order_nums)  # 41
pos_of = {num: i + 1 for i, num in enumerate(order_nums)}

# ---- 3. Theme swap base slides + fix footers -----------------------------
def swap_base(num):
    p = os.path.join(SLIDES, f'slide-{num}.js')
    with open(p, 'r', encoding='utf-8') as f:
        t = f.read()
    t = swap_hexes(t)
    # cover title accent color (Tailwind text-blue-400 -> text-blue-700)
    t = t.replace('text-blue-400', 'text-blue-700')
    # footer total + page number
    t = re.sub(r'·\s*\d+\s*/\s*28', f'· {pos_of[num]} / {TOTAL}', t)
    t = t.replace('共 28 页', f'共 {TOTAL} 页')
    with open(p, 'w', encoding='utf-8') as f:
        f.write(t)
    return t

for n in base_nums:
    swap_base(n)
print('theme-swapped base slides 1..28')

# ---- 4. Create diagram slides --------------------------------------------
DIAG_TPL = '''window.slideDataMap.set({KEY}, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-white" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="absolute top-0 left-0 w-full h-1" style="background:#2563EB;"></div>
    <div class="absolute top-20 right-24 w-24 h-24 border-t border-r" style="border-color:#E2E8F0;"></div>

    <div class="relative z-10 px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#EFF6FF;color:#2563EB;">{BADGE} · 架构图</span>
        <span class="text-[13px]" style="color:#64748B;">{CNAME}</span>
      </div>
      <h2 class="text-[34px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">{TITLE}</h2>
    </div>

    <div class="px-16 mt-3 flex items-center justify-center" style="height:600px;">
      <img src="/assets/images/{PNG}" alt="{TITLE}" style="max-width:96%;max-height:600px;object-fit:contain;border:1px solid #E2E8F0;border-radius:12px;background:#FFFFFF;box-shadow:0 1px 3px rgba(15,23,42,0.08);" />
    </div>

    <div class="px-16 mt-1">
      <p class="text-[15px] leading-relaxed" style="color:#475569;">{CAPTION}</p>
    </div>

    <div class="absolute bottom-10 right-24 text-[14px]" style="color:#94A3B8;">Python AI Agent 快速扫盲 · {POS} / {TOTAL}</div>
  </div>
`);
'''
for (after, key, png, badge, cname, title, caption) in DIAG:
    content = (DIAG_TPL
               .replace('{KEY}', str(key))
               .replace('{BADGE}', badge)
               .replace('{CNAME}', cname)
               .replace('{TITLE}', title)
               .replace('{PNG}', png)
               .replace('{CAPTION}', caption)
               .replace('{POS}', str(pos_of[key]))
               .replace('{TOTAL}', str(TOTAL)))
    with open(os.path.join(SLIDES, f'slide-{key}.js'), 'w', encoding='utf-8') as f:
        f.write(content)
print('created 13 diagram slides:', [d[1] for d in DIAG])

# ---- 5. Rewrite index.html script order ----------------------------------
with open(IDX, 'r', encoding='utf-8') as f:
    idx = f.read()
new_scripts = '\n'.join(
    f'    <script type="module" src="/src/slides/slide-{n}.js"></script>' for n in order_nums
)
marker_re = re.compile(r'(<!--\s*开始加载slide-N\.js区域\s*-->).*?(<!--\s*结束加载slide-N\.js区域\s*-->)', re.S)
idx, nsub = marker_re.subn(lambda m: f'{m.group(1)}\n{new_scripts}\n    {m.group(2)}', idx)
if nsub != 1:
    raise RuntimeError(f'index.html marker replace failed (nsub={nsub})')

# update :root --slide-background to white
idx = idx.replace("--slide-background: #0B1220;", "--slide-background: #FFFFFF;")
with open(IDX, 'w', encoding='utf-8') as f:
    f.write(idx)
print('rewrote index.html order + white bg:', TOTAL, 'slides')

# ---- 6. Update config json -----------------------------------------------
try:
    with open(CFG, 'r', encoding='utf-8') as f:
        cfg = json.load(f)
    cfg['background'] = {'type': 'color', 'value': '#FFFFFF'}
    cfg['colorScheme'] = 'White Tech'
    with open(CFG, 'w', encoding='utf-8') as f:
        json.dump(cfg, f, ensure_ascii=False, indent=2)
    print('updated page-global-config.json')
except Exception as e:
    print('config update skipped:', e)

print('DONE. TOTAL =', TOTAL)
