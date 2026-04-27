<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateProfile, updatePassword, uploadAvatar } from '@/api/user.js'
import { useUserStore } from '@/stores/user.js'

const userStore = useUserStore()
const info = ref({
  username: '', phone: '', email: '', createTime: '',
  avatar: '', gender: 0, birthday: '', bio: '',
})
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const saving = ref(false)
const pwdVisible = ref(false)
const avatarUploading = ref(false)
const previewUrl = ref('')

const avatarInput = ref(null)

onMounted(async () => {
  try {
    const res = await getUserInfo()
    const d = res.data
    info.value = {
      username: d.username || '',
      phone: d.phone || '',
      email: d.email || '',
      createTime: d.createTime || '',
      avatar: d.avatar || '',
      gender: d.gender != null ? d.gender : 0,
      birthday: d.birthday || '',
      bio: d.bio || '',
    }
    userStore.setUserInfo(d)
  } catch {}
})

// 头像预览URL
const avatarSrc = () => {
  if (previewUrl.value) return previewUrl.value
  if (info.value.avatar) return 'http://localhost:8080' + info.value.avatar
  return ''
}

function triggerAvatar() {
  avatarInput.value.click()
}

async function handleAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 2MB')
    return
  }
  previewUrl.value = URL.createObjectURL(file)
  avatarUploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await uploadAvatar(fd)
    info.value.avatar = res.data.avatar
    userStore.setUserInfo({ ...userStore.userInfo, avatar: info.value.avatar })
    ElMessage.success('头像上传成功')
  } finally {
    avatarUploading.value = false
    e.target.value = ''
  }
}

async function saveProfile() {
  saving.value = true
  try {
    await updateProfile({
      username: info.value.username,
      phone: info.value.phone,
      email: info.value.email,
      gender: info.value.gender,
      birthday: info.value.birthday || null,
      bio: info.value.bio,
    })
    ElMessage.success('保存成功')
    userStore.setUserInfo(info.value)
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  if (!pwdForm.value.oldPassword) return ElMessage.warning('请输入原密码')
  if (!pwdForm.value.newPassword) return ElMessage.warning('请输入新密码')
  if (pwdForm.value.newPassword.length < 6) return ElMessage.warning('密码长度至少6位')
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) return ElMessage.warning('两次密码不一致')
  try {
    await updatePassword(pwdForm.value)
    ElMessage.success('密码修改成功')
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    pwdVisible.value = false
  } catch {}
}
</script>

<template>
  <div class="profile-page">
    <!-- 顶部 Banner -->
    <div class="profile-banner">
      <div class="banner-bg" />
      <div class="banner-content">
        <div class="avatar-section" @click="triggerAvatar">
          <el-avatar v-if="!avatarSrc()" :size="90" icon="UserFilled" class="avatar-img" />
          <el-avatar v-else :size="90" :src="avatarSrc()" class="avatar-img" />
          <div class="avatar-overlay">
            <el-icon :size="22"><Camera /></el-icon>
          </div>
        </div>
        <div class="banner-text">
          <h2 class="banner-name">{{ info.username || '用户' }}</h2>
          <p class="banner-meta">{{ info.phone || '未绑定手机号' }}</p>
        </div>
        <el-button class="edit-btn" type="primary" plain round>编辑资料</el-button>
      </div>
      <input
        ref="avatarInput"
        type="file"
        accept="image/*"
        style="display:none"
        @change="handleAvatarChange"
      />
    </div>

    <div class="profile-grid">
      <!-- 基本信息 -->
      <el-card shadow="never" class="profile-card">
        <template #header>
          <div class="card-header">
            <span class="card-icon">
              <el-icon :size="16"><User /></el-icon>
            </span>
            <span class="card-title">基本信息</span>
          </div>
        </template>
        <el-form label-width="80px" class="info-form">
          <el-form-item label="用户名">
            <el-input v-model="info.username" placeholder="请输入用户名" size="large" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="info.phone" placeholder="请输入手机号" size="large" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="info.email" placeholder="请输入邮箱（选填）" size="large" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="info.gender">
              <el-radio :value="0">保密</el-radio>
              <el-radio :value="1">男</el-radio>
              <el-radio :value="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="出生日期">
            <el-date-picker
              v-model="info.birthday"
              type="date"
              placeholder="选择出生日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              size="large"
              style="width:100%"
            />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input
              v-model="info.bio"
              type="textarea"
              :rows="3"
              placeholder="介绍一下自己..."
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          <!-- <el-form-item label="注册时间">
            <span class="info-text">{{ info.createTime || '--' }}</span>
          </el-form-item> -->
          <el-form-item>
            <el-button type="primary" :loading="saving" size="large" class="save-btn" @click="saveProfile">
              保存修改
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 安全设置 -->
      <el-card shadow="never" class="profile-card">
        <template #header>
          <div class="card-header">
            <span class="card-icon">
              <el-icon :size="16"><Lock /></el-icon>
            </span>
            <span class="card-title">安全设置</span>
          </div>
        </template>
        <template v-if="!pwdVisible">
          <div class="safe-placeholder">
            <el-icon :size="36" color="#cbd5e1"><Lock /></el-icon>
            <p class="safe-desc">定期更换密码可以保护您的账号安全</p>
            <el-button type="primary" plain @click="pwdVisible = true">修改密码</el-button>
          </div>
        </template>
        <el-form v-else label-width="80px" class="pwd-form">
          <el-form-item label="原密码">
            <el-input
              v-model="pwdForm.oldPassword"
              type="password"
              show-password
              placeholder="请输入原密码"
              size="large"
            />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input
              v-model="pwdForm.newPassword"
              type="password"
              show-password
              placeholder="请输入新密码（至少6位）"
              size="large"
            />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input
              v-model="pwdForm.confirmPassword"
              type="password"
              show-password
              placeholder="请确认新密码"
              size="large"
              @keyup.enter="changePassword"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="changePassword">确认修改</el-button>
            <el-button @click="pwdVisible = false; pwdForm = { oldPassword: '', newPassword: '', confirmPassword: '' }">
              取消
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  animation: fade-in 0.4s ease-out;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ========= Banner ========= */
.profile-banner {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 24px;
}

.banner-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 40%, #3b82f6 70%, #60a5fa 100%);
}

.banner-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 32px 36px;
}

.avatar-section {
  position: relative;
  cursor: pointer;
  flex-shrink: 0;
}

.avatar-img {
  border: 3px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s;
}

.avatar-section:hover .avatar-img {
  border-color: rgba(255, 255, 255, 0.8);
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-section:hover .avatar-overlay {
  opacity: 1;
}

.banner-text {
  flex: 1;
  color: #fff;
}

.banner-name {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.banner-meta {
  font-size: 13px;
  opacity: 0.75;
  margin-top: 4px;
}

.edit-btn {
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
  color: #fff !important;
  font-weight: 500;
}

.edit-btn:hover {
  background: rgba(255, 255, 255, 0.3) !important;
}

/* ========= 卡片 ========= */
.profile-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.profile-card {
  border-radius: 14px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
}

.profile-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f8fafc;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-icon {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: #eff6ff;
  color: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

/* ========= 表单 ========= */
.info-form {
  padding-top: 4px;
}

.info-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #64748b;
}

.info-form :deep(.el-input__wrapper),
.info-form :deep(.el-textarea__inner) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e2e8f0;
  transition: all 0.25s;
}

.info-form :deep(.el-input__wrapper:hover),
.info-form :deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #93c5fd;
}

.info-form :deep(.el-input__wrapper.is-focus),
.info-form :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.35);
}

.info-text {
  color: #94a3b8;
  font-size: 14px;
}

.save-btn {
  width: 100%;
  border-radius: 10px;
  height: 44px;
  font-weight: 600;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  border: none;
  transition: all 0.3s;
}

.save-btn:hover {
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.3);
}

/* ========= 安全设置 ========= */
.safe-placeholder {
  text-align: center;
  padding: 20px 0;
}

.safe-desc {
  font-size: 14px;
  color: #94a3b8;
  margin: 12px 0 16px;
}

.pwd-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e2e8f0;
  transition: all 0.25s;
}

.pwd-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #93c5fd;
}

.pwd-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.35);
}

@media (max-width: 768px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
  .banner-content {
    flex-direction: column;
    text-align: center;
    padding: 24px;
  }
  .edit-btn {
    margin-top: 8px;
  }
}
</style>
