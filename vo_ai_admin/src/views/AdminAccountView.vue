<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminList, addAdmin, updateAdmin, deleteAdmin, updateAdminStatus } from '@/api/admin.js'

const list = ref([])
const page = ref({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: null, username: '', password: '', role: '普通管理员' })

async function load() {
  const res = await getAdminList({ page: page.value.current, size: page.value.size })
  list.value = res.data.records
  page.value.total = res.data.total
}

function openAdd() {
  isEdit.value = false
  form.value = { id: null, username: '', password: '', role: '普通管理员' }
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  form.value = { id: row.id, username: row.username, password: '', role: row.role }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.username) {
    ElMessage.warning('请输入账号')
    return
  }
  if (!isEdit.value && !form.value.password) {
    ElMessage.warning('请输入密码')
    return
  }
  if (isEdit.value) {
    await updateAdmin(form.value)
  } else {
    await addAdmin(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function toggleStatus(row) {
  const newStatus = row.status === 0 ? 1 : 0
  const action = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定${action}该管理员？`, '提示', { type: 'warning' })
    await updateAdminStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    load()
  } catch {
    // cancel
  }
}

async function remove(id) {
  try {
    await ElMessageBox.confirm('确定删除该管理员？', '提示', { type: 'warning' })
    await deleteAdmin(id)
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
      <div style="display:flex;gap:10px;margin-bottom:15px">
        <el-button type="primary" @click="openAdd">新增管理员</el-button>
      </div>

      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="账号" width="140" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === '超级管理员' ? 'danger' : 'primary'" size="small">{{ row.role }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" />

        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑管理员' : '新增管理员'" width="460px">
      <el-form :model="form" label-width="70px">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入管理员账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码，编辑时留空不修改" show-password />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="超级管理员" value="超级管理员" />
            <el-option label="普通管理员" value="普通管理员" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
