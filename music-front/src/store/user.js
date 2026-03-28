import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || null,
        username: localStorage.getItem('username') || null
    }),
    actions: {
        setToken(token, username) {
            this.token = token
            this.username = username
            localStorage.setItem('token', token)
            if (username) localStorage.setItem('username', username)
        },
        logout() {
            this.token = null
            this.username = null
            localStorage.removeItem('token')
            localStorage.removeItem('username')
        }
    }
})