window.slideDataMap.set(18, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 7 章 · 阶段 C</span>
        <span class="text-[13px]" style="color:#64748B;">MCP（Model Context Protocol）</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">MCP：N×M → N+M 的适配地狱</h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-2 gap-5">
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[16px] font-semibold mb-2" style="color:#F87171;">MCP 出现前</div>
        <p class="text-[15px] leading-relaxed" style="color:#334155;">每个 Agent 自己接工具，N 个 Agent × M 个系统 = <span class="font-mono text-[18px]" style="color:#F87171;">N×M</span> 份适配代码。</p>
      </div>
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[16px] font-semibold mb-2" style="color:#059669;">用 MCP 后</div>
        <p class="text-[15px] leading-relaxed" style="color:#334155;">标准化 "LLM ↔ 工具" 协议，N 个 Agent + M 个系统 = <span class="font-mono text-[18px]" style="color:#059669;">N+M</span> 份实现。</p>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">三件套 · 类比 gRPC</div>
      <div class="grid grid-cols-3 gap-5">
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[16px] font-bold" style="color:#2563EB;">Server</div>
          <p class="text-[14.5px] mt-2 leading-relaxed" style="color:#334155;">暴露工具的服务端（stdio / SSE / WebSocket）</p>
        </div>
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[16px] font-bold" style="color:#0891B2;">Client</div>
          <p class="text-[14.5px] mt-2 leading-relaxed" style="color:#334155;">发现并调用工具的客户端（通常是 Agent）</p>
        </div>
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[16px] font-bold" style="color:#7C3AED;">Tool</div>
          <p class="text-[14.5px] mt-2 leading-relaxed" style="color:#334155;">被暴露的具体能力（docstring 自动转 description）</p>
        </div>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="rounded-lg px-6 py-4 flex items-center gap-4" style="background:#EFF6FF;border-left:3px solid #7C3AED;">
        <span class="text-[15px] font-semibold" style="color:#7C3AED;">与 LangChain Tool 的区别</span>
        <p class="text-[16px]" style="color:#1E293B;">MCP 是<span class="font-semibold" style="color:#0891B2;">跨进程 / 跨语言 / 可复用</span>的标准协议——一个 Server 可被多个 Agent 共享。</p>
      </div>
    </div>
  </div>
`);
