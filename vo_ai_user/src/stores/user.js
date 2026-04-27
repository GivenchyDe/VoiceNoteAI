import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('user-token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('user-info') || '{}'))

  function setToken(t) {
    token.value = t
    localStorage.setItem('user-token', t)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('user-info', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('user-token')
    localStorage.removeItem('user-info')
  }

  return { token, userInfo, setToken, setUserInfo, logout }
})
