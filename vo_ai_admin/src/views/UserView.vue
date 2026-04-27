<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus } from '@/api/admin.js'

const list = ref([])
const page = ref({ current: 1, size: 10, total: 0 })
const keyword = ref('')

async function load() {
  const res = await getUserList({
    page: page.value.current,
    size: page.value.size,
    keyword: keyword.value
  })
  list.value = res.data.records
  page.value.total = res.data.total
}

async function toggleStatus(row) {
  const newStatus = row.status === 0 ? 1 : 0
  const action = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定${action}该用户？`, '提示', { type: 'warning' })
    await updateUserStatus(row.id, newStatus)
    ElMessage.success('操作成功')
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
        <el-input v-model="keyword" placeholder="搜索用户名/手机号" clearable style="width:260px" @keyup.enter="load" />
        <el-button type="primary" @click="load">搜索</el-button>
      </div>

      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="createTime" label="注册时间" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" :type="row.status === 0 ? 'danger' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 0 ? '禁用' : '启用' }}
            </el-button>
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
  </div>
</template>
