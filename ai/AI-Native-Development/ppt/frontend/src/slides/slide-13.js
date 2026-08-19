window.slideDataMap.set(13, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 4 章 · 代码</span>
        <span class="text-[13px]" style="color:#64748B;">examples/03_langgraph_demo.py</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">StateGraph + 条件边 + 人工介入</h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-5 gap-6">
      <div class="col-span-3">
        <div class="rounded-lg overflow-hidden" style="background:#F8FAFC;border:1px solid #E2E8F0;">
          <div class="flex items-center justify-between px-5 py-2.5" style="background:#F1F5F9;border-bottom:1px solid #E2E8F0;">
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 rounded-full" style="background:#F87171;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#D97706;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#059669;"></span>
              <span class="ml-3 text-[13px] font-mono" style="color:#64748B;">03_langgraph_demo.py</span>
            </div>
            <span class="text-[12px] font-mono" style="color:#0891B2;">StateGraph</span>
          </div>
          <pre class="px-6 py-5 text-[14.5px] leading-[1.65]" style="font-family:Consolas,'Courier New',monospace;color:#1E293B;margin:0;"><span style="color:#64748B"> 1</span>  <span style="color:#7C3AED">from</span> <span style="color:#2563EB">langgraph.graph</span> <span style="color:#7C3AED">import</span> <span style="color:#D97706">StateGraph</span>, <span style="color:#D97706">START</span>, <span style="color:#D97706">END</span>
<span style="color:#64748B"> 2</span>
<span style="color:#64748B"> 3</span>  <span style="color:#7C3AED">class</span> <span style="color:#D97706">AgentState</span>(<span style="color:#D97706">TypedDict</span>):
<span style="color:#64748B"> 4</span>      <span style="color:#334155">messages</span>: <span style="color:#D97706">Annotated</span>[<span style="color:#2563EB">list</span>, <span style="color:#2563EB">operator</span>.<span style="color:#334155">add</span>]  <span style="color:#64748B"># reducer：追加而非覆盖</span>
<span style="color:#64748B"> 5</span>      <span style="color:#334155">step</span>: <span style="color:#2563EB">int</span>
<span style="color:#64748B"> 6</span>
<span style="color:#64748B"> 7</span>  <span style="color:#334155">graph</span> = <span style="color:#D97706">StateGraph</span>(<span style="color:#D97706">AgentState</span>)
<span style="color:#64748B"> 8</span>  <span style="color:#334155">graph</span>.<span style="color:#0891B2">add_node</span>(<span style="color:#15803D">"agent"</span>, <span style="color:#334155">call_model</span>)
<span style="color:#64748B"> 9</span>  <span style="color:#334155">graph</span>.<span style="color:#0891B2">add_node</span>(<span style="color:#15803D">"tools"</span>, <span style="color:#334155">call_tool</span>)
<span style="color:#64748B">10</span>
<span style="color:#64748B">11</span>  <span style="color:#334155">graph</span>.<span style="color:#0891B2">add_edge</span>(<span style="color:#D97706">START</span>, <span style="color:#15803D">"agent"</span>)
<span style="color:#64748B">12</span>  <span style="color:#334155">graph</span>.<span style="color:#0891B2">add_conditional_edges</span>(<span style="color:#15803D">"agent"</span>, <span style="color:#334155">should_continue</span>, {
<span style="color:#64748B">13</span>      <span style="color:#15803D">"tools"</span>: <span style="color:#15803D">"tools"</span>, <span style="color:#15803D">"human"</span>: <span style="color:#15803D">"human"</span>, <span style="color:#15803D">"__end__"</span>: <span style="color:#D97706">END</span>,
<span style="color:#64748B">14</span>  })
<span style="color:#64748B">15</span>  <span style="color:#334155">graph</span>.<span style="color:#0891B2">add_edge</span>(<span style="color:#15803D">"tools"</span>, <span style="color:#15803D">"agent"</span>)   <span style="color:#64748B"># 回环 → 循环</span>
<span style="color:#64748B">16</span>
<span style="color:#64748B">17</span>  <span style="color:#334155">app</span> = <span style="color:#334155">graph</span>.<span style="color:#0891B2">compile</span>(<span style="color:#334155">interrupt_before</span>=[<span style="color:#15803D">"human"</span>])</pre>
        </div>
      </div>

      <div class="col-span-2 space-y-4">
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#2563EB;">State 显式化</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;"><span class="font-mono text-[13px]" style="color:#D97706;">Annotated[list, operator.add]</span> 定义 reducer：消息<span class="font-semibold" style="color:#0891B2;">累加</span>而非覆盖。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#0891B2;">条件边</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;"><span class="font-mono text-[13px]" style="color:#D97706;">should_continue</span> 返回下一节点名——类比状态机 guard。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#7C3AED;">tools → agent 回环</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">工具结果回灌再调 LLM，形成<span class="font-semibold" style="color:#7C3AED;">循环</span>——Chain 做不到的。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#EFF6FF;border-left:3px solid #059669;">
          <p class="text-[15px]" style="color:#334155;">💡 <span class="font-mono" style="color:#D97706;">interrupt_before</span> 让人工审批成为流程的一部分。</p>
        </div>
      </div>
    </div>
  </div>
`);
