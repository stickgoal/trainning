window.slideDataMap.set(22, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 10 章 · 阶段 C</span>
        <span class="text-[13px]" style="color:#64748B;">Agent Skill</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">Skill：把专家经验封装成能力</h2>
    </div>

    <div class="px-16 mt-7">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">三层递进 · Java 类比</div>
      <div class="grid grid-cols-3 gap-5">
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[16px] font-semibold" style="color:#2563EB;">Tool · 方法</div>
          <p class="text-[14.5px] mt-2 leading-relaxed" style="color:#334155;">一个函数，最小执行单元。</p>
          <div class="mt-3 text-[13px]" style="color:#64748B;">类比：一个方法</div>
        </div>
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[16px] font-semibold" style="color:#0891B2;">Prompt · 注释</div>
          <p class="text-[14.5px] mt-2 leading-relaxed" style="color:#334155;">一段文本指令，指导如何做。</p>
          <div class="mt-3 text-[13px]" style="color:#64748B;">类比：一段注释</div>
        </div>
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[16px] font-semibold" style="color:#7C3AED;">Skill · 能力</div>
          <p class="text-[14.5px] mt-2 leading-relaxed" style="color:#334155;">Prompt + Tool + 说明 + 触发条件。</p>
          <div class="mt-3 text-[13px]" style="color:#64748B;">类比：带文档的 Spring Bean</div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-7">
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold mb-3" style="color:#2563EB;">Skill 的组成</div>
        <div class="flex items-center justify-between text-center">
          <div class="flex-1"><div class="font-mono text-[15px] font-bold" style="color:#2563EB;">Prompt</div><div class="text-[13px] mt-1" style="color:#64748B;">做什么</div></div>
          <div class="text-[20px] px-1" style="color:#475569;">+</div>
          <div class="flex-1"><div class="font-mono text-[15px] font-bold" style="color:#0891B2;">Tool</div><div class="text-[13px] mt-1" style="color:#64748B;">用什么做</div></div>
          <div class="text-[20px] px-1" style="color:#475569;">+</div>
          <div class="flex-1"><div class="font-mono text-[15px] font-bold" style="color:#7C3AED;">说明</div><div class="text-[13px] mt-1" style="color:#64748B;">怎么用</div></div>
          <div class="text-[20px] px-1" style="color:#475569;">+</div>
          <div class="flex-1"><div class="font-mono text-[15px] font-bold" style="color:#059669;">触发条件</div><div class="text-[13px] mt-1" style="color:#64748B;">何时用</div></div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-7">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">典型 Skill 清单</div>
      <div class="grid grid-cols-4 gap-4">
        <div class="rounded-lg p-4 text-center" style="background:#F1F5F9;border:1px solid #E2E8F0;"><div class="text-[15px] font-semibold" style="color:#2563EB;">Coding</div></div>
        <div class="rounded-lg p-4 text-center" style="background:#F1F5F9;border:1px solid #E2E8F0;"><div class="text-[15px] font-semibold" style="color:#0891B2;">Database</div></div>
        <div class="rounded-lg p-4 text-center" style="background:#F1F5F9;border:1px solid #E2E8F0;"><div class="text-[15px] font-semibold" style="color:#7C3AED;">Testing</div></div>
        <div class="rounded-lg p-4 text-center" style="background:#F1F5F9;border:1px solid #E2E8F0;"><div class="text-[15px] font-semibold" style="color:#059669;">Deployment</div></div>
      </div>
    </div>
  </div>
`);
