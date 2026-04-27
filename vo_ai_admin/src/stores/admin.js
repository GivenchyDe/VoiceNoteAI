import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('admin-token') || '')
  const adminInfo = ref(JSON.parse(localStorage.getItem('admin-info') || '{}'))

  function setToken(t) {
    token.value = t
    localStorage.setItem('admin-token', t)
  }

  function setAdminInfo(info) {
    adminInfo.value = info
    localStorage.setItem('admin-info', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    adminInfo.value = {}
    localStorage.removeItem('admin-token')
    localStorage.removeItem('admin-info')
  }

  return { token, adminInfo, setToken, setAdminInfo, logout }
})
