window.slideDataMap.set(20, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 8 章 · 阶段 C</span>
        <span class="text-[13px]" style="color:#64748B;">A2A（Agent To Agent）</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">A2A：多 Agent 怎么协作</h2>
    </div>

    <div class="px-16 mt-6">
      <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold mb-3" style="color:#F87171;">为什么单 Agent 不够</div>
        <div class="grid grid-cols-3 gap-4">
          <div class="text-[15px]" style="color:#334155;">· 任务太复杂，单点难解</div>
          <div class="text-[15px]" style="color:#334155;">· 角色太多，职责难合一</div>
          <div class="text-[15px]" style="color:#334155;">· 上下文太长，超出窗口</div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">要解决的三大问题</div>
      <div class="grid grid-cols-3 gap-5">
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[16px] font-bold" style="color:#2563EB;">Communication</div>
          <div class="text-[13px] mt-0.5" style="color:#64748B;">通信</div>
          <p class="text-[14.5px] mt-2 leading-relaxed" style="color:#334155;">Agent 之间怎么可靠地收发消息。</p>
        </div>
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[16px] font-bold" style="color:#0891B2;">Discovery</div>
          <div class="text-[13px] mt-0.5" style="color:#64748B;">发现</div>
          <p class="text-[14.5px] mt-2 leading-relaxed" style="color:#334155;">怎么找到"谁能干什么"，动态注册能力。</p>
        </div>
        <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[16px] font-bold" style="color:#7C3AED;">Task Delegation</div>
          <div class="text-[13px] mt-0.5" style="color:#64748B;">任务委派</div>
          <p class="text-[14.5px] mt-2 leading-relaxed" style="color:#334155;">任务怎么拆分、派给谁、如何汇总。</p>
        </div>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="rounded-lg px-6 py-5" style="background:#EFF6FF;border-left:3px solid #0891B2;">
        <p class="text-[16px] leading-relaxed" style="color:#334155;">
          <span class="font-semibold" style="color:#0891B2;">典型流程：</span>Research → Coding → Testing 各司其职 ｜
          <span class="font-semibold" style="color:#0891B2;">一句话：</span>多 Agent 是"微服务化"在 Agent 层的重演，<span class="font-semibold" style="color:#D97706;">3-4 个为宜，别为了拆而拆</span>。
        </p>
      </div>
    </div>
  </div>
`);
