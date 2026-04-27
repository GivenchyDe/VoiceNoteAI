import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '@/stores/admin.js'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      component: () => import('@/views/LayoutView.vue'),
      redirect: '/dashboard',
      children: [
        { path: 'dashboard', component: () => import('@/views/DashboardView.vue'), meta: { title: '首页概览' } },
        { path: 'users', component: () => import('@/views/UserView.vue'), meta: { title: '用户管理' } },
        { path: 'logs', component: () => import('@/views/LogView.vue'), meta: { title: '识别日志' } },
        { path: 'feedbacks', component: () => import('@/views/FeedbackView.vue'), meta: { title: '反馈管理' } },
        { path: 'notices', component: () => import('@/views/NoticeView.vue'), meta: { title: '公告管理' } },
        { path: 'accounts', component: () => import('@/views/AdminAccountView.vue'), meta: { title: '管理员账号' } },
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const store = useAdminStore()
  if (!to.meta.public && !store.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
