<template>
  <el-container class="ai-inner" direction="vertical">
    <el-main class="kb-main">
      <div class="kb-toolbar">
        <div class="kb-toolbar__title">知识库管理</div>
        <el-button size="small" type="primary" plain :loading="loading" @click="loadList">刷新列表</el-button>
        <el-upload
          class="kb-upload"
          action="#"
          :show-file-list="false"
          :http-request="onUploadRequest"
          :disabled="uploading"
        >
          <el-button size="small" type="success" plain :loading="uploading">上传文件</el-button>
        </el-upload>
        <span class="kb-toolbar__hint">上传走现有接口 POST /upload，字段 file</span>
      </div>

      <el-alert
        v-if="listError"
        class="kb-alert"
        type="warning"
        :closable="false"
        :title="listError"
        show-icon
      />

      <el-table
        v-loading="loading"
        class="kb-table"
        :data="rows"
        stripe
        border
        size="small"
        empty-text="暂无文档，可先上传或检查后端列表接口"
        max-height="480"
      >
        <el-table-column prop="name" label="名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="id" label="ID" min-width="100" show-overflow-tooltip />
        <el-table-column label="大小" width="100" align="right">
          <template slot-scope="scope">
            {{ formatSize(scope.row.size) }}
          </template>
        </el-table-column>
        <el-table-column label="时间" width="168">
          <template slot-scope="scope">
            {{ formatTime(scope.row.time) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="88" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" class="kb-del" @click="onDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script>
import { Message, MessageBox } from 'element-ui';
import { uploadRagFile } from '../api/ragApi.js';
import { deleteKnowledgeDoc, fetchKnowledgeList } from '../api/knowledgeApi.js';

export default {
  name: 'KnowledgeBaseView',
  data() {
    return {
      loading: false,
      uploading: false,
      rows: [],
      listError: '',
    };
  },
  mounted() {
    this.loadList();
  },
  activated() {
    this.loadList();
  },
  methods: {
    loadList() {
      this.loading = true;
      this.listError = '';
      return fetchKnowledgeList()
        .then((list) => {
          this.rows = list;
        })
        .catch((e) => {
          this.rows = [];
          this.listError =
            (e && e.message) ||
            String(e) ||
            '加载失败；请实现 GET /knowledge/list（或配置 VITE_KB_LIST_URL）';
          Message.warning(this.listError);
        })
        .finally(() => {
          this.loading = false;
        });
    },
    formatSize(n) {
      if (n == null || n === '' || Number.isNaN(Number(n))) return '—';
      const x = Number(n);
      if (x < 1024) return `${x} B`;
      if (x < 1024 * 1024) return `${(x / 1024).toFixed(1)} KB`;
      return `${(x / 1024 / 1024).toFixed(2)} MB`;
    },
    formatTime(v) {
      if (v == null || v === '') return '—';
      if (typeof v === 'number') {
        const d = new Date(v > 1e12 ? v : v * 1000);
        return Number.isNaN(d.getTime()) ? String(v) : d.toLocaleString();
      }
      const d = new Date(v);
      return Number.isNaN(d.getTime()) ? String(v) : d.toLocaleString();
    },
    onUploadRequest({ file }) {
      this.uploading = true;
      return uploadRagFile(file)
        .then(() => {
          Message.success('上传成功');
          return this.loadList();
        })
        .catch((e) => {
          Message.error(e.message || String(e));
        })
        .finally(() => {
          this.uploading = false;
        });
    },
    onDelete(row) {
      const id = row && row.id;
      if (id == null || id === '') {
        Message.warning('该行无有效 ID，无法删除');
        return;
      }
      MessageBox.confirm(`确定删除「${row.name || id}」吗？`, '提示', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消',
      })
        .then(() => deleteKnowledgeDoc(id))
        .then(() => {
          Message.success('已删除');
          return this.loadList();
        })
        .catch((e) => {
          if (e === 'cancel') return;
          Message.error((e && e.message) || String(e));
        });
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

.kb-main {
  padding: 16px;
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.kb-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.kb-toolbar__title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-right: 4px;
}

.kb-toolbar__hint {
  font-size: 12px;
  color: #6b7280;
  flex: 1 1 200px;
  min-width: 0;
}

.kb-alert {
  flex-shrink: 0;
}

.kb-table {
  flex: 1;
  min-height: 200px;
}

.kb-del {
  color: #dc2626;
}
</style>
