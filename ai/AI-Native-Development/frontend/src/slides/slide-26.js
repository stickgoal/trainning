window.slideDataMap.set(26, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 13 章 · 阶段 D</span>
        <span class="text-[13px]" style="color:#64748B;">综合案例</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">两个端到端案例串起前 12 章</h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-2 gap-6">
      <div class="rounded-lg p-7" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="flex items-center gap-3 mb-3">
          <span class="px-2.5 py-1 rounded text-[13px] font-bold" style="background:#E2E8F0;color:#2563EB;">案例 1</span>
          <div class="text-[19px] font-semibold" style="color:#1E293B;">企业知识库 Agent</div>
        </div>
        <p class="text-[14px] mb-4 font-mono" style="color:#64748B;">Python + LangChain + RAG + Chroma + MCP</p>
        <ul class="space-y-2 text-[15px]" style="color:#334155;">
          <li>· 问制度 → 调 <span class="font-mono text-[13px]" style="color:#15803D;">search_policy</span></li>
          <li>· 问员工 → 调 <span class="font-mono text-[13px]" style="color:#15803D;">query_employee_db</span></li>
          <li>· 偏 <span class="font-semibold" style="color:#0891B2;">知识 + 检索</span> 场景</li>
        </ul>
      </div>

      <div class="rounded-lg p-7" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="flex items-center gap-3 mb-3">
          <span class="px-2.5 py-1 rounded text-[13px] font-bold" style="background:#E2E8F0;color:#7C3AED;">案例 2</span>
          <div class="text-[19px] font-semibold" style="color:#1E293B;">软件研发 Agent</div>
        </div>
        <p class="text-[14px] mb-4 font-mono" style="color:#64748B;">Python + LangGraph + Skill + Tool Calling + Harness</p>
        <ul class="space-y-2 text-[15px]" style="color:#334155;">
          <li>· 测试失败 → 回环 coding 节点</li>
          <li>· 带人工审批的研发流水线</li>
          <li>· 偏 <span class="font-semibold" style="color:#7C3AED;">流程 + 工具</span> 场景</li>
        </ul>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="rounded-lg p-6" style="background:#EFF6FF;border-left:3px solid #059669;">
        <div class="text-[15px] font-semibold mb-2" style="color:#059669;">选型原则</div>
        <p class="text-[16px] leading-relaxed" style="color:#334155;">
          <span class="font-semibold" style="color:#0891B2;">检索为主</span> → LangChain + RAG ｜
          <span class="font-semibold" style="color:#7C3AED;">流程复杂</span> → LangGraph。两个案例分别对应两类最典型的 Agent 需求。
        </p>
      </div>
    </div>
  </div>
`);
