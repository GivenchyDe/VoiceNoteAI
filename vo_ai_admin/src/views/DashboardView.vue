<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboard, getLogList, getFeedbackList, getNoticeList } from '@/api/admin.js'
import { UserFilled, DocumentCopy, ChatDotSquare, BellFilled, Notebook, TrendCharts } from '@element-plus/icons-vue'

const stats = ref({})
const recentLogs = ref([])
const recentFeedbacks = ref([])
const recentNotices = ref([])
const lineChartRef = ref(null)

const statCards = [
  { label: '用户总数', key: 'userCount', icon: UserFilled, color: 'linear-gradient(135deg, #3b82f6, #60a5fa)' },
  { label: '音频总数', key: 'audioCount', icon: DocumentCopy, color: 'linear-gradient(135deg, #10b981, #34d399)' },
  { label: '识别总数', key: 'logCount', icon: TrendCharts, color: 'linear-gradient(135deg, #8b5cf6, #a78bfa)' },
  { label: '反馈总数', key: 'feedbackCount', icon: ChatDotSquare, color: 'linear-gradient(135deg, #f59e0b, #fbbf24)' },
  { label: '笔记总数', key: 'noteCount', icon: Notebook, color: 'linear-gradient(135deg, #06b6d4, #22d3ee)' },
  { label: '平均准确率', key: 'avgAccuracy', icon: TrendCharts, color: 'linear-gradient(135deg, #ef4444, #f87171)', suffix: '%' },
]

onMounted(async () => {
  const dash = await getDashboard()
  stats.value = dash.data

  const logs = await getLogList({ page: 1, size: 5 })
  recentLogs.value = logs.data.records

  const fbs = await getFeedbackList({ page: 1, size: 5 })
  recentFeedbacks.value = fbs.data.records

  const nts = await getNoticeList({ page: 1, size: 5 })
  recentNotices.value = nts.data.records

  // 折线图
  const lineChart = echarts.init(lineChartRef.value)
  lineChart.setOption({
    title: { text: '近7日识别统计', left: 'center', textStyle: { fontSize: 16, fontWeight: 'bold' } },
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 30, bottom: 30, top: 50 },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value' },
    series: [{
      data: [12, 19, 8, 15, 22, 10, 18],
      type: 'line',
      smooth: true,
      areaStyle: { color: 'rgba(59,130,246,0.2)' },
      itemStyle: { color: '#3b82f6' }
    }]
  })


})
</script>

<template>
  <div>
    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col :span="4" v-for="item in statCards" :key="item.key">
        <div class="stat-card" :style="{ background: item.color }">
          <el-icon :size="28" color="rgba(255,255,255,0.9)"><component :is="item.icon" /></el-icon>
          <div class="stat-label">{{ item.label }}</div>
          <div class="stat-value">
            {{ stats[item.key] || 0 }}{{ item.suffix || '' }}
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card>
          <div ref="lineChartRef" style="height: 320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 列表区 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span style="font-weight: 600">最近识别记录</span>
          </template>
          <div v-for="log in recentLogs" :key="log.id" class="list-item">
            <div class="list-title">{{ log.result?.substring(0, 20) || '-' }}...</div>
            <div class="list-meta">{{ log.username || log.userId }} · {{ log.createTime }}</div>
          </div>
          <el-empty v-if="recentLogs.length === 0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span style="font-weight: 600">最新反馈</span>
          </template>
          <div v-for="fb in recentFeedbacks" :key="fb.id" class="list-item">
            <div class="list-title">{{ fb.content?.substring(0, 20) || '-' }}...</div>
            <div class="list-meta">
              <el-tag :type="fb.status === 0 ? 'warning' : 'success'" size="small">
                {{ fb.status === 0 ? '待处理' : '已回复' }}
              </el-tag>
              <span>{{ fb.createTime }}</span>
            </div>
          </div>
          <el-empty v-if="recentFeedbacks.length === 0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span style="font-weight: 600">最新公告</span>
          </template>
          <div v-for="n in recentNotices" :key="n.id" class="list-item">
            <div class="list-title">{{ n.title }}</div>
            <div class="list-meta">{{ n.createTime }}</div>
          </div>
          <el-empty v-if="recentNotices.length === 0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.stat-card {
  border-radius: 12px;
  padding: 16px;
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.stat-label {
  font-size: 13px;
  opacity: 0.9;
}
.stat-value {
  font-size: 22px;
  font-weight: 700;
}
.list-item {
  padding: 10px 0;
  border-bottom: 1px solid #f1f5f9;
}
.list-item:last-child {
  border-bottom: none;
}
.list-title {
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
}
.list-meta {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
