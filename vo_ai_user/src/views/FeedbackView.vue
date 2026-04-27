<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { submitFeedback, getMyFeedbacks } from '@/api/user.js'

const feedbacks = ref([])
const loading = ref(false)
const submitting = ref(false)
const form = ref({ content: '' })
const page = ref({ current: 1, size: 10, total: 0 })

async function loadFeedbacks() {
  loading.value = true
  try {
    const res = await getMyFeedbacks({ page: page.value.current, size: page.value.size })
    feedbacks.value = res.data.records || []
    page.value.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function submit() {
  if (!form.value.content.trim()) return ElMessage.warning('请输入反馈内容')
  submitting.value = true
  try {
    await submitFeedback(form.value)
    ElMessage.success('提交成功')
    form.value.content = ''
    loadFeedbacks()
  } finally {
    submitting.value = false
  }
}

const statusMap = { 0: '未处理', 1: '处理中', 2: '已处理' }
const statusType = { 0: 'info', 1: 'warning', 2: 'success' }

onMounted(loadFeedbacks)
</script>

<template>
  <div class="feedback-page">
    <h2 class="page-title">意见反馈</h2>
    <p class="page-desc">遇到问题或有建议？请告诉我们</p>

    <el-card shadow="never" class="submit-card">
      <el-input
        v-model="form.content"
        type="textarea"
        :rows="4"
        placeholder="请输入您的反馈内容..."
        maxlength="500"
        show-word-limit
      />
      <div class="submit-footer">
        <el-button
          type="primary"
          :loading="submitting"
          :disabled="!form.content.trim()"
          @click="submit"
        >
          提交反馈
        </el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="list-card">
      <template #header>
        <span class="card-title">我的反馈记录</span>
      </template>
      <div v-if="feedbacks.length === 0" class="empty">暂无反馈记录</div>
      <div v-for="fb in feedbacks" :key="fb.id" class="fb-item">
        <div class="fb-header">
          <el-tag :type="statusType[fb.status]" size="small">{{ statusMap[fb.status] || '未知' }}</el-tag>
          <span class="fb-time">{{ fb.createTime }}</span>
        </div>
        <div class="fb-content">{{ fb.content }}</div>
        <div v-if="fb.reply" class="fb-reply">
          <span class="reply-label">管理员回复：</span>{{ fb.reply }}
        </div>
      </div>
      <el-pagination
        v-model:current-page="page.current"
        v-model:page-size="page.size"
        :total="page.total"
        layout="prev, pager, next, total"
        small
        style="margin-top:16px;justify-content:flex-end"
        @change="loadFeedbacks"
      />
    </el-card>
  </div>
</template>

<style scoped>
.feedback-page {
  animation: fade-in 0.4s ease-out;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}

.page-desc {
  font-size: 14px;
  color: #94a3b8;
  margin: 4px 0 24px;
}

.submit-card,
.list-card {
  border-radius: 14px;
  border: 1px solid #f1f5f9;
  margin-bottom: 20px;
}

.submit-footer {
  text-align: right;
  margin-top: 12px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.empty {
  padding: 30px 0;
  text-align: center;
  color: #cbd5e1;
  font-size: 14px;
}

.fb-item {
  padding: 14px 0;
  border-bottom: 1px solid #f8fafc;
}

.fb-item:last-child {
  border-bottom: none;
}

.fb-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.fb-time {
  font-size: 12px;
  color: #cbd5e1;
}

.fb-content {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
}

.fb-reply {
  margin-top: 8px;
  padding: 10px 14px;
  background: #eff6ff;
  border-radius: 8px;
  font-size: 14px;
  color: #475569;
  line-height: 1.5;
}

.reply-label {
  color: #2563eb;
  font-weight: 500;
}
</style>
