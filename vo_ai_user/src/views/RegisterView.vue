<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user.js'

const router = useRouter()
const loading = ref(false)

const form = ref({
  username: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule, value, cb) => {
        if (value !== form.value.password) cb(new Error('两次密码不一致'))
        else cb()
      },
      trigger: 'blur',
    },
  ],
}

const formRef = ref(null)

async function doRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register(form.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function goLogin() {
  router.push('/login')
}
</script>

<template>
  <div class="register-page">
    <div class="register-card">
      <div class="card-top">
        <div class="back-link" @click="goLogin">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回登录</span>
        </div>
      </div>

      <div class="card-body">
        <div class="form-header">
          <h2>创建账号</h2>
          <p>注册后即可使用语音识别笔记功能</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="register-form"
        >
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username">
                <el-input
                  v-model="form.username"
                  placeholder="请输入用户名"
                  size="large"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input
                  v-model="form.phone"
                  placeholder="请输入手机号"
                  size="large"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="邮箱（选填）" prop="email">
            <el-input
              v-model="form.email"
              placeholder="请输入邮箱"
              size="large"
              clearable
            />
          </el-form-item>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="登录密码" prop="password">
                <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="form.confirmPassword"
                  type="password"
                  placeholder="请确认密码"
                  size="large"
                  show-password
                  @keyup.enter="doRegister"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="register-btn"
              @click="doRegister"
            >
              注 册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          已有账号？
          <a class="link" @click="goLogin">立即登录</a>
        </div>
      </div>
    </div>

    <div class="footer-text">VoiceNote AI © 2026</div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 40%, #bfdbfe 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 560px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 25px 60px rgba(59,130,246,0.12), 0 4px 12px rgba(0,0,0,0.04);
  overflow: hidden;
  animation: card-in 0.6s ease-out;
}

@keyframes card-in {
  from { opacity: 0; transform: translateY(30px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.card-top {
  padding: 16px 24px;
  border-bottom: 1px solid #f1f5f9;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #2563eb;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: opacity 0.2s;
}

.back-link:hover {
  opacity: 0.75;
}

.card-body {
  padding: 32px 36px 40px;
}

.form-header {
  margin-bottom: 28px;
}

.form-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.form-header p {
  font-size: 14px;
  color: #94a3b8;
  margin-top: 4px;
}

.register-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #475569;
  padding-bottom: 4px;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e2e8f0;
  transition: all 0.25s;
}

.register-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #93c5fd;
}

.register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.35);
}

.register-btn {
  width: 100%;
  border-radius: 10px;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  border: none;
  transition: all 0.3s;
  margin-top: 8px;
}

.register-btn:hover {
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.35);
}

.register-btn:active {
  transform: translateY(0);
}

.form-footer {
  text-align: center;
  font-size: 14px;
  color: #94a3b8;
}

.link {
  color: #2563eb;
  cursor: pointer;
  font-weight: 500;
}

.link:hover {
  opacity: 0.8;
}

.footer-text {
  position: absolute;
  bottom: 24px;
  font-size: 12px;
  color: rgba(100, 116, 139, 0.45);
}

@media (max-width: 640px) {
  .card-body {
    padding: 24px 20px 32px;
  }
}
</style>
