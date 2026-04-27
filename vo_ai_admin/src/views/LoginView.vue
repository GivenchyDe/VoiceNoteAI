<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/admin.js'
import { useAdminStore } from '@/stores/admin.js'
import { Microphone, Lock, User } from '@element-plus/icons-vue'

const router = useRouter()
const store = useAdminStore()

const form = ref({ username: '', password: '' })
const loading = ref(false)

async function onSubmit() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form.value)
    store.setToken(res.data.token)
    store.setAdminInfo(res.data.admin)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-wrapper">
      <div class="login-left">
        <div class="brand">
          <el-icon :size="48" color="#fff"><Microphone /></el-icon>
          <h1>VoiceNoteAi系统平台</h1>
          <p>智能语音识别 · 高效笔记管理 · 便捷后台运维</p>
        </div>
        <div class="features">
          <div class="feature-item">
            <el-icon :size="20" color="#67c23a"><Check /></el-icon>
            <span>语音实时转文字</span>
          </div>
          <div class="feature-item">
            <el-icon :size="20" color="#67c23a"><Check /></el-icon>
            <span>笔记分类与检索</span>
          </div>
          <div class="feature-item">
            <el-icon :size="20" color="#67c23a"><Check /></el-icon>
            <span>全平台数据管理</span>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-form-box">
          <h2>管理员登录</h2>
          <p class="sub-title">欢迎使用语音识别笔记管理后台</p>
          <el-form :model="form" label-position="top" class="login-form">
            <el-form-item label="账号">
              <el-input v-model="form.username" placeholder="请输入管理员账号" size="large" :prefix-icon="User" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" :prefix-icon="Lock" show-password @keyup.enter="onSubmit" />
            </el-form-item>
            <el-form-item style="margin-top: 10px">
              <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="onSubmit">
                立即登录
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a2a6c 0%, #2d5a87 50%, #4a9fd4 100%);
  padding: 20px;
}
.login-wrapper {
  display: flex;
  width: 900px;
  max-width: 100%;
  min-height: 520px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}
.login-left {
  flex: 1;
  background: linear-gradient(160deg, #1a2a6c 0%, #2d5a87 100%);
  color: #fff;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.brand h1 {
  font-size: 28px;
  margin: 20px 0 12px;
  font-weight: 600;
}
.brand p {
  font-size: 14px;
  opacity: 0.8;
  line-height: 1.6;
}
.features {
  margin-top: 40px;
}
.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  font-size: 14px;
}
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}
.login-form-box {
  width: 100%;
  max-width: 340px;
}
.login-form-box h2 {
  font-size: 24px;
  color: #1f2937;
  margin-bottom: 8px;
}
.sub-title {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 30px;
}
.login-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151;
}
</style>
