window.slideDataMap.set(12, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 4 章 · 阶段 B · 重点</span>
        <span class="text-[13px]" style="color:#64748B;">LangGraph</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">LangGraph：为什么 Chain 不够</h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-5 gap-6">
      <div class="col-span-2 space-y-4">
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#F87171;">Chain 的局限</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">Chain 是<span class="font-semibold" style="color:#F87171;">单向流水线</span>；Agent 需要<span class="font-semibold" style="color:#0891B2;">循环 + 条件 + 状态</span>。</p>
        </div>
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#059669;">LangGraph 的答案</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">显式<span class="font-semibold" style="color:#059669;">状态机 + 图</span>，把 Agent 流程画成可执行的有向图。</p>
        </div>
        <div class="rounded-lg p-6" style="background:#EFF6FF;border-left:3px solid #2563EB;">
          <p class="text-[15px] leading-relaxed" style="color:#334155;">💡 一句话：把 Agent 从"黑盒循环"变成"可中断、可观察的流程"。</p>
        </div>
      </div>

      <div class="col-span-3">
        <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">4 个原语 · Java 类比</div>
        <div class="space-y-3">
          <div class="flex items-center gap-4 rounded p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
            <span class="w-16 text-center font-mono text-[15px] font-bold" style="color:#2563EB;">Graph</span>
            <div class="flex-1"><div class="text-[15px]" style="color:#1E293B;">工作流骨架（有向图）</div><div class="text-[13px]" style="color:#64748B;">类比 BPMN 流程定义</div></div>
          </div>
          <div class="flex items-center gap-4 rounded p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
            <span class="w-16 text-center font-mono text-[15px] font-bold" style="color:#0891B2;">Node</span>
            <div class="flex-1"><div class="text-[15px]" style="color:#1E293B;">节点 = 一个函数 (state) → 更新</div><div class="text-[13px]" style="color:#64748B;">类比一个 Service 方法</div></div>
          </div>
          <div class="flex items-center gap-4 rounded p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
            <span class="w-16 text-center font-mono text-[15px] font-bold" style="color:#7C3AED;">Edge</span>
            <div class="flex-1"><div class="text-[15px]" style="color:#1E293B;">边 = 跳转（含条件边）</div><div class="text-[13px]" style="color:#64748B;">类比状态机的 transition guard</div></div>
          </div>
          <div class="flex items-center gap-4 rounded p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
            <span class="w-16 text-center font-mono text-[15px] font-bold" style="color:#059669;">State</span>
            <div class="flex-1"><div class="text-[15px]" style="color:#1E293B;">节点间共享状态（显式化）</div><div class="text-[13px]" style="color:#64748B;">类比流程变量（reducer 控制合并）</div></div>
          </div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-5">
      <div class="rounded-lg px-6 py-4 flex items-center gap-4" style="background:#EFF6FF;border-left:3px solid #0891B2;">
        <span class="text-[15px] font-semibold" style="color:#0891B2;">人工介入</span>
        <p class="text-[16px]" style="color:#1E293B;"><span class="font-mono" style="color:#D97706;">interrupt_before=["human_review"]</span> —— 在指定节点前暂停，等审批后继续。</p>
      </div>
    </div>
  </div>
`);
