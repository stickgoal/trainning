function kbApiBase() {
  const b = import.meta.env.VITE_KB_API_BASE || import.meta.env.VITE_RAG_API_BASE || '/ai';
  return b.endsWith('/') ? b.slice(0, -1) : b;
}

/** 列表接口路径，默认 GET `${base}/knowledge/list` */
function listUrl() {
  return import.meta.env.VITE_KB_LIST_URL || `${kbApiBase()}/knowledge/list`;
}

/** 删除单条，默认 DELETE `${base}/knowledge/{id}`；若后端为 POST，可设 VITE_KB_DELETE_URL 模板，如 `/ai/knowledge/delete?id={id}` */
function deleteUrl(id) {
  const tpl = import.meta.env.VITE_KB_DELETE_URL;
  if (tpl) return tpl.replace(/\{id\}/g, encodeURIComponent(String(id)));
  return `${kbApiBase()}/knowledge/${encodeURIComponent(String(id))}`;
}

export function normalizeDocRow(raw) {
  if (!raw || typeof raw !== 'object') {
    return { id: String(Math.random()), name: '—', size: null, time: null, raw };
  }
  return {
    id: raw.id ?? raw.docId ?? raw.fileId ?? raw.uuid ?? raw.key,
    name: raw.name ?? raw.fileName ?? raw.originalFilename ?? raw.title ?? '未命名',
    size: raw.size ?? raw.fileSize ?? raw.length,
    time: raw.createTime ?? raw.uploadTime ?? raw.createdAt ?? raw.gmtCreate,
    raw,
  };
}

export async function fetchKnowledgeList() {
  const res = await fetch(listUrl(), {
    method: 'GET',
    headers: { Accept: 'application/json' },
  });
  const t = await res.text();
  if (!res.ok) {
    throw new Error(`${res.status} ${res.statusText}${t ? `: ${t.slice(0, 300)}` : ''}`);
  }
  let json;
  try {
    json = t ? JSON.parse(t) : null;
  } catch {
    throw new Error('列表接口返回非 JSON');
  }
  let arr = [];
  if (Array.isArray(json)) arr = json;
  else if (json && Array.isArray(json.data)) arr = json.data;
  else if (json && Array.isArray(json.records)) arr = json.records;
  else if (json && Array.isArray(json.list)) arr = json.list;
  else if (json && Array.isArray(json.content)) arr = json.content;
  else if (json && Array.isArray(json.rows)) arr = json.rows;
  return arr.map((row) => {
    const n = normalizeDocRow(row);
    if (n.id == null || n.id === '') n.id = String(n.name) + String(n.time || '');
    return n;
  });
}

export async function deleteKnowledgeDoc(id) {
  const postTpl = import.meta.env.VITE_KB_DELETE_POST_URL;
  if (postTpl) {
    const url = postTpl.replace(/\{id\}/g, encodeURIComponent(String(id)));
    const res = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id }) });
    if (!res.ok) {
      const tx = await res.text().catch(() => '');
      throw new Error(`${res.status} ${res.statusText}${tx ? `: ${tx.slice(0, 300)}` : ''}`);
    }
    return;
  }
  const res = await fetch(deleteUrl(id), { method: 'DELETE' });
  if (!res.ok) {
    const tx = await res.text().catch(() => '');
    throw new Error(`${res.status} ${res.statusText}${tx ? `: ${tx.slice(0, 300)}` : ''}`);
  }
}
