window.slideDataMap.set(15, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 5 章 · 代码</span>
        <span class="text-[13px]" style="color:#64748B;">examples/04_rag_demo.py</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">RAG 六步管线</h2>
    </div>

    <div class="px-16 mt-6 grid grid-cols-5 gap-6">
      <div class="col-span-3">
        <div class="rounded-lg overflow-hidden" style="background:#F8FAFC;border:1px solid #E2E8F0;">
          <div class="flex items-center justify-between px-5 py-2.5" style="background:#F1F5F9;border-bottom:1px solid #E2E8F0;">
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 rounded-full" style="background:#F87171;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#D97706;"></span>
              <span class="w-3 h-3 rounded-full" style="background:#059669;"></span>
              <span class="ml-3 text-[13px] font-mono" style="color:#64748B;">04_rag_demo.py</span>
            </div>
            <span class="text-[12px] font-mono" style="color:#0891B2;">RAG Pipeline</span>
          </div>
          <pre class="px-6 py-5 text-[14.5px] leading-[1.6]" style="font-family:Consolas,'Courier New',monospace;color:#1E293B;margin:0;"><span style="color:#64748B"> 1</span>  <span style="color:#64748B"># Document → Chunk → Embedding → VectorDB → Retriever → LLM</span>
<span style="color:#64748B"> 2</span>  <span style="color:#334155">loader</span> = <span style="color:#D97706">TextLoader</span>(<span style="color:#15803D">"knowledge.txt"</span>)
<span style="color:#64748B"> 3</span>  <span style="color:#334155">docs</span> = <span style="color:#334155">loader</span>.<span style="color:#0891B2">load</span>()
<span style="color:#64748B"> 4</span>
<span style="color:#64748B"> 5</span>  <span style="color:#334155">splitter</span> = <span style="color:#D97706">RecursiveCharacterTextSplitter</span>(
<span style="color:#64748B"> 6</span>      <span style="color:#334155">chunk_size</span>=<span style="color:#C2410C">200</span>, <span style="color:#334155">chunk_overlap</span>=<span style="color:#C2410C">30</span>)
<span style="color:#64748B"> 7</span>  <span style="color:#334155">chunks</span> = <span style="color:#334155">splitter</span>.<span style="color:#0891B2">split_documents</span>(<span style="color:#334155">docs</span>)
<span style="color:#64748B"> 8</span>
<span style="color:#64748B"> 9</span>  <span style="color:#334155">embeddings</span> = <span style="color:#D97706">SentenceTransformerEmbeddings</span>(
<span style="color:#64748B">10</span>      <span style="color:#334155">model_name</span>=<span style="color:#15803D">"BAAI/bge-small-zh-v1.5"</span>)
<span style="color:#64748B">11</span>  <span style="color:#334155">vector_db</span> = <span style="color:#D97706">Chroma</span>.<span style="color:#0891B2">from_documents</span>(<span style="color:#334155">chunks</span>, <span style="color:#334155">embeddings</span>)
<span style="color:#64748B">12</span>
<span style="color:#64748B">13</span>  <span style="color:#334155">retriever</span> = <span style="color:#334155">vector_db</span>.<span style="color:#0891B2">as_retriever</span>(<span style="color:#334155">search_kwargs</span>={<span style="color:#15803D">"k"</span>: <span style="color:#C2410C">3</span>})
<span style="color:#64748B">14</span>  <span style="color:#334155">chain</span> = <span style="color:#334155">prompt</span> | <span style="color:#334155">model</span> | <span style="color:#D97706">StrOutputParser</span>()</pre>
        </div>
      </div>

      <div class="col-span-2 space-y-4">
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#0891B2;">chunk_size 的权衡</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">太大丢精度、太小断语义；<span class="font-mono text-[13px]" style="color:#D97706;">chunk_overlap</span> 保证语义连续。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#2563EB;">本地 Embedding</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">国产 LLM 不提供 Embedding，故用本地 <span class="font-mono text-[13px]" style="color:#15803D;">bge-small-zh</span>，无需厂商 Key。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
          <div class="text-[15px] font-semibold mb-2" style="color:#7C3AED;">防幻觉</div>
          <p class="text-[15px] leading-relaxed" style="color:#334155;">第四问"食堂几点开饭"→ 返回<span class="font-semibold" style="color:#059669;">"知识库中未找到"</span>，不编造。</p>
        </div>
        <div class="rounded-lg p-5" style="background:#EFF6FF;border-left:3px solid #2563EB;">
          <p class="text-[15px]" style="color:#334155;">💡 进阶：<span class="font-mono" style="color:#D97706;">BM25Retriever + EnsembleRetriever(RRF)</span> 多路召回，再加 Rerank 精排。</p>
        </div>
      </div>
    </div>
  </div>
`);
