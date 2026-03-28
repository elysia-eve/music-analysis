import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
    response => response.data,
    error => {
        if (error.response) {
            ElMessage.error(error.response.data?.message || '请求失败')
        } else {
            ElMessage.error('网络错误')
        }
        return Promise.reject(error)
    }
)

export default request