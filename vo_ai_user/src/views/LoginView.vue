<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user.js'
import { useUserStore } from '@/stores/user.js'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ phone: '', password: '' })
const loading = ref(false)

// 动态粒子效果
const canvasRef = ref(null)
let animId = null

function initCanvas() {
  const c = canvasRef.value
  if (!c) return
  const ctx = c.getContext('2d')
  c.width = window.innerWidth
  c.height = window.innerHeight
  const particles = Array.from({ length: 60 }, () => ({
    x: Math.random() * c.width,
    y: Math.random() * c.height,
    r: Math.random() * 2 + 1,
    dx: (Math.random() - 0.5) * 0.8,
    dy: (Math.random() - 0.5) * 0.8,
  }))

  function draw() {
    ctx.clearRect(0, 0, c.width, c.height)
    particles.forEach((p, i) => {
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fillStyle = 'rgba(59,130,246,0.3)'
      ctx.fill()
      p.x += p.dx
      p.y += p.dy
      if (p.x < 0 || p.x > c.width) p.dx *= -1
      if (p.y < 0 || p.y > c.height) p.dy *= -1
      particles.forEach((q, j) => {
        if (i === j) return
        const d = Math.hypot(p.x - q.x, p.y - q.y)
        if (d < 100) {
          ctx.beginPath()
          ctx.moveTo(p.x, p.y)
          ctx.lineTo(q.x, q.y)
          ctx.strokeStyle = `rgba(59,130,246,${0.15 * (1 - d / 100)})`
          ctx.stroke()
        }
      })
    })
    animId = requestAnimationFrame(draw)
  }
  draw()
}

onMounted(initCanvas)
onBeforeUnmount(() => animId && cancelAnimationFrame(animId))

async function doLogin() {
  if (!form.value.phone.trim()) return ElMessage.warning('请输入手机号')
  if (!form.value.password.trim()) return ElMessage.warning('请输入密码')
  loading.value = true
  try {
    const res = await login(form.value)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.user)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch { /* handled by interceptor */ }
  finally { loading.value = false }
}

function goRegister() {
  router.push('/register')
}
</script>

<template>
  <div class="login-page">
    <canvas ref="canvasRef" class="bg-canvas" />

    <div class="login-card">
      <div class="card-left">
        <div class="brand-area">
          <div class="brand-icon">
            <svg viewBox="0 0 48 48" width="48" height="48">
              <circle cx="24" cy="24" r="22" fill="#2563eb" opacity="0.15" />
              <path d="M16 20C16 14.5 20.5 10 26 10V10C31.5 10 36 14.5 36 20V28C36 33.5 31.5 38 26 38C22.5 38 19.4 36.2 17.5 33.6L19.2 32.2C20.6 34.2 23.1 35.5 26 35.5C30.1 35.5 33.5 32.1 33.5 28V20C33.5 15.9 30.1 12.5 26 12.5C21.9 12.5 18.5 15.9 18.5 20V24H16V20Z" fill="#2563eb" />
            </svg>
          </div>
          <h1 class="brand-title">VoiceNoteAi</h1>
          <h1 class="brand-title"> </h1>
          <h1 class="brand-title"> </h1>
          <h1 class="brand-title">语音识别笔记</h1>
          <p class="brand-desc">智能语音转文字 · AI 驱动的高效笔记</p>
        </div>
      </div>

      <div class="card-right">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>登录您的账号，继续使用</p>
        </div>

        <el-form class="login-form" @keyup.enter="doLogin">
          <el-form-item>
            <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              prefix-icon="Iphone"
              size="large"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-btn"
              @click="doLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          还没有账号？
          <a class="link" @click="goRegister">立即注册</a>
        </div>
      </div>
    </div>

    <div class="footer-text">VoiceNote AI © 2026</div>
  </div>
</template>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 40%, #bfdbfe 100%);
  overflow: hidden;
}

.bg-canvas {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.login-card {
  position: relative;
  z-index: 1;
  display: flex;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 25px 60px rgba(59,130,246,0.15), 0 4px 12px rgba(0,0,0,0.05);
  overflow: hidden;
  max-width: 860px;
  width: 92%;
  animation: card-in 0.6s ease-out;
}

@keyframes card-in {
  from { opacity: 0; transform: translateY(30px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.card-left {
  flex: 1;
  background: linear-gradient(160deg, #1d4ed8 0%, #2563eb 40%, #3b82f6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 40px;
  position: relative;
  overflow: hidden;
}

.card-left::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 50%, rgba(255,255,255,0.08) 0%, transparent 60%);
}

.card-left::after {
  content: '';
  position: absolute;
  bottom: 10%;
  left: 10%;
  width: 200px;
  height: 200px;
  border: 2px solid rgba(255,255,255,0.08);
  border-radius: 50%;
}

.brand-area {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
}

.brand-icon {
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
  margin-bottom: 8px;
}

.brand-desc {
  font-size: 14px;
  opacity: 0.75;
  letter-spacing: 0.5px;
}

.card-right {
  flex: 1;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 30px;
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

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e2e8f0;
  transition: all 0.25s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #93c5fd;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(59,130,246,0.35);
}

.login-btn {
  width: 100%;
  border-radius: 10px;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover {
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(37,99,235,0.35);
}

.login-btn:active {
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
  transition: opacity 0.2s;
}

.link:hover {
  opacity: 0.8;
}

.footer-text {
  position: absolute;
  bottom: 24px;
  z-index: 1;
  font-size: 12px;
  color: rgba(100,116,139,0.5);
}

@media (max-width: 640px) {
  .login-card {
    flex-direction: column;
    max-width: 400px;
  }
  .card-left {
    padding: 30px 24px;
  }
  .brand-title {
    font-size: 22px;
  }
  .card-right {
    padding: 30px 24px;
  }
}
</style>
