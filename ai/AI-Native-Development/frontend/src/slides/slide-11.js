window.slideDataMap.set(11, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 3 章 · 代码</span>
        <span class="text-[13px]" style="color:#64748B;">examples/02_langchain_demo.py</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">LCEL 管道符 <span style="color:#0891B2;">|</span></h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-5 gap-6">
      <div class="col-span-3">
        <div class="rounded-lg overflow-hidden" style="background:#F8FAFC;border:1px solid #E2E8F0;">
          <div class="flex items-center justify-between px-5 py-2.5" style="background:#F1F5F9;border-bottom:1px solid #E2E8F0;">
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 rounded-full" style="background:#F87171;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#D97706;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#059669;"></span>
              <span class="ml-3 text-[13px] font-mono" style="color:#64748B;">02_langchain_demo.py</span>
            </div>
            <span class="text-[12px] font-mono" style="color:#0891B2;">LCEL</span>
          </div>
          <pre class="px-6 py-5 text-[15px] leading-[1.7]" style="font-family:Consolas,'Courier New',monospace;color:#1E293B;margin:0;"><span style="color:#64748B"> 1</span>  <span style="color:#7C3AED">from</span> <span style="color:#2563EB">langchain_deepseek</span> <span style="color:#7C3AED">import</span> <span style="color:#D97706">ChatDeepSeek</span>
<span style="color:#64748B"> 2</span>  <span style="color:#7C3AED">from</span> <span style="color:#2563EB">langchain_core.prompts</span> <span style="color:#7C3AED">import</span> <span style="color:#D97706">ChatPromptTemplate</span>
<span style="color:#64748B"> 3</span>  <span style="color:#7C3AED">from</span> <span style="color:#2563EB">langchain_core.output_parsers</span> <span style="color:#7C3AED">import</span> <span style="color:#D97706">StrOutputParser</span>
<span style="color:#64748B"> 4</span>
<span style="color:#64748B"> 5</span>  <span style="color:#334155">prompt</span> = <span style="color:#D97706">ChatPromptTemplate</span>.<span style="color:#0891B2">from_template</span>(<span style="color:#15803D">"用一句话解释 {concept}。"</span>)
<span style="color:#64748B"> 6</span>  <span style="color:#334155">model</span> = <span style="color:#D97706">ChatDeepSeek</span>(<span style="color:#334155">model</span>=<span style="color:#15803D">"deepseek-chat"</span>)
<span style="color:#64748B"> 7</span>
<span style="color:#64748B"> 8</span>  <span style="color:#64748B"># ★ LCEL：用 | 把 prompt → model → parser 串成一条链</span>
<span style="color:#64748B"> 9</span>  <span style="color:#334155">chain</span> = <span style="color:#334155">prompt</span> | <span style="color:#334155">model</span> | <span style="color:#D97706">StrOutputParser</span>()
<span style="color:#64748B">10</span>
<span style="color:#64748B">11</span>  <span style="color:#64748B"># 数据流：{concept} → 填模板 → 发 LLM → 解析成纯字符串</span>
<span style="color:#64748B">12</span>  <span style="color:#334155">result</span> = <span style="color:#334155">chain</span>.<span style="color:#0891B2">invoke</span>({<span style="color:#15803D">"concept"</span>: <span style="color:#15803D">"RAG"</span>})
<span style="color:#64748B">13</span>  <span style="color:#2563EB">print</span>(<span style="color:#334155">result</span>)</pre>
        </div>
      </div>

      <div class="col-span-2 space-y-4">
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#0891B2;">| 是什么？</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">Python 的 <span class="font-mono" style="color:#D97706;">|</span> 被 LangChain 重载为 Runnable 串联（运算符重载）。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#2563EB;">统一入口</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">所有组件共享 <span class="font-mono" style="color:#D97706;">invoke / ainvoke / batch / stream</span> 四种调用方式。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#7C3AED;">换模型只改一处</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">OpenAI / Anthropic / 通义 / DeepSeek 都实现同一抽象。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#EFF6FF;border-left:3px solid #2563EB;">
          <p class="text-[15px]" style="color:#334155;">💡 80% 的"LLM + 提示词 + 检索"场景，默认起点就是 LangChain。</p>
        </div>
      </div>
    </div>
  </div>
`);
