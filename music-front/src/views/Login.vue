<template>
  <el-card class="login-card">
    <h2>登录</h2>
    <el-form :model="form" :rules="rules" ref="formRef">
      <el-form-item prop="username">
        <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" type="password" placeholder="密码（8-18位数字、字母、符号的任意两种组合）" prefix-icon="Lock" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
        <el-button @click="$router.push('/register')">注册</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ username: '', password: '' })
const loading = ref(false)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\W]{8,18}$/, message: '密码格式错误（8-18位数字、字母、符号的任意两种组合）', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await login(form.value.username, form.value.password)
    if (res.code === 200) {
      userStore.setToken(res.data, form.value.username)
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('登录失败')
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