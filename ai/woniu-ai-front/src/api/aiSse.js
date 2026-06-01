/**
 * 对话模式（由 VITE_CHAT_MODE 控制）：
 * - legacy：POST /ai/sse，body 为整段 messages（默认）
 * - memory：GET 拿 conversationId 后 POST /ai/sse/{conversationId}，body 为单条 ChatParam
 */
export function resolveChatMode() {
  const v = String(import.meta.env.VITE_CHAT_MODE || 'legacy').toLowerCase();
  return v === 'memory' ? 'memory' : 'legacy';
}

/** 默认走 Vite 代理 /ai → 后端，避免开发环境 CORS */
export function getAiSseUrl() {
  return import.meta.env.VITE_AI_SSE_URL || '/ai/sse';
}

/** GET，返回会话 id（纯文本或 JSON 字符串） */
export function getConversationIdUrl() {
  return import.meta.env.VITE_AI_GET_CONVERSATION_ID_URL || '/ai/getConversationId';
}

/** memory 模式：POST 流式地址，末尾拼接 conversationId */
export function getAiSseUrlForConversation(conversationId) {
  const base = (import.meta.env.VITE_AI_SSE_MEMORY_BASE_URL || '/ai/sse').replace(/\/$/, '');
  return `${base}/${encodeURIComponent(String(conversationId))}`;
}

/**
 * 拉取会话 id（首条消息前调用；清空对话后再次调用）。
 * @param {AbortSignal} [signal]
 * @returns {Promise<string>}
 */
export async function fetchConversationId(signal) {
  const url = getConversationIdUrl();
  const res = await fetch(url, { method: 'GET', signal });
  if (!res.ok) {
    const t = await res.text().catch(() => '');
    throw new Error(`${res.status} ${res.statusText}${t ? `: ${t.slice(0, 500)}` : ''}`);
  }
  const raw = (await res.text()).trim();
  if (!raw) throw new Error('getConversationId 返回为空');
  if (raw.startsWith('"') && raw.endsWith('"')) {
    try {
      return JSON.parse(raw);
    } catch {
      return raw.slice(1, -1);
    }
  }
  return raw;
}

/**
 * memory 模式请求体，对应后端 ChatParam。
 * 若字段名不一致，可改此处或后续加 VITE 映射。
 * @param {string} userText
 */
export function buildMemoryChatBody(userText) {
  return { content: userText };
}

export function extractSseTextChunk(obj) {
  const cr = obj && obj.chatResponse;
  if (!cr) return '';
  const t1 = cr.result && cr.result.output && cr.result.output.text;
  if (typeof t1 === 'string' && t1.length) return t1;
  const r0 = cr.results && cr.results[0] && cr.results[0].output;
  const t2 = r0 && r0.text;
  return typeof t2 === 'string' ? t2 : '';
}

/** 兼容「整段累积」与「token 增量」两种流式输出 */
export function mergeStreamText(prev, chunk) {
  if (chunk == null || chunk === '') return prev;
  if (!prev) return chunk;
  if (chunk === prev) return prev;
  if (chunk.startsWith(prev)) return chunk;
  return prev + chunk;
}

/**
 * POST SSE：按行解析 `data:` JSON。
 * @param {object} opts
 * @param {string} opts.url
 * @param {object} opts.body
 * @param {AbortSignal} [opts.signal]
 * @param {(obj: object) => void} opts.onJson 每个 data JSON 对象
 */
export async function postSseJsonStream({ url, body, signal, onJson }) {
  const res = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'text/event-stream',
    },
    body: JSON.stringify(body),
    signal,
  });

  if (!res.ok) {
    const t = await res.text().catch(() => '');
    throw new Error(`${res.status} ${res.statusText}${t ? `: ${t.slice(0, 500)}` : ''}`);
  }

  const reader = res.body && res.body.getReader();
  if (!reader) {
    throw new Error('响应无 body，无法读取 SSE');
  }

  const decoder = new TextDecoder();
  let carry = '';

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    carry += decoder.decode(value, { stream: true });
    const parts = carry.split('\n');
    carry = parts.pop() || '';

    for (const raw of parts) {
      const line = raw.replace(/\r$/, '');
      if (!line.startsWith('data:')) continue;
      let payload = line.slice(5).trimStart();
      if (!payload || payload === '[DONE]') continue;
      try {
        onJson(JSON.parse(payload));
      } catch {
        /* 非 JSON 的 data 行忽略 */
      }
    }
  }

  const tail = carry.replace(/\r$/, '').trim();
  if (tail.startsWith('data:')) {
    const payload = tail.slice(5).trimStart();
    if (payload && payload !== '[DONE]') {
      try {
        onJson(JSON.parse(payload));
      } catch {
        /* ignore */
      }
    }
  }
}

/**
 * 将当前对话转为请求体中的 messages（可按后端约定改字段名 / role 枚举）。
 * @param {{ role: string, content: string }[]} turns
 */
export function buildSpringStyleMessages(turns) {
  return turns.map((m) => ({
    role: m.role === 'user' ? 'USER' : 'ASSISTANT',
    content: m.content || '',
  }));
}
