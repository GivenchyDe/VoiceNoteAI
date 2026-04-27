<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedbackList, replyFeedback } from '@/api/admin.js'

const list = ref([])
const page = ref({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false)
const currentRow = ref({})
const replyContent = ref('')

async function load() {
  const res = await getFeedbackList({
    page: page.value.current,
    size: page.value.size
  })
  list.value = res.data.records
  page.value.total = res.data.total
}

function openReply(row) {
  currentRow.value = row
  replyContent.value = row.replyContent || ''
  dialogVisible.value = true
}

async function submitReply() {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  await replyFeedback(currentRow.value.id, { replyContent: replyContent.value })
  ElMessage.success('回复成功')
  dialogVisible.value = false
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <el-card>
      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.type || '其他' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="反馈内容" show-overflow-tooltip />
        <el-table-column prop="replyContent" label="回复内容" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : 'success'">
              {{ row.status === 0 ? '待处理' : '已回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openReply(row)">回复</el-button>
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

    <el-dialog v-model="dialogVisible" title="回复反馈" width="500px">
      <p style="margin-bottom:10px;color:#666">反馈内容：{{ currentRow.content }}</p>
      <el-input v-model="replyContent" type="textarea" rows="4" placeholder="请输入回复内容" />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
