<script setup>
import { ref, onMounted } from 'vue'
import { getLogList } from '@/api/admin.js'

const list = ref([])
const page = ref({ current: 1, size: 10, total: 0 })
const keyword = ref('')

async function load() {
  const res = await getLogList({
    page: page.value.current,
    size: page.value.size,
    keyword: keyword.value
  })
  list.value = res.data.records
  page.value.total = res.data.total
}

onMounted(load)
</script>

<template>
  <div>
    <el-card>
      <div style="display:flex;gap:10px;margin-bottom:15px">
        <el-input v-model="keyword" placeholder="搜索识别结果" clearable style="width:260px" @keyup.enter="load" />
        <el-button type="primary" @click="load">搜索</el-button>
      </div>

      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="result" label="识别结果" show-overflow-tooltip />
        <el-table-column prop="accuracy" label="准确率" width="80" />
        <el-table-column prop="engine" label="引擎" width="80" />
        <el-table-column prop="duration" label="耗时(ms)" width="90" />
        <el-table-column prop="createTime" label="时间" />
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
