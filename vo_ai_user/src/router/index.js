import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home',
    },
    {
      path: '/login',
      component: () => import('@/views/LoginView.vue'),
      meta: { title: '登录' },
    },
    {
      path: '/register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { title: '注册' },
    },
    {
      path: '/',
      component: () => import('@/views/LayoutView.vue'),
      children: [
        { path: 'home', component: () => import('@/views/HomeView.vue'), meta: { title: '首页' } },
        { path: 'upload', component: () => import('@/views/UploadView.vue'), meta: { title: '语音识别' } },
        { path: 'notes', component: () => import('@/views/NoteView.vue'), meta: { title: '我的笔记' } },
        { path: 'notices', component: () => import('@/views/NoticeView.vue'), meta: { title: '系统公告' } },
        { path: 'feedbacks', component: () => import('@/views/FeedbackView.vue'), meta: { title: '意见反馈' } },
        { path: 'profile', component: () => import('@/views/ProfileView.vue'), meta: { title: '个人中心' } },
      ],
    },
  ],
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('user-token')
  const publicPages = ['/login', '/register']
  if (!token && !publicPages.includes(to.path)) {
    next('/login')
  } else if (token && publicPages.includes(to.path)) {
    next('/home')
  } else {
    next()
  }
})

export default router
