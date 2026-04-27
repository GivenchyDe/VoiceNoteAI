<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useAdminStore } from '@/stores/admin.js'
import {
  HomeFilled, UserFilled, DocumentCopy, ChatDotSquare,
  BellFilled, SwitchButton, ArrowRight, Microphone, Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const store = useAdminStore()

function logout() {
  store.logout()
  router.push('/login')
}

const menuItems = [
  { path: '/', label: '首页概览', icon: HomeFilled },
  { path: '/users', label: '用户管理', icon: UserFilled },
  { path: '/logs', label: '识别日志', icon: DocumentCopy },
  { path: '/feedbacks', label: '反馈管理', icon: ChatDotSquare },
  { path: '/notices', label: '公告管理', icon: BellFilled },
  { path: '/accounts', label: '管理员账号', icon: Setting },
]
</script>

<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo-box">
        <img src="/logo.png" alt="logo" class="logo-img" />
        <span class="logo-text">VoiceNoteAi系统平台</span>
      </div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#0f172a"
        text-color="#94a3b8"
        active-text-color="#fff"
        class="layout-menu"
      >
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <el-breadcrumb :separator-icon="ArrowRight">
          <el-breadcrumb-item :to="{ path: '/' }">首页概览</el-breadcrumb-item>
          <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="header-right">
          <el-avatar :size="32" :icon="UserFilled" />
          <span class="username">{{ store.adminInfo?.username || '管理员' }}</span>
          <el-button type="danger" plain size="small" :icon="SwitchButton" @click="logout">退出</el-button>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
}
.layout-aside {
  background: #0f172a;
  color: #fff;
  box-shadow: 2px 0 8px rgba(0,0,0,0.15);
  z-index: 10;
}
.logo-box {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid #1e293b;
}
.logo-img {
  width: 28px;
  height: 28px;
  object-fit: contain;
}
.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1px;
}
.layout-menu {
  border-right: none;
  padding-top: 8px;
}
.layout-menu :deep(.el-menu-item) {
  height: 50px;
  margin: 4px 12px;
  border-radius: 8px;
}
.layout-menu :deep(.el-menu-item:hover) {
  background: #1e293b;
  color: #fff;
}
.layout-menu :deep(.el-menu-item.is-active) {
  background: #2563eb;
  color: #fff;
}
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  height: 56px;
  padding: 0 20px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.username {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}
.layout-main {
  background: #f1f5f9;
  padding: 20px;
}
</style>
