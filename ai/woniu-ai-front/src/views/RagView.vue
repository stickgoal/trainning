<template>
  <el-container class="ai-inner" direction="vertical">
    <el-main class="ai-main rag-main">
      <div class="rag-toolbar">
        <div class="rag-toolbar__title">知识库文件</div>
        <el-upload
          class="rag-upload"
          action="#"
          :show-file-list="false"
          :http-request="onUploadRequest"
          :disabled="uploading"
        >
          <el-button size="small" type="primary" plain :loading="uploading">选择文件并上传</el-button>
        </el-upload>
        <span v-if="lastUploadHint" class="rag-upload__hint">{{ lastUploadHint }}</span>
      </div>

      <div v-if="!messages.length" class="ai-empty rag-empty">
        <el-x-welcome
          title="RAG 问答"
          description="请先上传文档，再基于文档内容提问。请求走 GET /chat/rag，参数名为 message。"
          variant="borderless"
        />
      </div>

      <div v-else ref="scrollArea" class="ai-thread">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="ai-thread__row"
          :class="{ 'ai-thread__row--user': msg.role === 'user' }"
        >
          <el-x-bubble
            :is-markdown="true"
            :placement="msg.role === 'user' ? 'end' : 'start'"
            :content="msg.content"
            :loading="msg.loading"
            variant="filled"
            shape="round"
            :max-width="'min(720px, 92vw)'"
          />
        </div>
      </div>
    </el-main>

    <el-footer class="ai-footer" height="5em">
      <el-x-sender
        v-model="draft"
        placeholder="基于已上传文档提问，Enter 发送"
        submit-type="enter"
        :loading="sending"
        clearable
        @submit="onSubmit"
      />
    </el-footer>
  </el-container>
</template>

<script>
import { Message } from 'element-ui';
import { extractSseTextChunk, mergeStreamText } from '../api/aiSse.js';
import { fetchRagChat, uploadRagFile } from '../api/ragApi.js';

let idSeq = 0;
function nextId() {
  idSeq += 1;
  return idSeq;
}

export default {
  name: 'RagView',
  data() {
    return {
      draft: '',
      sending: false,
      uploading: false,
      abortCtrl: null,
      messages: [],
      lastUploadHint: '',
    };
  },
  destroyed() {
    if (this.abortCtrl) this.abortCtrl.abort();
  },
  methods: {
    clearAll() {
      this.messages = [];
      this.draft = '';
      this.lastUploadHint = '';
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const el = this.$refs.scrollArea;
        if (el) el.scrollTop = el.scrollHeight;
      });
    },
    async onUploadRequest({ file }) {
      this.uploading = true;
      this.lastUploadHint = '';
      try {
        const resText = await uploadRagFile(file);
        this.lastUploadHint = resText ? String(resText).slice(0, 200) : '上传成功';
        Message.success('上传成功');
      } catch (e) {
        Message.error(e.message || String(e));
        this.lastUploadHint = '';
      } finally {
        this.uploading = false;
      }
    },
    async onSubmit(value) {
      const text = (value != null ? value : this.draft).trim();
      if (!text || this.sending) return;

      if (this.abortCtrl) this.abortCtrl.abort();
      this.abortCtrl = new AbortController();
      const { signal } = this.abortCtrl;

      this.messages.push({ id: nextId(), role: 'user', content: text });
      this.draft = '';
      this.sending = true;

      const botId = nextId();
      this.messages.push({
        id: botId,
        role: 'assistant',
        content: '',
        loading: true,
      });
      this.scrollToBottom();

      try {
        const full = await fetchRagChat(text, {
          signal,
          onSseJson: (obj) => {
            const b = this.messages.find((m) => m.id === botId);
            if (!b) return;
            const chunk = extractSseTextChunk(obj);
            if (chunk) {
              b.loading = false;
              b.content = mergeStreamText(b.content, chunk);
            }
            this.scrollToBottom();
          },
        });
        const bot = this.messages.find((m) => m.id === botId);
        if (bot) {
          bot.loading = false;
          if (full != null && String(full).trim()) {
            bot.content = mergeStreamText(bot.content, String(full));
          }
          if (!String(bot.content || '').trim()) {
            bot.content = '（未返回可读文本，请检查接口响应格式或查询参数名是否与后端一致）';
          }
        }
      } catch (e) {
        if (e.name === 'AbortError') return;
        const bot = this.messages.find((m) => m.id === botId);
        if (bot) {
          bot.content = bot.content
            ? `${bot.content}\n\n——\n请求异常：${e.message || e}`
            : `请求失败：${e.message || e}`;
        }
        Message.error(e.message || String(e));
      } finally {
        this.sending = false;
        this.abortCtrl = null;
        const bot = this.messages.find((m) => m.id === botId);
        if (bot) bot.loading = false;
        this.scrollToBottom();
      }
    },
  },
  watch: {
    messages: {
      deep: true,
      handler() {
        this.scrollToBottom();
      },
    },
  },
};
</script>

<style scoped>
.ai-inner {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ai-main {
  padding: 16px;
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.rag-main {
  gap: 12px;
}

.rag-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.rag-toolbar__title {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  margin-right: 4px;
}

.rag-upload__hint {
  font-size: 12px;
  color: #6b7280;
  flex: 1 1 160px;
  min-width: 0;
  word-break: break-all;
}

.ai-empty {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 16px;
}

.rag-empty {
  margin-top: 8px;
}

.ai-thread {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding-right: 4px;
}

.ai-thread__row {
  margin-bottom: 14px;
}

.ai-thread__row--user {
  display: flex;
  justify-content: flex-end;
}

.ai-footer {
  padding: 12px 16px 16px;
  border-top: 1px solid #eef2f7;
  background: #fafafa;
}
</style>
