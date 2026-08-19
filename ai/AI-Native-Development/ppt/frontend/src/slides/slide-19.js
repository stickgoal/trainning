window.slideDataMap.set(19, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 7 章 · 代码</span>
        <span class="text-[13px]" style="color:#64748B;">examples/06_mcp_server.py</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">@mcp.tool() 一行暴露工具</h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-5 gap-6">
      <div class="col-span-3">
        <div class="rounded-lg overflow-hidden" style="background:#F8FAFC;border:1px solid #E2E8F0;">
          <div class="flex items-center justify-between px-5 py-2.5" style="background:#F1F5F9;border-bottom:1px solid #E2E8F0;">
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 rounded-full" style="background:#F87171;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#D97706;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#059669;"></span>
              <span class="ml-3 text-[13px] font-mono" style="color:#64748B;">06_mcp_server.py</span>
            </div>
            <span class="text-[12px] font-mono" style="color:#0891B2;">FastMCP</span>
          </div>
          <pre class="px-6 py-5 text-[15px] leading-[1.65]" style="font-family:Consolas,'Courier New',monospace;color:#1E293B;margin:0;"><span style="color:#64748B"> 1</span>  <span style="color:#7C3AED">from</span> <span style="color:#2563EB">mcp.server.fastmcp</span> <span style="color:#7C3AED">import</span> <span style="color:#D97706">FastMCP</span>
<span style="color:#64748B"> 2</span>
<span style="color:#64748B"> 3</span>  <span style="color:#334155">mcp</span> = <span style="color:#D97706">FastMCP</span>(<span style="color:#15803D">"enterprise-db-tools"</span>)
<span style="color:#64748B"> 4</span>
<span style="color:#64748B"> 5</span>  <span style="color:#0891B2">@mcp.tool()</span>
<span style="color:#64748B"> 6</span>  <span style="color:#7C3AED">def</span> <span style="color:#0891B2">query_employee</span>(<span style="color:#334155">emp_id</span>: <span style="color:#2563EB">str</span>) -&gt; <span style="color:#2563EB">dict</span>:
<span style="color:#64748B"> 7</span>      <span style="color:#15803D">"""根据员工 ID 查询员工信息。"""</span>
<span style="color:#64748B"> 8</span>      <span style="color:#7C3AED">return</span> <span style="color:#334155">db</span>.<span style="color:#0891B2">get</span>(<span style="color:#334155">emp_id</span>, {<span style="color:#15803D">"error"</span>: <span style="color:#15803D">f"员工 {emp_id} 不存在"</span>})
<span style="color:#64748B"> 9</span>
<span style="color:#64748B">10</span>  <span style="color:#64748B"># Client 侧：发现 + 调用</span>
<span style="color:#64748B">11</span>  <span style="color:#334155">tools</span> = <span style="color:#7C3AED">await</span> <span style="color:#334155">session</span>.<span style="color:#0891B2">list_tools</span>()
<span style="color:#64748B">12</span>  <span style="color:#334155">result</span> = <span style="color:#7C3AED">await</span> <span style="color:#334155">session</span>.<span style="color:#0891B2">call_tool</span>(<span style="color:#15803D">"query_employee"</span>, {<span style="color:#15803D">"emp_id"</span>: <span style="color:#15803D">"E001"</span>})</pre>
        </div>
      </div>

      <div class="col-span-2 space-y-4">
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#0891B2;">装饰器即注册</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;"><span class="font-mono text-[13px]" style="color:#D97706;">@mcp.tool()</span> 把普通函数变成 Agent 可调工具（Java 注解 <span class="font-mono" style="color:#D97706;">@RpcMethod</span>）。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#2563EB;">docstring 工程</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">函数 docstring 自动转 Tool 描述、类型提示自动转 JSON Schema。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#7C3AED;">async / await</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">异步调用，类比 WebFlux 的响应式，第 8 章详解。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#EFF6FF;border-left:3px solid #7C3AED;">
          <p class="text-[15px]" style="color:#334155;">💡 一个 Agent 接公司 N 个内部系统，不必每个手写适配。</p>
        </div>
      </div>
    </div>
  </div>
`);
