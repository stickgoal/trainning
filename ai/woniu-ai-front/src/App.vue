<template>
  <div class="ai-shell">
    <el-container class="ai-container" direction="vertical">
      <el-header class="ai-header" height="56px">
        <div class="ai-header__brand">
          <span class="ai-header__logo" aria-hidden="true">◇</span>
          <div>
            <div class="ai-header__title">蜗牛 AI</div>
            <div class="ai-header__sub">Vue 2 · Element UI · Element-UI-X</div>
          </div>
        </div>
        <el-radio-group v-model="view" class="ai-header__tabs" size="small">
          <el-radio-button label="chat">对话</el-radio-button>
          <el-radio-button label="rag">RAG</el-radio-button>
          <el-radio-button label="kb">知识库</el-radio-button>
        </el-radio-group>
        <div class="ai-header__actions">
          <el-button v-if="view === 'chat'" size="small" plain @click="clearPage">清空对话</el-button>
          <el-button v-else-if="view === 'rag'" size="small" plain @click="clearPage">清空</el-button>
          <el-button v-else-if="view === 'kb'" size="small" plain @click="clearPage">刷新列表</el-button>
        </div>
      </el-header>

      <div class="ai-body">
        <keep-alive>
          <component :is="currentView" ref="page" />
        </keep-alive>
      </div>
    </el-container>
  </div>
</template>

<script>
import ChatView from './views/ChatView.vue';
import RagView from './views/RagView.vue';
import KnowledgeBaseView from './views/KnowledgeBaseView.vue';

export default {
  name: 'App',
  components: { ChatView, RagView, KnowledgeBaseView },
  data() {
    return {
      view: 'chat',
    };
  },
  computed: {
    currentView() {
      if (this.view === 'rag') return 'RagView';
      if (this.view === 'kb') return 'KnowledgeBaseView';
      return 'ChatView';
    },
  },
  methods: {
    clearPage() {
      const p = this.$refs.page;
      if (!p) return;
      if (this.view === 'chat' && typeof p.clearChat === 'function') p.clearChat();
      else if (this.view === 'rag' && typeof p.clearAll === 'function') p.clearAll();
      else if (this.view === 'kb' && typeof p.loadList === 'function') p.loadList();
    },
  },
};
</script>

<style>
html,
body,
#app {
  height: 100%;
  margin: 0;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
    Arial, 'Noto Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: #f3f4f6;
  color: #111827;
}
</style>

<style scoped>
.ai-shell {
  height: 100%;
  box-sizing: border-box;
  padding: 16px;
}

.ai-container {
  height: 100%;
  max-width: 960px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.ai-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border-bottom: 1px solid #eef2f7;
  padding: 0 16px;
  flex-wrap: wrap;
}

.ai-header__brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.ai-header__tabs {
  flex-shrink: 0;
}

.ai-header__tabs :deep(.el-radio-button__inner) {
  padding-left: 14px;
  padding-right: 14px;
}

.ai-header__logo {
  display: inline-flex;
  width: 36px;
  height: 36px;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  color: #fff;
  font-size: 18px;
}

.ai-header__title {
  font-weight: 600;
  font-size: 16px;
  line-height: 1.2;
}

.ai-header__sub {
  margin-top: 2px;
  font-size: 12px;
  color: #6b7280;
}

.ai-header__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.ai-body {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ai-body :deep(.ai-inner) {
  flex: 1;
  min-height: 0;
}
</style>
