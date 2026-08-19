window.slideDataMap.set(6, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 1 章 · 代码</span>
        <span class="text-[13px]" style="color:#64748B;">examples/00_setup.py</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">8 行代码调通 LLM</h2>
    </div>

    <div class="px-16 mt-7 grid grid-cols-5 gap-6">
      <div class="col-span-3">
        <div class="rounded-lg overflow-hidden" style="background:#F8FAFC;border:1px solid #E2E8F0;">
          <div class="flex items-center justify-between px-5 py-2.5" style="background:#F1F5F9;border-bottom:1px solid #E2E8F0;">
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 rounded-full" style="background:#F87171;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#D97706;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#059669;"></span>
              <span class="ml-3 text-[13px] font-mono" style="color:#64748B;">00_setup.py</span>
            </div>
            <span class="text-[12px] font-mono" style="color:#0891B2;">Python · OpenAI 兼容</span>
          </div>
          <pre class="px-6 py-5 text-[15px] leading-[1.75]" style="font-family:Consolas,'Courier New',monospace;color:#1E293B;margin:0;"><span style="color:#64748B"> 1</span>  <span style="color:#7C3AED">from</span> <span style="color:#2563EB">openai</span> <span style="color:#7C3AED">import</span> <span style="color:#D97706">OpenAI</span>
<span style="color:#64748B"> 2</span>
<span style="color:#64748B"> 3</span>  <span style="color:#334155">client</span> = <span style="color:#D97706">OpenAI</span>(
<span style="color:#64748B"> 4</span>      <span style="color:#334155">api_key</span>=<span style="color:#2563EB">os</span>.<span style="color:#0891B2">getenv</span>(<span style="color:#15803D">"DEEPSEEK_API_KEY"</span>),
<span style="color:#64748B"> 5</span>      <span style="color:#334155">base_url</span>=<span style="color:#15803D">"https://api.deepseek.com"</span>,
<span style="color:#64748B"> 6</span>  )
<span style="color:#64748B"> 7</span>
<span style="color:#64748B"> 8</span>  <span style="color:#334155">response</span> = <span style="color:#334155">client</span>.<span style="color:#334155">chat</span>.<span style="color:#334155">completions</span>.<span style="color:#0891B2">create</span>(
<span style="color:#64748B"> 9</span>      <span style="color:#334155">model</span>=<span style="color:#15803D">"deepseek-chat"</span>,
<span style="color:#64748B">10</span>      <span style="color:#334155">messages</span>=[{<span style="color:#15803D">"role"</span>: <span style="color:#15803D">"user"</span>, <span style="color:#15803D">"content"</span>: <span style="color:#15803D">"你好"</span>}],
<span style="color:#64748B">11</span>  )
<span style="color:#64748B">12</span>  <span style="color:#2563EB">print</span>(<span style="color:#334155">response</span>.<span style="color:#334155">choices</span>[<span style="color:#C2410C">0</span>].<span style="color:#334155">message</span>.<span style="color:#334155">content</span>)</pre>
        </div>
      </div>

      <div class="col-span-2 space-y-4">
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#0891B2;">这是整个生态的 Hello World</div>
          <p class="text-[15px] leading-relaxed" style="color:#64748B;">相当于 Spring Boot 的启动类——先确认环境、跑通最简调用。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#2563EB;">Python 语法要点（Java 类比）</div>
          <ul class="space-y-2 text-[15px]" style="color:#334155;">
            <li><span class="font-mono text-[13px]" style="color:#D97706;">from X import Y</span> → import</li>
            <li><span class="font-mono text-[13px]" style="color:#D97706;">f"..."</span> → String.format</li>
            <li><span class="font-mono text-[13px]" style="color:#D97706;">chat.completions</span> → 链式属性</li>
          </ul>
        </div>
        <div class="rounded-lg p-5" style="background:#EFF6FF;border-left:3px solid #2563EB;">
          <p class="text-[15px] leading-relaxed" style="color:#334155;">💡 换通义千问 / 智谱：只改 <span class="font-mono" style="color:#15803D;">base_url</span> 即可，代码不变。</p>
        </div>
      </div>
    </div>
  </div>
`);
