<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeList, addNotice, updateNotice, deleteNotice } from '@/api/admin.js'

const list = ref([])
const page = ref({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: null, title: '', content: '' })

async function load() {
  const res = await getNoticeList({
    page: page.value.current,
    size: page.value.size
  })
  list.value = res.data.records
  page.value.total = res.data.total
}

function openAdd() {
  isEdit.value = false
  form.value = { id: null, title: '', content: '' }
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  form.value = { id: row.id, title: row.title, content: row.content }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写完整')
    return
  }
  if (isEdit.value) {
    await updateNotice(form.value)
  } else {
    await addNotice(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove(id) {
  try {
    await ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' })
    await deleteNotice(id)
    ElMessage.success('删除成功')
    load()
  } catch {
    // cancel
  }
}

onMounted(load)
</script>

<template>
  <div>
    <el-card>
      <div style="margin-bottom:15px">
        <el-button type="primary" @click="openAdd">新增公告</el-button>
      </div>

      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="createTime" label="发布时间" />
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
        style="margin-top:15px;justify-content:flex-end"
        @change="load"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '新增公告'" width="500px">
      <el-form :model="form" label-width="60px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
