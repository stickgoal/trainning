window.slideDataMap.set(25, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 12 章 · 阶段 D</span>
        <span class="text-[13px]" style="color:#64748B;">企业级 Agent 架构</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">企业级 Agent 六层架构</h2>
    </div>

    <div class="px-16 mt-6">
      <div class="rounded-lg overflow-hidden" style="border:1px solid #E2E8F0;">
        <div class="grid grid-cols-[1.4fr_2.6fr] px-6 py-3 text-[14px] font-semibold" style="background:#EFF6FF;color:#2563EB;">
          <div>层级</div><div>职责</div>
        </div>
        <div class="grid grid-cols-[1.4fr_2.6fr] px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#64748B;">用户 / 业务系统</div><div>入口，触发 Agent 任务</div>
        </div>
        <div class="grid grid-cols-[1.4fr_2.6fr] px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#2563EB;">Agent Application</div><div>编排、状态、循环、人工介入（LangGraph / CrewAI）</div>
        </div>
        <div class="grid grid-cols-[1.4fr_2.6fr] px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#0891B2;">LLM / Skill / MCP / A2A</div><div>推理 + 能力 + 工具协议 + 多 Agent 通信</div>
        </div>
        <div class="grid grid-cols-[1.4fr_2.6fr] px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#7C3AED;">Harness</div><div>运行时 / 记忆 / 权限 / 评估 / 监控</div>
        </div>
        <div class="grid grid-cols-[1.4fr_2.6fr] px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#059669;">Knowledge RAG / 企业系统</div><div>私有知识检索 + 对接既有业务系统</div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-7">
      <div class="rounded-lg px-7 py-5" style="background:#EFF6FF;border-left:3px solid #2563EB;">
        <p class="text-[20px] font-bold" style="color:#0F172A;">"Agent 应用 <span style="color:#F87171;">≠</span> 一个 LLM 调用"</p>
        <p class="text-[15px] mt-2" style="color:#64748B;">它是 LLM + 工具 + 知识 + 记忆 + 治理层的系统工程，能向架构评审会讲清每一层为什么存在。</p>
      </div>
    </div>
  </div>
`);
