import { defineStore } from 'pinia'
import { ref } from 'vue'

interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  avatar: string
  bio: string
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = ref(false)

  function setUser(user: UserInfo) {
    userInfo.value = user
    isLoggedIn.value = true
    localStorage.setItem('userId', String(user.id))
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  function loadUser() {
    const info = localStorage.getItem('userInfo')
    if (info) {
      userInfo.value = JSON.parse(info)
      isLoggedIn.value = true
    }
  }

  function logout() {
    userInfo.value = null
    isLoggedIn.value = false
    localStorage.removeItem('userId')
    localStorage.removeItem('userInfo')
  }

  // 初始化时加载
  loadUser()

  return { userInfo, isLoggedIn, setUser, loadUser, logout }
})
