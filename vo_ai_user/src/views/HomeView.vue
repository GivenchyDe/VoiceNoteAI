<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo } from '@/api/user.js'
import { getNoteList } from '@/api/user.js'
import { getNoticeList } from '@/api/user.js'
import { useRouter } from 'vue-router'

const router = useRouter()
const userInfo = ref({})
const notes = ref([])
const notices = ref([])

const features = [
  { icon: 'Microphone', title: '语音转文字', desc: '上传音频,AI 自动识别转写', color: '#3b82f6', path: '/upload' },
  { icon: 'Document', title: '智能笔记', desc: '识别结果自动生成笔记', color: '#8b5cf6', path: '/notes' },
  { icon: 'Bell', title: '系统公告', desc: '查看最新系统通知', color: '#f59e0b', path: '/notices' },
  { icon: 'ChatDotSquare', title: '意见反馈', desc: '随时反馈使用问题', color: '#10b981', path: '/feedbacks' },
]

onMounted(async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res.data
  } catch {}
  try {
    const nr = await getNoteList({ page: 1, size: 5 })
    notes.value = nr.data.records || []
  } catch {}
  try {
    const nr2 = await getNoticeList()
    notices.value = (nr2.data.records || nr2.data || []).slice(0, 5)
  } catch {}
})

function goFeature(path) {
  router.push(path)
}
</script>

<template>
  <div class="home-page">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-text">
        <h1>你好，{{ userInfo.username || '用户' }}</h1>
        <p>语音识别笔记系统，让记录更高效</p>
      </div>
      <div class="welcome-illustration">
        <svg viewBox="0 0 120 100" width="120" height="100">
          <ellipse cx="60" cy="85" rx="40" ry="8" fill="#2563eb" opacity="0.12" />
          <rect x="25" y="20" width="70" height="55" rx="10" fill="#2563eb" opacity="0.12" />
          <rect x="32" y="28" width="56" height="38" rx="6" fill="#fff" />
          <line x1="40" y1="38" x2="80" y2="38" stroke="#2563eb" stroke-width="2" opacity="0.3" stroke-linecap="round" />
          <line x1="40" y1="46" x2="70" y2="46" stroke="#2563eb" stroke-width="2" opacity="0.2" stroke-linecap="round" />
          <line x1="40" y1="54" x2="75" y2="54" stroke="#2563eb" stroke-width="2" opacity="0.2" stroke-linecap="round" />
          <circle cx="90" cy="22" r="12" fill="#2563eb" opacity="0.25" />
          <path d="M86 22l3 3 6-6" stroke="#fff" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </div>
    </div>

    <!-- 快捷功能 -->
    <div class="features-grid">
      <div
        v-for="(f, i) in features"
        :key="i"
        class="feature-card"
        @click="goFeature(f.path)"
      >
        <div class="feature-icon" :style="{ background: f.color + '15', color: f.color }">
          <el-icon :size="22"><component :is="f.icon" /></el-icon>
        </div>
        <div class="feature-info">
          <h3>{{ f.title }}</h3>
          <p>{{ f.desc }}</p>
        </div>
        <el-icon class="feature-arrow" :size="14" color="#cbd5e1"><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 两列布局 -->
    <div class="bottom-cols">
      <el-card class="col-card" shadow="never">
        <template #header>
          <div class="col-header">
            <span>最近笔记</span>
            <a class="col-link" @click="router.push('/notes')">查看全部 →</a>
          </div>
        </template>
        <div v-if="notes.length === 0" class="empty">暂无笔记</div>
        <div v-for="n in notes" :key="n.id" class="list-item">
          <div class="list-item-text">
            <div class="list-item-title">{{ n.title }}</div>
            <div class="list-item-desc">{{ (n.content || '').slice(0, 40) }}</div>
          </div>
          <span class="list-item-time">{{ (n.createTime || '').slice(0, 10) }}</span>
        </div>
      </el-card>

      <el-card class="col-card" shadow="never">
        <template #header>
          <div class="col-header">
            <span>系统公告</span>
            <a class="col-link" @click="router.push('/notices')">查看全部 →</a>
          </div>
        </template>
        <div v-if="notices.length === 0" class="empty">暂无公告</div>
        <div v-for="n in notices" :key="n.id" class="list-item">
          <div class="list-item-text">
            <div class="list-item-title">{{ n.title }}</div>
            <div class="list-item-desc">{{ (n.content || '').slice(0, 40) }}</div>
          </div>
          <span class="list-item-time">{{ (n.createTime || '').slice(0, 10) }}</span>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  animation: fade-in 0.4s ease-out;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

.welcome-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32px 36px;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border-radius: 16px;
  margin-bottom: 24px;
}

.welcome-text h1 {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.welcome-text p {
  font-size: 14px;
  color: #64748b;
  margin-top: 4px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.feature-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.25s;
  display: flex;
  align-items: center;
  gap: 14px;
  border: 1px solid #f1f5f9;
}

.feature-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.1);
  border-color: #dbeafe;
}

.feature-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-info h3 {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.feature-info p {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.feature-arrow {
  margin-left: auto;
}

.bottom-cols {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.col-card {
  border-radius: 14px;
  border: 1px solid #f1f5f9;
}

.col-card :deep(.el-card__header) {
  padding: 14px 20px;
  border-bottom: 1px solid #f8fafc;
}

.col-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.col-link {
  font-size: 13px;
  color: #2563eb;
  font-weight: 500;
  cursor: pointer;
}

.col-link:hover {
  opacity: 0.8;
}

.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f8fafc;
}

.list-item:last-child {
  border-bottom: none;
}

.list-item-title {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}

.list-item-desc {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.list-item-time {
  font-size: 12px;
  color: #cbd5e1;
  flex-shrink: 0;
}

.empty {
  padding: 30px 0;
  text-align: center;
  font-size: 14px;
  color: #cbd5e1;
}

@media (max-width: 900px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .bottom-cols {
    grid-template-columns: 1fr;
  }
}
</style>
