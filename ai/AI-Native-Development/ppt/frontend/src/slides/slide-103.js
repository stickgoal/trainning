window.slideDataMap.set(103, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-white" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="absolute top-0 left-0 w-full h-1" style="background:#2563EB;"></div>
    <div class="absolute top-20 right-24 w-24 h-24 border-t border-r" style="border-color:#E2E8F0;"></div>

    <div class="relative z-10 px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#EFF6FF;color:#2563EB;">第 3 章 · 架构图</span>
        <span class="text-[13px]" style="color:#64748B;">LangChain</span>
      </div>
      <h2 class="text-[34px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">LangChain 组件关系</h2>
    </div>

    <div class="px-16 mt-3 flex items-center justify-center" style="height:600px;">
      <img src="/assets/images/diagram-03_langchain.png" alt="LangChain 组件关系" style="max-width:96%;max-height:600px;object-fit:contain;border:1px solid #E2E8F0;border-radius:12px;background:#FFFFFF;box-shadow:0 1px 3px rgba(15,23,42,0.08);" />
    </div>

    <div class="px-16 mt-1">
      <p class="text-[15px] leading-relaxed" style="color:#475569;">Model / Prompt / Chain / Retriever / … 围绕 Runnable 统一抽象组合</p>
    </div>

    <div class="absolute bottom-10 right-24 text-[14px]" style="color:#94A3B8;">Python AI Agent 快速扫盲 · 13 / 41</div>
  </div>
`);
