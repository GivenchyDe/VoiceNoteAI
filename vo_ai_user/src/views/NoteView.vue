<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoteList, addNote, updateNote, deleteNote } from '@/api/user.js'

const list = ref([])
const page = ref({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const form = ref({ id: null, title: '', content: '', category: '其他', tags: '' })

const categories = ['工作', '学习', '生活', '会议', '其他']

async function load() {
  loading.value = true
  try {
    const res = await getNoteList({ page: page.value.current, size: page.value.size })
    list.value = res.data.records || []
    page.value.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function openAdd() {
  isEdit.value = false
  form.value = { id: null, title: '', content: '', category: '其他', tags: '' }
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.title) return ElMessage.warning('请输入标题')
  if (!form.value.content) return ElMessage.warning('请输入内容')
  if (isEdit.value) {
    await updateNote(form.value)
  } else {
    await addNote(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove(id) {
  try {
    await ElMessageBox.confirm('确定删除该笔记？', '提示', { type: 'warning' })
    await deleteNote(id)
    ElMessage.success('删除成功')
    load()
  } catch {}
}

onMounted(load)
</script>

<template>
  <div class="note-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">我的笔记</h2>
        <p class="page-desc">管理您的语音识别笔记</p>
      </div>
      <el-button type="primary" @click="openAdd">新建笔记</el-button>
    </div>

    <el-card shadow="never" class="note-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="序号" width="60">
          <template #default="{ $index }">
            {{ (page.current - 1) * page.size + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="90">
          <template #default="{ row }">
            <el-tag size="small">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page.current"
        v-model:page-size="page.size"
        :total="page.total"
        layout="prev, pager, next, total"
        style="margin-top:16px;justify-content:flex-end"
        @change="load"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑笔记' : '新建笔记'" width="600px">
      <el-form :model="form" label-width="60px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入笔记标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="标签，逗号分隔" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入笔记内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.note-page {
  animation: fade-in 0.4s ease-out;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}

.page-desc {
  font-size: 14px;
  color: #94a3b8;
  margin-top: 2px;
}

.note-card {
  border-radius: 14px;
  border: 1px solid #f1f5f9;
}
</style>
