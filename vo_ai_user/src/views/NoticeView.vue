<script setup>
import { ref, onMounted } from 'vue'
import { getNoticeList } from '@/api/user.js'

const notices = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res = await getNoticeList()
    notices.value = res.data.records || res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="notice-page">
    <h2 class="page-title">系统公告</h2>
    <p class="page-desc">查看最新系统通知与更新</p>

    <el-card shadow="never" class="notice-card">
      <div v-if="notices.length === 0" class="empty">暂无公告</div>
      <div v-for="n in notices" :key="n.id" class="notice-item">
        <div class="notice-top">
          <h3 class="notice-title">
            <el-tag v-if="n.isTop" type="danger" size="small" style="margin-right:8px">置顶</el-tag>
            {{ n.title }}
          </h3>
          <span class="notice-time">{{ n.createTime }}</span>
        </div>
        <p class="notice-content">{{ n.content }}</p>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.notice-page {
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

.notice-card {
  border-radius: 14px;
  border: 1px solid #f1f5f9;
}

.empty {
  padding: 40px 0;
  text-align: center;
  color: #cbd5e1;
  font-size: 14px;
}

.notice-item {
  padding: 18px 0;
  border-bottom: 1px solid #f8fafc;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.notice-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
}

.notice-time {
  font-size: 12px;
  color: #cbd5e1;
  flex-shrink: 0;
}

.notice-content {
  margin-top: 8px;
  font-size: 14px;
  color: #64748b;
  line-height: 1.7;
  white-space: pre-wrap;
}
</style>
