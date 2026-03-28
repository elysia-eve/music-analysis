<template>
    <el-header class="header">
        <div class="logo">🎵 音乐分析平台</div>
        <div class="nav">
            <el-menu mode="horizontal" :router="true">
                <el-menu-item index="/">首页</el-menu-item>
                <el-menu-item index="/history" v-if="userStore.token">我的历史</el-menu-item>
            </el-menu>
        </div>
        <div class="user">
            <span v-if="userStore.username">{{ userStore.username }}</span>
            <el-button v-if="userStore.token" @click="logout" link>退出</el-button>
            <el-button v-else @click="$router.push('/login')" type="primary" size="small">登录</el-button>
        </div>
    </el-header>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const logout = () => {
    userStore.logout()
    router.push('/login')
}
</script>

<style scoped>
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #fff;
    box-shadow: 0 1px 4px rgba(0,0,0,0.08);
    padding: 0 20px;
}
.logo {
    font-size: 20px;
    font-weight: bold;
}
.nav {
    flex: 1;
    margin-left: 40px;
}
.user {
    display: flex;
    gap: 10px;
    align-items: center;
}
</style>