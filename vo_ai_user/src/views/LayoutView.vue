<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user.js'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const tabs = [
  { path: '/home', label: '首页', icon: 'HomeFilled' },
  { path: '/upload', label: '语音识别', icon: 'Microphone' },
  { path: '/notes', label: '我的笔记', icon: 'Document' },
  { path: '/notices', label: '系统公告', icon: 'Bell' },
  { path: '/profile', label: '个人中心', icon: 'User' },
]

const activePath = computed(() => route.path)

const avatarUrl = computed(() => {
  const avatar = userStore.userInfo.avatar
  if (avatar) return 'http://localhost:8080' + avatar
  return ''
})

function navigate(path) {
  router.push(path)
}

async function doLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    userStore.logout()
    router.push('/login')
  } catch {}
}
</script>

<template>
  <div class="app-layout">
    <!-- 顶部导航 -->
    <header class="app-header">
      <div class="header-inner">
        <div class="logo" @click="router.push('/home')">
          <img src="/logo.png" alt="VoiceNote" class="logo-img" />
          <span class="logo-text">VoiceNoteAi</span>
        </div>
        <nav class="header-nav">
          <button
            v-for="t in tabs"
            :key="t.path"
            :class="['nav-btn', { active: activePath === t.path }]"
            @click="navigate(t.path)"
          >
            {{ t.label }}
          </button>
        </nav>
        <el-dropdown trigger="click" class="user-dropdown">
          <div class="avatar-trigger">
            <el-avatar v-if="!avatarUrl" :size="34" icon="UserFilled" />
            <el-avatar v-else :size="34" :src="avatarUrl" />
            <span class="user-name">{{ userStore.userInfo.username || '用户' }}</span>
            <el-icon class="arrow"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/profile')">
                <el-icon><User /></el-icon> 个人中心
              </el-dropdown-item>
              <el-dropdown-item divided @click="router.push('/feedbacks')">
                <el-icon><ChatDotSquare /></el-icon> 意见反馈
              </el-dropdown-item>
              <el-dropdown-item divided @click="doLogout">
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: #f0f4ff;
  display: flex;
  flex-direction: column;
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(59, 130, 246, 0.08);
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 24px;
  height: 56px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}

.logo-img {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: 40px;
  flex: 1;
}

.nav-btn {
  padding: 8px 16px;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-btn:hover {
  background: #eff6ff;
  color: #2563eb;
}

.nav-btn.active {
  background: #eff6ff;
  color: #2563eb;
  font-weight: 600;
}

.avatar-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.avatar-trigger:hover {
  background: #eff6ff;
}

.user-name {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow {
  font-size: 12px;
  color: #94a3b8;
}

.app-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 24px auto;
  padding: 0 24px 40px;
}

@media (max-width: 768px) {
  .header-nav {
    display: none;
  }
  .header-inner {
    padding: 0 16px;
  }
  .app-main {
    padding: 0 16px 24px;
  }
}
</style>
