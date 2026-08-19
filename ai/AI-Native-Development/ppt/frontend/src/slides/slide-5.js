window.slideDataMap.set(5, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 1 章 · 阶段 A</span>
        <span class="text-[13px]" style="color:#64748B;">Python AI 生态概览</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">为什么 AI 项目都用 Python</h2>
    </div>

    <div class="px-16 mt-7 grid grid-cols-4 gap-5">
      <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[26px] font-bold" style="color:#2563EB;font-family:'Montserrat',sans-serif;">01</div>
        <div class="text-[17px] font-semibold mt-2" style="color:#1E293B;">SDK 优先</div>
        <div class="text-[14px] mt-1.5 leading-relaxed" style="color:#64748B;">DeepSeek / 通义千问一行调用，厂商 SDK 首发 Python</div>
      </div>
      <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[26px] font-bold" style="color:#0891B2;font-family:'Montserrat',sans-serif;">02</div>
        <div class="text-[17px] font-semibold mt-2" style="color:#1E293B;">快速实验</div>
        <div class="text-[14px] mt-1.5 leading-relaxed" style="color:#64748B;">REPL / Notebook 即时反馈，迭代节奏快</div>
      </div>
      <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[26px] font-bold" style="color:#7C3AED;font-family:'Montserrat',sans-serif;">03</div>
        <div class="text-[17px] font-semibold mt-2" style="color:#1E293B;">数据生态</div>
        <div class="text-[14px] mt-1.5 leading-relaxed" style="color:#64748B;">NumPy / Pandas / PyTorch 一整套数据处理栈</div>
      </div>
      <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[26px] font-bold" style="color:#059669;font-family:'Montserrat',sans-serif;">04</div>
        <div class="text-[17px] font-semibold mt-2" style="color:#1E293B;">工具链成熟</div>
        <div class="text-[14px] mt-1.5 leading-relaxed" style="color:#64748B;">LangChain / LlamaIndex 等 LLM 生态已成型</div>
      </div>
    </div>

    <div class="px-16 mt-7">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">五层技术栈 · Java 类比</div>
      <div class="rounded-lg overflow-hidden" style="border:1px solid #E2E8F0;">
        <div class="grid grid-cols-3 px-6 py-3 text-[14px] font-semibold" style="background:#EFF6FF;color:#2563EB;">
          <div>技术层</div><div>Python 组件</div><div>Java 类比</div>
        </div>
        <div class="grid grid-cols-3 px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#64748B;">Web 框架</div><div>FastAPI</div><div>Spring Boot</div>
        </div>
        <div class="grid grid-cols-3 px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#64748B;">编排框架</div><div>LangChain / LangGraph</div><div>Spring Integration / Camunda</div>
        </div>
        <div class="grid grid-cols-3 px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#64748B;">LLM SDK</div><div>DeepSeek（OpenAI 兼容）</div><div>HttpClient</div>
        </div>
        <div class="grid grid-cols-3 px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#64748B;">向量检索</div><div>Milvus / Chroma / FAISS</div><div>Elasticsearch（向量版）</div>
        </div>
        <div class="grid grid-cols-3 px-6 py-3 text-[15px]" style="background:#F1F5F9;color:#334155;border-top:1px solid #E2E8F0;">
          <div style="color:#64748B;">工具协议 / 运行时</div><div>MCP / Harness</div><div>gRPC / Spring 容器 + AOP</div>
        </div>
      </div>
    </div>
  </div>
`);
