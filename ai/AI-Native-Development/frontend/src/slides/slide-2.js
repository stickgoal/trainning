window.slideDataMap.set(2, `
  <div class="w-[1440px] h-[810px] relative overflow-hidden bg-[#FFFFFF]" style="font-family:'Noto Sans SC',sans-serif;">
    <div class="px-16 pt-14">
      <div class="flex items-center gap-3">
        <span class="px-3 py-1 rounded text-[13px] font-semibold" style="background:#E2E8F0;color:#2563EB;">课程定位</span>
      </div>
      <h2 class="text-[40px] font-bold mt-4" style="color:#0F172A;font-family:'Montserrat','Noto Sans SC',sans-serif;">这门课是什么 / 不是什么</h2>
    </div>

    <div class="px-16 mt-10 grid grid-cols-3 gap-6">
      <div class="rounded-lg p-7" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[14px] font-semibold tracking-wide mb-4" style="color:#0891B2;">目标听众</div>
        <ul class="space-y-3 text-[16px] leading-relaxed" style="color:#334155;">
          <li>· 3 年以上 Java 后端经验</li>
          <li>· 能读 Python，不熟 asyncio / decorator</li>
          <li>· 用过 ChatGPT，没搭过 LLM 应用</li>
          <li>· 不需要机器学习 / 数学背景</li>
        </ul>
      </div>

      <div class="rounded-lg p-7" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[14px] font-semibold tracking-wide mb-4" style="color:#F87171;">这门课不是</div>
        <ul class="space-y-3 text-[16px] leading-relaxed" style="color:#334155;">
          <li>✕ 不是 Python 语法课</li>
          <li>✕ 不是机器学习原理课</li>
          <li>✕ 不训练模型，不碰梯度下降</li>
          <li>✕ 不设固定时长，按需跳读</li>
        </ul>
      </div>

      <div class="rounded-lg p-7" style="background:#F1F5F9;border:1px solid #E2E8F0;">
        <div class="text-[14px] font-semibold tracking-wide mb-4" style="color:#059669;">这门课是</div>
        <ul class="space-y-3 text-[16px] leading-relaxed" style="color:#334155;">
          <li>✓ 生态认知教程：建立技术地图</li>
          <li>✓ 看懂组件边界与数据流</li>
          <li>✓ 知道何时用 / 何时别用</li>
          <li>✓ 学完能画出企业级架构图</li>
        </ul>
      </div>
    </div>

    <div class="px-16 mt-9">
      <div class="rounded-lg px-7 py-5 flex items-start gap-4" style="background:#EFF6FF;border-left:3px solid #2563EB;">
        <span class="text-[20px] mt-0.5" style="color:#2563EB;">💡</span>
        <p class="text-[17px] leading-relaxed" style="color:#334155;">
          对 Java 工程师的关键提示：把 <span class="font-semibold" style="color:#2563EB;">LLM 当作"通过 HTTP 调用的、会推理的外部服务"</span>，
          把 <span class="font-semibold" style="color:#0891B2;">Agent 当作"用 LLM 做决策、会调用工具的 Spring Service"</span>。
          剩下的问题只是"用什么框架把这两件事拼起来"。
        </p>
      </div>
    </div>
  </div>
`);
