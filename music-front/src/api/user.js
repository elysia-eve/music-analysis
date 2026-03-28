import request from '@/utils/request'

export const register = (username, password) => {
    return request.post('/api/user/register', { username, password })
}

export const login = (username, password) => {
    return request.post('/api/user/login', { username, password })
}

export const getUserHistory = () => {
    return request.post('/api/user/history')
}