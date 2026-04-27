<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadAudio, addNote } from '@/api/user.js'

const fileList = ref([])
const uploading = ref(false)
const result = ref('')
const selectedFile = ref(null)

function handleChange(file) {
  selectedFile.value = file.raw
}

async function submitUpload() {
  if (!selectedFile.value) return ElMessage.warning('请选择音频文件')
  const allowed = ['audio/mp3', 'audio/wav', 'audio/mpeg', 'audio/x-wav', 'audio/wave', 'audio/x-m4a', 'audio/m4a', 'audio/mp4']
  const ext = (selectedFile.value.name || '').toLowerCase()
  if (!allowed.includes(selectedFile.value.type) && !ext.endsWith('.mp3') && !ext.endsWith('.wav') && !ext.endsWith('.m4a')) {
    return ElMessage.warning('仅支持 mp3 / wav / m4a 格式')
  }
  uploading.value = true
  result.value = ''
  try {
    const fd = new FormData()
    fd.append('file', selectedFile.value)
    const res = await uploadAudio(fd)
    result.value = res.data?.result || res.data || '识别完成'
    ElMessage.success('识别成功')
    fileList.value = []
    selectedFile.value = null
  } catch {
    // handled by interceptor
  } finally {
    uploading.value = false
  }
}

function clearResult() {
  result.value = ''
}

const saving = ref(false)
async function saveAsNote() {
  if (!result.value) return
  saving.value = true
  try {
    const lines = result.value.split('\n')
    const title = lines[0].replace('【模拟识别结果】', '').replace('【识别结果】', '').trim() || '语音笔记'
    await addNote({
      title: title.length > 30 ? title.slice(0, 30) : title,
      content: result.value,
      category: '其他',
      tags: '语音识别'
    })
    ElMessage.success('已保存为笔记')
  } catch {
    // handled by interceptor
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="upload-page">
    <h2 class="page-title">语音识别</h2>
    <p class="page-desc">上传音频文件，AI 自动识别并转写为文字</p>

    <el-card class="upload-card" shadow="never">
      <div class="upload-area" :class="{ hasfile: fileList.length > 0 }">
        <el-upload
          v-model:file-list="fileList"
          :auto-upload="false"
          :limit="1"
          drag
          :on-change="handleChange"
        >
          <el-icon class="upload-icon" :size="48"><Microphone /></el-icon>
          <div class="upload-text">
            <span class="upload-link">点击上传</span>
            <span> 或将音频文件拖拽到此区域</span>
          </div>
          <div class="upload-hint">支持 mp3 / wav / m4a 格式</div>
        </el-upload>
      </div>

      <div class="upload-actions">
        <el-button
          type="primary"
          size="large"
          :loading="uploading"
          :disabled="!selectedFile"
          class="submit-btn"
          @click="submitUpload"
        >
          <el-icon v-if="!uploading"><Promotion /></el-icon>
          {{ uploading ? '识别中...' : '开始识别' }}
        </el-button>
      </div>
    </el-card>

    <!-- 识别结果 -->
    <el-card v-if="result" class="result-card" shadow="never">
      <template #header>
        <div class="result-header">
          <span>识别结果</span>
          <el-button text size="small" type="primary" @click="clearResult">清空</el-button>
        </div>
      </template>
      <div class="result-content">{{ result }}</div>
      <div class="result-footer">
        <el-button type="primary" plain size="default" :loading="saving" @click="saveAsNote">
          保存为笔记 →
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.upload-page {
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

.upload-card,
.result-card {
  border-radius: 14px;
  border: 1px solid #f1f5f9;
  margin-bottom: 20px;
}

.upload-area {
  padding: 8px 0;
}

.upload-icon {
  color: #2563eb;
}

.upload-text {
  font-size: 15px;
  color: #475569;
  margin-top: 8px;
}

.upload-link {
  color: #2563eb;
  font-weight: 500;
  cursor: pointer;
}

.upload-hint {
  font-size: 12px;
  color: #cbd5e1;
  margin-top: 6px;
}

.upload-actions {
  text-align: center;
  margin-top: 20px;
}

.submit-btn {
  min-width: 180px;
  height: 44px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.result-content {
  padding: 16px 0;
  font-size: 15px;
  line-height: 1.8;
  color: #334155;
  white-space: pre-wrap;
}

.result-footer {
  padding-top: 12px;
  border-top: 1px solid #f8fafc;
}
</style>
