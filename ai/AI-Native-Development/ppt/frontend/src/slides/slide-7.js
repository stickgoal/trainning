window.slideDataMap.set(7, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 2 章 · 阶段 A</span>
        <span class="text-[13px]" style="color:#64748B;">LLM Application 开发基础</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">8 个核心术语 · 一页讲完</h2>
    </div>

    <div class="px-16 mt-7 grid grid-cols-4 gap-4">
      <div class="rounded-lg p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold" style="color:#2563EB;">Prompt</div>
        <div class="text-[13px] mt-1" style="color:#64748B;">给 LLM 的输入文本</div>
      </div>
      <div class="rounded-lg p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold" style="color:#2563EB;">Token</div>
        <div class="text-[13px] mt-1" style="color:#64748B;">文本处理单元，非字符非单词</div>
      </div>
      <div class="rounded-lg p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold" style="color:#2563EB;">Context</div>
        <div class="text-[13px] mt-1" style="color:#64748B;">单次请求的有效上下文窗口</div>
      </div>
      <div class="rounded-lg p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold" style="color:#2563EB;">Embedding</div>
        <div class="text-[13px] mt-1" style="color:#64748B;">文本转向量，语义可计算</div>
      </div>
      <div class="rounded-lg p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold" style="color:#0891B2;">Retrieval</div>
        <div class="text-[13px] mt-1" style="color:#64748B;">从知识库召回相关片段</div>
      </div>
      <div class="rounded-lg p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold" style="color:#0891B2;">Tool Calling</div>
        <div class="text-[13px] mt-1" style="color:#64748B;">LLM 决定调用哪个函数</div>
      </div>
      <div class="rounded-lg p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold" style="color:#7C3AED;">Memory</div>
        <div class="text-[13px] mt-1" style="color:#64748B;">跨轮对话状态（历史消息）</div>
      </div>
      <div class="rounded-lg p-4" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[15px] font-semibold" style="color:#7C3AED;">Agent</div>
        <div class="text-[13px] mt-1" style="color:#64748B;">LLM + 工具 + 决策循环</div>
      </div>
    </div>

    <div class="px-16 mt-8">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">Agent 的内核 · Tool Calling 四步闭环</div>
      <div class="rounded-lg p-6" style="background:#EFF6FF;border:1px solid #E2E8F0;">
        <div class="flex items-center justify-between text-center">
          <div class="flex-1">
            <div class="font-mono text-[13px] font-bold" style="color:#2563EB;">STEP 1</div>
            <div class="text-[15px] mt-1.5" style="color:#1E293B;">发工具 Schema 给 LLM</div>
          </div>
          <div class="text-[22px] px-2" style="color:#475569;">→</div>
          <div class="flex-1">
            <div class="font-mono text-[13px] font-bold" style="color:#0891B2;">STEP 2</div>
            <div class="text-[15px] mt-1.5" style="color:#1E293B;">LLM 返回 tool_call</div>
          </div>
          <div class="text-[22px] px-2" style="color:#475569;">→</div>
          <div class="flex-1">
            <div class="font-mono text-[13px] font-bold" style="color:#7C3AED;">STEP 3</div>
            <div class="text-[15px] mt-1.5" style="color:#1E293B;">应用层真正执行</div>
          </div>
          <div class="text-[22px] px-2" style="color:#475569;">→</div>
          <div class="flex-1">
            <div class="font-mono text-[13px] font-bold" style="color:#059669;">STEP 4</div>
            <div class="text-[15px] mt-1.5" style="color:#1E293B;">回灌结果生成回答</div>
          </div>
        </div>
        <p class="text-[14px] mt-4 text-center" style="color:#64748B;">关键：LLM 不执行工具，它只"决定"调用——执行权在应用层。</p>
      </div>
    </div>
  </div>
`);
