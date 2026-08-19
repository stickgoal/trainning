window.slideDataMap.set(8, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 2 章 · 代码</span>
        <span class="text-[13px]" style="color:#64748B;">examples/01_llm_basics.py</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">Tool Calling 四步闭环</h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-5 gap-6">
      <div class="col-span-3">
        <div class="rounded-lg overflow-hidden" style="background:#F8FAFC;border:1px solid #E2E8F0;">
          <div class="flex items-center justify-between px-5 py-2.5" style="background:#F1F5F9;border-bottom:1px solid #E2E8F0;">
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 rounded-full" style="background:#F87171;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#D97706;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#059669;"></span>
              <span class="ml-3 text-[13px] font-mono" style="color:#64748B;">01_llm_basics.py</span>
            </div>
            <span class="text-[12px] font-mono" style="color:#0891B2;">Tool Calling</span>
          </div>
          <pre class="px-6 py-5 text-[15px] leading-[1.7]" style="font-family:Consolas,'Courier New',monospace;color:#1E293B;margin:0;"><span style="color:#64748B"> 1</span>  <span style="color:#64748B"># 1. 把用户问题 + 工具 schema 一起发给 LLM</span>
<span style="color:#64748B"> 2</span>  <span style="color:#334155">resp</span> = <span style="color:#334155">client</span>.<span style="color:#334155">chat</span>.<span style="color:#334155">completions</span>.<span style="color:#0891B2">create</span>(
<span style="color:#64748B"> 3</span>      <span style="color:#334155">model</span>=<span style="color:#15803D">"deepseek-chat"</span>,
<span style="color:#64748B"> 4</span>      <span style="color:#334155">messages</span>=[{<span style="color:#15803D">"role"</span>: <span style="color:#15803D">"user"</span>, <span style="color:#15803D">"content"</span>: <span style="color:#15803D">"北京天气？"</span>}],
<span style="color:#64748B"> 5</span>      <span style="color:#334155">tools</span>=[<span style="color:#334155">WEATHER_TOOL_SCHEMA</span>],
<span style="color:#64748B"> 6</span>  )
<span style="color:#64748B"> 7</span>
<span style="color:#64748B"> 8</span>  <span style="color:#64748B"># 2. LLM 返回的不是答案，而是 tool_call 指令</span>
<span style="color:#64748B"> 9</span>  <span style="color:#334155">call</span> = <span style="color:#334155">resp</span>.<span style="color:#334155">choices</span>[<span style="color:#C2410C">0</span>].<span style="color:#334155">message</span>.<span style="color:#334155">tool_calls</span>[<span style="color:#C2410C">0</span>]
<span style="color:#64748B">10</span>  <span style="color:#334155">args</span> = <span style="color:#2563EB">json</span>.<span style="color:#0891B2">loads</span>(<span style="color:#334155">call</span>.<span style="color:#334155">function</span>.<span style="color:#334155">arguments</span>)
<span style="color:#64748B">11</span>
<span style="color:#64748B">12</span>  <span style="color:#64748B"># 3. 应用层真正执行工具</span>
<span style="color:#64748B">13</span>  <span style="color:#334155">result</span> = <span style="color:#0891B2">get_weather</span>(**<span style="color:#334155">args</span>)
<span style="color:#64748B">14</span>
<span style="color:#64748B">15</span>  <span style="color:#64748B"># 4. 回灌结果，让 LLM 生成最终自然语言回答</span>
<span style="color:#64748B">16</span>  <span style="color:#334155">final</span> = <span style="color:#334155">client</span>.<span style="color:#334155">chat</span>.<span style="color:#334155">completions</span>.<span style="color:#0891B2">create</span>(
<span style="color:#64748B">17</span>      <span style="color:#334155">messages</span>=[<span style="color:#15803D">"user..."</span>, <span style="color:#334155">msg</span>, {<span style="color:#15803D">"role"</span>: <span style="color:#15803D">"tool"</span>, <span style="color:#15803D">"content"</span>: <span style="color:#334155">result</span>}],
<span style="color:#64748B">18</span>  )</pre>
        </div>
      </div>

      <div class="col-span-2 space-y-4">
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[13px] font-bold" style="color:#2563EB;">STEP 1</div>
          <div class="text-[15px] mt-1" style="color:#334155;">发工具 Schema 给 LLM</div>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[13px] font-bold" style="color:#0891B2;">STEP 2</div>
          <div class="text-[15px] mt-1" style="color:#334155;">LLM 返回 tool_call 指令</div>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[13px] font-bold" style="color:#7C3AED;">STEP 3</div>
          <div class="text-[15px] mt-1" style="color:#334155;">应用层真正执行工具</div>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="font-mono text-[13px] font-bold" style="color:#059669;">STEP 4</div>
          <div class="text-[15px] mt-1" style="color:#334155;">回灌结果，生成回答</div>
        </div>
        <div class="rounded-lg p-5" style="background:#EFF6FF;border-left:3px solid #2563EB;">
          <p class="text-[15px] leading-relaxed" style="color:#334155;">💡 <span class="font-mono" style="color:#15803D;">**args</span> 把 dict 展开成关键字参数——Java 里没有的语法糖。</p>
        </div>
      </div>
    </div>
  </div>
`);
