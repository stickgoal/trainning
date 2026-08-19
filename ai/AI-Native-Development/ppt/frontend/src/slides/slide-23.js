window.slideDataMap.set(23, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 11 章 · 阶段 C</span>
        <span class="text-[13px]" style="color:#64748B;">主流框架选型与生态进阶</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">框架选型：先问需求，再选框架</h2>
    </div>

    <div class="px-16 mt-6">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">决策三问</div>
      <div class="grid grid-cols-3 gap-5">
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[14px] font-bold" style="color:#2563EB;">Q1</div>
          <div class="text-[16px] mt-1.5 font-semibold" style="color:#1E293B;">单 Agent 还是多 Agent？</div>
        </div>
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[14px] font-bold" style="color:#0891B2;">Q2</div>
          <div class="text-[16px] mt-1.5 font-semibold" style="color:#1E293B;">重 RAG 还是重流程编排？</div>
        </div>
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[14px] font-bold" style="color:#7C3AED;">Q3</div>
          <div class="text-[16px] mt-1.5 font-semibold" style="color:#1E293B;">是否跨语言栈（.NET / Java）？</div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">框架速查表</div>
      <div class="grid grid-cols-2 gap-x-6 gap-y-3">
        <div class="flex items-center justify-between rounded px-5 py-3" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <span class="text-[14.5px]" style="color:#64748B;">单 Agent / 通用</span><span class="text-[15px] font-semibold" style="color:#2563EB;">LangChain / LangGraph</span>
        </div>
        <div class="flex items-center justify-between rounded px-5 py-3" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <span class="text-[14.5px]" style="color:#64748B;">多 Agent 角色扮演</span><span class="text-[15px] font-semibold" style="color:#0891B2;">CrewAI</span>
        </div>
        <div class="flex items-center justify-between rounded px-5 py-3" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <span class="text-[14.5px]" style="color:#64748B;">多 Agent 对话式</span><span class="text-[15px] font-semibold" style="color:#0891B2;">AutoGen</span>
        </div>
        <div class="flex items-center justify-between rounded px-5 py-3" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <span class="text-[14.5px]" style="color:#64748B;">轻量 OpenAI 风格</span><span class="text-[15px] font-semibold" style="color:#7C3AED;">OpenAI Agents SDK</span>
        </div>
        <div class="flex items-center justify-between rounded px-5 py-3" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <span class="text-[14.5px]" style="color:#64748B;">提示词优化编译</span><span class="text-[15px] font-semibold" style="color:#7C3AED;">DSPy</span>
        </div>
        <div class="flex items-center justify-between rounded px-5 py-3" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <span class="text-[14.5px]" style="color:#64748B;">跨语言企业</span><span class="text-[15px] font-semibold" style="color:#059669;">Semantic Kernel</span>
        </div>
        <div class="flex items-center justify-between rounded px-5 py-3" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <span class="text-[14.5px]" style="color:#64748B;">RAG 专用</span><span class="text-[15px] font-semibold" style="color:#D97706;">LlamaIndex / Haystack</span>
        </div>
        <div class="flex items-center justify-center rounded px-5 py-3" style="background:#EFF6FF;border:1px dashed #E2E8F0;">
          <span class="text-[14.5px]" style="color:#64748B;">框架是手段不是目的，别被营销带偏</span>
        </div>
      </div>
    </div>
  </div>
`);
