window.slideDataMap.set(10, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 3 章 · 阶段 B</span>
        <span class="text-[13px]" style="color:#64748B;">LangChain</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">LangChain：把胶水代码组件化</h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-2 gap-5">
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold mb-2" style="color:#F87171;">不用 LangChain</div>
        <p class="text-[15px] leading-relaxed" style="color:#334155;">每个项目重写 Prompt / 检索 / 记忆的胶水代码，不可复用、难以维护。</p>
      </div>
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold mb-2" style="color:#059669;">用 LangChain</div>
        <p class="text-[15px] leading-relaxed" style="color:#334155;">把组件标准化、可复用、可组合，像搭积木一样拼 LLM 应用。</p>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">9 大组件 · 按职责分类</div>
      <div class="grid grid-cols-3 gap-4">
        <div class="rounded p-3.5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold" style="color:#2563EB;">Chat Model</div>
          <div class="text-[12.5px] mt-0.5" style="color:#64748B;">统一 LLM 调用接口</div>
        </div>
        <div class="rounded p-3.5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold" style="color:#2563EB;">Prompt Template</div>
          <div class="text-[12.5px] mt-0.5" style="color:#64748B;">变量插值复用提示词</div>
        </div>
        <div class="rounded p-3.5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold" style="color:#2563EB;">Runnable / Chain</div>
          <div class="text-[12.5px] mt-0.5" style="color:#64748B;">统一抽象与流水线</div>
        </div>
        <div class="rounded p-3.5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold" style="color:#0891B2;">Retriever</div>
          <div class="text-[12.5px] mt-0.5" style="color:#64748B;">检索器接口</div>
        </div>
        <div class="rounded p-3.5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold" style="color:#0891B2;">Vector Store</div>
          <div class="text-[12.5px] mt-0.5" style="color:#64748B;">向量存储</div>
        </div>
        <div class="rounded p-3.5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold" style="color:#0891B2;">Memory</div>
          <div class="text-[12.5px] mt-0.5" style="color:#64748B;">跨轮对话状态</div>
        </div>
        <div class="rounded p-3.5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold" style="color:#7C3AED;">Tool</div>
          <div class="text-[12.5px] mt-0.5" style="color:#64748B;">函数 → 工具</div>
        </div>
        <div class="rounded p-3.5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold" style="color:#7C3AED;">Agent</div>
          <div class="text-[12.5px] mt-0.5" style="color:#64748B;">LLM + Tool 决策循环</div>
        </div>
        <div class="rounded p-3.5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold" style="color:#7C3AED;">Output Parser</div>
          <div class="text-[12.5px] mt-0.5" style="color:#64748B;">结构化输出</div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="rounded-lg px-6 py-4 flex items-center gap-4" style="background:#EFF6FF;border-left:3px solid #0891B2;">
        <span class="text-[15px] font-semibold" style="color:#0891B2;">LCEL 精髓</span>
        <p class="text-[16px]" style="color:#1E293B;">所有组件都是 <span class="font-mono" style="color:#D97706;">Runnable</span>，用 <span class="font-mono text-[18px]" style="color:#0891B2;">|</span> 串联成流水线 —— 类比 Java <span class="font-mono" style="color:#15803D;">Stream.map().filter().collect()</span></p>
      </div>
    </div>
  </div>
`);
