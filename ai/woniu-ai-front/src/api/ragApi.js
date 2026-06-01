import { extractSseTextChunk, mergeStreamText } from './aiSse.js';

/** 与 vite 代理一致，默认同 `/ai` → 后端根路径；可通过 VITE_RAG_API_BASE 覆盖 */
function ragApiBase() {
  const b = import.meta.env.VITE_RAG_API_BASE || '/ai';
  return b.endsWith('/') ? b.slice(0, -1) : b;
}

export function getRagUploadUrl() {
  return `${ragApiBase()}/upload`;
}

/**
 * GET /chat/rag?message=...
 * 支持普通 JSON/文本，或 text/event-stream（按行解析 data: JSON，复用 aiSse 的 chunk 提取逻辑）。
 */
export async function fetchRagChat(message, { signal, onSseJson } = {}) {
  const q = encodeURIComponent(message || '');
  const url = `${ragApiBase()}/chat/rag?message=${q}`;

  const res = await fetch(url, {
    method: 'GET',
    headers: { Accept: 'text/event-stream, application/json, text/plain, */*' },
    signal,
  });

  if (!res.ok) {
    const t = await res.text().catch(() => '');
    throw new Error(`${res.status} ${res.statusText}${t ? `: ${t.slice(0, 500)}` : ''}`);
  }

  const ct = (res.headers.get('content-type') || '').toLowerCase();
  if (ct.includes('text/event-stream') && res.body && res.body.getReader) {
    const reader = res.body.getReader();
    const decoder = new TextDecoder();
    let carry = '';
    let text = '';
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
          const obj = JSON.parse(payload);
          if (onSseJson) onSseJson(obj);
          const chunk = extractSseTextChunk(obj);
          if (chunk) text = mergeStreamText(text, chunk);
        } catch {
          /* 非 JSON 忽略 */
        }
      }
    }
    const tail = carry.replace(/\r$/, '').trim();
    if (tail.startsWith('data:')) {
      const payload = tail.slice(5).trimStart();
      if (payload && payload !== '[DONE]') {
        try {
          const obj = JSON.parse(payload);
          if (onSseJson) onSseJson(obj);
          const chunk = extractSseTextChunk(obj);
          if (chunk) text = mergeStreamText(text, chunk);
        } catch {
          /* ignore */
        }
      }
    }
    return text;
  }

  if (ct.includes('application/json')) {
    const data = await res.json();
    if (typeof data === 'string') return data;
    if (data == null) return '';
    const keys = ['answer', 'content', 'data', 'result', 'message', 'text'];
    for (const k of keys) {
      const v = data[k];
      if (typeof v === 'string' && v) return v;
    }
    return JSON.stringify(data);
  }

  return res.text();
}

export async function uploadRagFile(file) {
  const fd = new FormData();
  fd.append('file', file);
  const res = await fetch(getRagUploadUrl(), {
    method: 'POST',
    body: fd,
  });
  const t = await res.text();
  if (!res.ok) {
    throw new Error(`${res.status} ${res.statusText}${t ? `: ${t.slice(0, 500)}` : ''}`);
  }
  try {
    const j = JSON.parse(t);
    if (typeof j === 'string') return j;
    if (j && typeof j.message === 'string') return j.message;
    return t;
  } catch {
    return t;
  }
}
