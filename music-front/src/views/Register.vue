<template>
    <el-card class="register-card">
        <h2>注册</h2>
        <el-form :model="form" :rules="rules" ref="formRef">
            <el-form-item prop="username">
                <el-input v-model="form.username" placeholder="用户名（4-16位字母数字下划线）" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
                <el-input v-model="form.password" type="password" placeholder="密码（8-18位数字字母符号组合）" prefix-icon="Lock" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
                <el-button @click="$router.push('/login')">已有账号？登录</el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)

const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { pattern: /^[a-zA-Z0-9_-]{4,16}$/, message: '用户名格式错误', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\W]{8,18}$/, message: '密码格式错误', trigger: 'blur' }
    ]
}

const handleRegister = async () => {
    loading.value = true
    try {
        const res = await register(form.value.username, form.value.password)
        if (res.code === 200) {
            ElMessage.success('注册成功，请登录')
            router.push('/login')
        } else {
            ElMessage.error(res.message || '注册失败')
        }
    } catch (error) {
        console.error(error)
        ElMessage.error('注册失败')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-card, .register-card {
  max-width: 400px;
  margin: 100px auto;
}
.el-card__body {
  padding: var(--space-xl) var(--space-lg);
}
</style>