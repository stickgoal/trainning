window.slideDataMap.set(14, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-12">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">第 5 章 · 阶段 B</span>
        <span class="text-[13px]" style="color:#64748B;">RAG 技术体系</span>
      </div>
      <h2 class="text-[38px] font-bold mt-3" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">RAG：让 LLM 用上私有知识</h2>
    </div>

    <div class="px-16 mt-6">
      <div class="text-[15px] font-semibold mb-3 tracking-wide" style="color:#64748B;">6 步管线</div>
      <div class="rounded-lg p-5" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="flex items-center justify-between text-center">
          <div class="flex-1"><div class="font-mono text-[14px] font-bold" style="color:#2563EB;">Document</div><div class="text-[12.5px] mt-1" style="color:#64748B;">加载文档</div></div>
          <div class="text-[20px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[14px] font-bold" style="color:#2563EB;">Chunk</div><div class="text-[12.5px] mt-1" style="color:#64748B;">切分片段</div></div>
          <div class="text-[20px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[14px] font-bold" style="color:#0891B2;">Embedding</div><div class="text-[12.5px] mt-1" style="color:#64748B;">文本转向量</div></div>
          <div class="text-[20px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[14px] font-bold" style="color:#0891B2;">VectorDB</div><div class="text-[12.5px] mt-1" style="color:#64748B;">向量入库</div></div>
          <div class="text-[20px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[14px] font-bold" style="color:#7C3AED;">Retriever</div><div class="text-[12.5px] mt-1" style="color:#64748B;">检索 Top-K</div></div>
          <div class="text-[20px] px-1" style="color:#475569;">→</div>
          <div class="flex-1"><div class="font-mono text-[14px] font-bold" style="color:#059669;">LLM</div><div class="text-[12.5px] mt-1" style="color:#64748B;">生成回答</div></div>
        </div>
      </div>
    </div>

    <div class="px-16 mt-6 grid grid-cols-3 gap-5">
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[16px] font-semibold mb-2" style="color:#0891B2;">多路召回</div>
        <p class="text-[14.5px] leading-relaxed" style="color:#334155;">向量 + BM25 双路，用 <span class="font-mono" style="color:#D97706;">RRF</span> 融合排序，互补召回。</p>
      </div>
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[16px] font-semibold mb-2" style="color:#7C3AED;">Rerank 精排</div>
        <p class="text-[14.5px] leading-relaxed" style="color:#334155;">召回 50 条 → 交叉编码器精排 Top-K，<span class="font-semibold" style="color:#7C3AED;">生产必加</span>。</p>
      </div>
      <div class="rounded-lg p-6" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[16px] font-semibold mb-2" style="color:#059669;">防幻觉</div>
        <p class="text-[14.5px] leading-relaxed" style="color:#334155;">Prompt 约束"没有答案就说未找到"，拒绝编造。</p>
      </div>
    </div>

    <div class="px-16 mt-6">
      <div class="rounded-lg px-6 py-4" style="background:#EFF6FF;border-left:3px solid #2563EB;">
        <p class="text-[15.5px] leading-relaxed" style="color:#334155;">
          <span class="font-semibold" style="color:#2563EB;">工具对照：</span>
          框架 LangChain RAG / LlamaIndex ｜ 向量库 Chroma / FAISS / Milvus / Qdrant / pgvector ｜ Embedding 用本地 <span class="font-mono" style="color:#15803D;">bge-small-zh</span>（国产 LLM 不提供 Embedding 接口）。
        </p>
      </div>
    </div>
  </div>
`);
