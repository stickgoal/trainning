window.slideDataMap.set(16, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 6 章 · 阶段 B</span>
        <span class="text-[13px]" style="color:#64748B;">LlamaIndex 与检索增强进阶</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">LlamaIndex 与 LangChain 怎么分工</h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-2 gap-5">
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[16px] font-semibold mb-1.5" style="color:#2563EB;">LangChain · 通用编排</div>
        <p class="text-[14.5px] leading-relaxed" style="color:#334155;">覆盖 Prompt / 检索 / 记忆 / 工具全链路，组件多而全。</p>
      </div>
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[16px] font-semibold mb-1.5" style="color:#0891B2;">LlamaIndex · RAG 专用</div>
        <p class="text-[14.5px] leading-relaxed" style="color:#334155;">专注知识索引与检索，抽象更贴合 RAG 场景，可混用。</p>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">核心抽象 · 两阶段（索引建库 / 查询实时）</div>
      <div class="rounded-lg p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="flex items-center justify-between text-center">
          <div class="flex-1"><div class="font-mono text-[13px] font-bold" style="color:#2563EB;">Document</div></div>
          <div class="text-[18px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[13px] font-bold" style="color:#2563EB;">Node</div></div>
          <div class="text-[18px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[13px] font-bold" style="color:#0891B2;">Index</div></div>
          <div class="text-[18px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[13px] font-bold" style="color:#0891B2;">Retriever</div></div>
          <div class="text-[18px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[13px] font-bold" style="color:#7C3AED;">Postprocessor</div></div>
          <div class="text-[18px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[13px] font-bold" style="color:#059669;">QueryEngine</div></div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">多路召回 · 三种形态</div>
      <div class="grid grid-cols-3 gap-5">
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-1.5" style="color:#0891B2;">路由 Router</div>
          <p class="text-[14px] leading-relaxed" style="color:#64748B;">按问题类型分发到不同索引</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-1.5" style="color:#7C3AED;">融合 RRF</div>
          <p class="text-[14px] leading-relaxed" style="color:#64748B;">多路结果融合排序</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-1.5" style="color:#059669;">子问题分解</div>
          <p class="text-[14px] leading-relaxed" style="color:#64748B;">拆成子问题分别检索再汇总</p>
        </div>
      </div>
    </div>
  </div>
`);
