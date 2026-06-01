<template>
  <el-container class="ai-inner" direction="vertical">
    <el-main class="ai-main">
      <div v-if="!messages.length" class="ai-empty">
        <el-x-welcome
          title="你好，我是蜗牛 AI"
          description="试着问我任何问题，或点击下方示例快速开始。"
          variant="borderless"
        />
        <div class="ai-prompts">
          <span class="ai-prompts__label">试试</span>
          <el-button
            v-for="(p, i) in starterPrompts"
            :key="i"
            size="small"
            round
            @click="sendPreset(p)"
          >
            {{ p }}
          </el-button>
        </div>
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
            :typing="msg.role === 'assistant' && msg.typing ? typingConfig : false"
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
        placeholder="输入消息，Enter 发送；Shift+Enter 换行"
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
import {
  buildMemoryChatBody,
  buildSpringStyleMessages,
  extractSseTextChunk,
  fetchConversationId,
  getAiSseUrl,
  getAiSseUrlForConversation,
  mergeStreamText,
  postSseJsonStream,
  resolveChatMode,
} from '../api/aiSse.js';

let idSeq = 0;
function nextId() {
  idSeq += 1;
  return idSeq;
}

export default {
  name: 'ChatView',
  data() {
    return {
      draft: '',
      sending: false,
      abortCtrl: null,
      starterPrompts: [
        '用三句话介绍 Vue 2 的响应式原理',
        '写一段 Element UI 表格分页的示例思路',
        '把「你好世界」翻译成英文并给一句使用场景',
      ],
      messages: [],
      /** memory 模式由后端持有的会话 id，清空对话时重置 */
      conversationId: null,
      typingConfig: { interval: 18, step: 1, suffix: '▍' },
    };
  },
  destroyed() {
    if (this.abortCtrl) this.abortCtrl.abort();
  },
  methods: {
    clearChat() {
      this.messages = [];
      this.draft = '';
      if (resolveChatMode() === 'memory') this.conversationId = null;
    },
    sendPreset(text) {
      this.draft = text;
      this.$nextTick(() => this.onSubmit(text));
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const el = this.$refs.scrollArea;
        if (el) {
          el.scrollTop = el.scrollHeight;
        }
      });
    },
    async onSubmit(value) {
      const text = (value != null ? value : this.draft).trim();
      if (!text || this.sending) return;

      if (this.abortCtrl) this.abortCtrl.abort();
      this.abortCtrl = new AbortController();
      const { signal } = this.abortCtrl;

      const mode = resolveChatMode();
      if (mode === 'memory' && !this.conversationId) {
        try {
          this.conversationId = await fetchConversationId(signal);
        } catch (e) {
          if (e.name === 'AbortError') return;
          Message.error(e.message || String(e));
          return;
        }
      }

      this.messages.push({
        id: nextId(),
        role: 'user',
        content: text,
      });
      this.draft = '';
      this.sending = true;

      const botId = nextId();
      this.messages.push({
        id: botId,
        role: 'assistant',
        content: '',
        loading: true,
        typing: false,
      });
      this.scrollToBottom();

      let url;
      let body;
      if (mode === 'memory') {
        url = getAiSseUrlForConversation(this.conversationId);
        body = buildMemoryChatBody(text);
      } else {
        const priorTurns = this.messages
          .filter((m) => m.id !== botId)
          .map((m) => ({ role: m.role, content: m.content }));
        url = getAiSseUrl();
        body = { messages: buildSpringStyleMessages(priorTurns) };
      }

      try {
        await postSseJsonStream({
          url,
          body,
          signal,
          onJson: (obj) => {
            const bot = this.messages.find((m) => m.id === botId);
            if (!bot) return;
            const chunk = extractSseTextChunk(obj);
            if (chunk) {
              bot.loading = false;
              bot.content = mergeStreamText(bot.content, chunk);
            }
            this.scrollToBottom();
          },
        });

        const bot = this.messages.find((m) => m.id === botId);
        if (bot && !String(bot.content || '').trim()) {
          bot.content = '（流已结束，但未解析到模型文本，请核对 data 字段或请求体格式）';
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

.ai-empty {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 16px;
}

.ai-prompts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  justify-content: center;
}

.ai-prompts__label {
  font-size: 12px;
  color: #6b7280;
  margin-right: 4px;
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
