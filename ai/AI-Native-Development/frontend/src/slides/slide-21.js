window.slideDataMap.set(21, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 9 章 · 阶段 C · 上生产关键</span>
        <span class="text-[13px]" style="color:#64748B;">Harness Engineering</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">Harness：让 Agent 稳定上生产</h2>
    </div>

    <div class="px-16 mt-6">
      <div class="rounded-lg px-7 py-5" style="background:#EFF6FF;border-left:3px solid #2563EB;">
        <p class="text-[18px]" style="color:#1E293B;"><span class="font-mono font-bold" style="color:#2563EB;">Agent = LLM + Harness</span>　<span class="text-[16px]" style="color:#64748B;">没有 Harness 的 Agent，只是 demo。</span></p>
      </div>
    </div>

    <div class="px-16 mt-7">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">7 个子能力</div>
      <div class="grid grid-cols-4 gap-4">
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[15px] font-bold" style="color:#2563EB;">Context</div>
          <p class="text-[13px] mt-1.5" style="color:#64748B;">上下文窗口管理</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[15px] font-bold" style="color:#2563EB;">Memory</div>
          <p class="text-[13px] mt-1.5" style="color:#64748B;">长期记忆存储</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[15px] font-bold" style="color:#0891B2;">Tool Runtime</div>
          <p class="text-[13px] mt-1.5" style="color:#64748B;">工具沙箱执行</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[15px] font-bold" style="color:#0891B2;">Permission</div>
          <p class="text-[13px] mt-1.5" style="color:#64748B;">权限与审批护栏</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[15px] font-bold" style="color:#7C3AED;">Eval</div>
          <p class="text-[13px] mt-1.5" style="color:#64748B;">评估回归</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[15px] font-bold" style="color:#7C3AED;">Monitor</div>
          <p class="text-[13px] mt-1.5" style="color:#64748B;">可观测与告警</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[15px] font-bold" style="color:#059669;">Feedback</div>
          <p class="text-[13px] mt-1.5" style="color:#64748B;">反馈闭环优化</p>
        </div>
        <div class="rounded-lg p-5 flex items-center justify-center" style="background:#EFF6FF;border:1px dashed #E2E8F0;">
          <div class="text-center">
            <div class="text-[14px] font-semibold" style="color:#64748B;">真实案例</div>
            <div class="text-[14px] mt-1" style="color:#2563EB;">Claude Code / Codex</div>
          </div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-7">
      <div class="rounded-lg px-6 py-4" style="background:#EFF6FF;border-left:3px solid #7C3AED;">
        <p class="text-[15.5px] leading-relaxed" style="color:#334155;">Claude Code 的 Harness 形态 = <span class="font-semibold" style="color:#0891B2;">CLI + 工具沙箱 + 上下文管理</span>——这也是 Codex 等 Coding Agent 背后的工程底座。</p>
      </div>
    </div>
  </div>
`);
